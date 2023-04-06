package dev.ckateptb.minecraft.abilityslots.ability;

import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.enums.AbilityActivateStatus;
import dev.ckateptb.minecraft.abilityslots.ability.enums.AbilityTickStatus;
import dev.ckateptb.minecraft.abilityslots.ability.enums.ActivationMethod;
import dev.ckateptb.minecraft.abilityslots.user.IAbilityUser;
import org.bukkit.World;

public interface IAbility {

    /**
     * Вызов данного метода управляется {@link dev.ckateptb.minecraft.abilityslots.ability.service.AbilityInstanceService}
     * Данный метод вызывается при соблюдении одного из описанных способов активации и управляет логикой самой активации.
     *
     * @param activationMethod указывает какой из описанных способов активации был соблюден.
     * @return стоит ли активировать способность для дальнейшей ее обработки в tick.
     */
    AbilityActivateStatus activate(ActivationMethod activationMethod);

    /**
     * Вызов этого метода управляется {@link dev.ckateptb.minecraft.abilityslots.ability.service.AbilityInstanceService}
     * Данный метод вызывается в асинхронном потоке, вызов которого блокируется,
     * если предыдущий вызов не успел обработаться прежде чем основной поток см. {@link io.papermc.paper.util.TickThread}
     * начнет новый тик. Таким образом можно гарантировать, что основной поток не будет перегружен.
     * В качестве возвращаемого значения принимается {@link AbilityTickStatus}, который ссылается на то,
     * должна ли способность обрабатываться на следующий тик, или ее необходимо уничтожить.
     *
     * @return стоит ли продолжать обрабатывать способность на следующий тик.
     */
    AbilityTickStatus tick();

    /**
     * Данный метод вызывается, когда способность была уничтожена
     * Вы можете использовать данный метод, для завершения логики вашей способности,
     * например {@link org.bukkit.event.Listener}. В прочем применять {@link org.bukkit.event.Listener} в
     * экземпляре способности крайне не рекомендуется, но не запрещено.
     */
    void destroy();

    /**
     * @return пользователя, который использует текущую способность.
     */
    IAbilityUser getUser();

    /**
     * Назначает нового пользователя, который управляет текущей способностью.
     * @param user пользователь, которому необходимо передать управление над текущей способностью.
     */
    void setUser(IAbilityUser user);

    /**
     * ВАЖНО! У нас нет своей реализации или обертки для {@link World},
     * и на данный момент создание одного из перечисленного не планируется.
     * Многие моменты в BukkitAPI не потоко-безопасны, а это значит, что у вас могут возникнуть проблемы
     * при использовании методов внутри {@link World} и т.д. по иерархии, ведь метод tick, в котором описана основная
     * логика способности обрабатывается в отдельном потоке.
     * @return мир, к которому способность относится в данный момент.
     */
    World getWorld();

    /**
     * Назначает для способности указанный мир.
     * @param world мир, в котором необходимо обрабатывать способность.
     */
    void setWorld(World world);

    /**
     * @return возвращает экземпляр декларации для текущей способности.
     */
    IAbilityDeclaration<IAbility> getAbilityDeclaration();
}
