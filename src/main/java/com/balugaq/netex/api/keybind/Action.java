package com.balugaq.netex.api.keybind;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
@SuppressWarnings("deprecation")
public interface Action {
    boolean apply(Player player, int slot, ItemStack item, ClickAction action, BlockMenu menu) throws IncompatibleKeybind;
}
