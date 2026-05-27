package tamable.spiders.mixin;

import tamable.spiders.client.renderer.entity.layers.SpiderArmorLayer;
import tamable.spiders.client.renderer.entity.layers.SpiderSaddleLayer;
import tamable.spiders.client.renderer.entity.layers.SpiderCollarLayer;
import tamable.spiders.client.renderer.entity.state.TamableSpiderState;
import tamable.spiders.TamableSpider;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.monster.Spider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
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

	@Inject(method = "createRenderState", at = @At("HEAD"), cancellable = true)
	private void createRenderState(CallbackInfoReturnable<LivingEntityRenderState> callback) {
		callback.setReturnValue(new TamableSpiderState());
	}

	@Inject(method = "extractRenderState", at = @At("TAIL"), cancellable = true)
	private void extractRenderState(Spider spider, LivingEntityRenderState state, float partialTick, CallbackInfo callback) {
		if (spider instanceof TamableSpider tamed && state instanceof TamableSpiderState tameState) {
			tameState.type = spider.getType();
			tameState.sittingAnim = tamed.getSittingAnimation();
			tameState.collarColor = tamed.getCollarDyeColor();
			tameState.armor = tamed.getArmorItem();
			tameState.hasSaddle = tamed.isSaddled();
			tameState.isSitting = tamed.isInSittingPose();
			tameState.tamed = tamed.isTame();
        	tameState.headRollAngle = tamed.getHeadRollAngle(partialTick);
		}
	}
}
