package com.balugaq.netex.utils;

import com.ytdd9527.networksexpansion.core.services.LocalizationService;
import io.github.sefiraat.networks.Networks;
import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import java.util.List;

@UtilityClass
public class Lang {
    public static LocalizationService get() {
        return Networks.getLocalizationService();
    }

    public static String getString(@Nonnull String key) {
        return get().getString(key);
    }

    public static List<String> getStringList(@Nonnull String key) {
        return get().getStringList(key);
    }
}
