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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import tameable.spiders.entity.ModdedSpider;
import tameable.spiders.client.model.BabyHead;

@OnlyIn(Dist.CLIENT)
public class SpiderBabyHead extends RenderLayer<ModdedSpider, SpiderModel<ModdedSpider>> {
    private static final RenderType SPIDER_EYES = RenderType.eyes(new ResourceLocation("textures/entity/spider_eyes.png"));
	
    public SpiderBabyHead(RenderLayerParent<ModdedSpider, SpiderModel<ModdedSpider>> spider) {
        super(spider);
    }

    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int light, ModdedSpider entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		if (!entity.isInvisible() && entity.isBaby()) {
			EntityModel model = new BabyHead(Minecraft.getInstance().getEntityModels().bakeLayer(BabyHead.LAYER_LOCATION));
			model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

			VertexConsumer Vertex = bufferSource.getBuffer(RenderType.entityCutoutNoCull(entity.getTexture(3)));
			model.renderToBuffer(poseStack, Vertex, light, LivingEntityRenderer.getOverlayCoords(entity, 0), 1, 1, 1, 1);

			VertexConsumer HeadVertex = bufferSource.getBuffer(SPIDER_EYES);
			model.renderToBuffer(poseStack, HeadVertex, light, LivingEntityRenderer.getOverlayCoords(entity, 0), 1, 1, 1, 1);
		}
	}
}
