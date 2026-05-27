
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package tameable.spiders.init;

import tameable.spiders.client.gui.SpiderInventoryScreen;

import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class TameableSpidersModScreens {
	@SubscribeEvent
	public static void clientLoad(RegisterMenuScreensEvent event) {
		event.register(TameableSpidersModMenus.SPIDER_INVENTORY.get(), SpiderInventoryScreen::new);
	}
}
