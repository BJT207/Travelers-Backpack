package com.tiviacz.travelersbackpack.client.screens;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.handlers.ModClientEventsHandler;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OverlayScreen
{
    static float animationProgress = 0.0F;

    public static void renderOverlay(ForgeGui gui, Minecraft mc, PoseStack poseStack)
    {
        Player player = mc.player;
        Window mainWindow = mc.getWindow();

        int offsetX = TravelersBackpackConfig.offsetX;
        int offsetY = TravelersBackpackConfig.offsetY;
        int scaledWidth = mainWindow.getGuiScaledWidth() - offsetX;
        int scaledHeight = mainWindow.getGuiScaledHeight() - offsetY;

        int textureX = 10;
        int textureY = 0;

        ITravelersBackpackContainer inv = CapabilityUtils.getBackpackInv(player);
        FluidTank rightTank = inv.getRightTank();
        FluidTank leftTank = inv.getLeftTank();

        KeyMapping key = ModClientEventsHandler.CYCLE_TOOL;
        List<ItemStack> tools = getTools(inv.getTier(), inv.getHandler());

        if(key.isDown() && tools.size() > 2)
        {
            if(animationProgress < 1.0F)
            {
                animationProgress += 0.05F;
            }
            for(int i = 0; i < getTools(inv.getTier(), inv.getHandler()).size(); i++)
            {
                drawItemStack(mc.getItemRenderer(), poseStack, getTools(inv.getTier(), inv.getHandler()).get(i), scaledWidth - 30, (int)(scaledHeight + 11 - (animationProgress * (i * 15))));
            }
        }
        else
        {
            if(animationProgress > 0.0F)
            {
                for(int i = 0; i < getTools(inv.getTier(), inv.getHandler()).size(); i++)
                {
                    drawItemStack(mc.getItemRenderer(), poseStack, getTools(inv.getTier(), inv.getHandler()).get(i), scaledWidth - 30, (int)(scaledHeight + 11 - (animationProgress * (i * 15))));
                }
                animationProgress -= 0.05F;
            }
            else
            {
                if(!inv.getHandler().getStackInSlot(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_FIRST)).isEmpty())
                {
                    drawItemStack(mc.getItemRenderer(), poseStack, inv.getHandler().getStackInSlot(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_FIRST)), scaledWidth - 30, scaledHeight - 4);
                }
                if(tools.size() > 1)
                {
                    if(!inv.getHandler().getStackInSlot(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_SECOND) + tools.size() - 2).isEmpty())
                    {
                        drawItemStack(mc.getItemRenderer(), poseStack, inv.getHandler().getStackInSlot(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_SECOND) + tools.size() - 2), scaledWidth - 30, scaledHeight + 11);
                    }
                }
            }
        }

        if(!rightTank.getFluid().isEmpty())
        {
            drawGuiTank(poseStack, rightTank, scaledWidth + 1, scaledHeight, 21, 8);
        }

        if(!leftTank.getFluid().isEmpty())
        {
            drawGuiTank(poseStack, leftTank, scaledWidth - 11, scaledHeight, 21, 8);
        }

        ResourceLocation texture = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/travelers_backpack_overlay.png");

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, texture);

        if(player.getMainHandItem().getItem() instanceof HoseItem)
        {
            int tank = HoseItem.getHoseTank(player.getMainHandItem());

            int selectedTextureX = 0;
            int selectedTextureY = 0;

            if(tank == 1)
            {
                GuiComponent.blit(poseStack, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
                GuiComponent.blit(poseStack, scaledWidth - 12, scaledHeight, selectedTextureX, selectedTextureY, 10, 23);
            }

            if(tank == 2)
            {
                GuiComponent.blit(poseStack, scaledWidth, scaledHeight, selectedTextureX, selectedTextureY, 10, 23);
                GuiComponent.blit(poseStack, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
            }

            if(tank == 0)
            {
                GuiComponent.blit(poseStack, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
                GuiComponent.blit(poseStack, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
            }
        }
        else
        {
            GuiComponent.blit(poseStack, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
            GuiComponent.blit(poseStack, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
        }
    }

    public static void drawGuiTank(PoseStack matrixStackIn, FluidTank tank, int startX, int startY, int height, int width)
    {
        RenderUtils.renderScreenTank(matrixStackIn, tank, startX, startY, 0, height, width);
    }

    private static void drawItemStack(ItemRenderer itemRenderer, PoseStack poseStack, ItemStack stack, int x, int y)
    {
        itemRenderer.m_274336_(poseStack, stack, x, y);
        itemRenderer.m_274412_(poseStack, Minecraft.getInstance().font, stack, x, y);
    }

    public static List<ItemStack> getTools(Tiers.Tier tier, ItemStackHandler inventory)
    {
        List<ItemStack> tools = new ArrayList<>();

        for(int i = tier.getSlotIndex(Tiers.SlotType.TOOL_FIRST); i <= tier.getSlotIndex(Tiers.SlotType.TOOL_FIRST) + (tier.getToolSlots() - 1); i++)
        {
            if(!inventory.getStackInSlot(i).isEmpty())
            {
                tools.add(inventory.getStackInSlot(i));
            }
        }
        Collections.reverse(tools);
        return tools;
    }
}