package tamable.spiders.mixin;

import tamable.spiders.TamableSpider;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;

@Mixin(NearestAttackableTargetGoal.class)
public abstract class NearestAttackableTargetGoalMixin extends TargetGoal {
	@Shadow
	protected TargetingConditions targetConditions;

	protected NearestAttackableTargetGoalMixin(Mob mob, boolean sight) {
		super(mob, sight);
	}

	@Inject(method = "(Lnet/minecraft/world/entity/Mob;Ljava/lang/Class;IZZLnet/minecraft/world/entity/ai/targeting/TargetingConditions$Selector;)V", at = @At("TAIL"))
	private void constructor(Mob mob, Class<LivingEntity> targetType, int randomInterval, boolean sight, boolean reach, TargetingConditions.Selector selector, CallbackInfo info) {
		if (selector == null)
			return;
		this.targetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector((entity, server) -> selector.test(entity, server) && !(entity instanceof TamableSpider spider && spider.isTame()));
	}
}
