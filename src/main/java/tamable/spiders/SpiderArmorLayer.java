package tamable.spiders.client.renderer.entity.layers;

import tamable.spiders.init.SpiderItems;
import tamable.spiders.client.model.SpiderArmorModel;
import tamable.spiders.TamableSpider;

import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.SpiderModel;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

import com.google.common.base.Function;

@OnlyIn(Dist.CLIENT)
public class SpiderArmorLayer extends RenderLayer<Spider, SpiderModel<Spider>> {
	private static final ModelLayerLocation normalLayer = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tamable_spiders", "spider_armor"), "main");
	private static final ModelLayerLocation caveLayer = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tamable_spiders", "spider_armor"), "cave");
	private static final Function<String, ResourceLocation> textureGetter = (material) -> ResourceLocation.fromNamespaceAndPath("tamable_spiders", "textures/entities/spider_armor_" + material + ".png");
	private final SpiderArmorModel armorModel;
	private final SpiderArmorModel caveModel;

	public SpiderArmorLayer(RenderLayerParent<Spider, SpiderModel<Spider>> parent, EntityModelSet modelSet) {
		super(parent);
		this.armorModel = new SpiderArmorModel(modelSet.bakeLayer(normalLayer));
		this.caveModel = new SpiderArmorModel(modelSet.bakeLayer(caveLayer));
	}

	public void render(PoseStack pose, MultiBufferSource buffer, int packedLight, Spider spider, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float headPitch, float headYaw) {
		if (spider instanceof TamableSpider tamable) {
			ResourceLocation texture = getTexture(tamable.getArmorItem());
			if (texture != null) {
				SpiderArmorModel model = spider.getType() == EntityType.CAVE_SPIDER ? caveModel : armorModel;
				model.setupAnim(spider, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch);
				VertexConsumer vertex = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
				int color = texture.getPath().contains("leather") ? DyedItemColor.getOrDefault(tamable.getArmorItem(), DyedItemColor.LEATHER_COLOR) : -1;
				model.renderToBuffer(pose, vertex, packedLight, OverlayTexture.NO_OVERLAY, color);
			}
		}
	}

	private static ResourceLocation getTexture(ItemStack armorStack) {
		Item armor = armorStack.getItem();
		if (armor == SpiderItems.DiamondArmor.get())
			return textureGetter.apply("diamond");
		if (armor == SpiderItems.ChainmailArmor.get())
			return textureGetter.apply("chainmail");
		if (armor == SpiderItems.GoldenArmor.get())
			return textureGetter.apply("golden");
		if (armor == SpiderItems.IronArmor.get())
			return textureGetter.apply("iron");
		if (armor == SpiderItems.LeatherArmor.get())
			return textureGetter.apply("leather");
		return null;
	}
}