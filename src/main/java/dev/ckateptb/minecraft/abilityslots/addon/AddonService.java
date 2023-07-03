package dev.ckateptb.minecraft.abilityslots.addon;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.category.AbilityCategory;
import dev.ckateptb.minecraft.abilityslots.ability.category.annotation.CategoryDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.category.service.AbilityCategoryService;
import dev.ckateptb.minecraft.abilityslots.ability.collision.CollidableAbility;
import dev.ckateptb.minecraft.abilityslots.ability.collision.declaration.generated.GeneratedCollisionDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.collision.declaration.generated.annotation.CollisionDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.collision.declaration.service.CollisionDeclarationService;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.generated.GeneratedAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.generated.annotation.AbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.service.AbilityDeclarationService;
import dev.ckateptb.minecraft.abilityslots.ability.enums.ActivationMethod;
import dev.ckateptb.minecraft.abilityslots.ability.sequence.annotation.Sequence;
import dev.ckateptb.minecraft.abilityslots.ability.sequence.service.AbilitySequenceService;
import dev.ckateptb.minecraft.abilityslots.addon.util.ClassPath;
import dev.ckateptb.minecraft.abilityslots.event.AbilitySlotsReloadEvent;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@CustomLog
@Component
@RequiredArgsConstructor
public class AddonService implements Listener {
    private final AbilitySlots plugin;
    private final AbilityCategoryService categoryService;
    private final AbilityDeclarationService abilityDeclarationService;
    private final CollisionDeclarationService collisionDeclarationService;
    private final AbilitySequenceService sequenceService;

    @SneakyThrows
    private void loadAddons() {
        File folder = Paths.get(this.plugin.getDataFolder().getPath(), "addons").toFile();
        FileUtils.forceMkdir(folder);
        Stream<Tuple2<JarFile, URL>> validJarFiles = this.getValidJarFiles(folder);
        Set<? extends Class<?>> classes = this.getClasses(validJarFiles);
        this.loadCategories(classes.stream());
        this.loadAbilities(classes.stream());
    }

    private Stream<Tuple2<JarFile, URL>> getValidJarFiles(File folder) {
        return FileUtils.listFiles(folder, new String[]{"jar"}, true)
                .stream()
                .filter(file -> { // Validate jar file
                    try {
                        new JarInputStream(new FileInputStream(file));
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .map(file -> {
                    try {
                        return Tuples.of(new JarFile(file), file.toURI().toURL());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private URLClassLoader createAddonClassLoader(Stream<URL> urls) {
        return URLClassLoader.newInstance(urls.toArray(URL[]::new), plugin.getClass().getClassLoader());
    }

    @SneakyThrows
    private Set<? extends Class<?>> getClasses(Stream<Tuple2<JarFile, URL>> jarFiles) {
        List<Tuple2<JarFile, URL>> urls = jarFiles.toList();
        URLClassLoader classLoader = this.createAddonClassLoader(urls.stream().map(Tuple2::getT2));
        return urls.stream().flatMap(objects -> ClassPath.from(classLoader, objects.getT1()).getAllClasses().stream()
                        .map(ClassPath.ClassInfo::load)
                        .filter(cl -> !cl.isInterface() && !Modifier.isAbstract(cl.getModifiers())))
                .collect(Collectors.toUnmodifiableSet());
    }

    private <U> Stream<? extends Class<? extends U>> getAssignableClasses(Stream<? extends Class<?>> classes, Class<U> castTo) {
        return classes
                .filter(castTo::isAssignableFrom)
                .map(cl -> {
                    final Class<? extends U> subclass;
                    subclass = cl.asSubclass(castTo);
                    return subclass;
                });
    }

    private void loadCategories(Stream<? extends Class<?>> classes) {
        this.getAssignableClasses(classes, AbilityCategory.class)
                .forEach(cl -> {
                    String className = cl.getName();
                    CategoryDeclaration annotation = cl.getAnnotation(CategoryDeclaration.class);
                    if (annotation != null) {
                        String categoryName = annotation.name();
                        try {
                            Constructor<? extends AbilityCategory> constructor = cl.getConstructor();
                            try {
                                this.categoryService.registerCategory(constructor.newInstance(), annotation);
                            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                                log.warn("Failed to instantiate category ({})", categoryName);
                                log.error("An error occurred while creating the category", e);
                            }
                        } catch (NoSuchMethodException e) {
                            log.warn("Found a new category for abilities ({}), " +
                                    "but the developer made a mistake and did not add a String constructor, " +
                                    "please pass this information to him", categoryName);
                            log.error("An error occurred while creating the category", e);
                        }
                    } else {
                        log.warn("Found a new category for abilities ({}), " +
                                "but the developer did not add the Category annotation " +
                                "please forward this information to him.", className);
                    }
                });
    }

    private void loadAbilities(Stream<? extends Class<?>> classes) {
        this.getAssignableClasses(classes, Ability.class)
                .forEach(cl -> {
                    String abilityName = cl.getName();
                    if (cl.isAnnotationPresent(AbilityDeclaration.class)) {
                        AbilityDeclaration declaration = cl.getAnnotation(AbilityDeclaration.class);
                        String declaredName = declaration.name();
                        if (!StringUtils.isBlank(declaredName)) {
                            abilityName = declaredName;
                            String author = declaration.author();
                            if (!StringUtils.isBlank(author)) {
                                try {
                                    cl.getConstructor();
                                    Class<? extends AbilityCategory> categoryClass = declaration.category();
                                    AbilityCategory category = this.categoryService.getCategories()
                                            .stream()
                                            .filter(categoryClass::isInstance).findFirst().orElse(null);
                                    if (category != null) {
                                        GeneratedAbilityDeclaration<? extends Ability> ability =
                                                new GeneratedAbilityDeclaration<>(declaration, category, cl);
                                        this.abilityDeclarationService.registerDeclaration(ability);
                                        if (CollidableAbility.class.isAssignableFrom(cl)) {
                                            Class<? extends CollidableAbility> collidableClass = (Class<? extends CollidableAbility>) cl;
                                            CollisionDeclaration collisionDeclaration = collidableClass.getAnnotation(CollisionDeclaration.class);
                                            if (collisionDeclaration != null) {
                                                GeneratedCollisionDeclaration collision = new GeneratedCollisionDeclaration(collisionDeclaration);
                                                this.collisionDeclarationService.registerDeclaration((IAbilityDeclaration<? extends CollidableAbility>) ability, collision);
                                            } else {
                                                log.warn("A new collidable ability was found ({}), " +
                                                        "but the developer did not add the CollisionDeclaration annotation " +
                                                        "to it. If you are in contact with the developer, " +
                                                        "please forward this information to him.", abilityName);
                                            }
                                        }
                                        if (ability.isActivatedBy(ActivationMethod.SEQUENCE)) {
                                            Sequence sequence = cl.getAnnotation(Sequence.class);
                                            if (sequence != null) {
                                                this.sequenceService.registerSequence(ability, sequence);
                                            } else {
                                                log.warn("A new sequence ability was found ({}), " +
                                                        "but the developer did not add the Sequence annotation " +
                                                        "to it. If you are in contact with the developer, " +
                                                        "please forward this information to him.", abilityName);
                                            }
                                        }
                                    } else {
                                        log.warn("A new ability was found ({}), " +
                                                "but the category ({}) specified by the developer does not exist. " +
                                                "If you are in contact with the developer, " +
                                                "please forward this information to him.", abilityName, categoryClass.getName());
                                    }
                                } catch (NoSuchMethodException e) {
                                    log.warn("A new ability was found ({}), " +
                                            "but the developer made a mistake and did not add an empty constructor. " +
                                            "If you are in contact with the developer, " +
                                            "please forward this information to him.", abilityName);
                                }
                            } else {
                                log.warn("A new ability was found ({}), " +
                                        "but the developer did not provide information about himself, " +
                                        "which is contrary to our copyright terms. " +
                                        "If you are in contact with the developer, " +
                                        "please forward this information to him.", abilityName);
                            }
                        } else {
                            log.warn("A new ability was found ({}), " +
                                    "but the developer did not provide information about ability name" +
                                    "which is contrary to our copyright terms. " +
                                    "If you are in contact with the developer, " +
                                    "please forward this information to him.", abilityName);
                        }
                    } else {
                        log.warn("A new ability was found ({}), " +
                                "but the developer did not add the AbilityDeclaration annotation to it. " +
                                "If you are in contact with the developer, " +
                                "please forward this information to him.", abilityName);
                    }
                });
    }

    /**
     * <p><b>НЕ ИСПОЛЬЗУЙТЕ ЭТОТ МЕТОД!</b></p>
     * Данный метод необходим, для перезагрузки дополнений и вызывается событиями Bukkit.
     *
     * @param event Событие перезагрузки плагина AbilitySlots.
     */
    @EventHandler(priority = EventPriority.NORMAL)
    private void on(AbilitySlotsReloadEvent event) {
        log.info("Loading addons...");
        this.loadAddons();
    }
}
