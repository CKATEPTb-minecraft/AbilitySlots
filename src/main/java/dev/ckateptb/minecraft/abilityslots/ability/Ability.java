package dev.ckateptb.minecraft.abilityslots.ability;

import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.enums.AbilityTickStatus;
import dev.ckateptb.minecraft.abilityslots.ability.enums.ActivationMethod;
import dev.ckateptb.minecraft.abilityslots.ability.lifecycle.destroy.DestroyProcessLifecycle;
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
    private boolean locked;
    private DestroyProcessLifecycle destroyProcessLifecycle;
    @Setter(AccessLevel.NONE)
    private boolean destroyed;

    /**
     * Данный метод вызывается при соблюдении одного из описанных способов активации и управляет логикой самой активации.
     *
     * @return стоит ли активировать способность для дальнейшей ее обработки в tick.
     */
    public abstract boolean activate();

    /**
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
        if (this.destroyed) return;
        this.destroyed = true;
        destroyProcessLifecycle.emit(this);
    }

    public void setUser(AbilityUser user) {
        if (this.user != null && !this.destroyed) {
            this.user.removeAbility(this);
            user.registerAbility(this);
        }
        this.user = user;
    }

    @Getter
    @Setter(AccessLevel.PRIVATE)
    protected IAbilityDeclaration<? extends Ability> declaration = null;
}
