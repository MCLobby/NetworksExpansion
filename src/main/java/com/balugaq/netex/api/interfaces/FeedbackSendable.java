package com.balugaq.netex.api.interfaces;

import com.balugaq.netex.api.enums.FeedbackType;
import com.balugaq.netex.utils.Lang;
import com.balugaq.netex.utils.LocationUtil;

import io.github.sefiraat.networks.Networks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EnableAsync
public interface FeedbackSendable {
    Map<UUID, Set<Location>> SUBSCRIBED_LOCATIONS = new ConcurrentHashMap<>();

    @Async
    static void subscribe(@NotNull Player player, Location location) {
        UUID key = player.getUniqueId();
        if (!SUBSCRIBED_LOCATIONS.containsKey(key)) {
            SUBSCRIBED_LOCATIONS.put(key, ConcurrentHashMap.newKeySet());
        }
        SUBSCRIBED_LOCATIONS.get(key).add(location);
    }

    @Async
    static void unsubscribe(@NotNull Player player, Location location) {
        UUID key = player.getUniqueId();
        if (SUBSCRIBED_LOCATIONS.containsKey(key)) {
            SUBSCRIBED_LOCATIONS.get(key).remove(location);
        }
    }

    @Async
    static boolean hasSubscribed(@NotNull Player player, Location location) {
        UUID key = player.getUniqueId();
        if (SUBSCRIBED_LOCATIONS.containsKey(key)) {
            return SUBSCRIBED_LOCATIONS.get(key).contains(location);
        }
        return false;
    }

    @Async
    static void sendFeedback0(@NotNull Location location, @NotNull FeedbackType type) {
    	Bukkit.getScheduler().runTaskAsynchronously(Networks.getInstance(), () -> {
    		for (UUID uuid : SUBSCRIBED_LOCATIONS.keySet()) {
                if (SUBSCRIBED_LOCATIONS.get(uuid).contains(location)) {
                    Player player = Bukkit.getServer().getPlayer(uuid);
                    if (player != null) {
                        sendFeedback0(player, location, type.getMessage());
                    }
                }
            }
    	});
        
    }

    @Async
    static void sendFeedback0(@NotNull Player player, @NotNull Location location, String message) {
        player.sendMessage(String.format(
            Lang.getString("messages.debug.status_view"), LocationUtil.humanizeBlock(location), message));
    }

    @Async
    default void sendFeedback(@NotNull Location location, @NotNull FeedbackType type) {
    	Bukkit.getScheduler().runTaskAsynchronously(Networks.getInstance(), () -> {
    		for (UUID uuid : SUBSCRIBED_LOCATIONS.keySet()) {
                if (SUBSCRIBED_LOCATIONS.get(uuid).contains(location)) {
                    Player player = Bukkit.getServer().getPlayer(uuid);
                    if (player != null) {
                        sendFeedback(player, location, type.getMessage());
                    }
                }
            }
    	});
        
    }

    @Async
    default void sendFeedback(@NotNull Player player, @NotNull Location location, String message) {
        player.sendMessage(String.format(
            Lang.getString("messages.debug.status_view"), LocationUtil.humanizeBlock(location), message));
    }
}
