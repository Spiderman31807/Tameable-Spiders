
package tameable.spiders.network;

import java.util.function.Supplier;
import java.util.HashMap;
import java.util.UUID;

import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;

import tameable.spiders.TameableSpidersMod;
import tameable.spiders.world.inventory.SpiderInventoryMenu;
import tameable.spiders.entity.AgeableSpider;
import tameable.spiders.init.SpiderRules;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public record SpiderInventoryButtonMessage(int buttonID, int x, int y, int z) implements CustomPacketPayload {

	public static final ResourceLocation ID = new ResourceLocation(TameableSpidersMod.MODID, "spider_inventory_buttons");
	public SpiderInventoryButtonMessage(FriendlyByteBuf buffer) {
		this(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt());
	}

	@Override
	public void write(final FriendlyByteBuf buffer) {
		buffer.writeInt(buttonID);
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	public static void handleData(final SpiderInventoryButtonMessage message, final PlayPayloadContext context) {
		if (context.flow() == PacketFlow.SERVERBOUND) {
			context.workHandler().submitAsync(() -> {
				Player entity = context.player().get();
				int buttonID = message.buttonID;
				int x = message.x;
				int y = message.y;
				int z = message.z;
				handleButtonAction(entity, buttonID, x, y, z);
			}).exceptionally(e -> {
				context.packetHandler().disconnect(Component.literal(e.getMessage()));
				return null;
			});
		}
	}

	public static void handleButtonAction(Player entity, int buttonID, int x, int y, int z) {
		Level world = entity.level();
		HashMap guistate = SpiderInventoryMenu.guistate;
		if (!world.hasChunkAt(new BlockPos(x, y, z)))
			return;
			
		if (buttonID == 0 && entity.level().getLevelData().getGameRules().getBoolean(SpiderRules.RIDABLE)) {
			if(world instanceof ServerLevel server)
			{
				TameableSpidersModVariables.PlayerVariables _vars = entity.getData(TameableSpidersModVariables.PLAYER_VARIABLES);
				UUID uuid = UUID.fromString(_vars.boundEntity);
			
				AgeableSpider spider = (AgeableSpider)server.getEntity(uuid);
				if (spider == null)
					return;
				if (!spider.isBaby())
					entity.startRiding(spider);
			}
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		TameableSpidersMod.addNetworkMessage(SpiderInventoryButtonMessage.ID, SpiderInventoryButtonMessage::new, SpiderInventoryButtonMessage::handleData);
	}
}