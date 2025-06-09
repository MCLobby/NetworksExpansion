package com.ytdd9527.networksexpansion.implementation.machines.encoders;

import com.balugaq.netex.api.helpers.SupportedCompressorRecipes;
import com.ytdd9527.networksexpansion.core.items.machines.AbstractEncoder;
import com.ytdd9527.networksexpansion.implementation.blueprints.CompressorBlueprint;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import java.util.Map;
import java.util.Set;
import org.bukkit.inventory.ItemStack;

public class CompressorEncoder extends AbstractEncoder {

    public CompressorEncoder(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    public void blueprintSetter(ItemStack itemStack, ItemStack[] inputs, ItemStack crafted) {
        CompressorBlueprint.setBlueprint(itemStack, inputs, crafted);
    }

    public boolean isValidBlueprint(ItemStack blueprint) {
        return SlimefunItem.getByItem(blueprint) instanceof CompressorBlueprint;
    }

    public Set<Map.Entry<ItemStack[], ItemStack>> getRecipeEntries() {
        return SupportedCompressorRecipes.getRecipes().entrySet();
    }

    public boolean getRecipeTester(ItemStack[] inputs, ItemStack[] recipe) {
        return SupportedCompressorRecipes.testRecipe(inputs, recipe);
    }
}
