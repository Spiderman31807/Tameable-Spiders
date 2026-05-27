package tameable.spiders.network;

import tameable.spiders.TameableSpidersMod;

import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.HolderLookup;

import java.util.function.Supplier;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class TameableSpidersModVariables {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, TameableSpidersMod.MODID);
	public static final Supplier<AttachmentType<PlayerVariables>> PLAYER_VARIABLES = ATTACHMENT_TYPES.register("player_variables", () -> AttachmentType.serializable(() -> new PlayerVariables()).build());

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		TameableSpidersMod.addNetworkMessage(PlayerVariablesSyncMessage.TYPE, PlayerVariablesSyncMessage.STREAM_CODEC, PlayerVariablesSyncMessage::handleData);
	}

	@EventBusSubscriber
	public static class EventBusVariableHandlers {
		@SubscribeEvent
		public static void onPlayerLoggedInSyncPlayerVariables(PlayerEvent.PlayerLoggedInEvent event) {
			if (event.getEntity() instanceof ServerPlayer player)
				player.getData(PLAYER_VARIABLES).syncPlayerVariables(event.getEntity());
		}

		@SubscribeEvent
		public static void onPlayerRespawnedSyncPlayerVariables(PlayerEvent.PlayerRespawnEvent event) {
			if (event.getEntity() instanceof ServerPlayer player)
				player.getData(PLAYER_VARIABLES).syncPlayerVariables(event.getEntity());
		}

		@SubscribeEvent
		public static void onPlayerChangedDimensionSyncPlayerVariables(PlayerEvent.PlayerChangedDimensionEvent event) {
			if (event.getEntity() instanceof ServerPlayer player)
				player.getData(PLAYER_VARIABLES).syncPlayerVariables(event.getEntity());
		}

		@SubscribeEvent
		public static void clonePlayer(PlayerEvent.Clone event) {
			PlayerVariables original = event.getOriginal().getData(PLAYER_VARIABLES);
			PlayerVariables clone = new PlayerVariables();
			clone.spidersKilled = original.spidersKilled;
			clone.localGuideProgress = original.localGuideProgress;
			if (!event.isWasDeath()) {
				clone.boundEntity = original.boundEntity;
				clone.assignBed = original.assignBed;
				clone.assigning = original.assigning;
				clone.assignX = original.assignX;
				clone.assignY = original.assignY;
				clone.assignZ = original.assignZ;
			}
			event.getEntity().setData(PLAYER_VARIABLES, clone);
		}
	}

	public static class PlayerVariables implements INBTSerializable<CompoundTag> {
		public String boundEntity = "";
		public double spidersKilled = 0;
		public double localGuideProgress = 0;
		public BlockState assignBed = Blocks.AIR.defaultBlockState();
		public boolean assigning = false;
		public double assignX = 0;
		public double assignY = 0;
		public double assignZ = 0;

		@Override
		public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider) {
			CompoundTag nbt = new CompoundTag();
			nbt.putString("boundEntity", boundEntity);
			nbt.putDouble("spidersKilled", spidersKilled);
			nbt.putDouble("localGuideProgress", localGuideProgress);
			nbt.put("assignBed", NbtUtils.writeBlockState(assignBed));
			nbt.putBoolean("assigning", assigning);
			nbt.putDouble("assignX", assignX);
			nbt.putDouble("assignY", assignY);
			nbt.putDouble("assignZ", assignZ);
			return nbt;
		}

		@Override
		public void deserializeNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
			boundEntity = nbt.getString("boundEntity");
			spidersKilled = nbt.getDouble("spidersKilled");
			localGuideProgress = nbt.getDouble("localGuideProgress");
			assignBed = NbtUtils.readBlockState(lookupProvider.lookupOrThrow(BuiltInRegistries.BLOCK.key()), nbt.getCompound("assignBed"));
			assigning = nbt.getBoolean("assigning");
			assignX = nbt.getDouble("assignX");
			assignY = nbt.getDouble("assignY");
			assignZ = nbt.getDouble("assignZ");
		}

		public void syncPlayerVariables(Entity entity) {
			if (entity instanceof ServerPlayer serverPlayer)
				PacketDistributor.sendToPlayer(serverPlayer, new PlayerVariablesSyncMessage(this));
		}
	}

	public record PlayerVariablesSyncMessage(PlayerVariables data) implements CustomPacketPayload {
		public static final Type<PlayerVariablesSyncMessage> TYPE = new Type<>(new ResourceLocation(TameableSpidersMod.MODID, "player_variables_sync"));
		public static final StreamCodec<RegistryFriendlyByteBuf, PlayerVariablesSyncMessage> STREAM_CODEC = StreamCodec
				.of((RegistryFriendlyByteBuf buffer, PlayerVariablesSyncMessage message) -> buffer.writeNbt(message.data().serializeNBT(buffer.registryAccess())), (RegistryFriendlyByteBuf buffer) -> {
					PlayerVariablesSyncMessage message = new PlayerVariablesSyncMessage(new PlayerVariables());
					message.data.deserializeNBT(buffer.registryAccess(), buffer.readNbt());
					return message;
				});

		@Override
		public Type<PlayerVariablesSyncMessage> type() {
			return TYPE;
		}

		public static void handleData(final PlayerVariablesSyncMessage message, final IPayloadContext context) {
			if (context.flow() == PacketFlow.CLIENTBOUND && message.data != null) {
				context.enqueueWork(() -> context.player().getData(PLAYER_VARIABLES).deserializeNBT(context.player().registryAccess(), message.data.serializeNBT(context.player().registryAccess()))).exceptionally(e -> {
					context.connection().disconnect(Component.literal(e.getMessage()));
					return null;
				});
			}
		}
	}
}
