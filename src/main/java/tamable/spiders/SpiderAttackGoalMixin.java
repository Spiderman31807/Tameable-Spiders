package tamable.spiders.mixin;

import tamable.spiders.TamableSpider;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.PathfinderMob;

@Mixin(targets = {"net.minecraft.world.entity.monster.Spider$SpiderAttackGoal"})
public class SpiderAttackGoalMixin extends MeleeAttackGoal {
	protected SpiderAttackGoalMixin(PathfinderMob mob, double speed, boolean sight) {
		super(mob, speed, sight);
	}

	@Inject(method = "canContinueToUse", at = @At("HEAD"), cancellable = true)
	private void canContinueToUse(CallbackInfoReturnable<Boolean> callback) {
		if (this.mob instanceof TamableSpider spider && spider.isTame())
			callback.setReturnValue(super.canContinueToUse());
	}
}
