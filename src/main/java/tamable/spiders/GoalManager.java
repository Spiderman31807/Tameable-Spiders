package tamable.spiders;

import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;

import java.util.Set;
import java.util.HashSet;

public class GoalManager {
	public Set<WrappedGoal> goals = Set.of();

	public GoalManager(Set<WrappedGoal> goals) {
		this.goals = goals;
	}

	public void addGoal(int priority, Goal goal) {
		this.goals.add(new WrappedGoal(priority, goal));
	}

	public void insertGoal(int priority, boolean shouldShift, Goal goal) {
		if (shouldShift)
			this.lowerPriorityFrom(priority);
		this.goals.add(new WrappedGoal(priority, goal));
	}

	public void lowerPriorityFrom(int priotity) {
		Set<WrappedGoal> newGoals = new HashSet();
		for (WrappedGoal wrapped : this.goals) {
			newGoals.add(wrapped.getPriority() >= priotity ? new WrappedGoal(wrapped.getPriority() + 1, wrapped.getGoal()) : wrapped);
		}

		this.goals = newGoals;
	}

	public void replaceGoalsFor(GoalSelector selector) {
		selector.removeAllGoals((goal) -> true);
		for (WrappedGoal wrapped : this.goals) {
			selector.addGoal(wrapped.getPriority(), wrapped.getGoal());
		}
	}

	public static ServerLevel getServerLevel(Entity entity) {
		return (ServerLevel) entity.level();
	}
}