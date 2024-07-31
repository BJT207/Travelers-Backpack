package com.tiviacz.travelersbackpack.client.renderer;

import com.tiviacz.travelersbackpack.components.FluidTanks;
import com.tiviacz.travelersbackpack.init.ModComponentTypes;
import com.tiviacz.travelersbackpack.inventory.FluidTank;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;

public class RenderData
{
    private final ItemStack stack;
    private final FluidTank leftTank = new FluidTank(81000); // createFluidTank();

    private final FluidTank rightTank = new FluidTank(81000); //createFluidTank();

    public RenderData(ItemStack stack, boolean loadData)
    {
        this.stack = stack;

        if(loadData)
        {
            this.loadDataFromStack();
        }
    }

    public FluidTank getLeftTank()
    {
        return this.leftTank;
    }

    public FluidTank getRightTank()
    {
        return this.rightTank;
    }

    public ItemStack getItemStack()
    {
        return this.stack;
    }

    public int getSleepingBagColor()
    {
        return stack.getOrDefault(ModComponentTypes.SLEEPING_BAG_COLOR, DyeColor.RED.getId());
       /* if(this.stack.getOrCreateNbt().contains(ITravelersBackpackInventory.SLEEPING_BAG_COLOR))
        {
            return this.stack.getOrCreateNbt().getInt(ITravelersBackpackInventory.SLEEPING_BAG_COLOR);
        }
        return DyeColor.RED.getId(); */
    }

    public void loadDataFromStack()
    {
        if(this.stack.contains(ModComponentTypes.FLUID_TANKS))
        {
            loadTanks();
        }
       /*+ if(!stack.isEmpty() && stack.hasNbt())
        {
            loadTanks(stack.getOrCreateNbt());
        } */
    }

    public void loadTanks()
    {
        FluidTanks tanks = stack.get(ModComponentTypes.FLUID_TANKS);

        this.leftTank.setCapacity(tanks.capacity());
        this.leftTank.setFluidVariant(tanks.leftTank().fluidVariant(), tanks.leftTank().amount());

        this.rightTank.setCapacity(tanks.capacity());
        this.rightTank.setFluidVariant(tanks.rightTank().fluidVariant(), tanks.rightTank().amount());

        //this.leftTank.readNbt(compound.getCompound(ITravelersBackpackInventory.LEFT_TANK));
        //this.rightTank.readNbt(compound.getCompound(ITravelersBackpackInventory.RIGHT_TANK));
    }

 /*   public FluidTank createFluidTank()
    {
        return new FluidTank(Tiers.LEATHER.getTankCapacity())
        {
            @Override
            public FluidTank readNbt(NbtCompound nbt)
            {
                setCapacity(nbt.contains("capacity") ? nbt.getLong("capacity") : Tiers.of(RenderData.this.stack.getOrCreateNbt().getInt(ITravelersBackpackInventory.TIER)).getTankCapacity());
                this.variant = FluidVariantImpl.fromNbt(nbt.getCompound("variant"));
                this.amount = nbt.getLong("amount");
                return this;
            }
        };
    } */
}