package com.balugaq.netex.utils;

import com.ytdd9527.networksexpansion.core.services.LocalizationService;
import io.github.sefiraat.networks.Networks;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@UtilityClass
public class Lang {
    public static LocalizationService get() {
        return Networks.getLocalizationService();
    }

    @Nonnull
    public static ItemStack getMechanism(@Nonnull String key) {
        return get().getMechanism(key);
    }

    @Nonnull
    public static SlimefunItemStack getItem(@Nonnull String key, @Nonnull Material material) {
        return get().getItem(key, material);
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public static SlimefunItemStack getItem(String id, String texture, String... extraLore) {
        return get().getItem(id, texture, extraLore);
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public static SlimefunItemStack getItem(String id, ItemStack itemStack, String... extraLore) {
        return get().getItem(id, itemStack, extraLore);
    }

    @Nonnull
    public static ItemStack getIcon(@Nonnull String key, @Nonnull Material material) {
        return get().getIcon(key, material);
    }

    @Nonnull
    public static String getString(@Nonnull String key) {
        return get().getString(key);
    }

    @ParametersAreNonnullByDefault
    @Nonnull
    public String getString(String key, Object... args) {
        return get().getString(key, args);
    }


    @Nonnull
    public static List<String> getStringList(@Nonnull String key) {
        return get().getStringList(key);
    }

    @Nonnull
    public static String[] getStringArray(@Nonnull String key) {
        return get().getStringArray(key);
    }
}
