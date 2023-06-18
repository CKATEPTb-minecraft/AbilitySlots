package dev.ckateptb.minecraft.abilityslots.ability.declaration;

import dev.ckateptb.minecraft.abilityslots.ability.IAbility;
import dev.ckateptb.minecraft.abilityslots.ability.category.IAbilityCategory;
import dev.ckateptb.minecraft.abilityslots.ability.enums.ActivationMethod;

public interface IAbilityDeclaration <A extends IAbility> {
    /**
     * Системное название способности, которое используется при реализации в качестве ключа,
     * в том числе и для файлов конфигурации. Название способности всегда должно быть уникальным, не зависимо от автора.
     *
     * @return Уникальное название способности.
     */
    String getName();

    /**
     * Каждая способность имеет свою категорию, для того, чтобы администраторам серверов было
     * проще управлять сразу несколькими способностями.
     *
     * @return Категорию к которой относится способность.
     */
    IAbilityCategory getCategory();

    /**
     * @return Никнейм автора способности.
     */
    String getAuthor();

    /**
     * Этот параметр управляется файлом конфигурации,
     * чтобы администраторы серверов могли отключать некоторые способности, по своему желанию.
     *
     * @return включена ли способность.
     */
    boolean isEnabled();

    /**
     * Отображаемое название способности, которое может отличаться от системного и может быть абсолютно любым.
     * Конфигурируется в файле конфигурации, что позволяет администраторам серверов локализировать
     * или изменить название на свой вкус и лад.
     *
     * @return Отображаемое название способности.
     */
    String getDisplayName();

    /**
     * Детальное описание способности.
     * Конфигурируется в файле конфигурации, что позволяет администраторам серверов локализировать
     * или изменить описание на свой вкус и лад.
     *
     * @return Описание способности.
     */
    String getDescription();

    /**
     * Полная инструкция для всех возможных способов применения способности.
     * Конфигурируется в файле конфигурации, что позволяет администраторам серверов локализировать
     * или изменить инструкцию на свой вкус и лад.
     *
     * @return Инструкцию по применению способности.
     */
    String getInstruction();

    /**
     * У каждой способности должен быть как минимум один способ активации,
     * которым конечные пользователи смогут использовать их.
     *
     * @return Массив способов активации способности.
     */
    ActivationMethod[] getActivationMethods();

    /**
     * Данный метод существует для удобства. Довольно часто нам необходимо узнать,
     * возможно ли активировать способность указанным способом.
     * @return Возможно ли активировать способность указанным методом активации
     */
    default boolean isActivatedBy(ActivationMethod method) {
        for (ActivationMethod activationMethod : this.getActivationMethods()) {
            if (activationMethod == method) {
                return true;
            }
        }
        return false;
    }

    /**
     * В идеале, каждую способность, которую можно активировать используя любой способ активации,
     * кроме {@link ActivationMethod#SEQUENCE} и {@link ActivationMethod#PASSIVE}, должно быть разрешено
     * привязывать к слоту. Но не стоит забывать, что на написании таких способностей можно строить целый бизнес,
     * по этому принятие данного решения я оставляю на автора способности и его здравый разум.
     *
     * @return Возможно ли привязать способность к слоту хот-бара.
     */
    boolean isBindable();

    /**
     * Кодом предусмотрено, что декларация способности описывает и логику создания её экземпляра.
     * Это поможет авторам расширить возможности данного API.
     *
     * @return Экземпляр способности, которую описывает данная декларация.
     */
    A createAbility();

    /**
     * @return Клас способности, которую описывает данная декларация.
     */
    Class<? extends IAbility> getAbilityClass();
}