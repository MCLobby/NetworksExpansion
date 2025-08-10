package com.balugaq.netex.api.enums;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public enum MinecraftVersion {
    MC1_20(20, 0),
    MC1_20_1(20, 1),
    MC1_20_2(20, 2),
    MC1_20_3(20, 3),
    MC1_20_4(20, 4),
    MC1_20_5(20, 5),
    MC1_20_6(20, 6),
    MC1_21(21, 0),
    MC1_21_1(21, 1),
    MC1_21_2(21, 2),
    MC1_21_3(21, 3),
    MC1_21_4(21, 4),
    MC1_21_5(21, 5),
    MC1_21_6(21, 6),
    MC1_21_7(21, 7),
    MC1_21_8(21, 8),
    MC1_21_9(21, 9),
    MC1_21_10(21, 10),
    MC1_21_11(21, 11),
    MC1_21_12(21, 12),
    MC1_21_13(21, 13),
    MC1_22(22, 0),
    MC1_22_1(22, 1),
    MC1_22_2(22, 2),
    MC1_22_3(22, 3),
    MC1_22_4(22, 4),
    MC1_22_5(22, 5),
    MC1_22_6(22, 6),
    MC1_22_7(22, 7),
    MC1_22_8(22, 8),
    MC1_22_9(22, 9),
    MC1_22_10(22, 10),
    MC1_22_11(22, 11),
    MC1_22_12(22, 12),
    MC1_22_13(22, 13),
    UNKNOWN(999, 999);

    private final int major;
    private final int minor;

    MinecraftVersion(int major, int minor) {
        this.major = major;
        this.minor = minor;
    }

    public static @NotNull MinecraftVersion of(int major, int minor) {
        for (MinecraftVersion version : values()) {
            if (version.major == major && version.minor == minor) {
                return version;
            }
        }
        return UNKNOWN;
    }

    public boolean isAtLeast(@NotNull MinecraftVersion version) {
        return this.major > version.major || (this.major == version.major && this.minor >= version.minor);
    }
}
