
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package tameable.spiders.init;

import tameable.spiders.block.SilkWebBlock;
import tameable.spiders.block.BedSouthBlock;
import tameable.spiders.block.BedNorthBlock;
import tameable.spiders.TameableSpidersMod;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;

import net.minecraft.world.level.block.Block;

public class TameableSpidersModBlocks {
	public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(TameableSpidersMod.MODID);
	public static final DeferredBlock<Block> BED_NORTH = REGISTRY.register("bed_north", BedNorthBlock::new);
	public static final DeferredBlock<Block> BED_SOUTH = REGISTRY.register("bed_south", BedSouthBlock::new);
	public static final DeferredBlock<Block> SILK_WEB = REGISTRY.register("silk_web", SilkWebBlock::new);
	// Start of user code block custom blocks
	// End of user code block custom blocks
}
