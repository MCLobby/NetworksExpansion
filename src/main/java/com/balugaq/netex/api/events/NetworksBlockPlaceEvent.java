package com.balugaq.netex.api.events;

import javax.annotation.Nonnull;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class NetworksBlockPlaceEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final @Nonnull Block block;
    private final @Nonnull Player player;
    private boolean cancelled = false;

    public NetworksBlockPlaceEvent(@Nonnull Block theBlock, @Nonnull Player player) {
        this.block = theBlock;
        this.player = player;
    }

    public static @Nonnull HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @Nonnull HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
