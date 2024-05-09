package xd.arkosammy.betterpickup.util;

import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;


public interface ItemStackAccessor {

    void betterpickup$setBreakingEntity(@Nullable Entity breakingEntity);

    @Nullable
    Entity betterpick$getBreakingEntity();

}
