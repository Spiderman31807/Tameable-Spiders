package tamable.spiders.client.renderer.entity.layers;

import tamable.spiders.init.SpiderItems;
import tamable.spiders.client.renderer.entity.state.TamableSpiderState;
import tamable.spiders.client.model.SpiderArmorModel;

import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
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
public class SpiderArmorLayer extends RenderLayer<TamableSpiderState, SpiderModel> {
	private static final ModelLayerLocation normalLayer = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tamable_spiders", "spider_armor"), "main");
	private static final ModelLayerLocation caveLayer = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tamable_spiders", "spider_armor"), "cave");
	private static final Function<String, ResourceLocation> textureGetter = (material) -> ResourceLocation.fromNamespaceAndPath("tamable_spiders", "textures/entities/spider_armor_" + material + ".png");
	private final SpiderArmorModel armorModel;
	private final SpiderArmorModel caveModel;

	public SpiderArmorLayer(RenderLayerParent<TamableSpiderState, SpiderModel> parent, EntityModelSet modelSet) {
		super(parent);
		this.armorModel = new SpiderArmorModel(modelSet.bakeLayer(normalLayer));
		this.caveModel = new SpiderArmorModel(modelSet.bakeLayer(caveLayer));
	}

	public void render(PoseStack pose, MultiBufferSource buffer, int packedLight, TamableSpiderState state, float float1, float float2) {
		ResourceLocation texture = getTexture(state.armor);
		if (texture != null) {
			SpiderArmorModel model = state.type == EntityType.CAVE_SPIDER ? caveModel : armorModel;
			model.setupAnim(state);
			VertexConsumer vertex = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
			int color = texture.getPath().contains("leather") ? DyedItemColor.getOrDefault(state.armor, DyedItemColor.LEATHER_COLOR) : -1;
			model.renderToBuffer(pose, vertex, packedLight, OverlayTexture.NO_OVERLAY, color);
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
