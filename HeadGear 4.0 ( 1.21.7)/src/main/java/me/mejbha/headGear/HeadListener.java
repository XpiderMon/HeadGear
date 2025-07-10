package me.mejbha.headGear;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;

public class HeadListener implements Listener {
    private final HeadGear plugin;
    private final CooldownManager cooldownManager;
    private final EffectManager effectManager;
    private final ConfigManager configManager;
    private final HealthManager healthManager;

    public HeadListener(HeadGear plugin) {
        this.plugin = plugin;
        this.cooldownManager = new CooldownManager();
        this.effectManager = new EffectManager();
        this.configManager = new ConfigManager(plugin);
        this.healthManager = new HealthManager();
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
                int healthBoost = configManager.getHealthBoost(itemName);

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

                // Handle existing headgear
                if (currentHeadgear != null && currentHeadgear.getType() != Material.AIR) {
                    // Try to add the current headgear back to the player's inventory
                    HashMap<Integer, ItemStack> remaining = player.getInventory().addItem(currentHeadgear);
                    if (!remaining.isEmpty()) {
                        // If inventory is full, drop the item
                        player.getWorld().dropItemNaturally(player.getLocation(), currentHeadgear);
                        player.sendMessage(ChatColor.YELLOW + "Your previous headgear was dropped as your inventory is full!");
                    }
                    // Remove effects and health boost of the old headgear
                    effectManager.removeEffects(player, configManager.getEffectType(currentHeadgear.getType().name().toLowerCase()));
                    if (configManager.isHealthBoostEnabled()) {
                        healthManager.removeHealthBoost(player);
                    }
                }

                // Apply new headgear
                if (effectType != null) {
                    effectManager.applyEffect(player, effectType, duration, level);
                    player.sendMessage(ChatColor.GREEN + "You feel the power of the " + itemName + "!");
                    cooldownManager.setCooldown(playerUUID);

                    // Start post-effect cooldown after effect ends
                    Bukkit.getScheduler().runTaskLater(plugin, () -> cooldownManager.setEffectCooldown(playerUUID), duration * 20L);
                }

                // Apply health boost if enabled and specified
                if (configManager.isHealthBoostEnabled() && healthBoost > 0) {
                    healthManager.applyHealthBoost(player, healthBoost);
                    player.sendMessage(ChatColor.GREEN + "You gained " + healthBoost + " extra heart" + (healthBoost > 1 ? "s" : "") + "!");
                }

                // Set the new item as the helmet and clear the cursor
                player.getInventory().setHelmet(cursorItem.clone()); // Clone to prevent modifying the original
                event.setCancelled(true);
                cursorItem.setAmount(0);
            } else if (currentHeadgear != null) {
                // Remove effects and health boost when taking off headgear
                effectManager.removeEffects(player, configManager.getEffectType(currentHeadgear.getType().name().toLowerCase()));
                if (configManager.isHealthBoostEnabled()) {
                    healthManager.removeHealthBoost(player);
                }
            }
        }
    }
}