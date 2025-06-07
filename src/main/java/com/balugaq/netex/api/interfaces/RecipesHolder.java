package com.balugaq.netex.api.interfaces;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public interface RecipesHolder {
    @Deprecated
    static @Nonnull Map<ItemStack[], ItemStack> getRecipes() {
        return new HashMap<>();
    }
}
