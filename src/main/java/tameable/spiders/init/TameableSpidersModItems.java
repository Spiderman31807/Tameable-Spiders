
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
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.bus.api.IEventBus;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.core.registries.BuiltInRegistries;

public class TameableSpidersModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(BuiltInRegistries.ITEM, TameableSpidersMod.MODID);
	public static final DeferredHolder<Item, Item> BED = REGISTRY.register("bed", () -> new BedItem());
	public static final DeferredHolder<Item, Item> SILK = REGISTRY.register("silk", () -> new SilkItem());
	public static final DeferredHolder<Item, Item> VENOM = REGISTRY.register("venom", () -> new VenomItem());
	public static final DeferredHolder<Item, Item> BED_NORTH = block(TameableSpidersModBlocks.BED_NORTH);
	public static final DeferredHolder<Item, Item> BED_SOUTH = block(TameableSpidersModBlocks.BED_SOUTH);
	public static final DeferredHolder<Item, Item> SILK_WEB = block(TameableSpidersModBlocks.SILK_WEB);
	public static final DeferredHolder<Item, Item> ROOT_ICON = REGISTRY.register("root_icon", () -> new RootIconItem());
	public static final DeferredHolder<Item, Item> ALLY_ICON = REGISTRY.register("ally_icon", () -> new AllyIconItem());
	public static final DeferredHolder<Item, Item> ACHE_ICON = REGISTRY.register("ache_icon", () -> new AcheIconItem());
	public static final DeferredHolder<Item, Item> PEST_ICON = REGISTRY.register("pest_icon", () -> new PestIconItem());
	public static final DeferredHolder<Item, Item> EXTERMINATOR_ICON = REGISTRY.register("exterminator_icon", () -> new ExterminatorIconItem());

	// Start of user code block custom items
	// End of user code block custom items
	public static void register(IEventBus bus) {
		REGISTRY.register(bus);
	}

	private static DeferredHolder<Item, Item> block(DeferredHolder<Block, Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
	}
}
