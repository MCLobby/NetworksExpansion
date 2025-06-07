package io.github.sefiraat.networks.managers;

import com.balugaq.netex.core.listeners.JEGCompatibleListener;
import io.github.sefiraat.networks.Networks;
import io.github.sefiraat.networks.listeners.ExplosiveToolListener;
import io.github.sefiraat.networks.listeners.SyncListener;
import org.bukkit.event.Listener;

public class ListenerManager {

    public ListenerManager() {
        addListener(new ExplosiveToolListener());
        addListener(new SyncListener());
        if (Networks.getSupportedPluginManager().isJustEnoughGuide()) {
            try {
                addListener(new JEGCompatibleListener());
            } catch (Throwable ignored) {
                Networks.getSupportedPluginManager().setJustEnoughGuide(false);
            }
        }
    }

    private void addListener(Listener listener) {
        Networks.getPluginManager().registerEvents(listener, Networks.getInstance());
    }
}
