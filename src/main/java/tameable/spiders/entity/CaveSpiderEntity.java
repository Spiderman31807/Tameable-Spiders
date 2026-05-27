package tameable.spiders.entity;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityType;

import tameable.spiders.entity.ModdedSpider;
import tameable.spiders.enums.SpiderVariant;

public class CaveSpiderEntity extends ModdedSpider {
	public CaveSpiderEntity(EntityType<CaveSpiderEntity> type, Level world) {
		super(type, world);
	}
}