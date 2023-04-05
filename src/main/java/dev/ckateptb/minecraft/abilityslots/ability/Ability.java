package dev.ckateptb.minecraft.abilityslots.ability;

import dev.ckateptb.minecraft.abilityslots.ability.enums.ActivationMethod;
import dev.ckateptb.minecraft.abilityslots.ability.enums.ActivationResult;
import dev.ckateptb.minecraft.abilityslots.ability.enums.UpdateResult;
import dev.ckateptb.minecraft.abilityslots.collision.ColliderHolder;
import dev.ckateptb.minecraft.abilityslots.declaration.AbilityDeclaration;
import dev.ckateptb.minecraft.abilityslots.process.AbilityInstanceService;
import dev.ckateptb.minecraft.abilityslots.user.AbilityUser;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;


public abstract class Ability implements ColliderHolder {
    protected final AbilityService abilityService;
    protected final AbilityInstanceService instanceService;
    @Getter
    protected final AbilityDeclaration declaration;
    @Getter
    protected AbilityUser user;
    protected LivingEntity livingEntity;
    @Getter
    protected World world;

    public Ability(AbilityService abilityService, AbilityInstanceService instanceService, AbilityDeclaration declaration) {
        this.abilityService = abilityService;
        this.instanceService = instanceService;
        this.declaration = declaration;
    }

    public abstract ActivationResult activate(ActivationMethod method);

    public abstract UpdateResult update();

    public abstract void destroy();

    public void setUser(AbilityUser user) {
        this.user = user;
        this.livingEntity = user.getHandle();
        this.world = livingEntity.getWorld();
    }
}
