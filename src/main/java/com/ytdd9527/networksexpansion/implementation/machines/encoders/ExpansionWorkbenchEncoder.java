package com.ytdd9527.networksexpansion.implementation.machines.encoders;

import com.balugaq.netex.api.helpers.SupportedExpansionWorkbenchRecipes;
import com.ytdd9527.networksexpansion.core.items.machines.AbstractEncoder;
import com.ytdd9527.networksexpansion.implementation.blueprints.ExpansionWorkbenchBlueprint;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import java.util.Map;
import java.util.Set;
import org.bukkit.inventory.ItemStack;

public class ExpansionWorkbenchEncoder extends AbstractEncoder {

    public ExpansionWorkbenchEncoder(
            ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    public void blueprintSetter(ItemStack itemStack, ItemStack[] inputs, ItemStack crafted) {
        ExpansionWorkbenchBlueprint.setBlueprint(itemStack, inputs, crafted);
    }

    public boolean isValidBlueprint(ItemStack blueprint) {
        return SlimefunItem.getByItem(blueprint) instanceof ExpansionWorkbenchBlueprint;
    }

    public Set<Map.Entry<ItemStack[], ItemStack>> getRecipeEntries() {
        return SupportedExpansionWorkbenchRecipes.getRecipes().entrySet();
    }

    public boolean getRecipeTester(ItemStack[] inputs, ItemStack[] recipe) {
        return SupportedExpansionWorkbenchRecipes.testRecipe(inputs, recipe);
    }
}
