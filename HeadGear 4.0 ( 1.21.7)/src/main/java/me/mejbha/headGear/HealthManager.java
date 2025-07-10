package me.mejbha.headGear;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class HealthManager {
    private static final String HEALTH_BOOST_KEY = "HeadGear_HealthBoost";

    @SuppressWarnings("deprecation")
    public void applyHealthBoost(Player player, int extraHearts) {
        if (extraHearts <= 0) return;

        // Store the original health if not already stored
        if (!player.hasMetadata(HEALTH_BOOST_KEY)) {
            player.setMetadata(HEALTH_BOOST_KEY, new FixedMetadataValue(HeadGear.getInstance(), player.getMaxHealth()));
        }

        // Add extra health (1 heart = 2 health points)
        double newMaxHealth = player.getMaxHealth() + (extraHearts * 2.0);
        player.setMaxHealth(newMaxHealth);

        // Ensure current health doesn't exceed new max
        if (player.getHealth() > newMaxHealth) {
            player.setHealth(newMaxHealth);
        }
    }

    @SuppressWarnings("deprecation")
    public void removeHealthBoost(Player player) {
        if (player.hasMetadata(HEALTH_BOOST_KEY)) {
            // Restore original max health
            double originalMaxHealth = player.getMetadata(HEALTH_BOOST_KEY).get(0).asDouble();
            player.setMaxHealth(originalMaxHealth);

            // Ensure current health doesn't exceed restored max
            if (player.getHealth() > originalMaxHealth) {
                player.setHealth(originalMaxHealth);
            }

            // Remove metadata
            player.removeMetadata(HEALTH_BOOST_KEY, HeadGear.getInstance());
        }
    }
}