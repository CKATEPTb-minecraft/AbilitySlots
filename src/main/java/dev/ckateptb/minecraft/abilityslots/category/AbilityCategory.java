package dev.ckateptb.minecraft.abilityslots.category;

import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.declaration.AbilityDeclaration;
import dev.ckateptb.minecraft.varflex.config.event.YamlConfigLoadEvent;
import dev.ckateptb.minecraft.varflex.config.event.YamlConfigSaveEvent;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Collection;

public interface AbilityCategory extends Listener {
    String getName();

    String getDisplayName();

    void setDisplayName(String displayName);

    String getPrefix();

    void setPrefix(String prefix);

    boolean isEnabled();

    void setEnabled(boolean enabled);

    Collection<AbilityDeclaration> getAbilities();

    AbilityDeclaration getAbility(String name);

    void registerAbility(AbilityDeclaration ability);

    @SneakyThrows
    @EventHandler
    default void on(YamlConfigLoadEvent event) {
        if (event.getYamlConfig() != AbilitySlots.config()) return;
        YamlConfiguration config = event.getBukkitConfig();
        setEnabled(config.getBoolean(getConfigPath("enabled"), isEnabled()));
        setDisplayName(config.getString(getConfigPath("name"), getDisplayName()));
        setPrefix(config.getString(getConfigPath("prefix"), getPrefix()));
        event.scan(getClass(), this, this::getConfigPath);
    }

    @SneakyThrows
    @EventHandler
    default void on(YamlConfigSaveEvent event) {
        if (event.getYamlConfig() != AbilitySlots.config()) return;
        event.set(getConfigPath("enabled"), isEnabled());
        event.set(getConfigPath("name"), getDisplayName());
        event.set(getConfigPath("prefix"), getPrefix());
        event.scan(getClass(), this, this::getConfigPath);
    }

    default String getConfigPath(String field) {
        return "categories." + getName() + "." + field;
    }

}
