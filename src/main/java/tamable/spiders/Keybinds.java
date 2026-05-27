package tamable.spiders;

import org.lwjgl.glfw.GLFW;

import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;

@EventBusSubscriber(modid = "tamable_spiders", value = {Dist.CLIENT})
public class Keybinds {
	public static final KeyMapping Negitive = new KeyMapping("spider_players.keybind1", GLFW.GLFW_KEY_UNKNOWN, "key.categories.spider") {
		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDown && Minecraft.getInstance().player instanceof SpiderData data)
				data.pressKey(1);
		}
	};
	public static final KeyMapping Down = new KeyMapping("spider_players.keybind2", GLFW.GLFW_KEY_UNKNOWN, "key.categories.spider") {
		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDown && Minecraft.getInstance().player instanceof SpiderData data)
				data.pressKey(2);
		}
	};
	public static final KeyMapping Neutral = new KeyMapping("spider_players.keybind3", GLFW.GLFW_KEY_UNKNOWN, "key.categories.spider") {
		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDown && Minecraft.getInstance().player instanceof SpiderData data)
				data.pressKey(3);
		}
	};
	public static final KeyMapping Vanilla = new KeyMapping("spider_players.keybind4", GLFW.GLFW_KEY_UNKNOWN, "key.categories.spider") {
		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDown && Minecraft.getInstance().player instanceof SpiderData data)
				data.pressKey(4);
		}
	};
	public static final KeyMapping Up = new KeyMapping("spider_players.keybind5", GLFW.GLFW_KEY_UNKNOWN, "key.categories.spider") {
		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDown && Minecraft.getInstance().player instanceof SpiderData data)
				data.pressKey(5);
		}
	};
	public static final KeyMapping CycleForward = new KeyMapping("spider_players.keybind6", GLFW.GLFW_KEY_UNKNOWN, "key.categories.spider") {
		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDown && Minecraft.getInstance().player instanceof SpiderData data)
				data.pressKey(6);
		}
	};
	public static final KeyMapping CycleBackward = new KeyMapping("spider_players.keybind7", GLFW.GLFW_KEY_UNKNOWN, "key.categories.spider") {
		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDown && Minecraft.getInstance().player instanceof SpiderData data)
				data.pressKey(7);
		}
	};
	private static boolean consumeClicks = false;

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		if (!SpiderData.vaildUUID(Minecraft.getInstance().getUser().getProfileId()))
			return;
		consumeClicks = true;
		event.register(Negitive);
		event.register(Down);
		event.register(Neutral);
		event.register(Vanilla);
		event.register(Up);
		event.register(CycleForward);
		event.register(CycleBackward);
	}

	@EventBusSubscriber({Dist.CLIENT})
	public static class KeyEventListener {
		@SubscribeEvent
		public static void onClientTick(ClientTickEvent.Post event) {
			if (consumeClicks && Minecraft.getInstance().screen == null) {
				Negitive.consumeClick();
				Down.consumeClick();
				Neutral.consumeClick();
				Vanilla.consumeClick();
				Up.consumeClick();
				CycleForward.consumeClick();
				CycleBackward.consumeClick();
			}
		}
	}
}