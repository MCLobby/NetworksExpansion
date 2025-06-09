package com.ytdd9527.networksexpansion.utils;

import com.balugaq.netex.api.groups.MainItemGroup;
import com.balugaq.netex.api.groups.SubFlexItemGroup;
import io.github.sefiraat.networks.utils.Keys;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import javax.annotation.Nonnull;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

/**
 * @author Final_ROOT
 * @since 2.0
 */
@UtilityClass
public class GroupConfigUtil {

    public static MainItemGroup getMainItemGroup(
            @Nonnull String key, @Nonnull Material defaultMaterial, @Nonnull String defaultName) {
        CustomItemStack customItemStack = new CustomItemStack(defaultMaterial, defaultName);
        NamespacedKey namespacedKey = Keys.newKey(key);
        return new MainItemGroup(namespacedKey, customItemStack, 0);
    }

    public static SubFlexItemGroup getSubFlexItemGroup(
            @Nonnull String key, @Nonnull Material defaultMaterial, @Nonnull String defaultName) {
        CustomItemStack customItemStack = new CustomItemStack(defaultMaterial, defaultName);
        NamespacedKey namespacedKey = Keys.newKey(key);
        return new SubFlexItemGroup(namespacedKey, customItemStack, 0);
    }
}
