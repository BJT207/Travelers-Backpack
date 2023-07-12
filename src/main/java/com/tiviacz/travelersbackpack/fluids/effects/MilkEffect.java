package com.tiviacz.travelersbackpack.fluids.effects;

import com.tiviacz.travelersbackpack.fluids.EffectFluid;
import com.tiviacz.travelersbackpack.init.ModFluids;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class MilkEffect extends EffectFluid
{
    public MilkEffect()
    {
        super("travelersbackpack:milk", ModFluids.MILK_STILL.getStill(), Reference.BUCKET);
    }

    @Override
    public void affectDrinker(StorageView<FluidVariant> fluidStack, World world, Entity entity)
    {
        if(entity instanceof PlayerEntity player)
        {
            player.clearStatusEffects();
        }
    }

    @Override
    public boolean canExecuteEffect(StorageView<FluidVariant> stack, World world, Entity entity)
    {
        return stack.getAmount() >= amountRequired;
    }
}