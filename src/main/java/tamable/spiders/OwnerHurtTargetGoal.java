package tamable.spiders.world.entity.ai.goal.target;

import tamable.spiders.TamableSpider;

import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.LivingEntity;

import java.util.EnumSet;

public class OwnerHurtTargetGoal extends TargetGoal {
	private final TamableSpider tamable;
	private LivingEntity ownerLastHurt;
	private int timestamp;

	public OwnerHurtTargetGoal(TamableSpider tamable) {
		super(tamable.spider(), false);
		this.tamable = tamable;
		this.setFlags(EnumSet.of(Goal.Flag.TARGET));
	}

	@Override
	public boolean canUse() {
		if (this.tamable.isTame() && !this.tamable.isOrderedToSit() && this.tamable.getOwner() instanceof LivingEntity owner) {
			this.ownerLastHurt = owner.getLastHurtMob();
			int hurtStamp = owner.getLastHurtMobTimestamp();
			return hurtStamp != this.timestamp && this.canAttack(this.ownerLastHurt, TargetingConditions.DEFAULT) && this.tamable.wantsToAttack(this.ownerLastHurt, owner);
		}
		return false;
	}

	@Override
	public void start() {
		this.mob.setTarget(this.ownerLastHurt);
		if (this.tamable.getOwner() instanceof LivingEntity owner)
			this.timestamp = owner.getLastHurtMobTimestamp();
		super.start();
	}
}