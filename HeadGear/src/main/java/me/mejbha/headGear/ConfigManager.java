package me.mejbha.headGear;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class ConfigManager {
    private final FileConfiguration config;
    private final FileConfiguration itemsConfig;

    public ConfigManager(HeadGear plugin) {
        this.config = plugin.getConfig();
        this.itemsConfig = plugin.getItemsConfig();
    }

    public boolean isAllowedItem(Material material) {
        List<String> blacklistedItems = config.getStringList("blacklisted-items");
        return !blacklistedItems.contains(material.toString());
    }

    public PotionEffectType getEffectType(String itemName) {
        ConfigurationSection itemConfig = itemsConfig.getConfigurationSection("items." + itemName);
        return itemConfig != null ? PotionEffectType.getByName(itemConfig.getString("effect")) : null;
    }

    public int getEffectDuration(String itemName) {
        return itemsConfig.getInt("items." + itemName + ".duration", 10);
    }

    public int getEffectLevel(String itemName) {
        return itemsConfig.getInt("items." + itemName + ".level", 1);
    }

    public int getCooldown(String itemName) {
        ConfigurationSection itemConfig = itemsConfig.getConfigurationSection("items." + itemName);
        if (itemConfig != null && itemConfig.contains("cooldown")) {
            return itemConfig.getInt("cooldown");
        }
        return config.getInt("default-cooldown", 10); // Uses config.yml default if not found
    }

    public String getMessage(String key) {
        return config.getString("messages." + key, "&cError: Message not found.");
    }
}
