package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackBlockEntityScreenHandler;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackItemScreenHandler;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.message.MessageType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ModNetwork
{
    public static final Identifier EQUIP_BACKPACK_ID = new Identifier(TravelersBackpack.MODID, "equip_backpack");
    public static final Identifier UNEQUIP_BACKPACK_ID = new Identifier(TravelersBackpack.MODID, "unequip_backpack");
    public static final Identifier OPEN_SCREEN_ID = new Identifier(TravelersBackpack.MODID, "open_screen");
    public static final Identifier DEPLOY_SLEEPING_BAG_ID = new Identifier(TravelersBackpack.MODID, "deploy_sleeping_bag");
    public static final Identifier SPECIAL_ACTION_ID = new Identifier(TravelersBackpack.MODID, "special_action");
    public static final Identifier ABILITY_SLIDER_ID = new Identifier(TravelersBackpack.MODID, "ability_slider");
    public static final Identifier SORTER_ID = new Identifier(TravelersBackpack.MODID, "sorter");
    public static final Identifier SLOT_ID = new Identifier(TravelersBackpack.MODID, "slot");
    public static final Identifier UPDATE_CONFIG_ID = new Identifier(TravelersBackpack.MODID,"update_config");

    public static void initClient()
    {
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_CONFIG_ID, (client, handler, buf, sender) ->
        {
            NbtCompound configNbt = buf.readNbt();
            client.execute(() ->
            {
                if(configNbt != null)
                {
                    TravelersBackpackConfig.fromNbt(configNbt);
                }
            });
        });
    }

    public static void initServer()
    {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
        {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeNbt(TravelersBackpackConfig.toNbt());
            sender.sendPacket(ModNetwork.UPDATE_CONFIG_ID, buf);
        });

        ServerPlayNetworking.registerGlobalReceiver(EQUIP_BACKPACK_ID, (server, player, handler, buf, response) ->
        {
            server.execute(() -> {
                if(player != null) //&& !TravelersBackpack.enableCurios())
                {
                    if(!ComponentUtils.isWearingBackpack(player))
                    {
                        ServerActions.equipBackpack(player);
                    }
                    else
                    {
                        player.closeScreenHandler();
                        player.sendMessage(Text.translatable(Reference.OTHER_BACKPACK), false);
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(UNEQUIP_BACKPACK_ID, (server, player, handler, buf, response) ->
        {
            server.execute(() -> {
                if(player != null) //&& !TravelersBackpack.enableCurios())
                {
                    if(ComponentUtils.isWearingBackpack(player))
                    {
                        ServerActions.unequipBackpack(player);
                    }
                    else
                    {
                        player.closeScreenHandler();
                        player.sendMessage(Text.translatable(Reference.NO_BACKPACK), false);
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(OPEN_SCREEN_ID, (server, player, handler, buf, response) ->
        {
            server.execute(() -> {
                if(player != null)
                {
                    if(ComponentUtils.isWearingBackpack(player))
                    {
                        TravelersBackpackInventory.openHandledScreen(player, ComponentUtils.getWearingBackpack(player), Reference.WEARABLE_SCREEN_ID);
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(DEPLOY_SLEEPING_BAG_ID, (server, player, handler, buf, response) ->
        {
            BlockPos pos = buf.readBlockPos();

            server.execute(() -> {
                if(player != null)
                {
                    ServerActions.toggleSleepingBag(player, pos);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(SPECIAL_ACTION_ID, (server, player, handler, buf, response) ->
        {
            double scrollDelta = buf.readDouble();
            byte actionId = buf.readByte();
            byte screenID = buf.readByte();
            BlockPos pos = null;

            if(screenID == Reference.BLOCK_ENTITY_SCREEN_ID) pos = buf.readBlockPos();
            BlockPos finalPos = pos;

            server.execute(() -> {
                if(player != null)
                {
                    if(actionId == Reference.SWAP_TOOL)
                    {
                        ServerActions.swapTool(player, scrollDelta);
                    }

                    else if(actionId == Reference.SWITCH_HOSE_MODE)
                    {
                        ServerActions.switchHoseMode(player, scrollDelta);
                    }

                    else if(actionId == Reference.TOGGLE_HOSE_TANK)
                    {
                        ServerActions.toggleHoseTank(player);
                    }

                    else if(actionId == Reference.EMPTY_TANK)
                    {
                        ServerActions.emptyTank(scrollDelta, player, player.world, screenID, finalPos);
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(ABILITY_SLIDER_ID, (server, player, handler, buf, response) ->
        {
            BlockPos blockPos = null;
            boolean sliderValue = buf.readBoolean();

            if(buf.writerIndex() == 9)
            {
                blockPos = buf.readBlockPos();
            }

            BlockPos finalBlockPos = blockPos;

            server.execute(() -> {
                if(player != null)
                {
                    if(finalBlockPos == null && ComponentUtils.isWearingBackpack(player))
                    {
                        ServerActions.switchAbilitySlider(player, sliderValue);
                    }
                    else if(finalBlockPos != null)
                    {
                        ServerActions.switchAbilitySliderBlockEntity(player, finalBlockPos);
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(SORTER_ID, (server, player, handler, buf, response) ->
        {
            byte screenID = buf.readByte();
            byte button = buf.readByte();
            boolean shiftPressed = buf.readBoolean();
            BlockPos pos = null;

            if(buf.writerIndex() == 11)
            {
                pos = buf.readBlockPos();
            }

            BlockPos finalBlockPos = pos;

            server.execute(() -> {
                if(player != null)
                {
                    ServerActions.sortBackpack(player, screenID, button, shiftPressed, finalBlockPos);
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(SLOT_ID, (server, player, handler, buf, response) ->
        {
            final byte screenID = buf.readByte();
            final boolean isActive = buf.readBoolean();
            final int[] selectedSlots = buf.readIntArray();

            server.execute(() -> {
                if(player != null)
                {
                    if(screenID == Reference.WEARABLE_SCREEN_ID)
                    {
                        SlotManager manager = ComponentUtils.getBackpackInv(player).getSlotManager();
                        manager.setActive(isActive);
                        manager.setUnsortableSlots(selectedSlots, true);
                        manager.setActive(!isActive);
                    }
                    if(screenID == Reference.ITEM_SCREEN_ID)
                    {
                        SlotManager manager = ((TravelersBackpackItemScreenHandler)player.currentScreenHandler).inventory.getSlotManager();
                        manager.setActive(isActive);
                        manager.setUnsortableSlots(selectedSlots, true);
                        manager.setActive(!isActive);
                    }
                    if(screenID == Reference.BLOCK_ENTITY_SCREEN_ID)
                    {
                        SlotManager manager = ((TravelersBackpackBlockEntityScreenHandler)player.currentScreenHandler).inventory.getSlotManager();
                        manager.setActive(isActive);
                        manager.setUnsortableSlots(selectedSlots, true);
                        manager.setActive(!isActive);
                    }
                }
            });
        });
    }
}