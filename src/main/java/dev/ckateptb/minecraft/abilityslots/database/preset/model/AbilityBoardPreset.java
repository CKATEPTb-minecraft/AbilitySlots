package dev.ckateptb.minecraft.abilityslots.database.preset.model;

import dev.ckateptb.minecraft.chest.internal.ormlite.field.DatabaseField;
import dev.ckateptb.minecraft.chest.internal.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@DatabaseTable(tableName = "presets")
public class AbilityBoardPreset {
    @DatabaseField(unique = true, generatedId = true, canBeNull = false)
    private Integer id;
    @DatabaseField(canBeNull = false)
    private UUID player;
    @DatabaseField
    private String name;
    @DatabaseField
    private String slot_1;
    @DatabaseField
    private String slot_2;
    @DatabaseField
    private String slot_3;
    @DatabaseField
    private String slot_4;
    @DatabaseField
    private String slot_5;
    @DatabaseField
    private String slot_6;
    @DatabaseField
    private String slot_7;
    @DatabaseField
    private String slot_8;
    @DatabaseField
    private String slot_9;
}
