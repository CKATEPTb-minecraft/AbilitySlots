package dev.ckateptb.minecraft.abilityslots.config;

import com.google.common.primitives.Primitives;
import dev.ckateptb.common.tableclothconfig.hocon.HoconConfig;
import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.common.tableclothcontainer.annotation.PostConstruct;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.category.AbilityCategory;
import dev.ckateptb.minecraft.abilityslots.ability.category.annotation.CategoryDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.collision.CollidableAbility;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.config.annotation.Configurable;
import dev.ckateptb.minecraft.abilityslots.config.global.GlobalConfig;
import dev.ckateptb.minecraft.abilityslots.config.language.LanguageConfig;
import dev.ckateptb.minecraft.abilityslots.event.AbilitySlotsReloadEvent;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

@Getter
@Component
public class AbilitySlotsConfig extends HoconConfig implements Listener {
    @Getter
    private static AbilitySlotsConfig instance;
    private GlobalConfig global = new GlobalConfig();
    private CommentedConfigurationNode categories = CommentedConfigurationNode.factory().createNode();
    private CommentedConfigurationNode abilities = CommentedConfigurationNode.factory().createNode();
    private CommentedConfigurationNode collisions = CommentedConfigurationNode.factory().createNode();
    private LanguageConfig language = new LanguageConfig();

    public AbilitySlotsConfig() {
        AbilitySlotsConfig.instance = this;
    }

    @PostConstruct
    @SneakyThrows
    public void init() {
        this.load();
        this.save();
    }

    @SneakyThrows
    public void loadCategory(AbilityCategory category, CategoryDeclaration declaration) {
        String name = declaration.name();
        CommentedConfigurationNode root = this.categories.node(name);
        Method setName = AbilityCategory.class.getDeclaredMethod("setName", String.class);
        setName.setAccessible(true);
        setName.invoke(category, name);
        category.setEnabled(root.node("enabled").get(Boolean.class, true));
        category.setDisplayName(root.node("displayName").get(String.class, declaration.displayName()));
        category.setDescription(root.node("description").get(String.class, declaration.description()));
        category.setAbilityPrefix(root.node("abilityPrefix").get(String.class, declaration.abilityPrefix()));
        this.loadCustom(category, category.getClass(), root);
    }

    @SneakyThrows
    public <T extends Ability> void loadAbility(IAbilityDeclaration<T> declaration) {
        CommentedConfigurationNode root = this.abilities.node(declaration.getCategory().getName(), declaration.getName());
        CommentedConfigurationNode about = root.node("about");
        declaration.setDisplayName(about.node("displayName").get(String.class, declaration.getDisplayName()));
        declaration.setDescription(about.node("description").get(String.class, declaration.getDescription()));
        declaration.setInstruction(about.node("instruction").get(String.class, declaration.getInstruction()));
        declaration.setEnabled(about.node("enabled").get(Boolean.class, declaration.isEnabled()));
        Class<? extends Ability> abilityClass = declaration.getAbilityClass();
        Constructor<? extends Ability> constructor = abilityClass.getConstructor();
        Ability ability = constructor.newInstance();
        this.loadCustom(ability, abilityClass, root, declaration);
    }

    @SneakyThrows
    public <T extends CollidableAbility> List<String> loadCollision(IAbilityDeclaration<T> declaration, List<String> collision) {
        CommentedConfigurationNode root = this.collisions.node(declaration.getCategory().getName(), declaration.getName());
        List<String> list = root.getList(String.class, collision);
        this.save();
        return list;
    }

    public <T> void loadCustom(T instance, Class<? extends T> clazz, CommentedConfigurationNode root) {
        this.loadCustom(instance, clazz, root, null);
    }

    @SneakyThrows
    @SuppressWarnings("all")
    public <T> void loadCustom(T instance, Class<? extends T> clazz, CommentedConfigurationNode root, IAbilityDeclaration<?> declaration) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Configurable annotation = field.getAnnotation(Configurable.class);
            if (annotation == null) continue;
            if (instance == null && !Modifier.isStatic(field.getModifiers())) continue;
            String annotationName = annotation.name();
            String keyName = StringUtils.isBlank(annotationName) ? field.getName() : annotationName;
            Object defaultValue = field.get(instance);
            CommentedConfigurationNode node = root;
            for (String recursive : keyName.split("\\.")) {
                node = node.node(recursive);
            }
            String comment = annotation.comment();
            if (!StringUtils.isBlank(comment)) node = node.comment(comment);
            Object value = node.get(Primitives.wrap(field.getType()), defaultValue);
            if (instance instanceof Ability ability && declaration != null) {
                declaration.setFieldValue(field, value);
            } else {
                field.set(instance, value);
            }
        }
        this.save();
    }

    @Override
    public File getFile() {
        return AbilitySlots.getPlugin().getDataFolder().toPath().resolve("config.json").toFile();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(AbilitySlotsReloadEvent event) {
        this.load();
    }
}
