package tameable.spiders.procedures;

import tameable.spiders.network.TameableSpidersModVariables;

import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

import java.util.Calendar;

@Mod.EventBusSubscriber
public class PlayerTickProcedure {
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			execute(event, event.player);
		}
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (entity == null)
			return;
		String ellipses = "";
		double time = 0;
		if (entity.getData(TameableSpidersModVariables.PLAYER_VARIABLES).assigning) {
			time = Math.floor(Calendar.getInstance().get(Calendar.SECOND) % 4);
			if (time == 0) {
				ellipses = "";
			} else if (time == 1) {
				ellipses = ".";
			} else if (time == 2) {
				ellipses = "..";
			} else {
				ellipses = "...";
			}
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal(("Assigning Bed" + ellipses)), true);
		}
	}
}
