package tameable.spiders.entity;

import tameable.spiders.init.TameableSpidersModEntities;
import tameable.spiders.entity.ModdedSpider;

import net.minecraftforge.network.PlayMessages;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.EntityType;

public class SpiderEntity extends ModdedSpider {
	public SpiderEntity(PlayMessages.SpawnEntity packet, Level world) {
		this(TameableSpidersModEntities.SPIDER.get(), world);
	}

	public SpiderEntity(EntityType<SpiderEntity> type, Level world) {
		super(type, world);
	}
}