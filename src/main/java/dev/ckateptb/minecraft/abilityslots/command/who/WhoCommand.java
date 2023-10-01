package dev.ckateptb.minecraft.abilityslots.command.who;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.category.service.AbilityCategoryService;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.service.AbilityDeclarationService;
import dev.ckateptb.minecraft.abilityslots.command.AbilitySlotsSubCommand;
import dev.ckateptb.minecraft.abilityslots.command.sender.AbilityCommandSender;
import dev.ckateptb.minecraft.abilityslots.command.who.config.WhoConfig;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.user.PlayerAbilityUser;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Component
public class WhoCommand extends AbilitySlotsSubCommand {
    public WhoCommand(AbilitySlots plugin, AbilitySlotsConfig config, AbilityUserService userService, AbilityCategoryService categoryService, AbilityDeclarationService abilityService) {
        super(plugin, config, userService, categoryService, abilityService);
    }

    @Override
    public void process(CommandSender sender, Object... args) {
        Player target = (Player) args[0];
        PlayerAbilityUser user = this.userService.getAbilityUser(target);
        WhoConfig config = this.config.getLanguage().getCommand().getWho();
        StringBuilder builder = new StringBuilder(config.getReply()
                .replaceAll("%player_name%", target.getName()));
        String abilityFormat = config.getAbilityFormat();
        String empty = config.getEmpty();
        for (int i = 1; i <= 9; ++i) {
            IAbilityDeclaration<? extends Ability> ability = user.getAbility(i);
            builder.append("\n");
            if (ability == null) {
                builder.append(empty.replaceAll("%slot%", String.valueOf(i)));
            } else {
                builder.append(this.setPlaceholders(ability, i, abilityFormat));
            }
        }
        AbilityCommandSender.of(sender).sendMessage(builder.toString());
    }

    private String setPlaceholders(IAbilityDeclaration<?> declaration, Integer slot, String formatter) {
        return formatter
                .replaceAll("%category_ability_prefix%", declaration.getCategory().getAbilityPrefix())
                .replaceAll("%ability_display_name%", declaration.getDisplayName())
                .replaceAll("%ability_name%", declaration.getName())
                .replaceAll("%ability_author%", declaration.getAuthor())
                .replaceAll("%ability_description%", declaration.getDescription())
                .replaceAll("%ability_instruction%", declaration.getInstruction())
                .replaceAll("%slot%", String.valueOf(slot));
    }
}
