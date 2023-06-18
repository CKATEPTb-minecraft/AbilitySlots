package dev.ckateptb.minecraft.abilityslots.cooldown;

import dev.ckateptb.minecraft.abilityslots.ability.IAbility;

import java.util.HashMap;
import java.util.Map;

public class CooldownHolder implements ICooldownHolder {
    private final Map<Class<? extends IAbility>, Long> cooldowns = new HashMap<>();

    @Override
    public void setCooldown(Class<? extends IAbility> ability, long duration) {
        this.cooldowns.put(ability, duration + System.currentTimeMillis());
    }

    @Override
    public boolean hasCooldown(Class<? extends IAbility> ability) {
        return this.getCooldown(ability) > System.currentTimeMillis();
    }

    @Override
    public long getCooldown(Class<? extends IAbility> ability) {
        return this.cooldowns.getOrDefault(ability, 0L);
    }
}
