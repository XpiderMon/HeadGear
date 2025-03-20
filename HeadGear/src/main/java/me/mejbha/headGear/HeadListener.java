package me.mejbha.headGear;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class HeadListener implements Listener {
    private final HeadGear plugin;
    private final CooldownManager cooldownManager;
    private final EffectManager effectManager;
    private final ConfigManager configManager;

    public HeadListener(HeadGear plugin) {
        this.plugin = plugin;
        this.cooldownManager = new CooldownManager();
        this.effectManager = new EffectManager();
        this.configManager = new ConfigManager(plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getClickedInventory().getType() != org.bukkit.event.inventory.InventoryType.PLAYER) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (event.getSlotType() == org.bukkit.event.inventory.InventoryType.SlotType.ARMOR && event.getSlot() == 39) { // Helmet slot
            ItemStack cursorItem = event.getCursor();
            ItemStack currentHeadgear = player.getInventory().getHelmet();

            if (cursorItem != null && cursorItem.getType() != Material.AIR) {
                if (!player.hasPermission("headgear.use")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to wear this item!");
                    return;
                }

                String itemName = cursorItem.getType().name().toLowerCase();
                if (!configManager.isAllowedItem(cursorItem.getType())) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', configManager.getMessage("item-not-allowed")));
                    return;
                }

                PotionEffectType effectType = configManager.getEffectType(itemName);
                int duration = configManager.getEffectDuration(itemName);
                int level = configManager.getEffectLevel(itemName);
                int cooldown = configManager.getCooldown(itemName);
                int effectCooldown = plugin.getConfig().getInt("effect-cooldown", 15);

                UUID playerUUID = player.getUniqueId();

                if (cooldownManager.isOnCooldown(playerUUID, cooldown)) {
                    int remainingTime = cooldownManager.getRemainingCooldown(playerUUID, cooldown);
                    String cooldownMessage = configManager.getMessage("cooldown-active").replace("{time}", String.valueOf(remainingTime));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', cooldownMessage));
                    return;
                }

                if (cooldownManager.isOnEffectCooldown(playerUUID, effectCooldown)) {
                    int remainingTime = cooldownManager.getRemainingEffectCooldown(playerUUID, effectCooldown);
                    String effectCooldownMessage = configManager.getMessage("effect-cooldown-active").replace("{time}", String.valueOf(remainingTime));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', effectCooldownMessage));
                    return;
                }

                if (effectType != null) {
                    effectManager.applyEffect(player, effectType, duration, level);
                    player.sendMessage(ChatColor.GREEN + "You feel the power of the " + itemName + "!");
                    cooldownManager.setCooldown(playerUUID);

                    // Start post-effect cooldown after effect ends
                    Bukkit.getScheduler().runTaskLater(plugin, () -> cooldownManager.setEffectCooldown(playerUUID), duration * 20L);
                }

                player.getInventory().setHelmet(cursorItem);
                event.setCancelled(true);
                cursorItem.setAmount(0);
            } else if (currentHeadgear != null) {
                // Remove effects when taking off headgear
                effectManager.removeEffects(player, configManager.getEffectType(currentHeadgear.getType().name().toLowerCase()));
            }
        }
    }
}
