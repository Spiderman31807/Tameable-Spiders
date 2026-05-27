package tameable.spiders.block;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;

import tameable.spiders.block.entity.SilkWebBlockEntity;

public class SilkWebBlock extends WebBlock implements EntityBlock {
	public SilkWebBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.WOOL).forceSolidOn().noCollission().requiresCorrectToolForDrops().strength(4.0F).pushReaction(PushReaction.DESTROY));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SilkWebBlockEntity(pos, state);
	}

	@Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
    	if(entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("tameable_spiders:web_immune"))))
			return;
        super.entityInside(state, world, pos, entity);
        
        SilkWebBlockEntity WebEntity = (SilkWebBlockEntity) world.getBlockEntity(pos);
		CompoundTag data = entity.getPersistentData();
        if(data.contains("SpeedData")) {
        	CompoundTag speedData = data.getCompound("SpeedData");
        	float speed = speedData.getFloat("speed");
        	speed = speed * (speed >= 0.1 ? 10 : 100);
        	WebEntity.damage(speed);
        }
    }

    @Override
	public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
		super.tick(blockstate, world, pos, random);
        ((SilkWebBlockEntity) world.getBlockEntity(pos)).tick();
	}
}