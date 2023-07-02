package dev.ckateptb.minecraft.abilityslots.user;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.board.AbilityBoardHolder;
import dev.ckateptb.minecraft.abilityslots.ability.board.config.AbilityBoardConfig;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.enums.ActivationMethod;
import dev.ckateptb.minecraft.abilityslots.ability.sequence.annotation.AbilityAction;
import dev.ckateptb.minecraft.abilityslots.energy.board.EnergyBoardHolder;
import dev.ckateptb.minecraft.abilityslots.energy.config.EnergyConfig;
import dev.ckateptb.minecraft.abilityslots.energy.event.PlayerEnergyChangeEvent;
import dev.ckateptb.minecraft.abilityslots.entity.PlayerAbilityTarget;
import dev.ckateptb.minecraft.abilityslots.predicate.AbilityConditional;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import dev.ckateptb.minecraft.colliders.internal.math3.util.FastMath;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.time.Duration;
import java.util.*;

public class PlayerAbilityUser extends PlayerAbilityTarget implements AbilityUser, EnergyBoardHolder, AbilityBoardHolder {
    protected final IAbilityDeclaration<? extends Ability>[] abilities = new IAbilityDeclaration<?>[9];
    protected final List<AbilityAction> actionHistory = new ArrayList<>();
    protected final Map<IAbilityDeclaration<? extends Ability>, Long> cooldowns = new HashMap<>();
    protected double currentEnergy;
    private final AbilityUserService service;

    public PlayerAbilityUser(Player player, AbilityUserService service) {
        super(player);
        this.service = service;
        this.showEnergyBoard();
        this.showAbilityBoard();
    }

    @Override
    public IAbilityDeclaration<? extends Ability>[] getAbilities() {
        return this.abilities;
    }

    @Override
    public IAbilityDeclaration<? extends Ability> getAbility(int slot) {
        Validate.inclusiveBetween(1, 9, slot);
        return this.abilities[slot - 1];
    }

    @Override
    public IAbilityDeclaration<? extends Ability> getSelectedAbility() {
        int slot = this.getInventory().getHeldItemSlot() + 1;
        return this.getAbility(slot);
    }

    @Override
    public void setAbility(int slot, IAbilityDeclaration<? extends Ability> ability) {
        Validate.inclusiveBetween(1, 9, slot);
        this.abilities[slot - 1] = ability;
    }

    public synchronized List<AbilityAction> registerAction(AbilityAction action) {
        this.actionHistory.add(action);
        return this.actionHistory;
    }

    @Override
    public boolean canUse(IAbilityDeclaration<? extends Ability> ability) {
        return new AbilityConditional.Builder()
                .hasPermission()
                .hasCategory()
                .isEnabled()
                .build().matches(this, ability);
    }

    @Override
    public boolean canBind(IAbilityDeclaration<? extends Ability> ability) {
        return new AbilityConditional.Builder()
                .hasPermission()
                .hasCategory()
                .isEnabled()
                .isBindable()
                .build().matches(this, ability);
    }

    @Override
    public synchronized void setCooldown(IAbilityDeclaration<? extends Ability> ability, long duration) {
        if (this.isCooldownEnabled()) {
            this.cooldowns.put(ability, duration + System.currentTimeMillis());
        }
    }

    @Override
    public boolean hasCooldown(IAbilityDeclaration<? extends Ability> ability) {
        if (!this.isCooldownEnabled()) return false;
        return this.getCooldown(ability) > System.currentTimeMillis();
    }

    @Override
    public synchronized long getCooldown(IAbilityDeclaration<? extends Ability> ability) {
        return this.cooldowns.getOrDefault(ability, 0L);
    }

    @Override
    public boolean isCooldownEnabled() {
        return this.service.getConfig().getGlobal().getCooldown().isEnabled();
    }

    @Override
    public double getEnergy() {
        if (!this.isEnergyEnabled()) return Double.MAX_VALUE;
        return this.currentEnergy;
    }

    @Override
    public boolean removeEnergy(double value) {
        if (!this.isEnergyEnabled()) return true;
        if (this.currentEnergy >= value) {
            this.setEnergy(this.currentEnergy - value);
            return true;
        }
        return false;
    }

    @Override
    public void addEnergy(double value) {
        this.setEnergy(this.currentEnergy + value);
    }

    @Override
    public void setEnergy(double value) {
        if (!this.isEnergyEnabled()) return;
        PlayerEnergyChangeEvent event = new PlayerEnergyChangeEvent(this, this.currentEnergy, value);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        this.currentEnergy = FastMath.max(FastMath.min(event.getEnergy(), this.getMaxEnergy()), 0);
        this.updateEnergyBoard();
    }

    @Override
    public double getMaxEnergy() {
        if (!this.isEnergyEnabled()) return Double.MAX_VALUE;
        return this.service.getConfig().getGlobal().getEnergy().getMax();
    }

    private boolean isEnergyEnabled() {
        return this.service.getConfig().getGlobal().getEnergy().isEnabled();
    }

    public boolean equals(Object other) {
        return super.equals(other);
    }

    // Energy Board - START

    private BossBar energyBar;

    @Override
    public synchronized void showEnergyBoard() {
        if (!this.hasPermission("abilityslots.energybar.display")) return;
        EnergyConfig energyConfig = service.getConfig().getGlobal().getEnergy();
        if (this.energyBar != null || !energyConfig.isEnabled()) return;
        this.energyBar = Bukkit.createBossBar(energyConfig.getName(), energyConfig.getEnergyColor(), energyConfig.getEnergyStyle());
        this.energyBar.addPlayer(Objects.requireNonNull(this.handle_.getPlayer()));
    }

    @Override
    public synchronized void hideEnergyBoard() {
        if (this.energyBar == null) return;
        this.energyBar.removePlayer(Objects.requireNonNull(this.handle_.getPlayer()));
        this.energyBar = null;
    }

    @Override
    public void updateEnergyBoard() {
        if (this.isEnergyEnabled()) {
            if (this.energyBar == null) return;
            double progress = this.getEnergy() / this.getMaxEnergy();
            if (this.energyBar.getProgress() != progress) {
                this.energyBar.setProgress(progress);
            }
        } else if (this.energyBar != null) {
            this.hideEnergyBoard();
        }
    }

    // Energy Board - END

    // Ability Board - START

    private Scoreboard scoreboard;
    private Objective objective;

    private boolean isAbilityBoardEnabled() {
        return this.service.getConfig().getGlobal().getBoard().isEnabled();
    }

    @Override
    public void showAbilityBoard() {
        if (this.scoreboard != null) return;
        if (!this.hasPermission("abilityslots.board.display")) return;
        AbilityBoardConfig config = service.getConfig().getGlobal().getBoard();
        if (!config.isEnabled()) return;
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        this.scoreboard = manager.getNewScoreboard();
        Component header = Component.text().content(config.getHeader()).build();
        this.objective = scoreboard.registerNewObjective("abilityboard", Criteria.DUMMY, header, RenderType.INTEGER);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    @Override
    public void hideAbilityBoard() {
        if (this.scoreboard == null) return;
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        this.setScoreboard(manager.getMainScoreboard());
        this.scoreboard = null;
        this.objective = null;
    }

    @Override
    public void updateAbilityBoard() {
        if (this.isAbilityBoardEnabled()) {
            if (this.scoreboard == null || this.objective == null) return;
            if (this.handle_.getScoreboard() != this.scoreboard) {
                this.setScoreboard(scoreboard);
            }
            Set<String> scores = new HashSet<>();
            this.updateSlots(scores);
            this.updateSequences(scores);
            // Clear out any scores that aren't needed.
            for (String entry : scoreboard.getEntries()) {
                if (scores.contains(entry)) continue;
                scoreboard.resetScores(entry);
            }
        } else if (this.scoreboard != null) {
            this.hideAbilityBoard();
        }
    }

    private void updateSlots(Set<String> scores) {
        AbilityBoardConfig config = service.getConfig().getGlobal().getBoard();
        int currentSlot = this.getInventory().getHeldItemSlot() + 1;
        for (int slotIndex = 1; slotIndex <= 9; ++slotIndex) {
            StringBuilder sb = new StringBuilder();
            IAbilityDeclaration<? extends Ability> ability = this.getAbility(slotIndex);
            sb.append(this.getUniquePrefix(slotIndex));
            if (slotIndex == currentSlot) {
                sb.append(">");
            }
            if (ability == null) {
                sb.append(config.getEmpty());
            } else {
                if (this.hasCooldown(ability)) {
                    sb.append(config.getCooldown()
                            .replaceAll("%category_prefix%", ability.getCategory().getAbilityPrefix())
                            .replaceAll("%ability%", ability.getDisplayName())
                            .replaceAll("%cooldown%", String.valueOf(
                                    Duration.ofMillis(this.getCooldown(ability) - System.currentTimeMillis()).getSeconds()
                            )));
                } else {
                    sb.append(config.getAbility()
                            .replaceAll("%category_prefix%", ability.getCategory().getAbilityPrefix())
                            .replaceAll("%ability%", ability.getDisplayName()));
                }
            }
            scores.add(sb.toString());
            Score score = objective.getScore(sb.toString());
            // Only set the new score if it changes.
            if (score.getScore() != -slotIndex) {
                score.setScore(-slotIndex);
            }
        }
    }

    private void updateSequences(Set<String> scores) {
        int slotIndex = 10;
        AbilityBoardConfig config = service.getConfig().getGlobal().getBoard();
        List<String> updates = new ArrayList<>();
        for (IAbilityDeclaration<? extends Ability> ability : this.cooldowns.keySet()) {
            if (ability.isActivatedBy(ActivationMethod.SEQUENCE)) {
                if (this.hasCooldown(ability)) {
                    if (updates.isEmpty()) {
                        updates.add(config.getComboDivider());
                    }
                    updates.add(getUniquePrefix(slotIndex) + config.getCooldown()
                            .replaceAll("%category_prefix%", ability.getCategory().getAbilityPrefix())
                            .replaceAll("%ability%", ability.getDisplayName())
                            .replaceAll("%cooldown%", String.valueOf(
                                    Duration.ofMillis(this.getCooldown(ability) - System.currentTimeMillis()).getSeconds()
                            )));
                }
            }
        }
        for (String update : updates) {
            Score score = objective.getScore(update);
            if (score.getScore() != -slotIndex) {
                score.setScore(-slotIndex);
            }
            scores.add(update);
            ++slotIndex;
        }
    }

    private String getUniquePrefix(int index) {
        return index < 22 ? ChatColor.values()[index].toString() + ChatColor.RESET : ChatColor.RESET + getUniquePrefix(index - 22);
    }

    // Ability Board - END
}
