package dev.ckateptb.minecraft.abilityslots.predicate;

import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.IAbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.user.AbilityUser;
import dev.ckateptb.minecraft.abilityslots.user.PlayerAbilityUser;
import dev.ckateptb.minecraft.colliders.math.ImmutableVector;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public interface RemovalConditional extends Conditional<Ability> {
    RemovalConditional IS_OFFLINE = (user, ability) -> !user.isOnline();

    RemovalConditional IS_DEAD = (user, ability) -> user.isDead();

    RemovalConditional IS_SNEAKING = (user, ability) -> user.isSneaking();

    RemovalConditional IS_NOT_SNEAKING = (user, ability) -> !user.isSneaking();

    default boolean shouldRemove(AbilityUser user, Ability ability) {
        return this.matches(user, ability);
    }

    @Override
    boolean matches(AbilityUser user, Ability ability);

    @NoArgsConstructor
    class Builder {
        private final Set<RemovalConditional> policies = new HashSet<>();

        public Builder offline() {
            policies.add(IS_OFFLINE);
            return this;
        }

        public Builder dead() {
            policies.add(IS_DEAD);
            return this;
        }

        public Builder world() {
            policies.add((user, ability) -> !Objects.equals(user.getWorld(), ability.getWorld()));
            return this;
        }

        public Builder world(World world) {
            policies.add((user, ability) -> !Objects.equals(user.getWorld(), world));
            return this;
        }

        public Builder sneaking(boolean shouldSneaking) {
            policies.add(shouldSneaking ? IS_NOT_SNEAKING : IS_SNEAKING);
            return this;
        }

        public Builder duration(long duration) {
            if (duration > 0) {
                long expire = System.currentTimeMillis() + duration;
                policies.add((user, ability) -> System.currentTimeMillis() > expire);
            }
            return this;
        }

        public Builder range(Supplier<Location> from, Supplier<Location> to, double range) {
            policies.add((user, ability) -> {
                Location fromLocation = from.get();
                Location toLocation = to.get();
                return !Objects.equals(fromLocation.getWorld(), toLocation.getWorld()) || fromLocation.distance(toLocation) > range;
            });
            return this;
        }

        public Builder range(Supplier<Location> from, Supplier<Location> to, Supplier<Double> range) {
            policies.add((user, ability) -> {
                Location fromLocation = from.get();
                Location toLocation = to.get();
                return !Objects.equals(fromLocation.getWorld(), toLocation.getWorld()) || fromLocation.distance(toLocation) > range.get();
            });
            return this;
        }

        public Builder canUse(Supplier<Location> location) {
            policies.add((user, ability) -> {
                if (user instanceof PlayerAbilityUser player) {
                    return !player.canUse(location.get());
                } else return false;
            });
            return this;
        }

        public Builder slot() {
            policies.add((user, ability) -> {
                IAbilityDeclaration<?> information = user.getSelectedAbility();
                return user.isPlayer() && (information == null || !information.getAbilityClass().equals(ability.getClass()));
            });
            return this;
        }

        public Builder slot(Class<? extends Ability> type) {
            policies.add((user, ability) -> {
                IAbilityDeclaration<?> information = user.getSelectedAbility();
                return user.isPlayer() && (information == null || !information.getAbilityClass().equals(type));
            });
            return this;
        }

        public Builder costInterval(double amount, long interval) {
            AtomicLong expiredEnergySafeTime = new AtomicLong(System.currentTimeMillis() + interval);
            policies.add((user, ability) -> {
                if (System.currentTimeMillis() > expiredEnergySafeTime.get()) {
                    expiredEnergySafeTime.set(System.currentTimeMillis() + interval);
                    return !user.removeEnergy(amount);
                }
                return false;
            });
            return this;
        }

        public Builder water(boolean shouldStayInWater) {
            policies.add((user, ability) -> {
                if (user instanceof PlayerAbilityUser player) {
                    boolean inWater = player.getLocation().getBlock().getType() == Material.WATER;
                    return inWater == !shouldStayInWater;
                } else return false;
            });
            return this;
        }

        public Builder water(ImmutableVector location, boolean shouldBeWater) {
            policies.add((user, ability) -> {
                boolean inWater = location.toLocation(user.getWorld()).getBlock().getType() == Material.WATER;
                return inWater == !shouldBeWater;
            });
            return this;
        }

        public Builder water(Location location, boolean shouldBeWater) {
            policies.add((user, ability) -> {
                boolean inWater = location.getBlock().getType() == Material.WATER;
                return inWater == !shouldBeWater;
            });
            return this;
        }

        public Builder liquid(boolean shouldStayInLiquid) {
            policies.add((user, ability) -> {
                if (user instanceof PlayerAbilityUser player) {
                    boolean inLiquid = player.getLocation().getBlock().isLiquid();
                    return inLiquid == !shouldStayInLiquid;
                } else return false;
            });
            return this;
        }

        public Builder liquid(ImmutableVector location, boolean shouldBeLiquid) {
            policies.add((user, ability) -> {
                boolean inLiquid = location.toLocation(user.getWorld()).getBlock().isLiquid();
                return inLiquid == !shouldBeLiquid;
            });
            return this;
        }

        public Builder liquid(Location location, boolean shouldBeLiquid) {
            policies.add((user, ability) -> {
                boolean inLiquid = location.getBlock().isLiquid();
                return inLiquid == !shouldBeLiquid;
            });
            return this;
        }

        public Builder passable(boolean shouldStayInPassable) {
            policies.add((user, ability) -> {
                if (user instanceof PlayerAbilityUser player) {
                    boolean passable = player.getLocation().getBlock().isPassable();
                    return passable == !shouldStayInPassable;
                } else return false;
            });
            return this;
        }

        public Builder passable(ImmutableVector location, boolean shouldBePassable) {
            policies.add((user, ability) -> {
                boolean passable = location.toLocation(user.getWorld()).getBlock().isPassable();
                return passable == !shouldBePassable;
            });
            return this;
        }

        public Builder passable(Location location, boolean shouldBePassable) {
            policies.add((user, ability) -> {
                boolean passable = location.getBlock().isPassable();
                return passable == !shouldBePassable;
            });
            return this;
        }

        public Builder passableNoLiquid(boolean shouldStayInPassable) {
            policies.add((user, ability) -> {
                if (user instanceof PlayerAbilityUser player) {
                    Block block = player.getLocation().getBlock();
                    boolean passable = block.isPassable() && !block.isLiquid();
                    return passable == !shouldStayInPassable;
                } else return false;
            });
            return this;
        }

        public Builder passableNoLiquid(ImmutableVector location, boolean shouldBePassable) {
            policies.add((user, ability) -> {
                Block block = location.toLocation(user.getWorld()).getBlock();
                boolean passable = block.isPassable() && !block.isLiquid();
                return passable == !shouldBePassable;
            });
            return this;
        }

        public Builder passableNoLiquid(Location location, boolean shouldBePassable) {
            policies.add((user, ability) -> {
                Block block = location.getBlock();
                boolean passable = block.isPassable() && !block.isLiquid();
                return passable == !shouldBePassable;
            });
            return this;
        }

        public Builder custom(RemovalConditional conditional) {
            policies.add(conditional);
            return this;
        }

        public Builder remove(RemovalConditional conditional) {
            policies.remove(conditional);
            return this;
        }

        public RemovalConditional build() {
            return (user, ability) -> policies.stream().anyMatch(police -> police.shouldRemove(user, ability));
        }
    }
}
