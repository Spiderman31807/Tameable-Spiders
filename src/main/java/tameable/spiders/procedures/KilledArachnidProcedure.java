package tameable.spiders.procedures;

import tameable.spiders.network.TameableSpidersModVariables;

import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.Event;

import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;

@Mod.EventBusSubscriber
public class KilledArachnidProcedure {
	@SubscribeEvent
	public static void onEntityDeath(LivingDeathEvent event) {
		Entity entity = event.getEntity();
		Entity sourceentity = event.getSource().getEntity();
		if(entity == null || sourceentity == null || ((Spider) entity).getMobType() != MobType.ARTHROPOD)
			return;
		if(entity instanceof Spider && sourceentity instanceof Player player) {
			TameableSpidersModVariables.PlayerVariables _vars = player.getData(TameableSpidersModVariables.PLAYER_VARIABLES);
			_vars.spidersKilled++;
			_vars.syncPlayerVariables(player);

			if (player instanceof ServerPlayer serverPlayer) {
				if (_vars.spidersKilled >= 50) {
					AdvancementHolder _adv = serverPlayer.server.getAdvancements().get(new ResourceLocation("tameable_spiders:pest_control"));
					if (_adv != null) {
						AdvancementProgress _ap = serverPlayer.getAdvancements().getOrStartProgress(_adv);
						if (!_ap.isDone()) {
							for (String criteria : _ap.getRemainingCriteria())
								serverPlayer.getAdvancements().award(_adv, criteria);
						}
					}
				}

				if (_vars.spidersKilled >= 250) {
					AdvancementHolder _adv = serverPlayer.server.getAdvancements().get(new ResourceLocation("tameable_spiders:exterminator"));
					if (_adv != null) {
						AdvancementProgress _ap = serverPlayer.getAdvancements().getOrStartProgress(_adv);
						if (!_ap.isDone()) {
							for (String criteria : _ap.getRemainingCriteria())
								serverPlayer.getAdvancements().award(_adv, criteria);
						}
					}
				}
			}
		}
	}
}