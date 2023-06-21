package com.tiviacz.travelersbackpack.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class OverlayScreen extends Screen
{
    public Minecraft mc;
    public ItemRenderer itemRenderer;
    public MainWindow mainWindow;

    public OverlayScreen()
    {
        super(new StringTextComponent(""));

        this.mc = Minecraft.getInstance();
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
        this.mainWindow = Minecraft.getInstance().getWindow();
    }

    public void renderOverlay(MatrixStack matrixStack)
    {
        PlayerEntity player = mc.player;

        int offsetX = TravelersBackpackConfig.offsetX;
        int offsetY = TravelersBackpackConfig.offsetY;
        int scaledWidth = mainWindow.getGuiScaledWidth() - offsetX;
        int scaledHeight = mainWindow.getGuiScaledHeight() - offsetY;

        int textureX = 10;
        int textureY = 0;

        ITravelersBackpackInventory inv = CapabilityUtils.getBackpackInv(player);
        FluidTank rightTank = inv.getRightTank();
        FluidTank leftTank = inv.getLeftTank();

        if(!rightTank.getFluid().isEmpty())
        {
            this.drawGuiTank(matrixStack, rightTank, scaledWidth + 1, scaledHeight, 21, 8);
        }

        if(!leftTank.getFluid().isEmpty())
        {
            this.drawGuiTank(matrixStack, leftTank, scaledWidth - 11, scaledHeight, 21, 8);
        }

        if(!inv.getInventory().getStackInSlot(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_UPPER)).isEmpty())
        {
            this.drawItemStack(inv.getInventory().getStackInSlot(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_UPPER)), scaledWidth - 30, scaledHeight - 4);
        }

        if(!inv.getInventory().getStackInSlot(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_LOWER)).isEmpty())
        {
            this.drawItemStack(inv.getInventory().getStackInSlot(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_LOWER)), scaledWidth - 30, scaledHeight + 11);
        }

        ResourceLocation texture = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/travelers_backpack_overlay.png");
        mc.getTextureManager().bind(texture);

        if(player.getMainHandItem().getItem() instanceof HoseItem)
        {
            int tank = HoseItem.getHoseTank(player.getMainHandItem());

            int selectedTextureX = 0;
            int selectedTextureY = 0;

            if(tank == 1)
            {
                blit(matrixStack, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
                blit(matrixStack, scaledWidth - 12, scaledHeight, selectedTextureX, selectedTextureY, 10, 23);
            }

            if(tank == 2)
            {
                blit(matrixStack, scaledWidth, scaledHeight, selectedTextureX, selectedTextureY, 10, 23);
                blit(matrixStack, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
            }

            if(tank == 0)
            {
                blit(matrixStack, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
                blit(matrixStack, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
            }
        }
        else
        {
            blit(matrixStack, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
            blit(matrixStack, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
        }
    }

    public void drawGuiTank(MatrixStack matrixStackIn, FluidTank tank, int startX, int startY, int height, int width)
    {
        RenderUtils.renderScreenTank(matrixStackIn, tank, startX, startY, height, width);
    }

    private void drawItemStack(ItemStack stack, int x, int y)
    {
        //RenderHelper.turnBackOn();
        itemRenderer.renderGuiItem(stack, x, y);
        itemRenderer.renderGuiItemDecorations(Minecraft.getInstance().font, stack, x, y);
        //RenderHelper.turnOff();
    }
}