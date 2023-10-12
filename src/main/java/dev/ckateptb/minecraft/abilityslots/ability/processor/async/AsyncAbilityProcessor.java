package dev.ckateptb.minecraft.abilityslots.ability.processor.async;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.collision.service.AbilityCollisionService;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.enums.AbilityTickStatus;
import dev.ckateptb.minecraft.abilityslots.ability.processor.AbstractAbilityProcessor;
import dev.ckateptb.minecraft.abilityslots.ability.processor.sync.SyncAbilityProcessor;
import lombok.CustomLog;
import org.slf4j.helpers.MessageFormatter;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.ArrayList;

@CustomLog
public class AsyncAbilityProcessor extends SyncAbilityProcessor {

    public AsyncAbilityProcessor(AbilityCollisionService collisionService) {
        super(collisionService);
    }

//    @Override
//    public void tick() {
//        for (Ability destroy : new ArrayList<>(this.abilities)
//                .stream()
//                .filter(ability -> !ability.isLocked())
//                .peek(ability -> ability.setLocked(true))
//                .map(ability -> {
//                    AbilityTickStatus status = AbilityTickStatus.DESTROY;
//                    try {
//                        status = ability.tick();
//                    } catch (Throwable throwable) {
//                        IAbilityDeclaration<? extends Ability> declaration = ability.getDeclaration();
//                        log.error(MessageFormatter.arrayFormat(
//                                "There was an error processing ability {} and has" +
//                                        " been called back. Contact the author {}.",
//                                new Object[]{declaration.getName(), declaration.getAuthor()}
//                        ).getMessage(), throwable);
//                    }
//                    return Tuples.of(ability, status);
//                })
//                .peek(objects -> objects.getT1().setLocked(false))
//                .filter(objects -> objects.getT2() == AbilityTickStatus.DESTROY)
//                .map(Tuple2::getT1)
//                .toList()) {
//            this.destroy(destroy);
//        }
//    }

    @Override
    public boolean isAsync() {
        return true;
    }
}
