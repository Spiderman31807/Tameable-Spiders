package tamable.spiders.world.entity.ai.goal;

import tamable.spiders.TamableSpider;

import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.LivingEntity;

import java.util.EnumSet;

public class FollowOwnerGoal extends Goal {
	private final Spider spider;
	private final TamableSpider tamable;
	private final double speedModifier;
	private final PathNavigation navigation;
	private final float stopDistance;
	private final float startDistance;
	private int timeToRecalcPath;
	private float oldWaterCost;
	private LivingEntity owner;

	public FollowOwnerGoal(TamableSpider spider, double speed, float start, float stop) {
		this.tamable = spider;
		this.spider = this.tamable.spider();
		this.speedModifier = speed;
		this.navigation = this.spider.getNavigation();
		this.startDistance = start;
		this.stopDistance = stop;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
		if (!(this.navigation instanceof GroundPathNavigation) && !(this.navigation instanceof FlyingPathNavigation))
			throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
	}

	@Override
	public boolean canUse() {
		LivingEntity owner = this.tamable.getOwner();
		if (owner == null)
			return false;
		if (this.tamable.unableToMoveToOwner())
			return false;
		if (this.spider.distanceToSqr(owner) < (double) (this.startDistance * this.startDistance))
			return false;
		this.owner = owner;
		return true;
	}

	@Override
	public boolean canContinueToUse() {
		if (this.navigation.isDone())
			return false;
		return this.tamable.unableToMoveToOwner() ? false : !(this.spider.distanceToSqr(this.owner) <= (double) (this.stopDistance * this.stopDistance));
	}

	@Override
	public void start() {
		this.timeToRecalcPath = 0;
		this.oldWaterCost = this.spider.getPathfindingMalus(PathType.WATER);
		this.spider.setPathfindingMalus(PathType.WATER, 0);
	}

	@Override
	public void stop() {
		this.owner = null;
		this.navigation.stop();
		this.spider.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
	}

	@Override
	public void tick() {
		boolean tryTeleporting = this.tamable.shouldTryTeleportToOwner();
		if (!tryTeleporting)
			this.spider.getLookControl().setLookAt(this.owner, 10.0F, (float) this.spider.getMaxHeadXRot());
		if (--this.timeToRecalcPath <= 0) {
			this.timeToRecalcPath = this.adjustedTickDelay(10);
			if (tryTeleporting) {
				this.tamable.tryToTeleportToOwner();
			} else {
				this.navigation.moveTo(this.owner, this.speedModifier);
			}
		}
	}
}