package tamable.spiders.init;

import tamable.spiders.world.level.block.WebPathBlock;
import tamable.spiders.TamableSpidersMod;
import tamable.spiders.SpiderArmorItem;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredBlock;

import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;

import java.util.function.Supplier;
import java.util.function.Function;
import net.minecraft.core.Holder;

public class SpiderItems {
	public static final DeferredRegister.Blocks Blocks = DeferredRegister.createBlocks(TamableSpidersMod.MODID);
	public static final DeferredBlock<Block> WebPathBlock = registerBlock("webpath", WebPathBlock::new,
			BlockBehaviour.Properties.of().mapColor(MapColor.WOOL).sound(SoundType.COBWEB).noCollission().requiresCorrectToolForDrops().strength(0.2f).pushReaction(PushReaction.DESTROY));;

	private static <B extends Block> DeferredBlock<B> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> supplier, BlockBehaviour.Properties properties) {
		return Blocks.registerBlock(name, supplier, properties);
	}

	public static final DeferredRegister.Items Items = DeferredRegister.createItems(TamableSpidersMod.MODID);
	public static final DeferredItem<BlockItem> WebPath = Items.registerSimpleBlockItem(WebPathBlock);
	public static final DeferredItem<Item> DiamondArmor = registerItem("diamond_spider_armor", (properties) -> new SpiderArmorItem(ArmorMaterials.DIAMOND, SpiderArmorItem.BodyType.Ordinary, properties), new Item.Properties().stacksTo(1));
	public static final DeferredItem<Item> ChainmailArmor = registerItem("chainmail_spider_armor", (properties) -> new SpiderArmorItem(ArmorMaterials.GOLD, SpiderArmorItem.BodyType.Ordinary, properties), new Item.Properties().stacksTo(1));
	public static final DeferredItem<Item> GoldenArmor = registerItem("golden_spider_armor", (properties) -> new SpiderArmorItem(ArmorMaterials.CHAINMAIL, SpiderArmorItem.BodyType.Ordinary, properties), new Item.Properties().stacksTo(1));
	public static final DeferredItem<Item> IronArmor = registerItem("iron_spider_armor", (properties) -> new SpiderArmorItem(ArmorMaterials.IRON, SpiderArmorItem.BodyType.Ordinary, properties), new Item.Properties().stacksTo(1));
	public static final DeferredItem<Item> LeatherArmor = registerItem("leather_spider_armor", (properties) -> new SpiderArmorItem(ArmorMaterials.LEATHER, SpiderArmorItem.BodyType.Ordinary, properties), new Item.Properties().stacksTo(1));

	private static <I extends Item> DeferredItem<I> registerItem(String name, Function<Item.Properties, ? extends I> supplier, Item.Properties defaultProperties) {
		return Items.registerItem(name, supplier, defaultProperties);
	}

	private static <I extends Item> DeferredItem<I> registerItem(String name, Function<Item.Properties, ? extends I> supplier) {
		return Items.registerItem(name, supplier, new Item.Properties());
	}
}
