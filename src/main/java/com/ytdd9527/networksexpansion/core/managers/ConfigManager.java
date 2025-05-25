package com.ytdd9527.networksexpansion.core.managers;

import io.github.sefiraat.networks.Networks;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class ConfigManager {


    public ConfigManager() {
        setupDefaultConfig();
    }

    private void setupDefaultConfig() {
        // config.yml
        final Networks plugin = Networks.getInstance();
        final InputStream inputStream = plugin.getResource("config.yml");
        final File existingFile = new File(plugin.getDataFolder(), "config.yml");

        if (inputStream == null) {
            return;
        }

        final Reader reader = new InputStreamReader(inputStream);
        final FileConfiguration resourceConfig = YamlConfiguration.loadConfiguration(reader);
        final FileConfiguration existingConfig = YamlConfiguration.loadConfiguration(existingFile);

        for (String key : resourceConfig.getKeys(false)) {
            checkKey(existingConfig, resourceConfig, key);
        }

        try {
            existingConfig.save(existingFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ParametersAreNonnullByDefault
    private void checkKey(FileConfiguration existingConfig, FileConfiguration resourceConfig, String key) {
        final Object currentValue = existingConfig.get(key);
        final Object newValue = resourceConfig.get(key);
        if (newValue instanceof ConfigurationSection section) {
            for (String sectionKey : section.getKeys(false)) {
                checkKey(existingConfig, resourceConfig, key + "." + sectionKey);
            }
        } else if (currentValue == null) {
            existingConfig.set(key, newValue);
        }
    }

    public boolean isAutoUpdate() {
        return Networks.getInstance().getConfig().getBoolean("auto-update", false);
    }

    public boolean isDebug() {
        return Networks.getInstance().getConfig().getBoolean("debug", false);
    }

    public String getLanguage() {
        return Networks.getInstance().getConfig().getString("language", "zh-CN");
    }

    public boolean isForceCheckLore() {
        return Networks.getInstance().getConfig().getBoolean("rpg-fix.force-check-lore", false);
    }

    public int getPersistentThreshold() {
        return Networks.getInstance().getConfig().getInt("speed-up.persistent-threshold", 15);
    }

    public int getCacheMissThreshold() {
        return Networks.getInstance().getConfig().getInt("speed-up.cache-miss-threshold", 15);
    }

    public int getReduceMs() {
        return Networks.getInstance().getConfig().getInt("speed-down.reduce-ms", 8000);
    }

    public int getTransportMissThreshold() {
        return Networks.getInstance().getConfig().getInt("speed-down.transport-miss-threshold", 120);
    }

    public void saveAll() {
        Networks.getInstance().getLogger().info(Networks.getLocalizationService().getString("messages.save-all"));
    }
}
