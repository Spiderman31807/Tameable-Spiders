package tamable.spiders.mixin;

import tamable.spiders.TamableSpider;
import tamable.spiders.Events;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.LivingEntity;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Inject(method = "handleOnClimbable", at = @At("RETURN"), cancellable = true)
	private void handleOnClimbable(Vec3 movement, CallbackInfoReturnable<Vec3> callback) {
		movement = callback.getReturnValue();
		if (movement.y() > 0)
			return;

		if (this instanceof TamableSpider tameSpider) {
			Spider spider = tameSpider.spider();
			if (spider.onClimbable() && !spider.isSuppressingSlidingDownLadder())
				callback.setReturnValue(new Vec3(movement.x(), 0, movement.z()));
		}
	}
}