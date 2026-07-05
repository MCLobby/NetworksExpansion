package io.github.sefiraat.networks.network.barrel;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.sefiraat.networks.network.stackcaches.ItemRequest;

public interface BarrelCore {

    @Nullable ItemStack requestItem(@NotNull ItemRequest itemRequest);

    default void depositItemStack(ItemStack itemToDeposit) {
        depositItemStack(new ItemStack[]{itemToDeposit});
    }

    void depositItemStack(ItemStack[] itemsToDeposit);

    int[] getInputSlot();

    int[] getOutputSlot();
}
