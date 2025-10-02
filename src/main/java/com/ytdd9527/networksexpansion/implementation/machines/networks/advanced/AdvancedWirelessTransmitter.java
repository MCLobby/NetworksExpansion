package com.ytdd9527.networksexpansion.implementation.machines.networks.advanced;

import com.balugaq.netex.api.enums.FeedbackType;
import com.balugaq.netex.utils.Lang;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.core.items.machines.AdvancedDirectional;
import io.github.sefiraat.networks.NetworkStorage;
import io.github.sefiraat.networks.network.NetworkRoot;
import io.github.sefiraat.networks.network.NodeDefinition;
import io.github.sefiraat.networks.network.NodeType;
import io.github.sefiraat.networks.network.stackcaches.ItemRequest;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class AdvancedWirelessTransmitter extends AdvancedDirectional {
    private static final String BS_TARGET_LOCATION = "target";
    private static final int[] TEMPLATE_SLOTS = new int[]{0, 1, 2, 3};
    public AdvancedWirelessTransmitter(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe, NodeType.ADVANCED_WIRELESS_TRANSMITTER);
        addItemHandler(new BlockTicker() {
            @Override
            public void tick(Block b, SlimefunItem item, SlimefunBlockData data) {
                onTick(b, data);
            }

            @Override
            public boolean isSynchronized() {
                return false;
            }
        });
    }

    @Override
    protected int @NotNull [] getBackgroundSlots() {
        return new int[]{4, 5};
    }

    @Override
    public boolean isExceedLimit(int quantity) {
        return quantity < 0 || quantity > 3456;
    }

    @Override
    public int getMaxLimit() {
        return 3456;
    }

    @Override
    protected @Range(from = -1, to = 53) int getMinusSlot() {
        return 6;
    }

    @Override
    protected @Range(from = -1, to = 53) int getCargoNumberSlot() {
        return 7;
    }

    @Override
    protected @Range(from = -1, to = 53) int getAddSlot() {
        return 8;
    }

    @Override
    protected @Range(from = -1, to = 53) int getTransportModeSlot() {
        return -1;
    }

    public void setTargetLocation(Location machine, Player player, Location target) {
        if (machine.getWorld() != target.getWorld()) {
            player.sendMessage(Lang.getString("messages.commands.worldedit.must-select-same-world"));
            return;
        }

        if (!Slimefun.getProtectionManager().hasPermission(player, target, Interaction.INTERACT_BLOCK)) {
            player.sendMessage(Lang.getString("messages.feedback.no_permission"));
            return;
        }

        StorageCacheUtils.setData(machine, BS_TARGET_LOCATION, string(target));
    }

    public static String string(Location target) {
        return target.getBlockX() + ";" + target.getBlockY() + ";" + target.getBlockZ();
    }

    @Nullable
    public Location getTargetLocation(Location machine) {
        String s = StorageCacheUtils.getData(machine, BS_TARGET_LOCATION);
        if (s == null) return null;
        String[] split = s.split(";");
        return new Location(machine.getWorld(), Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
    }

    private void onTick(Block b, SlimefunBlockData data) {
        Location location = b.getLocation();
        Location target = getTargetLocation(location);
        if (target == null) {
            sendFeedback(location, FeedbackType.NO_TARGET_LOCATION);
            return;
        }

        NodeDefinition srcn = NetworkStorage.getNode(location);
        if (srcn == null || srcn.getNode() == null) {
            sendFeedback(location, FeedbackType.NO_NETWORK_FOUND);
            return;
        }
        NodeDefinition tgtn = NetworkStorage.getNode(target);
        if (tgtn == null || tgtn.getNode() == null) {
            sendFeedback(location, FeedbackType.NO_TARGET_NETWORK_FOUND);
            return;
        }
        NetworkRoot src = srcn.getNode().getRoot();
        NetworkRoot tgt = tgtn.getNode().getRoot();
        if (src == tgt) {
            sendFeedback(location, FeedbackType.SAME_NETWORK);
            return;
        }

        BlockMenu menu = StorageCacheUtils.getMenu(location);
        if (menu == null) {
            sendFeedback(location, FeedbackType.NO_MENU);
            return;
        }

        List<ItemStack> templates = new ArrayList<>();
        for (int slot : TEMPLATE_SLOTS) {
            ItemStack template = menu.getItemInSlot(slot);
            if (template != null && template.getType() != Material.AIR) {
                templates.add(template);
            }
        }

        if (templates.isEmpty()) {
            sendFeedback(location, FeedbackType.NO_TEMPLATE_FOUND);
            return;
        }
        for (ItemStack template : templates) {
            ItemStack itemStack = src.getItemStack0(location, new ItemRequest(template, getLimitQuantity(location)));
            if (itemStack != null && itemStack.getAmount() > 0) {
                src.addItemStack(itemStack);
                tgt.addItemStack0(target, itemStack);
            }
        }
        sendFeedback(location, FeedbackType.WORKING);
    }
}
