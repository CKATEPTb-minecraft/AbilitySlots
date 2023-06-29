package dev.ckateptb.minecraft.abilityslots.ability.board.service;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.ability.board.AbilityBoardHolder;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import dev.ckateptb.minecraft.nicotine.annotation.Schedule;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AbilityBoardService {
    private final AbilityUserService userService;

    @Schedule(initialDelay = 20, fixedRate = 1, async = true)
    public void update() {
        this.userService.getAbilityUsers()
                .filter(user -> user instanceof AbilityBoardHolder)
                .forEach(user -> ((AbilityBoardHolder) user).updateAbilityBoard());
    }
}
