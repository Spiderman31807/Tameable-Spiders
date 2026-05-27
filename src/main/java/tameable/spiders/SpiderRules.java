package tameable.spiders.init;

import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;

import net.minecraft.world.level.GameRules;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpiderRules {
	public static GameRules.Key<GameRules.IntegerValue> SPAWN_RATE;
	public static GameRules.Key<GameRules.IntegerValue> BABY_RATE;
	public static GameRules.Key<GameRules.BooleanValue> BREEDING;
	public static GameRules.Key<GameRules.BooleanValue> RIDABLE;
	public static GameRules.Key<GameRules.BooleanValue> INVENTORY;
	public static GameRules.Key<GameRules.BooleanValue> BABY_POISON;
	public static GameRules.Key<GameRules.BooleanValue> PRODUCE_SILK;
	public static GameRules.Key<GameRules.BooleanValue> PRODUCE_VENOM;
	public static GameRules.Key<GameRules.BooleanValue> RESPAWN;
	public static GameRules.Key<GameRules.IntegerValue> RESPAWN_TIME;
	public static GameRules.Key<GameRules.BooleanValue> KEEP_INVENTORY;
	public static GameRules.Key<GameRules.BooleanValue> FALLDAMAGE;
	public static GameRules.Key<GameRules.BooleanValue> INTERBREEDING;

	@SubscribeEvent
	public static void registerGameRules(FMLCommonSetupEvent event) {
		SPAWN_RATE = GameRules.register("TS_SpawnRate", GameRules.Category.MOBS, GameRules.IntegerValue.create(33));
		BABY_RATE = GameRules.register("TS_BabyRate", GameRules.Category.MOBS, GameRules.IntegerValue.create(5));
		BREEDING = GameRules.register("TS_Breeding", GameRules.Category.MOBS, GameRules.BooleanValue.create(true));
		RIDABLE = GameRules.register("TS_Ridable", GameRules.Category.MOBS, GameRules.BooleanValue.create(true));
		INVENTORY = GameRules.register("TS_Inventory", GameRules.Category.MOBS, GameRules.BooleanValue.create(true));
		PRODUCE_SILK = GameRules.register("TS_ProduceSilk", GameRules.Category.MOBS, GameRules.BooleanValue.create(true));
		PRODUCE_VENOM = GameRules.register("TS_ProduceVenom", GameRules.Category.MOBS, GameRules.BooleanValue.create(true));
		BABY_POISON = GameRules.register("TS_BabyPoison", GameRules.Category.MOBS, GameRules.BooleanValue.create(false));
		RESPAWN = GameRules.register("TS_Respawn", GameRules.Category.MOBS, GameRules.BooleanValue.create(false));
		RESPAWN_TIME = GameRules.register("TS_RespawnTime", GameRules.Category.MOBS, GameRules.IntegerValue.create(600));
		KEEP_INVENTORY = GameRules.register("TS_KeepInventory", GameRules.Category.MOBS, GameRules.BooleanValue.create(false));
		FALLDAMAGE = GameRules.register("TS_FallDamage", GameRules.Category.MOBS, GameRules.BooleanValue.create(true));
		INTERBREEDING = GameRules.register("TS_Interbreeding", GameRules.Category.MOBS, GameRules.BooleanValue.create(false));
	}
}