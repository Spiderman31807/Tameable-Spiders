
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package tameable.spiders.init;

import tameable.spiders.client.model.SpiderBack;
import tameable.spiders.client.model.BabyHead;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class TameableSpidersModModels {
	@SubscribeEvent
	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(BabyHead.LAYER_LOCATION, BabyHead::createBodyLayer);
		event.registerLayerDefinition(SpiderBack.LAYER_LOCATION, SpiderBack::createBodyLayer);
	}
}
