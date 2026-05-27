package tamable.spiders.world.entity.ai.goal;

import tamable.spiders.world.level.block.WebPathBlock;
import tamable.spiders.TamableSpider;
import tamable.spiders.GoalManager;

import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionHand;

import java.util.Optional;
import java.util.EnumSet;

public class AttackTargetInWebGoal extends Goal {
	protected final TamableSpider tamable;
	protected final Spider spider;
	private final double speedModifier;
	private final boolean followingTargetEvenIfNotSeen;
	private Path path;
	private double pathedTargetX;
	private double pathedTargetY;
	private double pathedTargetZ;
	private int ticksUntilNextPathRecalculation;
	private int ticksUntilNextAttack;
	private final int attackInterval = 20;
	private long lastCanUseCheck;
	private static final long COOLDOWN_BETWEEN_CAN_USE_CHECKS = 20L;
	private int failedPathFindingPenalty = 0;
	private boolean canPenalize = false;

	public AttackTargetInWebGoal(TamableSpider tamable, double speed, boolean wallhacks) {
		this.tamable = tamable;
		this.spider = this.tamable.spider();
		this.speedModifier = speed;
		this.followingTargetEvenIfNotSeen = wallhacks;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		long i = this.spider.level().getGameTime();
		if (i - this.lastCanUseCheck < 20L) {
			return false;
		} else {
			this.lastCanUseCheck = i;
			LivingEntity target = this.tamable.getDefendTarget();
			if (target == null) {
				return false;
			} else if (!target.isAlive()) {
				return false;
			} else {
				if (canPenalize) {
					if (--this.ticksUntilNextPathRecalculation <= 0) {
						this.path = this.spider.getNavigation().createPath(target, 0);
						this.ticksUntilNextPathRecalculation = 4 + this.spider.getRandom().nextInt(7);
						return this.path != null;
					} else {
						return true;
					}
				}
				this.path = this.spider.getNavigation().createPath(target, 0);
				return this.path != null ? true : this.spider.isWithinMeleeAttackRange(target);
			}
		}
	}

	@Override
	public boolean canContinueToUse() {
		LivingEntity target = this.tamable.getDefendTarget();
		if (target == null)
			return false;

		if (!this.tamable.isTame() && !(target.getBlockStateOn().getBlock() instanceof WebPathBlock)) {
			float light = this.spider.getLightLevelDependentMagicValue();
			if (light >= 0.5f && this.spider.getRandom().nextInt(100) == 0) {
				this.tamable.setDefendTarget(null);
				return false;
			}
		}

		if (!target.isAlive())
			return false;
		if (!this.followingTargetEvenIfNotSeen)
			return !this.spider.getNavigation().isDone();
		return !this.spider.isWithinRestriction(target.blockPosition()) ? false : !(target instanceof Player) || !target.isSpectator() && !((Player) target).isCreative();
	}

	@Override
	public void start() {
		if (this.tamable.getLastSittingPos().isEmpty())
			this.tamable.setLastSittingPos(Optional.of(this.spider.position()));
		this.spider.getNavigation().moveTo(this.path, this.speedModifier);
		this.spider.setAggressive(true);
		this.ticksUntilNextPathRecalculation = 0;
		this.ticksUntilNextAttack = 0;
	}

	@Override
	public void stop() {
		LivingEntity target = this.tamable.getDefendTarget();
		this.tamable.setDefendTarget(null);
		this.spider.setAggressive(false);
		this.spider.getNavigation().stop();
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		LivingEntity target = this.tamable.getDefendTarget();
		if (target != null) {
			this.spider.getLookControl().setLookAt(target, 30.0F, 30.0F);
			this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
			if ((this.followingTargetEvenIfNotSeen || this.spider.getSensing().hasLineOfSight(target)) && this.ticksUntilNextPathRecalculation <= 0
					&& (this.pathedTargetX == 0.0 && this.pathedTargetY == 0.0 && this.pathedTargetZ == 0.0 || target.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0 || this.spider.getRandom().nextFloat() < 0.05F)) {
				this.pathedTargetX = target.getX();
				this.pathedTargetY = target.getY();
				this.pathedTargetZ = target.getZ();
				this.ticksUntilNextPathRecalculation = 4 + this.spider.getRandom().nextInt(7);
				double d0 = this.spider.distanceToSqr(target);
				if (this.canPenalize) {
					this.ticksUntilNextPathRecalculation += failedPathFindingPenalty;
					if (this.spider.getNavigation().getPath() != null) {
						net.minecraft.world.level.pathfinder.Node finalPathPoint = this.spider.getNavigation().getPath().getEndNode();
						if (finalPathPoint != null && target.distanceToSqr(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1)
							failedPathFindingPenalty = 0;
						else
							failedPathFindingPenalty += 10;
					} else {
						failedPathFindingPenalty += 10;
					}
				}
				if (d0 > 1024.0) {
					this.ticksUntilNextPathRecalculation += 10;
				} else if (d0 > 256.0) {
					this.ticksUntilNextPathRecalculation += 5;
				}
				if (!this.spider.getNavigation().moveTo(target, this.speedModifier)) {
					this.ticksUntilNextPathRecalculation += 15;
				}
				this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(this.ticksUntilNextPathRecalculation);
			}
			this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
			this.checkAndPerformAttack(target);
		}
	}

	protected void checkAndPerformAttack(LivingEntity target) {
		if (this.canPerformAttack(target)) {
			this.resetAttackCooldown();
			this.spider.swing(InteractionHand.MAIN_HAND);
			this.spider.doHurtTarget(target);
		}
	}

	protected void resetAttackCooldown() {
		this.ticksUntilNextAttack = this.adjustedTickDelay(20);
	}

	protected boolean isTimeToAttack() {
		return this.ticksUntilNextAttack <= 0;
	}

	protected boolean canPerformAttack(LivingEntity target) {
		return this.isTimeToAttack() && this.spider.isWithinMeleeAttackRange(target) && this.spider.getSensing().hasLineOfSight(target);
	}

	protected int getTicksUntilNextAttack() {
		return this.ticksUntilNextAttack;
	}

	protected int getAttackInterval() {
		return this.adjustedTickDelay(20);
	}
}