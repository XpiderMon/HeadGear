package me.mejbha.headGear;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EffectManager {
    public void applyEffect(Player player, PotionEffectType effectType, int duration, int level) {
        if (effectType != null) {
            player.addPotionEffect(new PotionEffect(effectType, 20 * duration, level));
        }
    }

    public void removeEffects(Player player, PotionEffectType effectType) {
        if (effectType != null) {
            player.removePotionEffect(effectType);
        }
    }
}
