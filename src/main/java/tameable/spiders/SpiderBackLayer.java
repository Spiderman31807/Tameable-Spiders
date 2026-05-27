package tameable.spiders.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.resources.ResourceLocation;

import tameable.spiders.entity.ModdedSpider;
import tameable.spiders.client.model.SpiderBack;

public class SpiderBackLayer extends RenderLayer<ModdedSpider, SpiderModel<ModdedSpider>> {
    private static final ResourceLocation CollarTexture = new ResourceLocation("tameable_spiders:textures/entities/spider_collar.png");
    private static final ResourceLocation SaddleTexture = new ResourceLocation("tameable_spiders:textures/entities/saddle.png");

    public SpiderBackLayer(RenderLayerParent<ModdedSpider, SpiderModel<ModdedSpider>> spider) {
        super(spider);
    }

    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int light, ModdedSpider entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		EntityModel model = new SpiderBack(Minecraft.getInstance().getEntityModels().bakeLayer(SpiderBack.LAYER_LOCATION));
		model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		model.prepareMobModel(entity, 1, 0, 0);
    	
		if (!entity.isInvisible()) {
			VertexConsumer backVertex = bufferSource.getBuffer(RenderType.entityCutoutNoCull(entity.getTexture(2)));
			model.renderToBuffer(poseStack, backVertex, light, LivingEntityRenderer.getOverlayCoords(entity, 0), 1, 1, 1, 1);

			if (entity.isTame()) {
				float[] afloat = entity.getCollarColor().getTextureDiffuseColors();
				VertexConsumer collarVertex = bufferSource.getBuffer(RenderType.entityCutoutNoCull(CollarTexture));
				model.renderToBuffer(poseStack, collarVertex, light, LivingEntityRenderer.getOverlayCoords(entity, 0), afloat[0], afloat[1], afloat[2], 1.0F);
			}
		}

		if(entity.isSaddled()) {
			model.prepareMobModel(entity, -1, 0, 0);
			VertexConsumer saddleVertex = bufferSource.getBuffer(RenderType.entityCutoutNoCull(SaddleTexture));
			model.renderToBuffer(poseStack, saddleVertex, light, LivingEntityRenderer.getOverlayCoords(entity, 0), 1, 1, 1, 1);
		}
	}
}
