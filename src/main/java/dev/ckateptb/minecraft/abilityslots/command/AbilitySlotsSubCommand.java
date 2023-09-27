package dev.ckateptb.minecraft.abilityslots.command;

import cloud.commandframework.context.CommandContext;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.ability.category.service.AbilityCategoryService;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.service.AbilityDeclarationService;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public abstract class AbilitySlotsSubCommand {
    protected final AbilitySlots plugin;
    protected final AbilitySlotsConfig config;
    protected final AbilityUserService userService;
    protected final AbilityCategoryService categoryService;
    protected final AbilityDeclarationService abilityService;

    public List<String> suggestion(CommandContext<CommandSender> sender, String input) {
        return Collections.emptyList();
    }

    public abstract void process(CommandSender sender, Object... args);
}
