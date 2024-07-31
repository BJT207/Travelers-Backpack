package com.tiviacz.travelersbackpack.compat.trinkets;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketEnums;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class TravelersBackpackTrinket implements Trinket
{
    @Override
    public boolean canEquip(ItemStack stack, SlotReference slot, LivingEntity entity)
    {
        return TravelersBackpackConfig.getConfig().backpackSettings.trinketsIntegration && !slot.inventory().containsAny(p -> p.getItem() instanceof TravelersBackpackItem);
    }

    @Override
    public TrinketEnums.DropRule getDropRule(ItemStack stack, SlotReference slot, LivingEntity entity)
    {
        return TrinketEnums.DropRule.DEFAULT;
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity)
    {
        if(!TravelersBackpackConfig.getConfig().backpackSettings.trinketsIntegration) return;

        if(entity instanceof PlayerEntity player)
        {
            TravelersBackpackInventory.abilityTick(player);
        }
    }
}