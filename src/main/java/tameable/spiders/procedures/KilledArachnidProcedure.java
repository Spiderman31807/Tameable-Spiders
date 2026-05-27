package tameable.spiders.procedures;

import tameable.spiders.network.TameableSpidersModVariables;
import tameable.spiders.entity.AgeableSpider;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;

@Mod.EventBusSubscriber
public class KilledArachnidProcedure {
	@SubscribeEvent
	public static void onEntityDeath(LivingDeathEvent event) {
		Entity entity = event.getEntity();
		Entity sourceentity = event.getSource().getEntity();
		if(entity == null || sourceentity == null || entity instanceof AgeableSpider)
			return;
		if(entity instanceof Spider && sourceentity instanceof Player player) {
			TameableSpidersModVariables.PlayerVariables _vars = player.getCapability(TameableSpidersModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new TameableSpidersModVariables.PlayerVariables());
			_vars.spidersKilled++;
			_vars.syncPlayerVariables(player);

			if (player instanceof ServerPlayer serverPlayer) {
				if (_vars.spidersKilled >= 50) {
					Advancement _adv = serverPlayer.server.getAdvancements().getAdvancement(new ResourceLocation("tameable_spiders:pest_control"));
					if (_adv != null) {
						AdvancementProgress _ap = serverPlayer.getAdvancements().getOrStartProgress(_adv);
						if (!_ap.isDone()) {
							for (String criteria : _ap.getRemainingCriteria())
								serverPlayer.getAdvancements().award(_adv, criteria);
						}
					}
				}

				if (_vars.spidersKilled >= 250) {
					Advancement _adv = serverPlayer.server.getAdvancements().getAdvancement(new ResourceLocation("tameable_spiders:exterminator"));
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
