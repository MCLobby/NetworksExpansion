package com.balugaq.netex.api.atrributes;

import io.github.sefiraat.networks.network.NetworkRoot;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Material;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface WhitelistedVanillaGrabber extends WhitelistedGrabber {
    default void grabInventory(@NotNull BlockMenu blockMenu, @NotNull Inventory inventory, @NotNull NetworkRoot root, @NotNull List<ItemStack> templates) {
        if (inventory instanceof FurnaceInventory furnaceInventory) {
            final ItemStack furnaceInventoryResult = furnaceInventory.getResult();
            final ItemStack furnaceInventoryFuel = furnaceInventory.getFuel();
            if (furnaceInventoryResult != null && furnaceInventoryResult.getType() != Material.AIR && inTemplates(templates, furnaceInventoryResult)) {
                grabItem(root, blockMenu, furnaceInventoryResult);
            } else if (inTemplates(templates, furnaceInventoryFuel)) {
                grabItem(root, blockMenu, furnaceInventoryFuel);
            }
        } else if (inventory instanceof BrewerInventory brewerInventory) {
            if (inTemplates(templates, brewerInventory.getFuel())) {
                grabItem(root, blockMenu, brewerInventory.getFuel());
                return;
            }

            for (int i = 0; i < 3; i++) {
                final ItemStack stack = brewerInventory.getContents()[i];
                if (inTemplates(templates, stack)) {
                    grabItem(root, blockMenu, stack);
                    break;
                }
            }
        } else {
            for (ItemStack stack : inventory.getContents()) {
                if (inTemplates(templates, stack) && grabItem(root, blockMenu, stack)) {
                    break;
                }
            }
        }
    }

    default boolean grabItem(@NotNull NetworkRoot root, @NotNull BlockMenu blockMenu, @Nullable ItemStack stack) {
        if (stack != null && stack.getType() != Material.AIR) {
            root.addItemStack0(blockMenu.getLocation(), stack);
            return true;
        } else {
            return false;
        }
    }
}
