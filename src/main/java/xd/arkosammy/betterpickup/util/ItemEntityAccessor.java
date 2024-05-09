package xd.arkosammy.betterpickup.util;

import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public interface ItemEntityAccessor {

    void betterpickup$setPlayerDropPickupDelay(int pickupDelay);

    void betterpickup$setBlockDropPickupDelay(int blockDropPickupDelay);

    void betterpickup$setStealPickupDelay(int stealPickupDelay);

    void betterpickup$setBreakingEntityUuid(UUID breakingEntity);

    PlayerEntity betterpickup$getBreakingEntity();

}
