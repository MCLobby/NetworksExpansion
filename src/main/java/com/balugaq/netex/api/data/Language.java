package com.balugaq.netex.api.data;

import com.balugaq.netex.utils.Debug;
import com.google.common.base.Preconditions;
import java.io.File;
import java.io.IOException;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class Language {
    private final @Nonnull String lang;
    private final @Nonnull File currentFile;
    private final @Nonnull FileConfiguration currentConfig;

    @ParametersAreNonnullByDefault
    public Language(String lang, File currentFile, FileConfiguration defaultConfig) {
        Preconditions.checkArgument(lang != null, "Language key cannot be null");
        Preconditions.checkArgument(currentFile != null, "Current file cannot be null");
        Preconditions.checkArgument(defaultConfig != null, "default config cannot be null");
        this.lang = lang;
        this.currentFile = currentFile;
        this.currentConfig = YamlConfiguration.loadConfiguration(currentFile);
        this.currentConfig.setDefaults(defaultConfig);

        for (String key : defaultConfig.getKeys(true)) {
            if (!this.currentConfig.contains(key)) {
                this.currentConfig.set(key, defaultConfig.get(key));
            }
        }

        this.save();
    }

    @Nonnull
    public String getName() {
        return this.lang;
    }

    @Nonnull
    public FileConfiguration getLang() {
        return this.currentConfig;
    }

    public void save() {
        try {
            this.currentConfig.save(this.currentFile);
        } catch (IOException e) {
            Debug.trace(e);
        }
    }
}
