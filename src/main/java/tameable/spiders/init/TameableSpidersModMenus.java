
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package tameable.spiders.init;

import tameable.spiders.world.inventory.SpiderInventoryMenu;
import tameable.spiders.TameableSpidersMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.extensions.IForgeMenuType;

import net.minecraft.world.inventory.MenuType;

public class TameableSpidersModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, TameableSpidersMod.MODID);
	public static final RegistryObject<MenuType<SpiderInventoryMenu>> SPIDER_INVENTORY = REGISTRY.register("spider_inventory", () -> IForgeMenuType.create(SpiderInventoryMenu::new));
}
