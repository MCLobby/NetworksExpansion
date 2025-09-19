package com.balugaq.netex.api.keybind;

import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface Keybindable {
    default ItemStack icon() {
        return getItem();
    }

    ItemStack getItem();
}
