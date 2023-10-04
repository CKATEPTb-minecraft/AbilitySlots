package dev.ckateptb.minecraft.abilityslots.ability.declaration.generated;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.category.AbilityCategory;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.generated.annotation.AbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.enums.ActivationMethod;
import dev.ckateptb.minecraft.abilityslots.event.AbilityCreateEvent;
import dev.ckateptb.minecraft.abilityslots.user.AbilityUser;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.World;
import reactor.core.publisher.Mono;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Экземпляр данного класса создается автоматически для способностей отмеченных аннотацией {@link AbilityDeclaration}
 *
 * @param <A>
 */
@Getter
@Setter
public class GeneratedAbilityDeclaration<A extends Ability> implements IAbilityDeclaration<A> {
    private final String name;
    private final AbilityCategory category;
    private final String author;
    private final Class<A> abilityClass;
    private final boolean bindable;
    private final ActivationMethod[] activationMethods;
    private final Constructor<A> newInstanceConstructor;
    private final Method setDeclaration;
    private boolean enabled;
    private String displayName;
    private String description;
    private String instruction;
    private final Map<Field, Object> config = new ConcurrentHashMap<>();

    @SneakyThrows
    public GeneratedAbilityDeclaration(AbilityDeclaration declaration, AbilityCategory category, Class<A> abilityClass) {
        this.name = declaration.name();
        this.category = category;
        this.author = declaration.author();
        this.abilityClass = abilityClass;
        this.bindable = declaration.bindable();
        this.activationMethods = declaration.activators();
        this.newInstanceConstructor = this.abilityClass.getConstructor();
        this.setDeclaration = Ability.class.getDeclaredMethod("setDeclaration", IAbilityDeclaration.class);
        this.setDeclaration.setAccessible(true);
        this.enabled = true;
        this.displayName = declaration.displayName();
        this.description = declaration.description();
        this.instruction = declaration.instruction();
    }

    @Override
    public Mono<A> createAbility(AbilityUser user, World world, ActivationMethod method) {
        return Mono.just(this.newInstanceConstructor)
                .<A>handle((constructor, sink) -> {
                    try {
                        A instance = constructor.newInstance();
                        for (Map.Entry<Field, Object> entry : this.config.entrySet()) {
                            Field field = entry.getKey();
                            Object value = entry.getValue();
                            field.set(instance, value);
                        }
                        this.setDeclaration.invoke(instance, this);
                        instance.setUser(user);
                        instance.setWorld(world);
                        instance.setActivationMethod(method);
                        sink.next(instance);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        sink.error(new RuntimeException(e));
                    }
                })
                .onErrorComplete()
//                .publishOn(new SyncScheduler()) // If we should call event sync
                .doOnNext(instance -> {
                    Bukkit.getPluginManager().callEvent(new AbilityCreateEvent(instance));
                });
    }

    @Override
    public void setFieldValue(Field field, Object value) {
        this.config.put(field, value);
    }
}
