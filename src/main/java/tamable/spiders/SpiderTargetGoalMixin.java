package tamable.spiders.mixin;

import tamable.spiders.TamableSpider;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.LivingEntity;

@Mixin(targets = {"net.minecraft.world.entity.monster.Spider$SpiderTargetGoal"})
public class SpiderTargetGoalMixin extends NearestAttackableTargetGoal {
	protected SpiderTargetGoalMixin(PathfinderMob mob, Class<LivingEntity> targetClass, boolean sight) {
		super(mob, targetClass, sight);
	}

	@Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
	private void canUse(CallbackInfoReturnable<Boolean> callback) {
		if (this.mob instanceof TamableSpider spider && spider.isTame())
			callback.setReturnValue(false);
	}
}
