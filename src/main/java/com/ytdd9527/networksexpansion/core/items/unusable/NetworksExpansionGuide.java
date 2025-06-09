package com.ytdd9527.networksexpansion.core.items.unusable;

import com.ytdd9527.networksexpansion.core.items.SpecialSlimefunItem;
import com.ytdd9527.networksexpansion.implementation.ExpansionRecipes;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import javax.annotation.Nonnull;

@Deprecated
public class NetworksExpansionGuide extends SpecialSlimefunItem {

    public NetworksExpansionGuide(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item, RecipeType.NULL, ExpansionRecipes.NULL);
    }
}
