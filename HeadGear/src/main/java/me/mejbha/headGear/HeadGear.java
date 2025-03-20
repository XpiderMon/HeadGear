package me.mejbha.headGear;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class HeadGear extends JavaPlugin {
    private FileConfiguration itemsConfig;
    private static HeadGear instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        reloadConfig();

        // Load items.yml
        this.saveResource("items.yml", false);
        reloadItemsConfig();

        // Register event listeners
        getServer().getPluginManager().registerEvents(new HeadListener(this), this);

        getLogger().info("=================================");
        getLogger().info(" HeadGear has been enabled! ");
        getLogger().info(" Version : 3.0 ");
        getLogger().info("=================================");

        // Enable Metrics (bstats)
        int pluginId = 20556; // <-- Replace with the id of your plugin!
        Metrics metrics = new Metrics(this, pluginId);
    }

    public void reloadItemsConfig() {
        itemsConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "items.yml"));
        InputStream defaultStream = getResource("items.yml");

        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            itemsConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getItemsConfig() {
        if (itemsConfig == null) {
            reloadItemsConfig();
        }
        return itemsConfig;
    }

    public static HeadGear getInstance() {
        return instance;
    }
}
