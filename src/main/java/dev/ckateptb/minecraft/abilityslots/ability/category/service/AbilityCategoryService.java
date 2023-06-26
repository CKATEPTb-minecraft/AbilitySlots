package dev.ckateptb.minecraft.abilityslots.ability.category.service;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.ability.category.AbilityCategory;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.event.AbilitySlotsReloadEvent;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.*;

/**
 * Сервис выступает в качестве хранилища для категорий.
 */
@CustomLog
@Component
@RequiredArgsConstructor
public class AbilityCategoryService implements Listener {
    private final Map<String, AbilityCategory> categories = new HashMap<>();
    private final AbilitySlotsConfig config;

    /**
     * Найти категорию по названию.
     *
     * @param name Системное название категории.
     * @return Контейнер, который содержит категорию, если та была найдена.
     */
    public Optional<? extends AbilityCategory> findCategory(String name) {
        return Optional.ofNullable(this.categories.get(name.toLowerCase()));
    }

    /**
     * Получить список всех, зарегистрированных, категорий.
     *
     * @return Неизменяемый список деклараций.
     */
    public Collection<? extends AbilityCategory> getCategories() {
        return Collections.unmodifiableCollection(this.categories.values());
    }

    /**
     * Зарегистрировать категорию для дальнейшего использования в сервисе.
     *
     * @param category Экземпляр категории.
     */
    public void registerCategory(AbilityCategory category) {
        String name = category.getName();
        if (!name.matches("[a-zA-Z]+")) {
            log.warn("Found a new category for abilities ({}), but the developer made a mistake. " +
                    "The name must contain characters from a-zA-Z", name);
        } else {
            log.info("Registering a new ability category with name {}", name);
            this.categories.put(name.toLowerCase(), category);
            this.config.loadCategory(category);
        }
    }

    /**
     * <p><b>НЕ ИСПОЛЬЗУЙТЕ ЭТОТ МЕТОД!</b></p>
     * Данный метод необходим, для перезагрузки хранилища с категориями и вызывается событиями Bukkit.
     *
     * @param event Событие перезагрузки плагина AbilitySlots.
     */
    @EventHandler(priority = EventPriority.LOW)
    private void on(AbilitySlotsReloadEvent event) {
        if (this.categories.size() > 0) log.info("Delete all registered ability categories");
        this.categories.clear();
    }
}
