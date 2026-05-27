package tamable.spiders;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Holder;
import net.minecraft.client.color.item.ItemColors;

public class SpiderArmorItem extends Item {
	private final SpiderArmorItem.BodyType bodyType;
	private final Holder<ArmorMaterial> material;

	public SpiderArmorItem(Holder<ArmorMaterial> material, SpiderArmorItem.BodyType bodyType, Item.Properties properties) {
		super(properties);
		this.bodyType = bodyType;
		this.material = material;
	}

	public Holder<ArmorMaterial> getMaterial() {
		return this.material;
	}

	public static enum BodyType {
		Ordinary(EntityType.SPIDER), Cave(EntityType.CAVE_SPIDER);

		final HolderSet<EntityType<?>> allowedEntities;

		private BodyType(EntityType<?>... allowedTypes) {
			this.allowedEntities = HolderSet.direct(EntityType::builtInRegistryHolder, allowedTypes);
		}
	}
}