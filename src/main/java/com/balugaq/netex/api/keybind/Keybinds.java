package com.balugaq.netex.api.keybind;

import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import lombok.Data;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@NullMarked
@SuppressWarnings("deprecation")
public @Data class Keybinds implements ChestMenu.MenuClickHandler, Keyed {
    private static final Map<NamespacedKey, Map<Keybind, Action>> defaultKeybinds = new HashMap<>();
    private static final Map<NamespacedKey, Set<Keybind>> usableKeybind = new HashMap<>();
    private static final Map<NamespacedKey, Set<Action>> usableAction = new HashMap<>();

    private static final Map<String, Action> actionRegistry = new HashMap<>();

    private final NamespacedKey key;
    private @Nullable Location location;

    public Keybinds usableKeybinds(Keybind... keybinds) {
        return usableKeybinds(List.of(keybinds));
    }

    public Keybinds usableKeybinds(Collection<Keybind> keybinds) {
        usableKeybind.merge(key, new HashSet<>(keybinds), (a, b) ->
            new HashSet<>() {{addAll(a);addAll(b);}}
        );
        return this;
    }

    public Keybinds usableActions(Action... actions) {
        return usableActions(List.of(actions));
    }

    public Keybinds usableActions(Collection<Action> actions) {
        usableAction.merge(key, new HashSet<>(actions), (a, b) ->
            new HashSet<>() {{addAll(a);addAll(b);}}
        );
        return this;
    }

    public Keybinds defaultKeybinds(Map<Keybind, Action> keybinds) {
        defaultKeybinds.put(key, keybinds);
        return this;
    }

    public static Keybinds create(NamespacedKey item) {
        return new Keybinds(item, null);
    }

    public static Keybinds create(NamespacedKey item, Consumer<Keybinds> consumer) {
        return new Keybinds(item, null).set(consumer);
    }

    public Keybinds(NamespacedKey key, @Nullable Location location) {
        this.key = key;
        this.location = location;
    }

    public Map<Keybind, Action> getKeybinds(Location location) {
        Map<Keybind, Action> keybinds = defaultKeybinds.get(key);

        // Remap actions
        for (Keybind keybind : keybinds.keySet()) {
            String type = StorageCacheUtils.getData(location, keybind.getKey().getKey());
            if (type == null) continue;

            Action action = actionRegistry.get(type);
            if (action == null) continue;

            keybinds.put(keybind, action);
        }

        return keybinds;
    }

    public Keybinds location(BlockMenu menu) {
        return location(menu.getLocation());
    }

    public Keybinds location(Block block) {
        return location(block.getLocation());
    }

    public Keybinds location(Location location) {
        return new Keybinds(getKey(), location);
    }

    @Override
    public boolean onClick(Player player, int i, ItemStack itemStack, ClickAction clickAction) {
        BlockMenu menu = StorageCacheUtils.getMenu(location);
        for (Map.Entry<Keybind, Action> entry : getKeybinds(location).entrySet()) {
            if (entry.getKey().test(player, i, itemStack, clickAction)) {
                return entry.getValue().apply(player, i, itemStack, clickAction, menu);
            }
        }

        return defaultValue;
    }

    private boolean defaultValue = false;

    public boolean defaultValue() {
        return defaultValue;
    }

    public Keybinds defaultValue(boolean defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public Keybinds set(Consumer<Keybinds> consumer) {
        consumer.accept(this);
        return this;
    }

    public Keybinds generate() {
        return usableActions((p, s, i, a, m) -> defaultValue);
    }

    public void openMenu(Player player) {
        openMenu(player, 1);
    }

    public void openMenu(Player player, int page) {
        // todo
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }
}
