
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package tameable.spiders.init;

import tameable.spiders.world.inventory.SpiderInventoryMenu;
import tameable.spiders.TameableSpidersMod;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.core.registries.Registries;

public class TameableSpidersModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, TameableSpidersMod.MODID);
	public static final DeferredHolder<MenuType<?>, MenuType<SpiderInventoryMenu>> SPIDER_INVENTORY = REGISTRY.register("spider_inventory", () -> IMenuTypeExtension.create(SpiderInventoryMenu::new));
}
