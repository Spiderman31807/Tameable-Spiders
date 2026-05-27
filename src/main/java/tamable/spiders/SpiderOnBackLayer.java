package tamable.spiders.client.renderer.entity.layers;

import tamable.spiders.client.renderer.entity.state.TamableSpiderState;

import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.PlayerModel;

import com.mojang.math.Axis;
import com.mojang.blaze3d.vertex.PoseStack;

@OnlyIn(Dist.CLIENT)
public class SpiderOnBackLayer extends RenderLayer<PlayerRenderState, PlayerModel> {
	private static final ResourceLocation texture = ResourceLocation.withDefaultNamespace("textures/entity/spider/cave_spider.png");
	private final TamableSpiderState state = new TamableSpiderState();
	private final SpiderModel model;

	public SpiderOnBackLayer(RenderLayerParent<PlayerRenderState, PlayerModel> parent, EntityModelSet modelset) {
		super(parent);
		this.model = new SpiderModel(modelset.bakeLayer(ModelLayers.CAVE_SPIDER));
		this.state.onBack = true;
		this.state.scale *= 0.5;
	}

	public void render(PoseStack pose, MultiBufferSource buffer, int packedLight, PlayerRenderState playerState, float headYaw, float headPitch) {
		this.renderOnBack(pose, buffer, packedLight, playerState, headYaw, headPitch, false);
	}

	private void renderOnBack(PoseStack pose, MultiBufferSource buffer, int packedLight, PlayerRenderState playerState, float headYaw, float headPitch, boolean isLeft) {
		pose.pushPose();
		pose.scale(0.55f, 0.55f, 0.55f);
		pose.translate(0, playerState.isCrouching ? 0.6f : 0.4f, 1.6f);
		pose.mulPose(Axis.XP.rotationDegrees(-90));
		pose.mulPose(Axis.YP.rotationDegrees(20));
		pose.translate(-0.4f, 0, -0.4);
		this.state.ageInTicks = playerState.ageInTicks;
		//this.state.walkAnimationPos = playerState.walkAnimationPos;
		//this.state.walkAnimationSpeed = playerState.walkAnimationSpeed;
		this.state.yRot = headYaw * -1.5f;
		this.state.xRot = headPitch + 75;
		this.model.setupAnim(this.state);
		this.model.renderToBuffer(pose, buffer.getBuffer(this.model.renderType(texture)), packedLight, OverlayTexture.NO_OVERLAY);
		pose.popPose();
	}
}