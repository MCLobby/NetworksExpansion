package com.balugaq.netex.api.interfaces;

import com.balugaq.netex.api.hanging.HangingPlaceholderBlock;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.sefiraat.networks.utils.Keys;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.HashMap;
import java.util.Map;

public interface HangingBlock {
    Map<String, HangingBlock> REGISTRY = new HashMap<>();

    static void registerHangingBlock(HangingBlock block) {
        REGISTRY.put(block.getId(), block);
    }

    @Contract("null -> null")
    static HangingBlock getHangingBlock(String id) {
        if (id == null) {
            return null;
        }
        return REGISTRY.get(id);
    }

    String HANGING_PLACEHOLDER_BLOCK_ID = "NTW_EXPANSION_HANGING_PLACEHOLDER_BLOCK";
    NamespacedKey NETEX_HANGING_KEY = Keys.newKey("netex_hanging");

    static void tagItemFrame(ItemFrame itemFrame, String id) {
        itemFrame.getPersistentDataContainer().set(NETEX_HANGING_KEY, PersistentDataType.STRING, id);
    }

    static HangingBlock getByItemFrame(ItemFrame itemFrame) {
        return getHangingBlock(itemFrame.getPersistentDataContainer().get(NETEX_HANGING_KEY, PersistentDataType.STRING));
    }

    default SlimefunBlockData trySpawnPlaceholderBlock(Location placeholder) {
        var data = StorageCacheUtils.getBlock(placeholder);;
        if (data == null) {
            // spawn placeholder block
            return Slimefun.getDatabaseManager().getBlockDataController().createBlock(placeholder, HANGING_PLACEHOLDER_BLOCK_ID);
        }

        var sf = SlimefunItem.getById(data.getSfId());
        if (sf instanceof HangingPlaceholderBlock) {
            return data;
        }

        // Unable to spawn placeholder block, deny onPlace action later.
        if (sf != null) {
            return null;
        }

        return null;
    }

    @OverridingMethodsMustInvokeSuper
    default void onPlace(Location placeholder, ItemFrame entityBlock, BlockFace attachSide) {
        var data = trySpawnPlaceholderBlock(placeholder);
        if (data == null) {
            return;
        }

        HangingPlaceholderBlock.placeHangingBlock(data, entityBlock, attachSide, this);
    }

    @OverridingMethodsMustInvokeSuper
    default void onBreak(Location placeholder, ItemFrame entityBlock) {
        HangingPlaceholderBlock.breakHangingBlock(placeholder, entityBlock.getAttachedFace());
    }

    void onInteract(Location placeholder, PlayerItemFrameChangeEvent event);
    void onTick(Location placeholder, ItemFrame entityBlock);

    @Nullable
    static ItemFrame getItemFrame(Location placeholder, BlockFace attachSide) {
        var center = placeholder.toBlockLocation().add(0.5, 0.5, 0.5);
        var es = center.getWorld().getNearbyEntities(center, 0.5, 0.5, 0.5);
        for (var e : es) {
            if (e instanceof ItemFrame itemFrame) {
                if (itemFrame.getAttachedFace() == attachSide.getOppositeFace()) {
                    return itemFrame;
                }
            }
        }

        return null;
    }

    String getId();
}
