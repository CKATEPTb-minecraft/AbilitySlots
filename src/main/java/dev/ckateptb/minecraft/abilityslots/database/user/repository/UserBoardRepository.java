package dev.ckateptb.minecraft.abilityslots.database.user.repository;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.database.config.DatabaseConfig;
import dev.ckateptb.minecraft.abilityslots.database.config.mysql.MySQLConfig;
import dev.ckateptb.minecraft.abilityslots.database.user.model.UserBoard;
import dev.ckateptb.minecraft.chest.repository.Repository;
import dev.ckateptb.minecraft.chest.repository.configurable.ConfigurableRepository;
import lombok.experimental.Delegate;

import java.nio.file.Path;
import java.util.UUID;

@Component
public class UserBoardRepository implements Repository<UserBoard, UUID> {
    @Delegate
    private final ConfigurableRepository<UserBoard, UUID> repository;

    public UserBoardRepository(AbilitySlotsConfig config) {
        DatabaseConfig database = config.getGlobal().getDatabase();
        if (database.getType() == DatabaseConfig.DatabaseType.SQLITE) {
            Path path = AbilitySlots.getPlugin().getDataFolder().toPath().resolve("database.db");
            this.repository = new ConfigurableRepository<UserBoard, UUID>(UserBoard.class,
                    path) {
            };
        } else {
            MySQLConfig mysql = database.getMysql();
            this.repository = new ConfigurableRepository<UserBoard, UUID>(UserBoard.class,
                    mysql.getUsername(),
                    mysql.getPassword(),
                    mysql.getJdbcUrl()) {
            };
        }
    }
}
