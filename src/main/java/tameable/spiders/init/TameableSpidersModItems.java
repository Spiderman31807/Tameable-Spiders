
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package tameable.spiders.init;

import tameable.spiders.item.VenomItem;
import tameable.spiders.item.SilkItem;
import tameable.spiders.item.RootIconItem;
import tameable.spiders.item.PestIconItem;
import tameable.spiders.item.ExterminatorIconItem;
import tameable.spiders.item.BedItem;
import tameable.spiders.item.AllyIconItem;
import tameable.spiders.item.AcheIconItem;
import tameable.spiders.TameableSpidersMod;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;

public class TameableSpidersModItems {
	public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(TameableSpidersMod.MODID);
	public static final DeferredItem<Item> SILK = REGISTRY.register("silk", SilkItem::new);
	public static final DeferredItem<Item> ROOT_ICON = REGISTRY.register("root_icon", RootIconItem::new);
	public static final DeferredItem<Item> ALLY_ICON = REGISTRY.register("ally_icon", AllyIconItem::new);
	public static final DeferredItem<Item> ACHE_ICON = REGISTRY.register("ache_icon", AcheIconItem::new);
	public static final DeferredItem<Item> PEST_ICON = REGISTRY.register("pest_icon", PestIconItem::new);
	public static final DeferredItem<Item> EXTERMINATOR_ICON = REGISTRY.register("exterminator_icon", ExterminatorIconItem::new);
	public static final DeferredItem<Item> BED = REGISTRY.register("bed", BedItem::new);
	public static final DeferredItem<Item> BED_NORTH = block(TameableSpidersModBlocks.BED_NORTH);
	public static final DeferredItem<Item> BED_SOUTH = block(TameableSpidersModBlocks.BED_SOUTH);
	public static final DeferredItem<Item> SILK_WEB = block(TameableSpidersModBlocks.SILK_WEB);
	public static final DeferredItem<Item> VENOM = REGISTRY.register("venom", VenomItem::new);

	// Start of user code block custom items
	// End of user code block custom items
	private static DeferredItem<Item> block(DeferredHolder<Block, Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
	}
}
