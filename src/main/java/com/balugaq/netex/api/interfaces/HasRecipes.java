package com.balugaq.netex.api.interfaces;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public interface HasRecipes {
    static @NotNull Map<ItemStack[], ItemStack> getRecipes() {
        return new HashMap<>();
    }
}
