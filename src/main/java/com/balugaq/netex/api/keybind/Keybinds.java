package com.balugaq.netex.api.keybind;

import com.balugaq.netex.api.helpers.Icon;
import com.balugaq.netex.utils.Lang;
import com.xzavier0722.mc.plugin.slimefun4.storage.util.StorageCacheUtils;
import com.ytdd9527.networksexpansion.utils.ReflectionUtil;
import io.github.sefiraat.networks.utils.Keys;
import lombok.Data;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@NullMarked
@SuppressWarnings("deprecation")
public @Data class Keybinds implements ChestMenu.MenuClickHandler, Keyed {
    private static final Map<NamespacedKey, LinkedHashMap<Keybind, Action>> defaultKeybinds = new HashMap<>();
    private static final Map<NamespacedKey, Set<Keybind>> usableKeybind = new HashMap<>();
    private static final Map<NamespacedKey, Set<Action>> usableAction = new HashMap<>();

    private static final Map<String, Action> actionRegistry = new HashMap<>();

    private final NamespacedKey key;
    private @Nullable Location location;
    private boolean defaultValue = false;

    public Keybinds(NamespacedKey key) {
        this.key = key;
    }

    public static void registerAction(Action action) {
        actionRegistry.put(action.getKey().getKey(), action);
    }

    public static Keybinds create(NamespacedKey item) {
        return new Keybinds(item);
    }

    public static Keybinds create(NamespacedKey item, Consumer<Keybinds> consumer) {
        return new Keybinds(item).set(consumer);
    }

    public Keybinds usableKeybinds(Keybind... keybinds) {
        return usableKeybinds(List.of(keybinds));
    }

    public Keybinds usableKeybinds(Collection<Keybind> keybinds) {
        usableKeybind.merge(key, new HashSet<>(keybinds), (a, b) ->
            new HashSet<>() {{
                addAll(a);
                addAll(b);
            }}
        );
        return this;
    }

    public Set<Keybind> usableKeybinds() {
        return usableKeybind.getOrDefault(key, new HashSet<>());
    }

    public Keybinds usableActions(Action... actions) {
        return usableActions(List.of(actions));
    }

    public Keybinds usableActions(Collection<Action> actions) {
        usableAction.merge(key, new HashSet<>(actions), (a, b) ->
            new HashSet<>() {{
                addAll(a);
                addAll(b);
            }}
        );
        return this;
    }

    public Set<Action> usableActions() {
        return usableAction.getOrDefault(key, new HashSet<>());
    }

    public Keybinds defaultKeybinds(LinkedHashMap<Keybind, Action> keybinds) {
        defaultKeybinds.put(key, keybinds);
        return this;
    }

    public Keybinds defaultKeybinds(Object... keybinds) {
        if (!defaultKeybinds.containsKey(key)) {
            defaultKeybinds.put(key, new LinkedHashMap<>());
        }

        for (int i = 0; i < keybinds.length; i += 2) {
            if (!(keybinds[i] instanceof Keybind keybind)) continue;
            if (!(keybinds[i + 1] instanceof Action action)) continue;
            defaultKeybinds.get(key).put(keybind, action);
        }

        return this;
    }

    public Keybinds defaultKeybinds(Map<Keybind, Action> keybinds) {
        if (!defaultKeybinds.containsKey(key)) {
            defaultKeybinds.put(key, new LinkedHashMap<>());
        }

        defaultKeybinds.get(key).putAll(keybinds);
        return this;
    }

    public String keybindKey(String key) {
        return "keybinds." + this.key.getKey() + "." + key;
    }

    public LinkedHashMap<Keybind, Action> getKeybinds(Location location) {
        LinkedHashMap<Keybind, Action> keybinds = new LinkedHashMap<>(defaultKeybinds.get(key));

        // Remap actions
        for (Keybind keybind : keybinds.keySet()) {
            String type = StorageCacheUtils.getData(location, keybindKey(keybind.getKey().getKey()));
            if (type == null) continue;

            Action action = actionRegistry.get(type);
            if (action == null) continue;

            keybinds.put(keybind, action);
        }

        return keybinds;
    }

    @Nullable
    public BlockMenu getMenu(Player player) {
        if (((Inventory) ReflectionUtil.invokeMethod(ReflectionUtil.invokeMethod(player, "getOpenInventory"), "getTopInventory")).getHolder() instanceof BlockMenu menu)
            return menu;
        return null;
    }

    @Override
    public boolean onClick(Player player, int i, ItemStack itemStack, ClickAction clickAction) {
        BlockMenu menu = getMenu(player);
        if (menu == null) return false;

        Location location = menu.getLocation();
        for (Map.Entry<Keybind, Action> entry : getKeybinds(location).entrySet()) {
            if (entry.getKey().test(player, i, itemStack, clickAction, menu)) {
                if (entry.getValue().apply(player, i, itemStack, clickAction, menu) != defaultValue) {
                    return !defaultValue;
                }
            }
        }

        return defaultValue;
    }

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
        return usableActions(Action.of(Keys.newKey("do-nothing"), (p, s, i, a, m) -> defaultValue));
    }

    public ItemStack icon() {
        return Lang.getIcon("keybinds." + key.getKey(), Material.DIAMOND_ORE);
    }

    public void openMenu(Location location, Player player, Consumer<Player> back) {
        openMenu(location, player, 1, back);
    }

    public void openMenu(Location location, Player player, int page, Consumer<Player> back) {
        ChestMenu menu = new ChestMenu(Lang.getString("messages.keybind.sub-title"));

        int[] backgroundSlots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 13, 17, 18, 22, 26, 27, 31, 35, 36, 40, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};
        int[] keybindsSlots = {10, 19, 28, 37, 14, 23, 32, 41};
        int[] bordersSlots = {11, 20, 29, 38, 15, 24, 33, 42};
        int[] actionsSlots = {12, 21, 30, 39, 16, 25, 34, 43};
        int previousSlot = 47;
        int nextSlot = 52;

        for (int slot : backgroundSlots) {
            menu.addItem(slot, Icon.BLUE_BACKGROUND, (p, s, i, a) -> false);
        }

        List<Map.Entry<Keybind, Action>> keybinds = getKeybinds(location).entrySet().stream().toList();
        for (int slot = 0; slot < keybindsSlots.length; slot++) {
            if (slot >= keybinds.size()) {
                menu.addItem(keybindsSlots[slot], Icon.LIGHT_GRAY_BACKGROUND, (p, s, i, a) -> false);
                menu.addItem(bordersSlots[slot], Icon.LIGHT_GRAY_BACKGROUND, (p, s, i, a) -> false);
                menu.addItem(actionsSlots[slot], Icon.LIGHT_GRAY_BACKGROUND, (p, s, i, a) -> false);
            } else {
                var entry = keybinds.get(slot + (page - 1) * keybindsSlots.length);
                Keybind keybind = entry.getKey();
                Action action = entry.getValue();
                menu.addItem(keybindsSlots[slot], Lang.getIcon("keybinds." + keybind.getKey().getKey(), Material.OAK_WOOD), (p, s, i, a) -> false);
                menu.addItem(bordersSlots[slot], Icon.YELLOW_BORDER, (p, s, i, a) -> false);
                menu.addItem(actionsSlots[slot], Lang.getIcon("keybinds." + action.getKey().getKey(), Material.REDSTONE_TORCH), (p, s, i, a) -> {
                    openActionSelectMenu(location, player, this, keybind, action, 1, p2 -> {
                        openMenu(location, p2, page, back);
                    });
                    return false;
                });
            }
        }

        int maxPage = (keybinds.size() - 1) / keybindsSlots.length + 1;

        if (page > 1) {
            menu.addItem(previousSlot, Icon.getPageStack(Icon.PAGE_PREVIOUS_STACK, page, maxPage), (p, s, i, a) -> {
                if (page <= 1) return false;
                openMenu(location, p, page - 1, back);
                return false;
            });
        }

        if (page < maxPage) {
            menu.addItem(nextSlot, Icon.getPageStack(Icon.PAGE_NEXT_STACK, page, maxPage), (p, s, i, a) -> {
                if (page >= maxPage) return false;
                openMenu(location, p, page + 1, back);
                return false;
            });
        }

        menu.open(player);
    }

    public void openActionSelectMenu(Location location, Player player, Keybinds keybinds, Keybind keybind, Action action, int page, Consumer<Player> back) {
        ChestMenu menu = new ChestMenu(Lang.getString("messages.keybind.action-select-title"));

        List<Action> actions = keybinds.usableActions().stream().toList();
        int i = 0;
        for (; i < Math.min(45, actions.size()); i++) {
            Action a = actions.get(i);
            menu.addItem(i, Lang.getIcon("keybinds." + a.getKey().getKey(), Material.REDSTONE_TORCH), (p, s, i1, a2) -> {
                keybind.set(location, keybinds, a);
                back.accept(player);
                return false;
            });
        }
        i = (i + 8) / 9 * 9;
        int maxPage = (actions.size() - 1) / 45 + 1;
        for (int j = 0; j < 9; j++) {
            if (j == 1) {
                menu.addItem(i + j, Icon.getPageStack(Icon.PAGE_PREVIOUS_STACK, page, maxPage), (p, s, i1, a) -> {
                    if (page <= 1) return false;
                    openActionSelectMenu(location, p, keybinds, keybind, action, page - 1, back);
                    return false;
                });
            } else if (j == 7) {
                menu.addItem(i + j, Icon.getPageStack(Icon.PAGE_NEXT_STACK, page, maxPage), (p, s, i1, a) -> {
                    if (page >= maxPage) return false;
                    openActionSelectMenu(location, p, keybinds, keybind, action, page + 1, back);
                    return false;
                });
            } else {
                menu.addItem(i + j, Icon.BLUE_BACKGROUND, (p, s, i1, a) -> false);
            }
        }

        menu.open(player);
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }
}
