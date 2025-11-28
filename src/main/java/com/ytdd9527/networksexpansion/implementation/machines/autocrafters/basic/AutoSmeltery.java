package com.ytdd9527.networksexpansion.implementation.machines.autocrafters.basic;

import com.balugaq.netex.api.enums.CraftType;
import com.ytdd9527.networksexpansion.core.items.machines.AbstractAutoCrafter;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AutoSmeltery extends AbstractAutoCrafter {
    public AutoSmeltery(
        @NotNull ItemGroup itemGroup,
        @NotNull SlimefunItemStack item,
        @NotNull RecipeType recipeType,
        ItemStack @NotNull [] recipe,
        int chargePerCraft,
        boolean withholding) {
        super(itemGroup, item, recipeType, recipe, chargePerCraft, withholding);
    }

    @Override
    public @NotNull CraftType craftType() {
        return CraftType.SMELTERY;
    }
}
