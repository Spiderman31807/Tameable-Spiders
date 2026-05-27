
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package tameable.spiders.init;

import tameable.spiders.entity.SpiderEntity;
import tameable.spiders.entity.CaveSpiderEntity;
import tameable.spiders.TameableSpidersMod;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.event.entity.SpawnPlacementRegisterEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.registries.Registries;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class TameableSpidersModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(Registries.ENTITY_TYPE, TameableSpidersMod.MODID);
	public static final DeferredHolder<EntityType<?>, EntityType<SpiderEntity>> SPIDER = register("spider",
			EntityType.Builder.<SpiderEntity>of(SpiderEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)

					.sized(1.4f, 0.9f));
	public static final DeferredHolder<EntityType<?>, EntityType<CaveSpiderEntity>> CAVE_SPIDER = register("cave_spider",
			EntityType.Builder.<CaveSpiderEntity>of(CaveSpiderEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)

					.sized(1.4f, 0.9f));

	private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname));
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerEntity(Capabilities.ItemHandler.ENTITY, SPIDER.get(), (living, context) -> living.getCombinedInventory());
		event.registerEntity(Capabilities.ItemHandler.ENTITY, CAVE_SPIDER.get(), (living, context) -> living.getCombinedInventory());
	}

	@SubscribeEvent
	public static void init(SpawnPlacementRegisterEvent event) {
		SpiderEntity.init(event);
		CaveSpiderEntity.init(event);
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(SPIDER.get(), SpiderEntity.createAttributes().build());
		event.put(CAVE_SPIDER.get(), CaveSpiderEntity.createAttributes().build());
	}
}
