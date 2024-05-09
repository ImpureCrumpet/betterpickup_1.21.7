package xd.arkosammy.betterpickup;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterPickup implements ModInitializer {

	public static final String MOD_ID = "betterpickup";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final GameRules.Key<GameRules.BooleanRule> DO_AUTO_PICKUP = GameRuleRegistry.register("doAutoPickups", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));
	public static final GameRules.Key<GameRules.BooleanRule> INVULNERABLE_BLOCK_DROPS = GameRuleRegistry.register("invulnerableBlockDrops", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));
	public static final GameRules.Key<GameRules.IntRule> PLAYER_DROPS_DELAY = GameRuleRegistry.register("playerDropsDelay", GameRules.Category.MISC, GameRuleFactory.createIntRule(10, 0));
	public static final GameRules.Key<GameRules.IntRule> BLOCK_DROPS_DELAY = GameRuleRegistry.register("blockDropsDelay", GameRules.Category.MISC, GameRuleFactory.createIntRule(20, 0));
	public static final GameRules.Key<GameRules.IntRule> STEAL_DELAY = GameRuleRegistry.register("stealDelay", GameRules.Category.MISC, GameRuleFactory.createIntRule(20, 0));

	@Override
	public void onInitialize() {


	}
}