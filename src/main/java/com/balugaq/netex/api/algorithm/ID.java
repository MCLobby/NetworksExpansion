package com.balugaq.netex.api.algorithm;

import io.github.sefiraat.networks.Networks;

import java.util.concurrent.atomic.AtomicLong;

public class ID {
    private static AtomicLong id = new AtomicLong(0);
    public static long nextId() {
        return id.incrementAndGet();
    }

    public static void fetchId() {
        id = new AtomicLong(Networks.getConfigManager().getLong("id", 0));
    }

    public static void saveId() {
        Networks.getConfigManager().set("id", id.get());
    }
}
