package com.balugaq.netex.api.keybind;

import com.balugaq.netex.api.helpers.Icon;
import io.github.sefiraat.networks.utils.Keys;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
@SuppressWarnings("deprecation")
public interface Keybind extends Keyed {
    Keybind leftClick = KeybindImpl.of(
        Keys.newKey("left_click"),
        Icon.UNKNOWN_ITEM,
        (player, slot, item, action) -> !action.isRightClicked()
    );

    Keybind rightClick = KeybindImpl.of(
        Keys.newKey("right_click"),
        Icon.UNKNOWN_ITEM,
        (player, slot, item, action) -> action.isRightClicked()
    );

    Keybind shiftClick = KeybindImpl.of(
        Keys.newKey("shift_click"),
        Icon.UNKNOWN_ITEM,
        (player, slot, item, action) -> action.isShiftClicked()
    );

    Keybind shiftLeftClick = KeybindImpl.of(
        Keys.newKey("shift_left_click"),
        Icon.UNKNOWN_ITEM,
        (player, slot, item, action) -> action.isShiftClicked() && !action.isRightClicked()
    );

    Keybind shiftRightClick = KeybindImpl.of(
        Keys.newKey("shift_right_click"),
        Icon.UNKNOWN_ITEM,
        (player, slot, item, action) -> action.isShiftClicked() && action.isRightClicked()
    );

    ItemStack icon();
    boolean test(Player player, int slot, ItemStack item, ClickAction action);

    class KeybindImpl implements Keybind {
        private final NamespacedKey key;
        private final ItemStack icon;
        private final KeybindPredicate predicate;

        public KeybindImpl(NamespacedKey key, ItemStack icon, KeybindPredicate predicate) {
            this.key = key;
            this.icon = icon;
            this.predicate = predicate;
        }

        public static KeybindImpl of(NamespacedKey key, ItemStack icon, KeybindPredicate predicate) {
            return new KeybindImpl(key, icon, predicate);
        }

        @Override
        public ItemStack icon() {
            return icon;
        }

        @Override
        public boolean test(Player player, int slot, ItemStack item, ClickAction action) {
            return predicate.test(player, slot, item, action);
        }

        @Override
        public @NotNull NamespacedKey getKey() {
            return key;
        }
    }
}
