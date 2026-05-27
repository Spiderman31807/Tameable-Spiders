package tameable.spiders.entity;

import tameable.spiders.init.TameableSpidersModEntities;
import tameable.spiders.entity.ModdedSpider;

import net.minecraftforge.network.PlayMessages;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.EntityType;

public class CaveSpiderEntity extends ModdedSpider {
	public CaveSpiderEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(TameableSpidersModEntities.CAVE_SPIDER.get(), world);
	}

	public CaveSpiderEntity(EntityType<CaveSpiderEntity> type, Level world) {
		super(type, world);
	}
}