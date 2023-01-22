package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity
{
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world)
    {
        super(entityType, world);
    }

    /**
     * Ability removal for attribute modifiers
     */
    private static boolean checkAbilitiesForRemoval = true;

    @Inject(at = @At(value = "TAIL"), method = "tick")
    private void abilityTick(CallbackInfo info)
    {
        if(this instanceof Object)
        {
            if((Object) this instanceof PlayerEntity player)
            {
                if(TravelersBackpackConfig.enableBackpackAbilities && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, ComponentUtils.getWearingBackpack(player)))
                {
                    TravelersBackpackInventory.abilityTick(player);
                    if(!checkAbilitiesForRemoval && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_REMOVAL_LIST, ComponentUtils.getWearingBackpack(player))) checkAbilitiesForRemoval = true;
                }

                if(checkAbilitiesForRemoval && !player.world.isClient && (!ComponentUtils.isWearingBackpack(player) || !TravelersBackpackConfig.enableBackpackAbilities))
                {
                    BackpackAbilities.ABILITIES.armorAbilityRemovals(player);
                    checkAbilitiesForRemoval = false;
                }
            }
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "attack")
    private void attack(Entity target, CallbackInfo ci)
    {
        if(TravelersBackpackConfig.enableBackpackAbilities)
        {
            if(this instanceof Object)
            {
                if((Object) this instanceof PlayerEntity player)
                {
                    BackpackAbilities.beeAbility(player, target);
                }
            }
        }
    }
}