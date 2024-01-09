// HeadGear.java
package me.mejbha.headgear;

import me.mejbha.headgear.effect.EffectItems;
import me.mejbha.headgear.util.Metrics;
import me.mejbha.headgear.util.UpdateAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class HeadGear extends JavaPlugin {
    private static HeadGear instance;
    public static HeadGear getInstance() {
        return instance;
    }
    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic
        saveDefaultConfig();
        EffectItems.loadConfig(this); // Load effect items configuration
        getServer().getPluginManager().registerEvents(new HeadListener(this), this);
        getLogger().info("======================================");
        getLogger().info("HeadGear has been enabled!");
        getLogger().info("Version : 2.0");
        getLogger().info("======================================");
        new Metrics(this, 20556);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("HeadGear has been disabled!");
    }
    public void updateChecker() {
        UpdateAPI updateAPI = new UpdateAPI();

        if (updateAPI.hasGithubUpdate("XpiderMon", "HeadGear")) {
            String newVersion = updateAPI.getGithubVersion("XpiderMon", "HeadGear");
            if (getConfig().getBoolean("Config.Update-Notify")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.hasPermission("headgear.notify")) {
                        p.sendMessage("--------------------------------");
                        p.sendMessage("You are using HeadGear " + getDescription().getVersion());
                        p.sendMessage("However version " + newVersion + " is available.");
                        p.sendMessage("You can download it from: " + "https://www.spigotmc.org/resources/99976/");
                        p.sendMessage("--------------------------------");
                    }
                }
            }
            if (!Bukkit.getOnlinePlayers().isEmpty()){
                Bukkit.getLogger().info("--------------------------------");
                Bukkit.getLogger().info("You are using HeadGear v" + getDescription().getVersion());
                Bukkit.getLogger().info("However version " + newVersion + " is available.");
                Bukkit.getLogger().info("You can download it from: " + "https://www.spigotmc.org/resources/99976/");
                Bukkit.getLogger().info("--------------------------------");
            }

        }
    }


}

