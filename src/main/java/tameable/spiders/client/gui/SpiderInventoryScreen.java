package tameable.spiders.client.gui;

import java.util.HashMap;
import org.joml.Vector3f;
import org.joml.Quaternionf;
import com.mojang.blaze3d.systems.RenderSystem;

import net.neoforged.neoforge.network.PacketDistributor;

import net.minecraft.world.level.Level;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.GuiGraphics;

import tameable.spiders.world.inventory.SpiderInventoryMenu;
import tameable.spiders.network.SpiderInventoryButtonMessage;
import tameable.spiders.network.TameableSpidersModVariables;
import tameable.spiders.entity.ModdedSpider;
import tameable.spiders.init.SpiderRules;
import tameable.spiders.enums.Utility;
import tameable.spiders.UtilitySlot;

public class SpiderInventoryScreen extends AbstractContainerScreen<SpiderInventoryMenu> {
	private static final ResourceLocation texture = new ResourceLocation("tameable_spiders:textures/screens/inventory.png");
	private static final ResourceLocation slot = new ResourceLocation("tameable_spiders:textures/screens/slot.png");
	private static final ResourceLocation string = new ResourceLocation("tameable_spiders:textures/screens/string.png");
	private final static HashMap<String, Object> guistate = SpiderInventoryMenu.guistate;
	private final Level world;
	private final int x, y, z;
	private final Player entity;
	public ModdedSpider spider;
	public SpiderInventoryMenu menu;
	public GuiGraphics guiGraphics;

	public SpiderInventoryScreen(SpiderInventoryMenu container, Inventory inventory, Component text) {
		super(container, inventory, text);
		this.menu = container;
		this.world = menu.world;
		this.x = menu.x;
		this.y = menu.y;
		this.z = menu.z;
		this.entity = menu.entity;
		this.spider = (ModdedSpider)menu.boundEntity;
		this.imageWidth = 176;
		this.imageHeight = 166;
	}
	
	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.guiGraphics = guiGraphics;
		this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderEntityInInventoryFollowsAngle(guiGraphics, this.leftPos + 60, this.topPos + 60, spider.getInventoryScale(), 0f + (float) Math.atan((this.leftPos + 60 - mouseX) / 40.0), (float) Math.atan((this.topPos + 38 - mouseY) / 40.0), (LivingEntity) menu.boundEntity);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
		
		this.renderUtilityTooltip(menu.UtilityTop, menu.tooltipTop, mouseX, mouseY);
		this.renderUtilityTooltip(menu.UtilityMiddle, menu.tooltipMiddle, mouseX, mouseY);
		this.renderUtilityTooltip(menu.UtilityBottom, menu.tooltipBottom, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		int VenomLevel = spider.getVenomLevel();
		ResourceLocation bottle = new ResourceLocation("tameable_spiders:textures/screens/venom" + VenomLevel + ".png");
		if(VenomLevel == 0)
			bottle = new ResourceLocation("tameable_spiders:textures/screens/bottle_outline.png");

		guiGraphics.blit(texture, this.leftPos, this.topPos + 5, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
		int maxIdx = menu.get().size() + (menu.countUtilities() == 0 ? 2 : 1);
		for(int idx = 0; idx <= maxIdx; ++idx) {
			Slot customSlot = menu.get().get(idx);
			if(customSlot != null)
				guiGraphics.blit(slot, this.leftPos + customSlot.x - 1 , this.topPos + customSlot.y - 1, 0, 0, 18, 18, 18, 18);
		}

		renderUtility(menu.UtilityTop);
		renderUtility(menu.UtilityMiddle);
		renderUtility(menu.UtilityBottom);
		renderHearts();
		
		RenderSystem.disableBlend();
	}

	public void renderUtility(UtilitySlot utility) {
		int slot = utility.getSlot();
		if(utility.get() == Utility.NONE || (menu.get().get(slot).hasItem() && utility.get() != Utility.VENOM))
			return;
		guiGraphics.blit(getUtilityTexture(utility.get()), this.leftPos + 8, this.topPos + (23 + (18 * slot)), 0, 0, 16, 16, 16, 16);
	}

	public void renderUtilityTooltip(UtilitySlot utility, int offset, int mouseX, int mouseY) {
		Utility type = utility.get();
		Slot slot = menu.get().get(utility.getSlot());
		int startY = topPos + (19 + (18 * utility.getSlot()));

		if (mouseX > leftPos + 7 && mouseX < leftPos + 24 && mouseY > startY && mouseY < startY + 18 && offset != 0)
			guiGraphics.renderTooltip(font, utility.getTooltip(), leftPos + offset, topPos);
	}

	public ResourceLocation getUtilityTexture(Utility utility) {
		int VenomLevel = spider.getVenomLevel();
		String texture = switch(utility) {
			default -> "";
			case SADDLE -> "saddle_outline";
			case ARMOR -> "armor_outline";
			case SILK -> "string_outline";
			case VENOM -> "vemon" + VenomLevel;
		};

		return new ResourceLocation("tameable_spiders:textures/screens/" + texture + ".png");
	}

	public void renderHearts() {
		int yPos = this.topPos + 78;
		float health = ((LivingEntity) menu.boundEntity).getHealth();
		int maxHealth = (int) ((LivingEntity) menu.boundEntity).getMaxHealth();
		double maxHearts = maxHealth * 0.5;
		boolean EvenHP = maxHearts == Math.floor(maxHearts);
		int MiddleX = this.leftPos + (EvenHP ? 78 : 74);
		
		for (int i = 2; i < maxHealth + 2; i += 2) {
    		int xPos = MiddleX - (maxHealth * 5) / 2 + (i * 5);
    		String Heart = "heart_outline";
		
    		if(health > i - 2)
    			Heart = "heart_half";
    		if(health > i - 1)
    			Heart = "heart_full";
  				
  			guiGraphics.blit(new ResourceLocation("tameable_spiders:textures/screens/" + Heart + ".png"), xPos, yPos, 0, 0, 9, 9, 9, 9);
		}
	}

	@Override
	public boolean keyPressed(int key, int b, int c) {
		if(key == 256) {
			this.minecraft.player.closeContainer();
			return true;
		}
		else if(key == 341 && menu.hasUtility(Utility.SADDLE)) {
			PacketDistributor.SERVER.noArg().send(new SpiderInventoryButtonMessage(0, x, y, z));
			SpiderInventoryButtonMessage.handleButtonAction(entity, 0, x, y, z);
			this.minecraft.player.closeContainer();
			return true;
		}
		
		return super.keyPressed(key, b, c);
	}

	private void renderEntityInInventoryFollowsAngle(GuiGraphics guiGraphics, int x, int y, int scale, float angleXComponent, float angleYComponent, LivingEntity entity) {
		Quaternionf pose = new Quaternionf().rotateZ((float) Math.PI);
		Quaternionf cameraOrientation = new Quaternionf().rotateX(angleYComponent * 20 * ((float) Math.PI / 180F));
		pose.mul(cameraOrientation);
		float f2 = entity.yBodyRot;
		float f3 = entity.getYRot();
		float f4 = entity.getXRot();
		float f5 = entity.yHeadRotO;
		float f6 = entity.yHeadRot;
		entity.yBodyRot = 180.0F + angleXComponent * 20.0F;
		entity.setYRot(180.0F + angleXComponent * 40.0F);
		entity.setXRot(-angleYComponent * 20.0F);
		entity.yHeadRot = entity.getYRot();
		entity.yHeadRotO = entity.getYRot();
		InventoryScreen.renderEntityInInventory(guiGraphics, x, y, scale, new Vector3f(0, 0, 0), pose, cameraOrientation, entity);
		entity.yBodyRot = f2;
		entity.setYRot(f3);
		entity.setXRot(f4);
		entity.yHeadRotO = f5;
		entity.yHeadRot = f6;
	}
}