package dev.ckateptb.minecraft.abilityslots.database.config;

import org.spongepowered.configurate.objectmapping.meta.Comment;

public class DatabaseConfig {
    @Comment(value = "Select the database you want to use", override = true)
    private String database = DatabaseType.SQLITE.name();

    public enum DatabaseType {
        MYSQL,
        SQLITE
    }
}
