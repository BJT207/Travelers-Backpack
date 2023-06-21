package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.math.Vec3f;
import org.lwjgl.opengl.GL11;

public class StackPart extends ModelPart
{
    private final PlayerEntity player;
    private final VertexConsumerProvider provider;

    public StackPart(Model model, PlayerEntity player, VertexConsumerProvider provider)
    {
        super(model);
        this.player = player;
        this.provider = provider;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay)
    {
        matrices.push();
        this.rotate(matrices);
        render(this.player, matrices, this.provider, light, overlay);
        matrices.pop();
    }

    public void render(PlayerEntity player, MatrixStack matrices, VertexConsumerProvider vertices, int light, int overlay)
    {
        ITravelersBackpackInventory inv = ComponentUtils.getBackpackInv(player);
        ItemStack toolUpper = inv.getInventory().getStack(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_UPPER));
        ItemStack toolLower = inv.getInventory().getStack(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_LOWER));

        if(!toolUpper.isEmpty())
        {
            BakedModel model = MinecraftClient.getInstance().getItemRenderer().getHeldItemModel(toolUpper, player.world, player);
            //model = ForgeHooksClient.handleCameraTransforms(matrices, model, ItemCameraTransforms.TransformType.NONE, false);

            matrices.push();

            RenderSystem.enableRescaleNormal();
            RenderSystem.alphaFunc(GL11.GL_GREATER, 0.1F);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

            matrices.translate(0.05, 0.075, 0.27);
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(45F));
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180F));
            matrices.scale(0.65F, 0.65F, 0.65F);

            MinecraftClient.getInstance().getTextureManager().bindTexture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
            MinecraftClient.getInstance().getItemRenderer()
                    .renderItem(toolUpper, ModelTransformation.Mode.NONE, false, matrices, vertices, light, overlay, model);

            RenderSystem.disableRescaleNormal();
            RenderSystem.disableBlend();
            matrices.pop();
        }

        if(!toolLower.isEmpty())
        {
            BakedModel model = MinecraftClient.getInstance().getItemRenderer().getHeldItemModel(toolLower, player.world, player);
            //model = ForgeHooksClient.handleCameraTransforms(matrices, model, ModelTransformation.Mode.NONE, false);

            matrices.push();
            RenderSystem.enableRescaleNormal();
            RenderSystem.alphaFunc(GL11.GL_GREATER, 0.1f);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

            matrices.translate(-0.35, 0.95, 0);
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90F));
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(45F));
            matrices.scale(0.65F, 0.65F, 0.65F);

            MinecraftClient.getInstance().getTextureManager().bindTexture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
            MinecraftClient.getInstance().getItemRenderer()
                    .renderItem(toolLower, ModelTransformation.Mode.NONE, false, matrices, vertices, light, overlay, model);

            RenderSystem.disableRescaleNormal();
            RenderSystem.disableBlend();
            matrices.pop();
        }
    }
}