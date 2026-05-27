package tameable.spiders.enums;

import java.util.List;
import java.util.ArrayList;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import org.jetbrains.annotations.Contract;
import javax.annotation.Nullable;

import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.network.chat.Component;

public enum Utility implements StringRepresentable {
    NONE(0, "none", null, null, null, false, true),
    SADDLE(1, "saddle", "saddle", null, null, false, true),
    ARMOR(2, "armor", null, null, null, false, true),
    SILK(3, "silk", null, null, null, false, true),
    VENOM(4, "vemon", null, null, null, false, true);

	private static final IntFunction<Utility> BY_ID = ByIdMap.continuous(Utility::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StringRepresentable.EnumCodec<Utility> CODEC = StringRepresentable.fromEnum(Utility::values);
    private final int ID;
    private final String Name;
    private final Component Tooltip;
	private final List<Integer> UseableSlots;
	private final List<Integer> OrderSlots;
	private final List<Integer> ReplaceableSlots;
	private final boolean Replaceable;
    
	private Utility(int id, String name, String tooltip, List<Integer> useable, List<Integer> order, List<Integer> canReplace, boolean replaceable) {
		this.ID = id;
		this.Name = name;
		this.Tooltip = tooltip == null ? Component.literal("") : Component.translatable(tooltip);
		this.UseableSlots = useable == null ? List.of(0, 1, 2) : useable;
		this.OrderSlots = order == null ? List.of(0, 1, 2) : order;
		this.ReplaceableSlots = canReplace == null ? List.of() : canReplace;
		this.Replaceable = replaceable;
	}

	private Utility(int id, String name, String tooltip, List<Integer> useable, List<Integer> order, boolean canReplace, boolean replaceable) {
		this.ID = id;
		this.Name = name;
		this.Tooltip = tooltip == null ? Component.literal("") : Component.translatable(tooltip);
		this.UseableSlots = useable == null ? List.of(0, 1, 2) : useable;
		this.OrderSlots = order == null ? List.of(0, 1, 2) : order;
		this.ReplaceableSlots = canReplace ? List.of(0, 1, 2) : List.of();
		this.Replaceable = replaceable;
	}

    
    public static Utility byId(int id) {
        return BY_ID.apply(id);
    }

    public int getId() {
        return this.ID;
    }

    public String toString() {
    	return this.Name;
    }

    @Override
    public String getSerializedName() {
        return this.Name;
    }

    @Nullable
    @Contract("_,!null->!null;_,null->_")
    public static Utility byName(String name, @Nullable Utility type) {
        Utility utility = CODEC.byName(name);
        return utility != null ? utility : type;
    }

    public Component getTooltip() {
    	return this.Tooltip;
    }

    public ArrayList<Integer> useableSlots() {
    	return new ArrayList(this.UseableSlots);
    }

    public ArrayList<Integer> slotOrder() {
    	return new ArrayList(this.OrderSlots);
    }

    public ArrayList<Integer> replaceableSlots() {
    	return new ArrayList(this.ReplaceableSlots);
    }

    public boolean isReplaceable() {
    	return this.Replaceable;
    }
}
