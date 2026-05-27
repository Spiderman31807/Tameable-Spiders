package tamable.spiders;

import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public interface SpiderData {
	static final UUID spoopieUUID = UUID.fromString("83d10885-da30-431c-a570-5d55e3e16758");
	static final UUID sanityUUID = UUID.fromString("56abe31d-a310-4984-973a-b727919ff12b");

	static boolean vaildUUID(UUID uuid) {
		return uuid.equals(SpiderData.spoopieUUID) || uuid.equals(SpiderData.sanityUUID);
	}

	default Player getPlayer() {
		return (Player) this;
	}

	default boolean isSpider() {
		return vaildUUID(getPlayer().getUUID());
	}

	default void pressKey(int key) {
		if (!this.isSpider())
			return;

		switch (key) {
			case 1 :
				this.setClimbMode(-2);
				break;
			case 2 :
				this.setClimbMode(-1);
				break;
			case 3 :
				this.setClimbMode(0);
				break;
			case 4 :
				this.setClimbMode(1);
				break;
			case 5 :
				this.setClimbMode(2);
				break;
			case 6 :
				this.cycleClimbMode(true);
				break;
			case 7 :
				this.cycleClimbMode(false);
				break;
		}
	}

	default boolean isClimbing() {
		return getPlayer().getData(CustomData.Climbing);
	}

	default void setClimbing(boolean climbing) {
		getPlayer().setData(CustomData.Climbing, climbing);
	}

	default int getClimbMode() {
		if(getPlayer().isLocalPlayer())
			return ClimbMode.get();
		return 1;
	}

	default void setClimbMode(int mode) {
		if(getPlayer().isLocalPlayer())
			ClimbMode.set(mode);
	}

	default void cycleClimbMode(boolean foward) {
		int mode = this.getClimbMode();
		mode += foward ? 1 : -1;
		if (foward ? (mode > 2) : (mode < -2))
			mode = foward ? -2 : 2;
		this.setClimbMode(mode);
	}

	abstract void preClimbTick();

	abstract void postClimbTick();
}