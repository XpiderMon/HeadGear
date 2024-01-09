// HeadEffect.java
package me.mejbha.headgear.effect;

public class HeadEffect {
    private final EffectType type;
    private final int amplifier;

    public HeadEffect(EffectType type, int amplifier) {
        this.type = type;
        this.amplifier = amplifier;
    }

    public EffectType getType() {
        return type;
    }

    public int getAmplifier() {
        return amplifier;
    }
}
