package me.mejbha.headGear;

import java.util.HashMap;
import java.util.UUID;

public class CooldownManager {
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private final HashMap<UUID, Long> effectCooldowns = new HashMap<>();

    public boolean isOnCooldown(UUID playerUUID, int cooldownSeconds) {
        long currentTime = System.currentTimeMillis();
        return cooldowns.containsKey(playerUUID) &&
                (currentTime - cooldowns.get(playerUUID)) < (cooldownSeconds * 1000L);
    }

    public boolean isOnEffectCooldown(UUID playerUUID, int cooldownSeconds) {
        long currentTime = System.currentTimeMillis();
        return effectCooldowns.containsKey(playerUUID) &&
                (currentTime - effectCooldowns.get(playerUUID)) < (cooldownSeconds * 1000L);
    }

    public int getRemainingCooldown(UUID playerUUID, int cooldownSeconds) {
        if (!cooldowns.containsKey(playerUUID)) {
            return 0;
        }
        long elapsedTime = (System.currentTimeMillis() - cooldowns.get(playerUUID)) / 1000;
        int remainingTime = cooldownSeconds - (int) elapsedTime;
        return Math.max(remainingTime, 0);
    }

    public int getRemainingEffectCooldown(UUID playerUUID, int cooldownSeconds) {
        if (!effectCooldowns.containsKey(playerUUID)) {
            return 0;
        }
        long elapsedTime = (System.currentTimeMillis() - effectCooldowns.get(playerUUID)) / 1000;
        int remainingTime = cooldownSeconds - (int) elapsedTime;
        return Math.max(remainingTime, 0);
    }

    public void setCooldown(UUID playerUUID) {
        cooldowns.put(playerUUID, System.currentTimeMillis());
    }

    public void setEffectCooldown(UUID playerUUID) {
        effectCooldowns.put(playerUUID, System.currentTimeMillis());
    }
}
