package tameable.spiders;

import java.util.ArrayList;

import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;

import tameable.spiders.enums.Utility;
import tameable.spiders.UtilitySlot;

public class Utilities {
	public ArrayList<UtilitySlot> Utilities = new ArrayList();
	
	public Utilities() {
	}

	public int size() {
		return this.Utilities.size();
	}

	public boolean hasUtility(Utility utility) {
		UtilitySlot utilityData = this.getUtility(utility);
		if(utilityData != null)
			 return true;
		return false;
	}

	public boolean addUtility(Utility utility) {
		return this.Utilities.add(new UtilitySlot(utility));
	}

	public boolean removeUtility(Utility utility) {
		UtilitySlot utilityData = this.getUtility(utility);
		if(utilityData != null)
			 return this.Utilities.remove(utilityData);
		return false;
	}

	public void refreshUtility(Utility utility) {
		UtilitySlot utilityData = this.getUtility(utility);
		if(utilityData != null)
			 utilityData.refresh(new ArrayList(this.Utilities));
	}

	public UtilitySlot getUtility(Utility utilityCheck) {
    	ArrayList<UtilitySlot> utilities = new ArrayList(this.Utilities);
		for(UtilitySlot utility : utilities) {
			if(utility.get() == utilityCheck)
				return utility;
		}
		return null;
	}

	public int getUtilitySlot(Utility utility) {
		UtilitySlot utilityData = this.getUtility(utility);
		if(utilityData != null)
			 return utilityData.getSlot();
		return -1;
	}

	public Component getUtilityTooltip(Utility utility) {
		UtilitySlot utilityData = this.getUtility(utility);
		if(utilityData != null)
			 return utilityData.getTooltip();
		return Component.literal("");
	}

    public void refreshUtilities() {
    	ArrayList<UtilitySlot> utilities = new ArrayList(this.Utilities);
    	ArrayList<UtilitySlot> using = new ArrayList();
    	for(UtilitySlot utility : utilities) {
    		if(utility.refresh(using))
    			using.add(utility);
    	}
    }

    public UtilitySlot getUtility(int slot) {
    	ArrayList<UtilitySlot> utilities = new ArrayList(this.Utilities);
		for(UtilitySlot utility : utilities) {
			if(utility.getSlot() == slot)
				return utility;
		}
		return null;
    }

    public CompoundTag toCompound() {
    	CompoundTag compound = new CompoundTag();
    	compound.putInt("Size", this.size());
    	
    	int index = 0;
    	ArrayList<UtilitySlot> utilities = new ArrayList(this.Utilities);
    	for(UtilitySlot utility : utilities) {
    		compound.put("Utility" + index, utility.toCompound());
    		index++;
    	}
    	
    	return compound;
    }

    public void fromCompound(CompoundTag compound) {
    	this.Utilities = new ArrayList();
    	for(int idx = 0; idx < compound.getInt("Size"); ++idx) {
    		UtilitySlot utility = new UtilitySlot(Utility.NONE);
    		utility.fromCompound(compound.getCompound("Utility" + idx));
    		this.Utilities.add(utility);
    	}
    }
}