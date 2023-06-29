package dev.ckateptb.minecraft.abilityslots.entity;

import dev.ckateptb.minecraft.atom.adapter.AdapterUtils;
import dev.ckateptb.minecraft.atom.adapter.entity.PlayerAdapter;
import lombok.Getter;
import lombok.experimental.Delegate;
import org.bukkit.entity.Player;

import java.util.Objects;

@Getter
public class PlayerAbilityTarget extends LivingEntityAbilityTarget implements Player {
    @Delegate
    protected PlayerAdapter handle_;

    protected PlayerAbilityTarget(Player player) {
        super(player);
        this.updatePlayerAdapter(player);
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

    public void updatePlayerAdapter(Player player) {
        this.handle_ = AdapterUtils.adapt(player);
    }
}