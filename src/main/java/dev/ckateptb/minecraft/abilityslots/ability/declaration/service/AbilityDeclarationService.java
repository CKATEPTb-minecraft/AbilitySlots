package dev.ckateptb.minecraft.abilityslots.ability.declaration.service;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.event.AbilitySlotsReloadEvent;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.*;

/**
 * Сервис выступает в качестве хранилища для деклараций.
 */
@CustomLog
@Component
@RequiredArgsConstructor
public class AbilityDeclarationService implements Listener {
    private final Map<String, IAbilityDeclaration<? extends Ability>> declarations = new HashMap<>();
    private final AbilitySlotsConfig config;

    /**
     * Найти декларацию для способности по названию.
     *
     * @param name Системное название способности.
     * @return Контейнер, который содержит декларацию, если та была найдена.
     */
    public Optional<IAbilityDeclaration<? extends Ability>> findDeclaration(String name) {
        return Optional.ofNullable(this.declarations.get(name.toLowerCase()));
    }

    /**
     * Получить список всех, зарегистрированных, деклараций.
     *
     * @return Неизменяемый список деклараций.
     */
    public Collection<IAbilityDeclaration<? extends Ability>> getDeclarations() {
        return Collections.unmodifiableCollection(this.declarations.values());
    }

    /**
     * Зарегистрировать декларацию способности для дальнейшего использования в сервисе.
     *
     * @param declaration Декларация способности.
     */
    public void registerDeclaration(IAbilityDeclaration<? extends Ability> declaration) {
        String name = declaration.getName();
        if (!name.matches("[a-zA-Z]+")) {
            log.warn("Found a new ability ({}), but the developer made a mistake. " +
                    "The name must contain characters from a-zA-Z", name);
        } else {
            log.info("Registering a ability declaration for the {} Ability", name);
            this.declarations.put(name.toLowerCase(), declaration);
            this.config.loadAbility(declaration);
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
        log.info("Delete all registered ability declarations");
        this.declarations.clear();
    }
}
