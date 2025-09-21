package com.balugaq.netex.api.keybind;

import com.balugaq.netex.api.algorithm.ID;
import io.github.sefiraat.networks.Networks;
import io.github.thebusybiscuit.slimefun4.libraries.dough.config.Config;
import io.github.thebusybiscuit.slimefun4.utils.ChatUtils;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

@NullMarked
public @Data class KeybindsScript {
    private final OfflinePlayer player;
    private final Keybinds keybinds;
    private final String keybindsName;
    private final Map<Keybind, Action> code;

    public KeybindsScript(OfflinePlayer player, Keybinds keybinds, String keybindsName,  Map<Keybind, Action> code) {
        this.player = player;
        this.keybinds = keybinds;
        this.keybindsName = keybindsName;
        this.code = code;
    }

    public KeybindsScript(OfflinePlayer player, Keybinds keybinds, String keybindsName, Location location) {
        this(player, keybinds, keybindsName, keybinds.getKeybinds(location));
    }

    public static KeybindsScript warp(Player player, Keybinds keybinds, String keybindsName, Location location) {
        return new KeybindsScript(player, keybinds, keybindsName, location);
    }

    public String getAuthorName() {
        return player.getName();
    }

    public String getKeybindsName() {
        return ChatUtils.removeColorCodes(keybindsName);
    }

    public String getKeybindsType() {
        return keybinds.getKey().toString();
    }

    public void save() {
        Config config =
            new Config("plugins/" + Networks.getInstance().getName() + "/keybinds/" + ID.nextId() + ".nkb");

        config.setValue("keybinds-type", getKeybindsType());
        config.setValue("author-name", getAuthorName());
        config.setValue("keybinds-name", getKeybindsName());

        for (Map.Entry<Keybind, Action> entry : code.entrySet()) {
            config.setValue("keybinds." + entry.getKey().getKey(), entry.getValue().getKey().toString());
        }

        config.save();
    }

    public static KeybindsScript fromConfig(Config config) {
        Map<Keybind, Action> code = new HashMap<>();
        for (String key : config.getKeys("keybinds")) {
            code.put(Keybind.get(NamespacedKey.fromString(key)), Action.get(NamespacedKey.fromString(config.getString("keybinds." + key))));
        }
        return new KeybindsScript(
            Bukkit.getOfflinePlayer(config.getString("author-name")),
            Keybinds.get(NamespacedKey.fromString(config.getString("keybinds-type"))),
            config.getString("keybinds-name"),
            code
        );
    }
}
