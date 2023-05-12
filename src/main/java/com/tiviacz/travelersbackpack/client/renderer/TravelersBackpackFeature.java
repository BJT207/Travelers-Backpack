package com.tiviacz.travelersbackpack.client.renderer;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.model.TravelersBackpackWearableModel;
import com.tiviacz.travelersbackpack.common.recipes.BackpackDyeRecipe;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import com.tiviacz.travelersbackpack.util.ResourceUtils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Triple;

public class TravelersBackpackFeature extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>
{
    public TravelersBackpackWearableModel<AbstractClientPlayerEntity> model;

    public TravelersBackpackFeature(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> entityRendererIn)
    {
        super(entityRendererIn);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch)
    {
        if(TravelersBackpackConfig.disableBackpackRender) return;

        if(ComponentUtils.isWearingBackpack(entity))
        {
            ITravelersBackpackInventory inv = ComponentUtils.getBackpackInv(entity);

            if(inv != null && !entity.isInvisible())
            {
                ItemStack stack = entity.getEquippedStack(EquipmentSlot.CHEST);

                if(!TravelersBackpackConfig.renderBackpackWithElytra)
                {
                    if(stack.getItem() instanceof ElytraItem)
                    {
                        return;
                    }
                    else
                    {
                        renderLayer(matrices, vertexConsumers, light, entity, inv);
                    }
                }
                else
                {
                    renderLayer(matrices, vertexConsumers, light, entity, inv);
                }
            }
        }
    }

    private void renderLayer(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, ITravelersBackpackInventory inv)
    {
        model = new TravelersBackpackWearableModel<>(entity, vertexConsumers);
        boolean flag = inv.getItemStack().getItem() == ModItems.QUARTZ_TRAVELERS_BACKPACK || inv.getItemStack().getItem() == ModItems.SNOW_TRAVELERS_BACKPACK;

        Identifier id = ResourceUtils.getBackpackTexture(inv.getItemStack().getItem());

        boolean isColorable = false;
        boolean isCustomSleepingBag = false;

        if(inv.getItemStack().getTag() != null && inv.getItemStack().getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK)
        {
            if(BackpackDyeRecipe.hasColor(inv.getItemStack()))
            {
                isColorable = true;
                id = new Identifier(TravelersBackpack.MODID, "textures/model/dyed.png");
            }
        }

        if(inv.getItemStack().getTag() != null)
        {
            if(inv.getItemStack().getTag().contains("SleepingBagColor"))
            {
                isCustomSleepingBag = true;
            }
        }

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(flag ? RenderLayer.getEntityTranslucentCull(id) : RenderLayer.getEntitySolid(id));

        matrices.push();

        if(entity.isSneaking())
        {
            matrices.translate(0D, -0.155D, 0.025D);
        }

        this.getContextModel().setAttributes(model);    //#TODO Is it okay? I know no other way to stick model to player's model
        model.setupAngles(this.getContextModel());

        matrices.translate(0, 0.175, 0.325);
        matrices.scale(0.85F, 0.85F, 0.85F);

        if(isColorable)
        {
            Triple<Float, Float, Float> rgb = RenderUtils.intToRGB(BackpackDyeRecipe.getColor(inv.getItemStack()));
            model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, rgb.getLeft(), rgb.getMiddle(), rgb.getRight(), 1.0F);

            id = new Identifier(TravelersBackpack.MODID, "textures/model/dyed_extras.png");
            vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(id));

        }
        model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);

        if(isCustomSleepingBag)
        {
            id = ResourceUtils.getSleepingBagTexture(inv.getSleepingBagColor());
        }
        else
        {
            id = ResourceUtils.getDefaultSleepingBagTexture();
        }

        vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(id));
        model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 0.25F);

        matrices.pop();
    }
}