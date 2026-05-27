package tamable.spiders.client.model;

import tamable.spiders.client.renderer.entity.state.TamableSpiderState;

import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.builders.MeshTransformer;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.AnimationChannel;

public class SpiderSaddleModel extends EntityModel<TamableSpiderState> {
	private static final AnimationDefinition Sit = AnimationDefinition.Builder.withLength(0.25F)
			.addAnimation("saddle",
					new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
							new Keyframe(0.25F, KeyframeAnimations.degreeVec(-27.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
			.addAnimation("saddle", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.25F, KeyframeAnimations.posVec(0.0F, -3.7F, -1.9F), AnimationChannel.Interpolations.LINEAR)))
			.build();

	private static final AnimationDefinition Stand = AnimationDefinition.Builder.withLength(0.25F)
			.addAnimation("saddle",
					new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(-27.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
							new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
			.addAnimation("saddle", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -3.7F, -1.9F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.25F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
			.build();
	private final ModelPart saddle;

	public SpiderSaddleModel(ModelPart root) {
		super(root);
		this.saddle = root.getChild("saddle");
	}

	public static LayerDefinition createLayer(boolean hasArmorLayer, boolean isCave) {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition definition = mesh.getRoot();
		PartDefinition saddle = definition.addOrReplaceChild("saddle", CubeListBuilder.create().texOffs(24, 8).addBox(-6, -8, -3, 12, 16, 8, new CubeDeformation(hasArmorLayer ? 0 : -0.75f)), PartPose.offsetAndRotation(0, 15, 9, 1.5708f, 0, 0));
		return !isCave ? LayerDefinition.create(mesh, 64, 32) : LayerDefinition.create(mesh, 64, 32).apply(MeshTransformer.scaling(0.7F));
	}

	@Override
	public void setupAnim(TamableSpiderState state) {
		super.setupAnim(state);
		this.animate(state.sittingAnim, state.isSitting ? Sit : Stand, state.ageInTicks, 1f);
	}
}
