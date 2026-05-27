package tamable.spiders.mixin;

import tamable.spiders.client.renderer.entity.layers.SpiderOnBackLayer;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin {
	@Inject(method = "<init>", at = @At("TAIL"))
	private void constructor(EntityRendererProvider.Context context, boolean flag, CallbackInfo callback) {
		if ((Object) this instanceof PlayerRenderer renderer)
			renderer.addLayer(new SpiderOnBackLayer(renderer, context.getModelSet()));
	}
}