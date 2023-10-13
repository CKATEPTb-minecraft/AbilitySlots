package dev.ckateptb.minecraft.abilityslots.ability.lifecycle.destroy;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.common.tableclothcontainer.annotation.PostConstruct;
import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.lifecycle.AbstractAbilityLifecycle;
import dev.ckateptb.minecraft.abilityslots.event.AbilityCreateEvent;
import dev.ckateptb.minecraft.abilityslots.user.AbilityUser;
import lombok.CustomLog;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.slf4j.helpers.MessageFormatter;
import reactor.core.scheduler.Schedulers;

@Component
@CustomLog
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
                        IAbilityDeclaration<? extends Ability> declaration = ability.getDeclaration();
                        log.error(MessageFormatter.arrayFormat(
                                "There was an error on ability {} was destroyed. Contact the author {}.",
                                new Object[]{declaration.getName(), declaration.getAuthor()}
                        ).getMessage(), throwable);
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
