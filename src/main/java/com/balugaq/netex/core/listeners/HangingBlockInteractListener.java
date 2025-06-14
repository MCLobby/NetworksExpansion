package com.balugaq.netex.core.listeners;

import com.balugaq.netex.api.interfaces.HangingBlock;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;

public class HangingBlockInteractListener implements Listener {
    @EventHandler
    public void onInteract(PlayerItemFrameChangeEvent event) {
        var hangingBlock = HangingBlock.getByItemFrame(event.getItemFrame());
        if (hangingBlock != null) {
            hangingBlock.onInteract(event.getItemFrame().getLocation().toBlockLocation(), event);
        }
    }

    @EventHandler
    public void onBreak(EntityDeathEvent event) {
        if (event.getEntity() instanceof ItemFrame itemFrame) {
            var hangingBlock = HangingBlock.getByItemFrame(itemFrame);
            if (hangingBlock != null) {
                hangingBlock.onBreak(itemFrame.getLocation().toBlockLocation(), itemFrame);
            }
        }
    }

    @EventHandler
    public void onHangingPlace(HangingPlaceEvent event) {
        SlimefunItem sf = SlimefunItem.getByItem(event.getItemStack());
        if (sf instanceof HangingBlock hangingBlock && event.getEntity() instanceof ItemFrame itemFrame) {
            hangingBlock.onPlace(itemFrame.getLocation(), itemFrame, itemFrame.getAttachedFace());
        }
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent event) {
        if (!(event.getEntity() instanceof ItemFrame itemFrame)) {
            return;
        }

        var hangingBlock = HangingBlock.getByItemFrame(itemFrame);
        if (hangingBlock != null) {
            hangingBlock.onBreak(itemFrame.getLocation().toBlockLocation(), itemFrame);
        }
    }
}
