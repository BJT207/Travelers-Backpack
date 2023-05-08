package com.tiviacz.travelersbackpack.inventory.container;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.init.ModContainerTypes;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.container.slot.DisabledSlot;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

import java.util.Objects;

public class TravelersBackpackItemContainer extends TravelersBackpackBaseContainer
{
    public TravelersBackpackItemContainer(int windowID, PlayerInventory playerInventory, PacketBuffer data)
    {
        this(windowID, playerInventory, createInventory(playerInventory, data));
    }

    public TravelersBackpackItemContainer(int windowID, PlayerInventory playerInventory, ITravelersBackpackInventory inventory)
    {
        super(ModContainerTypes.TRAVELERS_BACKPACK_ITEM.get(), windowID, playerInventory, inventory);
    }

    private static TravelersBackpackInventory createInventory(final PlayerInventory playerInventory, final PacketBuffer data)
    {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");

        final ItemStack stack; //Get ItemStack from hand or capability to avoid sending a lot of information by packetBuffer
        final byte screenID = data.readByte();

        if(screenID == Reference.ITEM_SCREEN_ID)
        {
            stack = playerInventory.player.getItemBySlot(EquipmentSlotType.MAINHAND);
        }
        else
        {
            if(data.writerIndex() == 5)
            {
                final int entityId = data.readInt();
                stack = CapabilityUtils.getWearingBackpack((PlayerEntity)playerInventory.player.level.getEntity(entityId));

                if(stack.getItem() instanceof TravelersBackpackItem)
                {
                    return CapabilityUtils.getBackpackInv((PlayerEntity)playerInventory.player.level.getEntity(entityId));
                }
            }
            else
            {
                stack = CapabilityUtils.getWearingBackpack(playerInventory.player);
            }
        }
        if(stack.getItem() instanceof TravelersBackpackItem)
        {
            if(screenID == Reference.WEARABLE_SCREEN_ID)
            {
                return CapabilityUtils.getBackpackInv(playerInventory.player);
            }
            else if(screenID == Reference.ITEM_SCREEN_ID)
            {
                return new TravelersBackpackInventory(stack, playerInventory.player, screenID);
            }
        }
        throw new IllegalStateException("ItemStack is not correct! " + stack);
    }

    @Override
    public ItemStack clicked(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player)
    {
        if(clickTypeIn == ClickType.SWAP)
        {
            final ItemStack stack = player.inventory.getItem(dragType);
            final ItemStack currentItem = player.inventory.getSelected();

            if(!currentItem.isEmpty() && stack == currentItem)
            {
                return ItemStack.EMPTY;
            }
        }
        return super.clicked(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public void addPlayerInventoryAndHotbar(PlayerInventory playerInv, int currentItemIndex)
    {
        for(int y = 0; y < 3; y++)
        {
            for(int x = 0; x < 9; x++)
            {
                this.addSlot(new Slot(playerInv, x + y * 9 + 9, 44 + x*18, (71 + this.inventory.getTier().getMenuSlotPlacementFactor()) + y*18));
            }
        }

        for(int x = 0; x < 9; x++)
        {
            if(x == currentItemIndex && this.inventory.getScreenID() == Reference.ITEM_SCREEN_ID)
            {
                this.addSlot(new DisabledSlot(playerInv, x, 44 + x*18, 129 + this.inventory.getTier().getMenuSlotPlacementFactor()));
            }
            else
            {
                this.addSlot(new Slot(playerInv, x, 44 + x*18, 129 + this.inventory.getTier().getMenuSlotPlacementFactor()));
            }
        }
    }
}