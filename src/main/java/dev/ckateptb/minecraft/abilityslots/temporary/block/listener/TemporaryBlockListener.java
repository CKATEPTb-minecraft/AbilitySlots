package dev.ckateptb.minecraft.abilityslots.temporary.block.listener;

import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.minecraft.abilityslots.temporary.block.TemporaryBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

@Component
public class TemporaryBlockListener implements Listener {
    @EventHandler
    public void on(BlockBreakEvent event) {
        if (TemporaryBlock.of(event.getBlock()).isTemporary()) {
            event.setCancelled(true);
        }
    }
}
