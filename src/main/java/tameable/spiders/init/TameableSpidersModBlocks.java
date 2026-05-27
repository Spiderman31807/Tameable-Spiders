
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package tameable.spiders.init;

import tameable.spiders.block.SilkWebBlock;
import tameable.spiders.block.BedSouthBlock;
import tameable.spiders.block.BedNorthBlock;
import tameable.spiders.TameableSpidersMod;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.BuiltInRegistries;

public class TameableSpidersModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(BuiltInRegistries.BLOCK, TameableSpidersMod.MODID);
	public static final DeferredHolder<Block, Block> BED_NORTH = REGISTRY.register("bed_north", () -> new BedNorthBlock());
	public static final DeferredHolder<Block, Block> BED_SOUTH = REGISTRY.register("bed_south", () -> new BedSouthBlock());
	public static final DeferredHolder<Block, Block> SILK_WEB = REGISTRY.register("silk_web", () -> new SilkWebBlock());
	// Start of user code block custom blocks
	// End of user code block custom blocks
}
