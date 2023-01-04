package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackItemContainer;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackTileContainer;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SSlotPacket
{
    private final byte screenID;
    private final boolean isActive;
    private final int[] selectedSlots;

    public SSlotPacket(byte screenID, boolean isActive, int[] selectedSlots)
    {
        this.screenID = screenID;
        this.isActive = isActive;
        this.selectedSlots = selectedSlots;
    }

    public static SSlotPacket decode(final PacketBuffer buffer)
    {
        final byte screenID = buffer.readByte();
        final boolean isActive = buffer.readBoolean();
        final int[] selectedSlots = buffer.readVarIntArray();

        return new SSlotPacket(screenID, isActive, selectedSlots);
    }

    public static void encode(final SSlotPacket message, final PacketBuffer buffer)
    {
        buffer.writeByte(message.screenID);
        buffer.writeBoolean(message.isActive);
        buffer.writeVarIntArray(message.selectedSlots);
    }

    public static void handle(final SSlotPacket message, final Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
            final ServerPlayerEntity serverPlayerEntity = ctx.get().getSender();

            if(serverPlayerEntity != null)
            {
                if(message.screenID == Reference.WEARABLE_SCREEN_ID)
                {
                    SlotManager manager = CapabilityUtils.getBackpackInv(serverPlayerEntity).getSlotManager();
                    manager.setSelectorActive(SlotManager.UNSORTABLE, message.isActive);
                    manager.setUnsortableSlots(message.selectedSlots, true);
                    manager.setSelectorActive(SlotManager.UNSORTABLE, !message.isActive);
                }
                if(message.screenID == Reference.ITEM_SCREEN_ID)
                {
                    SlotManager manager = ((TravelersBackpackItemContainer)serverPlayerEntity.containerMenu).inventory.getSlotManager();
                    manager.setSelectorActive(SlotManager.UNSORTABLE, message.isActive);
                    manager.setUnsortableSlots(message.selectedSlots, true);
                    manager.setSelectorActive(SlotManager.UNSORTABLE, !message.isActive);
                }
                if(message.screenID == Reference.TILE_SCREEN_ID)
                {
                    SlotManager manager = ((TravelersBackpackTileContainer)serverPlayerEntity.containerMenu).inventory.getSlotManager();
                    manager.setSelectorActive(SlotManager.UNSORTABLE, message.isActive);
                    manager.setUnsortableSlots(message.selectedSlots, true);
                    manager.setSelectorActive(SlotManager.UNSORTABLE, !message.isActive);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}