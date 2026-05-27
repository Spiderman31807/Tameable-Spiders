package tameable.spiders;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;

import tameable.spiders.enums.Utility;

public class UtilitySlot {
	public Component tooltip = Component.literal("");
	public Utility utility = Utility.NONE;
	public ArrayList<Integer> useableSlots = new ArrayList();
	public ArrayList<Integer> orderSlots = new ArrayList();
	public ArrayList<Integer> replaceableSlots = new ArrayList();
	public boolean replaceable = true;
	public int usedSlot = -1;

	public UtilitySlot(Utility type) {
		this.utility = type;
		this.tooltip = type.getTooltip();
		this.useableSlots = type.useableSlots();
		this.orderSlots = type.slotOrder();
		this.replaceableSlots = type.replaceableSlots();
		this.replaceable = type.isReplaceable();
	}

	public void setUtility(Utility type) {
		if(type == null)
			return;
		this.utility = type;
	}

	public void setSlot(int slot) {
		this.usedSlot = slot;
	}

	public void tooltip(String text) {
		this.tooltip = Component.translatable(text);
	}

	public void order(int first, int second, int third) {
    	this.orderSlots = new ArrayList<>(List.of(first, second, third));
	}

	public void exclude(int... slots) {
		for (int slot : slots) {
			if(useableSlots.contains(slot))
        		useableSlots.remove(slot);
    	}
	}

	public void reverse() {
    	Collections.reverse(orderSlots);
	}

	public void replace(int... slots) {
		for (int slot : slots) {
			if(replaceableSlots.contains(slot))
        		replaceableSlots.add(slot);
    	}
	}

	public void replace(ArrayList<Integer> slots) {
		for (int slot : slots) {
			if(replaceableSlots.contains(slot))
        		replaceableSlots.add(slot);
    	}
	}

	public void replaceable(boolean canReplace) {
		this.replaceable = canReplace;
	}

	public boolean refresh(ArrayList<UtilitySlot> other) {
		int usedSlot = -1;
		ArrayList<Integer> usedSlots = getUsedSlots(other, true);
		ArrayList<Integer> UsedSlots = getUsedSlots(other, false);
		for(int slot : orderSlots) {
			if(!useableSlots.contains(slot))
				continue;
			if((usedSlots.contains(slot) && !replaceableSlots.contains(slot)) || UsedSlots.contains(slot))
				continue;
			replaceSlot(other, slot);
			return true;
		}
		return false;
	}

	public ArrayList<Integer> getUsedSlots(ArrayList<UtilitySlot> other, boolean replaceable) {
		ArrayList<Integer> used = new ArrayList();
		for(UtilitySlot utility : other) {
			if(utility.getSlot() == -1)
				continue;
			if(utility.isReplaceable() == replaceable)
				used.add(utility.getSlot());
		}
		return used;
	}

	public void replaceSlot(ArrayList<UtilitySlot> other, int slot) {
		ArrayList<UtilitySlot> refreshUtility = new ArrayList(other);
		refreshUtility.add(this);
		
		usedSlot = slot;
		/*for(UtilitySlot utility : other) {
			if(utility.getSlot() == slot) {
				refreshUtility.remove(utility);
				utility.refresh(refreshUtility);
				break;
			}
		}*/
	}

	public int getSlot() {
		return this.usedSlot;
	}

	public Utility get() {
		return this.utility;
	}

	public Component getTooltip() {
		return Component.translatable("spider_inventory.tooltip_" + this.tooltip.getString());
	}

	public boolean isReplaceable() {
		return this.replaceable;
	}

	public CompoundTag useableCompound() {
    	CompoundTag compound = new CompoundTag();
    	compound.putBoolean("0", this.useableSlots.contains(0));
    	compound.putBoolean("1", this.useableSlots.contains(1));
    	compound.putBoolean("2", this.useableSlots.contains(2));
    	return compound;
	}

	public CompoundTag replaceableCompound() {
    	CompoundTag compound = new CompoundTag();
    	compound.putBoolean("0", this.replaceableSlots.contains(0));
    	compound.putBoolean("1", this.replaceableSlots.contains(1));
    	compound.putBoolean("2", this.replaceableSlots.contains(2));
    	return compound;
	}

	public CompoundTag orderCompound() {
    	CompoundTag compound = new CompoundTag();
    	compound.putInt("first", this.orderSlots.get(0));
    	compound.putInt("second", this.orderSlots.get(1));
    	compound.putInt("third", this.orderSlots.get(2));
    	return compound;
	}

    public CompoundTag toCompound() {
    	CompoundTag compound = new CompoundTag();
    	compound.putString("tooltip", this.tooltip.getString());
    	compound.put("useable", this.useableCompound());
    	compound.put("order", this.orderCompound());
    	compound.put("canReplace", this.replaceableCompound());
    	compound.putBoolean("replaceable", this.replaceable);
    	compound.putInt("slot", this.usedSlot);
    	compound.putString("type", this.utility.toString());
    	return compound;
    }

    public void fromCompound(CompoundTag compound) {
    	this.tooltip(compound.getString("tooltip"));
    	this.setUtility(Utility.byName(compound.getString("type"), null));
    	this.replaceable(compound.getBoolean("replaceable"));
    	this.usedSlot = compound.getInt("slot");
    	this.useableCompound(compound.getCompound("useable"));
    	this.orderCompound(compound.getCompound("order"));
    	this.replaceableCompound(compound.getCompound("canReplace"));
    }

	public void useableCompound(CompoundTag compound) {
		ArrayList<Integer> useable = new ArrayList();
		if(compound.getBoolean("0"))
			useable.add(0);
		if(compound.getBoolean("1"))
			useable.add(1);
		if(compound.getBoolean("2"))
			useable.add(2);
		this.useableSlots = useable;
	}

	public void replaceableCompound(CompoundTag compound) {
		ArrayList<Integer> canReplace = new ArrayList();
		if(compound.getBoolean("0"))
			canReplace.add(0);
		if(compound.getBoolean("1"))
			canReplace.add(1);
		if(compound.getBoolean("2"))
			canReplace.add(2);
		this.replace(canReplace);
	}

	public void orderCompound(CompoundTag compound) {
		int First = compound.getInt("first");
		int Second = compound.getInt("second");
		int Third = compound.getInt("third");
		this.order(First, Second, Third);
	}
}
