package io.github.sefiraat.networks.slimefun.network;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import io.github.sefiraat.networks.Networks;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

public interface AdminDebuggable {
    Queue<Pair<Location, String>> DEBUG_QUEUE = new ConcurrentLinkedQueue<>();
    Set<Player> VIEWERS = new HashSet<>();
    String DEBUG_KEY = "network_debugging";

    static void load() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Networks.getInstance(), () -> {
            while (!DEBUG_QUEUE.isEmpty()) {
                final Pair<Location, String> pair = DEBUG_QUEUE.poll();
                if (pair != null) {
                    sendDebugMessage1(pair.getFirstValue(), pair.getSecondValue());
                }
            }
        }, 1L, 10L);
    }

    static boolean isDebug0(@Nonnull Location location) {
        String debug = StorageCacheUtils.getData(location, DEBUG_KEY);
        return Boolean.parseBoolean(debug);
    }

    static void sendDebugMessage0(@Nonnull Location location, @Nonnull String string) {
        if (isDebug0(location)) {
            DEBUG_QUEUE.add(new Pair<>(location, string));
        }
    }

    static void sendDebugMessage1(@Nonnull Location location, @Nonnull String string) {
        final String locationString = "W[" + location.getWorld().getName() + "] " +
                "X[" + location.getBlockX() + "] " +
                "Y[" + location.getBlockY() + "] " +
                "Z[" + location.getBlockZ() + "] ";
        Networks.getInstance().getLogger().log(Level.INFO, String.format(Networks.getLocalizationService().getString("messages.debug.info"), locationString, string));
        for (Player player : VIEWERS) {
            if (player.isOnline()) {
                player.sendMessage(String.format(Networks.getLocalizationService().getString("messages.debug.viewer-info"), locationString, string));
            } else {
                removeViewer0(player);
            }
        }
    }

    static void addViewer0(@Nonnull Player player) {
        VIEWERS.add(player);
    }

    static void removeViewer0(@Nonnull Player player) {
        VIEWERS.remove(player);
    }

    static boolean hasViewer0(@Nonnull Player player) {
        return VIEWERS.contains(player);
    }

    default boolean isDebug(@Nonnull Location location) {
        String debug = StorageCacheUtils.getData(location, DEBUG_KEY);
        return Boolean.parseBoolean(debug);
    }

    default void setDebug(@Nonnull Location location, boolean value) {
        StorageCacheUtils.setData(location, DEBUG_KEY, String.valueOf(value));
    }

    default void toggleDebugMode(@Nonnull Location location, @Nonnull Player player) {
        final boolean isDebug = isDebug(location);
        final boolean nextState = !isDebug;
        setDebug(location, nextState);
        player.sendMessage(String.format(Networks.getLocalizationService().getString("messages.debug.toggleDebugMode-debug"), nextState));
        if (nextState) {
            player.sendMessage(Networks.getLocalizationService().getString("messages.debug.enabled-debug"));
        }
    }

    default void sendDebugMessage(@Nonnull Location location, @Nonnull String string) {
        if (isDebug(location)) {
            DEBUG_QUEUE.add(new Pair<>(location, string));
        }
    }

    default void addViewer(@Nonnull Player player) {
        VIEWERS.add(player);
    }

    default void removeViewer(@Nonnull Player player) {
        VIEWERS.remove(player);
    }

    default boolean hasViewer(@Nonnull Player player) {
        return VIEWERS.contains(player);
    }
}
