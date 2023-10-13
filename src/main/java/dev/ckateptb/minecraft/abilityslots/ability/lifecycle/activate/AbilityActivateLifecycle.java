package dev.ckateptb.minecraft.abilityslots.ability.lifecycle.activate;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.common.tableclothcontainer.annotation.PostConstruct;
import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.enums.ActivationMethod;
import dev.ckateptb.minecraft.abilityslots.ability.lifecycle.AbstractAbilityLifecycle;
import dev.ckateptb.minecraft.abilityslots.ability.sequence.annotation.AbilityAction;
import dev.ckateptb.minecraft.abilityslots.ability.sequence.service.AbilitySequenceService;
import dev.ckateptb.minecraft.abilityslots.user.AbilityUser;
import dev.ckateptb.minecraft.abilityslots.user.PlayerAbilityUser;
import lombok.CustomLog;
import lombok.RequiredArgsConstructor;
import org.slf4j.helpers.MessageFormatter;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.Optional;

@Component
@CustomLog
@RequiredArgsConstructor
public class AbilityActivateLifecycle extends AbstractAbilityLifecycle<Tuple3<PlayerAbilityUser, IAbilityDeclaration<?>, ActivationMethod>> {
    private final AbilitySequenceService sequenceService;

    @PostConstruct
    public void process() {
        this.flux
                .publishOn(Schedulers.boundedElastic())
                .filter(objects -> { // Может ли пользователь использовать данную способность?
                    IAbilityDeclaration<?> declaration = objects.getT2();
                    PlayerAbilityUser user = objects.getT1();
                    return user.canUse(declaration);
                })
                .map(objects -> { // Если пользователь выполнил условия для комбинации - активировать комбинацию
                    PlayerAbilityUser user = objects.getT1();
                    IAbilityDeclaration<?> declaration = objects.getT2();
                    ActivationMethod activationMethod = objects.getT3();
                    Class<? extends Ability> abilityClass = declaration.getAbilityClass();
                    AbilityAction action = this.sequenceService.createAction(abilityClass, activationMethod);
                    List<AbilityAction> actions = user.registerAction(action);
                    if (actions.size() > this.sequenceService.getMaxActionsSize()) actions.remove(0);
                    Optional<IAbilityDeclaration<? extends Ability>> sequence = this.sequenceService
                            .findSequence(actions).filter(user::canUse);
                    if (sequence.isEmpty()) return objects;
                    else return Tuples.of(user, sequence.get(), ActivationMethod.SEQUENCE);
                })
                .filter(objects -> { // Возможно ли активировать данную способность указанным методом активации?
                    IAbilityDeclaration<?> declaration = objects.getT2();
                    ActivationMethod activationMethod = objects.getT3();
                    return declaration.isActivatedBy(activationMethod);
                })
                .flatMap(objects -> { // Создать экземпляр способности
                    PlayerAbilityUser user = objects.getT1();
                    IAbilityDeclaration<?> declaration = objects.getT2();
                    ActivationMethod activationMethod = objects.getT3();
                    return declaration.createAbility(user, activationMethod);
                })
                .filter(ability -> {
                    try {
                        return ability.activate();
                    } catch (Throwable throwable) {
                        IAbilityDeclaration<? extends Ability> declaration = ability.getDeclaration();
                        log.error(MessageFormatter.arrayFormat(
                                "There was an error on ability {} was activated. Contact the author {}.",
                                new Object[]{declaration.getName(), declaration.getAuthor()}
                        ).getMessage(), throwable);
                        return false;
                    }
                })
                .subscribe(ability -> {
                    AbilityUser user = ability.getUser();
                    user.registerAbility(ability);
                });
    }
}
