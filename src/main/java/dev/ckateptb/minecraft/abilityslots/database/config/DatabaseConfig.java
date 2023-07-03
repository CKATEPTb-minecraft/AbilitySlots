package dev.ckateptb.minecraft.abilityslots.database.config;

import dev.ckateptb.minecraft.abilityslots.database.config.mysql.MySQLConfig;
import lombok.Getter;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@Getter
public class DatabaseConfig {
    @Comment(value = "Select the database you want to use. Allowed: SQLITE, MYSQL", override = true)
    private String type = DatabaseType.SQLITE.name();
    private MySQLConfig mysql = new MySQLConfig();

    public DatabaseType getType() {
        return DatabaseType.valueOf(this.type);
    }

    public enum DatabaseType {
        MYSQL,
        SQLITE
    }
}
