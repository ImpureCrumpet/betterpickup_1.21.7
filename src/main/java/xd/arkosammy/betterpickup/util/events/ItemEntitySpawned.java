package xd.arkosammy.betterpickup.util.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface ItemEntitySpawned {

    Event<ItemEntitySpawned> EVENT = EventFactory.createArrayBacked(ItemEntitySpawned.class, listeners -> (itemEntity, breakingEntity, world) -> {
        for (ItemEntitySpawned listener : listeners) {
            listener.onItemEntitySpawned(itemEntity, breakingEntity, world);
        }
    });

    void onItemEntitySpawned(ItemEntity itemEntity, @Nullable Entity breakingEntity, World world);

}
