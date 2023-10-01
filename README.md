<p align="center">
<h3 align="center">AbilitySlots</h3>

------

<p align="center">
Have you always dreamed of having superpowers? Now it's possible! A fully asynchronous plugin framework for creating and using abilities.
</p>

<p align="center">
<img alt="License" src="https://img.shields.io/github/license/CKATEPTb-minecraft/AbilitySlots">
<a href="https://docs.gradle.org/7.5/release-notes.html"><img src="https://img.shields.io/badge/Gradle-7.5-brightgreen.svg?colorB=469C00&logo=gradle"></a>
<a href="https://discord.gg/P7FaqjcATp" target="_blank"><img alt="Discord" src="https://img.shields.io/discord/925686623222505482?label=discord"></a>
</p>

------

# Versioning

We use [Semantic Versioning 2.0.0](https://semver.org/spec/v2.0.0.html) to manage our releases.

# Features

- [X] Fully async
- [X] Colliders between abilities
- [X] Protection from poor developers
- [X] Energy & Cooldowns
- [X] Fully configurable
- [X] Ability board
- [X] Temporary block system
- [X] Respect protection plugins
- [ ] Ability combinations (implemented but untested yet)
- [X] Passive abilities
- [X] Database support
- [X] PAPI Integration
- [X] Addon's loader
- [X] Easy to use annotation based API
- [ ] Per player ability config
- [ ] Multiversion (Will never be implemented due to BukkitAPI delegation)

# How To Install Plugin

* Download plugin [link (will add later)]()
* Download dependencies [link (will add later)]()
* Put plugin and dependencies to your plugins folder

# How To Install Addons

* Download addon [link (will add later)]()
* Put addon into `AbilitySlots/addons` folder

# How To Create Addon

* Import dependencies
```kotlin
repositories {
    maven("https://repo.animecraft.fun/repository/maven-snapshots/")
}

dependencies {
    compileOnly("dev.ckateptb.minecraft:AbilitySlots:+")
    compileOnly("dev.ckateptb.minecraft:Nicotine:+")
    compileOnly("dev.ckateptb.minecraft:Atom:+")
    compileOnly("dev.ckateptb.minecraft:Colliders:+")
}
```
* To create abilities you must use a category, so.. Create ability category
```java
import dev.ckateptb.minecraft.abilityslots.ability.category.AbilityCategory;
import dev.ckateptb.minecraft.abilityslots.ability.category.annotation.CategoryDeclaration;
import dev.ckateptb.minecraft.abilityslots.config.annotation.Configurable;

@CategoryDeclaration(
        name = "example", // System category name
        displayName = "&7Air", // The name that will be displayed to players
        abilityPrefix = "&7", // The category prefix that will be used to display everything associated with it
        description = "" // Description of the category that will be displayed to players
)
public class ExampleCategory extends AbilityCategory {
    // All information about the category will be placed in the configuration file!
    // If you want to expand the configuration options, use a static variable with the @Configurable annotation
    @Configurable(
            name = "(optional) You can change the section name inside the configuration file",
            comment = "(optional) You can also leave comments"
    )
    private static String particle = Particle.SPELL.name(); // The initial value is the default value
    
    // It often happens that you need to move the methods that are used in abilities somewhere into a single place. You can use category for this.
    // Just make this method static
    public static void example(Object args) {
        // ...
    }
}

// That's all, the category is created and no further action is required
```
* Create ability
```java
import dev.ckateptb.minecraft.abilityslots.ability.Ability;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.generated.annotation.AbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.enums.AbilityActivateStatus;
import dev.ckateptb.minecraft.abilityslots.ability.enums.AbilityTickStatus;
import dev.ckateptb.minecraft.abilityslots.ability.enums.ActivationMethod;
import dev.ckateptb.minecraft.abilityslots.config.annotation.Configurable;

@AbilityDeclaration(
        author = "CKATEPTb", // Indicate yourself
        name = "example",  // System category name
        displayName = "ExampleDisplayName", // The name that will be displayed to players
        category = ExampleCategory.class, // Category to which the ability belongs
        activators = {ActivationMethod.SNEAK, ActivationMethod.LEFT_CLICK}, // Ways to activate the ability
        description = "Ability description", // Description to help players understand the purpose of the ability
        instruction = "Ability instruction" // Instructions to help players understand how to use the ability
)
public class ExampleAbility extends Ability {
    // All information about the ability will be placed in the configuration file!
    // If you want to expand the configuration options, use a static variable with the @Configurable annotation
    @Configurable(
            name = "(optional) You can change the section name inside the configuration file",
            comment = "(optional) You can also leave comments"
    )
    private static long cooldown = 6000; // The initial value is the default value
    @Configurable
    private static long energyCost = 10;


    // Be sure to declare an empty constructor
    public ExampleAbility() {
        // any case do nothing
    }

    // When a player performs one of the activation methods specified in @AbilityDeclaration
    //   this method calls, in response you must indicate whether to activate the ability
    @Override
    public AbilityActivateStatus activate(ActivationMethod activationMethod) {
        if(activationMethod == ActivationMethod.LEFT_CLICK && this.user.removeEnergy(energyCost)) {
            return AbilityActivateStatus.ACTIVATE;
        }
        if(activationMethod == ActivationMethod.SNEAK) {
            this.user.setCooldown(this.declaration, cooldown);
            return AbilityActivateStatus.ACTIVATE;
        }
        return AbilityActivateStatus.IGNORE;
    }


    // Called every server tick if ability is activated
    //    in response, you must indicate whether the ability should be destroyed
    @Override
    public AbilityTickStatus tick() {
        // Actually, here you determine the behavior of the ability for each game tick
        return ExampleCaterory.doSth() ? AbilityTickStatus.DESTROY : AbilityTickStatus.CONTINUE;
    }

    // Triggers when the ability is destroyed
    @Override
    public void destroy(Void unused) {
        // ...
    }
}
```
* Create an ability that collides with other abilities
```java
import dev.ckateptb.minecraft.abilityslots.ability.collision.CollidableAbility;
import dev.ckateptb.minecraft.abilityslots.ability.collision.declaration.generated.annotation.CollisionDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.collision.enums.AbilityCollisionResult;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.generated.annotation.AbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.enums.AbilityActivateStatus;
import dev.ckateptb.minecraft.abilityslots.ability.enums.AbilityTickStatus;
import dev.ckateptb.minecraft.abilityslots.ability.enums.ActivationMethod;
import dev.ckateptb.minecraft.abilityslots.config.annotation.Configurable;
import dev.ckateptb.minecraft.colliders.Collider;
import dev.ckateptb.minecraft.colliders.Colliders;

import java.util.Collection;
import java.util.Collections;

@AbilityDeclaration(
        author = "CKATEPTb", // Indicate yourself
        name = "example",  // System category name
        displayName = "ExampleDisplayName", // The name that will be displayed to players
        category = ExampleCategory.class, // Category to which the ability belongs
        activators = {ActivationMethod.SNEAK, ActivationMethod.LEFT_CLICK}, // Ways to activate the ability
        description = "Ability description", // Description to help players understand the purpose of the ability
        instruction = "Ability instruction" // Instructions to help players understand how to use the ability
)
@CollisionDeclaration(destructible = {ExampleAbility.class, OtherExampleAbylity.class}) // A list of abilities that must be destroyed when colliding with the current ability.
public class ExampleAbility extends CollidableAbility {
    // All information about the ability will be placed in the configuration file!
    // If you want to expand the configuration options, use a static variable with the @Configurable annotation
    @Configurable(
            name = "(optional) You can change the section name inside the configuration file",
            comment = "(optional) You can also leave comments"
    )
    private static long cooldown = 6000; // The initial value is the default value
    @Configurable
    private static long energyCost = 10;

    private Collider collider;


    // Be sure to declare an empty constructor
    public ExampleAbility() {
        // any case do nothing
    }

    // When a player performs one of the activation methods specified in @AbilityDeclaration
    //   this method calls, in response you must indicate whether to activate the ability
    @Override
    public AbilityActivateStatus activate(ActivationMethod activationMethod) {
        if(activationMethod == ActivationMethod.LEFT_CLICK && this.user.removeEnergy(energyCost)) {
            return AbilityActivateStatus.ACTIVATE;
        }
        if(activationMethod == ActivationMethod.SNEAK) {
            this.user.setCooldown(this.declaration, cooldown);
            return AbilityActivateStatus.ACTIVATE;
        }
        return AbilityActivateStatus.IGNORE;
    }


    // Called every server tick if ability is activated
    //    in response, you must indicate whether the ability should be destroyed
    @Override
    public AbilityTickStatus tick() {
        // Actually, here you determine the behavior of the ability for each game tick
        this.collider = Colliders.sphere(center, radius);
        return ExampleCaterory.doSth() ? AbilityTickStatus.DESTROY : AbilityTickStatus.CONTINUE;
    }

    // Triggers when the ability is destroyed
    @Override
    public void destroy(Void unused) {
        // ...
    }

    // List of colliders that should interact with other abilities
    @Override
    public Collection<Collider> getColliders() {
        return Collections.singleton(this.collider);
    }

    // This method is called when the collider of the current ability collides with the collider of another ability.
    // The result of the processing must be in favor of the other ability.
    //    in response, you must indicate whether the ability should be destroyed
    @Override
    public AbilityCollisionResult onCollide(Collider collider, CollidableAbility otherAbility, Collider otherAbilityCollider) {
        return shouldDestroy ? AbilityCollisionResult.DESTROY : AbilityCollisionResult.CONTINUE;
    }
}
```
* Create sequence ability
```java
import dev.ckateptb.minecraft.abilityslots.ability.collision.CollidableAbility;
import dev.ckateptb.minecraft.abilityslots.ability.collision.declaration.generated.annotation.CollisionDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.collision.enums.AbilityCollisionResult;
import dev.ckateptb.minecraft.abilityslots.ability.declaration.generated.annotation.AbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.ability.enums.AbilityActivateStatus;
import dev.ckateptb.minecraft.abilityslots.ability.enums.AbilityTickStatus;
import dev.ckateptb.minecraft.abilityslots.ability.enums.ActivationMethod;
import dev.ckateptb.minecraft.abilityslots.ability.sequence.annotation.AbilityAction;
import dev.ckateptb.minecraft.abilityslots.ability.sequence.annotation.Sequence;
import dev.ckateptb.minecraft.abilityslots.config.annotation.Configurable;
import dev.ckateptb.minecraft.colliders.Collider;
import dev.ckateptb.minecraft.colliders.Colliders;

import java.util.Collection;
import java.util.Collections;

@AbilityDeclaration(
        author = "CKATEPTb", // Indicate yourself
        name = "example",  // System category name
        displayName = "ExampleDisplayName", // The name that will be displayed to players
        category = AirCategory.class, // Category to which the ability belongs
        activators = {ActivationMethod.SEQUENCE}, // Ways to activate the ability
        description = "Ability description", // Description to help players understand the purpose of the ability
        instruction = "Ability instruction" // Instructions to help players understand how to use the ability
)
@CollisionDeclaration(destructible = {ExampleAbility.class})
// A list of abilities that must be destroyed when colliding with the current ability.
@Sequence(value = { // List of actions for activation ActivationMethod.SEQUENCE
        @AbilityAction(ability = ExampleAbility.class, action = ActivationMethod.LEFT_CLICK),
        @AbilityAction(ability = OtherAbility.class, action = ActivationMethod.SNEAK),
        @AbilityAction(ability = OtherAbility.class, action = ActivationMethod.SNEAK_RELEASE)
})
public class ExampleAbility extends CollidableAbility {
    // All information about the ability will be placed in the configuration file!
    // If you want to expand the configuration options, use a static variable with the @Configurable annotation
    @Configurable(
            name = "(optional) You can change the section name inside the configuration file",
            comment = "(optional) You can also leave comments"
    )
    private static long cooldown = 6000; // The initial value is the default value
    @Configurable
    private static long energyCost = 10;

    private Collider collider;


    // Be sure to declare an empty constructor
    public ExampleAbility() {
        // any case do nothing
    }

    // When a player performs one of the activation methods specified in @AbilityDeclaration
    //   this method calls, in response you must indicate whether to activate the ability
    @Override
    public AbilityActivateStatus activate(ActivationMethod activationMethod) {
        if (activationMethod == ActivationMethod.LEFT_CLICK && this.user.removeEnergy(energyCost)) {
            return AbilityActivateStatus.ACTIVATE;
        }
        if (activationMethod == ActivationMethod.SNEAK) {
            this.user.setCooldown(this.declaration, cooldown);
            return AbilityActivateStatus.ACTIVATE;
        }
        return AbilityActivateStatus.IGNORE;
    }


    // Called every server tick if ability is activated
    //    in response, you must indicate whether the ability should be destroyed
    @Override
    public AbilityTickStatus tick() {
        // Actually, here you determine the behavior of the ability for each game tick
        this.collider = Colliders.sphere(center, radius);
        return ExampleCaterory.doSth() ? AbilityTickStatus.DESTROY : AbilityTickStatus.CONTINUE;
    }

    // Triggers when the ability is destroyed
    @Override
    public void destroy(Void unused) {
        // ...
    }

    // List of colliders that should interact with other abilities
    @Override
    public Collection<Collider> getColliders() {
        return Collections.singleton(this.collider);
    }

    // This method is called when the collider of the current ability collides with the collider of another ability.
    // The result of the processing must be in favor of the other ability.
    //    in response, you must indicate whether the ability should be destroyed
    @Override
    public AbilityCollisionResult onCollide(Collider collider, CollidableAbility otherAbility, Collider otherAbilityCollider) {
        return shouldDestroy ? AbilityCollisionResult.DESTROY : AbilityCollisionResult.CONTINUE;
    }
}
```
* The best way to understand how to create abilities is to look at examples [link (will add later)]().
