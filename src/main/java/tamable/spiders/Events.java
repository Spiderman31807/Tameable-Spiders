package tamable.spiders;

import tamable.spiders.init.SpiderItems;
import tamable.spiders.client.model.SpiderSaddleModel;
import tamable.spiders.client.model.SpiderArmorModel;

import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.Minecraft;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class Events {
	private static final CreativeModeTab.TabVisibility fullVis = CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS;
	private static final ItemStack armorTabTarget = new ItemStack(Items.DIAMOND_HORSE_ARMOR);

	@SubscribeEvent
	public static void registerDyeable(RegisterColorHandlersEvent.Item event) {
		event.register((color, dyed) -> dyed > 0 ? -1 : DyedItemColor.getOrDefault(color, -6265536), SpiderItems.LeatherArmor);
	}

	@SubscribeEvent
	public static void registerModels(BuildCreativeModeTabContentsEvent event) {
		ResourceKey<CreativeModeTab> tabKey = event.getTabKey();
		if (tabKey == CreativeModeTabs.COMBAT) {
			event.insertAfter(armorTabTarget, new ItemStack(SpiderItems.DiamondArmor.get()), fullVis);
			event.insertAfter(armorTabTarget, new ItemStack(SpiderItems.GoldenArmor.get()), fullVis);
			event.insertAfter(armorTabTarget, new ItemStack(SpiderItems.IronArmor.get()), fullVis);
			event.insertAfter(armorTabTarget, new ItemStack(SpiderItems.ChainmailArmor.get()), fullVis);
			event.insertAfter(armorTabTarget, new ItemStack(SpiderItems.LeatherArmor.get()), fullVis);
		} else if (tabKey == CreativeModeTabs.NATURAL_BLOCKS) {
			event.insertAfter(new ItemStack(Items.COBWEB), new ItemStack(SpiderItems.WebPath.get()), fullVis);
		}
	}

	@SubscribeEvent
	public static void registerModels(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tamable_spiders", "spider_armor"), "main"), () -> SpiderArmorModel.createLayer(false));
		event.registerLayerDefinition(new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tamable_spiders", "spider_armor"), "cave"), () -> SpiderArmorModel.createLayer(true));
		event.registerLayerDefinition(new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tamable_spiders", "spider_saddle"), "main"), () -> SpiderSaddleModel.createLayer(false, false));
		event.registerLayerDefinition(new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tamable_spiders", "spider_saddle"), "cave"), () -> SpiderSaddleModel.createLayer(false, true));
		event.registerLayerDefinition(new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tamable_spiders", "spider_saddle"), "main_armor"), () -> SpiderSaddleModel.createLayer(true, false));
		event.registerLayerDefinition(new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tamable_spiders", "spider_saddle"), "cave_armor"), () -> SpiderSaddleModel.createLayer(true, true));
	}

	@SubscribeEvent
	public static void RenderHud(RenderGuiEvent.Pre event) {
		if (Minecraft.getInstance().player instanceof SpiderData data && data.isSpider()) {
			int width = event.getGuiGraphics().guiWidth();
			int height = event.getGuiGraphics().guiHeight();
			int climbMode = data.getClimbMode();
			String texture = ClimbMode.texture();
			event.getGuiGraphics().blit(ResourceLocation.parse("textures/block/ladder.png"), width - 30, height - 30, 0, 0, 16, 16, 16, 16);
			if (texture != "")
				event.getGuiGraphics().blit(ResourceLocation.parse("tamable_spiders:textures/screens/" + texture + ".png"), width - 30, height - (climbMode == 0 ? 30 : (climbMode == 2 ? 34 : 26)), 0, 0, 16, 16, 16, 16);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static boolean isPlayerJumping(Player player) {
		return player instanceof LocalPlayer local && local.input.jumping;
	}

	@OnlyIn(Dist.CLIENT)
	public static boolean isPlayerSprinting(Player player) {
		return player instanceof LocalPlayer local && local.isSprinting();
	}
}