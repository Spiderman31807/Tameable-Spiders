package tameable.spiders.block;

import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import tameable.spiders.TameableSpidersMod;
import tameable.spiders.entity.ModdedSpider;
import tameable.spiders.init.TameableSpidersModBlocks;
import tameable.spiders.init.TameableSpidersModBlockEntities;
import tameable.spiders.network.TameableSpidersModVariables;
import tameable.spiders.block.entity.BedNorthBlockEntity;
import tameable.spiders.block.BedSouthBlock;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

public class BedNorthBlock extends BedSouthBlock implements EntityBlock {
	public BedNorthBlockEntity bedEntity;
	public Optional<UUID> OccupiedUUID;

	public BedNorthBlock() {
		super();
		OccupiedUUID = Optional.empty();
	}

	@Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean bool) {
        super.onPlace(state, world, pos, oldState, bool);
		world.scheduleTick(pos, state.getBlock(), 20);
    }

    @Override
	public void tick(BlockState state, ServerLevel server, BlockPos pos, RandomSource random) {
		super.tick(state, server, pos, random);
		bedEntity = (BedNorthBlockEntity) server.getBlockEntity(pos);
        bedEntity.tick(state, server, pos);
	}

	public void updateOccupied(Optional<UUID> uuid) {
		OccupiedUUID = uuid;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		bedEntity = new BedNorthBlockEntity(pos, state);
		return bedEntity;
	}

	@Override
	public void destroy(LevelAccessor accessor, BlockPos pos, BlockState state) {
		OccupiedUUID.ifPresent(UUID -> {
			if(accessor instanceof ServerLevel server)
			{
				ModdedSpider spider = (ModdedSpider) server.getEntity(UUID);
				if(spider != null)
    				spider.resetHome();
			}
		});

		super.destroy(accessor, pos, state);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(FACING)) {
			default -> box(0, 0, 0, 16, 8, 15);
			case NORTH -> box(0, 0, 1, 16, 8, 16);
			case EAST -> box(0, 0, 0, 15, 8, 16);
			case WEST -> box(1, 0, 0, 16, 8, 16);
		};
	}
	
	@Override
	public boolean isSouth() {
		return false;
	}

	@Override
	public void updateAttached(Boolean occupied, Level world, BlockPos pos)
	{
		if(world == null)
			return;
		BlockState state = world.getBlockState(pos);
		if (state.getBlock().getStateDefinition().getProperty("attached") instanceof BooleanProperty property)
		{
			if(state.getValue(property) != occupied)
			{
				BedNorthBlockEntity entity = (BedNorthBlockEntity) world.getBlockEntity(pos);
				Entity stored = entity.getOccupied();
			
				world.setBlock(pos, state.setValue(property, occupied), 3);
				entity = (BedNorthBlockEntity) world.getBlockEntity(pos);
				if(stored != null)
					entity.setOccupied(stored, false);
			}

			BlockState SouthState = getOtherHalf(state, world, pos);
			if(SouthState.getBlock() instanceof BedSouthBlock SouthBlock)
				SouthBlock.updateAttached(occupied, world, getOtherPos(pos, state));
		}

		world.updateNeighborsAt(pos, world.getBlockState(pos).getBlock());
	}

	@Override
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
}
