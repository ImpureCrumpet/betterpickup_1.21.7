package xd.arkosammy.betterpickup.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import xd.arkosammy.betterpickup.util.ItemStackAccessor;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ItemStackAccessor {

    @Unique
    private Entity breakingEntity;

    @Override
    public void betterpickup$setBreakingEntity(Entity breakingEntity) {
        this.breakingEntity = breakingEntity;
    }

    @Override
    public Entity betterpick$getBreakingEntity() {
        return this.breakingEntity;
    }

}
