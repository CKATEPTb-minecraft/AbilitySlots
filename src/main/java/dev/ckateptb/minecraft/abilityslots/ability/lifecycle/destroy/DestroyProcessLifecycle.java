package dev.ckateptb.minecraft.abilityslots.ability.lifecycle.destroy;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.common.tableclothcontainer.annotation.PostConstruct;
import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.collision.CollidableAbility;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.lifecycle.AbstractAbilityLifecycle;
import dev.ckateptb.minecraft.abilityslots.event.AbilityCreateEvent;
import dev.ckateptb.minecraft.abilityslots.user.AbilityUser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import reactor.core.scheduler.Schedulers;

@Component
public class DestroyProcessLifecycle extends AbstractAbilityLifecycle<Ability> implements Listener {
    @PostConstruct
    public void process() {
        this.flux
                .publishOn(Schedulers.boundedElastic())
                .subscribe(ability -> {
                    ability.setLocked(true);
                    try {
                        ability.destroy(null);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    AbilityUser user = ability.getUser();
                    user.removeAbility(ability);
                });
    }

    @EventHandler
    private void on(AbilityCreateEvent event) {
        Ability ability = event.getAbility();
        ability.setDestroyProcessLifecycle(this);
    }
}
