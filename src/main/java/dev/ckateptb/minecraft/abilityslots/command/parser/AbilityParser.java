package dev.ckateptb.minecraft.abilityslots.command.parser;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.service.AbilityDeclarationService;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Queue;

@RequiredArgsConstructor
public class AbilityParser implements ArgumentParser<CommandSender, IAbilityDeclaration<?>> {
    private final AbilityDeclarationService abilityService;
    private final AbilityUserService userService;

    @Override
    @SuppressWarnings("all")
    public ArgumentParseResult<IAbilityDeclaration<?>> parse(CommandContext<CommandSender> context, Queue<String> inputQueue) {
        String input = inputQueue.peek();
        NoInputProvidedException exception = new NoInputProvidedException(
                AbilityParser.class,
                context
        );
        if (input == null) return ArgumentParseResult.failure(exception);
        ArgumentParseResult<?> result = this.abilityService.findDeclaration(input).filter(declaration -> {
            if (context.getSender() instanceof Player player) {
                return this.userService.getAbilityUser(player).canBind(declaration);
            }
            return true;
        }).map(ArgumentParseResult::success).orElse(ArgumentParseResult.failure(exception));
        inputQueue.remove();
        return (ArgumentParseResult<IAbilityDeclaration<?>>) result;
    }

    @Override
    public @NotNull List<String> suggestions(@NotNull CommandContext<CommandSender> context, @NotNull String input) {
        return this.abilityService.getDeclarations().stream()
                .filter(declaration -> declaration.getName().toLowerCase().startsWith(input.toLowerCase()))
                .filter(category -> {
                    if (context.getSender() instanceof Player player) {
                        return this.userService.getAbilityUser(player).canUse(category);
                    }
                    return true;
                })
                .map(IAbilityDeclaration::getName)
                .toList();
    }
}