package tameable.spiders.goal;

import java.util.EnumSet;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import tameable.spiders.entity.TamableSpider;

public class SittingGoal extends Goal {
    private final TamableSpider mob;

    public SittingGoal(TamableSpider spider) {
        this.mob = spider;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.isSitting() && !this.mob.isVehicle();
    }

    @Override
    public boolean canUse() {
        if (!this.mob.isTame()) {
            return false;
        } else if (this.mob.isVehicle()) {
            return false;
        } else if (this.mob.isInWaterOrBubble()) {
            return false;
        } else if (!this.mob.onGround()) {
            return false;
        } else {
            LivingEntity livingentity = this.mob.getOwner();
            if (livingentity == null) {
                return true;
            } else {
                return this.mob.distanceToSqr(livingentity) < 144.0 && livingentity.getLastHurtByMob() != null ? false : this.mob.isSitting();
            }
        }
    }

    @Override
    public void start() {
        this.mob.getNavigation().stop();
        this.mob.setSittingPose(true);
    }

    @Override
    public void stop() {
        this.mob.setSittingPose(false);
    }
}