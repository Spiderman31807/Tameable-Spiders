package tamable.spiders.world.level.block;

import tamable.spiders.TamableSpider;

import net.neoforged.neoforge.common.ItemAbilities;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import java.util.Stack;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;
import com.mojang.datafixers.util.Pair;

public class WebPathBlock extends MultifaceBlock {
	public WebPathBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@Override
	protected boolean propagatesSkylightDown(BlockState state) {
		return state.getFluidState().isEmpty();
	}

	@Override
	protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		boolean apply = !(entity instanceof Spider);
		Vec3 multiplier = new Vec3(0.95, 1, 0.95);
		if (entity instanceof LivingEntity living && living.hasEffect(MobEffects.WEAVING))
			apply = false;
		if (!apply)
			return;

		entity.makeStuckInBlock(state, multiplier);
		if (entity instanceof LivingEntity living && !living.dampensVibrations())
			aggroNearbySpiders(world, pos, living);
	}

	@Override
	public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
		if (!player.getWeaponItem().canPerformAction(ItemAbilities.SHEARS_DISARM))
			aggroNearbySpiders(world, pos, player);
		return super.playerWillDestroy(world, pos, state, player);
	}

	public static boolean isInsideConnectedWebPaths(Set<BlockPos> web, Entity entity) {
		return web.contains(entity.blockPosition());
	}

	public static List<LivingEntity> getEntitiesAlongWebPath(Level world, Set<BlockPos> web) {
		ArrayList<LivingEntity> withinPath = new ArrayList();
		for (BlockPos path : web) {
			for (Entity entity : world.getEntities(null, AABB.ofSize(path.getBottomCenter(), 1, 0.1, 1))) {
				if (entity instanceof LivingEntity living)
					withinPath.add(living);
			}
		}
		return withinPath;
	}

	public static Set<BlockPos> getConnectedWebPaths(Level world, BlockPos start) {
		Set<BlockPos> wholePath = new HashSet();
		Set<Pair<BlockPos, Direction>> visited = new HashSet<>();
		Stack<Pair<BlockPos, Direction>> stack = new Stack<>();
		for(Direction direction : Direction.values()) {
			stack.push(Pair.of(start, direction));
		}
		
		while (!stack.isEmpty()) {
			Pair<BlockPos, Direction> currentInfo = stack.pop();
			BlockPos currentPos = currentInfo.getFirst();
			Direction currentSource = currentInfo.getSecond();
			if (!visited.add(currentInfo))
				continue;

			wholePath.add(currentPos);
			BlockState state = world.getBlockState(currentPos);
			if (!(state.getBlock() instanceof WebPathBlock))
				continue;
			if(!MultifaceBlock.hasFace(state, currentSource))
				continue;

			for(Direction direction : Direction.values()) {
				if(!MultifaceBlock.hasFace(state, direction))
					continue;
				if(currentSource == direction)
					continue;
				if(currentSource.getOpposite() == direction)
					continue;
				if(visited.contains(Pair.of(currentPos, direction)))
					continue;
				stack.push(Pair.of(currentPos, direction));
			}
				
			if(currentSource == Direction.UP || currentSource == Direction.DOWN) {
				addWebsIfPresent(world, currentSource, currentPos.offset(0, 0, 1), Direction.NORTH, visited, stack);
				addWebsIfPresent(world, currentSource, currentPos.offset(0, 0, -1), Direction.SOUTH, visited, stack);
				addWebsIfPresent(world, currentSource, currentPos.offset(1, 0, 0), Direction.WEST, visited, stack);
				addWebsIfPresent(world, currentSource, currentPos.offset(-1, 0, 0), Direction.EAST, visited, stack);
			} else if(currentSource == Direction.NORTH || currentSource == Direction.SOUTH) {
				addWebsIfPresent(world, currentSource, currentPos.offset(0, 1, 0), Direction.DOWN, visited, stack);
				addWebsIfPresent(world, currentSource, currentPos.offset(0, -1, 0), Direction.UP, visited, stack);
				addWebsIfPresent(world, currentSource, currentPos.offset(1, 0, 0), Direction.WEST, visited, stack);
				addWebsIfPresent(world, currentSource, currentPos.offset(-1, 0, 0), Direction.EAST, visited, stack);
			} else if(currentSource == Direction.EAST || currentSource == Direction.WEST) {
				addWebsIfPresent(world, currentSource, currentPos.offset(0, 1, 0), Direction.DOWN, visited, stack);
				addWebsIfPresent(world, currentSource, currentPos.offset(0, -1, 0), Direction.UP, visited, stack);
				addWebsIfPresent(world, currentSource, currentPos.offset(0, 0, 1), Direction.NORTH, visited, stack);
				addWebsIfPresent(world, currentSource, currentPos.offset(0, 0, -1), Direction.SOUTH, visited, stack);
			}
		}

		return wholePath;
	}

	public static void addWebsIfPresent(Level world, Direction direction, BlockPos targetPos, Direction targetFace, Set<Pair<BlockPos, Direction>> visited, Stack stack) {
		if(!visited.contains(Pair.of(targetPos.relative(direction), targetFace)))
			addWrappedWebIfPresent(world, targetPos.relative(direction), targetPos, targetFace, direction, stack);
		if(!visited.contains(Pair.of(targetPos, direction)))
			addWebIfPresent(world, targetPos, direction, stack);
	}

	public static void addWebIfPresent(Level world, BlockPos pos, Direction face, Stack stack) {
		BlockState state = world.getBlockState(pos);
		if(state.getBlock() instanceof WebPathBlock && MultifaceBlock.hasFace(state, face))
			stack.push(Pair.of(pos, face));
	}

	public static void addWrappedWebIfPresent(Level world, BlockPos targetPos, BlockPos betweenPos, Direction targetFace, Direction betweenFace, Stack stack) {
		BlockState betweenState = world.getBlockState(betweenPos);
		if(betweenState.isFaceSturdy(world, betweenPos, targetFace) || betweenState.isFaceSturdy(world, betweenPos, betweenFace))
			return;
			
		BlockState state = world.getBlockState(targetPos);
		if(state.getBlock() instanceof WebPathBlock && MultifaceBlock.hasFace(state, targetFace))
			stack.push(Pair.of(targetPos, targetFace));
	}

	public static void aggroNearbySpiders(Level world, BlockPos origin, LivingEntity target) {
		if (world.isClientSide)
			return;
			
		Set<BlockPos> connectedPaths = getConnectedWebPaths(world, origin);
		for (Entity entity : getEntitiesAlongWebPath(world, connectedPaths)) {
			if (entity instanceof TamableSpider spider)
				spider.defendWebFrom(target);
		}
	}
}
