
package tameable.spiders.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.core.BlockPos;

import tameable.spiders.TameableSpidersMod;
import tameable.spiders.world.inventory.SpiderInventoryMenu;
import tameable.spiders.entity.AgeableSpider;
import tameable.spiders.init.SpiderRules;

import java.util.HashMap;
import java.util.UUID;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public record SpiderInventoryButtonMessage(int buttonID, int x, int y, int z) implements CustomPacketPayload {
	public static final Type<SpiderInventoryButtonMessage> TYPE = new Type<>(new ResourceLocation(TameableSpidersMod.MODID, "spider_inventory_buttons"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SpiderInventoryButtonMessage> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, SpiderInventoryButtonMessage message) -> {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}, (RegistryFriendlyByteBuf buffer) -> new SpiderInventoryButtonMessage(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt()));

	@Override
	public Type<SpiderInventoryButtonMessage> type() {
		return TYPE;
	}

	public static void handleData(final SpiderInventoryButtonMessage message, final IPayloadContext context) {
		if (context.flow() == PacketFlow.SERVERBOUND) {
			context.enqueueWork(() -> {
				Player entity = context.player();
				int buttonID = message.buttonID;
				int x = message.x;
				int y = message.y;
				int z = message.z;
				handleButtonAction(entity, buttonID, x, y, z);
			}).exceptionally(e -> {
				context.connection().disconnect(Component.literal(e.getMessage()));
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
		TameableSpidersMod.addNetworkMessage(SpiderInventoryButtonMessage.TYPE, SpiderInventoryButtonMessage.STREAM_CODEC, SpiderInventoryButtonMessage::handleData);
	}

}