package tameable.spiders.entity;

import javax.annotation.Nullable;
import io.netty.buffer.Unpooled;

import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.sounds.SoundSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

import tameable.spiders.entity.TamableSpider;
import tameable.spiders.enums.Utility;
import tameable.spiders.AddItemResult;

public abstract class RideableSpider extends TamableSpider implements HasCustomInventoryScreen, Saddleable {
    private static final EntityDataAccessor<Boolean> Saddled = SynchedEntityData.defineId(RideableSpider.class, EntityDataSerializers.BOOLEAN);

	protected RideableSpider(EntityType<? extends RideableSpider> type, Level world) {
        super(type, world);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        int SaddleSlot = this.getSlot(Utility.SADDLE);
        Item item = itemstack.getItem();
        
    	if(this.isRideable() && !this.hasInventory() && !player.isShiftKeyDown()) {
    		if(isSaddled()) {
    			player.startRiding(this);
    			return InteractionResult.SUCCESS;
    		} else if(item == Items.SADDLE) {
    			AddItemResult equipped = this.addItem(new ItemStack(Items.SADDLE), 0, this.getInventorySize(true), false, null);
    			if(!equipped.failed) {
    				this.updateSaddle();
    				this.usePlayerItem(player, hand, itemstack);
    				return InteractionResult.SUCCESS;
    			}
    		}
    	}

    	return super.mobInteract(player, hand);
    }
    
    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(Saddled, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Saddled",  this.entityData.get(Saddled));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if(compound.contains("Saddled"))
        	this.entityData.set(Saddled, compound.getBoolean("Saddled"));
    }
    
    @Override
    public boolean isSaddleable() {
        return !this.isBaby() && this.isRideable();
    }

    @Override
    public void equipSaddle(@Nullable SoundSource source) {
    }

    @Override
    public boolean isSaddled() {
    	return this.entityData.get(Saddled);
    }

    public void updateSaddle() {
    	boolean hasSaddle = false;
    	if(this.isSaddleable()) {
    		if(getSlot(Utility.SADDLE) != -1) {
    			hasSaddle = inventory.getStackInSlot(getSlot(Utility.SADDLE)).getItem() == Items.SADDLE;
    		} else {
    			hasSaddle = this.hasItem(Items.SADDLE, true);
    		}
    	}
    	if(hasSaddle != this.entityData.get(Saddled) && hasSaddle == true)
    		playSound("entity.pig.saddle", SoundSource.NEUTRAL);
    	this.entityData.set(Saddled, hasSaddle);
    }

    @Override
    public boolean isPushable() {
        return !this.isVehicle();
    }

    @Override
    public boolean isImmobile() {
        return super.isImmobile() && this.isVehicle() && this.isSaddled();
    }
    
    @Override
    public void openCustomInventoryScreen(Player player) {
        if (!this.level().isClientSide && (!this.isVehicle() || this.hasPassenger(player)) && this.isTame()) {
            this.openInventory(player);
        }
    }

    protected void doPlayerRide(Player player) {
        if (!this.level().isClientSide && this.isRideable()) {
            player.setYRot(this.getYRot());
            player.setXRot(this.getXRot());
            player.startRiding(this);
        }
    }

    public boolean isRideable() {
    	return this.getVariant().isRideable(this.level());
    }

    @Override
	public void travel(Vec3 dir) 
	{
		LivingEntity rider = this.getControllingPassenger();
		if (this.isVehicle() && this.isSaddled() && rider != null)
			super.travel(new Vec3(rider.xxa, 0, rider.zza));
		super.travel(dir);
	}

    @Override
    protected void tickRidden(Player player, Vec3 dir) {
        super.tickRidden(player, dir);
		if(!this.isRideable())
			return;
        
        Vec2 vec2 = this.getRiddenRotation(player);
        this.setRot(vec2.y, vec2.x);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
    }

    protected Vec2 getRiddenRotation(LivingEntity entity) {
        return new Vec2(entity.getXRot() * 0.5F, entity.getYRot());
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 dir) {
        if (this.onGround()) {
            return Vec3.ZERO;
        } else {
            float f = player.xxa * 0.5F;
            float f1 = player.zza;
            if (f1 <= 0.0F) {
                f1 *= 0.25F;
            }

            return new Vec3((double)f, 0.0, (double)f1);
        }
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.35f;
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        if (this.isSaddled() && this.isRideable()) {
            Entity entity = this.getFirstPassenger();
            if (entity instanceof Player) {
                return (Player)entity;
            }
        }

        return super.getControllingPassenger();
    }
}