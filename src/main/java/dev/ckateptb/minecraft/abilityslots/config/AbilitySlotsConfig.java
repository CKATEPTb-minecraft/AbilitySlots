package dev.ckateptb.minecraft.abilityslots.config;

import com.google.common.primitives.Primitives;
import dev.ckateptb.common.tableclothconfig.hocon.HoconConfig;
import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.common.tableclothcontainer.annotation.PostConstruct;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.category.AbilityCategory;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.config.global.GlobalConfig;
import dev.ckateptb.minecraft.varflex.config.annotation.ConfigField;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.slf4j.helpers.MessageFormatter;
import org.spongepowered.configurate.CommentedConfigurationNode;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@Getter
@Component
public class AbilitySlotsConfig extends HoconConfig {
    @Getter
    private static AbilitySlotsConfig instance;
    private GlobalConfig global = new GlobalConfig();
    private CommentedConfigurationNode categories = CommentedConfigurationNode.factory().createNode();
    private CommentedConfigurationNode abilities = CommentedConfigurationNode.factory().createNode();

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
    public void loadCategory(AbilityCategory category) {
        this.loadCustom(category, category.getClass(), category.getName(), this.categories);
    }

    @SneakyThrows
    public <T extends Ability> void loadAbility(IAbilityDeclaration<T> declaration) {
        this.loadCustom(null, declaration.getAbilityClass(), declaration.getName(), this.categories
                .node(declaration.getCategory().getName(), "abilities"));
    }

    @SneakyThrows
    @SuppressWarnings("all")
    public <T> void loadCustom(T instance, Class<? extends T> clazz, String nodeKey, CommentedConfigurationNode root) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            ConfigField annotation = field.getAnnotation(ConfigField.class);
            if (annotation == null) continue;
            if(instance == null && !Modifier.isStatic(field.getModifiers())) continue;
            String annotationName = annotation.name();
            String keyName = StringUtils.isBlank(annotationName) ? field.getName() : annotationName;
            Object defaultValue = field.get(instance);
            String key = MessageFormatter.format("{}.{}", nodeKey, keyName).getMessage();
            CommentedConfigurationNode node = root.node(key.split("\\."));
            String comment = annotation.comment();
            if (!StringUtils.isBlank(comment)) node = node.comment(comment);
            field.set(instance, node.get(Primitives.wrap(field.getType()), defaultValue));
        }
        this.save();
    }

    @Override
    public File getFile() {
        return AbilitySlots.getPlugin().getDataFolder().toPath().resolve("config.conf").toFile();
    }
}
