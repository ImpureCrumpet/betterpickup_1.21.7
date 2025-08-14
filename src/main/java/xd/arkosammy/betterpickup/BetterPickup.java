package xd.arkosammy.betterpickup;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xd.arkosammy.betterpickup.util.ItemEntityAccessor;
import xd.arkosammy.betterpickup.util.events.ItemEntitySpawned;

public class BetterPickup implements ModInitializer {

	public static final String MOD_ID = "betterpickup";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final CustomGameRuleCategory CATEGORY = new CustomGameRuleCategory(Identifier.of(MOD_ID, "gamerules"), Text.literal(MOD_ID));
	public static final GameRules.Key<GameRules.BooleanRule> DO_AUTO_PICKUP = GameRuleRegistry.register("doAutoPickups", CATEGORY, GameRuleFactory.createBooleanRule(true));
	public static final GameRules.Key<GameRules.BooleanRule> INVULNERABLE_BLOCK_DROPS = GameRuleRegistry.register("invulnerableBlockDrops", CATEGORY, GameRuleFactory.createBooleanRule(true));
	public static final GameRules.Key<GameRules.IntRule> PLAYER_DROPS_DELAY = GameRuleRegistry.register("playerDropsDelay", CATEGORY, GameRuleFactory.createIntRule(20, 1));
	public static final GameRules.Key<GameRules.IntRule> BLOCK_DROPS_DELAY = GameRuleRegistry.register("blockDropsDelay", CATEGORY, GameRuleFactory.createIntRule(10, 1));
	public static final GameRules.Key<GameRules.IntRule> STEAL_DELAY = GameRuleRegistry.register("stealDelay", CATEGORY, GameRuleFactory.createIntRule(40, 1));


	@Override
	public void onInitialize() {
		ItemEntitySpawned.EVENT.register((itemEntity, breakingEntity, world) -> {
			((ItemEntityAccessor)itemEntity).betterpickup$setBreakingEntityUuid(breakingEntity == null ? null : breakingEntity.getUuid());
			if(world instanceof ServerWorld serverWorld) {
				((ItemEntityAccessor)itemEntity).betterpickup$setPlayerDropPickupDelay(serverWorld.getGameRules().getInt(BetterPickup.PLAYER_DROPS_DELAY));
				((ItemEntityAccessor)itemEntity).betterpickup$setBlockDropPickupDelay(serverWorld.getGameRules().getInt(BetterPickup.BLOCK_DROPS_DELAY));
				((ItemEntityAccessor)itemEntity).betterpickup$setStealPickupDelay(serverWorld.getGameRules().getInt(BetterPickup.STEAL_DELAY));
			}
		});

		// Fallback for player item drops: fire our event when an ItemEntity owned by a player is spawned
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (!(entity instanceof net.minecraft.entity.ItemEntity itemEntity)) {
				return;
			}
			var owner = itemEntity.getOwner();
			if (owner instanceof net.minecraft.entity.player.PlayerEntity) {
				ItemEntitySpawned.EVENT.invoker().onItemEntitySpawned(itemEntity, null, world);
			}
		});
	}
}