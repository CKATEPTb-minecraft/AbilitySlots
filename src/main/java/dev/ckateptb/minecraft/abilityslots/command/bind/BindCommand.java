package dev.ckateptb.minecraft.abilityslots.command.bind;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.ability.category.service.AbilityCategoryService;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.service.AbilityDeclarationService;
import dev.ckateptb.minecraft.abilityslots.command.AbilitySlotsSubCommand;
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
        IAbilityDeclaration<?> ability = (IAbilityDeclaration<?>) args[0];
        Integer slot = (Integer) args[1];
        Player player = (Player) sender;
        PlayerAbilityUser user = this.userService.getAbilityUser(player);
        if (slot == null) slot = user.getInventory().getHeldItemSlot() + 1;
        user.setAbility(slot, ability);
        AbilityCommandSender.of(sender).sendMessage(this.config.getLanguage().getCommand().getSuccessfulBind()
                .replaceAll("%ability%", ability.getFormattedName())
                .replaceAll("%slot%", String.valueOf(slot)));
    }
}
