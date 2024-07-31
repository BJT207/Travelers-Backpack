package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundSpecialActionPacket(byte screenID, byte typeOfAction, double scrollDelta) implements CustomPacketPayload
{
    public static final Type<ServerboundSpecialActionPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "special_action"));

    public static final StreamCodec<FriendlyByteBuf, ServerboundSpecialActionPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE, ServerboundSpecialActionPacket::screenID,
            ByteBufCodecs.BYTE, ServerboundSpecialActionPacket::typeOfAction,
            ByteBufCodecs.DOUBLE, ServerboundSpecialActionPacket::scrollDelta,
            ServerboundSpecialActionPacket::new
    );

    public static void handle(final ServerboundSpecialActionPacket message, IPayloadContext ctx)
    {
        ctx.enqueueWork(() ->
        {
            Player player = ctx.player();

            if(player instanceof ServerPlayer serverPlayer)
            {
                if(message.typeOfAction == Reference.SWAP_TOOL)
                {
                    ServerActions.swapTool(serverPlayer, message.scrollDelta);
                }

                else if(message.typeOfAction == Reference.SWITCH_HOSE_MODE)
                {
                    ServerActions.switchHoseMode(serverPlayer, message.scrollDelta);
                }

                else if(message.typeOfAction == Reference.TOGGLE_HOSE_TANK)
                {
                    ServerActions.toggleHoseTank(serverPlayer);
                }

                else if(message.typeOfAction == Reference.EMPTY_TANK)
                {
                    ServerActions.emptyTank(message.scrollDelta, serverPlayer, serverPlayer.serverLevel(), message.screenID);
                }

                else if(message.typeOfAction == Reference.OPEN_SCREEN)
                {
                    if(AttachmentUtils.isWearingBackpack(serverPlayer))
                    {
                        TravelersBackpackContainer.openGUI(serverPlayer, AttachmentUtils.getWearingBackpack(serverPlayer), Reference.WEARABLE_SCREEN_ID);
                    }
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}