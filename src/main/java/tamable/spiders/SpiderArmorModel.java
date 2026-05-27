package tamable.spiders.client.model;

import tamable.spiders.client.renderer.entity.state.TamableSpiderState;

import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

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

@OnlyIn(Dist.CLIENT)
public class SpiderArmorModel extends EntityModel<TamableSpiderState> {
	private static final AnimationDefinition Sit = AnimationDefinition.Builder.withLength(0.25F)
			.addAnimation("armor",
					new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
							new Keyframe(0.25F, KeyframeAnimations.degreeVec(-27.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
			.addAnimation("armor", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.25F, KeyframeAnimations.posVec(0.0F, -3.7F, -1.9F), AnimationChannel.Interpolations.LINEAR)))
			.build();

	private static final AnimationDefinition Stand = AnimationDefinition.Builder.withLength(0.25F)
			.addAnimation("armor",
					new AnimationChannel(AnimationChannel.Targets.ROTATION, new Keyframe(0.0F, KeyframeAnimations.degreeVec(-27.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
							new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
			.addAnimation("armor", new AnimationChannel(AnimationChannel.Targets.POSITION, new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -3.7F, -1.9F), AnimationChannel.Interpolations.LINEAR),
					new Keyframe(0.25F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)))
			.build();

	private final ModelPart helmet;
	private final ModelPart armor;

	public SpiderArmorModel(ModelPart root) {
		super(root);
		this.helmet = root.getChild("helmet");
		this.armor = root.getChild("armor");
	}

	public static LayerDefinition createLayer(boolean isCave) {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition definition = mesh.getRoot();
		PartDefinition helmet = definition.addOrReplaceChild("helmet", CubeListBuilder.create().texOffs(32, 0).addBox(-4, -4, -8, 8, 7, 8, new CubeDeformation(0.75F)), PartPose.offset(0, 15, -3));
		PartDefinition body_armor = definition.addOrReplaceChild("armor", CubeListBuilder.create().texOffs(0, 12).addBox(-5, -4, -6, 10, 8, 12, new CubeDeformation(0.75F)), PartPose.offset(0, 15, 9));
		return !isCave ? LayerDefinition.create(mesh, 64, 32) : LayerDefinition.create(mesh, 64, 32).apply(MeshTransformer.scaling(0.7F));
	}

	public void setupAnim(TamableSpiderState state) {
		super.setupAnim(state);
		this.helmet.yRot = state.yRot * (float) (Math.PI / 180);
		this.helmet.xRot = state.xRot * (float) (Math.PI / 180);
		this.animate(state.sittingAnim, state.isSitting ? Sit : Stand, state.ageInTicks, 1f);
	}
}
