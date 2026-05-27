package tameable.spiders.enums;

import java.util.function.IntFunction;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ByIdMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceKey;

import tameable.spiders.init.SpiderRules;
import tameable.spiders.init.TameableSpidersModEntities;

public enum SpiderVariant {
    MISSING(0, "spider"),
    VANILLA_BASE(1, "spider"),
    VANILLA_CAVE(2, "cave_spider"),
    BASE(3, 8, true, "", "base", true, false),
    CAVE(4, 4, false, "", "cave", true, true);

	private static final IntFunction<SpiderVariant> BY_ID = ByIdMap.continuous(SpiderVariant::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    private final int ID;
    private final String Name;
    private final boolean produceSilk;
    private final boolean produceVenom;
    private final int inventorySize;
    private final boolean canRide;
    private final List<Item> vaildArmor;

    private SpiderVariant(int id, String name) {
    	this(id, 0, false, "", name, false, false);
    }
    
    private SpiderVariant(int id, int inventory, boolean rideable, String armorItems, String name, boolean silk, boolean venom) {
    	this.ID = id;
    	this.Name = name;
    	this.produceSilk = silk;
    	this.produceVenom = venom;
    	this.inventorySize = inventory;
    	this.canRide = rideable;
    	this.vaildArmor = getArmorItems(armorItems);
    }
    
    public static SpiderVariant byId(int id) {
        return BY_ID.apply(id);
    }

    public int getId() {
        return this.ID;
    }

    public static SpiderVariant byType(EntityType type, boolean replacing) {
        if(type == EntityType.SPIDER)
        	return replacing ? SpiderVariant.BASE : SpiderVariant.VANILLA_BASE;
        if(type == EntityType.CAVE_SPIDER)
        	return replacing ? SpiderVariant.CAVE : SpiderVariant.VANILLA_CAVE;
        if(type == TameableSpidersModEntities.SPIDER.get())
        	return SpiderVariant.BASE;
        if(type == TameableSpidersModEntities.CAVE_SPIDER.get())
        	return SpiderVariant.CAVE;
        return SpiderVariant.MISSING;
    }

    public EntityType getType() {
        return switch(this.ID) {
        	default -> EntityType.SPIDER;
        	case 2 -> EntityType.CAVE_SPIDER;
        	case 3 -> TameableSpidersModEntities.SPIDER.get();
        	case 4 -> TameableSpidersModEntities.CAVE_SPIDER.get();
        };
    }

    public ResourceKey<LootTable> getLoot() {
        EntityType Table = switch(this.ID) {
        	default -> EntityType.SPIDER;
        	case 2 -> EntityType.CAVE_SPIDER;
        	case 4 -> EntityType.CAVE_SPIDER;
        };

        return Table.getDefaultLootTable();
    }

    public ItemStack getEgg() {
        Item egg = switch(this.ID) {
        	default -> Items.SPIDER_SPAWN_EGG;
        	case 2 -> Items.CAVE_SPIDER_SPAWN_EGG;
        	case 4 -> Items.CAVE_SPIDER_SPAWN_EGG;
        };

        return new ItemStack(egg);
    }


    public String toString() {
    	return this.Name;
    }

    public String getDisplayName() {
    	return switch(this.Name)
    	{
    		default -> "Spider";
    		case "cave" -> "Cave Spider";
    	};
    }

    public int inventorySize() {
    	return this.inventorySize;
    }

    public boolean isRideable(Level world) {
    	return this.canRide && world.getLevelData().getGameRules().getBoolean(SpiderRules.RIDABLE);
    }

    public boolean canProduceSilk(Level world) {
    	return this.produceSilk && world.getLevelData().getGameRules().getBoolean(SpiderRules.PRODUCE_SILK);
    }

    public boolean canProduceVenom(Level world) {
    	return this.produceVenom && world.getLevelData().getGameRules().getBoolean(SpiderRules.PRODUCE_VENOM);
    }

    public boolean canArmor() {
    	return this.vaildArmor.size() > 0;
    }

    public List<Item> getArmorItems() {
    	return this.vaildArmor;
    }

    public List<Item> getArmorItems(String type) {
    	return switch(type) {
    		default -> new ArrayList<>();
    	};
    }
}