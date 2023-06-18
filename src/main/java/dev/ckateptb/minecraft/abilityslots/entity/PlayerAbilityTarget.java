package dev.ckateptb.minecraft.abilityslots.entity;

import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MainHand;

@Getter
public class PlayerAbilityTarget<T extends Player> extends LivingEntityAbilityTarget<T> {
    protected PlayerAbilityTarget(T entity) {
        super(entity);
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.handle.hasPermission(permission);
    }

    @Override
    public GameMode getGameMode() {
        return this.handle.getGameMode();
    }

    @Override
    public MainHand getMainHand() {
        return this.handle.getMainHand();
    }
}