package xd.arkosammy.betterpickup.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import java.util.UUID;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xd.arkosammy.betterpickup.BetterPickup;
import xd.arkosammy.betterpickup.util.ItemEntityAccessor;

import java.util.UUID;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin implements ItemEntityAccessor {

    @Shadow private int pickupDelay;

    @Shadow public abstract void onPlayerCollision(PlayerEntity player);

    // This returns the entity that threw the item entity
    @Shadow public abstract @Nullable Entity getOwner();

    // This is the uuid of the entity that this iem belongs to, either by advancement or command
    @Shadow private @Nullable UUID owner;

    @Unique
    private UUID breakingEntity;

    @Unique
    private int blockDropPickupDelay = 10;

    @Unique
    private int stealDelay = 40;

    @Override
    public void betterpickup$setPlayerDropPickupDelay(int pickupDelay) {
        if(pickupDelay < 0) {
            throw new IllegalArgumentException("Pickup delay of item entities cannot be negative");
        }
        this.pickupDelay = pickupDelay;
    }

    @Override
    public void betterpickup$setBlockDropPickupDelay(int blockDropDelay) {
        if(blockDropDelay < 0) {
            throw new IllegalArgumentException("Block drop pickup delay of item entities cannot be negative");
        }
        this.blockDropPickupDelay = blockDropDelay;
    }

    @Override
    public void betterpickup$setStealPickupDelay(int stealPickupDelay) {
        if(stealPickupDelay < 0) {
            throw new IllegalArgumentException("Steal pickup delay of item entities cannot be negative");
        }
        this.stealDelay = stealPickupDelay;
    }

    @Override
    public void betterpickup$setBreakingEntityUuid(UUID breakingEntity) {
        this.breakingEntity = breakingEntity;
    }

    @SuppressWarnings("UnreachableCode")
    @Nullable
    @Override
    public PlayerEntity betterpickup$getBreakingEntity()  {
        if(this.breakingEntity == null) {
            return null;
        }
        World world = ((ItemEntity)(Object) this).getWorld();
        return world.getPlayerByUuid(this.breakingEntity);
    }

    // Give the player the dropped item if they are the miner of the block regardless of how far they are
    @SuppressWarnings("UnreachableCode")
    @Inject(method = "tick", at = @At("RETURN"))
    private void onTickFinished(CallbackInfo ci) {
        if (this.blockDropPickupDelay > 0 && this.blockDropPickupDelay != 32767) {
            --this.blockDropPickupDelay;
        }
        if (this.stealDelay > 0 && this.stealDelay != 32767) {
            --this.stealDelay;
        }
        // We only make item entities without owners and with miners be auto picked up (they come from block drops)
        PlayerEntity miner = this.betterpickup$getBreakingEntity();
        if(miner == null) {
            return;
        }
        if(this.getOwner() != null) {
            return;
        }
        World world = ((ItemEntity)(Object) this).getWorld();
        if(world instanceof ServerWorld serverWorld && serverWorld.getGameRules().getBoolean(BetterPickup.DO_AUTO_PICKUP)) {
            this.onPlayerCollision(miner);
        }

    }

    @Inject(method = "writeCustomData(Lnet/minecraft/storage/WriteView;)V", at = @At("RETURN"))
    private void writeCustomDataToNbt(WriteView nbt , CallbackInfo ci) {
        nbt.putInt("BlockDropPickupDelay", this.blockDropPickupDelay);
        nbt.putInt("StealDelay", this.stealDelay);
        if(this.breakingEntity != null) {
            nbt.putString("BreakingEntity", this.breakingEntity.toString());
        }
    }

    @Inject(method = "readCustomData(Lnet/minecraft/storage/ReadView;)V", at = @At("RETURN"))
    private void readCustomDataFromNbt(ReadView nbt, CallbackInfo ci) {
        nbt.getOptionalInt("BlockDropPickupDelay").ifPresent(value -> this.blockDropPickupDelay = value);
        nbt.getOptionalInt("StealDelay").ifPresent(value -> this.stealDelay = value);
        nbt.getOptionalString("BreakingEntity").ifPresent(value -> {
            try {
                this.breakingEntity = UUID.fromString(value);
            } catch (IllegalArgumentException ignored) {
                this.breakingEntity = null;
            }
        });
    }

    // Make the item entity invulnerable to damage for block drops when gamerule is enabled
    @SuppressWarnings("UnreachableCode")
    @Inject(method = "damage(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;F)Z", at = @At("HEAD"), cancellable = true)
    private void betterpickup$cancelDamageIfInvulnerable(ServerWorld serverWorld, DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
        // Only for item entities that come from block drops (no thrower but has miner)
        if (this.getOwner() != null) {
            return;
        }
        PlayerEntity miner = this.betterpickup$getBreakingEntity();
        if (miner == null) {
            return;
        }
        if (serverWorld.getGameRules().getBoolean(BetterPickup.INVULNERABLE_BLOCK_DROPS)) {
            cir.setReturnValue(false);
        }
    }

    @SuppressWarnings("UnreachableCode")
    @ModifyExpressionValue(method = "onPlayerCollision", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/entity/ItemEntity;pickupDelay:I"))
    private int modifyPickupDelay(int original, @Local(argsOnly = true) PlayerEntity collidingPlayer) {
        Entity thrower = this.getOwner();
        PlayerEntity minerEntity = this.betterpickup$getBreakingEntity();
        boolean isCollidingPlayerOwner = this.owner != null && this.owner.equals(collidingPlayer.getUuid());
        if(thrower instanceof PlayerEntity throwerEntity) {
            // Player drops
            boolean isCollidingPlayerThrower = throwerEntity.getUuid().equals(collidingPlayer.getUuid());
            int pickupDelayToUse = isCollidingPlayerThrower || isCollidingPlayerOwner ? original : this.stealDelay;
            return pickupDelayToUse;
        } else if (minerEntity != null) {
            // Block drops
            World world = ((ItemEntity)(Object) this).getWorld();
            boolean isCollidingPlayerMiner = minerEntity.getUuid().equals(collidingPlayer.getUuid());
            int pickupDelayToUse = isCollidingPlayerMiner || isCollidingPlayerOwner ? this.blockDropPickupDelay : this.stealDelay;
            return (world instanceof ServerWorld serverWorld && serverWorld.getGameRules().getBoolean(BetterPickup.DO_AUTO_PICKUP)) ? 0 : pickupDelayToUse;
        }
        return original;
    }

}
