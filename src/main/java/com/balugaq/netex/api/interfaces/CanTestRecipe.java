package com.balugaq.netex.api.interfaces;

import javax.annotation.Nonnull;
import org.bukkit.inventory.ItemStack;

public interface CanTestRecipe {
    static boolean testRecipe(@Nonnull ItemStack[] input, @Nonnull ItemStack[] recipe) {
        return false;
    }
}
