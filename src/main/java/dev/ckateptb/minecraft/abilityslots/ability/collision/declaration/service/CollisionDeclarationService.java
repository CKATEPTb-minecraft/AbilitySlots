package dev.ckateptb.minecraft.abilityslots.ability.collision.declaration.service;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.ability.collision.CollidableAbility;
import dev.ckateptb.minecraft.abilityslots.ability.collision.declaration.ICollisionDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.collision.declaration.generated.GeneratedCollisionDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.generated.annotation.AbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.service.AbilityDeclarationService;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.event.AbilityCreateEvent;
import dev.ckateptb.minecraft.abilityslots.event.AbilitySlotsReloadEvent;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.*;
import java.util.stream.Collectors;

@Component
@CustomLog
@RequiredArgsConstructor
public class CollisionDeclarationService implements Listener {
    private final Map<IAbilityDeclaration<? extends CollidableAbility>, List<String>> namedDeclarations = new HashMap<>();
    private final Map<IAbilityDeclaration<? extends CollidableAbility>, ICollisionDeclaration> declarations = new HashMap<>();
    private final AbilitySlotsConfig config;
    private final AbilityDeclarationService declarationService;

    @SuppressWarnings("all")
    public Optional<ICollisionDeclaration> findDeclaration(IAbilityDeclaration<? extends CollidableAbility> declaration) {
        if (this.declarations.containsKey(declaration)) return Optional.ofNullable(this.declarations.get(declaration));
        return Optional.ofNullable(this.namedDeclarations.get(declaration)).map(names -> {
            Set<Class<? extends CollidableAbility>> abilities = names.stream()
                    .map(abilityName -> this.declarationService.findDeclaration(abilityName).orElse(null))
                    .filter(Objects::nonNull)
                    .map(IAbilityDeclaration::getAbilityClass)
                    .filter(CollidableAbility.class::isAssignableFrom)
                    .map(aClass -> (Class<? extends CollidableAbility>) aClass)
                    .collect(Collectors.toUnmodifiableSet());
            GeneratedCollisionDeclaration generatedCollisionDeclaration = new GeneratedCollisionDeclaration(abilities);
            return this.declarations.put(declaration, generatedCollisionDeclaration);
        });
    }

    public void registerDeclaration(IAbilityDeclaration<? extends CollidableAbility> abilityDeclaration, ICollisionDeclaration collisionDeclaration) {
        this.namedDeclarations.put(abilityDeclaration, this.config.loadCollision(abilityDeclaration, collisionDeclaration.getDestructible().stream()
                .map(ability -> ability.getAnnotation(AbilityDeclaration.class))
                .filter(Objects::nonNull)
                .map(AbilityDeclaration::name)
                .collect(Collectors.toList())));
    }

    @EventHandler
    private void on(AbilityCreateEvent event) {
        if (event.getAbility() instanceof CollidableAbility ability) {
            IAbilityDeclaration<? extends CollidableAbility> declaration = ability.getDeclaration();
            this.findDeclaration(declaration).ifPresent(ability::setCollisionDeclaration);
        }
    }

    /**
     * <p><b>НЕ ИСПОЛЬЗУЙТЕ ЭТОТ МЕТОД!</b></p>
     * Данный метод необходим, для перезагрузки хранилища с декларациями и вызывается событиями Bukkit.
     *
     * @param event Событие перезагрузки плагина AbilitySlots.
     */
    @EventHandler(priority = EventPriority.LOW)
    private void on(AbilitySlotsReloadEvent event) {
        if (this.namedDeclarations.size() > 0) log.info("Delete all registered collision declarations");
        this.declarations.clear();
        this.namedDeclarations.clear();
    }
}
