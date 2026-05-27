package tamable.spiders.mixin;

import tamable.spiders.client.renderer.entity.layers.SpiderSaddleLayer;
import tamable.spiders.client.renderer.entity.layers.SpiderCollarLayer;
import tamable.spiders.client.renderer.entity.layers.SpiderArmorLayer;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelLayerLocation;

@Mixin(SpiderRenderer.class)
public abstract class SpiderRendererMixin {
	@Inject(method = "(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Lnet/minecraft/client/model/geom/ModelLayerLocation;)V", at = @At("TAIL"))
	private void constructor(EntityRendererProvider.Context context, ModelLayerLocation modelLayer, CallbackInfo info) {
		SpiderRenderer instance = (SpiderRenderer) (Object) this;
		instance.addLayer(new SpiderArmorLayer(instance, context.getModelSet()));
		instance.addLayer(new SpiderSaddleLayer(instance, context.getModelSet()));
		instance.addLayer(new SpiderCollarLayer(instance));
	}
}