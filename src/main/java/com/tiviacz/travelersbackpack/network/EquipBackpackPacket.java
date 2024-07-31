package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public record EquipBackpackPacket(boolean equip) implements CustomPayload
{
    public static final CustomPayload.Id<EquipBackpackPacket> PACKET_ID = new CustomPayload.Id<>(Identifier.of(TravelersBackpack.MODID, "equip_backpack"));
    public static final PacketCodec<RegistryByteBuf, EquipBackpackPacket> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.BOOL, EquipBackpackPacket::equip, EquipBackpackPacket::new);

    public static void apply(EquipBackpackPacket message, ServerPlayNetworking.Context context)
    {
        context.player().getServer().execute(() ->
        {
            if(context.player() != null)
            {
                if(message.equip())
                {
                    if(!ComponentUtils.isWearingBackpack(context.player()))
                    {
                        ServerActions.equipBackpack(context.player());
                    }
                    else
                    {
                        context.player().onHandledScreenClosed();
                        context.player().sendMessage(Text.translatable(Reference.OTHER_BACKPACK), false);
                    }
                }
                else
                {
                    if(ComponentUtils.isWearingBackpack(context.player()))
                    {
                        ServerActions.unequipBackpack(context.player());
                    }
                    else
                    {
                        context.player().onHandledScreenClosed();
                        context.player().sendMessage(Text.translatable(Reference.NO_BACKPACK), false);
                    }
                }
            }
        });
    }

    @Override
    public Id<? extends CustomPayload> getId()
    {
        return PACKET_ID;
    }
}