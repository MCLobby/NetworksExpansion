package com.balugaq.netex.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import io.github.sefiraat.networks.network.NetworkRoot;
import lombok.Getter;

@Getter
public class NetworkRootReadyEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final NetworkRoot root;

    public NetworkRootReadyEvent(NetworkRoot root) {
        super(true);
        this.root = root;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
