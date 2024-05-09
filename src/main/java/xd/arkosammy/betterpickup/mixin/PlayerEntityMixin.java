package xd.arkosammy.betterpickup.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xd.arkosammy.betterpickup.util.events.ItemEntitySpawned;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @SuppressWarnings("UnreachableCode")
    @ModifyReturnValue(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;", at = @At("RETURN"))
    private ItemEntity onItemDroppedByPlayer(@Nullable ItemEntity original) {
        if(original == null) {
            return original;
        }
        ItemEntitySpawned.EVENT.invoker().onItemEntitySpawned(original, null, ((PlayerEntity) (Object) this).getWorld());
        return original;
    }

}
