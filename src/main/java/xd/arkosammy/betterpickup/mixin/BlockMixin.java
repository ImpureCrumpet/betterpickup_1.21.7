package xd.arkosammy.betterpickup.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xd.arkosammy.betterpickup.BetterPickup;
import xd.arkosammy.betterpickup.util.ItemEntityAccessor;
import xd.arkosammy.betterpickup.util.ItemStackAccessor;

import java.util.List;

@Mixin(Block.class)
public class BlockMixin {

    @SuppressWarnings("UnreachableCode")
    @WrapOperation(method = "dropStack(Lnet/minecraft/world/World;Ljava/util/function/Supplier;Lnet/minecraft/item/ItemStack;)V", at = @At(value = "INVOKE",  target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
    private static boolean onItemStackSpawned(World instance, Entity entity, Operation<Boolean> original) {

        if(!(entity instanceof ItemEntity itemEntity)) {
            return original.call(instance, entity);
        }

        Entity breakingEntity = ((ItemStackAccessor) (Object) itemEntity.getStack()).betterpick$getBreakingEntity();
        ((ItemEntityAccessor)itemEntity).betterpickup$setBreakingEntityUuid(breakingEntity == null ? null : breakingEntity.getUuid());
        ((ItemEntityAccessor)itemEntity).betterpickup$setPlayerDropPickupDelay(instance.getGameRules().getInt(BetterPickup.PLAYER_DROPS_DELAY));
        ((ItemEntityAccessor)itemEntity).betterpickup$setBlockDropPickupDelay(instance.getGameRules().getInt(BetterPickup.BLOCK_DROPS_DELAY));
        ((ItemEntityAccessor)itemEntity).betterpickup$setStealPickupDelay(instance.getGameRules().getInt(BetterPickup.STEAL_DELAY));

        return original.call(instance, entity);
    }

    @ModifyReturnValue(method = "getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Ljava/util/List;", at = @At("RETURN"))
    private static List<ItemStack> onStackDropped(List<ItemStack> original, @Local(argsOnly = true) @Nullable Entity entity) {
        original.forEach(itemStack -> ((ItemStackAccessor) (Object) itemStack).betterpickup$setBreakingEntity(entity));
        return original;
    }

}
