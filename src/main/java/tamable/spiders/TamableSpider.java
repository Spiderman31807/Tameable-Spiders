package tamable.spiders;

import tamable.spiders.world.level.block.WebPathBlock;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.common.CommonHooks;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.util.ByIdMap;
import net.minecraft.tags.ItemTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.AnimationChannel;

import java.util.function.IntFunction;
import java.util.UUID;
import java.util.Optional;

public interface TamableSpider extends OwnableEntity, Saddleable {
	static final Logger logger = LoggerFactory.getLogger(TamableSpider.class);

	default Spider spider() {
		return (Spider) this;
	}

	abstract boolean isTame();

	abstract void setTame(boolean tamed);

	abstract int getCollarColor();

	abstract void setCollarColor(int color);

	default DyeColor getCollarDyeColor() {
		int colorID = this.getCollarColor();
		return colorID < 0 ? null : DyeColor.byId(colorID);
	}

	default void setCollarColor(DyeColor color) {
		this.setCollarColor(color.getId());
	}

	abstract void setSaddled(boolean saddled);

	abstract ItemStack getArmorItem();

	abstract void setArmorItem(ItemStack stack);

	abstract boolean isOrderedToSit();

	abstract void setOrderedToSit(boolean sit);

	abstract boolean isInSittingPose();

	abstract void setInSittingPose(boolean sitting);

	abstract long sittingStamp();

	abstract void stampSittingChange(long stamp);

	default long getSittingChangeTime() {
		return this.spider().level().getGameTime() - Math.abs(this.sittingStamp());
	}

	abstract void setOwnerUUID(UUID uuid);

	default boolean isFood(ItemStack stack) {
		return stack.is(ItemTags.WOLF_FOOD);
	}

	abstract boolean isInterested();

	abstract void setIsInterested(boolean intrested);

	abstract float getHeadRollAngle(float partialTick);

	abstract Optional<Vec3> getLastSittingPos();

	abstract void setLastSittingPos(Optional<Vec3> pos);

	abstract LivingEntity getDefendTarget();

	abstract void setDefendTarget(LivingEntity target);

	default void defendWebFrom(LivingEntity entity) {
		if (entity == null)
			return;
		Spider spider = this.spider();
		if (!spider.canAttack(entity))
			return;
		if (!this.shouldDefendWebFrom(entity))
			return;
		if (this.getDefendTarget() instanceof LivingEntity oldTarget && spider.distanceToSqr(oldTarget) < spider.distanceToSqr(entity))
			return;
		LivingChangeTargetEvent targetEvent = CommonHooks.onLivingChangeTarget(spider, entity, LivingChangeTargetEvent.LivingTargetType.MOB_TARGET);
		if (!targetEvent.isCanceled())
			this.setDefendTarget(targetEvent.getNewAboutToBeSetTarget());
	}

	default boolean shouldDefendWebFrom(LivingEntity entity) {
		if (entity instanceof Creeper)
			return false;
		if (entity instanceof Ghast)
			return false;
		if (!this.isTame())
			return true;
		if (entity instanceof Player)
			return false;
		if (entity instanceof OwnableEntity ownable && ownable.getOwner() != null)
			return false;
		return true;
	}

	default void usePlayerItem(Player player, InteractionHand hand, ItemStack stack) {
		stack.consume(1, player);
	}

	default AnimationState getSittingAnimation() {
		AnimationState animation = new AnimationState();
		animation.startIfStopped(this.spider().tickCount - (int) this.getSittingChangeTime());
		return animation;
	}

	default boolean isInsideWeb() {
		AABB box = this.spider().getBoundingBox();
		int minX = Mth.floor(box.minX);
		int minY = Mth.floor(box.minY);
		int minZ = Mth.floor(box.minZ);
		int maxX = Mth.ceil(box.maxX);
		int maxY = Mth.ceil(box.maxY);
		int maxZ = Mth.ceil(box.maxZ);
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				for (int z = minZ; z <= maxZ; z++) {
					if (this.spider().level().getBlockState(new BlockPos(x, y, z)).getBlock() instanceof WebPathBlock)
						return true;
				}
			}
		}
		return false;
	}

	default void spawnTamingParticles(boolean success) {
		ParticleOptions particleoptions = success ? ParticleTypes.HEART : ParticleTypes.SMOKE;
		Spider spider = this.spider();
		RandomSource random = spider.getRandom();
		for (int count = 0; count < 7; count++) {
			double impluseX = random.nextGaussian() * 0.02;
			double impluseY = random.nextGaussian() * 0.02;
			double impluseZ = random.nextGaussian() * 0.02;
			spider.level().addParticle(particleoptions, spider.getRandomX(1), spider.getRandomY() + 0.5, spider.getRandomZ(1), impluseX, impluseY, impluseZ);
		}
	}

	default void tryToTame(Player player) {
		Spider spider = this.spider();
		if (spider.level().isClientSide)
			return;
		if (spider.getRandom().nextInt(3) == 0) {
			this.tame(player);
			spider.getNavigation().stop();
			spider.setTarget(null);
			this.setOrderedToSit(true);
			spider.level().broadcastEntityEvent(spider, (byte) 7);
		} else {
			spider.level().broadcastEntityEvent(spider, (byte) 6);
		}
	}

	default void setTame(boolean tamed, boolean shouldUpdate) {
		this.setTame(tamed);
		if (shouldUpdate)
			this.applyTamingSideEffects();
	}

	default void applyTamingSideEffects() {
	}

	default void tame(Player player) {
		this.setTame(true, true);
		this.setOwnerUUID(player.getUUID());
		this.setCollarColor(DyeColor.RED);
	}

	default boolean isOwnedBy(LivingEntity entity) {
		return entity == this.getOwner();
	}

	default boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
		return true;
	}

	default void tryToTeleportToOwner() {
		if (this.getOwner() instanceof LivingEntity owner)
			this.teleportToAroundBlockPos(owner.blockPosition());
	}

	default boolean shouldTryTeleportToOwner() {
		return this.getOwner() instanceof LivingEntity owner && this.spider().distanceToSqr(owner) >= 144;
	}

	default void teleportToAroundBlockPos(BlockPos pos) {
		RandomSource random = this.spider().getRandom();
		for (int attempt = 0; attempt < 10; attempt++) {
			int offsetX = random.nextIntBetweenInclusive(-3, 3);
			int offsetZ = random.nextIntBetweenInclusive(-3, 3);
			if (Math.abs(offsetX) >= 2 || Math.abs(offsetZ) >= 2) {
				int offsetY = random.nextIntBetweenInclusive(-1, 1);
				if (this.maybeTeleportTo(pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ))
					return;
			}
		}
	}

	default boolean maybeTeleportTo(int x, int y, int z) {
		if (!this.canTeleportTo(new BlockPos(x, y, z)))
			return false;
		Spider spider = this.spider();
		spider.moveTo((double) x + 0.5, (double) y, (double) z + 0.5, spider.getYRot(), spider.getXRot());
		spider.getNavigation().stop();
		return true;
	}

	default boolean canTeleportTo(BlockPos pos) {
		Spider spider = this.spider();
		PathType path = WalkNodeEvaluator.getPathTypeStatic(spider, pos);
		if (path != PathType.WALKABLE)
			return false;
		BlockState state = spider.level().getBlockState(pos.below());
		if (!this.canFlyToOwner() && state.getBlock() instanceof LeavesBlock)
			return false;
		BlockPos movement = pos.subtract(spider.blockPosition());
		return spider.level().noCollision(spider, spider.getBoundingBox().move(movement).inflate(0.1, 0, 0.1));
	}

	default boolean unableToMoveToOwner() {
		Spider spider = this.spider();
		return this.isOrderedToSit() || spider.isPassenger() || spider.mayBeLeashed() || this.getOwner() != null && this.getOwner().isSpectator();
	}

	default boolean canFlyToOwner() {
		return false;
	}

	default void sendSyncFix(SyncType type) {
		if (!this.spider().level().isClientSide || !this.spider().isControlledByLocalInstance())
			return;
		CompoundTag data = new CompoundTag();
		if (type != SyncType.Owner || this.getOwnerUUID() != null) {
			switch (type) {
				case Climbing -> data.putBoolean("climbing", this.spider().isClimbing());
				case Sitting -> data.putBoolean("sitting", this.isInSittingPose());
				case Tamed -> data.putBoolean("tamed", this.isTame());
				case Saddled -> data.putBoolean("saddled", this.isSaddled());
				case Interested -> data.putBoolean("interested", this.isInterested());
				case CollarColor -> data.putInt("color", this.getCollarColor());
				case SittingStamp -> data.putLong("stamp", this.sittingStamp());
				case Owner -> data.putUUID("uuid", this.getOwnerUUID());
				case Armor -> data.put("item", this.getArmorItem().save(this.spider().registryAccess()));
			};
		}
		data.putInt("type", type.getId());
		ServerSyncFixPacket packet = new ServerSyncFixPacket(this.spider().getId(), data);
		PacketDistributor.sendToServer(packet);
	}

	default void handleSyncFix(CompoundTag data) {
		if (data == null || data.isEmpty() || !data.contains("type"))
			return;
		switch (SyncType.ById.apply(data.getInt("type"))) {
			case Climbing -> this.spider().setClimbing(data.getBoolean("climbing"));
			case Sitting -> this.setInSittingPose(data.getBoolean("sitting"));
			case Tamed -> this.setTame(data.getBoolean("tamed"));
			case Saddled -> this.setSaddled(data.getBoolean("saddled"));
			case Interested -> this.setIsInterested(data.getBoolean("interested"));
			case CollarColor -> this.setCollarColor(data.getInt("color"));
			case SittingStamp -> this.stampSittingChange(data.getLong("stamp"));
			case Owner -> this.setOwnerUUID(data.hasUUID("uuid") ? data.getUUID("uuid") : null);
			case Armor -> this.setArmorItem(ItemStack.parse(this.spider().registryAccess(), data.getCompound("item")).orElse(ItemStack.EMPTY));
		};
	}

	public static enum SyncType {
		Climbing, Sitting, Tamed, Saddled, Interested, CollarColor, SittingStamp, Owner, Armor;

		public static final IntFunction<SyncType> ById = ByIdMap.continuous(SyncType::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
		public final int id;

		private SyncType() {
			this.id = this.ordinal();
		}

		public int getId() {
			return this.id;
		}
	}
}