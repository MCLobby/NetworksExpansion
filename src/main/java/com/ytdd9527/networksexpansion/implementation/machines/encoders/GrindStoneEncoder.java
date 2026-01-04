package com.ytdd9527.networksexpansion.implementation.machines.encoders;

import com.balugaq.netex.api.enums.CraftType;
import com.ytdd9527.networksexpansion.core.items.machines.AbstractEncoder;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GrindStoneEncoder extends AbstractEncoder {

    public GrindStoneEncoder(
        @NotNull ItemGroup itemGroup,
        @NotNull SlimefunItemStack item,
        @NotNull RecipeType recipeType,
        ItemStack @NotNull [] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public @NotNull CraftType craftType() {
        return CraftType.GRIND_STONE;
    }
}
