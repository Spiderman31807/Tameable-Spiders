package tameable.spiders.block.entity;

import java.util.Optional;
import java.util.UUID;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import tameable.spiders.init.SpiderRules;
import tameable.spiders.init.TameableSpidersModBlockEntities;
import tameable.spiders.block.BedNorthBlock;
import tameable.spiders.entity.ModdedSpider;
import tameable.spiders.entity.TamableSpider;
import tameable.spiders.enums.SpiderVariant;

public class BedNorthBlockEntity extends BlockEntity {
	private Optional<UUID> OccupiedUUID;
	private SpiderVariant RespawnType;
	private CompoundTag RespawnData;
	private boolean IsRespawning;
	private int RespawnTimer;

	public BedNorthBlockEntity(BlockPos position, BlockState state) {
		super(TameableSpidersModBlockEntities.BED_NORTH.get(), position, state);
		OccupiedUUID = Optional.empty();
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		
        if(compound.contains("Respawning"))
        	IsRespawning = compound.getBoolean("Respawning");
        if(compound.contains("RespawnTimer"))
        	RespawnTimer = compound.getInt("RespawnTimer");
        if(compound.contains("RespawnType"))
        	RespawnType = SpiderVariant.byId(compound.getInt("RespawnType"));
        if(compound.contains("RespawnData"))
        	RespawnData = compound.getCompound("RespawnData");
        if (compound.hasUUID("Occupied"))
            OccupiedUUID = Optional.of(compound.getUUID("Occupied"));

        updateAttached();
		updateOccupied();
	}

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);

        if(IsRespawning) {
			compound.putBoolean("Respawning", true);
			compound.putInt("RespawnTimer", RespawnTimer);
			compound.putInt("RespawnType", RespawnType.getId());
			compound.put("RespawnData", RespawnData);
		}
		
        OccupiedUUID.ifPresent(UUID -> {
        	compound.putUUID("Occupied", UUID);
        });
    }

    public boolean Verify(Entity requestee) {
    	Level world = this.getLevel();
    	BlockPos pos = this.getBlockPos();
    	BlockState state = world.getBlockState(pos);
    	BedNorthBlockEntity entity = (BedNorthBlockEntity) world.getBlockEntity(pos);

    	if(entity == null)
    		return false;
    	if(!(state.getBlock() instanceof BedNorthBlock))
    		return false;
    	if(!entity.isOccupied() || entity.getOccupied() != requestee)
    		return false;
    		
    	return true;
    }
	
	public void setOccupied(Entity mob, Boolean updateWebs)
	{
		OccupiedUUID = Optional.of(mob.getUUID());
		updateOccupied();
		if(updateWebs == true)
			updateAttached();
	}
	
	public void removeOccupied() {
		OccupiedUUID = Optional.empty();
    	this.IsRespawning = false;
    	this.RespawnTimer = 0;
		updateOccupied();
		updateAttached();
	}

	public void tick(BlockState state, Level world, BlockPos pos) {
		if(this.IsRespawning == false)
			return;

		if(RespawnTimer > 0)
    		RespawnTimer--;

        if(this.getOccupied() != null || RespawnTimer > 0) {
        	world.scheduleTick(pos, state.getBlock(), 20);
        } else if(this.getOccupied() == null && RespawnTimer <= 0) {
 			this.finishRespawn(world, pos);
        }
    }
    public void finishRespawn(Level world, BlockPos pos) {
    	TamableSpider spider = (TamableSpider)RespawnType.getType().create(world);
    	RespawnData.put("HomePos", NbtUtils.writeBlockPos(pos));
    	spider.load(RespawnData);
    	
        spider.setPos((double)pos.getX() + 0.5, (double)pos.getY() + 0.6875, (double)pos.getZ() + 0.5);
		world.addFreshEntity(spider);
    	this.IsRespawning = false;
    	this.RespawnTimer = 0;
    }

	public void triggerRespawn(TamableSpider spider, int time) {
		IsRespawning = true;
		RespawnTimer = time;
		RespawnType = spider.getVariant();
		
		RespawnData = new CompoundTag();
		spider.saveWithoutId(RespawnData);
		float maxHP = spider.getMaxHealth();

		spider.remove(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
		RespawnData.putBoolean("HasRespawned", true);
		RespawnData.putBoolean("Sitting", true);
		RespawnData.putFloat("Health", maxHP);
		RespawnData.remove("PortalCooldown");
		RespawnData.remove("HasVisualFire");
		RespawnData.remove("FallDistance");
		RespawnData.remove("TicksFrozen");
		RespawnData.remove("Passengers");
		RespawnData.remove("DeathTime");
		RespawnData.remove("VemonTimer");
		RespawnData.remove("SilkTimer");
		RespawnData.remove("Motion");
		RespawnData.remove("Fire");
		RespawnData.remove("Air");

		if(time <= 0) {
			this.finishRespawn(this.getLevel(), this.getBlockPos());
		} else {
			this.tick(this.getBlockState(), this.getLevel(), this.getBlockPos());
		}
	}

	public Entity getOccupied() {
		UUID uuid = this.getOccupiedUUID();
		if(uuid != null && getLevel() instanceof ServerLevel server)
        	return server.getEntity(uuid);
    	return null;
	}

	public UUID getOccupiedUUID() {
		if(!OccupiedUUID.isPresent())
			return null;
		return OccupiedUUID.get();
	}

	public boolean respawning() {
		return this.IsRespawning;
	}

	public int respawnTime() {
		return this.RespawnTimer;
	}

	public boolean isOccupied() {
		return this.IsRespawning ? true : this.getOccupied() != null;
	}

	public void updateOccupied() {
		((BedNorthBlock) getBlockState().getBlock()).updateOccupied(OccupiedUUID);
	}

	public void updateAttached() {
		((BedNorthBlock) getBlockState().getBlock()).updateAttached(isOccupied(), this.getLevel(), this.getBlockPos());
	}
}
