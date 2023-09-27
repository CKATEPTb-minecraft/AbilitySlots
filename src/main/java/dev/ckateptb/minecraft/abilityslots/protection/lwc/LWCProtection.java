package dev.ckateptb.minecraft.abilityslots.protection.lwc;

import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import dev.ckateptb.minecraft.abilityslots.config.AbilitySlotsConfig;
import dev.ckateptb.minecraft.abilityslots.protection.AbstractProtection;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class LWCProtection extends AbstractProtection {
    private final LWC lwc;

    public LWCProtection(Plugin plugin, AbilitySlotsConfig config) {
        super(config);
        this.lwc = ((LWCPlugin) plugin).getLWC();
    }

    @Override
    public boolean canUse(LivingEntity entity, Location location) {
        if (!this.config.getGlobal().getProtection().isRespectLWC()) return true;
        if (entity instanceof Player player) {
            com.griefcraft.model.Protection protection = this.lwc.getProtectionCache().getProtection(location.getBlock());
            return protection == null || this.lwc.canAccessProtection(player, protection);
        }
        return true;
    }
}
