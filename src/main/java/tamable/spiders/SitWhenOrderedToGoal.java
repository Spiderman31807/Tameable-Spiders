package tamable.spiders.world.entity.ai.goal;

import tamable.spiders.TamableSpider;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.LivingEntity;

import java.util.EnumSet;
import net.minecraft.world.phys.Vec3;

public class SitWhenOrderedToGoal extends Goal {
	private final TamableSpider tamable;

	public SitWhenOrderedToGoal(TamableSpider tamable) {
		this.tamable = tamable;
		this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
	}

	@Override
	public boolean canContinueToUse() {
		if(!this.tamable.isOrderedToSit())
			return false;
		if (this.tamable.getDefendTarget() != null)
			return false;
		if (this.tamable.spider().isVehicle())
			return false;
		return true;
	}

	@Override
	public boolean canUse() {
		if (!this.tamable.isTame()) 
			return false;
		if (this.tamable.getDefendTarget() != null)
			return false;
		if (this.tamable.spider().isInWaterOrBubble()) 
			return false;
		if (!this.tamable.spider().onGround())
			return false;
		if (this.tamable.spider().isVehicle())
			return false;
		LivingEntity owner = this.tamable.getOwner();
		if (owner == null)
			return true;
		return this.tamable.spider().distanceToSqr(owner) < 144.0 && owner.getLastHurtByMob() != null ? false : this.tamable.isOrderedToSit();
	}

	@Override
	public void start() {
		this.tamable.spider().getNavigation().moveTo(this.tamable.spider(), 0);
		this.tamable.setInSittingPose(true);
	}

	@Override
	public void stop() {
		this.tamable.setInSittingPose(false);
	}
}
