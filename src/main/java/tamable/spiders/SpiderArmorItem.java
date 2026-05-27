package tamable.spiders;

import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.HolderSet;

public class SpiderArmorItem extends Item {
	private final SpiderArmorItem.BodyType bodyType;

	public SpiderArmorItem(ArmorMaterial material, SpiderArmorItem.BodyType bodyType, Item.Properties properties) {
		super(material.animalProperties(properties, bodyType.allowedEntities));
		this.bodyType = bodyType;
	}

	public static enum BodyType {
		Ordinary(EntityType.SPIDER), Cave(EntityType.CAVE_SPIDER);

		final HolderSet<EntityType<?>> allowedEntities;

		private BodyType(EntityType<?>... allowedTypes) {
			this.allowedEntities = HolderSet.direct(EntityType::builtInRegistryHolder, allowedTypes);
		}
	}
}
