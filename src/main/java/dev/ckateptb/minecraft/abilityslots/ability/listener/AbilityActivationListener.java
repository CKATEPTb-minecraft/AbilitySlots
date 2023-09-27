package dev.ckateptb.minecraft.abilityslots.ability.listener;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.enums.AbilityActivateStatus;
import dev.ckateptb.minecraft.abilityslots.ability.enums.ActivationMethod;
import dev.ckateptb.minecraft.abilityslots.ability.sequence.annotation.AbilityAction;
import dev.ckateptb.minecraft.abilityslots.ability.sequence.service.AbilitySequenceService;
import dev.ckateptb.minecraft.abilityslots.ability.service.AbilityInstanceService;
import dev.ckateptb.minecraft.abilityslots.ray.Ray;
import dev.ckateptb.minecraft.abilityslots.user.AbilityUser;
import dev.ckateptb.minecraft.abilityslots.user.PlayerAbilityUser;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import io.papermc.paper.event.player.PlayerArmSwingEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Component
@RequiredArgsConstructor
public class AbilityActivationListener implements Listener {
    private final AbilityUserService userService;
    private final AbilitySequenceService sequenceService;
    private final AbilityInstanceService instanceService;

    private void activate(PlayerAbilityUser user, ActivationMethod activation) {
        IAbilityDeclaration<? extends Ability> declaration = user.getSelectedAbility();
        if (declaration == null || !user.canUse(declaration)) return;
        AbilityAction action = this.sequenceService.createAction(declaration.getAbilityClass(), activation);
        List<AbilityAction> actions = user.registerAction(action);
        if (actions.size() > sequenceService.getMaxActionsSize()) actions.remove(0);
        AtomicReference<ActivationMethod> atomicActivation = new AtomicReference<>(ActivationMethod.SEQUENCE);
        this.sequenceService.findSequence(actions) // Выполнил ли пользователь условия для какого-то Sequence
                .filter(user::canUse) // Может ли пользователь использовать Sequence
                .or(() -> { // Если Sequence не найден, или у пользователя нет прав, переключаемся на выбранную способности
                    atomicActivation.set(activation);
                    return Optional.of(declaration);
                })
                .filter(ability -> ability.isActivatedBy(atomicActivation.get()))
                .map(IAbilityDeclaration::createAbility)
                .ifPresent(ability -> {
                    ability.setUser(user);
                    ability.setWorld(user.getWorld());
                    if (ability.activate(atomicActivation.get()) == AbilityActivateStatus.ACTIVATE) {
                        instanceService.register(ability);
                    }
                });
    }

    @EventHandler
    public void on(PlayerToggleSneakEvent event) {
        PlayerAbilityUser user = this.userService.getAbilityUser(event.getPlayer());
        boolean sneaking = event.isSneaking();
        ActivationMethod activation = sneaking ? ActivationMethod.SNEAK : ActivationMethod.SNEAK_RELEASE;
        this.activate(user, activation);
    }

    @EventHandler
    public void on(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        PlayerAbilityUser user = userService.getAbilityUser(player);
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            this.activate(user, ActivationMethod.FALL);
        }
    }

    @EventHandler
    public void on(PlayerArmSwingEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.ADVENTURE) return;
        PlayerAbilityUser user = userService.getAbilityUser(event.getPlayer());
        Ray ray = user.ray(5, 0.1);
        if (ray.entity()
                .ignoreBlocks(false)
                .ignoreLiquids(true)
                .ignorePassable(true)
                .livingOnly(false)
                .filter(entity -> !user.equals(entity))
                .find().isPresent())
            this.on(new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR, null, null, BlockFace.UP));
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        PlayerAbilityUser user = userService.getAbilityUser(event.getPlayer());
        Ray ray = user.ray(5, 0.1);
        boolean isBlock = ray.block()
                .ignoreLiquids(true)
                .ignorePassable(true)
                .find().isPresent();
        boolean isEntity = ray.entity()
                .ignoreBlocks(false)
                .ignoreLiquids(true)
                .ignorePassable(true)
                .filter(entity -> !user.equals(entity))
                .find().isPresent();
        Action action = event.getAction();
        ActivationMethod activationMethod = null;
        if (action.isLeftClick()) {
            activationMethod = isEntity ? ActivationMethod.LEFT_CLICK_ENTITY : isBlock ? ActivationMethod.LEFT_CLICK_BLOCK : ActivationMethod.LEFT_CLICK;
        } else if (action.isRightClick()) {
            activationMethod = isEntity ? ActivationMethod.RIGHT_CLICK_ENTITY : isBlock ? ActivationMethod.RIGHT_CLICK_BLOCK : ActivationMethod.RIGHT_CLICK;
        }
        if (activationMethod != null) this.activate(user, activationMethod);
    }

    @EventHandler
    public void on(PlayerSwapHandItemsEvent event) {
        PlayerAbilityUser user = userService.getAbilityUser(event.getPlayer());
        this.activate(user, ActivationMethod.HAND_SWAP);
    }
}
