package dev.ckateptb.minecraft.abilityslots.command.bind;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.ability.category.service.AbilityCategoryService;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.service.AbilityDeclarationService;
import dev.ckateptb.minecraft.abilityslots.command.AbilitySlotsSubCommand;
import dev.ckateptb.minecraft.abilityslots.command.bind.config.BindConfig;
import dev.ckateptb.minecraft.abilityslots.command.sender.AbilityCommandSender;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.user.PlayerAbilityUser;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Component
public class BindCommand extends AbilitySlotsSubCommand {

    public BindCommand(AbilitySlots plugin, AbilitySlotsConfig config, AbilityUserService userService, AbilityCategoryService categoryService, AbilityDeclarationService abilityService) {
        super(plugin, config, userService, categoryService, abilityService);
    }

    @Override
    public void process(CommandSender sender, Object... args) {
        BindConfig config = this.config.getLanguage().getCommand().getBind();
        IAbilityDeclaration<?> ability = (IAbilityDeclaration<?>) args[0];
        PlayerAbilityUser user = this.userService.getAbilityUser((Player) sender);
        AbilityCommandSender commandSender = AbilityCommandSender.of(sender);
        Integer slot = (Integer) args[1];
        if (slot == null) slot = user.getInventory().getHeldItemSlot() + 1;
        if (!user.canBind(ability)) commandSender.sendMessage(this.setPlaceholders(ability, slot, config.getFailed()));
        else {
            user.setAbility(slot, ability);
            commandSender.sendMessage(this.setPlaceholders(ability, slot, config.getReply()));
        }
    }

    private String setPlaceholders(IAbilityDeclaration<?> ability, Integer slot, String text) {
        return text
                .replaceAll("%ability_name%", ability.getName())
                .replaceAll("%ability_display_name%", ability.getDisplayName())
                .replaceAll("%category_ability_prefix%", ability.getCategory().getAbilityPrefix())
                .replaceAll("%slot%", String.valueOf(slot));
    }
}
