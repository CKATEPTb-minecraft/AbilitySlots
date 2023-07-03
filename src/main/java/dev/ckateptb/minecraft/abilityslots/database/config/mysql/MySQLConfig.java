package dev.ckateptb.minecraft.abilityslots.database.config.mysql;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MySQLConfig {
    private String jdbcUrl = "jdbc:mysql://localhost:3306/abilityslots?useUnicode=true&characterEncoding=UTF-8";
    private String username = "username";
    private String password = "password";
}
