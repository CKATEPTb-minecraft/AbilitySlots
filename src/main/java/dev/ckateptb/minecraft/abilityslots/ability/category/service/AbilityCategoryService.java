package dev.ckateptb.minecraft.abilityslots.ability.category.service;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.ability.category.IAbilityCategory;
import dev.ckateptb.minecraft.abilityslots.event.AbilitySlotsReloadEvent;
import lombok.CustomLog;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Сервис выступает в качестве хранилища для категорий.
 */
@CustomLog
@Component
public class AbilityCategoryService implements Listener {
    private final Map<String, IAbilityCategory> categories = new HashMap<>();

    /**
     * Найти категорию по названию.
     *
     * @param name Системное название категории.
     * @return Контейнер, который содержит категорию, если та была найдена.
     */
    public Optional<? extends IAbilityCategory> findCategory(String name) {
        return Optional.ofNullable(this.categories.get(name.toLowerCase()));
    }

    /**
     * Зарегистрировать категорию для дальнейшего использования в сервисе.
     *
     * @param category Экземпляр категории.
     */
    public void registerCategory(IAbilityCategory category) {
        String name = category.getName();
        log.info("Registering a new ability category with name {}", name);
        this.categories.put(name.toLowerCase(), category);
    }

    /**
     * <p><b>НЕ ИСПОЛЬЗУЙТЕ ЭТОТ МЕТОД!</b></p>
     * Данный метод необходим, для перезагрузки хранилища с категориями и вызывается событиями Bukkit.
     *
     * @param event Событие перезагрузки плагина AbilitySlots.
     */
    @EventHandler(priority = EventPriority.LOW)
    private void on(AbilitySlotsReloadEvent event) {
        log.info("Delete all registered ability categories");
        this.categories.clear();
    }
}
