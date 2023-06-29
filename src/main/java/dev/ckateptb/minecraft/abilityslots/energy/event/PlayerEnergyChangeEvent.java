package dev.ckateptb.minecraft.abilityslots.energy.event;

import dev.ckateptb.minecraft.abilityslots.user.PlayerAbilityUser;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class PlayerEnergyChangeEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    public static synchronized HandlerList getHandlerList() {
        return HANDLERS;
    }

    private boolean cancelled = false;
    private final PlayerAbilityUser user;
    private final double previous;
    private double energy;

    public PlayerEnergyChangeEvent(PlayerAbilityUser user, double previous, double energy) {
        super(!Bukkit.isPrimaryThread());
        this.user = user;
        this.previous = previous;
        this.energy = energy;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
