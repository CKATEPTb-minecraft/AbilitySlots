package dev.ckateptb.minecraft.abilityslots.ability.listener;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.service.AbilityDeclarationService;
import dev.ckateptb.minecraft.abilityslots.ability.enums.ActivationMethod;
import dev.ckateptb.minecraft.abilityslots.ability.lifecycle.activate.AbilityActivateLifecycle;
import dev.ckateptb.minecraft.abilityslots.ray.Ray;
import dev.ckateptb.minecraft.abilityslots.user.PlayerAbilityUser;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import dev.ckateptb.minecraft.nicotine.annotation.Schedule;
import io.papermc.paper.event.player.PlayerArmSwingEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AbilityActivationListener implements Listener {
    private final AbilityUserService userService;
    private final AbilityActivateLifecycle activateLifecycle;
    private final AbilityDeclarationService abilityDeclarationService;

    private void activate(PlayerAbilityUser user, ActivationMethod activation) {
        this.activate(user, activation, user.getSelectedAbility());
    }

    private void activate(PlayerAbilityUser user, ActivationMethod activation, IAbilityDeclaration<? extends Ability> declaration) {
        if (declaration == null) return;
        this.activateLifecycle.emit(Tuples.of(user, declaration, activation));
    }

    @Schedule(initialDelay = 20, fixedRate = 20, async = true)
    public void activatePassives() {
        Collection<IAbilityDeclaration<? extends Ability>> declarations = this.abilityDeclarationService.getDeclarations();
        Flux.fromStream(this.userService.getAbilityUsers())
                .filter(user -> user instanceof PlayerAbilityUser)
                .cast(PlayerAbilityUser.class)
                .join(Flux.fromIterable(declarations), f -> Flux.never(), f -> Flux.never(), Tuples::of)
                .subscribe(objects -> {
                    IAbilityDeclaration<? extends Ability> declaration = objects.getT2();
                    if (declaration.isActivatedBy(ActivationMethod.PASSIVE)) {
                        PlayerAbilityUser user = objects.getT1();
                        Optional<? extends Ability> optional = user.getAbilityInstances(declaration.getAbilityClass())
                                .filter(ability -> ability.getActivationMethod().equals(ActivationMethod.PASSIVE))
                                .findFirst();
                        boolean hasInstance = optional.isPresent();
                        boolean canUse = user.canUse(declaration);
                        if (canUse && !hasInstance) {
                            this.activate(user, ActivationMethod.PASSIVE, declaration);
                        }
                        if (!canUse && hasInstance) {
                            optional.get().destroy(); // destroy instance because user can't is it
                        }
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
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        PlayerAbilityUser user = userService.getAbilityUser(player);
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            user.setLastFallDistance(entity.getFallDistance());
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
