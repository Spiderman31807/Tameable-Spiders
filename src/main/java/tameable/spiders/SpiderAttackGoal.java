package tameable.spiders.goal;

import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

import tameable.spiders.entity.AgeableSpider;

public class SpiderAttackGoal extends MeleeAttackGoal {
    public SpiderAttackGoal(AgeableSpider spider) {
        super(spider, 1.0, true);
    }

    @Override
    public boolean canUse() {
        return super.canUse() && !this.mob.isVehicle();
    }

    @Override
    public boolean canContinueToUse() {
		if(((AgeableSpider) this.mob).canAttackDuringDay())
			return super.canContinueToUse();
					
        float f = this.mob.getLightLevelDependentMagicValue();
        if (f >= 0.5F && this.mob.getRandom().nextInt(100) == 0) {
            this.mob.setTarget(null);
            return false;
        } else {
            return super.canContinueToUse();
        }
    }
}