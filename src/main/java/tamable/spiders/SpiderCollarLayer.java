package tamable.spiders.client.renderer.entity.layers;

import tamable.spiders.client.renderer.entity.state.TamableSpiderState;

import net.minecraft.world.item.DyeColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.model.SpiderModel;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class SpiderCollarLayer extends RenderLayer<TamableSpiderState, SpiderModel> {
	private static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("tamable_spiders", "textures/entities/spider_collar.png");

	public SpiderCollarLayer(RenderLayerParent<TamableSpiderState, SpiderModel> parent) {
		super(parent);
	}

	public void render(PoseStack pose, MultiBufferSource buffer, int packedLight, TamableSpiderState state, float headYaw, float headPitch) {
		DyeColor dye = state.collarColor;
		if (dye != null && !state.isInvisible) {
			int color = dye.getTextureDiffuseColor();
			VertexConsumer vertex = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
			this.getParentModel().renderToBuffer(pose, vertex, packedLight, OverlayTexture.NO_OVERLAY, color);
		}
	}
}
