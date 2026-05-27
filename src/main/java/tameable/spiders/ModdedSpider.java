package tameable.spiders.entity;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

import tameable.spiders.init.SpiderRules;
import tameable.spiders.init.TameableSpidersModItems;
import tameable.spiders.network.TameableSpidersModVariables;
import tameable.spiders.block.entity.BedNorthBlockEntity;
import tameable.spiders.block.BedNorthBlock;
import tameable.spiders.entity.RideableSpider;
import tameable.spiders.goal.ClaimBedGoal;
import tameable.spiders.enums.Utility;
import tameable.spiders.AddItemResult;

public abstract class ModdedSpider extends RideableSpider {
    protected static final EntityDataAccessor<Integer> VenomTimer = SynchedEntityData.defineId(ModdedSpider.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> SilkTimer = SynchedEntityData.defineId(ModdedSpider.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Optional<BlockPos>> HomePos = SynchedEntityData.defineId(ModdedSpider.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
	public static final EntityDataAccessor<Boolean> HasRespawned = SynchedEntityData.defineId(ModdedSpider.class, EntityDataSerializers.BOOLEAN);
    public BedNorthBlockEntity Home;
	
    protected ModdedSpider(EntityType<? extends ModdedSpider> type, Level world) {
        super(type, world);
    }

    @Override
    protected void registerGoals() {
    	super.registerGoals();
        this.goalSelector.addGoal(8, new ClaimBedGoal(this, 1.0, 12));
    }

    @Override
    public boolean shouldDropExperience() {
        return !this.entityData.get(HasRespawned);
    }

    @Override
    protected boolean shouldDropLoot() {
        return !this.entityData.get(HasRespawned);
    }

    @Override
	public void die(DamageSource source) {
		super.die(source);

		if(this.entityData.get(HasRespawned) == true && !this.level().getLevelData().getGameRules().getBoolean(SpiderRules.KEEP_INVENTORY))
			this.dropEquipment();
	}

	@Override
	protected void tickDeath()
	{
        ++this.deathTime;
        if (this.deathTime >= 20 && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte)60);
            if (this.willRespawn()) {
				this.Home.triggerRespawn((TamableSpider)this, this.level().getLevelData().getGameRules().getInt(SpiderRules.RESPAWN_TIME));
			} else {
				this.remove(Entity.RemovalReason.KILLED);
			}
        }
    }

	@Override
    public void remove(Entity.RemovalReason reason) {
    	boolean willRemain = reason.shouldSave() == true || reason.shouldDestroy() == false;
        if(hasHome() && !willRemain)
        	Home.removeOccupied();
        	
        super.remove(reason);
    }

    @Override
    public void baseTick() {
    	super.baseTick();
    	if(!this.isTame())
    		return;
    	
    	int VenomSlot = getSlot(Utility.VENOM);
    	if(this.canProduceVenom()) {
    		if(this.getVenomTimer() > 0) {
    			this.setVenomTimer(this.getVenomTimer() - 1);
    		} else if(getSlot(Utility.VENOM) != -1) {
    			if(inventory.getStackInSlot(getSlot(Utility.VENOM)).getItem() == Items.GLASS_BOTTLE)
    			{
					AddItemResult venomResult = this.convertItem(VenomSlot, TameableSpidersModItems.VENOM.get(), true, true, new ArrayList(List.of(Items.GLASS_BOTTLE)));
					this.setVenomTimer(venomResult.failed ? 20 : 6000);
					if(!venomResult.dropped && !venomResult.failed)
						playSound("entity.cow.milk", SoundSource.PLAYERS);
    			}
    		}
    	}

    	if(this.canProduceSilk()) {
    		if(this.getSilkTimer() > 0) {
    			this.setSilkTimer(this.getSilkTimer() - 1);
    		} else {
				addItem(new ItemStack(TameableSpidersModItems.SILK), 0, 9, true, null);
				this.setSilkTimer(6000);
    		}
    	}
    }

    @Override
	protected void defineSynchedData() {
		super.defineSynchedData();
        this.entityData.define(VenomTimer, 6000);
        this.entityData.define(SilkTimer, 6000);
        this.entityData.define(HomePos, Optional.empty());
        this.entityData.define(HasRespawned, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
		compound.putBoolean("HasRespawned", this.entityData.get(HasRespawned));
        compound.putInt("VenomTimer", this.getVenomTimer());
        compound.putInt("SilkTimer", this.getSilkTimer());
        this.getHome().ifPresent(Home -> {
            compound.put("HomePos", NbtUtils.writeBlockPos(Home));
        });
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        
		if (compound.contains("HasRespawned"))
			this.entityData.set(HasRespawned, compound.getBoolean("HasRespawned"));
        if(compound.contains("VemonTimer"))
        	this.setVenomTimer(compound.getInt("VenomTimer"));
        if(compound.contains("SilkTimer"))
        	this.setSilkTimer(compound.getInt("SilkTimer"));
        if (compound.contains("HomePos"))
			this.setHomePos(NbtUtils.readBlockPos(compound.getCompound("HomePos")));
    }
	
	public Optional<BlockPos> getHome() {
        return this.entityData.get(HomePos);
    }
	
	public Block getBed() {
        return this.level().getBlockState(this.getHomePos()).getBlock();
    }
	
	public BlockEntity getBedEntity() {
        return this.level().getBlockEntity(this.getHomePos());
    }

    public BlockPos getHomePos() {
    	BlockPos Pos = new BlockPos(0, 0, 0);
    	Optional<BlockPos> Home = getHome();
    	if(Home.isPresent())
    		return Home.get();
    	
    	return Pos;
    }
    
    public void setHomePos(BlockPos pos) {
    	this.entityData.set(HomePos, Optional.of(pos));
    	this.Home = (BedNorthBlockEntity)this.getBedEntity();
    }

    public void storeHome(BlockPos pos) {
    	BlockEntity block = this.level().getBlockEntity(pos);
    	if(block instanceof BedNorthBlockEntity Bed)
    	{
    		if(Bed.isOccupied() == false)
    		{
    			Bed.setOccupied(this, true);
    			this.setHomePos(pos);
    			this.Home = Bed;
    		}
    	}
    }

    public void storeHome(int x, int y, int z) {
    	this.storeHome(BlockPos.containing(x, y, z));
	}
   	
   	public boolean hasHome() {
      	if(Home == null || (!(this.getBed() instanceof BedNorthBlock) && this.level().isLoaded(this.getHomePos())))
      		return false;
      	if(!this.Home.Verify(this))
      		return false;
      	return true;
   	}

   	public void resetHome() {
   		this.entityData.set(HomePos, Optional.empty());
   		Home = null;
   	}

	@Override
    public boolean willRespawn() {
    	return this.level().getLevelData().getGameRules().getBoolean(SpiderRules.RESPAWN) && this.isTame() && this.hasHome();
    }

    public int getVenomTimer() {
        return this.entityData.get(VenomTimer);
    }

    public void setVenomTimer(int time) {
        this.entityData.set(VenomTimer, time);
    }

    public int getSilkTimer() {
        return this.entityData.get(SilkTimer);
    }

    public void setSilkTimer(int time) {
        this.entityData.set(SilkTimer, time);
    }

    public void harvestVenom(Player player, ItemStack bottle) {
    	bottle.shrink(1);
		player.getInventory().add(new ItemStack(TameableSpidersModItems.VENOM));
		this.playSound("entity.cow.milk", SoundSource.PLAYERS);
		this.setVenomTimer(6000);
    }

    public int getVenomLevel() {
    	int MaxLevel = 6;
    	return Mth.clamp(MaxLevel - Mth.floor(this.getVenomTimer() / (6000 / MaxLevel)), 0, MaxLevel);
    }

    public boolean canProduceVenom() {
    	if(!this.level().getLevelData().getGameRules().getBoolean(SpiderRules.PRODUCE_VENOM) || this.isBaby())
    		return false;
    		
		return getVariant().canProduceVenom(this.level());
    }

    public boolean canProduceSilk() {
    	if(!this.level().getLevelData().getGameRules().getBoolean(SpiderRules.PRODUCE_SILK) || this.isBaby())
    		return false;
    		
		return getVariant().canProduceSilk(this.level());
    }

    public boolean canArmor() {
		return getVariant().canArmor() && !this.isBaby();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
		TameableSpidersModVariables.PlayerVariables _vars = player.getData(TameableSpidersModVariables.PLAYER_VARIABLES);
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();

		if(_vars.assigning == true)
		{
			Component Text = assignHome(_vars, player);
    		player.displayClientMessage(Text, true);
    		_vars.assigning = false;
			_vars.syncPlayerVariables(player);
			return InteractionResult.SUCCESS;
		}
		
		if(this.canProduceVenom() && this.getVenomLevel() == 9 && item == Items.GLASS_BOTTLE) {
			this.harvestVenom(player, itemstack);
			return InteractionResult.CONSUME;
		}


		return super.mobInteract(player, hand);
    }

    public Component assignHome(TameableSpidersModVariables.PlayerVariables variables, Player assigner)
    {
    	Block block = variables.assignBed.getBlock();
    	BlockPos pos = BlockPos.containing(variables.assignX, variables.assignY, variables.assignZ);
    	Level world = this.level();
    	
    	if(block instanceof BedNorthBlock Bed)
    	{
    		BedNorthBlockEntity BedEntity = (BedNorthBlockEntity) world.getBlockEntity(pos);
			Block newBlock = level().getBlockState(BedEntity.getBlockPos()).getBlock();
			if(newBlock instanceof BedNorthBlock newBed)
			{
    			if(BedEntity.isOccupied())
    				return Component.translatable("tameable_spiders.bed_claimed");
    			if(hasHome())
    				return Component.translatable("tameable_spiders.home_already");
    			if(!isTame() || !isOwnedBy(assigner))
    				return Component.translatable("tameable_spiders.lack_ownership");

    			storeHome(BedEntity.getBlockPos());
    			return Component.translatable("tameable_spiders.bed_linked");
			}
    		
			return Component.translatable("tameable_spiders.bed_missing");
    	}

    	return Component.literal("Error: unknown outcome");
    }
}