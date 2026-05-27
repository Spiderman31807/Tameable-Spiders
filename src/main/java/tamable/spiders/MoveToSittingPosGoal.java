package tamable.spiders.world.entity.ai.goal;

import tamable.spiders.world.level.block.WebPathBlock;
import tamable.spiders.TamableSpider;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.core.BlockPos;

import java.util.Optional;
import java.util.EnumSet;

public class MoveToSittingPosGoal extends Goal {
	private final PathNavigation navigation;
	private final TamableSpider tamable;
	private final double speedModifier;
	private final Spider spider;
	private int timeToRecalcPath;
	private float oldWaterCost;

	public MoveToSittingPosGoal(TamableSpider tamable, float speed) {
		this.tamable = tamable;
		this.spider = this.tamable.spider();
		this.setFlags(EnumSet.of(Goal.Flag.MOVE));
		this.navigation = this.spider.getNavigation();
		this.speedModifier = speed;
	}

	@Override
	public boolean canContinueToUse() {
		if (this.tamable.getDefendTarget() != null)
			return false;
		return super.canContinueToUse();
	}

	@Override
	public boolean canUse() {
		if (!this.tamable.isTame())
			return false;
		if (this.tamable.getLastSittingPos().isEmpty())
			return false;
		if (this.tamable.isInsideWeb() && this.spider.distanceToSqr(this.tamable.getLastSittingPos().get()) < 1)
			return false;
		if (this.tamable.getDefendTarget() != null)
			return false;
		LivingEntity owner = this.tamable.getOwner();
		if (owner == null)
			return true;
		return this.spider.distanceToSqr(owner) < 144.0 && owner.getLastHurtByMob() != null ? false : this.tamable.isOrderedToSit();
	}

	@Override
	public void start() {
		this.timeToRecalcPath = 0;
		this.oldWaterCost = this.spider.getPathfindingMalus(PathType.WATER);
		this.spider.setPathfindingMalus(PathType.WATER, 0);
	}

	@Override
	public void stop() {
		this.navigation.stop();
		if (this.tamable.getDefendTarget() == null)
			this.tamable.setLastSittingPos(Optional.empty());
		this.spider.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
	}

	@Override
	public void tick() {
		if (--this.timeToRecalcPath <= 0) {
			this.timeToRecalcPath = this.adjustedTickDelay(10);
			this.tamable.getLastSittingPos().ifPresent((pos) -> {
				this.navigation.moveTo(pos.x(), pos.y() + 0.5f, pos.z(), this.speedModifier);
			});
		}
	}
}
