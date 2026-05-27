
package tameable.spiders.item;

import javax.annotation.Nullable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;

import tameable.spiders.init.TameableSpidersModBlocks;

public class BedItem extends BlockItem {
	public BedItem() {
		super(TameableSpidersModBlocks.BED_NORTH.get(), new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        Level level = context.getLevel();
        BlockPos blockpos = this.getOtherPos(context.getClickedPos(), state);
        BlockState blockstate = level.getBlockState(blockpos);
		if(!blockstate.is(BlockTags.REPLACEABLE))
			return false;

        if(level.setBlock(context.getClickedPos(), state, 26)) {
        	BlockState deafult = level.isWaterAt(blockpos) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
        	level.setBlock(blockpos, deafult, 27);
        	level.setBlock(blockpos, this.getSouthPlacementState(context), 26);
        	return true;
        }
        
        return false;
    }

	@Override
    protected boolean canPlace(BlockPlaceContext context, BlockState state) {
        Player player = context.getPlayer();
        CollisionContext collisioncontext = player == null ? CollisionContext.empty() : CollisionContext.of(player);
        BlockState blockstate = this.getSouthPlacementState(context);
        BlockPos blockpos = this.getOtherPos(context.getClickedPos(), state);
        
        boolean North = (!this.mustSurvive() || state.canSurvive(context.getLevel(), context.getClickedPos())) && context.getLevel().isUnobstructed(state, context.getClickedPos(), collisioncontext);
        boolean South = (!this.mustSurvive() || blockstate.canSurvive(context.getLevel(), blockpos)) && context.getLevel().isUnobstructed(blockstate, blockpos, collisioncontext);
        return North && South;
    }

    @Nullable
    protected BlockState getSouthPlacementState(BlockPlaceContext context) {
        BlockState blockstate = TameableSpidersModBlocks.BED_SOUTH.get().getStateForPlacement(context);
        return blockstate;
    }

    public BlockPos getOtherPos(BlockPos pos, BlockState state)
	{
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		
		Direction dir = getDirection(state);
		if (dir == Direction.NORTH) {
			z++;
		} else if (dir == Direction.SOUTH) {
			z--;
		} else if (dir == Direction.WEST) {
			x++;
		} else {
			x--;
		}

		return BlockPos.containing(x, y, z);
	}

	public Direction getDirection(BlockState state) {
		Property<?> facing = state.getBlock().getStateDefinition().getProperty("facing");
		if (facing instanceof DirectionProperty dir)
			return state.getValue(dir);
		facing = state.getBlock().getStateDefinition().getProperty("axis");
		return facing instanceof EnumProperty _ep && _ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) state.getValue(_ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
	}
}
