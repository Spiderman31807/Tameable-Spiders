package tamable.spiders;

import tamable.spiders.init.SpiderItems;
import tamable.spiders.client.model.SpiderSaddleModel;
import tamable.spiders.client.model.SpiderArmorModel;

import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.model.geom.ModelLayerLocation;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class Events {
	private static final CreativeModeTab.TabVisibility fullVis = CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS;
	private static final ItemStack armorTabTarget = new ItemStack(Items.DIAMOND_HORSE_ARMOR);

	@SubscribeEvent
	public static void registerModels(BuildCreativeModeTabContentsEvent event) {
		ResourceKey<CreativeModeTab> tabKey = event.getTabKey();
		if (tabKey == CreativeModeTabs.COMBAT) {
			event.insertAfter(armorTabTarget, new ItemStack(SpiderItems.DiamondArmor.get()), fullVis);
			event.insertAfter(armorTabTarget, new ItemStack(SpiderItems.GoldenArmor.get()), fullVis);
			event.insertAfter(armorTabTarget, new ItemStack(SpiderItems.IronArmor.get()), fullVis);
			event.insertAfter(armorTabTarget, new ItemStack(SpiderItems.ChainmailArmor.get()), fullVis);
			event.insertAfter(armorTabTarget, new ItemStack(SpiderItems.LeatherArmor.get()), fullVis);
		}
	}

	@SubscribeEvent
	public static void registerModels(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tamable_spiders", "spider_armor"), "main"), () -> SpiderArmorModel.createLayer(false));
		event.registerLayerDefinition(new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tamable_spiders", "spider_armor"), "cave"), () -> SpiderArmorModel.createLayer(true));
		event.registerLayerDefinition(new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tamable_spiders", "spider_saddle"), "main"), () -> SpiderSaddleModel.createLayer(false, false));
		event.registerLayerDefinition(new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tamable_spiders", "spider_saddle"), "armor"), () -> SpiderSaddleModel.createLayer(true, false));
		event.registerLayerDefinition(new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tamable_spiders", "spider_saddle"), "cave"), () -> SpiderSaddleModel.createLayer(false, true));
		event.registerLayerDefinition(new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tamable_spiders", "spider_saddle"), "cave_armor"), () -> SpiderSaddleModel.createLayer(true, true));

	}

	@OnlyIn(Dist.CLIENT)
	public static boolean isPlayerJumping(Player player) {
		return player instanceof LocalPlayer local && local.input.keyPresses.jump();
	}

	@OnlyIn(Dist.CLIENT)
	public static boolean isPlayerSprinting(Player player) {
		return player instanceof LocalPlayer local && local.input.keyPresses.sprint();
	}
}
