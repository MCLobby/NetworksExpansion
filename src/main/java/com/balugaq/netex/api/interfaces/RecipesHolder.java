package com.balugaq.netex.api.interfaces;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import org.bukkit.inventory.ItemStack;

public interface RecipesHolder {
    @Deprecated
    static @Nonnull Map<ItemStack[], ItemStack> getRecipes() {
        return new HashMap<>();
    }
}
