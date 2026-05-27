package tameable.spiders.procedures;

import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

@EventBusSubscriber
public class EntityTickProcedure {
	@SubscribeEvent
	public static void onEntityTick(EntityTickEvent.Pre event) {
		execute(event, event.getEntity());
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (entity == null)
			return;
			
		CompoundTag data = entity.getPersistentData();
		CompoundTag speedData = new CompoundTag();
		Vec3 Pos = new Vec3(entity.getX(), entity.getY(), entity.getZ());
		if(data.contains("SpeedData")) {
			CompoundTag SpeedData = data.getCompound("SpeedData");
			Vec3 pastPos = new Vec3(SpeedData.getDouble("X"), SpeedData.getDouble("Y"), SpeedData.getDouble("Z"));
			float Distance = (float)pastPos.distanceTo(Pos);
			speedData.putFloat("speed", Distance);
		}

		speedData.putDouble("X", entity.getX());
		speedData.putDouble("Y", entity.getY());
		speedData.putDouble("Z", entity.getZ());
		data.put("SpeedData", speedData);

	}
}
