package tameable.spiders.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingEvent;

import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

@Mod.EventBusSubscriber
public class EntityTickProcedure {
	@SubscribeEvent
	public static void onEntityTick(LivingEvent.LivingTickEvent event) {
		Entity entity = event.getEntity();
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
