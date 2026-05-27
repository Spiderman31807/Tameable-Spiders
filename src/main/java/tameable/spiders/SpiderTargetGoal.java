package tameable.spiders.goal;

import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.LivingEntity;

import tameable.spiders.entity.AgeableSpider;

public class SpiderTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
    public SpiderTargetGoal(AgeableSpider spider, Class<T> target) {
        super(spider, target, true);
    }

    @Override
    public boolean canUse() {
    	float light = this.mob.getLightLevelDependentMagicValue();
        return light >= 0.5F ? false : ((AgeableSpider) this.mob).canAttackRandom() && super.canUse();
   	}
}