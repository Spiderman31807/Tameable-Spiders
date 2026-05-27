package tamable.spiders;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public record ServerSyncFixPacket(int spiderId, CompoundTag data) implements CustomPacketPayload {
	public static final Type<ServerSyncFixPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TamableSpidersMod.MODID, "climb_sync"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ServerSyncFixPacket> STREAM_CODEC = StreamCodec.of((RegistryFriendlyByteBuf buffer, ServerSyncFixPacket packet) -> {
		buffer.writeInt(packet.spiderId);
		buffer.writeNbt(packet.data);
	}, (RegistryFriendlyByteBuf buffer) -> new ServerSyncFixPacket(buffer.readInt(), buffer.readNbt()));

	@Override
	public Type<ServerSyncFixPacket> type() {
		return TYPE;
	}

	public static void handleData(final ServerSyncFixPacket packet, final IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player().level().getEntity(packet.spiderId) instanceof TamableSpider spider)
				spider.handleSyncFix(packet.data);
		}).exceptionally(e -> {
			context.connection().disconnect(Component.literal(e.getMessage()));
			return null;
		});
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		TamableSpidersMod.addNetworkMessage(ServerSyncFixPacket.TYPE, ServerSyncFixPacket.STREAM_CODEC, ServerSyncFixPacket::handleData);
	}
}