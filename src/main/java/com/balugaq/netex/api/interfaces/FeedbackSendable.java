package com.balugaq.netex.api.interfaces;

import com.balugaq.netex.api.enums.FeedbackType;
import com.balugaq.netex.utils.Lang;
import com.balugaq.netex.utils.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface FeedbackSendable {
    Map<UUID, Set<Location>> SUBSCRIBED_LOCATIONS = new HashMap<>();

    static void subscribe(@NotNull Player player, Location location) {
        UUID key = player.getUniqueId();
        if (!SUBSCRIBED_LOCATIONS.containsKey(key)) {
            SUBSCRIBED_LOCATIONS.put(key, new HashSet<>());
        }
        SUBSCRIBED_LOCATIONS.get(key).add(location);
    }

    static void unsubscribe(@NotNull Player player, Location location) {
        UUID key = player.getUniqueId();
        if (SUBSCRIBED_LOCATIONS.containsKey(key)) {
            SUBSCRIBED_LOCATIONS.get(key).remove(location);
        }
    }

    static boolean hasSubscribed(@NotNull Player player, Location location) {
        UUID key = player.getUniqueId();
        if (SUBSCRIBED_LOCATIONS.containsKey(key)) {
            return SUBSCRIBED_LOCATIONS.get(key).contains(location);
        }
        return false;
    }

    default void sendFeedback(@NotNull Location location, @NotNull FeedbackType type) {
        for (UUID uuid : SUBSCRIBED_LOCATIONS.keySet()) {
            if (SUBSCRIBED_LOCATIONS.get(uuid).contains(location)) {
                Player player = Bukkit.getServer().getPlayer(uuid);
                if (player != null) {
                    sendFeedback(player, location, type.getMessage());
                }
            }
        }
    }

    default void sendFeedback(@NotNull Player player, @NotNull Location location, String message) {
        player.sendMessage(String.format(
            Lang.getString("messages.debug.status_view"), LocationUtil.humanizeBlock(location), message));
    }
}
