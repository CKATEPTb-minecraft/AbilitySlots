package dev.ckateptb.minecraft.abilityslots.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import dev.ckateptb.common.tableclothcontainer.IoC;
import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.AbilitySlots;
import dev.ckateptb.minecraft.abilityslots.entity.AbilityTarget;
import dev.ckateptb.minecraft.atom.chain.AtomChain;
import dev.ckateptb.minecraft.colliders.math.ImmutableVector;
import dev.ckateptb.minecraft.supervisor.Command;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import reactor.core.scheduler.Schedulers;

@Getter
@Component
public class DebugCommands implements Command<AbilitySlots> {
    private final AbilitySlots plugin;

    public DebugCommands() {
        this.plugin = IoC.getBean(AbilitySlots.class);
    }

    @CommandMethod("abilityslots debug distanceAboveGround")
    @CommandPermission("abilityslots.admin.debug")
    public void distanceAboveGround(Player player) {
        Schedulers.boundedElastic().schedule(() -> {
            player.sendMessage("Distance above ground: " + AbilityTarget.of(player).getDistanceAboveGround());
        });
    }

    @CommandMethod("abilityslots debug isOnGround")
    @CommandPermission("abilityslots.admin.debug")
    public void isOnGround(Player player) {
        Schedulers.boundedElastic().schedule(() -> {
            player.sendMessage("Is on ground: " + AbilityTarget.of(player).isOnGround());
        });
    }

    @CommandMethod("abilityslots debug isOnline")
    @CommandPermission("abilityslots.admin.debug")
    public void isOnline(Player player) {
        Schedulers.boundedElastic().schedule(() -> {
            player.sendMessage("Is online: " + AbilityTarget.of(player).isOnline());
        });
    }

    @CommandMethod("abilityslots debug findPosition <range>")
    @CommandPermission("abilityslots.admin.debug")
    public void findPosition(Player player, @Argument("range") Double range) {
        Schedulers.boundedElastic().schedule(() -> {
            AtomChain.sync(AbilityTarget.of(player).findPosition(range).toLocation(player.getWorld()).getBlock())
                    .promise(block -> block.setType(Material.DIAMOND_BLOCK));
        });
    }

    @CommandMethod("abilityslots debug findBlock <range>")
    @CommandPermission("abilityslots.admin.debug")
    public void findBlock(Player player, @Argument("range") Double range) {
        Schedulers.boundedElastic().schedule(() -> {
            AbilityTarget.of(player).findBlock(range).map(AtomChain::sync).ifPresent(chain ->
                    chain.promise(block -> block.setType(Material.DIAMOND_BLOCK)));
        });
    }

    @CommandMethod("abilityslots debug findEntity <range>")
    @CommandPermission("abilityslots.admin.debug")
    public void findEntity(Player player, @Argument("range") Double range) {
        Schedulers.boundedElastic().schedule(() -> {
            AbilityTarget.of(player).findLivingEntity(range).ifPresent(entity ->
                    AtomChain.sync(entity).promise(livingEntity -> livingEntity.damage(5, player)));
        });
    }

    @CommandMethod("abilityslots debug targetEntity <range>")
    @CommandPermission("abilityslots.admin.debug")
    public void targetEntity(Player player, @Argument("range") Integer range) {
        Schedulers.boundedElastic().schedule(() -> {
            AbilityTarget.of(player).getTargetEntity(range, false).ifPresent(entity ->
                    entity.setVelocity(ImmutableVector.PLUS_J.multiply(5)));
        });
    }
}
