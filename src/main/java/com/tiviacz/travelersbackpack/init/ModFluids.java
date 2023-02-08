package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.fluids.MilkFluid;
import com.tiviacz.travelersbackpack.fluids.PotionFluid;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModFluids
{
    public static final FlowableFluid POTION_STILL = register("potion_still", new PotionFluid.Still());
    public static final FlowableFluid POTION_FLOWING = register("potion_flowing", new PotionFluid.Flowing());
    public static final FlowableFluid MILK_STILL = register("milk_still", new MilkFluid.Still());
    public static final FlowableFluid MILK_FLOWING = register("milk_flowing", new MilkFluid.Flowing());

    private static FlowableFluid register(String name, FlowableFluid flowableFluid)
    {
        return Registry.register(Registry.FLUID, new Identifier(TravelersBackpack.MODID, name), flowableFluid);
    }
}