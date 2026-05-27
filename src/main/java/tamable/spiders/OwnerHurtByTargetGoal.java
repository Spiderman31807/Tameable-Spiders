package tamable.spiders.world.entity.ai.goal.target;

import tamable.spiders.TamableSpider;

import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.LivingEntity;

import java.util.EnumSet;

public class OwnerHurtByTargetGoal extends TargetGoal {
	private final TamableSpider tamable;
	private LivingEntity ownerLastHurtBy;
	private int timestamp;

	public OwnerHurtByTargetGoal(TamableSpider tamable) {
		super(tamable.spider(), false);
		this.tamable = tamable;
		this.setFlags(EnumSet.of(Goal.Flag.TARGET));
	}

	@Override
    public boolean canUse() {
        if (this.tamable.isTame() && !this.tamable.isOrderedToSit() && this.tamable.getOwner() instanceof LivingEntity owner) {
            this.ownerLastHurtBy = owner.getLastHurtByMob();
            int hurtStamp = owner.getLastHurtByMobTimestamp();
            return hurtStamp != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT) && this.tamable.wantsToAttack(this.ownerLastHurtBy, owner);
        }

		return false;
	}

	@Override
    public void start() {
        this.mob.setTarget(this.ownerLastHurtBy);
        if (this.tamable.getOwner() instanceof LivingEntity owner)
            this.timestamp = owner.getLastHurtByMobTimestamp();
        super.start();
    }
}