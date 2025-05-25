package com.balugaq.netex.api.enums;

import com.ytdd9527.networksexpansion.utils.TextUtil;
import io.github.sefiraat.networks.Networks;
import org.jetbrains.annotations.NotNull;

public enum TransportMode {
    NONE,
    NULL_ONLY,
    NONNULL_ONLY,
    FIRST_ONLY,
    LAST_ONLY,
    FIRST_STOP,
    LAZY;

    public @NotNull String getName() {
        return TextUtil.colorRandomString(getRawName());
    }

    public @NotNull String getRawName() {
        return switch (this) {
            case NONE -> Networks.getLocalizationService().getString("icons.transport_mode.none");
            case NULL_ONLY -> Networks.getLocalizationService().getString("icons.transport_mode.null_only");
            case NONNULL_ONLY -> Networks.getLocalizationService().getString("icons.transport_mode.nonnull_only");
            case FIRST_ONLY -> Networks.getLocalizationService().getString("icons.transport_mode.first_only");
            case LAST_ONLY -> Networks.getLocalizationService().getString("icons.transport_mode.last_only");
            case FIRST_STOP -> Networks.getLocalizationService().getString("icons.transport_mode.first_stop");
            case LAZY -> Networks.getLocalizationService().getString("icons.transport_mode.lazy");
        };
    }

    public TransportMode next() {
        int index = this.ordinal() + 1;
        if (index >= TransportMode.values().length) {
            index = 0;
        }
        return TransportMode.values()[index];
    }

    public TransportMode previous() {
        int index = this.ordinal() - 1;
        if (index < 0) {
            index = TransportMode.values().length - 1;
        }
        return TransportMode.values()[index];
    }
}
