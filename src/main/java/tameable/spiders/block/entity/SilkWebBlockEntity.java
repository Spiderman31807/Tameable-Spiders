package tameable.spiders.block.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.BlockPos;

import tameable.spiders.init.TameableSpidersModBlockEntities;
import tameable.spiders.block.entity.ContainerBlock;

public class SilkWebBlockEntity extends ContainerBlock {
	private static float maxDurability = 200;
	private static float repairRate = 2;
	private float durability;
	private int tickCount;
	private int damagedStamp;

	public SilkWebBlockEntity(BlockPos position, BlockState state) {
		super(TameableSpidersModBlockEntities.SILK_WEB.get(), position, state);
		this.durability = maxDurability;
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
        this.durability = compound.getFloat("Durability");
        this.damagedStamp = compound.getInt("DamagedStamp");
        this.tickCount = compound.getInt("Tick");

        if(this.durability != maxDurability)
    		this.tick();
	}

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putFloat("Durability", this.durability);
        compound.putInt("DamagedStamp", this.damagedStamp);
        compound.putInt("Tick", this.tickCount);
    }

    public void damage(float amount) {
    	if(amount == 0)
    		return;
    	this.durability -= amount;
    	this.damagedStamp = this.tickCount;

    	if(durability <= 0) {
    		LevelAccessor accessor = (LevelAccessor)this.getLevel();
    		accessor.destroyBlock(this.getBlockPos(), false);
    	}

    	this.tick();
    }

    public void tick() {
        this.tickCount++;
        if(this.damagedStamp + 10 < this.tickCount && this.durability < maxDurability)
    		this.durability = Math.min(maxDurability, this.durability + repairRate);

        if(this.durability != maxDurability) {
    		BlockPos pos = this.getBlockPos();
    		Level world = this.getLevel();
    		Block block = world.getBlockState(pos).getBlock();
			world.scheduleTick(pos, block, 1);
        }
    }
}