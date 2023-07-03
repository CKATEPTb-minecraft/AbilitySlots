package dev.ckateptb.minecraft.abilityslots.database.preset.repository;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.database.config.DatabaseConfig;
import dev.ckateptb.minecraft.abilityslots.database.config.mysql.MySQLConfig;
import dev.ckateptb.minecraft.abilityslots.database.preset.model.AbilityBoardPreset;
import dev.ckateptb.minecraft.chest.repository.Repository;
import dev.ckateptb.minecraft.chest.repository.configurable.ConfigurableRepository;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class AbilityBoardPresetRepository implements Repository<AbilityBoardPreset, Integer> {
    @Delegate
    private final ConfigurableRepository<AbilityBoardPreset, Integer> repository;

    public AbilityBoardPresetRepository(AbilitySlotsConfig config) {
        DatabaseConfig database = config.getGlobal().getDatabase();
        if (database.getType() == DatabaseConfig.DatabaseType.SQLITE) {
            Path path = AbilitySlots.getPlugin().getDataFolder().toPath().resolve("database.db");
            this.repository = new ConfigurableRepository<AbilityBoardPreset, Integer>(AbilityBoardPreset.class,
                    path) {
            };
        } else {
            MySQLConfig mysql = database.getMysql();
            this.repository = new ConfigurableRepository<AbilityBoardPreset, Integer>(AbilityBoardPreset.class,
                    mysql.getUsername(),
                    mysql.getPassword(),
                    mysql.getJdbcUrl()) {
            };
        }
    }

    @SneakyThrows
    public List<AbilityBoardPreset> getPresets(UUID player) {
        AbilityBoardPreset preset = new AbilityBoardPreset();
        preset.setPlayer(player);
        return this.queryForMatchingArgs(preset);
    }

    @SneakyThrows
    public Optional<AbilityBoardPreset> getPreset(Integer id) {
        return Optional.ofNullable(this.queryForId(id));
    }

    @SneakyThrows
    public Optional<AbilityBoardPreset> getPreset(UUID player, String name) {
        AbilityBoardPreset preset = new AbilityBoardPreset();
        preset.setPlayer(player);
        preset.setName(name);
        return Optional.ofNullable(this.queryForMatching(preset).get(0));
    }
}
