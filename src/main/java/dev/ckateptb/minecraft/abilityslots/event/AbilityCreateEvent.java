package dev.ckateptb.minecraft.abilityslots.event;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class AbilityCreateEvent extends Event {
    private final Ability ability;
    private static final HandlerList HANDLERS = new HandlerList();

    public AbilityCreateEvent(Ability ability) {
        super(true);
        this.ability = ability;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}