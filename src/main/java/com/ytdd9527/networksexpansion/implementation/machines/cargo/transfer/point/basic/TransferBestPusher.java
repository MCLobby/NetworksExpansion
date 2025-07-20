package com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.point.basic;

import com.balugaq.netex.api.enums.TransferType;
import com.balugaq.netex.api.interfaces.PushTickOnly;
import com.balugaq.netex.api.interfaces.SoftCellBannable;
import com.ytdd9527.networksexpansion.core.items.machines.AbstractTransfer;
import io.github.sefiraat.networks.network.NodeType;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TransferBestPusher extends AbstractTransfer implements SoftCellBannable, PushTickOnly {
    public TransferBestPusher(
        @NotNull ItemGroup itemGroup,
        @NotNull SlimefunItemStack item,
        @NotNull RecipeType recipeType,
        ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.TRANSFER_PUSHER);
    }

    @Override
    public @NotNull TransferType getTransferType() {
        return TransferType.TRANSFER_BEST_PUSHER;
    }
}
