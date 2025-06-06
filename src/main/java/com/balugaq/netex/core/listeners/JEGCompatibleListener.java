package com.balugaq.netex.core.listeners;

import com.balugaq.jeg.api.objects.events.GuideEvents;
import com.balugaq.jeg.utils.ReflectionUtil;
import io.github.thebusybiscuit.slimefun4.api.events.SlimefunGuideOpenEvent;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.GuideHistory;
import lombok.SneakyThrows;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class JEGCompatibleListener implements Listener {
    public static final Map<UUID, GuideHistory> GUIDE_HISTORY = new ConcurrentHashMap<>();
    public static final Map<UUID, BiConsumer<GuideEvents.ItemButtonClickEvent, PlayerProfile>> PROFILE_CALLBACKS = new ConcurrentHashMap<>();

    public static void addCallback(@Nonnull UUID uuid, @Nonnull BiConsumer<GuideEvents.ItemButtonClickEvent, PlayerProfile> callback) {
        PROFILE_CALLBACKS.put(uuid, callback);
    }

    public static void removeCallback(@Nonnull UUID uuid) {
        PROFILE_CALLBACKS.remove(uuid);
    }

    @EventHandler
    public void onGuideOpen(SlimefunGuideOpenEvent event) {
        var player = event.getPlayer();
        if (!PROFILE_CALLBACKS.containsKey(player.getUniqueId())) {
            return;
        }

        var profile = getPlayerProfile(player);
        saveOriginGuideHistory(profile);
    }

    @EventHandler
    public void onJEGItemClick(GuideEvents.ItemButtonClickEvent event) {
        var player = event.getPlayer();
        if (!PROFILE_CALLBACKS.containsKey(player.getUniqueId())) {
            return;
        }

        var profile = getPlayerProfile(player);
        rollbackGuideHistory(profile);
        PROFILE_CALLBACKS.get(player.getUniqueId()).accept(event, profile);
    }

    private void saveOriginGuideHistory(PlayerProfile profile) {
        var history = new GuideHistory(profile);
        GUIDE_HISTORY.put(profile.getUUID(), history);
    }

    private void rollbackGuideHistory(PlayerProfile profile) {
        var originHistory = GUIDE_HISTORY.get(profile.getUUID());
        if (originHistory == null) {
            return;
        }

        ReflectionUtil.setValue(profile, "guideHistory", originHistory);
    }

    @SneakyThrows
    @Nonnull
    public static PlayerProfile getPlayerProfile(OfflinePlayer player) {
        // Shouldn't be null;
        return PlayerProfile.find(player).orElseThrow(() -> new RuntimeException("PlayerProfile not found"));
    }
}
