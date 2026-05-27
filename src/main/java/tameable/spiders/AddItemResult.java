package tameable.spiders;

import net.minecraft.world.item.Item;

public class AddItemResult {
	public final boolean dropped;
	public final boolean failed;
	public final Item replaced;
	public final int slot;

	public AddItemResult(Item replaced, int slot) {
		this.dropped = false;
		this.failed = false;
		this.replaced = replaced;
		this.slot = slot;
	}

	public AddItemResult(int slot) {
		this.dropped = false;
		this.failed = false;
		this.replaced = null;
		this.slot = slot;
	}

	public AddItemResult(boolean dropped) {
		this.dropped = dropped;
		this.failed = !dropped;
		this.replaced = null;
		this.slot = -1;
	}
}