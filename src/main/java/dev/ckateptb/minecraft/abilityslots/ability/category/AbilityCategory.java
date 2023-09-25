package dev.ckateptb.minecraft.abilityslots.ability.category;

import dev.ckateptb.minecraft.abilityslots.interfaces.DisplayNameHolder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbilityCategory implements DisplayNameHolder {

    /**
     * Системное название категории, которое используется при реализации в качестве ключа,
     * в том числе и для файлов конфигурации. Название категории всегда должно быть уникальным, не зависимо от автора.
     */
    @Setter(AccessLevel.PRIVATE)
    protected String name;

    /**
     * Отображаемое название категории, которое может отличаться от системного и может быть абсолютно любым.
     * Конфигурируется в файле конфигурации, что позволяет администраторам серверов локализировать
     * или изменить название на свой вкус и лад.
     */
    protected String displayName;

    /**
     * Категория должна быть визуально отличимой.
     * Комбинируя отображаемое имя категории и префикс для способности можно достичь желаемого результата.
     */
    protected String abilityPrefix;

    /**
     * Полное описание способности.
     */
    protected String description;

    /**
     * Этот параметр управляется файлом конфигурации,
     * чтобы администраторы серверов могли отключать некоторые категории, по своему желанию.
     */
    protected boolean enabled = true;
}
