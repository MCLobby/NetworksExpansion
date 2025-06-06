package com.balugaq.netex.api.interfaces;

import com.balugaq.jeg.api.objects.events.GuideEvents;
import io.github.sefiraat.networks.network.NetworkRoot;

import javax.annotation.Nonnull;

public interface RecipeCompletableWithGuide {
    default void completeRecipeWithGuide(@Nonnull NetworkRoot root, GuideEvents.ItemButtonClickEvent event) {
        // todo: get item from root / player inventory
    }

    int[] getIngredientSlots();
}
