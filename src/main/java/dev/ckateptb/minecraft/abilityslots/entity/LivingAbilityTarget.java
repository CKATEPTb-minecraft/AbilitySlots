package dev.ckateptb.minecraft.abilityslots.entity;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
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

public interface LivingAbilityTarget extends AbilityTarget {
    @Override
    LivingEntity getHandle();

    /**
     * Deals the given amount of damage to this entity, from a specified entity.
     *
     * @param amount  Amount of damage to deal
     * @param ability {@link Ability} that caused this damage
     */
    void damage(Ability ability, double amount);

    /**
     * Deals the given amount of damage to this entity, from a specified entity.
     *
     * @param amount              Amount of damage to deal
     * @param ability             {@link Ability} that caused this damage
     * @param force cause this damage ignoring noDamageTicks
     */
    void damage(Ability ability, double amount, boolean force);

    /**
     * @return true if it is in sneak mode,
     */
    boolean isSneaking();

    /**
     * Get distant position found in the entity direction
     *
     * @param range this is the maximum distance to scan
     *              <p>raySize <b>default 0</b> entity bounding boxes will be uniformly expanded (or shrinked) by this value</p>
     *              <p>ignoreEntity <b>default false</b> should ignore entity on scan path</p>
     *              <p>ignoreBlocks <b>default false</b> should ignore blocks on scan path</p>
     *              <p>ignorePassable <b>default true</b> should ignore passable blocks on scan path</p>
     *              <p>ignoreLiquids <b>default false</b> should ignore liquids on scan path</p>
     *              <p>entityFilter <b>entity -> true</b> entity predicate</p>
     *              <p>blockFilter <b>block -> true</b> block predicate</p>
     * @return distant position in the direction of the entity
     */
    ImmutableVector findPosition(double range);

    /**
     * Get distant position found in the entity direction
     *
     * @param range         this is the maximum distance to scan
     *                      <p>raySize <b>default 0</b> entity bounding boxes will be uniformly expanded (or shrinked) by this value</p>
     *                      <p>ignoreEntity <b>default false</b> should ignore entity on scan path</p>
     *                      <p>ignoreBlocks <b>default false</b> should ignore blocks on scan path</p>
     *                      <p>ignorePassable <b>default true</b> should ignore passable blocks on scan path</p>
     * @param ignoreLiquids should ignore liquids on scan path
     *                      <p>entityFilter <b>entity -> true</b> entity predicate</p>
     *                      <p>blockFilter <b>block -> true</b> block predicate</p>
     * @return distant position in the direction of the entity
     */
    ImmutableVector findPosition(double range, boolean ignoreLiquids);

    /**
     * Get distant position found in the entity direction
     *
     * @param range         this is the maximum distance to scan
     *                      <p>raySize <b>default 0</b> entity bounding boxes will be uniformly expanded (or shrinked) by this value</p>
     *                      <p>ignoreEntity <b>default false</b> should ignore entity on scan path</p>
     *                      <p>ignoreBlocks <b>default false</b> should ignore blocks on scan path</p>
     *                      <p>ignorePassable <b>default true</b> should ignore passable blocks on scan path</p>
     * @param ignoreLiquids should ignore liquids on scan path
     *                      <p>entityFilter <b>entity -> true</b> entity predicate</p>
     * @param blockFilter   block predicate
     * @return distant position in the direction of the entity
     */
    ImmutableVector findPosition(double range, boolean ignoreLiquids, Predicate<Block> blockFilter);

    /**
     * Get distant position found in the entity direction
     *
     * @param range       this is the maximum distance to scan
     *                    <p>raySize <b>default 0</b> entity bounding boxes will be uniformly expanded (or shrinked) by this value</p>
     *                    <p>ignoreEntity <b>default false</b> should ignore entity on scan path</p>
     *                    <p>ignoreBlocks <b>default false</b> should ignore blocks on scan path</p>
     *                    <p>ignorePassable <b>default true</b> should ignore passable blocks on scan path</p>
     *                    <p>ignoreLiquids <b>default false</b> should ignore liquids on scan path</p>
     *                    <p>entityFilter <b>entity -> true</b> entity predicate</p>
     * @param blockFilter block predicate
     * @return distant position in the direction of the entity
     */
    ImmutableVector findPosition(double range, Predicate<Block> blockFilter);

    /**
     * Get distant position found in the entity direction
     *
     * @param range        this is the maximum distance to scan
     *                     <p>raySize <b>default 0</b> entity bounding boxes will be uniformly expanded (or shrinked) by this value</p>
     *                     <p>ignoreEntity <b>default false</b> should ignore entity on scan path</p>
     *                     <p>ignoreBlocks <b>default false</b> should ignore blocks on scan path</p>
     *                     <p>ignorePassable <b>default true</b> should ignore passable blocks on scan path</p>
     *                     <p>ignoreLiquids <b>default false</b> should ignore liquids on scan path</p>
     * @param entityFilter entity predicate
     * @param blockFilter  block predicate
     * @return distant position in the direction of the entity
     */
    ImmutableVector findPosition(double range, Predicate<Entity> entityFilter, Predicate<Block> blockFilter);

    /**
     * Get distant position found in the entity direction
     *
     * @param range         this is the maximum distance to scan
     *                      <p>raySize <b>default 0</b> entity bounding boxes will be uniformly expanded (or shrinked) by this value</p>
     *                      <p>ignoreEntity <b>default false</b> should ignore entity on scan path</p>
     *                      <p>ignoreBlocks <b>default false</b> should ignore blocks on scan path</p>
     *                      <p>ignorePassable <b>default true</b> should ignore passable blocks on scan path</p>
     * @param ignoreLiquids should ignore liquids on scan path
     * @param entityFilter  entity predicate
     * @param blockFilter   block predicate
     * @return distant position in the direction of the entity
     */
    ImmutableVector findPosition(double range, boolean ignoreLiquids, Predicate<Entity> entityFilter, Predicate<Block> blockFilter);

    /**
     * Get distant position found in the entity direction
     *
     * @param range          this is the maximum distance to scan
     *                       <p>raySize <b>default 0</b> entity bounding boxes will be uniformly expanded (or shrinked) by this value</p>
     *                       <p>ignoreEntity <b>default false</b> should ignore entity on scan path</p>
     *                       <p>ignoreBlocks <b>default false</b> should ignore blocks on scan path</p>
     * @param ignorePassable should ignore passable blocks on scan path
     * @param ignoreLiquids  should ignore liquids on scan path
     * @param entityFilter   entity predicate
     * @param blockFilter    block predicate
     * @return distant position in the direction of the entity
     */
    ImmutableVector findPosition(double range, boolean ignorePassable, boolean ignoreLiquids, Predicate<Entity> entityFilter, Predicate<Block> blockFilter);

    /**
     * Get distant position found in the entity direction
     *
     * @param range          this is the maximum distance to scan
     *                       <p>raySize <b>default 0</b> entity bounding boxes will be uniformly expanded (or shrinked) by this value</p>
     *                       <p>ignoreEntity <b>default false</b> should ignore entity on scan path</p>
     * @param ignoreBlocks   should ignore blocks on scan path
     * @param ignorePassable should ignore passable blocks on scan path
     * @param ignoreLiquids  should ignore liquids on scan path
     * @param entityFilter   entity predicate
     * @param blockFilter    block predicate
     * @return distant position in the direction of the entity
     */
    ImmutableVector findPosition(double range, boolean ignoreBlocks, boolean ignorePassable, boolean ignoreLiquids, Predicate<Entity> entityFilter, Predicate<Block> blockFilter);

    /**
     * Get distant position found in the entity direction
     *
     * @param range   this is the maximum distance to scan
     * @param raySize entity bounding boxes will be uniformly expanded (or shrinked) by this value
     *                <p>ignoreEntity <b>default false</b> should ignore entity on scan path</p>
     *                <p>ignoreBlocks <b>default false</b> should ignore blocks on scan path</p>
     *                <p>ignorePassable <b>default true</b> should ignore passable blocks on scan path</p>
     *                <p>ignoreLiquids <b>default false</b> should ignore liquids on scan path</p>
     *                <p>entityFilter <b>entity -> true</b> entity predicate</p>
     *                <p>blockFilter <b>block -> true</b> block predicate</p>
     * @return distant position in the direction of the entity
     */
    ImmutableVector findPosition(double range, double raySize);

    /**
     * Get distant position found in the entity direction
     *
     * @param range       this is the maximum distance to scan
     * @param raySize     entity bounding boxes will be uniformly expanded (or shrinked) by this value
     *                    <p>ignoreEntity <b>default false</b> should ignore entity on scan path</p>
     *                    <p>ignoreBlocks <b>default false</b> should ignore blocks on scan path</p>
     *                    <p>ignorePassable <b>default true</b> should ignore passable blocks on scan path</p>
     *                    <p>ignoreLiquids <b>default false</b> should ignore liquids on scan path</p>
     *                    <p>entityFilter <b>entity -> true</b> entity predicate</p>
     * @param blockFilter block predicate
     * @return distant position in the direction of the entity
     */
    ImmutableVector findPosition(double range, double raySize, Predicate<Block> blockFilter);

    /**
     * Get distant position found in the entity direction
     *
     * @param range        this is the maximum distance to scan
     * @param raySize      entity bounding boxes will be uniformly expanded (or shrinked) by this value
     *                     <p>ignoreEntity <b>default false</b> should ignore entity on scan path</p>
     *                     <p>ignoreBlocks <b>default false</b> should ignore blocks on scan path</p>
     *                     <p>ignorePassable <b>default true</b> should ignore passable blocks on scan path</p>
     *                     <p>ignoreLiquids <b>default false</b> should ignore liquids on scan path</p>
     * @param entityFilter entity predicate
     * @param blockFilter  block predicate
     * @return distant position in the direction of the entity
     */
    ImmutableVector findPosition(double range, double raySize, Predicate<Entity> entityFilter, Predicate<Block> blockFilter);

    /**
     * Get distant position found in the entity direction
     *
     * @param range         this is the maximum distance to scan
     * @param raySize       entity bounding boxes will be uniformly expanded (or shrinked) by this value
     *                      <p>ignoreEntity <b>default false</b> should ignore entity on scan path</p>
     *                      <p>ignoreBlocks <b>default false</b> should ignore blocks on scan path</p>
     *                      <p>ignorePassable <b>default true</b> should ignore passable blocks on scan path</p>
     * @param ignoreLiquids should ignore liquids on scan path
     * @param entityFilter  entity predicate
     * @param blockFilter   block predicate
     * @return distant position in the direction of the entity
     */
    ImmutableVector findPosition(double range, double raySize, boolean ignoreLiquids, Predicate<Entity> entityFilter, Predicate<Block> blockFilter);

    /**
     * Get distant position found in the entity direction
     *
     * @param range          this is the maximum distance to scan
     * @param raySize        entity bounding boxes will be uniformly expanded (or shrinked) by this value
     *                       <p>ignoreEntity <b>default false</b> should ignore entity on scan path</p>
     *                       <p>ignoreBlocks <b>default false</b> should ignore blocks on scan path</p>
     * @param ignorePassable should ignore passable blocks on scan path
     * @param ignoreLiquids  should ignore liquids on scan path
     * @param entityFilter   entity predicate
     * @param blockFilter    block predicate
     * @return distant position in the direction of the entity
     */
    ImmutableVector findPosition(double range, double raySize, boolean ignorePassable, boolean ignoreLiquids, Predicate<Entity> entityFilter, Predicate<Block> blockFilter);

    /**
     * Get distant position found in the entity direction
     *
     * @param range          this is the maximum distance to scan
     * @param raySize        entity bounding boxes will be uniformly expanded (or shrinked) by this value
     *                       <p>ignoreEntity <b>default false</b> should ignore entity on scan path</p>
     * @param ignoreBlocks   should ignore blocks on scan path
     * @param ignorePassable should ignore passable blocks on scan path
     * @param ignoreLiquids  should ignore liquids on scan path
     * @param entityFilter   entity predicate
     * @param blockFilter    block predicate
     * @return distant position in the direction of the entity
     */
    ImmutableVector findPosition(double range, double raySize, boolean ignoreBlocks, boolean ignorePassable, boolean ignoreLiquids, Predicate<Entity> entityFilter, Predicate<Block> blockFilter);

    /**
     * Get distant position found in the entity direction
     *
     * @param range          this is the maximum distance to scan
     *                       <p>raySize <b>default 0</b> entity bounding boxes will be uniformly expanded (or shrinked) by this value</p>
     * @param ignoreEntity   should ignore entity on scan path
     * @param ignoreBlocks   should ignore blocks on scan path
     * @param ignorePassable should ignore passable blocks on scan path
     * @param ignoreLiquids  should ignore liquids on scan path
     * @param entityFilter   entity predicate
     * @param blockFilter    block predicate
     * @return distant position in the direction of the entity
     */
    ImmutableVector findPosition(double range, boolean ignoreEntity, boolean ignoreBlocks, boolean ignorePassable, boolean ignoreLiquids, Predicate<Entity> entityFilter, Predicate<Block> blockFilter);

    /**
     * Get distant position found in the entity direction
     *
     * @param range          this is the maximum distance to scan
     * @param raySize        entity bounding boxes will be uniformly expanded (or shrinked) by this value
     * @param ignoreEntity   should ignore entity on scan path
     * @param ignoreBlocks   should ignore blocks on scan path
     * @param ignorePassable should ignore passable blocks on scan path
     * @param ignoreLiquids  should ignore liquids on scan path
     * @param entityFilter   entity predicate
     * @param blockFilter    block predicate
     * @return distant position in the direction of the entity
     */
    ImmutableVector findPosition(double range, double raySize, boolean ignoreEntity, boolean ignoreBlocks, boolean ignorePassable, boolean ignoreLiquids, Predicate<Entity> entityFilter, Predicate<Block> blockFilter);

    /**
     * Get the first block found in the entity direction
     *
     * @param range this is the maximum distance to scan
     *              <p>raySize <b>default 0</b> entity bounding boxes will be uniformly expanded (or shrinked) by this value before</p>
     *              <p>ignoreLiquid <b>default false</b> should ignore liquids on scan path</p>
     *              <p>ignorePassable <b>default true</b> should ignore passable blocks on the scan path</p>
     *              <p>filter <b>default block -> true</b> block predicate</p>
     * @return Block that was found in the direction of the entity or null
     */
    Optional<Block> findBlock(double range);

    /**
     * Get the first block found in the entity direction
     *
     * @param range   this is the maximum distance to scan
     * @param raySize entity bounding boxes will be uniformly expanded (or shrinked) by this value before
     *                <p>ignoreLiquid <b>default false</b> should ignore liquids on scan path</p>
     *                <p>ignorePassable <b>default true</b> should ignore passable blocks on the scan path</p>
     *                <p>filter <b>default block -> true</b> block predicate</p>
     * @return Block that was found in the direction of the entity or null
     */
    Optional<Block> findBlock(double range, double raySize);

    /**
     * Get the first block found in the entity direction
     *
     * @param range        this is the maximum distance to scan
     * @param raySize      entity bounding boxes will be uniformly expanded (or shrinked) by this value before
     * @param ignoreLiquid should ignore liquids on scan path
     *                     <p>ignorePassable <b>default true</b> should ignore passable blocks on the scan path</p>
     *                     <p>filter <b>default block -> true</b> block predicate</p>
     * @return Block that was found in the direction of the entity or null
     */
    Optional<Block> findBlock(double range, double raySize, boolean ignoreLiquid);

    /**
     * Get the first block found in the entity direction
     *
     * @param range          this is the maximum distance to scan
     * @param raySize        entity bounding boxes will be uniformly expanded (or shrinked) by this value before
     * @param ignoreLiquid   should ignore liquids on scan path
     * @param ignorePassable should ignore passable blocks on the scan path
     *                       <p>filter <b>default block -> true</b> block predicate</p>
     * @return Block that was found in the direction of the entity or null
     */
    Optional<Block> findBlock(double range, double raySize, boolean ignoreLiquid, boolean ignorePassable);

    /**
     * Get the first block found in the entity direction
     *
     * @param range        this is the maximum distance to scan
     *                     <p>raySize <b>default 0</b> entity bounding boxes will be uniformly expanded (or shrinked) by this value before</p>
     * @param ignoreLiquid should ignore liquids on scan path
     *                     <p>ignorePassable <b>default true</b> should ignore passable blocks on the scan path</p>
     *                     <p>filter <b>default block -> true</b> block predicate</p>
     * @return Block that was found in the direction of the entity or null
     */
    Optional<Block> findBlock(double range, boolean ignoreLiquid);

    /**
     * Get the first block found in the entity direction
     *
     * @param range          this is the maximum distance to scan
     *                       <p>raySize <b>default 0</b> entity bounding boxes will be uniformly expanded (or shrinked) by this value before</p>
     * @param ignoreLiquid   should ignore liquids on scan path
     * @param ignorePassable should ignore passable blocks on the scan path
     *                       <p>filter <b>default block -> true</b> block predicate</p>
     * @return Block that was found in the direction of the entity or null
     */
    Optional<Block> findBlock(double range, boolean ignoreLiquid, boolean ignorePassable);

    /**
     * Get the first block found in the entity direction
     *
     * @param range          this is the maximum distance to scan
     *                       <p>raySize <b>default 0</b> entity bounding boxes will be uniformly expanded (or shrinked) by this value before</p>
     * @param ignoreLiquid   should ignore liquids on scan path
     * @param ignorePassable should ignore passable blocks on the scan path
     * @param filter         block predicate
     * @return Block that was found in the direction of the entity or null
     */
    Optional<Block> findBlock(double range, boolean ignoreLiquid, boolean ignorePassable, Predicate<Block> filter);

    /**
     * Get the first block found in the entity direction
     *
     * @param range          this is the maximum distance to scan
     * @param raySize        entity bounding boxes will be uniformly expanded (or shrinked) by this value before
     * @param ignoreLiquid   should ignore liquids on scan path
     * @param ignorePassable should ignore passable blocks on the scan path
     * @param filter         block predicate
     * @return Block that was found in the direction of the entity or null
     */
    Optional<Block> findBlock(double range, double raySize, boolean ignoreLiquid, boolean ignorePassable, Predicate<Block> filter);

    /**
     * Get the first LivingEntity found in the entity direction
     *
     * @param range   this is the maximum distance to scan
     * @param raySize entity bounding boxes will be uniformly expanded (or shrinked) by this value before
     *                <p>ignoreBlock <b>default false</b> - should ignore blocks on scan path</p>
     *                <p>predicate <b>default true</b> - filter for founded entity</p>
     * @return LivingEntity that was found in the direction of the entity or null
     */
    Optional<LivingEntity> findLivingEntity(double range, double raySize);

    /**
     * Get the first LivingEntity found in the entity direction
     *
     * @param range this is the maximum distance to scan
     *              <p>raySize <b>default 0</b> - entity bounding boxes will be uniformly expanded (or shrinked) by this value before</p>
     *              <p>ignoreBlock <b>default false</b> - should ignore blocks on scan path</p>
     *              <p>predicate <b>default true</b> - filter for founded entity</p>
     * @return LivingEntity that was found in the direction of the entity or null
     */
    Optional<LivingEntity> findLivingEntity(double range);

    /**
     * Get the first LivingEntity found in the entity direction
     *
     * @param range     this is the maximum distance to scan
     * @param predicate filter for founded entity
     *                  <p>raySize <b>default 0</b> - entity bounding boxes will be uniformly expanded (or shrinked) by this value before</p>
     *                  <p>ignoreBlock <b>default false</b> - should ignore blocks on scan path</p>
     * @return LivingEntity that was found in the direction of the entity or null
     */
    Optional<LivingEntity> findLivingEntity(double range, Predicate<LivingEntity> predicate);

    /**
     * Get the first LivingEntity found in the entity direction
     *
     * @param range       this is the maximum distance to scan
     * @param raySize     entity bounding boxes will be uniformly expanded (or shrinked) by this value before
     * @param ignoreBlock should ignore blocks on scan path
     * @param predicate   filter for founded entity
     * @return LivingEntity that was found in the direction of the entity or null
     */
    Optional<LivingEntity> findLivingEntity(double range, double raySize, boolean ignoreBlock, Predicate<LivingEntity> predicate);

    /**
     * Gets information about the entity being targeted
     *
     * @param range        this is the maximum distance to scan
     * @param ignoreBlocks true to scan through blocks
     * @return entity being targeted, or null if no entity is targeted
     */
    Optional<Entity> getTargetEntity(int range, boolean ignoreBlocks);

    /**
     * Gets {@link ImmutableVector} of {@link LivingEntity#getEyeLocation()}
     *
     * @return an {@link ImmutableVector} of entity eye location
     */
    ImmutableVector getEyeLocation();

    /**
     * Gets {@link ImmutableVector} of {@link LivingEntity} direction
     *
     * @return an {@link ImmutableVector} pointing the direction of entity eye location's pitch and yaw
     */
    ImmutableVector getDirection();

    /**
     * Gets the yaw of this location, measured in degrees.
     * <ul>
     * <li>A yaw of 0 or 360 represents the positive z direction.
     * <li>A yaw of 180 represents the negative z direction.
     * <li>A yaw of 90 represents the negative x direction.
     * <li>A yaw of 270 represents the positive x direction.
     * </ul>
     * Increasing yaw values are the equivalent of turning to your
     * right-facing, increasing the scale of the next respective axis, and
     * decreasing the scale of the previous axis.
     *
     * @return the rotation's yaw
     */
    float getYaw();

    /**
     * Gets the pitch of this location, measured in degrees.
     * <ul>
     * <li>A pitch of 0 represents level forward facing.
     * <li>A pitch of 90 represents downward facing, or negative y
     *     direction.
     * <li>A pitch of -90 represents upward facing, or positive y direction.
     * </ul>
     * Increasing pitch values the equivalent of looking down.
     *
     * @return the incline's pitch
     */
    float getPitch();

    /**
     * Adds the given {@link PotionEffect} to the living entity.
     *
     * @param ability this method is called from
     * @param effect  PotionEffect to be added
     */
    void addPotionEffect(Ability ability, @NotNull PotionEffect effect);

    /**
     * Attempts to add all of the given {@link PotionEffect} to the living
     * entity.
     *
     * @param ability this method is called from
     * @param effects the effects to add
     */
    void addPotionEffects(Ability ability, @NotNull Collection<PotionEffect> effects);

    /**
     * Returns whether the living entity already has an existing effect of
     * the given {@link PotionEffectType} applied to it.
     *
     * @param type the potion type to check
     * @return whether the living entity has this potion effect active on them
     */
    boolean hasPotionEffect(@NotNull PotionEffectType type);

    /**
     * Returns the active {@link PotionEffect} of the specified type.
     * <p>
     * If the effect is not present on the entity then null will be returned.
     *
     * @param type the potion type to check
     * @return the effect active on this entity, or null if not active.
     */
    PotionEffect getPotionEffect(@NotNull PotionEffectType type);

    /**
     * Removes any effects present of the given {@link PotionEffectType}.
     *
     * @param type the potion type to remove
     */
    void removePotionEffect(@NotNull PotionEffectType type);

    /**
     * Returns all currently active {@link PotionEffect}s on the living
     * entity.
     *
     * @return a collection of {@link PotionEffect}s
     */
    Collection<PotionEffect> getPotionEffects();

    /**
     * Returns true if this entity has been marked for removal.
     *
     * @return True if it is dead.
     */
    boolean isDead();


    /**
     * Gets whether is sprinting or not.
     *
     * @return true if it is sprinting.
     */
    boolean isSprinting();

    /**
     * Gets the value of the specified permission, if set.
     * <p>
     * If a permission override is not set on this object, the default value
     * of the permission will be returned.
     *
     * @param permission Name of the permission
     * @return Value of the permission
     */
    boolean hasPermission(String permission);

    /**
     * Gets this human's current {@link GameMode}
     *
     * @return Current game mode
     */
    GameMode getGameMode();

    /**
     * Checks if this is currently online
     *
     * @return true if it is online
     */
    boolean isOnline();

    /**
     * Gets the selected main hand
     *
     * @return the main hand
     */
    MainHand getMainHand();

    /**
     * Note: The returned value includes an offset and is ideal for showing charging particles.
     *
     * @return a vector which represents the user's main hand location
     */
    ImmutableVector getMainHandLocation();

    /**
     * Gets the user's specified hand position.
     *
     * @return a vector which represents the user's specified hand location
     */
    ImmutableVector getHandLocation(@Nullable MainHand hand);
}
