package tameable.spiders.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;

import net.minecraft.tags.TagKey;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;

import tameable.spiders.init.SpiderRules;
import tameable.spiders.init.TameableSpidersModEntities;
import tameable.spiders.entity.ModdedSpider;
import tameable.spiders.entity.AgeableSpider;
import tameable.spiders.enums.SpiderVariant;

@Mod.EventBusSubscriber
public class SpawnProcedure {
	@SubscribeEvent
	public static void onEntitySpawned(EntityJoinLevelEvent event) {
		Entity entity = event.getEntity();
		Level world = event.getLevel();
		boolean replaceable = entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("tameable_spiders:can_replace")));
		if(entity == null || !replaceable || world.isClientSide() || event.loadedFromDisk())
			return;
			
		int ReplaceChance = Mth.nextInt(RandomSource.create(), 1, 100);
		int BabyChance = Mth.nextInt(RandomSource.create(), 1, 100);
		boolean IsBaby = BabyChance <= world.getLevelData().getGameRules().getInt(SpiderRules.BABY_RATE);
		if(ReplaceChance > world.getLevelData().getGameRules().getInt(SpiderRules.SPAWN_RATE))
			return;
			
		EntityType Type = SpiderVariant.byType(entity.getType(), true).getType();
		ModdedSpider spider = (ModdedSpider)Type.create(world);

		CompoundTag data = entity.saveWithoutId(new CompoundTag());
		entity.discard();
		spider.load(data);
		spider.setBaby(IsBaby);
		world.addFreshEntity(spider);
	}
}