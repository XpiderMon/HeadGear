// HeadListener.java
package me.mejbha.headgear;

import me.mejbha.headgear.effect.EffectItems;
import me.mejbha.headgear.effect.HeadEffect;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class HeadListener implements Listener {
    private final JavaPlugin plugin;

    public HeadListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getClickedInventory().getType() != InventoryType.PLAYER) {
            return; // Not clicking the player's inventory
        }

        Player player = (Player) event.getWhoClicked();

        if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
            // Check if the clicked slot is the helmet slot
            if (event.getSlot() == 39) { // Helmet slot index for the player's inventory
                ItemStack cursorItem = event.getCursor();
                ItemStack helmet = player.getInventory().getHelmet();

                if (cursorItem != null && cursorItem.getType() != Material.AIR) {
                    if (helmet == null || helmet.getType() == Material.AIR) {
                        if (isAllowedItem(cursorItem.getType())) {
                            if (!player.hasPermission("headgear.use")) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You don't have permission to use items on your head"));
                                return;
                            }

                            // Apply head effect if defined for the item
                            if (player.hasPermission("headgear.effect.allow")){
                                HeadEffect headEffect = EffectItems.getHeadEffect(cursorItem.getType());
                                if (headEffect != null) {
                                    PotionEffectType potionEffectType = headEffect.getType().getPotionEffectType();
                                    player.addPotionEffect(new PotionEffect(potionEffectType, -1, headEffect.getAmplifier()));
                                }

                            }

                            // Allow the item to be placed ein the helmet slot
                            player.getInventory().setHelmet(cursorItem);
                            event.setCancelled(true);
                            cursorItem.setAmount(0);
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.item-not-allowed")));
                        }
                    }
                } else {
                    // Remove head effect when the item is removed
                    HeadEffect headEffect = EffectItems.getHeadEffect(helmet.getType());
                    if (headEffect != null) {
                        PotionEffectType potionEffectType = headEffect.getType().getPotionEffectType();
                        player.removePotionEffect(potionEffectType);
                    }
                }
            }
        }
    }

    private boolean isAllowedItem(Material material) {
        List<String> blacklistedItems = plugin.getConfig().getStringList("blacklisted-items");
        return !blacklistedItems.contains(material.toString());
    }
}
