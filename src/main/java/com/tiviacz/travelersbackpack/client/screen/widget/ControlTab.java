package com.tiviacz.travelersbackpack.client.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackScreen;
import com.tiviacz.travelersbackpack.inventory.sorter.InventorySorter;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.network.SSorterPacket;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;

public class ControlTab extends WidgetBase
{
    public ControlTab(TravelersBackpackScreen screen, int x, int y, int width, int height)
    {
        super(screen, x, y, width, height);
        this.isVisible = true;
    }

    @Override
    void renderBg(MatrixStack matrixStack, Minecraft minecraft, int mouseX, int mouseY)
    {
        minecraft.getTextureManager().bind(TravelersBackpackScreen.EXTRAS_TRAVELERS_BACKPACK);

        if(isVisible())
        {
            blit(matrixStack, x, y, 133, 0, width, height);

            if(isButtonHovered(mouseX, mouseY, Buttons.SORT))
            {
                blit(matrixStack, x + 4, y + 4, 137, 18, 9, 9);
            }

            if(isButtonHovered(mouseX, mouseY, Buttons.QUICK_STACK))
            {
                blit(matrixStack, x + 15, y + 4, 148, 18, 9, 9);
            }

            if(isButtonHovered(mouseX, mouseY, Buttons.TRANSFER_TO_BACKPACK))
            {
                blit(matrixStack, x + 26, y + 4, 159, 18, 9, 9);
            }

            if(isButtonHovered(mouseX, mouseY, Buttons.TRANSFER_TO_PLAYER))
            {
                blit(matrixStack, x + 37, y + 4, 170, 18, 9, 9);
            }
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if(BackpackUtils.isShiftPressed())
        {
            if(isButtonHovered(mouseX, mouseY, Buttons.SORT))
            {
                List<ITextComponent> list = new ArrayList<>();
                list.add(new TranslationTextComponent("screen.travelersbackpack.sort"));

                screen.renderComponentTooltip(matrixStack, list, mouseX, mouseY);
            }

            if(isButtonHovered(mouseX, mouseY, Buttons.QUICK_STACK))
            {
                List<ITextComponent> list = new ArrayList<>();
                list.add(new TranslationTextComponent("screen.travelersbackpack.quick_stack"));
                list.add(new TranslationTextComponent("screen.travelersbackpack.quick_stack_shift"));

                screen.renderComponentTooltip(matrixStack, list, mouseX, mouseY);
            }

            if(isButtonHovered(mouseX, mouseY, Buttons.TRANSFER_TO_BACKPACK))
            {
                List<ITextComponent> list = new ArrayList<>();
                list.add(new TranslationTextComponent("screen.travelersbackpack.transfer_to_backpack"));
                list.add(new TranslationTextComponent("screen.travelersbackpack.transfer_to_backpack_shift"));

                screen.renderComponentTooltip(matrixStack, list, mouseX, mouseY);
            }

            if(isButtonHovered(mouseX, mouseY, Buttons.TRANSFER_TO_PLAYER))
            {
                screen.renderTooltip(matrixStack, new TranslationTextComponent("screen.travelersbackpack.transfer_to_player"), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean isSettingsChild()
    {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(screen.inv.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE) || screen.inv.getSlotManager().isSelectorActive(SlotManager.MEMORY))
        {
            return false;
        }

        if(isButtonHovered((int)mouseX, (int)mouseY, Buttons.SORT))
        {
            TravelersBackpack.NETWORK.sendToServer(new SSorterPacket(screen.inv.getScreenID(), InventorySorter.SORT_BACKPACK, BackpackUtils.isShiftPressed()));
            screen.playUIClickSound();
            return true;
        }

        if(isButtonHovered((int)mouseX, (int)mouseY, Buttons.QUICK_STACK))
        {
            TravelersBackpack.NETWORK.sendToServer(new SSorterPacket(screen.inv.getScreenID(), InventorySorter.QUICK_STACK, BackpackUtils.isShiftPressed()));
            screen.playUIClickSound();
            return true;
        }

        if(isButtonHovered((int)mouseX, (int)mouseY, Buttons.TRANSFER_TO_BACKPACK))
        {
            TravelersBackpack.NETWORK.sendToServer(new SSorterPacket(screen.inv.getScreenID(), InventorySorter.TRANSFER_TO_BACKPACK, BackpackUtils.isShiftPressed()));
            screen.playUIClickSound();
            return true;
        }

        if(isButtonHovered((int)mouseX, (int)mouseY, Buttons.TRANSFER_TO_PLAYER))
        {
            TravelersBackpack.NETWORK.sendToServer(new SSorterPacket(screen.inv.getScreenID(), InventorySorter.TRANSFER_TO_PLAYER, BackpackUtils.isShiftPressed()));
            screen.playUIClickSound();
            return true;
        }
        return false;
    }

    public boolean isButtonHovered(int mouseX, int mouseY, Buttons button)
    {
        return (65 + button.ordinal() * 11) + screen.getGuiLeft() <= mouseX && mouseX <= (65 + button.ordinal() * 11) + 8 + screen.getGuiLeft() && -6 + screen.getGuiTop() <= mouseY && mouseY <= -6 + 8 + screen.getGuiTop();
    }

    public enum Buttons
    {
        SORT,
        QUICK_STACK,
        TRANSFER_TO_BACKPACK,
        TRANSFER_TO_PLAYER
    }
}
