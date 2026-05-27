package tamable.spiders.mixin;

import tamable.spiders.TamableSpider;
import tamable.spiders.Animations;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;

import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.HierarchicalModel;

@Mixin(SpiderModel.class)
public abstract class SpiderModelMixin extends HierarchicalModel<Entity> {
	@Shadow
	@Final
	private ModelPart root;
	@Shadow
	@Final
	private ModelPart head;

	private SpiderModelMixin() {
		super();
	}

	@Inject(method = "setupAnim", at = @At("TAIL"))
	private void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headPitch, float headYaw, CallbackInfo callback) {
		if (entity instanceof TamableSpider spider) {
			this.root.getChild("body0").setPos(0f, 15f, 0f);
			this.root.getChild("body0").setRotation(0f, 0f, 0f);
			this.root.getChild("body1").setPos(0f, 15f, 9f);
			this.root.getChild("body1").setRotation(0f, 0f, 0f);
			this.animate(spider.getSittingAnimation(), spider.isInSittingPose() ? Animations.Sit : Animations.Stand, ageInTicks, 1f);
			float tiltAngle = spider.getHeadRollAngle(ageInTicks);
			float cosTilt = Mth.cos(tiltAngle);
			float sinTilt = Mth.sin(tiltAngle);
			float newX = headYaw * cosTilt + headPitch * sinTilt;
			float newY = headPitch * cosTilt - headYaw * sinTilt;
			this.head.xRot = newX * ((float) Math.PI / 180F);
			this.head.yRot = newY * ((float) Math.PI / 180F);
			this.head.zRot = tiltAngle;
		}
	}
}