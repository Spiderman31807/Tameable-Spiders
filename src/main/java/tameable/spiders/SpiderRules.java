package tameable.spiders.init;

import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.level.GameRules;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpiderRules {
	public static GameRules.Key<GameRules.IntegerValue> SPAWN_RATE = GameRules.register("TS_SpawnRate", GameRules.Category.MOBS, GameRules.IntegerValue.create(33));
	public static GameRules.Key<GameRules.IntegerValue> BABY_RATE = GameRules.register("TS_BabyRate", GameRules.Category.MOBS, GameRules.IntegerValue.create(5));
	public static GameRules.Key<GameRules.BooleanValue> BREEDING = GameRules.register("TS_Breeding", GameRules.Category.MOBS, GameRules.BooleanValue.create(true));
	public static GameRules.Key<GameRules.BooleanValue> RIDABLE = GameRules.register("TS_Rideable", GameRules.Category.MOBS, GameRules.BooleanValue.create(true));
	public static GameRules.Key<GameRules.BooleanValue> INVENTORY = GameRules.register("TS_Inventory", GameRules.Category.MOBS, GameRules.BooleanValue.create(true));
	public static GameRules.Key<GameRules.BooleanValue> BABY_POISON = GameRules.register("TS_BabyPoison", GameRules.Category.MOBS, GameRules.BooleanValue.create(false));
	public static GameRules.Key<GameRules.BooleanValue> PRODUCE_SILK = GameRules.register("TS_ProduceSilk", GameRules.Category.MOBS, GameRules.BooleanValue.create(true));
	public static GameRules.Key<GameRules.BooleanValue> PRODUCE_VENOM = GameRules.register("TS_ProduceVenom", GameRules.Category.MOBS, GameRules.BooleanValue.create(true));
	public static GameRules.Key<GameRules.BooleanValue> RESPAWN = GameRules.register("TS_Respawn", GameRules.Category.MOBS, GameRules.BooleanValue.create(true));
	public static GameRules.Key<GameRules.IntegerValue> RESPAWN_TIME = GameRules.register("TS_RespawnTime", GameRules.Category.MOBS, GameRules.IntegerValue.create(600));
	public static GameRules.Key<GameRules.BooleanValue> KEEP_INVENTORY = GameRules.register("TS_KeepInventory", GameRules.Category.MOBS, GameRules.BooleanValue.create(false));
	public static GameRules.Key<GameRules.BooleanValue> FALLDAMAGE = GameRules.register("TS_FallDamage", GameRules.Category.MOBS, GameRules.BooleanValue.create(true));
	public static GameRules.Key<GameRules.BooleanValue> INTERBREEDING = GameRules.register("TS_Interbreeding", GameRules.Category.MOBS, GameRules.BooleanValue.create(false));
}
