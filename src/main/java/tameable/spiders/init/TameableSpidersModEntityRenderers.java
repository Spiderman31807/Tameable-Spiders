
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package tameable.spiders.init;

import tameable.spiders.client.renderer.SpiderRenderer;
import tameable.spiders.client.renderer.CaveSpiderRenderer;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class TameableSpidersModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(TameableSpidersModEntities.SPIDER.get(), SpiderRenderer::new);
		event.registerEntityRenderer(TameableSpidersModEntities.CAVE_SPIDER.get(), CaveSpiderRenderer::new);
	}
}
