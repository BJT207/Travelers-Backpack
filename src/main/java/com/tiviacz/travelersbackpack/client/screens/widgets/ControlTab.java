package com.tiviacz.travelersbackpack.client.screens.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screens.TravelersBackpackScreen;
import com.tiviacz.travelersbackpack.inventory.sorter.ContainerSorter;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.network.ServerboundSorterPacket;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;

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
    void renderBg(PoseStack poseStack, Minecraft minecraft, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TravelersBackpackScreen.EXTRAS_TRAVELERS_BACKPACK);

        if(isVisible())
        {
            blit(poseStack, x, y, 133, 0, width, height);

            if(isButtonHovered(mouseX, mouseY, Buttons.SORT))
            {
                blit(poseStack, x + 4, y + 4, 137, 18, 9, 9);
            }

            if(isButtonHovered(mouseX, mouseY, Buttons.QUICK_STACK))
            {
                blit(poseStack, x + 15, y + 4, 148, 18, 9, 9);
            }

            if(isButtonHovered(mouseX, mouseY, Buttons.TRANSFER_TO_BACKPACK))
            {
                blit(poseStack, x + 26, y + 4, 159, 18, 9, 9);
            }

            if(isButtonHovered(mouseX, mouseY, Buttons.TRANSFER_TO_PLAYER))
            {
                blit(poseStack, x + 37, y + 4, 170, 18, 9, 9);
            }
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    void renderTooltip(PoseStack poseStack, int mouseX, int mouseY)
    {
        if(BackpackUtils.isShiftPressed())
        {
            if(isButtonHovered(mouseX, mouseY, Buttons.SORT))
            {
                List<FormattedCharSequence> list = new ArrayList<>();
                list.add(new TranslatableComponent("screen.travelersbackpack.sort").getVisualOrderText());
                //list.add(Component.translatable("screen.travelersbackpack.sort_shift").getVisualOrderText());

                screen.renderTooltip(poseStack, list, mouseX, mouseY);
            }

            if(isButtonHovered(mouseX, mouseY, Buttons.QUICK_STACK))
            {
                List<FormattedCharSequence> list = new ArrayList<>();
                list.add(new TranslatableComponent("screen.travelersbackpack.quick_stack").getVisualOrderText());
                list.add(new TranslatableComponent("screen.travelersbackpack.quick_stack_shift").getVisualOrderText());

                screen.renderTooltip(poseStack, list, mouseX, mouseY);
            }

            if(isButtonHovered(mouseX, mouseY, Buttons.TRANSFER_TO_BACKPACK))
            {
                List<FormattedCharSequence> list = new ArrayList<>();
                list.add(new TranslatableComponent("screen.travelersbackpack.transfer_to_backpack").getVisualOrderText());
                list.add(new TranslatableComponent("screen.travelersbackpack.transfer_to_backpack_shift").getVisualOrderText());

                screen.renderTooltip(poseStack, list, mouseX, mouseY);
            }

            if(isButtonHovered(mouseX, mouseY, Buttons.TRANSFER_TO_PLAYER))
            {
                screen.renderTooltip(poseStack, new TranslatableComponent("screen.travelersbackpack.transfer_to_player"), mouseX, mouseY);
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
        if(screen.container.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE) || screen.container.getSlotManager().isSelectorActive(SlotManager.MEMORY))
        {
            return false;
        }

        if(isButtonHovered((int)mouseX, (int)mouseY, Buttons.SORT))
        {
            TravelersBackpack.NETWORK.sendToServer(new ServerboundSorterPacket(screen.container.getScreenID(), ContainerSorter.SORT_BACKPACK, BackpackUtils.isShiftPressed()));
            screen.playUIClickSound();
            return true;
        }

        if(isButtonHovered((int)mouseX, (int)mouseY, Buttons.QUICK_STACK))
        {
            TravelersBackpack.NETWORK.sendToServer(new ServerboundSorterPacket(screen.container.getScreenID(), ContainerSorter.QUICK_STACK, BackpackUtils.isShiftPressed()));
            screen.playUIClickSound();
            return true;
        }

        if(isButtonHovered((int)mouseX, (int)mouseY, Buttons.TRANSFER_TO_BACKPACK))
        {
            TravelersBackpack.NETWORK.sendToServer(new ServerboundSorterPacket(screen.container.getScreenID(), ContainerSorter.TRANSFER_TO_BACKPACK, BackpackUtils.isShiftPressed()));
            screen.playUIClickSound();
            return true;
        }

        if(isButtonHovered((int)mouseX, (int)mouseY, Buttons.TRANSFER_TO_PLAYER))
        {
            TravelersBackpack.NETWORK.sendToServer(new ServerboundSorterPacket(screen.container.getScreenID(), ContainerSorter.TRANSFER_TO_PLAYER, BackpackUtils.isShiftPressed()));
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