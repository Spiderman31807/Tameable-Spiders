
package tameable.spiders.network;

import java.util.function.Supplier;
import java.util.HashMap;
import java.util.UUID;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

import tameable.spiders.TameableSpidersMod;
import tameable.spiders.world.inventory.SpiderInventoryMenu;
import tameable.spiders.entity.AgeableSpider;
import tameable.spiders.init.SpiderRules;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpiderInventoryButtonMessage {
	private final int buttonID, x, y, z;

	public SpiderInventoryButtonMessage(FriendlyByteBuf buffer) {
		this.buttonID = buffer.readInt();
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
	}

	public SpiderInventoryButtonMessage(int buttonID, int x, int y, int z) {
		this.buttonID = buttonID;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static void buffer(SpiderInventoryButtonMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}

	public static void handler(SpiderInventoryButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			Player entity = context.getSender();
			int buttonID = message.buttonID;
			int x = message.x;
			int y = message.y;
			int z = message.z;
			handleButtonAction(entity, buttonID, x, y, z);
		});
		context.setPacketHandled(true);
	}

	public static void handleButtonAction(Player entity, int buttonID, int x, int y, int z) {
		Level world = entity.level();
		HashMap guistate = SpiderInventoryMenu.guistate;
		
		if (!world.hasChunkAt(new BlockPos(x, y, z)))
			return;
			
		if (buttonID == 0 && entity.level().getLevelData().getGameRules().getBoolean(SpiderRules.RIDABLE)) {
			if(world instanceof ServerLevel server)
			{
				TameableSpidersModVariables.PlayerVariables _vars = entity.getCapability(TameableSpidersModVariables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new TameableSpidersModVariables.PlayerVariables());
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
		TameableSpidersMod.addNetworkMessage(SpiderInventoryButtonMessage.class, SpiderInventoryButtonMessage::buffer, SpiderInventoryButtonMessage::new, SpiderInventoryButtonMessage::handler);
	}
}
