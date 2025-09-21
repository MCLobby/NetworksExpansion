package com.balugaq.netex.api.algorithm;

import io.github.sefiraat.networks.Networks;

public class ID {
    private static long id = 0;
    public static long nextId() {
        return id++;
    }

    public static void fetchId() {
        id = Networks.getConfigManager().getLong("id", 0);
    }

    public static void saveId() {
        Networks.getConfigManager().set("id", id);
    }
}
