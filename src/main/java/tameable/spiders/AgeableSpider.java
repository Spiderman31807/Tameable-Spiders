package tameable.spiders.entity;

import javax.annotation.Nullable;

import net.neoforged.neoforge.event.entity.SpawnPlacementRegisterEvent;

import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.nbt.CompoundTag;

import tameable.spiders.init.SpiderRules;
import tameable.spiders.init.TameableSpidersModEntities;
import tameable.spiders.goal.SpiderAttackGoal;
import tameable.spiders.goal.SpiderTargetGoal;
import tameable.spiders.enums.SpiderVariant;
import tameable.spiders.enums.Utility;
import tameable.spiders.UtilitySlot;
import tameable.spiders.Utilities;

public abstract class AgeableSpider extends Spider {
    private static final EntityDataAccessor<Boolean> IsBaby = SynchedEntityData.defineId(AgeableSpider.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Integer> UtilityTop_Slot = SynchedEntityData.defineId(AgeableSpider.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> UtilityMiddle_Slot = SynchedEntityData.defineId(AgeableSpider.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> UtilityBottom_Slot = SynchedEntityData.defineId(AgeableSpider.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> UtilityTop_Type = SynchedEntityData.defineId(AgeableSpider.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> UtilityMiddle_Type = SynchedEntityData.defineId(AgeableSpider.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> UtilityBottom_Type = SynchedEntityData.defineId(AgeableSpider.class, EntityDataSerializers.INT);
    public static final int BABY_START_AGE = -24000;
    private static final int FORCED_AGE_PARTICLE_TICKS = 40;
    protected int age;
    protected int forcedAge;
    protected int forcedAgeTimer;
    protected SpiderVariant variant;
    public final AnimationState SittingState = new AnimationState();
    public final AnimationState StandingState = new AnimationState();
    public Utilities UtilityData = new Utilities();
    public EntityType Type;

	protected AgeableSpider(EntityType<? extends AgeableSpider> type, Level world) {
        super(type, world);
        this.setVariant(SpiderVariant.byType(type, false));
        this.Type = type;

        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getVariantHealth());
        this.setHealth(this.getMaxHealth());
    }

    @Nullable
    @Override
    public ItemStack getPickResult() {
        return this.getVariant().getEgg();
    }

	@Override
    protected ResourceKey<LootTable> getDefaultLootTable() {
        return this.getVariant().getLoot();
    }

    public void playSound(String sound, SoundSource type) {
		this.level().playSound(null, BlockPos.containing(this.getX(), this.getY(), this.getZ()), BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation(sound)), type, 1, 1);
    }

    public void popItem(ItemStack stack, int pickupDelay) {
    	if(this.level() instanceof ServerLevel server) {
    		ItemEntity item = new ItemEntity(server, this.getX(), this.getY(), this.getZ(), stack);
			item.setPickUpDelay(pickupDelay);
			server.addFreshEntity(item);
			playSound("entity.chicken.egg", SoundSource.NEUTRAL);
    	}
    }

    public boolean isSittingPose() {
    	return this.getPose() == Pose.SITTING;
    }
    
    public AnimationState getState(boolean Sitting) {
    	if(Sitting)
    		return SittingState;
    	return StandingState;
    }

    private void resetAnimations() {
        this.SittingState.stop();
        this.StandingState.stop();
    }

    public ResourceLocation getTexture(int type) {
    	String Texture = "tameable_spiders:textures/entities/";
		if(this.variant != SpiderVariant.BASE)
			Texture += this.variant.toString() + "_";
		
		Texture += switch (type) {
    		case 1 -> "spider_legs";
    		case 2 -> "spider_back";
    		case 3 -> "spider_head";
    		default -> "spider";
		};
		
		return new ResourceLocation(Texture + ".png");
	}

	public float getModelScale() {
		float scale = switch(this.getVariant())
		{
			default -> 1f;
			case SpiderVariant.CAVE -> 0.7f;
		};

		return scale / this.getHitboxScale();
	}

	public float getHitboxScale() {
		float scale = switch(this.getVariant())
		{
			default -> 1f;
			case SpiderVariant.CAVE -> 0.55f;
		};

		return scale;
	}

	public int getInventoryScale() {
		return switch(this.getVariant())
		{
			default -> 30;
			case SpiderVariant.CAVE -> 43;
		};
	}

	public float getVariantHealth() {
		return switch(this.getVariant())
		{
			default -> 16f;
			case SpiderVariant.CAVE -> 12f;
		};
	}

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Armadillo.class, 6.0F, 1.0, 1.2, p_320185_ -> !((Armadillo)p_320185_).isScared()));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, Math.max(0.4f, 0.4f * this.getModelScale())));
        this.goalSelector.addGoal(5, new SpiderAttackGoal(this));
        this.goalSelector.addGoal(9, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(11, new RandomLookAroundGoal(this));
        
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(4, new SpiderTargetGoal<>(this, Player.class));
        this.targetSelector.addGoal(5, new SpiderTargetGoal<>(this, IronGolem.class));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance difficulty, MobSpawnType type, @Nullable SpawnGroupData spawnData) {
        if (spawnData == null) {
            spawnData = new AgeableSpider.AgeableSpiderData(true);
        }

        AgeableSpider.AgeableSpiderData GroupData = (AgeableSpider.AgeableSpiderData)spawnData;
        if (GroupData.isShouldSpawnBaby()
            && GroupData.getGroupSize() > 0
            && accessor.getRandom().nextFloat() <= GroupData.getBabySpawnChance()) {
            this.setAge(-24000);
        }

        GroupData.increaseGroupSizeByOne();
        return super.finalizeSpawn(accessor, difficulty, type, spawnData);
    }

    @Nullable
    public abstract AgeableSpider getBreedOffspring(ServerLevel server, AgeableSpider mob);

    @Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(IsBaby, false);
        builder.define(UtilityTop_Slot, -1);
        builder.define(UtilityMiddle_Slot, -1);
        builder.define(UtilityBottom_Slot, -1);
        builder.define(UtilityTop_Type, 0);
        builder.define(UtilityMiddle_Type, 0);
        builder.define(UtilityBottom_Type, 0);
	}

    @Override
    public boolean doHurtTarget(Entity entity) {
    	if(!this.getVariant().canProduceVenom(this.level()))
    		return super.doHurtTarget(entity);

        if (super.doHurtTarget(entity)) {
            if (entity instanceof LivingEntity target && (!this.isBaby() || this.level().getLevelData().getGameRules().getBoolean(SpiderRules.BABY_POISON)))
            {
                if (this.level().getDifficulty() == Difficulty.NORMAL) {
                    target.addEffect(new MobEffectInstance(MobEffects.POISON, 140, 0), this);
                } else if (this.level().getDifficulty() == Difficulty.HARD) {
                    target.addEffect(new MobEffectInstance(MobEffects.POISON, 300, 0), this);
                }
            }

            return true;
        }
        return false;
    }

    public boolean canBreed() {
        return false;
    }

    public boolean canAttackDuringDay() {
    	return false;
    }

    public boolean canAttackRandom() {
    	return true;
    }

    public SpiderVariant getVariant() {
    	if(this.variant == null)
    		return SpiderVariant.MISSING;
    	return this.variant;
    }

    protected void setVariant(SpiderVariant type) {
    	this.variant = type;
        this.getAttribute(Attributes.SCALE).setBaseValue(this.getHitboxScale());
    }

    public int getAge() {
        if (this.level().isClientSide) {
            return this.entityData.get(IsBaby) ? -1 : 1;
        } else {
            return this.age;
        }
    }

    public void ageUp(int age, boolean forced) {
        int i = this.getAge();
        i += age * 20;
        if (i > 0) {
            i = 0;
        }

        int j = i - i;
        this.setAge(i);
        if (forced) {
            this.forcedAge += j;
            if (this.forcedAgeTimer == 0) {
                this.forcedAgeTimer = 40;
            }
        }

        if (this.getAge() == 0) {
            this.setAge(this.forcedAge);
        }
    }

    public void ageUp(int age) {
        this.ageUp(age, false);
    }

    public void setAge(int age) {
        int i = this.getAge();
        this.age = age;
        if (i < 0 && age >= 0 || i >= 0 && age < 0) {
            this.entityData.set(IsBaby, age < 0);
            this.ageBoundaryReached();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Age", this.getAge());
        compound.putInt("ForcedAge", this.forcedAge);
        compound.put("UtilityData", this.UtilityData.toCompound());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        this.setAge(compound.getInt("Age"));
        this.forcedAge = compound.getInt("ForcedAge");
        this.UtilityData.fromCompound(compound.getCompound("UtilityData"));
		UtilityData.refreshUtilities();
		this.refreshUtilities();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        if (IsBaby.equals(data)) {
            this.refreshDimensions();
        }

        if (this.level().isClientSide() && DATA_POSE.equals(data)) {
            this.resetAnimations();
            Pose pose = this.getPose();
            int animate = this.tickCount;
            	
            switch (pose) {
                case Pose.STANDING:
                    this.StandingState.startIfStopped(animate);
                    break;
                case Pose.SITTING:
                    this.SittingState.startIfStopped(animate);
                    break;
            }
        }

        super.onSyncedDataUpdated(data);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            if (this.forcedAgeTimer > 0) {
                if (this.forcedAgeTimer % 4 == 0) {
                    this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), 0.0, 0.0, 0.0);
                }

                --this.forcedAgeTimer;
            }
        } else if (this.isAlive()) {
            int i = this.getAge();
            if (i < 0) {
                this.setAge(++i);
            } else if (i > 0) {
                this.setAge(--i);
            }
        }
    }

    protected void ageBoundaryReached() {
        if (!this.isBaby() && this.isPassenger()) {
            Entity entity = this.getVehicle();
            if (entity instanceof Boat boat && !boat.hasEnoughSpaceFor(this)) {
                this.stopRiding();
            }
        }
    }

    @Override
    public boolean isBaby() {
        return this.getAge() < 0;
    }

    @Override
    public void setBaby(boolean isBaby) {
        this.setAge(isBaby ? -24000 : 0);
    }

    protected void setupUtilities() {
		if(getVariant().isRideable(this.level()))
    		UtilityData.addUtility(Utility.SADDLE);
		if(getVariant().canArmor())
    		UtilityData.addUtility(Utility.ARMOR);
		if(getVariant().canProduceSilk(this.level()))
    		UtilityData.addUtility(Utility.SILK);
		if(getVariant().canProduceVenom(this.level()))
    		UtilityData.addUtility(Utility.VENOM);

    	UtilityData.refreshUtilities();
		this.refreshUtilities();
    }

    public int getSlot(Utility utility) {
    	return this.UtilityData.getUtilitySlot(utility);
    }

    public void refreshUtilities() {
    	UtilitySlot utilityTop = this.UtilityData.getUtility(0);
    	UtilitySlot utilityMiddle = this.UtilityData.getUtility(1);
    	UtilitySlot utilityBottom = this.UtilityData.getUtility(2);

    	this.entityData.set(UtilityTop_Slot, utilityTop == null ? -1 : utilityTop.getSlot());
    	this.entityData.set(UtilityMiddle_Slot, utilityMiddle == null ? -1 : utilityMiddle.getSlot());
    	this.entityData.set(UtilityBottom_Slot, utilityBottom == null ? -1 : utilityBottom.getSlot());

    	this.entityData.set(UtilityTop_Type, utilityTop == null ? 0 : utilityTop.get().getId());
    	this.entityData.set(UtilityMiddle_Type, utilityMiddle == null ? 0 : utilityMiddle.get().getId());
    	this.entityData.set(UtilityBottom_Type, utilityBottom == null ? 0 : utilityBottom.get().getId());
    }

    public Utility getUtility(int type) {
    	type = Math.clamp(type, 0, 2);
    	return switch(type) {
    		default -> Utility.byId(this.entityData.get(UtilityTop_Type));
    		case 1 -> Utility.byId(this.entityData.get(UtilityMiddle_Type));
    		case 2 -> Utility.byId(this.entityData.get(UtilityBottom_Type));
    	};
    }

    public int getUtilitySlot(int type) {
    	type = Math.clamp(type, 0, 2);
    	return switch(type) {
    		default -> this.entityData.get(UtilityTop_Slot);
    		case 1 -> this.entityData.get(UtilityMiddle_Slot);
    		case 2 -> this.entityData.get(UtilityBottom_Slot);
    	};
    }

    public boolean hasUtility(Utility type) {
    	int check = type.getId();
    	if(this.entityData.get(UtilityTop_Type) == check)
    		return true;
    	if(this.entityData.get(UtilityMiddle_Type) == check)
    		return true;
    	if(this.entityData.get(UtilityBottom_Type) == check)
    		return true;
    		
    	return false;
    }

    public static int getSpeedUpSecondsWhenFeeding(int p_216968_) {
        return (int)((float)(p_216968_ / 20) * 0.1F);
    }

    public static class AgeableSpiderData implements SpawnGroupData {
        private int groupSize;
        private final boolean shouldSpawnBaby;
        private final float babySpawnChance;

        private AgeableSpiderData(boolean isBaby, float babyChance) {
            this.shouldSpawnBaby = isBaby;
            this.babySpawnChance = babyChance;
        }

        public AgeableSpiderData(boolean isBaby) {
            this(isBaby, 0.05F);
        }

        public AgeableSpiderData(float babyChance) {
            this(true, babyChance);
        }

        public int getGroupSize() {
            return this.groupSize;
        }

        public void increaseGroupSizeByOne() {
            ++this.groupSize;
        }

        public boolean isShouldSpawnBaby() {
            return this.shouldSpawnBaby;
        }

        public float getBabySpawnChance() {
            return this.babySpawnChance;
        }
    }

	public static void init(SpawnPlacementRegisterEvent event) {
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Spider.createAttributes();
	}
}