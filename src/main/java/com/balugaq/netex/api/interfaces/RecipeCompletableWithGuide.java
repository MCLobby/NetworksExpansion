package com.balugaq.netex.api.interfaces;

import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.core.listeners.JEGCompatibleListener;
import com.balugaq.netex.utils.BlockMenuUtil;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.stackcaches.ItemRequest;
import io.github.sefiraat.networks.utils.StackUtils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface RecipeCompletableWithGuide {
    default void addJEGButton(@Nonnull BlockMenu blockMenu, @Range(from = 0, to = 53) int slot) {
        if (Networks.getSupportedPluginManager().isJustEnoughGuide()) {
            blockMenu.replaceExistingItem(slot, Icon.JEG_BUTTON);
            blockMenu.addMenuClickHandler(slot, (player, slot1, item, action) -> {
                openGuide(blockMenu, player);
                return false;
            });
        }
    }
    default void openGuide(@Nonnull BlockMenu blockMenu, @Nonnull Player player) {
        JEGCompatibleListener.addCallback(player.getUniqueId(), ((event, profile) -> {
            NodeDefinition definition = NetworkStorage.getNode(blockMenu.getLocation());
            if (definition == null || definition.getNode() == null) {
                return;
            }

            completeRecipeWithGuide(blockMenu, definition.getNode().getRoot(), event);
        }));
    }

    default void completeRecipeWithGuide(@Nonnull BlockMenu blockMenu, @Nonnull NetworkRoot root, GuideEvents.ItemButtonClickEvent event) {
        Player player = event.getPlayer();

        ItemStack clickedItem = event.getClickedItem();
        if (clickedItem == null) {
            return;
        }

        // choices.size() must be 9
        List<RecipeChoice> choices = getRecipe(clickedItem);
        if (choices == null) {
            return;
        }

        for (int i = 0; i < 9; i++) {
            if (i >= choices.size()) {
                break;
            }

            if (i >= getIngredientSlots().length) {
                break;
            }

            RecipeChoice choice = choices.get(i);
            if (choice == null) {
                continue;
            }

            ItemStack existing = blockMenu.getItemInSlot(getIngredientSlots()[i]);
            if (!choice.test(existing)) {
                continue;
            }

            if (choice instanceof RecipeChoice.MaterialChoice materialChoice) {
                List<ItemStack> itemStacks = materialChoice.getChoices().stream().map(ItemStack::new).toList();
                for (ItemStack itemStack : itemStacks) {
                    ItemStack received = getItemStack(root, player, itemStack);
                    if (received != null) {
                        BlockMenuUtil.pushItem(blockMenu, received, getIngredientSlots()[i]);
                    }
                }
            } else if (choice instanceof RecipeChoice.ExactChoice exactChoice) {
                for (ItemStack itemStack : exactChoice.getChoices()) {
                    ItemStack received = getItemStack(root, player, itemStack);
                    if (received != null) {
                        BlockMenuUtil.pushItem(blockMenu, received, getIngredientSlots()[i]);
                    }
                }
            }
        }
    }

    int[] getIngredientSlots();

    @Nullable
    default List<RecipeChoice> getRecipe(@Nonnull ItemStack itemStack) {
        SlimefunItem sf = SlimefunItem.getByItem(itemStack);
        if (sf != null) {
            List<RecipeChoice> raw = new ArrayList<>(Arrays.stream(sf.getRecipe()).map(item -> (RecipeChoice) new RecipeChoice.ExactChoice(item)).toList());
            if (raw.size() < 9) {
                for (int i = raw.size(); i < 9; i++) {
                    raw.add(null);
                }
            }

            return raw;
        } else {
            List<Recipe> recipes = Bukkit.getRecipesFor(itemStack);
            for (Recipe recipe : recipes) {
                if (recipe instanceof ShapedRecipe shapedRecipe) {
                    List<RecipeChoice> choices = new ArrayList<>(9);
                    String[] shape = shapedRecipe.getShape();

                    for (int i = 0; i < 3; i++) {
                        String line = i < shape.length ? shape[i] : "   ";
                        for (int j = 0; j < 3; j++) {
                            if (j >= line.length()) {
                                choices.add(null);
                            } else {
                                choices.add(shapedRecipe.getChoiceMap().get(line.charAt(j)));
                            }
                        }
                    }

                    if (choices.size() < 9) {
                        for (int i = choices.size(); i < 9; i++) {
                            choices.add(null);
                        }
                    }

                    return choices;
                } else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
                    List<RecipeChoice> raw = new ArrayList<>(shapelessRecipe.getChoiceList());
                    if (raw.size() < 9) {
                        for (int i = raw.size(); i < 9; i++) {
                            raw.add(null);
                        }
                    }

                    return raw;
                }
            }
        }

        return null;
    }

    @Nullable
    default ItemStack getItemStack(@Nonnull NetworkRoot root, @Nonnull Player player, @Nonnull ItemStack itemStack) {
        // get from root
        ItemStack item = root.getItemStack0(player.getLocation(), new ItemRequest(itemStack, 1));
        if (item != null) {
            return item;
        }

        // get from player inventory
        for (ItemStack itemStack1 : player.getInventory().getContents()) {
            if (itemStack1 != null && itemStack1.getType() != Material.AIR) {
                if (StackUtils.itemsMatch(itemStack1, itemStack, true, false)) {
                    itemStack1.setAmount(itemStack1.getAmount() - 1);
                    return itemStack1;
                }
            }
        }

        return null;
    }
}
