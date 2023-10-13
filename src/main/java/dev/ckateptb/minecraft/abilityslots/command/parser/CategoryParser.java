package dev.ckateptb.minecraft.abilityslots.command.parser;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import dev.ckateptb.minecraft.abilityslots.ability.category.AbilityCategory;
import dev.ckateptb.minecraft.abilityslots.ability.category.service.AbilityCategoryService;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Queue;

@RequiredArgsConstructor
public class CategoryParser implements ArgumentParser<CommandSender, AbilityCategory> {
    private final AbilityCategoryService categoryService;
    private final AbilityUserService userService;

    @Override
    @SuppressWarnings("all")
    public ArgumentParseResult<AbilityCategory> parse(CommandContext<CommandSender> context, Queue<String> inputQueue) {
        String input = inputQueue.peek();
        NoInputProvidedException exception = new NoInputProvidedException(
                CategoryParser.class,
                context
        );
        if (input == null) return ArgumentParseResult.failure(exception);
        ArgumentParseResult<AbilityCategory> result = (ArgumentParseResult<AbilityCategory>) this.categoryService.findCategory(input).filter(category -> {
            if (context.getSender() instanceof Player player) {
                return this.userService.getAbilityUser(player).canUse(category);
            }
            return true;
        }).map(ArgumentParseResult::success).orElse(ArgumentParseResult.failure(exception));
        inputQueue.remove();
        return result;
    }

    @Override
    public @NotNull List<String> suggestions(@NotNull CommandContext<CommandSender> context, @NotNull String input) {
        return this.categoryService.getCategories().stream()
                .filter(category -> category.getName().toLowerCase().startsWith(input.toLowerCase()))
                .filter(category -> {
                    if (context.getSender() instanceof Player player) {
                        return this.userService.getAbilityUser(player).canUse(category);
                    }
                    return true;
                })
                .map(AbilityCategory::getName)
                .toList();
    }
}