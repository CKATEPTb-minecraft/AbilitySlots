package dev.ckateptb.minecraft.abilityslots.user;

import dev.ckateptb.common.tableclothcontainer.IoC;
import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.board.AbilityBoardHolder;
import dev.ckateptb.minecraft.abilityslots.ability.board.config.AbilityBoardConfig;
import dev.ckateptb.minecraft.abilityslots.ability.category.AbilityCategory;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.service.AbilityDeclarationService;
import dev.ckateptb.minecraft.abilityslots.ability.enums.ActivationMethod;
import dev.ckateptb.minecraft.abilityslots.ability.sequence.annotation.AbilityAction;
import dev.ckateptb.minecraft.abilityslots.database.preset.model.AbilityBoardPreset;
import dev.ckateptb.minecraft.abilityslots.database.preset.repository.AbilityBoardPresetRepository;
import dev.ckateptb.minecraft.abilityslots.database.user.model.UserBoard;
import dev.ckateptb.minecraft.abilityslots.database.user.repository.UserBoardRepository;
import dev.ckateptb.minecraft.abilityslots.energy.board.EnergyBoardHolder;
import dev.ckateptb.minecraft.abilityslots.energy.config.EnergyConfig;
import dev.ckateptb.minecraft.abilityslots.energy.event.PlayerEnergyChangeEvent;
import dev.ckateptb.minecraft.abilityslots.entity.PlayerAbilityTarget;
import dev.ckateptb.minecraft.abilityslots.predicate.AbilityConditional;
import dev.ckateptb.minecraft.abilityslots.predicate.CategoryConditional;
import dev.ckateptb.minecraft.abilityslots.user.service.AbilityUserService;
import dev.ckateptb.minecraft.colliders.internal.math3.util.FastMath;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import reactor.core.scheduler.Schedulers;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Stream;

public class PlayerAbilityUser extends PlayerAbilityTarget implements AbilityUser, EnergyBoardHolder, AbilityBoardHolder {
    protected final IAbilityDeclaration<? extends Ability>[] abilities = new IAbilityDeclaration<?>[9];
    protected final List<AbilityAction> actionHistory = new ArrayList<>();
    protected final Map<IAbilityDeclaration<? extends Ability>, Long> cooldowns = new HashMap<>();
    protected double currentEnergy;
    private final AbilityUserService service;
    private final Map<String, AbilityBoardPreset> presets = Collections.synchronizedMap(new HashMap<>());

    public PlayerAbilityUser(Player player, AbilityUserService service) {
        super(player);
        this.service = service;
        this.showEnergyBoard();
        this.showAbilityBoard();
        Schedulers.boundedElastic().schedule(() -> {
            try {
                this.loadPresets();
                this.loadCurrentBoard();
            } catch (Exception ignored) {

            }
        });
    }

    @Override
    public IAbilityDeclaration<? extends Ability>[] getAbilities() {
        return this.overrideAbilities == null ? this.abilities : this.overrideAbilities;
    }

    @Override
    public IAbilityDeclaration<? extends Ability> getAbility(int slot) {
        Validate.inclusiveBetween(1, 9, slot);
        return this.getAbilities()[slot - 1];
    }

    @Override
    public IAbilityDeclaration<? extends Ability> getSelectedAbility() {
        int slot = this.getInventory().getHeldItemSlot() + 1;
        return this.getAbility(slot);
    }

    @Override
    public void setAbility(int slot, IAbilityDeclaration<? extends Ability> ability) {
        this.setAbility(slot, ability, true);
    }

    public void setAbility(int slot, IAbilityDeclaration<? extends Ability> ability, boolean save) {
        Validate.inclusiveBetween(1, 9, slot);
        this.abilities[slot - 1] = ability;
        if (save) {
            this.saveCurrentBoard();
        }
    }

    @Override
    public synchronized Stream<Ability> getAbilityInstances() {
        return this.service.getAbilityInstanceService().instances(this);
    }

    @Override
    public synchronized <T extends Ability> Stream<T> getAbilityInstances(Class<T> type) {
        return this.getAbilityInstances().filter(ability -> ability.getClass().equals(type)).map(ability -> (T) ability);
    }

    public synchronized List<AbilityAction> registerAction(AbilityAction action) {
        int size = this.actionHistory.size();
        if(size > 0) {
            AbilityAction previous = this.actionHistory.get(size - 1);
            if (previous.ability().equals(action.ability())) {
                if (previous.action().equals(action.action())) {
                    return this.actionHistory;
                }
            }
        }
        this.actionHistory.add(action);
        return this.actionHistory;
    }

    @Override
    public boolean canUse(IAbilityDeclaration<? extends Ability> ability) {
        return new AbilityConditional.Builder()
                .hasPermission()
                .hasCategory()
                .isEnabled()
                .withoutCooldown()
                .custom((user, declaration) -> ((PlayerAbilityUser) user).isAbilitiesEnabled())
                .build().matches(this, ability);
    }

    @Override
    public boolean canUse(AbilityCategory category) {
        return new CategoryConditional.Builder().hasPermission().isEnabled().build().matches(this, category);
    }

    @Override
    public boolean canUse(Location location) {
        return this.service.getProtectionService().canUse(this, location);
    }

    @Override
    public boolean canBind(IAbilityDeclaration<? extends Ability> ability) {
        return new AbilityConditional.Builder()
                .hasPermission()
                .hasCategory()
                .isCategoryEnabled()
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
    public synchronized void updateAbilityBoard() {
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
                sb.append(ability.getFormattedName(this));
            }
            String result = sb.toString().replaceAll("&", "§");
            scores.add(result);
            Score score = objective.getScore(result);
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
                    updates.add(getUniquePrefix(slotIndex) + ability.getFormattedName(this));
                }
            }
        }
        for (String update : updates) {
            Score score = objective.getScore(update);
            if (score.getScore() != -slotIndex) {
                score.setScore(-slotIndex);
            }
            scores.add(ChatColor.translateAlternateColorCodes('&', update));
            ++slotIndex;
        }
    }

    private String getUniquePrefix(int index) {
        return index < 22 ? ChatColor.values()[index].toString() + ChatColor.RESET : ChatColor.RESET + getUniquePrefix(index - 22);
    }

    // Ability Board - END

    // Presets - START
    private void loadPresets() {
        AbilityBoardPresetRepository repository = this.service.getPresetRepository();
        UUID id = this.handle_.getUniqueId();
        this.presets.clear();
        repository.getPresets(id).forEach(preset -> {
            this.presets.put(preset.getName().toLowerCase(), preset);
        });
    }

    public Optional<AbilityBoardPreset> getPreset(String name) {
        return Optional.ofNullable(this.presets.get(name));
    }

    public Set<String> getPresets() {
        return Collections.unmodifiableSet(this.presets.keySet());
    }

    public void applyPreset(AbilityBoardPreset preset) {
        Schedulers.boundedElastic().schedule(() -> {
            AbilityDeclarationService declarationService = IoC.getBean(AbilityDeclarationService.class);
            for (int i = 1; i <= 9; i++) {
                try {
                    this.setAbility(i, null, false);
                    Method declaredMethod = preset.getClass().getDeclaredMethod("getSlot_" + i);
                    declaredMethod.setAccessible(true);
                    String abilityName = (String) declaredMethod.invoke(preset);
                    int finalI = i;
                    declarationService.findDeclaration(abilityName).ifPresent(declaration -> {
                        if (this.canBind(declaration)) {
                            this.setAbility(finalI, declaration, false);
                        }
                    });
                } catch (Exception ignored) {
                }
            }
            this.saveCurrentBoard();
        });
    }

    public boolean saveAsPreset(String name) {
        if (this.presets.containsKey(name.toLowerCase())) return false;
        Schedulers.boundedElastic().schedule(() -> {
            AbilityBoardPresetRepository repository = this.service.getPresetRepository();
            AbilityBoardPreset preset = new AbilityBoardPreset();
            preset.setPlayer(this.handle_.getUniqueId());
            preset.setName(name);
            this.fillPreset(preset);
            this.presets.put(name.toLowerCase(), preset);
            try {
                repository.createOrUpdate(preset);
            } catch (Exception ignored) {
            }
        });
        return true;
    }

    public void deletePreset(AbilityBoardPreset preset) {
        this.presets.remove(preset.getName().toLowerCase());
        Schedulers.boundedElastic().schedule(() -> {
            AbilityBoardPresetRepository repository = this.service.getPresetRepository();
            try {
                repository.delete(preset);
            } catch (Exception ignored) {
            }
        });
    }

    @SneakyThrows
    private void fillPreset(Object preset) {
        for (int i = 1; i <= 9; i++) {
            Method declaredMethod = preset.getClass().getDeclaredMethod("setSlot_" + i, String.class);
            declaredMethod.setAccessible(true);
            declaredMethod.invoke(preset, Optional.ofNullable(this.abilities[i - 1]).map(IAbilityDeclaration::getName).orElse(null));
        }
    }

    // Presets - END

    // Current board - START
    private void loadCurrentBoard() throws SQLException {
        UserBoardRepository repository = this.service.getBoardsRepository();
        Optional.ofNullable(repository.queryForId(this.handle_.getUniqueId())).ifPresent(board -> {
            AbilityBoardPreset preset = new AbilityBoardPreset();
            preset.setSlot_1(board.getSlot_1());
            preset.setSlot_2(board.getSlot_2());
            preset.setSlot_3(board.getSlot_3());
            preset.setSlot_4(board.getSlot_4());
            preset.setSlot_5(board.getSlot_5());
            preset.setSlot_6(board.getSlot_6());
            preset.setSlot_7(board.getSlot_7());
            preset.setSlot_8(board.getSlot_8());
            preset.setSlot_9(board.getSlot_9());
            this.applyPreset(preset);
        });
    }

    public void saveCurrentBoard() {
        Schedulers.boundedElastic().schedule(() -> {
            UserBoardRepository repository = this.service.getBoardsRepository();
            UserBoard board = new UserBoard();
            board.setPlayer(this.handle_.getUniqueId());
            this.fillPreset(board);
            try {
                repository.createOrUpdate(board);
            } catch (Exception ignored) {

            }
        });
    }
    // Current board - END

    // Ability toggle - START
    private boolean abilitiesEnabled = true;

    public boolean isAbilitiesEnabled() {
        return this.abilitiesEnabled;
    }

    public void enableAbilities() {
        this.abilitiesEnabled = true;
    }

    public void disableAbilities() {
        this.abilitiesEnabled = false;
    }
    // Ability toggle - END

    // Force ability - START
    protected IAbilityDeclaration<? extends Ability>[] overrideAbilities;

    public void setOverrideAbilities(IAbilityDeclaration<? extends Ability>[] abilities) {
        Validate.isTrue(abilities == null || abilities.length == 9);
        this.overrideAbilities = abilities;
    }

    public void removeOverrideAbilities() {
        this.setOverrideAbilities(null);
    }

    public Optional<IAbilityDeclaration<? extends Ability>[]> getOverrideAbilities() {
        return Optional.ofNullable(this.overrideAbilities);
    }
    // Force ability - END
}
