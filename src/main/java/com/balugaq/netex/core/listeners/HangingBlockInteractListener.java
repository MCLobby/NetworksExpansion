package com.balugaq.netex.core.listeners;

import com.balugaq.netex.api.interfaces.HangingBlock;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

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
}
