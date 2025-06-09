package com.ytdd9527.networksexpansion.implementation.machines.autocrafters.basic;

import com.balugaq.netex.api.helpers.SupportedArmorForgeRecipes;
import com.ytdd9527.networksexpansion.core.items.machines.AbstractAutoCrafter;
import com.ytdd9527.networksexpansion.implementation.blueprints.ArmorForgeBlueprint;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import java.util.Map;
import java.util.Set;
import org.bukkit.inventory.ItemStack;

public class AutoArmorForge extends AbstractAutoCrafter {
    public AutoArmorForge(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            int chargePerCraft,
            boolean withholding) {
        super(itemGroup, item, recipeType, recipe, chargePerCraft, withholding);
    }

    public Set<Map.Entry<ItemStack[], ItemStack>> getRecipeEntries() {
        return SupportedArmorForgeRecipes.getRecipes().entrySet();
    }

    public boolean getRecipeTester(ItemStack[] inputs, ItemStack[] recipe) {
        return SupportedArmorForgeRecipes.testRecipe(inputs, recipe);
    }

    public boolean isValidBlueprint(SlimefunItem item) {
        return item instanceof ArmorForgeBlueprint;
    }
}
