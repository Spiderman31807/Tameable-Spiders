package tamable.spiders.mixin;

import tamable.spiders.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import tamable.spiders.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import tamable.spiders.world.entity.ai.goal.AttackTargetInWebGoal;
import tamable.spiders.world.entity.ai.goal.MoveToSittingPosGoal;
import tamable.spiders.world.entity.ai.goal.SitWhenOrderedToGoal;
import tamable.spiders.world.entity.ai.goal.FollowOwnerGoal;
import tamable.spiders.world.entity.ai.goal.BegGoal;
import tamable.spiders.TamableSpider;
import tamable.spiders.SpiderArmorItem;
import tamable.spiders.GoalManager;
import tamable.spiders.Events;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.Mixin;

import org.checkerframework.checker.units.qual.radians;

import net.neoforged.neoforge.common.ItemAbilities;

import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.Mth;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.Holder;

import java.util.UUID;
import java.util.Optional;
import net.minecraft.world.InteractionResult.Pass;
import net.minecraft.server.level.ServerPlayer;

@Mixin(Spider.class)
public abstract class SpiderMixin extends Monster implements TamableSpider {
	@Unique
	private static final EntityDataAccessor<Boolean> Tamed = SynchedEntityData.defineId(Spider.class, EntityDataSerializers.BOOLEAN);
	@Unique
	private static final EntityDataAccessor<Boolean> Sitting = SynchedEntityData.defineId(Spider.class, EntityDataSerializers.BOOLEAN);
	@Unique
	private static final EntityDataAccessor<Boolean> Saddled = SynchedEntityData.defineId(Spider.class, EntityDataSerializers.BOOLEAN);
	@Unique
	private static final EntityDataAccessor<Boolean> Interested = SynchedEntityData.defineId(Spider.class, EntityDataSerializers.BOOLEAN);
	@Unique
	private static final EntityDataAccessor<Integer> CollarColor = SynchedEntityData.defineId(Spider.class, EntityDataSerializers.INT);
	@Unique
	private static final EntityDataAccessor<Long> SittingStamp = SynchedEntityData.defineId(Spider.class, EntityDataSerializers.LONG);
	@Unique
	private static final EntityDataAccessor<Optional<UUID>> Owner = SynchedEntityData.defineId(Spider.class, EntityDataSerializers.OPTIONAL_UUID);
	@Unique
	private static final EntityDataAccessor<ItemStack> Armor = SynchedEntityData.defineId(Spider.class, EntityDataSerializers.ITEM_STACK);
	@Unique
	public Optional<Vec3> lastSittingPos = Optional.empty();
	@Unique
	public LivingEntity defendTarget = null;
	@Unique
	public float interestedAngle;
	@Unique
	public float interestedAngleO;
	@Unique
	public boolean orderedToSit;

	private SpiderMixin(EntityType<? extends Monster> type, Level world) {
		super(type, world);
	}

	public void doPlayerRide(Player player) {
		if (!this.level().isClientSide) {
			player.setYRot(this.getYRot());
			player.setXRot(this.getXRot());
			player.startRiding(this);
		}
	}

	@Override
    protected int calculateFallDamage(float distance, float multiplier) {
    	return this.isInsideWeb() ? 0 : super.calculateFallDamage(distance, multiplier);
    }

	@Override
	protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions hitbox, float value) {
		Vec3 original = super.getPassengerAttachmentPoint(entity, hitbox, value);
		if (!this.isSaddled())
			return original;
		float radians = (float) Math.toRadians(-this.getYRot());
		double offsetX = Math.sin(radians) * (-0.6 * value);
		double offsetZ = Math.cos(radians) * (-0.6 * value);
		return original.add(offsetX, 0, offsetZ);
	}

	@Override
	public LivingEntity getControllingPassenger() {
		if (this.isSaddled() && this.getFirstPassenger() instanceof Player rider)
			return rider;
		return super.getControllingPassenger();
	}

	@Override
	protected void tickRidden(Player rider, Vec3 input) {
		super.tickRidden(rider, input);
		Vec2 rotation = this.getRiddenRotation(rider);
		this.setRot(rotation.y, rotation.x);
		this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
		this.setJumping(this.onClimbable() && Events.isPlayerJumping(rider));
	}

	protected Vec2 getRiddenRotation(LivingEntity rider) {
		return new Vec2(rider.getXRot() * 0.5F, rider.getYRot());
	}

	@Override
	protected Vec3 getRiddenInput(Player rider, Vec3 original) {
		double sidewaysMultiplier = this.onClimbable() ? 1 : 0.5;
		double straightMultiplier = rider.zza > 0 || this.onClimbable() ? 1 : 0.6;
		return new Vec3(rider.xxa * sidewaysMultiplier, 0, rider.zza * straightMultiplier);
	}

	protected float getRiddenSpeed(Player rider) {
		return 0.091f;
	}

	@Override
	public boolean isImmobile() {
		return super.isImmobile() && this.isVehicle() && this.isSaddled();
	}

	@Override
	public boolean isPushable() {
		return super.isPushable() && !this.isVehicle();
	}

	@Override
	protected void dropEquipment(ServerLevel server) {
		super.dropEquipment(server);
		ItemStack armor = this.getArmorItem();
		if (!armor.isEmpty() && !EnchantmentHelper.has(armor, EnchantmentEffectComponents.PREVENT_EQUIPMENT_DROP))
			this.spawnAtLocation(server, armor);
		if (this.isSaddled())
			this.spawnAtLocation(server, new ItemStack(Items.SADDLE));
	}

	@Override
	public void equipSaddle(ItemStack saddle, SoundSource soundSource) {
		this.setSaddled(true);
		this.makeSound(SoundEvents.HORSE_SADDLE);
	}

	@Override
	public boolean isSaddleable() {
		return this.isAlive() && !this.isBaby() && this.getType() != EntityType.CAVE_SPIDER && this.isTame();
	}

	@Override
	public void setSaddled(boolean saddled) {
		if (this.isSaddled() == saddled)
			return;
		this.entityData.set(Saddled, saddled);
		this.sendSyncFix(TamableSpider.SyncType.Saddled);
	}

	@Override
	public boolean isSaddled() {
		return this.entityData.get(Saddled);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		ItemStack armor = this.getArmorItem();
		if (!armor.isEmpty())
			compound.put("SpiderArmor", armor.save(this.registryAccess()));
		if (this.getOwnerUUID() instanceof UUID uuid)
			compound.putUUID("Owner", uuid);
		compound.putBoolean("Saddled", this.isSaddled());
		compound.putBoolean("Sitting", this.orderedToSit);
		compound.putInt("CollarColor", this.getCollarColor());
		compound.putLong("SittingStamp", this.sittingStamp());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("SpiderArmor") && ItemStack.parse(this.registryAccess(), compound.getCompound("SpiderArmor")).orElse(null) instanceof ItemStack stack)
			this.setArmorItem(stack);
		if (compound.hasUUID("Owner")) {
			try {
				this.setOwnerUUID(compound.getUUID("Owner"));
				this.setTame(true, false);
			} catch (Throwable throwable) {
				this.setTame(false, true);
			}
		} else {
			this.setOwnerUUID(null);
			this.setTame(false, true);
		}
		this.setSaddled(compound.getBoolean("Saddled"));
		this.orderedToSit = compound.getBoolean("Sitting");
		if (!this.isVehicle())
			this.setInSittingPose(this.orderedToSit);
		if (compound.contains("CollarColor"))
			this.setCollarColor(compound.getInt("CollarColor"));
		if (compound.contains("SittingStamp"))
			this.stampSittingChange(compound.getLong("SittingStamp"));
	}

	@Override
	public boolean isTame() {
		return this.entityData.get(Tamed);
	}

	@Override
	public void setTame(boolean tamed) {
		if (this.isTame() == tamed)
			return;
		this.entityData.set(Tamed, tamed);
		this.sendSyncFix(TamableSpider.SyncType.Tamed);
	}

	@Override
	public int getCollarColor() {
		return this.entityData.get(CollarColor);
	}

	@Override
	public void setCollarColor(int color) {
		if (this.getCollarColor() == color)
			return;
		this.entityData.set(CollarColor, color);
		this.sendSyncFix(TamableSpider.SyncType.CollarColor);
	}

	@Override
	public ItemStack getArmorItem() {
		return this.entityData.get(Armor);
	}

	@Override
	public void setArmorItem(ItemStack stack) {
		if (this.getArmorItem().equals(stack))
			return;
		this.entityData.set(Armor, stack);
		this.sendSyncFix(TamableSpider.SyncType.Armor);
	}

	@Override
	public boolean isOrderedToSit() {
		return this.orderedToSit;
	}

	@Override
	public void setOrderedToSit(boolean sit) {
		this.orderedToSit = sit;
	}

	@Override
	public boolean isInSittingPose() {
		return this.entityData.get(Sitting);
	}

	@Override
	public void setInSittingPose(boolean sitting) {
		if (this.isInSittingPose() == sitting)
			return;
		this.entityData.set(Sitting, sitting);
		this.sendSyncFix(TamableSpider.SyncType.Sitting);
		this.stampSittingChange(this.level().getGameTime());
	}

	@Override
	public long sittingStamp() {
		return this.entityData.get(SittingStamp);
	}

	@Override
	public void stampSittingChange(long stamp) {
		if (this.sittingStamp() == stamp)
			return;
		this.entityData.set(SittingStamp, stamp);
		this.sendSyncFix(TamableSpider.SyncType.SittingStamp);
	}

	@Override
	public boolean isInterested() {
		return this.entityData.get(Interested);
	}

	@Override
	public void setIsInterested(boolean interested) {
		if (this.isInterested() == interested)
			return;
		this.entityData.set(Interested, interested);
		this.sendSyncFix(TamableSpider.SyncType.Interested);
	}

	@Override
	public float getHeadRollAngle(float partialTick) {
		return Mth.lerp(partialTick, this.interestedAngleO, this.interestedAngle) * 0.15f * (float) Math.PI;
	}

	@Override
	public Optional<Vec3> getLastSittingPos() {
		return this.lastSittingPos;
	}

	@Override
	public void setLastSittingPos(Optional<Vec3> pos) {
		this.lastSittingPos = pos;
	}

	@Override
	public LivingEntity getDefendTarget() {
		return this.defendTarget;
	}
	
	@Override
	public void setDefendTarget(LivingEntity target) {
		this.defendTarget = target;
	}

	@Override
	public UUID getOwnerUUID() {
		return this.entityData.get(Owner).orElse(null);
	}

	@Override
	public void setOwnerUUID(UUID uuid) {
		UUID oldOwner = this.getOwnerUUID();
		boolean areSameNull = (oldOwner == null) == (uuid == null);
		boolean areBothValues = areSameNull && oldOwner != null;
		if (!areSameNull || (areBothValues && !oldOwner.equals(uuid)))
			this.sendSyncFix(TamableSpider.SyncType.Owner);
		this.entityData.set(Owner, Optional.ofNullable(uuid));
	}

	@Override
	public boolean canAttack(LivingEntity target) {
		return this.isOwnedBy(target) ? false : super.canAttack(target);
	}

	@Override
	public PlayerTeam getTeam() {
		if (this.isTame() && this.getOwner() instanceof LivingEntity owner)
			return owner.getTeam();
		return super.getTeam();
	}

	@Override
	protected boolean considersEntityAsAlly(Entity entity) {
		if (this.isTame() && this.getOwner() instanceof LivingEntity owner)
			return entity == owner || owner.isAlliedTo(entity);
		return super.considersEntityAsAlly(entity);
	}

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (this.isTame()) {
			if (this.isFood(stack) && this.getHealth() < this.getMaxHealth()) {
				FoodProperties foodproperties = stack.get(DataComponents.FOOD);
				float f = foodproperties != null ? (float) foodproperties.nutrition() : 1.0F;
				this.heal(2.0F * f);
				this.usePlayerItem(player, hand, stack);
				this.gameEvent(GameEvent.EAT);
				return InteractionResult.SUCCESS;
			}
			
			if (this.isOwnedBy(player)) {
				if (stack.getItem() instanceof DyeItem dye) {
					DyeColor color = dye.getDyeColor();
					if (color != this.getCollarDyeColor()) {
						this.setCollarColor(color);
						stack.consume(1, player);
						return InteractionResult.SUCCESS;
					}
					return super.mobInteract(player, hand);
				}
				
				ItemStack armorStack = this.getArmorItem();
				if (stack.getItem() instanceof SpiderArmorItem armor && armorStack.isEmpty() && !this.isBaby()) {
					this.makeSound(stack.get(DataComponents.EQUIPPABLE).equipSound().value());
					this.setArmorItem(stack.copyWithCount(1));
					stack.consume(1, player);
					return InteractionResult.SUCCESS;
				}
				
				if (stack.is(Items.SADDLE) && !this.isSaddled() && this.isSaddleable()) {
					this.equipSaddle(stack, SoundSource.NEUTRAL);
					stack.consume(1, player);
					return InteractionResult.SUCCESS;
				}
				
				boolean canRemove = stack.canPerformAction(ItemAbilities.SHEARS_REMOVE_ARMOR);
				if (!armorStack.isEmpty() && canRemove && (!EnchantmentHelper.has(armorStack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE) || player.isCreative())) {
					stack.hurtAndBreak(1, player, getSlotForHand(hand));
					this.playSound(SoundEvents.ARMOR_UNEQUIP_WOLF);
					this.setArmorItem(ItemStack.EMPTY);
					if (this.level() instanceof ServerLevel server)
						this.spawnAtLocation(server, armorStack);
					return InteractionResult.SUCCESS;
				}
				
				if (this.isSaddled()) {
					if (canRemove) {
						if (player.isSecondaryUseActive()) {
							this.doPlayerRide(player);
							return InteractionResult.SUCCESS;
						} else {
							stack.hurtAndBreak(1, player, getSlotForHand(hand));
							this.playSound(SoundEvents.ARMOR_UNEQUIP_WOLF);
							this.setSaddled(false);
							if (this.level() instanceof ServerLevel server)
								this.spawnAtLocation(server, new ItemStack(Items.SADDLE));
							return InteractionResult.SUCCESS;
						}
					}
					
					if (!player.isSecondaryUseActive()) {
						this.doPlayerRide(player);
						return InteractionResult.SUCCESS;
					}
				}
				
				if (this.isInSittingPose() && !armorStack.isEmpty() && armorStack.isDamaged() && armorStack.isValidRepairItem(stack)) {
					stack.shrink(1);
					this.playSound(SoundEvents.WOLF_ARMOR_REPAIR);
					int repairAmount = (int) ((float) armorStack.getMaxDamage() * 0.125F);
					armorStack.setDamageValue(Math.max(0, armorStack.getDamageValue() - repairAmount));
					return InteractionResult.SUCCESS;
				}
				
				if (player.isSecondaryUseActive() && this.canSitOnBack()) {
					if(player instanceof ServerPlayer serverPlayer && this.setEntityOnBack(serverPlayer))
						return InteractionResult.SUCCESS_SERVER;
					else
						return InteractionResult.SUCCESS;
				}
				
				InteractionResult result = super.mobInteract(player, hand);
				if (!result.consumesAction()) {
					this.setOrderedToSit(!this.isOrderedToSit());
					this.jumping = false;
					this.setTarget(null);
					return InteractionResult.SUCCESS.withoutItem();
				}
				
				return result;
			}
		} else if (!this.level().isClientSide && this.isFood(stack) && this.getTarget() == null) {
			stack.consume(1, player);
			this.tryToTame(player);
			return InteractionResult.SUCCESS_SERVER;
		}
		
		return super.mobInteract(player, hand);
	}

	@Override
	public boolean canFitOnBack() {
		return this.getType() == EntityType.CAVE_SPIDER;
	}

	@Override
	public boolean canBeLeashed() {
		return this.isTame();
	}

	@Override
	public boolean handleLeashAtDistance(Entity leasher, float distance) {
		if (this.isInSittingPose()) {
			if (distance > 10)
				this.dropLeash();
			return false;
		}
		return super.handleLeashAtDistance(leasher, distance);
	}

	@Override
	public void handleEntityEvent(byte event) {
		if (event == 7) {
			this.spawnTamingParticles(true);
		} else if (event == 6) {
			this.spawnTamingParticles(false);
		} else if (event == 65) {
			this.breakArmor();
		} else {
			super.handleEntityEvent(event);
		}
	}

    @Override
    protected void actuallyHurt(ServerLevel server, DamageSource source, float damage) {
        if (!this.canArmorAbsorb(source)) {
            super.actuallyHurt(server, source, damage);
        } else {
            this.doHurtArmor(source, damage);
        }
    }

	private void breakArmor() {
		ItemStack armorStack = this.getArmorItem();
		if (!armorStack.isEmpty()) {
			if (!this.isSilent()) {
				this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), armorStack.getBreakingSound(), this.getSoundSource(), 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F, false);
			}
			this.spawnItemParticles(armorStack, 5);
			this.setArmorItem(ItemStack.EMPTY);
		}
	}

	@Override
	public boolean canUseSlot(EquipmentSlot slot) {
		return true;
	}

	@Override
	public int getAmbientSoundInterval() {
		return this.isTame() ? 120 : super.getAmbientSoundInterval();
	}

	@Override
	public boolean removeWhenFarAway(double distance) {
		return this.isTame() ? false : super.removeWhenFarAway(distance);
	}

	@Override
	protected boolean shouldDespawnInPeaceful() {
		return false;
	}

	@Override
	public boolean shouldDropExperience() {
		return !this.isTame();
	}

	@Override
	protected boolean shouldDropLoot() {
		return !this.isTame();
	}

	@Override
	public boolean isPreventingPlayerRest(ServerLevel server, Player player) {
		return !this.isTame() || this.getTarget() == player;
	}

	@Override
	public double getAttributeValue(Holder<Attribute> attribute) {
		return this.computeAttribute(attribute);
	}

	private double computeAttribute(Holder<Attribute> attribute) {
		double originalValue = super.getAttributeValue(attribute);
		if (this.getArmorItem().isEmpty())
			return originalValue;
		double newValue = originalValue;
		for (ItemAttributeModifiers.Entry entry : this.getArmorItem().getAttributeModifiers().modifiers()) {
			if (entry.slot().test(EquipmentSlot.BODY) && entry.attribute() == attribute) {
				double value = entry.modifier().amount();
				newValue += switch (entry.modifier().operation()) {
					case ADD_VALUE -> value;
					case ADD_MULTIPLIED_BASE -> value * originalValue;
					case ADD_MULTIPLIED_TOTAL -> value * newValue;
				};
			}
		}
		return newValue;
	}

	@Override
	protected void hurtArmor(DamageSource source, float damage) {
		this.doHurtArmor(source, damage);
	}

	private void doHurtArmor(DamageSource source, float damage) {
		if (damage > 0) {
			ItemStack armorStack = this.getArmorItem();
			Equippable equipment = armorStack.get(DataComponents.EQUIPPABLE);
			if (equipment != null && equipment.damageOnHurt() && armorStack.isDamageableItem() && armorStack.canBeHurtBy(source))
				armorStack.hurtAndBreak((int) Math.max(1, damage / 4), this, EquipmentSlot.BODY);
		}
	}

	private boolean canArmorAbsorb(DamageSource source) {
		return !this.getArmorItem().isEmpty() && !source.is(DamageTypeTags.BYPASSES_ARMOR);
	}

	private boolean isNearGround() {
		double distance = this.distanceToSqr(this.level().clip(new ClipContext(this.position().add(0, 0.5, 0), this.position().add(0, -1, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, this)).getLocation());
		return distance <= 0.05;
	}

	@Override
	public boolean isSuppressingSlidingDownLadder() {
		if (this.isControlledByLocalInstance() && this.getControllingPassenger() instanceof Player rider && !Events.isPlayerSprinting(rider))
			return this.isNearGround();
		return true;
	}

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void tick(CallbackInfo callback) {
		super.tick();
		if (this.isAlive()) {
			this.interestedAngleO = this.interestedAngle;
			if (this.isInterested()) {
				this.interestedAngle = this.interestedAngle + (1 - this.interestedAngle) * 0.4f;
			} else {
				this.interestedAngle = this.interestedAngle + (0 - this.interestedAngle) * 0.4f;
			}
		}
		if (this.isControlledByLocalInstance()) {
			boolean climbing = this.isVehicle() ? !this.level().noCollision(this.getBoundingBox().inflate(0.4, 0, 0.4)) : this.horizontalCollision;
			if (this.spider().isClimbing() != climbing) {
				this.spider().setClimbing(climbing);
				this.sendSyncFix(TamableSpider.SyncType.Climbing);
			}
		}
		callback.cancel();
	}

	@Inject(method = "getHurtSound", at = @At("HEAD"), cancellable = true)
	private void getHurtSound(DamageSource source, CallbackInfoReturnable<SoundEvent> callback) {
		if (this.canArmorAbsorb(source))
			callback.setReturnValue(SoundEvents.WOLF_ARMOR_DAMAGE);
	}

	@Inject(method = "registerGoals", at = @At("TAIL"))
	private void registerGoals(CallbackInfo callback) {
		PanicGoal panic = new PanicGoal(this, 1.5, DamageTypeTags.PANIC_ENVIRONMENTAL_CAUSES) {
			@Override
			public void tick() {
				if (!unableToMoveToOwner() && shouldTryTeleportToOwner())
					tryToTeleportToOwner();
				super.tick();
			}
		};
		GoalManager manager = new GoalManager(this.goalSelector.getAvailableGoals());
		manager.insertGoal(1, false, panic);
		manager.insertGoal(2, true, new MoveToSittingPosGoal(this, 1));
		manager.insertGoal(3, true, new SitWhenOrderedToGoal(this));
		manager.insertGoal(7, true, new AttackTargetInWebGoal(this, 1, true));
		manager.insertGoal(6, true, new FollowOwnerGoal(this, 1, 10, 2));
		manager.insertGoal(8, true, new BegGoal(this, 8));
		manager.replaceGoalsFor(this.goalSelector);

		GoalManager targeting = new GoalManager(this.targetSelector.getAvailableGoals());
		targeting.insertGoal(1, true, new OwnerHurtByTargetGoal(this));
		targeting.insertGoal(2, true, new OwnerHurtTargetGoal(this));
		targeting.replaceGoalsFor(this.targetSelector);
	}

	@Inject(method = "defineSynchedData", at = @At("TAIL"))
	private void defineSynchedData(SynchedEntityData.Builder builder, CallbackInfo callback) {
		builder.define(Tamed, false);
		builder.define(Sitting, false);
		builder.define(Saddled, false);
		builder.define(Interested, false);
		builder.define(CollarColor, -1);
		builder.define(SittingStamp, 0l);
		builder.define(Owner, Optional.empty());
		builder.define(Armor, ItemStack.EMPTY);
	}
}
