package com.ytdd9527.networksexpansion.implementation.machines.autocrafters.advanced;

import com.balugaq.netex.api.helpers.SupportedAncientAltarRecipes;
import com.ytdd9527.networksexpansion.core.items.machines.AbstractAdvancedAutoCrafter;
import com.ytdd9527.networksexpansion.implementation.blueprints.AncientAltarBlueprint;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class AdvancedAutoAncientAltar extends AbstractAdvancedAutoCrafter {
    public AdvancedAutoAncientAltar(
        @NotNull ItemGroup itemGroup,
        @NotNull SlimefunItemStack item,
        @NotNull RecipeType recipeType,
        ItemStack @NotNull [] recipe,
        int chargePerCraft,
        boolean withholding) {
        super(itemGroup, item, recipeType, recipe, chargePerCraft, withholding);
    }

    public @NotNull Set<Map.Entry<ItemStack[], ItemStack>> getRecipeEntries() {
        return SupportedAncientAltarRecipes.getRecipes().entrySet();
    }

    public boolean getRecipeTester(ItemStack[] inputs, ItemStack @NotNull [] recipe) {
        return SupportedAncientAltarRecipes.testRecipe(inputs, recipe);
    }

    public boolean isValidBlueprint(SlimefunItem item) {
        return item instanceof AncientAltarBlueprint;
    }
}
