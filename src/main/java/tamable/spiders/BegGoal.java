package tamable.spiders.world.entity.ai.goal;

import tamable.spiders.TamableSpider;
import tamable.spiders.GoalManager;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.InteractionHand;
import net.minecraft.server.level.ServerLevel;

import java.util.EnumSet;

public class BegGoal extends Goal {
	private final TargetingConditions targeting;
	private final TamableSpider spider;
	private final float lookDistance;
	private final ServerLevel level;
	private Player player;
	private int lookTime;

	public BegGoal(TamableSpider spider, float distance) {
		this.targeting = TargetingConditions.forNonCombat().range((double) distance);
		this.level = GoalManager.getServerLevel(spider.spider());
		this.setFlags(EnumSet.of(Goal.Flag.LOOK));
		this.lookDistance = distance;
		this.spider = spider;
	}

	@Override
	public boolean canUse() {
		if (this.spider.spider().getTarget() != null)
			return false;
		if (this.spider.isTame() && this.spider.spider().getHealth() == this.spider.spider().getMaxHealth())
			return false;
		this.player = this.level.getNearestPlayer(this.targeting, this.spider.spider());
		return this.player == null ? false : this.playerHoldingInteresting(this.player);
	}

	@Override
	public boolean canContinueToUse() {
		if (!this.player.isAlive())
			return false;
		if (!this.canUse())
			return false;
		return this.spider.spider().distanceToSqr(this.player) > (double) (this.lookDistance * this.lookDistance) ? false : this.lookTime > 0 && this.playerHoldingInteresting(this.player);
	}

	@Override
	public void start() {
		this.spider.setIsInterested(true);
		this.lookTime = this.adjustedTickDelay(40 + this.spider.spider().getRandom().nextInt(40));
	}

	@Override
	public void stop() {
		this.spider.setIsInterested(false);
		this.player = null;
	}

	@Override
	public void tick() {
		this.spider.spider().getLookControl().setLookAt(this.player.getX(), this.player.getEyeY(), this.player.getZ(), 10, (float) this.spider.spider().getMaxHeadXRot());
		this.lookTime--;
	}

	private boolean playerHoldingInteresting(Player player) {
		for (InteractionHand hand : InteractionHand.values()) {
			ItemStack stack = player.getItemInHand(hand);
			if (this.spider.isFood(stack))
				return true;
		}
		return false;
	}
}