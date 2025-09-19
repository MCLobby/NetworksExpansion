package com.balugaq.netex.api.keybind;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
@FunctionalInterface
@SuppressWarnings("deprecation")
public interface KeybindPredicate {
    boolean test(Player player, int slot, ItemStack itemStack, ClickAction clickAction);
}