package tameable.spiders.goal;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import tameable.spiders.entity.BreedableSpider;

public class BreedGoal extends Goal {
    private static final TargetingConditions PARTNER_TARGETING = TargetingConditions.forNonCombat().range(8.0).ignoreLineOfSight();
    protected final BreedableSpider animal;
    private final Class<? extends BreedableSpider> partnerClass;
    protected final Level level;
    @Nullable
    protected BreedableSpider partner;
    private int loveTime;
    private final double speedModifier;

    public BreedGoal(BreedableSpider spider, double speed) {
        this(spider, speed, (Class<? extends BreedableSpider>)spider.getClass());
    }

    public BreedGoal(BreedableSpider spider, double speed, Class<? extends BreedableSpider> targets) {
        this.animal = spider;
        this.level = spider.level();
        this.partnerClass = targets;
        this.speedModifier = speed;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.animal.isInLove()) {
            return false;
        } else {
            this.partner = this.getFreePartner();
            return this.partner != null;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.partner.isAlive() && this.partner.isInLove() && this.loveTime < 60 && !this.partner.isPanicking();
    }

    @Override
    public void stop() {
        this.partner = null;
        this.loveTime = 0;
    }

    @Override
    public void tick() {
        this.animal.getLookControl().setLookAt(this.partner, 10.0F, (float)this.animal.getMaxHeadXRot());
        this.animal.getNavigation().moveTo(this.partner, this.speedModifier);
        this.loveTime++;
        if (this.loveTime >= this.adjustedTickDelay(60) && this.animal.distanceToSqr(this.partner) < 9.0) {
            this.breed();
        }
    }

    @Nullable
    private BreedableSpider getFreePartner() {
        List<? extends BreedableSpider> list = this.level
            .getNearbyEntities(this.partnerClass, PARTNER_TARGETING, this.animal, this.animal.getBoundingBox().inflate(8.0));
        double d0 = Double.MAX_VALUE;
        BreedableSpider animal = null;

        for (BreedableSpider animal1 : list) {
            if (this.animal.canMate(animal1) && !animal1.isPanicking() && this.animal.distanceToSqr(animal1) < d0) {
                animal = animal1;
                d0 = this.animal.distanceToSqr(animal1);
            }
        }

        return animal;
    }

    protected void breed() {
        this.animal.spawnChildFromBreeding((ServerLevel)this.level, this.partner);
    }
}
