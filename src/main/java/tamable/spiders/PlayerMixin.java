package tamable.spiders.mixin;

import tamable.spiders.SpiderData;
import tamable.spiders.Events;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.neoforged.fml.loading.FMLEnvironment;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.core.BlockPos;

import java.util.UUID;

@Mixin(Player.class)
public abstract class PlayerMixin implements SpiderData {
	private AABB getBoundingBoxForPose(Pose pose) {
		EntityDimensions dimensions = this.getPlayer().getDimensions(pose);
		float f = dimensions.width() / 2.0F;
		Vec3 vec3 = new Vec3(this.getPlayer().getX() - (double) f, this.getPlayer().getY(), this.getPlayer().getZ() - (double) f);
		Vec3 vec31 = new Vec3(this.getPlayer().getX() + (double) f, this.getPlayer().getY() + (double) dimensions.height(), this.getPlayer().getZ() + (double) f);
		return new AABB(vec3, vec31);
	}

	private boolean nearbyWall() {
		AABB aabb = this.getBoundingBoxForPose(this.getPlayer().getPose()).inflate(0.2, 0, 0.2);
		return BlockPos.betweenClosedStream(aabb).anyMatch((pos) -> {
			BlockState blockstate = this.getPlayer().level().getBlockState(pos);
			return !blockstate.isAir() && Shapes.joinIsNotEmpty(blockstate.getCollisionShape(this.getPlayer().level(), pos).move((double) pos.getX(), (double) pos.getY(), (double) pos.getZ()), Shapes.create(aabb), BooleanOp.AND);
		});
	}

	private boolean isJumping() {
		if (FMLEnvironment.dist.isClient())
			return Events.isPlayerJumping((Player) (Object) this);
		return false;
	}

	
	public void preClimbTick() {
		if(this.getPlayer().isInWater() || this.getClimbMode() == -2) {
			if(this.isClimbing())
				this.setClimbing(false);
			return;
		}
	
		if(isJumping()) {
			this.setClimbing(true);
			return;
		}
	
		boolean moving = Math.abs(this.getPlayer().xxa) > 0.1 || Math.abs(this.getPlayer().zza) > 0.1;
		boolean climb =  this.getClimbMode() >= 1 && moving && this.getPlayer().horizontalCollision;
		this.setClimbing(climb ? this.nearbyWall() : false);
	}

	public void postClimbTick() {
		if(this.getPlayer().isInWater())
			return;
	
		int climbMode = this.getClimbMode();
		if (climbMode != -2 && this.nearbyWall()) {
			Vec3 delta = this.getPlayer().getDeltaMovement();
			boolean descend = this.getPlayer().isCrouching() == (climbMode == 2 || climbMode == 0);
			double yDelta = this.isClimbing() ? 0.2 : (descend ? -0.2 : 0);
			delta = new Vec3(delta.x, Math.max(delta.y, yDelta), delta.z);
			if (descend || yDelta >= 0)
				this.getPlayer().resetFallDistance();
			this.getPlayer().setDeltaMovement(delta);
		}
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void tick(CallbackInfo callback) {
		if(this.isSpider())
			this.postClimbTick();
	}
}