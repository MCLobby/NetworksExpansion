package com.ytdd9527.networksexpansion.implementation.machines.encoders;

import com.balugaq.netex.api.helpers.SupportedJuicerRecipes;
import com.ytdd9527.networksexpansion.core.items.machines.AbstractEncoder;
import com.ytdd9527.networksexpansion.implementation.blueprints.JuicerBlueprint;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import java.util.Map;
import java.util.Set;
import org.bukkit.inventory.ItemStack;

public class JuicerEncoder extends AbstractEncoder {

    public JuicerEncoder(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    public void blueprintSetter(ItemStack itemStack, ItemStack[] inputs, ItemStack crafted) {
        JuicerBlueprint.setBlueprint(itemStack, inputs, crafted);
    }

    public boolean isValidBlueprint(ItemStack blueprint) {
        return SlimefunItem.getByItem(blueprint) instanceof JuicerBlueprint;
    }

    public Set<Map.Entry<ItemStack[], ItemStack>> getRecipeEntries() {
        return SupportedJuicerRecipes.getRecipes().entrySet();
    }

    public boolean getRecipeTester(ItemStack[] inputs, ItemStack[] recipe) {
        return SupportedJuicerRecipes.testRecipe(inputs, recipe);
    }
}
