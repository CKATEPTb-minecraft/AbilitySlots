package dev.ckateptb.minecraft.abilityslots.entity;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.atom.chain.AtomChain;
import dev.ckateptb.minecraft.colliders.Colliders;
import dev.ckateptb.minecraft.colliders.geometry.RayTraceCollider;
import dev.ckateptb.minecraft.colliders.internal.math3.util.FastMath;
import dev.ckateptb.minecraft.colliders.math.ImmutableVector;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.MainHand;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

public class LivingEntityAbilityTarget extends EntityAbilityTarget implements LivingAbilityTarget {
    protected final LivingEntity entity;

    public LivingEntityAbilityTarget(LivingEntity entity) {
        super(entity);
        this.entity = entity;
    }

    public void damage(Ability ability, double amount) {
        this.damage(ability, amount, false);
    }

    public void damage(Ability ability, double amount, boolean force) {
        AtomChain.sync(entity).promise(livingEntity -> {
            if (force) entity.setNoDamageTicks(0);
            livingEntity.damage(amount, ability.getUser().getHandle());
        });
    }

    public boolean isSneaking() {
        return true;
    }

    public ImmutableVector findPosition(double range) {
        return this.findPosition(range, block -> true);
    }

    public ImmutableVector findPosition(double range, boolean ignoreLiquids) {
        return this.findPosition(range, ignoreLiquids, block -> true);
    }

    public ImmutableVector findPosition(double range, boolean ignoreLiquids, Predicate<Block> blockFilter) {
        return this.findPosition(range, ignoreLiquids, entity -> true, blockFilter);
    }

    public ImmutableVector findPosition(double range, Predicate<Block> blockFilter) {
        return this.findPosition(range, entity -> true, blockFilter);
    }

    public ImmutableVector findPosition(double range, Predicate<Entity> entityFilter, Predicate<Block> blockFilter) {
        return this.findPosition(range, false, entityFilter, blockFilter);
    }

    public ImmutableVector findPosition(double range, boolean ignoreLiquids, Predicate<Entity> entityFilter, Predicate<Block> blockFilter) {
        return this.findPosition(range, true, ignoreLiquids, entityFilter, blockFilter);
    }

    public ImmutableVector findPosition(double range, boolean ignorePassable, boolean ignoreLiquids, Predicate<Entity> entityFilter, Predicate<Block> blockFilter) {
        return this.findPosition(range, false, ignorePassable, ignoreLiquids, entityFilter, blockFilter);
    }

    public ImmutableVector findPosition(double range, boolean ignoreBlocks, boolean ignorePassable, boolean ignoreLiquids, Predicate<Entity> entityFilter, Predicate<Block> blockFilter) {
        return this.findPosition(range, false, ignoreBlocks, ignorePassable, ignoreLiquids, entityFilter, blockFilter);
    }

    public ImmutableVector findPosition(double range, double raySize) {
        return this.findPosition(range, raySize, block -> true);
    }

    public ImmutableVector findPosition(double range, double raySize, Predicate<Block> blockFilter) {
        return this.findPosition(range, raySize, entity -> true, blockFilter);
    }

    public ImmutableVector findPosition(double range, double raySize, Predicate<Entity> entityFilter, Predicate<Block> blockFilter) {
        return this.findPosition(range, raySize, false, entityFilter, blockFilter);
    }

    public ImmutableVector findPosition(double range, double raySize, boolean ignoreLiquids, Predicate<Entity> entityFilter, Predicate<Block> blockFilter) {
        return this.findPosition(range, raySize, true, ignoreLiquids, entityFilter, blockFilter);
    }

    public ImmutableVector findPosition(double range, double raySize, boolean ignorePassable, boolean ignoreLiquids, Predicate<Entity> entityFilter, Predicate<Block> blockFilter) {
        return this.findPosition(range, raySize, false, ignorePassable, ignoreLiquids, entityFilter, blockFilter);
    }

    public ImmutableVector findPosition(double range, double raySize, boolean ignoreBlocks, boolean ignorePassable, boolean ignoreLiquids, Predicate<Entity> entityFilter, Predicate<Block> blockFilter) {
        return this.findPosition(range, raySize, false, ignoreBlocks, ignorePassable, ignoreLiquids, entityFilter, blockFilter);
    }

    public ImmutableVector findPosition(double range, boolean ignoreEntity, boolean ignoreBlocks, boolean ignorePassable, boolean ignoreLiquids, Predicate<Entity> entityFilter, Predicate<Block> blockFilter) {
        return this.findPosition(range, 0, ignoreEntity, ignoreBlocks, ignorePassable, ignoreLiquids, entityFilter, blockFilter);
    }

    public ImmutableVector findPosition(double range, double raySize, boolean ignoreEntity, boolean ignoreBlocks, boolean ignorePassable, boolean ignoreLiquids, Predicate<Entity> entityFilter, Predicate<Block> blockFilter) {
        RayTraceCollider ray = Colliders.ray(this.entity, range, raySize);
        return ImmutableVector.of(ray.getPosition(ignoreEntity,
                        ignoreBlocks,
                        ignoreLiquids,
                        ignorePassable,
                        entityFilter.and(entity -> entity != this.entity),
                        blockFilter.and(block -> !block.getType().isAir()))
                .orElseGet(() -> this.getDirection().normalize().multiply(range)));
    }

    public Optional<Block> findBlock(double range) {
        return this.findBlock(range, 0);
    }

    public Optional<Block> findBlock(double range, double raySize) {
        return this.findBlock(range, raySize, false);
    }

    public Optional<Block> findBlock(double range, double raySize, boolean ignoreLiquid) {
        return this.findBlock(range, raySize, ignoreLiquid, true);
    }

    public Optional<Block> findBlock(double range, double raySize, boolean ignoreLiquid, boolean ignorePassable) {
        return this.findBlock(range, raySize, ignoreLiquid, ignorePassable, block -> true);
    }

    public Optional<Block> findBlock(double range, boolean ignoreLiquid) {
        return this.findBlock(range, ignoreLiquid, true);
    }

    public Optional<Block> findBlock(double range, boolean ignoreLiquid, boolean ignorePassable) {
        return this.findBlock(range, ignoreLiquid, ignorePassable, block -> true);
    }

    public Optional<Block> findBlock(double range, boolean ignoreLiquid, boolean ignorePassable, Predicate<Block> filter) {
        return this.findBlock(range, 0, ignoreLiquid, ignorePassable, filter);
    }

    public Optional<Block> findBlock(double range, double raySize, boolean ignoreLiquid, boolean ignorePassable, Predicate<Block> filter) {
        return Colliders.ray(this.entity, range, raySize).getBlock(ignoreLiquid, ignorePassable, filter.and(block -> !block.getType().isAir()));
    }

    public Optional<LivingEntity> findLivingEntity(double range, double raySize) {
        return this.findLivingEntity(range, raySize, false, entity -> true);
    }

    public Optional<LivingEntity> findLivingEntity(double range) {
        return this.findLivingEntity(range, entity -> true);
    }

    public Optional<LivingEntity> findLivingEntity(double range, Predicate<LivingEntity> predicate) {
        return this.findLivingEntity(range, 0, false, predicate);
    }

    public Optional<LivingEntity> findLivingEntity(double range, double raySize, boolean ignoreBlock, Predicate<LivingEntity> predicate) {
        if (!ignoreBlock) {
            Optional<Block> optional = this.findBlock(range, raySize, true);
            if (optional.isPresent()) {
                Block block = optional.get();
                range = ImmutableVector.of(block.getLocation().toCenterLocation()).distance(this.getCenteredLocation());
            }
        }
        return Colliders.ray(this.entity, range, raySize)
                .getEntity(entity -> entity instanceof LivingEntity target
                        && target != this.entity
                        && predicate.test(target))
                .map(e -> (LivingEntity) e);
    }

    public Optional<Entity> getTargetEntity(int range, boolean ignoreBlocks) {
        return Optional.ofNullable(this.entity.getTargetEntity(range, ignoreBlocks));
    }

    public ImmutableVector getEyeLocation() {
        return ImmutableVector.of(this.entity.getEyeLocation());
    }

    public ImmutableVector getDirection() {
        return ImmutableVector.of(this.entity.getEyeLocation().getDirection());
    }

    public float getYaw() {
        return this.entity.getEyeLocation().getYaw();
    }

    public float getPitch() {
        return this.entity.getEyeLocation().getPitch();
    }

    public void addPotionEffect(Ability ability, @NotNull PotionEffect effect) {
        AtomChain.sync(this.entity).promise(livingEntity -> livingEntity.addPotionEffect(effect));
    }

    public void addPotionEffects(Ability ability, @NotNull Collection<PotionEffect> effects) {
        AtomChain.sync(this.entity).promise(livingEntity -> livingEntity.addPotionEffects(effects));
    }

    public boolean hasPotionEffect(@NotNull PotionEffectType type) {
        return this.entity.hasPotionEffect(type);
    }

    public PotionEffect getPotionEffect(@NotNull PotionEffectType type) {
        return this.entity.getPotionEffect(type);
    }

    public void removePotionEffect(@NotNull PotionEffectType type) {
        this.entity.removePotionEffect(type);
    }

    public Collection<PotionEffect> getPotionEffects() {
        return this.entity.getActivePotionEffects();
    }

    public boolean isDead() {
        return this.entity.isDead();
    }

    public boolean isSprinting() {
        return true;
    }

    public boolean hasPermission(String permission) {
        return true;
    }

    public GameMode getGameMode() {
        return GameMode.ADVENTURE;
    }

    public boolean isOnline() {
        return true;
    }

    public MainHand getMainHand() {
        return MainHand.RIGHT;
    }

    public ImmutableVector getMainHandLocation() {
        return this.getHandLocation(null);
    }

    public ImmutableVector getHandLocation(@Nullable MainHand hand) {
        ImmutableVector direction = getDirection();
        if (hand == null) return this.getEyeLocation().add(direction.multiply(0.4));
        double angle = FastMath.toRadians(getYaw());
        ImmutableVector location = this.getLocation();
        ImmutableVector offset = direction.multiply(0.4).add(0, 1.2, 0);
        ImmutableVector vector = new ImmutableVector(FastMath.cos(angle), 0, FastMath.sin(angle)).normalize().multiply(0.3);
        return (hand == MainHand.LEFT ? location.add(vector) : location.subtract(vector)).add(offset);
    }

    @Override
    public boolean isLiving() {
        return true;
    }

    @Override
    public LivingEntity getHandle() {
        return this.entity;
    }
}
