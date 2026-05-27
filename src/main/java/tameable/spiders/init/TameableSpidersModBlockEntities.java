
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package tameable.spiders.init;

import tameable.spiders.block.entity.SilkWebBlockEntity;
import tameable.spiders.block.entity.BedNorthBlockEntity;
import tameable.spiders.TameableSpidersMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.Block;

public class TameableSpidersModBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TameableSpidersMod.MODID);
	public static final RegistryObject<BlockEntityType<?>> BED_NORTH = register("bed_north", TameableSpidersModBlocks.BED_NORTH, BedNorthBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> SILK_WEB = register("silk_web", TameableSpidersModBlocks.SILK_WEB, SilkWebBlockEntity::new);

	private static RegistryObject<BlockEntityType<?>> register(String registryname, RegistryObject<Block> block, BlockEntityType.BlockEntitySupplier<?> supplier) {
		return REGISTRY.register(registryname, () -> BlockEntityType.Builder.of(supplier, block.get()).build(null));
	}
}
