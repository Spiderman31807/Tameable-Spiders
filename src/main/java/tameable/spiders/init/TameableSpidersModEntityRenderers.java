
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package tameable.spiders.init;

import tameable.spiders.client.renderer.SpiderRenderer;
import tameable.spiders.client.renderer.CaveSpiderRenderer;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.api.distmarker.Dist;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class TameableSpidersModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(TameableSpidersModEntities.SPIDER.get(), SpiderRenderer::new);
		event.registerEntityRenderer(TameableSpidersModEntities.CAVE_SPIDER.get(), CaveSpiderRenderer::new);
	}
}
