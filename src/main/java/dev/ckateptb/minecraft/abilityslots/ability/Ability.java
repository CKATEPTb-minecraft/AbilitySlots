package dev.ckateptb.minecraft.abilityslots.ability;

import dev.ckateptb.common.tableclothcontainer.IoC;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.enums.AbilityActivateStatus;
import dev.ckateptb.minecraft.abilityslots.ability.enums.AbilityTickStatus;
import dev.ckateptb.minecraft.abilityslots.ability.enums.ActivationMethod;
import dev.ckateptb.minecraft.abilityslots.ability.service.AbilityInstanceService;
import dev.ckateptb.minecraft.abilityslots.user.AbilityUser;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.World;

@Getter
@Setter
@NoArgsConstructor
public abstract class Ability {
    protected AbilityUser user;
    protected World world;
    protected ActivationMethod activationMethod;

    /**
     * Вызов данного метода управляется {@link dev.ckateptb.minecraft.abilityslots.ability.service.AbilityInstanceService}
     * Данный метод вызывается при соблюдении одного из описанных способов активации и управляет логикой самой активации.
     *
     * @param activationMethod указывает какой из описанных способов активации был соблюден.
     * @return стоит ли активировать способность для дальнейшей ее обработки в tick.
     */
    public abstract AbilityActivateStatus activate(ActivationMethod activationMethod);

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
    public abstract AbilityTickStatus tick();

    /**
     * Данный метод вызывается, когда способность была уничтожена
     * Вы можете использовать данный метод, для завершения логики вашей способности,
     * например {@link org.bukkit.event.Listener}. В прочем применять {@link org.bukkit.event.Listener} в
     * экземпляре способности крайне не рекомендуется, но не запрещено.
     */
    public abstract void destroy(Void unused);

    public final void destroy() {
        IoC.getBean(AbilityInstanceService.class).destroy(this);
    }

    @Getter
    @Setter(AccessLevel.PRIVATE)
    protected IAbilityDeclaration<? extends Ability> declaration = null;
}
