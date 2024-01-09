// EffectItems.java
package me.mejbha.headgear.effect;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EffectItems {
    private static final Map<Material, HeadEffect> effectItems = new HashMap<>();
    private static boolean enabled;

    public static void loadConfig(JavaPlugin plugin) {
        effectItems.clear();

        // Load effect items from effect_items.yml
        File effectFile = new File(plugin.getDataFolder(), "effect_items.yml");

        if (!effectFile.exists()) {
            plugin.saveResource("effect_items.yml", false);
        }

        try (InputStream inputStream = plugin.getResource("effect_items.yml")) {
            FileConfiguration effectConfig = YamlConfiguration.loadConfiguration(effectFile);

            // Check if the effect feature is enabled
            enabled = effectConfig.getBoolean("enabled", true);

            if (enabled && effectConfig.isConfigurationSection("effects")) {
                for (String itemName : effectConfig.getConfigurationSection("effects").getKeys(false)) {
                    Material material = Material.getMaterial(itemName);
                    if (material != null) {
                        HeadEffect headEffect = new HeadEffect(
                                EffectType.valueOf(effectConfig.getString("effects." + itemName + ".type")),
                                effectConfig.getInt("effects." + itemName + ".amplifier")
                        );
                        effectItems.put(material, headEffect);
                    }
                }
            } else {
                plugin.getLogger().warning("Effect feature is disabled or missing 'effects' section in effect_items.yml");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static HeadEffect getHeadEffect(Material material) {
        return effectItems.get(material);
    }
}
