package tamable.spiders.mixin;

import tamable.spiders.SpiderData;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.Entity;

@Mixin(Entity.class)
public abstract class EntityMixin {
	@Inject(method = "baseTick", at = @At("TAIL"))
	private void baseTick(CallbackInfo callback) {
		if (this instanceof SpiderData spider && spider.isSpider())
			spider.preClimbTick();
	}
}