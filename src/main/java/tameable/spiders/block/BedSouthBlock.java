package tameable.spiders.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import tameable.spiders.TameableSpidersMod;
import tameable.spiders.init.TameableSpidersModBlocks;
import tameable.spiders.init.TameableSpidersModItems;
import tameable.spiders.network.TameableSpidersModVariables;
import tameable.spiders.block.entity.BedNorthBlockEntity;
import tameable.spiders.block.BedNorthBlock;

public class BedSouthBlock extends Block implements SimpleWaterloggedBlock {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
	
	public BedSouthBlock() {
		super(BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASEDRUM).mapColor(MapColor.DEEPSLATE).sound(SoundType.DEEPSLATE).strength(3.5f, 6f).requiresCorrectToolForDrops().noOcclusion().pushReaction(PushReaction.DESTROY).isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false).setValue(ATTACHED, Boolean.valueOf(false)));
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return state.getFluidState().isEmpty();
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 0;
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(FACING)) {
			default -> box(0, 0, 1, 16, 8, 16);
			case NORTH -> box(0, 0, 0, 16, 8, 15);
			case EAST -> box(1, 0, 0, 16, 8, 16);
			case WEST -> box(0, 0, 0, 15, 8, 16);
		};
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED, ATTACHED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, flag).setValue(ATTACHED, Boolean.valueOf(false));
	}

	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED)) {
			world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}
		return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
		return new ItemStack(TameableSpidersModItems.BED.get());
	}

	public boolean isSouth() {
		return true;
	}

	@Override
	public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
		super.neighborChanged(blockstate, world, pos, neighborBlock, fromPos, moving);
		Block otherHalf = getBedHalf(blockstate, world, pos);
		
		if(otherHalf instanceof BedNorthBlock && this.isSouth())
			return;
		if(otherHalf instanceof BedSouthBlock && !this.isSouth())
			return;

		((LevelAccessor)world).destroyBlock(pos, false);
	}

	public String getTime(int Time) {
		int DisplayType = 0;
		
		if(Time < 60) {
			DisplayType = 0;
		} else if(Time > 3600) {
			DisplayType = 2;
		} else {
			DisplayType = 1;
		}
		
		int DisplayTime = switch(DisplayType) {
			default -> Time;
			case 1 -> Time / 60;
			case 2 -> Time / 3600;
		};
		String displayType = switch(DisplayType) {
			default -> "second";
			case 1 -> "minute";
			case 2 -> "hour";
		};

		if(DisplayTime > 1)
			displayType += "s";
		String display = Component.translatable(displayType).getString();
		return Component.literal(DisplayTime + " " + display).getString(); 
	}

	public Component getOccupiedMessage(BedNorthBlockEntity entity, boolean assigning) {
		if(entity == null || assigning)
			return Component.literal("");
			
		Entity OccupiedBy = entity.getOccupied();
		boolean Respawning = entity.respawning();
		int RespawnTimer = entity.respawnTime();

		if(!Respawning) {
			if (OccupiedBy != null)
				return Component.literal(Component.translatable("tameable_spiders.bed_claimed").getString() + ": " + OccupiedBy.getDisplayName().getString());
			return Component.translatable("tameable_spiders.bed_vacant");
		}

		return Component.literal(Component.translatable("tameable_spiders.bed_respawning").getString() + ": " + this.getTime(RespawnTimer));
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player entity, BlockHitResult hit)
	{
		TameableSpidersModVariables.PlayerVariables _vars = entity.getData(TameableSpidersModVariables.PLAYER_VARIABLES);
		BlockState stateNorth = isSouth() ? getOtherHalf(state, world, pos) : state;
		BedNorthBlockEntity bedEntity = this.getBedEntity(state, world, pos);
		Entity OccupiedBy = bedEntity.getOccupied();
		boolean Respawning = bedEntity.respawning();
		int RespawnTimer = bedEntity.respawnTime();

		Component Message = this.getOccupiedMessage(bedEntity, _vars.assigning);
		if (!entity.level().isClientSide())
			entity.displayClientMessage(Message, true);
			
		if(entity.isShiftKeyDown()) {
			_vars.assignBed = stateNorth;
			_vars.assigning = true;

			BlockPos AssignPos = bedEntity.getBlockPos();
			_vars.assignX = AssignPos.getX();
			_vars.assignY = AssignPos.getY();
			_vars.assignZ = AssignPos.getZ();
			
			_vars.syncPlayerVariables(entity);
			return InteractionResult.PASS;
		} else if(canInteract(entity, state) == false) {
			_vars.assigning = false;
			_vars.syncPlayerVariables(entity);		
			return InteractionResult.SUCCESS_NO_ITEM_USED;
		}

		return super.useWithoutItem(state, world, pos, entity, hit);
	}

	public boolean canInteract(Player player, BlockState state)
	{
		ItemStack mainHand = player.getMainHandItem();
		ItemStack offHand = player.getOffhandItem();
		
		return canInteract(mainHand, state) || canInteract(offHand, state);
	}

	public boolean canInteract(ItemStack itemstack, BlockState state)
	{
		Boolean Waterlogged = state.getValue(WATERLOGGED);
		Item item = itemstack.getItem();


		if(item == Items.BUCKET && Waterlogged == true)
			return true;
		if(item == Items.WATER_BUCKET && Waterlogged == false)
			return true;

		return false;
	}

	public void updateAttached(Boolean occupied, Level world, BlockPos pos)
	{
		BlockState state = world.getBlockState(pos);
		if (state.getBlock().getStateDefinition().getProperty("attached") instanceof BooleanProperty property)
			if(state.getValue(property) != occupied)
				world.setBlock(pos, state.setValue(property, occupied), 3);
		
		world.updateNeighborsAt(pos, world.getBlockState(pos).getBlock());
	}

	public BedNorthBlockEntity getBedEntity(BlockState state, Level world, BlockPos pos)
	{
		if(isSouth())
			return (BedNorthBlockEntity) world.getBlockEntity(getOtherPos(pos, state));
		return (BedNorthBlockEntity) world.getBlockEntity(pos);
	}

	public BlockPos getOtherPos(BlockPos pos, BlockState state)
	{
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		
		Direction dir = getDirection(state);
		if (dir == Direction.NORTH) {
			z--;
		} else if (dir == Direction.SOUTH) {
			z++;
		} else if (dir == Direction.WEST) {
			x--;
		} else {
			x++;
		}

		return BlockPos.containing(x, y, z);
	}

	public BedSouthBlock getBedHalf(BlockState state, Level world, BlockPos pos)
	{
		Block OtherHalf = getOtherHalf(state, world, pos).getBlock();
		if(OtherHalf instanceof BedSouthBlock Bed)
			return Bed;
		return null;
	}

	public BlockState getOtherHalf(BlockState state, Level world, BlockPos pos) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		Direction dir = getDirection(state);
		return world.getBlockState(getOtherPos(pos, state));
	}

	public Direction getDirection(BlockState state) {
		Property<?> facing = state.getBlock().getStateDefinition().getProperty("facing");
		if (facing instanceof DirectionProperty dir)
			return state.getValue(dir);
		facing = state.getBlock().getStateDefinition().getProperty("axis");
		return facing instanceof EnumProperty _ep && _ep.getPossibleValues().toArray()[0] instanceof Direction.Axis ? Direction.fromAxisAndDirection((Direction.Axis) state.getValue(_ep), Direction.AxisDirection.POSITIVE) : Direction.NORTH;
	}
}
