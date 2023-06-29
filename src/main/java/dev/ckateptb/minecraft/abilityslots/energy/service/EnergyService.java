package dev.ckateptb.minecraft.abilityslots.energy.service;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.energy.board.EnergyBoardHolder;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import dev.ckateptb.minecraft.nicotine.annotation.Schedule;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EnergyService {
    private final AbilityUserService userService;
    private final AbilitySlotsConfig config;

    @Schedule(initialDelay = 20, fixedRate = 20, async = true)
    public void regenerate() {
        double regen = this.config.getGlobal().getEnergy().getRegen();
        this.userService.getAbilityUsers()
                .filter(user -> user instanceof EnergyBoardHolder)
                .forEach(user -> user.addEnergy(regen));
    }
}
