package dev.ckateptb.minecraft.abilityslots.ability.declaration.generated;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.category.AbilityCategory;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.generated.annotation.AbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.enums.ActivationMethod;
import dev.ckateptb.minecraft.abilityslots.event.AbilityCreateEvent;
import dev.ckateptb.minecraft.atom.scheduler.SyncScheduler;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

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
    @SneakyThrows
    public A createAbility() {
        A instance = this.newInstanceConstructor.newInstance();
        this.setDeclaration.invoke(instance, this);
        new SyncScheduler().schedule(() -> Bukkit.getPluginManager().callEvent(new AbilityCreateEvent(instance)));
        return instance;
    }
}
