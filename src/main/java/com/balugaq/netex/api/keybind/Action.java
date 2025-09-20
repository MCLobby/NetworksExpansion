package com.balugaq.netex.api.keybind;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ClickAction;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
@SuppressWarnings("deprecation")
public interface Action extends Keyed, Comparable<Action> {
    static Action of(NamespacedKey key, KeybindPredicate consumer) {
        return new Action() {
            @Override
            public boolean apply(Player player, int slot, ItemStack item, ClickAction action, BlockMenu menu) throws IncompatibleKeybind {
                return consumer.test(player, slot, item, action, menu);
            }

            @Override
            public @NotNull NamespacedKey getKey() {
                return key;
            }
        }.register();
    }

    default int compareTo(Action o) {
        return getKey().compareTo(o.getKey());
    }

    default Action register() {
        Keybinds.registerAction(this);
        return this;
    }

    boolean apply(Player player, int slot, ItemStack item, ClickAction action, BlockMenu menu) throws IncompatibleKeybind;
}
