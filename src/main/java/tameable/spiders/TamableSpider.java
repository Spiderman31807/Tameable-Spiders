package tameable.spiders.entity;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import io.netty.buffer.Unpooled;

import net.neoforged.neoforge.items.wrapper.EntityHandsInvWrapper;
import net.neoforged.neoforge.items.wrapper.EntityArmorInvWrapper;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import net.neoforged.neoforge.items.ItemStackHandler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;

import tameable.spiders.init.SpiderRules;
import tameable.spiders.init.TameableSpidersModItems;
import tameable.spiders.world.inventory.SpiderInventoryMenu;
import tameable.spiders.entity.BreedableSpider;
import tameable.spiders.goal.OwnerHurtByGoal;
import tameable.spiders.goal.OwnerHurtGoal;
import tameable.spiders.goal.SittingGoal;
import tameable.spiders.goal.FollowGoal;
import tameable.spiders.enums.Utility;
import tameable.spiders.AddItemResult;

public abstract class TamableSpider extends BreedableSpider implements OwnableEntity {
    protected static final EntityDataAccessor<Boolean> Tamed = SynchedEntityData.defineId(TamableSpider.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> Sitting = SynchedEntityData.defineId(TamableSpider.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Optional<UUID>> Owner = SynchedEntityData.defineId(TamableSpider.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final EntityDataAccessor<Integer> CollarColor = SynchedEntityData.defineId(TamableSpider.class, EntityDataSerializers.INT);
	
    protected TamableSpider(EntityType<? extends TamableSpider> type, Level world) {
        super(type, world);
        this.setPathfindingMalus(BlockPathTypes.POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_POWDER_SNOW, -1.0F);
    }

    @Override
    protected void registerGoals() {
    	super.registerGoals();
        this.goalSelector.addGoal(2, new SittingGoal(this));
        this.goalSelector.addGoal(6, new FollowGoal(this, 1, 10, 2));
        this.targetSelector.addGoal(1, new OwnerHurtByGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtGoal(this));
    }

    public boolean canAttackDuringDay() {
    	return this.isTame();
    }

    public boolean canAttackRandom() {
    	return !this.isTame();
    }

    @Override
	protected void defineSynchedData() {
		super.defineSynchedData();
        this.entityData.define(Tamed, false);
        this.entityData.define(Sitting, false);
        this.entityData.define(Owner, Optional.empty());
        this.entityData.define(CollarColor, DyeColor.RED.getId());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        
        if (this.getOwnerUUID() != null)
            compound.putUUID("Owner", this.getOwnerUUID());
        compound.putInt("CollarColor", this.getCollarColor().getId());
        compound.putBoolean("Sitting", this.isSitting());
        compound.putBoolean("Tamed", this.isTame());
		compound.put("InventoryCustom", inventory.serializeNBT());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        
        if(compound.contains("Owner"))
        	this.setOwnerUUID(compound.getUUID("Owner"));
        if(compound.contains("CollarColor"))
        	this.setCollarColor(DyeColor.byId(compound.getInt("CollarColor")));
        if(compound.contains("Sitting"))
        	this.setSitting(compound.getBoolean("Sitting"));
        if(compound.contains("Tamed"))
			this.setTame(compound.getBoolean("Tamed"), false);
		if (compound.get("InventoryCustom") instanceof CompoundTag inventoryTag)
			inventory.deserializeNBT(inventoryTag);
    }

    @Override
    public boolean canBeLeashed(Player player) {
        return !this.isLeashed() && this.isTame();
    }

    protected void spawnTamingParticles(boolean succeed) {
        ParticleOptions particleoptions = ParticleTypes.HEART;
        if (!succeed)
            particleoptions = ParticleTypes.SMOKE;

        for(int i = 0; i < 7; ++i) {
            double d0 = this.random.nextGaussian() * 0.02;
            double d1 = this.random.nextGaussian() * 0.02;
            double d2 = this.random.nextGaussian() * 0.02;
            this.level().addParticle(particleoptions, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d0, d1, d2);
        }
    }

    @Override
    public void handleEntityEvent(byte Event) {
        if (Event == 7) {
            this.spawnTamingParticles(true);
        } else if (Event == 6) {
            this.spawnTamingParticles(false);
        } else {
            super.handleEntityEvent(Event);
        }
    }

	public DyeColor getCollarColor() {
        return DyeColor.byId(this.entityData.get(CollarColor));
    }

	public void setCollarColor(DyeColor dyecolor) {
        this.entityData.set(CollarColor, dyecolor.getId());
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double p_21542_) {
        return !this.isTame();
    }

    public boolean isPreventingPlayerRest(Player player) {
        return !this.isTame();
    }

    public boolean isTame() {
        return this.entityData.get(Tamed);
    }

    public void setTame(boolean tamed, boolean sideAffect) {
        this.entityData.set(Tamed, tamed);
		if (sideAffect)
            this.applyTamingSideEffects();
    }

    protected void applyTamingSideEffects() {
		float Health = this.getVariantHealth();
		float HealthBuff = Health + 8;
    	
        if (this.isTame()) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(HealthBuff);
            this.setHealth(HealthBuff);
        } else {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(Health);
        }
    }

    public boolean isSitting() {
        return this.entityData.get(Sitting);
    }

    public void setSitting(boolean sitting) {
        this.entityData.set(Sitting, sitting);
    }

    public void setSittingPose(boolean sitting) {
    	this.setPose(Pose.STANDING);
    	if(sitting)
        	this.setPose(Pose.SITTING);
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return this.entityData.get(Owner).orElse(null);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(Owner, Optional.ofNullable(uuid));
    }

    public void tame(Player player) {
        this.setTame(true, true);
        this.setOwnerUUID(player.getUUID());
        this.setupUtilities();
        if (player instanceof ServerPlayer serverPlayer) {
			AdvancementHolder _adv = serverPlayer.server.getAdvancements().get(new ResourceLocation("tameable_spiders:arachnid_ally"));
			if (_adv != null) {
				AdvancementProgress _ap = serverPlayer.getAdvancements().getOrStartProgress(_adv);
				if (!_ap.isDone()) {
					for (String criteria : _ap.getRemainingCriteria())
						serverPlayer.getAdvancements().award(_adv, criteria);
				}
			}
		}
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            if (!this.level().isClientSide) {
                this.setSitting(false);
            }

            return super.hurt(source, amount);
        }
    }

    public boolean isInvulnerableTo(DamageSource source) {
        if (source.is(DamageTypeTags.IS_FALL))
            return !this.level().getGameRules().getBoolean(SpiderRules.FALLDAMAGE);
    	return super.isInvulnerableTo(source);
    }

    @Override
    public boolean canAttack(LivingEntity entity) {
        return this.isOwnedBy(entity) ? false : super.canAttack(entity);
    }

    public boolean isOwnedBy(LivingEntity entity) {
        return entity == this.getOwner();
    }

    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        if (target instanceof Creeper || target instanceof Ghast || target instanceof ArmorStand) {
            return false;
        } else if (target instanceof TamableSpider spider) {
            return !spider.isTame() || spider.getOwner() != owner;
        } else {
            if (target instanceof Player player && owner instanceof Player player1 && !player1.canHarmPlayer(player)) {
                return false;
            }

            if (target instanceof AbstractHorse abstracthorse && abstracthorse.isTamed()) {
                return false;
            }

            if (target instanceof TamableAnimal tamableanimal && tamableanimal.isTame()) {
                return false;
            }

            return true;
        }
    }

    @Override
    public PlayerTeam getTeam() {
        if (this.isTame()) {
            LivingEntity livingentity = this.getOwner();
            if (livingentity != null) {
                return livingentity.getTeam();
            }
        }

        return super.getTeam();
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        if (this.isTame()) {
            LivingEntity livingentity = this.getOwner();
            if (entity == livingentity) {
                return true;
            }

            if (livingentity != null) {
                return livingentity.isAlliedTo(entity);
            }
        }

        return super.isAlliedTo(entity);
    }

    @Override
    public void die(DamageSource source) {
        net.minecraft.network.chat.Component deathMessage = this.getCombatTracker().getDeathMessage();
        super.die(source);

        if (this.dead)
        if (!this.level().isClientSide && this.level().getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES) && this.getOwner() instanceof ServerPlayer) {
            this.getOwner().sendSystemMessage(deathMessage);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
    	boolean ShiftSitting = this.getVariant().isRideable(this.level()) && !this.hasInventory();
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();

        
        if (!this.level().isClientSide || this.isBaby() && this.isFood(itemstack))
        {
            if (this.isTame())
            {
                if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
                    this.heal((float)itemstack.getFoodProperties(this).getNutrition());
            		this.usePlayerItem(player, hand, itemstack);
            		this.gameEvent(GameEvent.EAT, this);
            		return InteractionResult.SUCCESS;
                }
                else
                {
                    if (item instanceof DyeItem dyeitem && this.isOwnedBy(player))
                    {
                        DyeColor dyecolor = dyeitem.getDyeColor();
                        if (dyecolor != this.getCollarColor()) {
                            this.setCollarColor(dyecolor);
                            this.usePlayerItem(player, hand, itemstack);
                            return InteractionResult.SUCCESS;
                        }

                        return super.mobInteract(player, hand);
                    }

                    if(this.hasInventory() && player.isShiftKeyDown() != ShiftSitting) {
                    	this.openInventory(player);
                    	return InteractionResult.SUCCESS;
                    }
                    
                    InteractionResult interactionresult = super.mobInteract(player, hand);
                    if (!interactionresult.consumesAction() && this.isOwnedBy(player) && player.isShiftKeyDown() == ShiftSitting)
                    {
	                    this.setSitting(!this.isSitting());
						this.getNavigation().moveTo(this.getX(), this.getY(), this.getZ(), 0);
                        this.jumping = false;
                        this.setTarget(null);
                        return InteractionResult.SUCCESS;
                    }
                    else
                    {
                        return interactionresult;
                    }
                }
            }
            else if (this.isFood(itemstack) && this.getTarget() == null)
            {
                this.usePlayerItem(player, hand, itemstack);
                this.tryToTame(player);
                return InteractionResult.SUCCESS;
            } else {
                return super.mobInteract(player, hand);
            }
        }
        else
        {
            boolean flag = this.isOwnedBy(player) || this.isTame() || this.isFood(itemstack) && !this.isTame() && this.getTarget() == null;
            return flag ? InteractionResult.CONSUME : InteractionResult.PASS;
        }
    }

    private void tryToTame(Player player) {
        if (this.random.nextInt(3) == 0) {
            this.tame(player);
			this.getNavigation().moveTo(this.getX(), this.getY(), this.getZ(), 0);
            this.jumping = false;
            this.setTarget(null);
            this.setSitting(true);
            this.level().broadcastEntityEvent(this, (byte)7);
        } else {
            this.level().broadcastEntityEvent(this, (byte)6);
        }
    }
    
	@Override
    public boolean isFood(ItemStack itemstack) {
        return itemstack.getItem().isEdible() && itemstack.getFoodProperties(this).isMeat();
    }

	@Override
    public AnimationState getState(boolean Sitting) {
    	return super.getState(Sitting);
    }

	@Override
    public boolean canMate(BreedableSpider spider) {
    	if(spider instanceof TamableSpider Spider) {
    		if(!this.isTame() || !Spider.isTame())
    			return false;
    		if(this.isSittingPose() || Spider.isSittingPose())
    			return false;
    	}
    	return super.canMate(spider);
    }
    
    @Nullable
    public TamableSpider getBreedOffspring(ServerLevel server, AgeableSpider partner) {
        TamableSpider baby = (TamableSpider)this.Type.create(server);
        if (baby != null && partner instanceof TamableSpider spider) {
            if (this.random.nextBoolean()) {
                baby.setVariant(this.getVariant());
            } else {
                baby.setVariant(spider.getVariant());
            }

            if (this.isTame()) {
                baby.setOwnerUUID(this.getOwnerUUID());
                baby.setTame(true, true);
                if (this.random.nextBoolean()) {
                    baby.setCollarColor(this.getCollarColor());
                } else {
                    baby.setCollarColor(spider.getCollarColor());
                }
            }
        }

        return baby;
    }

	protected final ItemStackHandler inventory = new ItemStackHandler(14) {
		@Override
		public int getSlotLimit(int slot) {
			return 64;
		}
	};
	protected final CombinedInvWrapper combined = new CombinedInvWrapper(inventory, new EntityHandsInvWrapper(this), new EntityArmorInvWrapper(this));

	public CombinedInvWrapper getCombinedInventory() {
		return combined;
	}

	public boolean hasInventory() {
		return this.getInventorySize(true) > 0 && this.level().getLevelData().getGameRules().getBoolean(SpiderRules.INVENTORY);
	}

	public int getInventorySize(boolean includeUtility) {
		int InventorySize = getVariant().inventorySize();
		int UtilitySize = 0;
		
		InventorySize = (int) (this.isBaby() ? (double)InventorySize * 0.25 : InventorySize);
		if(includeUtility)
			UtilitySize = this.UtilityData.size();
			
		return InventorySize + UtilitySize;
	}

	public boolean willRespawn() {
		return false;
	}

	@Override
	protected void dropEquipment() {
		if(this.level().getLevelData().getGameRules().getBoolean(SpiderRules.KEEP_INVENTORY) && this.willRespawn() == true)
			return;
		
		super.dropEquipment();
		for (int i = 0; i < inventory.getSlots(); ++i) {
			ItemStack itemstack = inventory.getStackInSlot(i);
			if (!itemstack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemstack)) {
				ItemStack GroundItem = itemstack.copy();
				this.spawnAtLocation(GroundItem);
			}
			itemstack.setCount(0);
		}
	}

    protected void openInventory (Player player) {
		UtilityData.refreshUtilities();
		this.refreshUtilities();
		
    	if (player instanceof ServerPlayer serverPlayer) {
			serverPlayer.openMenu(new MenuProvider() {
				@Override
				public Component getDisplayName() {
					return Component.literal("Spider Inventory");
				}
				
				@Override
				public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player)
				{
					FriendlyByteBuf packetBuffer = new FriendlyByteBuf(Unpooled.buffer());
					packetBuffer.writeBlockPos(player.blockPosition());
					packetBuffer.writeByte(0);
					packetBuffer.writeVarInt(TamableSpider.this.getId());
					return new SpiderInventoryMenu(id, inventory, packetBuffer);
				}
			}, 
				buf -> {
				buf.writeBlockPos(player.blockPosition());
				buf.writeByte(0);
				buf.writeVarInt(this.getId());
			});
		}
    }

    public boolean hasItem(Item item, boolean includeUtility) {
    	for (int idx = includeUtility ? 0 : 3; idx < this.getInventorySize(includeUtility); ++idx) {
    		ItemStack stack = inventory.getStackInSlot(idx);
    		if(stack.getItem() == item)
    			return true;
    	}
    	return false;
    }

    protected AddItemResult convertItem(int slot, Item newItem, boolean allowOtherSlots, boolean allowDropping, ArrayList<Item> canReplace) {
   		ItemStack oldStack = inventory.getStackInSlot(slot);
   		Item oldItem = oldStack.getItem();
   		if(oldItem == newItem)
   			return new AddItemResult(false);

    	if(canReplace == null)
    		canReplace = new ArrayList(List.of(oldItem, newItem));
    	if(!canReplace.contains(oldItem))
    		canReplace.add(oldItem);
    	if(!canReplace.contains(newItem))
    		canReplace.add(newItem);
    		
		ItemStack newStack = new ItemStack(newItem);
   		int count = oldStack.getCount();
   		if(count <= 1) {
    		inventory.extractItem(slot, 1, false);
    		inventory.insertItem(slot, newStack, false);
    		return new AddItemResult(oldItem, slot);
   		}

		if(!allowOtherSlots)
			return new AddItemResult(false);

   		AddItemResult result = addItem(newStack, 0, this.getInventorySize(true) + 1, allowDropping, canReplace);
   		if(result.replaced == null || result.replaced == oldItem)
    		inventory.extractItem(slot, 1, false);
   			
   		return result;
	}

    protected AddItemResult addItem(ItemStack addStack, int minSlot, int maxSlot, boolean canDrop, ArrayList<Item> canReplace) {
    	if(canReplace == null)
    		canReplace = new ArrayList(List.of(addStack.getItem()));
    	if(!canReplace.contains(addStack.getItem()))
    		canReplace.add(addStack.getItem());
    	
    	boolean replace = false;
    	Item replacing = null;
    	int vaildSlot = -1;
    	int emptySlot = -1;

    	for (int idx = minSlot; idx < maxSlot; ++idx) {
    		ItemStack stack = inventory.getStackInSlot(idx);
    		if(stack.isEmpty() && emptySlot == -1  && vaildItem(idx, addStack))
    			emptySlot = idx;
    			
    		if(canReplace.contains(stack.getItem()) && vaildItem(idx, addStack)) {
    			if(stack.getItem() == addStack.getItem() || stack.getCount() <= 1) {
    				replace = stack.getItem() != addStack.getItem() && !stack.isEmpty();
    				replacing = stack.getItem();
    				vaildSlot = idx;
    				break;
    			}
    		}
    	}

    	if(vaildSlot != -1) {
    		if(replace)
    			inventory.extractItem(vaildSlot, 1, false);
    		inventory.insertItem(vaildSlot, addStack, false);
    		return new AddItemResult(replacing, vaildSlot);
    	} else if(emptySlot != -1) {
    		inventory.insertItem(emptySlot, addStack, false);
    		return new AddItemResult(emptySlot);
    	} else if(canDrop) {
			popItem(addStack, 0);
    		return new AddItemResult(true);
    	}

    	return new AddItemResult(false);
    }

    public boolean vaildItem(int slot, ItemStack addStack) {
    	ItemStack stack = inventory.getStackInSlot(slot);
    	int slotLimit = inventory.getSlotLimit(slot);
    	int itemLimit = stack.isStackable() ? stack.getMaxStackSize() : 1;
    	int limit = Math.min(slotLimit, itemLimit);
    	Item item = addStack.getItem();
    	
    	if(limit < stack.getCount() + addStack.getCount())
    		return false;
    	if(slot == getSlot(Utility.SADDLE) && item != Items.SADDLE)
    		return false;
    	if(slot == getSlot(Utility.ARMOR) && !getVariant().getArmorItems().contains(item))
    		return false;
    	if(slot == getSlot(Utility.SILK) && item != TameableSpidersModItems.SILK.get())
    		return false;
    	if(slot == getSlot(Utility.VENOM) && (item != TameableSpidersModItems.VENOM.get() && item != Items.GLASS_BOTTLE))
    		return false;

    	return true;
    }
}