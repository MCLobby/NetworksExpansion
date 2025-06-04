package com.balugaq.netex.api.data;

import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.network.stackcaches.ItemRequest;
import io.github.sefiraat.networks.utils.StackUtils;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class ItemFlowRecord {
    public static final long THRESHOLD = Networks.getConfigManager().getRecordGCThreshold();
    public static final long DEADLINE = Networks.getConfigManager().getRecordGCDeadline();
    public final Map<ItemStack, List<TransportAction>> actions = new ConcurrentHashMap<>();
    public long actionsCount = 0;
    public long lastChangeTime = System.currentTimeMillis();

    public void gc() {
        if (actionsCount > THRESHOLD) {
            actions.clear();
        }
    }

    public void addAction(Location accessor, ItemStack item) {
        lastChangeTime = System.currentTimeMillis();
        actionsCount++;

        var key = StackUtils.getAsQuantity(item, 1);

        var list = actions.computeIfAbsent(key, k -> new ArrayList<>());

        var action = new TransportAction(accessor, item.getAmount(), System.currentTimeMillis());
        list.add(action);
    }

    public void addAction(Location accessor, ItemRequest request) {
        lastChangeTime = System.currentTimeMillis();
        actionsCount++;

        var key = StackUtils.getAsQuantity(request.getItemStack(), 1);
        var list = actions.computeIfAbsent(key, k -> new ArrayList<>());

        var action = new TransportAction(accessor, -request.getAmount(), System.currentTimeMillis());
        list.add(action);
    }

    @Data
    public static class TransportAction {
        public final Location accessor;
        public final long amount;
        public final long nanoTime;
    }
}
