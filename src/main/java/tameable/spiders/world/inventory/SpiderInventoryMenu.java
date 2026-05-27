
package tameable.spiders.world.inventory;

import java.util.function.Supplier;
import java.util.Map;
import java.util.HashMap;

import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.capabilities.Capabilities;

import net.minecraft.world.Container;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

import tameable.spiders.init.TameableSpidersModMenus;
import tameable.spiders.network.TameableSpidersModVariables;
import tameable.spiders.entity.RideableSpider;
import tameable.spiders.entity.ModdedSpider;
import tameable.spiders.enums.Utility;
import tameable.spiders.UtilitySlot;

public class SpiderInventoryMenu extends AbstractContainerMenu implements Supplier<Map<Integer, Slot>> {
	public final static HashMap<String, Object> guistate = new HashMap<>();
	public final Level world;
	public final Player entity;
	public int x, y, z;
	private ContainerLevelAccess access = ContainerLevelAccess.NULL;
	private IItemHandler internal;
	public final Map<Integer, Slot> customSlots = new HashMap<>();
	private boolean bound = false;
	private Supplier<Boolean> boundItemMatcher = null;
	public Entity boundEntity = null;
	public ModdedSpider spider;
	public UtilitySlot UtilityTop;
	public UtilitySlot UtilityMiddle;
	public UtilitySlot UtilityBottom;
	public int tooltipTop;
	public int tooltipMiddle;
	public int tooltipBottom;

	public SpiderInventoryMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
		super(TameableSpidersModMenus.SPIDER_INVENTORY.get(), id);
		this.entity = inv.player;
		this.world = inv.player.level();
		this.internal = new ItemStackHandler(14);
		BlockPos pos = null;
		
		if (extraData != null) {
			pos = extraData.readBlockPos();
			this.x = pos.getX();
			this.y = pos.getY();
			this.z = pos.getZ();
			access = ContainerLevelAccess.create(world, pos);
		}
		
		if (pos != null) {
			extraData.readByte();
			boundEntity = world.getEntity(extraData.readVarInt());
			if (boundEntity != null) {
				IItemHandler cap = boundEntity.getCapability(Capabilities.ItemHandler.ENTITY);
				if (cap != null) {
					this.internal = cap;
					this.bound = true;
				}
			}
		}
		
		spider = (ModdedSpider)boundEntity;
		TameableSpidersModVariables.PlayerVariables _vars = this.entity.getData(TameableSpidersModVariables.PLAYER_VARIABLES);
		_vars.boundEntity = this.boundEntity.getStringUUID();
		_vars.syncPlayerVariables(entity);

		this.UtilityTop = this.getUtility(0);
		this.UtilityMiddle = this.getUtility(1);
		this.UtilityBottom = this.getUtility(2);
		
		if(UtilityTop.getTooltip() != Component.literal(""))
			this.tooltipTop = this.getTooltipLength(UtilityTop.get());
		if(UtilityMiddle.getTooltip() != Component.literal(""))
			this.tooltipMiddle = this.getTooltipLength(UtilityMiddle.get());
		if(UtilityBottom.getTooltip() != Component.literal(""))
			this.tooltipBottom = this.getTooltipLength(UtilityBottom.get());
		
		this.createInventorySlots();		
		for (int si = 0; si < 3; ++si)
			for (int sj = 0; sj < 9; ++sj)
				this.addSlot(new Slot(inv, sj + (si + 1) * 9, 0 + 8 + sj * 18, 5 + 84 + si * 18));
		for (int si = 0; si < 9; ++si)
			this.addSlot(new Slot(inv, si, 0 + 8 + si * 18, 5 + 142));
	}

	public UtilitySlot getUtility(int type) {
		Utility utility = spider.getUtility(type);
		int slot = spider.getUtilitySlot(type);
		UtilitySlot returnValue = new UtilitySlot(utility);
		returnValue.setSlot(slot);
		return returnValue;
	}

	public int countUtilities() {
		int count = 0;
		if(this.UtilityTop.get() != Utility.NONE)
			count++;
		if(this.UtilityMiddle.get() != Utility.NONE)
			count++;
		if(this.UtilityBottom.get() != Utility.NONE)
			count++;
		return count;
	}

    @Override
	public void broadcastChanges() {
        super.broadcastChanges();
        if(this.hasUtility(Utility.SADDLE))
       		spider.updateSaddle();
    }

	public Vec2 getSlotPlacement(int slot) {
    	int horizontal = 97 + (18 * (slot % 4));
    	int vertical = 22 + (18 * (slot / 4));

		if(slot < 3)
			new Vec2(7, 17 + (18 * slot));
    	return new Vec2(horizontal, vertical);
	}

	public void createInventorySlots() {
		this.createUtilitySlot(UtilityTop);
		this.createUtilitySlot(UtilityMiddle);
		this.createUtilitySlot(UtilityBottom);

		int slots = Math.min(12, Math.max(spider.getInventorySize(false), 0));
		for(int idx = 0; idx < slots; ++idx) {
			Vec2 placement = this.getSlotPlacement(idx);
			this.customSlots.put(idx + 3, this.addSlot(new SlotItemHandler(internal, idx + 3, (int)placement.x + 1, (int)placement.y + 1) {}));
		}
	}

	public void createUtilitySlot(UtilitySlot utility) {
		int slot = utility.getSlot();
		Utility type = utility.get();

	 	int Vertical = 23 + (18 * slot);
		switch(type)
		{
			case SADDLE:
				this.customSlots.put(slot, this.addSlot(new SlotItemHandler(internal, slot, 8, Vertical) {
					@Override
					public boolean mayPlace(ItemStack stack) {
						return stack.getItem() == Items.SADDLE;
					}
				}));
				break;
			case ARMOR:
				this.customSlots.put(slot, this.addSlot(new SlotItemHandler(internal, slot, 8, Vertical) {
					@Override
					public boolean mayPlace(ItemStack stack) {
						return spider.getVariant().getArmorItems().contains(stack.getItem());
					}
				}));
				break;
			case SILK:
				this.customSlots.put(slot, this.addSlot(new SlotItemHandler(internal, slot, 8, Vertical) {
					@Override
					public boolean mayPlace(ItemStack stack) {
						return false;
					}
				}));
				break;
			case VENOM:
				this.customSlots.put(slot, this.addSlot(new SlotItemHandler(internal, slot, 8, Vertical) {
					@Override
					public boolean mayPlace(ItemStack stack) {
						return stack.getItem() == Items.GLASS_BOTTLE;
					}
				}));
				break;
		};
	}

	public int getTooltipLength(Utility utility) {
		return switch(utility) {
			default -> 0;
			case SADDLE -> 188;
		};
	}

	public boolean hasUtility(Utility type) {
		return spider.hasUtility(type);
	}

	@Override
	public boolean stillValid(Player player) {
		return this.boundEntity.isAlive() && this.boundEntity.distanceTo(player) < 8.0F;
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = (Slot) this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (index < 10) {
				if (!this.moveItemStackTo(itemstack1, 10, this.slots.size(), true))
					return ItemStack.EMPTY;
				slot.onQuickCraft(itemstack1, itemstack);
			} else if (!this.moveItemStackTo(itemstack1, 0, 10, false)) {
				if (index < 10 + 27) {
					if (!this.moveItemStackTo(itemstack1, 10 + 27, this.slots.size(), true))
						return ItemStack.EMPTY;
				} else {
					if (!this.moveItemStackTo(itemstack1, 10, 10 + 27, false))
						return ItemStack.EMPTY;
				}
				return ItemStack.EMPTY;
			}
			if (itemstack1.getCount() == 0)
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();
			if (itemstack1.getCount() == itemstack.getCount())
				return ItemStack.EMPTY;
			slot.onTake(playerIn, itemstack1);
		}
		return itemstack;
	}

	@Override
	protected boolean moveItemStackTo(ItemStack p_38904_, int p_38905_, int p_38906_, boolean p_38907_) {
		boolean flag = false;
		int i = p_38905_;
		if (p_38907_) {
			i = p_38906_ - 1;
		}
		if (p_38904_.isStackable()) {
			while (!p_38904_.isEmpty() && (p_38907_ ? i >= p_38905_ : i < p_38906_)) {
				Slot slot = this.slots.get(i);
				ItemStack itemstack = slot.getItem();
				if (slot.mayPlace(itemstack) && !itemstack.isEmpty() && ItemStack.isSameItemSameTags(p_38904_, itemstack)) {
					int j = itemstack.getCount() + p_38904_.getCount();
					int k = slot.getMaxStackSize(itemstack);
					if (j <= k) {
						p_38904_.setCount(0);
						itemstack.setCount(j);
						slot.set(itemstack);
						flag = true;
					} else if (itemstack.getCount() < k) {
						p_38904_.shrink(k - itemstack.getCount());
						itemstack.setCount(k);
						slot.set(itemstack);
						flag = true;
					}
				}
				if (p_38907_) {
					i--;
				} else {
					i++;
				}
			}
		}
		if (!p_38904_.isEmpty()) {
			if (p_38907_) {
				i = p_38906_ - 1;
			} else {
				i = p_38905_;
			}
			while (p_38907_ ? i >= p_38905_ : i < p_38906_) {
				Slot slot1 = this.slots.get(i);
				ItemStack itemstack1 = slot1.getItem();
				if (itemstack1.isEmpty() && slot1.mayPlace(p_38904_)) {
					int l = slot1.getMaxStackSize(p_38904_);
					slot1.setByPlayer(p_38904_.split(Math.min(p_38904_.getCount(), l)));
					slot1.setChanged();
					flag = true;
					break;
				}
				if (p_38907_) {
					i--;
				} else {
					i++;
				}
			}
		}
		return flag;
	}

	public Map<Integer, Slot> get() {
		return customSlots;
	}
}