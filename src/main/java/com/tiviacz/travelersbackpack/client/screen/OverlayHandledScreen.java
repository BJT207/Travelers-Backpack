package com.tiviacz.travelersbackpack.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class OverlayHandledScreen extends Screen
{
    public MinecraftClient mc;
    public ItemRenderer itemRenderer;
    public Window mainWindow;

    public OverlayHandledScreen()
    {
        super(new LiteralText(""));

        this.mc = MinecraftClient.getInstance();
        this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        this.mainWindow = MinecraftClient.getInstance().getWindow();
    }

    public void renderOverlay(MatrixStack matrices)
    {
        PlayerEntity player = mc.player;

        int offsetX = TravelersBackpackConfig.offsetX;
        int offsetY = TravelersBackpackConfig.offsetY;
        int scaledWidth = mainWindow.getScaledWidth() - offsetX;
        int scaledHeight = mainWindow.getScaledHeight() - offsetY;

        int textureX = 10;
        int textureY = 0;

        ITravelersBackpackInventory inv = ComponentUtils.getBackpackInv(player);
        SingleVariantStorage<FluidVariant> rightFluidStorage = inv.getRightTank();
        SingleVariantStorage<FluidVariant> leftFluidStorage = inv.getLeftTank();

        if(!rightFluidStorage.getResource().isBlank())
        {
            this.drawGuiTank(matrices, rightFluidStorage, scaledWidth + 1, scaledHeight, 21, 8);
        }

        if(!leftFluidStorage.getResource().isBlank())
        {
            this.drawGuiTank(matrices, leftFluidStorage, scaledWidth - 11, scaledHeight, 21, 8);
        }

        if(inv.getTier() != null)
        {
            if(!inv.getInventory().getStack(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_UPPER)).isEmpty())
            {
                this.drawItemStack(inv.getInventory().getStack(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_UPPER)), scaledWidth - 30, scaledHeight - 4);
            }

            if(!inv.getInventory().getStack(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_LOWER)).isEmpty())
            {
                this.drawItemStack(inv.getInventory().getStack(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_LOWER)), scaledWidth - 30, scaledHeight + 11);
            }
        }

        Identifier id = new Identifier(TravelersBackpack.MODID, "textures/gui/travelers_backpack_overlay.png");
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, id);

        if(player.getMainHandStack().getItem() instanceof HoseItem)
        {
            int tank = HoseItem.getHoseTank(player.getMainHandStack());

            int selectedTextureX = 0;
            int selectedTextureY = 0;

            if(tank == 1)
            {
                drawTexture(matrices, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
                drawTexture(matrices, scaledWidth - 12, scaledHeight, selectedTextureX, selectedTextureY, 10, 23);
            }

            if(tank == 2)
            {
                drawTexture(matrices, scaledWidth, scaledHeight, selectedTextureX, selectedTextureY, 10, 23);
                drawTexture(matrices, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
            }

            if(tank == 0)
            {
                drawTexture(matrices, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
                drawTexture(matrices, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
            }
        }
        else
        {
            drawTexture(matrices, scaledWidth, scaledHeight, textureX, textureY, 10, 23);
            drawTexture(matrices, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23);
        }
    }

    public void drawGuiTank(MatrixStack matrixStackIn, SingleVariantStorage<FluidVariant> fluidStorage, int startX, int startY, int height, int width)
    {
        RenderUtils.renderScreenTank(matrixStackIn, fluidStorage, startX, startY, 0, height, width);
    }

    private void drawItemStack(ItemStack stack, int x, int y)
    {
        this.itemRenderer.renderGuiItemIcon(stack, x, y);
        this.itemRenderer.renderGuiItemOverlay(MinecraftClient.getInstance().textRenderer, stack, x, y);
    }
}