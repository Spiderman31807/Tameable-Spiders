
package tameable.spiders.item;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

public class ExterminatorIconItem extends Item {
	public ExterminatorIconItem() {
		super(new Item.Properties().stacksTo(0).rarity(Rarity.COMMON));
	}

	@Override
	public float getDestroySpeed(ItemStack itemstack, BlockState state) {
		return 0f;
	}
}
