package com.ytdd9527.networksexpansion.implementation.machines.cargo.transfer.point.basic;

import com.balugaq.netex.api.atrributes.WhitelistedVanillaGrabber;
import com.balugaq.netex.api.enums.FeedbackType;
import com.balugaq.netex.api.enums.TransferType;
import com.balugaq.netex.api.factories.TransferConfigFactory;
import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.api.interfaces.SoftCellBannable;
import com.balugaq.netex.api.transfer.TransferConfiguration;
import com.bgsoftware.wildchests.api.WildChestsAPI;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.slimefun.network.NetworkDirectional;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class WhitelistedTransferVanillaGrabber extends NetworkDirectional implements SoftCellBannable, WhitelistedVanillaGrabber {
    private static final TransferConfiguration config = TransferConfigFactory
        .getTransferConfiguration(TransferType.WHITELISTED_TRANSFER_VANILLA_GRABBER);

    public WhitelistedTransferVanillaGrabber(
        @NotNull ItemGroup itemGroup,
        @NotNull SlimefunItemStack item,
        @NotNull RecipeType recipeType,
        ItemStack @NotNull [] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.GRABBER);
        for (int slot : getItemSlots()) {
            this.getSlotsToDrop().add(slot);
        }
    }

    @Override
    protected void onTick(@Nullable BlockMenu blockMenu, @NotNull Block block) {
        super.onTick(blockMenu, block);
        if (blockMenu != null) {
            tryGrabItem(blockMenu);
        }
    }

    @SuppressWarnings("removal")
    private void tryGrabItem(@NotNull BlockMenu blockMenu) {
        final NodeDefinition definition = NetworkStorage.getNode(blockMenu.getLocation());

        if (definition == null || definition.getNode() == null) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_NETWORK_FOUND);
            return;
        }

        final NetworkRoot root = definition.getNode().getRoot();
        if (checkSoftCellBan(blockMenu.getLocation(), root)) {
            return;
        }

        final BlockFace direction = getCurrentDirection(blockMenu);

        // Fix for early vanilla pusher release
        final Block block = blockMenu.getBlock();
        final String ownerUUID = StorageCacheUtils.getData(block.getLocation(), OWNER_KEY);
        if (ownerUUID == null) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_OWNER_FOUND);
            return;
        }
        final UUID uuid = UUID.fromString(ownerUUID);
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        // dirty fix
        Block targetBlock = block.getRelative(direction);

        try {
            if (!Slimefun.getProtectionManager()
                .hasPermission(offlinePlayer, targetBlock, Interaction.INTERACT_BLOCK)) {
                sendFeedback(blockMenu.getLocation(), FeedbackType.NO_PERMISSION);
                return;
            }
        } catch (NullPointerException ex) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.ERROR_OCCURRED);
            return;
        }

        final BlockState blockState = targetBlock.getState();

        if (!(blockState instanceof InventoryHolder holder)) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.NO_INVENTORY_FOUND);
            return;
        }

        boolean wildChests = Networks.getSupportedPluginManager().isWildChests();
        boolean isChest = wildChests && WildChestsAPI.getChest(targetBlock.getLocation()) != null;

        if (wildChests && isChest) {
            sendFeedback(blockMenu.getLocation(), FeedbackType.PROTECTED_BLOCK);
            return;
        }

        final Inventory inventory = holder.getInventory();

        grabInventory(blockMenu, inventory, root, getClonedTemplateItems(blockMenu));

        sendFeedback(blockMenu.getLocation(), FeedbackType.WORKING);
    }

    @Override
    protected Particle.@NotNull DustOptions getDustOptions() {
        return new Particle.DustOptions(Color.FUCHSIA, 1);
    }

    @Override
    public int getNorthSlot() {
        return config.getNorthSlot();
    }

    @Override
    public int getSouthSlot() {
        return config.getSouthSlot();
    }

    @Override
    public int getEastSlot() {
        return config.getEastSlot();
    }

    @Override
    public int getWestSlot() {
        return config.getWestSlot();
    }

    @Override
    public int getUpSlot() {
        return config.getUpSlot();
    }

    @Override
    public int getDownSlot() {
        return config.getDownSlot();
    }

    @Nullable
    @Override
    protected ItemStack getOtherBackgroundStack() {
        return Icon.GRABBER_TEMPLATE_BACKGROUND_STACK;
    }

    @Override
    public int @NotNull [] getBackgroundSlots() {
        return config.getBackgroundSlots();
    }

    @Override
    public int @NotNull [] getOtherBackgroundSlots() {
        return config.getTemplateBackgroundSlots();
    }

    @Override
    public int @NotNull [] getItemSlots() {
        return config.getTemplateSlots();
    }

    @Override
    public boolean runSync() {
        return true;
    }

    @Override
    public int[] getTemplateSlots() {
        return config.getTemplateSlots();
    }
}
