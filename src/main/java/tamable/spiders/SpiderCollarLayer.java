package tamable.spiders.client.renderer.entity.layers;

import tamable.spiders.TamableSpider;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.model.SpiderModel;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class SpiderCollarLayer extends RenderLayer<Spider, SpiderModel<Spider>> {
	private static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("tamable_spiders", "textures/entities/spider_collar.png");

	public SpiderCollarLayer(RenderLayerParent<Spider, SpiderModel<Spider>> parent) {
		super(parent);
	}

	public void render(PoseStack pose, MultiBufferSource buffer, int packedLight, Spider spider, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float partialTick) {
		if (spider instanceof TamableSpider tamable) {
			DyeColor dye = tamable.getCollarDyeColor();
			if (dye != null && !spider.isInvisible()) {
				int color = dye.getTextureDiffuseColor();
				VertexConsumer vertex = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
				this.getParentModel().renderToBuffer(pose, vertex, packedLight, OverlayTexture.NO_OVERLAY, color);
			}
		}
	}
}