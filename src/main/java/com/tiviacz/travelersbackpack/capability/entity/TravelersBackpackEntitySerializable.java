package com.tiviacz.travelersbackpack.capability.entity;

import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.network.ClientboundSyncAttachmentPacket;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.UnknownNullability;

public class TravelersBackpackEntitySerializable implements INBTSerializable<CompoundTag>, IEntityTravelersBackpack
{
    private ItemStack wearable = new ItemStack(Items.AIR, 0);
    private final LivingEntity livingEntity;

    public TravelersBackpackEntitySerializable(final IAttachmentHolder holder)
    {
        this.livingEntity = (LivingEntity)holder;
    }

    @Override
    public boolean hasWearable()
    {
        return !this.wearable.isEmpty();
    }

    @Override
    public ItemStack getWearable()
    {
        return this.wearable;
    }

    @Override
    public void setWearable(ItemStack stack)
    {
        this.wearable = stack;
    }

    @Override
    public void removeWearable()
    {
        this.wearable = new ItemStack(Items.AIR, 0);
    }

    @Override
    public void synchronise()
    {
        if(livingEntity != null && !livingEntity.level().isClientSide)
        {
            AttachmentUtils.getEntityAttachment(livingEntity).ifPresent(data -> PacketDistributor.sendToPlayersTrackingEntity(livingEntity, new ClientboundSyncAttachmentPacket(livingEntity.getId(), false, this.wearable)));
        }
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider)
    {
        CompoundTag compound = new CompoundTag();

        if(hasWearable())
        {
            ItemStack wearable = getWearable();
            wearable.save(provider, compound);
        }
        return compound;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt)
    {
        ItemStack wearable = ItemStack.parseOptional(provider, nbt);
        setWearable(wearable);
    }
}