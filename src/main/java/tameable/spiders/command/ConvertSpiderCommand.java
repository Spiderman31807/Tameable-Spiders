
package tameable.spiders.command;

import org.checkerframework.checker.units.qual.s;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.RegisterCommandsEvent;

import net.minecraft.tags.TagKey;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.Commands;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;

import tameable.spiders.init.SpiderRules;
import tameable.spiders.init.TameableSpidersModEntities;
import tameable.spiders.entity.ModdedSpider;
import tameable.spiders.entity.AgeableSpider;
import tameable.spiders.enums.SpiderVariant;

@Mod.EventBusSubscriber
public class ConvertSpiderCommand {
	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("tameable").requires(s -> s.hasPermission(2)).then(Commands.literal("convert").then(Commands.literal("spider").then(Commands.argument("target", EntityArgument.entity()).executes(arguments -> {
			Level world = arguments.getSource().getUnsidedLevel();
			double x = arguments.getSource().getPosition().x();
			double y = arguments.getSource().getPosition().y();
			double z = arguments.getSource().getPosition().z();
			Entity target = EntityArgument.getEntity(arguments, "target");
			Entity commander = arguments.getSource().getEntity();
			Player displayMessage = (Player) commander;

			if(target instanceof AgeableSpider) {
				displayMessage.displayClientMessage(Component.translatable("tameable_spiders.convert_redundant"), false);
				return 0;
			}

			boolean replaceable = target.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("tameable_spiders:can_replace")));
			if(target == null || !replaceable) {
				displayMessage.displayClientMessage(Component.translatable("tameable_spiders.convert_fail"), false);
				return 0;
			}
			
			EntityType Type = SpiderVariant.byType(target.getType(), true).getType();
			ModdedSpider spider = (ModdedSpider)Type.create(world);

			CompoundTag data = target.saveWithoutId(new CompoundTag());
			target.discard();
			spider.load(data);
			world.addFreshEntity(spider);
			displayMessage.displayClientMessage(Component.translatable("tameable_spiders.convert_success"), false);
			return 1;
		})))));
	}
}
