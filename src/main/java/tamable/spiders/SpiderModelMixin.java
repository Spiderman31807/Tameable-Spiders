package tamable.spiders.mixin;

import tamable.spiders.client.renderer.entity.state.TamableSpiderState;

import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.model.EntityModel;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Unique;
import net.minecraft.util.Mth;

@Mixin(SpiderModel.class)
public abstract class SpiderModelMixin extends EntityModel<LivingEntityRenderState> {
	@Shadow
	@Final
    private ModelPart head;
    
	private static final AnimationDefinition Sit = AnimationDefinition.Builder.withLength(0.25F)
		.addAnimation("body0", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.25F, KeyframeAnimations.degreeVec(-12.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		)).addAnimation("body0", new AnimationChannel(AnimationChannel.Targets.POSITION, 
			new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.25F, KeyframeAnimations.posVec(0.0F, -0.5F, -0.6F), AnimationChannel.Interpolations.LINEAR)
		)).addAnimation("body1", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.25F, KeyframeAnimations.degreeVec(-27.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		)).addAnimation("body1", new AnimationChannel(AnimationChannel.Targets.POSITION, 
			new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.25F, KeyframeAnimations.posVec(0.0F, -3.7F, -1.9F), AnimationChannel.Interpolations.LINEAR)
		)).build();
			
	private static final AnimationDefinition Stand = AnimationDefinition.Builder.withLength(0.25F)
		.addAnimation("body0", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(-12.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		)).addAnimation("body0", new AnimationChannel(AnimationChannel.Targets.POSITION, 
			new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -0.5F, -0.6F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.25F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		)).addAnimation("body1", new AnimationChannel(AnimationChannel.Targets.ROTATION, 
			new Keyframe(0.0F, KeyframeAnimations.degreeVec(-27.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		)).addAnimation("body1", new AnimationChannel(AnimationChannel.Targets.POSITION, 
			new Keyframe(0.0F, KeyframeAnimations.posVec(0.0F, -3.7F, -1.9F), AnimationChannel.Interpolations.LINEAR),
			new Keyframe(0.25F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
		)).build();
		
	protected SpiderModelMixin(ModelPart root) {
		super(root);
	}

	@Inject(method = "setupAnim", at = @At("TAIL"))
	private void setupAnim(LivingEntityRenderState state, CallbackInfo callback) {
		if (state instanceof TamableSpiderState spiderState) {
			this.animate(spiderState.sittingAnim, spiderState.isSitting ? Sit : Stand, state.ageInTicks, 1f);
			float tilt = spiderState.headRollAngle;
        	if(tilt > 0) {
	        	this.head.zRot = tilt;
				float cosTilt = Mth.cos(tilt);
				float sinTilt = Mth.sin(tilt);
				float newX = state.xRot * cosTilt + state.yRot * sinTilt;
				float newY = state.yRot * cosTilt - state.xRot * sinTilt;
				this.head.xRot = newX * (float) (Math.PI / 180);
				this.head.yRot = newY * (float) (Math.PI / 180);
        	}
		}
	}
}
