package tameable.spiders.procedures;

import tameable.spiders.network.TameableSpidersModVariables;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.TickEvent;

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
		if ((entity.getCapability(TameableSpidersModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new TameableSpidersModVariables.PlayerVariables())).assigning) {
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
