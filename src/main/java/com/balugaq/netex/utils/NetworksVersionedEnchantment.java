package com.balugaq.netex.utils;

import com.balugaq.netex.api.enums.MinecraftVersion;
import io.github.sefiraat.networks.Networks;
import java.lang.reflect.Field;
import javax.annotation.Nonnull;
import lombok.experimental.UtilityClass;
import org.bukkit.enchantments.Enchantment;

@UtilityClass
public class NetworksVersionedEnchantment {
    public static final @Nonnull Enchantment GLOW;
    public static final @Nonnull Enchantment LUCK_OF_THE_SEA;

    static {
        MinecraftVersion version = Networks.getInstance().getMCVersion();
        GLOW = version.isAtLeast(MinecraftVersion.MC1_20_5) ? Enchantment.POWER : getKey("ARROW_DAMAGE");
        LUCK_OF_THE_SEA = version.isAtLeast(MinecraftVersion.MC1_20_5) ? Enchantment.LUCK_OF_THE_SEA : getKey("LUCK");
    }

    @Nonnull
    private static Enchantment getKey(@Nonnull String key) {
        try {
            Field field = Enchantment.class.getDeclaredField(key);
            return (Enchantment) field.get(null);
        } catch (Exception e) {
            // Shouldn't be null
            Debug.trace(e);
            //noinspection DataFlowIssue
            return null;
        }
    }
}
