
package tameable.spiders.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class SilkItem extends Item {
	public SilkItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}
}
