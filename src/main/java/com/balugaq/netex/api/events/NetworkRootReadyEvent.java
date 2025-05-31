package com.balugaq.netex.api.events;

import io.github.sefiraat.networks.network.NetworkRoot;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import javax.annotation.Nonnull;

@Getter
public class NetworkRootReadyEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final NetworkRoot root;

    public NetworkRootReadyEvent(NetworkRoot root) {
        super(true);
        this.root = root;
    }

    public static @Nonnull HandlerList getHandlerList() {
        return handlers;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
