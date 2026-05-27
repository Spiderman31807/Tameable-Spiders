
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

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;

public class TameableSpidersModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, TameableSpidersMod.MODID);
	public static final RegistryObject<Item> BED = REGISTRY.register("bed", () -> new BedItem());
	public static final RegistryObject<Item> BED_NORTH = block(TameableSpidersModBlocks.BED_NORTH);
	public static final RegistryObject<Item> BED_SOUTH = block(TameableSpidersModBlocks.BED_SOUTH);
	public static final RegistryObject<Item> VENOM = REGISTRY.register("venom", () -> new VenomItem());
	public static final RegistryObject<Item> SILK = REGISTRY.register("silk", () -> new SilkItem());
	public static final RegistryObject<Item> ROOT_ICON = REGISTRY.register("root_icon", () -> new RootIconItem());
	public static final RegistryObject<Item> ALLY_ICON = REGISTRY.register("ally_icon", () -> new AllyIconItem());
	public static final RegistryObject<Item> ACHE_ICON = REGISTRY.register("ache_icon", () -> new AcheIconItem());
	public static final RegistryObject<Item> PEST_ICON = REGISTRY.register("pest_icon", () -> new PestIconItem());
	public static final RegistryObject<Item> EXTERMINATOR_ICON = REGISTRY.register("exterminator_icon", () -> new ExterminatorIconItem());
	public static final RegistryObject<Item> SILK_WEB = block(TameableSpidersModBlocks.SILK_WEB);

	// Start of user code block custom items
	// End of user code block custom items
	private static RegistryObject<Item> block(RegistryObject<Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
	}
}
