package dev.ckateptb.minecraft.abilityslots.entity;

import lombok.Getter;
import lombok.experimental.Delegate;
import org.bukkit.entity.Player;

import java.util.Objects;

@Getter
public class PlayerAbilityTarget extends LivingEntityAbilityTarget implements Player {
    @Delegate
    protected Player handle_;

    protected PlayerAbilityTarget(Player player) {
        super(player);
        this.updateDelegator(player);
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    public boolean equals(Object other) {
        if (other instanceof PlayerAbilityTarget adapter) {
            other = adapter.handle_;
        }
        return Objects.equals(this.handle_, other);
    }

    public int hashCode() {
        return this.handle_.hashCode();
    }

    public void updateDelegator(Player player) {
        this.handle_ = player;
    }
}