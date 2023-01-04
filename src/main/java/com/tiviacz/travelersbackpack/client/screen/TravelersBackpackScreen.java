package com.tiviacz.travelersbackpack.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.handlers.ModClientEventHandler;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackBaseContainer;
import com.tiviacz.travelersbackpack.inventory.sorter.InventorySorter;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.network.*;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TravelersBackpackScreen extends ContainerScreen<TravelersBackpackBaseContainer>
{
    public static final ResourceLocation SCREEN_TRAVELERS_BACKPACK = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/travelers_backpack.png");
    private static final ScreenImageButton BED_BUTTON = new ScreenImageButton(5, 96, 18, 18);
    private static final ScreenImageButton EQUIP_BUTTON = new ScreenImageButton(5, 96, 18, 18);
    private static final ScreenImageButton UNEQUIP_BUTTON = new ScreenImageButton(5, 96, 18, 18);
    private static final ScreenImageButton DISABLED_CRAFTING_BUTTON = new ScreenImageButton(225, 96, 18, 18);
    private static final ScreenImageButton ABILITY_SLIDER = new ScreenImageButton(5, 56,18, 11);
    private static final ScreenImageButton SORT_BUTTON = new ScreenImageButton(61, -10, 14, 13);
    private static final ScreenImageButton QUICK_STACK_BUTTON = new ScreenImageButton(75, -10, 11, 13);
    private static final ScreenImageButton TRANSFER_TO_BACKPACK_BUTTON = new ScreenImageButton(86, -10, 11, 13);
    private static final ScreenImageButton TRANSFER_TO_PLAYER_BUTTON = new ScreenImageButton(97, -10, 11, 13);
    private static final ScreenImageButton MEMORY_BUTTON = new ScreenImageButton(108, -10, 14, 13);
    private final ITravelersBackpackInventory inv;
    private final byte screenID;
    private final TankScreen tankLeft;
    private final TankScreen tankRight;

    public TravelersBackpackScreen(TravelersBackpackBaseContainer screenContainer, PlayerInventory inventory, ITextComponent titleIn)
    {
        super(screenContainer, inventory, titleIn);
        this.inv = screenContainer.inventory;
        this.screenID = screenContainer.inventory.getScreenID();

        this.leftPos = 0;
        this.topPos = 0;

        this.imageWidth = 248;
        this.imageHeight = 207;

        this.tankLeft = new TankScreen(inv.getLeftTank(), 25, 7, 100, 16);
        this.tankRight = new TankScreen(inv.getRightTank(), 207, 7, 100, 16);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if(!this.inv.getLeftTank().isEmpty())
        {
            this.tankLeft.drawScreenFluidBar(matrixStack);
        }
        if(!this.inv.getRightTank().isEmpty())
        {
            this.tankRight.drawScreenFluidBar(matrixStack);
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);

        if(this.tankLeft.inTank(this, mouseX, mouseY))
        {
            this.renderComponentTooltip(matrixStack, tankLeft.getTankTooltip(), mouseX, mouseY);
        }

        if(this.tankRight.inTank(this, mouseX, mouseY))
        {
            this.renderComponentTooltip(matrixStack, tankRight.getTankTooltip(), mouseX, mouseY);
        }

        if(BackpackUtils.isShiftPressed())
        {
            if(SORT_BUTTON.inButton(this, mouseX, mouseY, 65))
            {
                List<IReorderingProcessor> list = new ArrayList<>();
                list.add(new TranslationTextComponent("screen.travelersbackpack.sort").getVisualOrderText());
                list.add(new TranslationTextComponent("screen.travelersbackpack.sort_shift").getVisualOrderText());

                this.renderTooltip(matrixStack, list, mouseX, mouseY);
            }

            if(QUICK_STACK_BUTTON.inButton(this, mouseX, mouseY, 76))
            {
                List<IReorderingProcessor> list = new ArrayList<>();
                list.add(new TranslationTextComponent("screen.travelersbackpack.quick_stack").getVisualOrderText());
                list.add(new TranslationTextComponent("screen.travelersbackpack.quick_stack_shift").getVisualOrderText());

                this.renderTooltip(matrixStack, list, mouseX, mouseY);
            }

            if(TRANSFER_TO_BACKPACK_BUTTON.inButton(this, mouseX, mouseY, 87))
            {
                List<IReorderingProcessor> list = new ArrayList<>();
                list.add(new TranslationTextComponent("screen.travelersbackpack.transfer_to_backpack").getVisualOrderText());
                list.add(new TranslationTextComponent("screen.travelersbackpack.transfer_to_backpack_shift").getVisualOrderText());

                this.renderTooltip(matrixStack, list, mouseX, mouseY);
            }

            if(TRANSFER_TO_PLAYER_BUTTON.inButton(this, mouseX, mouseY, 98))
            {
                this.renderTooltip(matrixStack, new TranslationTextComponent("screen.travelersbackpack.transfer_to_player"), mouseX, mouseY);
            }

            if(MEMORY_BUTTON.inButton(this, mouseX, mouseY, 109))
            {
                this.renderTooltip(matrixStack, new TranslationTextComponent("screen.travelersbackpack.memory"), mouseX, mouseY);
            }
        }

        if(this.screenID == Reference.TILE_SCREEN_ID || this.screenID == Reference.WEARABLE_SCREEN_ID)
        {
            if(BackpackAbilities.isOnList(this.screenID == Reference.WEARABLE_SCREEN_ID ? BackpackAbilities.ITEM_ABILITIES_LIST : BackpackAbilities.BLOCK_ABILITIES_LIST, inv.getItemStack()) && ABILITY_SLIDER.inButton(this, mouseX, mouseY))
            {
                if(inv.getAbilityValue())
                {
                    List<IReorderingProcessor> list = new ArrayList<>();
                    list.add(new TranslationTextComponent("screen.travelersbackpack.ability_enabled").getVisualOrderText());
                    if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_TIMER_ABILITIES_LIST, inv.getItemStack()) || BackpackAbilities.isOnList(BackpackAbilities.BLOCK_TIMER_ABILITIES_LIST, inv.getItemStack()))
                    {
                        list.add(inv.getLastTime() == 0 ? new TranslationTextComponent("screen.travelersbackpack.ability_ready").getVisualOrderText() : new StringTextComponent(BackpackUtils.getConvertedTime(inv.getLastTime())).getVisualOrderText());
                    }
                    this.renderTooltip(matrixStack, list, mouseX, mouseY);
                }
                else
                {
                    if(!TravelersBackpackConfig.enableBackpackAbilities)
                    {
                        this.renderTooltip(matrixStack, new TranslationTextComponent("screen.travelersbackpack.ability_disabled_config"), mouseX, mouseY);
                    }
                    else
                    {
                        this.renderTooltip(matrixStack, new TranslationTextComponent("screen.travelersbackpack.ability_disabled"), mouseX, mouseY);
                    }
                }
            }
        }

        if(TravelersBackpackConfig.disableCrafting)
        {
            if(DISABLED_CRAFTING_BUTTON.inButton(this, mouseX, mouseY))
            {
                this.renderTooltip(matrixStack, new TranslationTextComponent("screen.travelersbackpack.disabled_crafting"), mouseX, mouseY);
            }
        }
    }

    public int getX(int slot)
    {
        if(slot <= 7)
        {
            return 62 + (18 * (slot));
        }
        else if(slot >= 8 && slot <= 15)
        {
            return 62 + (18 * (slot - 8));
        }
        else if(slot >= 16 && slot <= 23)
        {
            return 62 + (18 * (slot - 16));
        }
        else if(slot >= 24 && slot <= 28)
        {
            return 62 + (18 * (slot - 24));
        }
        else if(slot >= 29 && slot <= 33)
        {
            return 62 + (18 * (slot - 29));
        }
        else if(slot >= 34 && slot <= 38)
        {
            return 62 + (18 * (slot - 34));
        }

        return 0;
    }

    public int getY(int slot)
    {
        if(slot <= 7) return 7;
        else if(slot <= 15) return 25;
        else if(slot <= 23) return 43;
        else if(slot <= 28) return 61;
        else if(slot <= 33) return 79;
        else if(slot <= 38) return 97;

        return 0;
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bind(SCREEN_TRAVELERS_BACKPACK);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, x, y, 0, 0, this.imageWidth, this.imageHeight);

        if(TravelersBackpackConfig.disableCrafting)
        {
            DISABLED_CRAFTING_BUTTON.draw(matrixStack, this, 77, 208);
        }

        if(!inv.getSlotManager().getUnsortableSlots().isEmpty())
        {
            inv.getSlotManager().getUnsortableSlots()
                    .forEach(i -> this.blit(matrixStack, this.getGuiLeft() + getX(i), this.getGuiTop() + getY(i), 78, 228, 16, 16));
        }

        if(!inv.getSlotManager().getMemorySlots().isEmpty())
        {
            this.setBlitOffset(100);
            this.itemRenderer.blitOffset = 100.0F;

            inv.getSlotManager().getMemorySlots()
                    .forEach(pair -> {

                        if (!inv.getInventory().getStackInSlot(pair.getFirst()).isEmpty()) return;

                        ItemStack itemstack = pair.getSecond();
                        RenderSystem.enableDepthTest();
                        this.itemRenderer.renderAndDecorateItem(this.minecraft.player, itemstack, this.getGuiLeft() + getX(pair.getFirst()), this.getGuiTop() + getY(pair.getFirst()));
                        drawMemoryOverlay(matrixStack, this.getGuiLeft() + getX(pair.getFirst()), this.getGuiTop() + getY(pair.getFirst()));
                    });

            this.itemRenderer.blitOffset = 0.0F;
            this.setBlitOffset(0);
        }

        if(SORT_BUTTON.inButton(this, mouseX, mouseY, 65))
        {
            SORT_BUTTON.draw(matrixStack, this, 134, 222);
        }
        else
        {
            SORT_BUTTON.draw(matrixStack, this, 134, 208);
        }

        if(inv.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE))
        {
            SORT_BUTTON.draw(matrixStack, this, 134, 236);
        }

        if(MEMORY_BUTTON.inButton(this, mouseX, mouseY, 109))
        {
            MEMORY_BUTTON.draw(matrixStack, this, 181, 222);
        }
        else
        {
            MEMORY_BUTTON.draw(matrixStack, this, 181, 208);
        }

        if(inv.getSlotManager().isSelectorActive(SlotManager.MEMORY))
        {
            MEMORY_BUTTON.draw(matrixStack, this, 181, 236);
        }

        if(QUICK_STACK_BUTTON.inButton(this, mouseX, mouseY, 76))
        {
            QUICK_STACK_BUTTON.draw(matrixStack, this, 148, 222);
        }
        else
        {
            QUICK_STACK_BUTTON.draw(matrixStack, this, 148, 208);
        }

        if(TRANSFER_TO_BACKPACK_BUTTON.inButton(this, mouseX, mouseY, 87))
        {
            TRANSFER_TO_BACKPACK_BUTTON.draw(matrixStack, this, 159, 222);
        }
        else
        {
            TRANSFER_TO_BACKPACK_BUTTON.draw(matrixStack, this, 159, 208);
        }

        if(TRANSFER_TO_PLAYER_BUTTON.inButton(this, mouseX, mouseY, 98))
        {
            TRANSFER_TO_PLAYER_BUTTON.draw(matrixStack, this, 170, 222);
        }
        else
        {
            TRANSFER_TO_PLAYER_BUTTON.draw(matrixStack, this, 170, 208);
        }

        if(inv.hasTileEntity())
        {
            if(BED_BUTTON.inButton(this, mouseX, mouseY))
            {
                BED_BUTTON.draw(matrixStack, this, 20, 227);
            }
            else
            {
                BED_BUTTON.draw(matrixStack, this, 1, 227);
            }

            if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, inv.getItemStack()))
            {
                if(ABILITY_SLIDER.inButton(this, mouseX, mouseY))
                {
                    if(inv.getAbilityValue())
                    {
                        ABILITY_SLIDER.draw(matrixStack, this, 115, 208);
                    }
                    else
                    {
                        ABILITY_SLIDER.draw(matrixStack, this, 115, 220);
                    }
                }
                else
                {
                    if(inv.getAbilityValue())
                    {
                        ABILITY_SLIDER.draw(matrixStack, this, 96, 208);
                    }
                    else
                    {
                        ABILITY_SLIDER.draw(matrixStack, this, 96, 220);
                    }
                }
            }
        }
        else
        {
            if(!CapabilityUtils.isWearingBackpack(inventory.player) && this.screenID == Reference.ITEM_SCREEN_ID && !TravelersBackpack.enableCurios())
            {
                if(EQUIP_BUTTON.inButton(this, mouseX, mouseY))
                {
                    EQUIP_BUTTON.draw(matrixStack, this, 58, 208);
                }
                else
                {
                    EQUIP_BUTTON.draw(matrixStack, this, 39, 208);
                }
            }

            if(CapabilityUtils.isWearingBackpack(inventory.player) && this.screenID == Reference.WEARABLE_SCREEN_ID)
            {
                if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, inv.getItemStack()))
                {
                    if(ABILITY_SLIDER.inButton(this, mouseX, mouseY))
                    {
                        if(inv.getAbilityValue())
                        {
                            ABILITY_SLIDER.draw(matrixStack, this, 115, 208);
                        }
                        else
                        {
                            ABILITY_SLIDER.draw(matrixStack, this, 115, 220);
                        }
                    }
                    else
                    {
                        if(inv.getAbilityValue())
                        {
                            ABILITY_SLIDER.draw(matrixStack, this, 96, 208);
                        }
                        else
                        {
                            ABILITY_SLIDER.draw(matrixStack, this, 96, 220);
                        }
                    }
                }

                if(!TravelersBackpack.enableCurios())
                {
                    if(UNEQUIP_BUTTON.inButton(this, mouseX, mouseY))
                    {
                        UNEQUIP_BUTTON.draw(matrixStack, this, 58, 227);
                    }
                    else
                    {
                        UNEQUIP_BUTTON.draw(matrixStack, this, 39, 227);
                    }
                }
            }
        }
    }

    public void drawMemoryOverlay(MatrixStack matrixStack, int x, int y)
    {
        matrixStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        //RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(SCREEN_TRAVELERS_BACKPACK);
        blit(matrixStack, x, y, 97, 232, 16, 16);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        matrixStack.popPose();
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int button, ClickType type)
    {
        super.slotClicked(slot, slotId, button, type);

        if((slotId >= 10 && slotId <= 48) && inv.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE))
        {
            inv.getSlotManager().setUnsortableSlot(slotId - 10);
        }

        if((slotId >= 10 && slotId <= 48) && inv.getSlotManager().isSelectorActive(SlotManager.MEMORY) && (!slot.getItem().isEmpty() || (slot.getItem().isEmpty() && inv.getSlotManager().isSlot(SlotManager.MEMORY, slotId - 10))))
        {
            inv.getSlotManager().setMemorySlot(slotId - 10, slot.getItem());
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if(inv.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE) && !SORT_BUTTON.inButton(this, (int)mouseX, (int)mouseY, 65) || (inv.getSlotManager().isSelectorActive(SlotManager.MEMORY) && !MEMORY_BUTTON.inButton(this, (int)mouseX, (int)mouseY, 109)))
        {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        if(SORT_BUTTON.inButton(this, (int)mouseX, (int)mouseY, 65))
        {
            TravelersBackpack.NETWORK.sendToServer(new SSorterPacket(inv.getScreenID(), InventorySorter.SORT_BACKPACK, BackpackUtils.isShiftPressed()));

            //Turns slot checking on client
            if(BackpackUtils.isShiftPressed())
            {
                TravelersBackpack.NETWORK.sendToServer(new SSlotPacket(inv.getScreenID(), inv.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE), inv.getSlotManager().getUnsortableSlots().stream().mapToInt(i -> i).toArray()));
                inv.getSlotManager().setSelectorActive(SlotManager.UNSORTABLE, !inv.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE));
            }
            playUIClickSound();
            return true;
        }

        if(QUICK_STACK_BUTTON.inButton(this, (int)mouseX, (int)mouseY, 76))
        {
            TravelersBackpack.NETWORK.sendToServer(new SSorterPacket(inv.getScreenID(), InventorySorter.QUICK_STACK, BackpackUtils.isShiftPressed()));
            playUIClickSound();
            return true;
        }

        if(TRANSFER_TO_BACKPACK_BUTTON.inButton(this, (int)mouseX, (int)mouseY, 87))
        {
            TravelersBackpack.NETWORK.sendToServer(new SSorterPacket(inv.getScreenID(), InventorySorter.TRANSFER_TO_BACKPACK, BackpackUtils.isShiftPressed()));
            playUIClickSound();
            return true;
        }

        if(TRANSFER_TO_PLAYER_BUTTON.inButton(this, (int)mouseX, (int)mouseY, 98))
        {
            TravelersBackpack.NETWORK.sendToServer(new SSorterPacket(inv.getScreenID(), InventorySorter.TRANSFER_TO_PLAYER, BackpackUtils.isShiftPressed()));
            playUIClickSound();
            return true;
        }

        if(MEMORY_BUTTON.inButton(this, (int)mouseX, (int)mouseY, 109))
        {
            //Turns slot checking on server
            TravelersBackpack.NETWORK.sendToServer(new SSorterPacket(inv.getScreenID(), InventorySorter.MEMORY, BackpackUtils.isShiftPressed()));

            //Turns slot checking on client
            TravelersBackpack.NETWORK.sendToServer(new SMemoryPacket(inv.getScreenID(), inv.getSlotManager().isSelectorActive(SlotManager.MEMORY), inv.getSlotManager().getMemorySlots().stream().mapToInt(Pair::getFirst).toArray(), inv.getSlotManager().getMemorySlots().stream().map(Pair::getSecond).toArray(ItemStack[]::new)));
            inv.getSlotManager().setSelectorActive(SlotManager.MEMORY, !inv.getSlotManager().isSelectorActive(SlotManager.MEMORY));

            playUIClickSound();
            return true;
        }

        if(!inv.getLeftTank().isEmpty())
        {
            if(this.tankLeft.inTank(this, (int)mouseX, (int)mouseY) && BackpackUtils.isShiftPressed())
            {
                TravelersBackpack.NETWORK.sendToServer(new SSpecialActionPacket(inv.getScreenID(), Reference.EMPTY_TANK, 1));

                if(inv.getScreenID() == Reference.ITEM_SCREEN_ID) ServerActions.emptyTank(1, inventory.player, inv.getLevel(), inv.getScreenID());
            }
        }

        if(!inv.getRightTank().isEmpty())
        {
            if(this.tankRight.inTank(this, (int)mouseX, (int)mouseY) && BackpackUtils.isShiftPressed())
            {
                TravelersBackpack.NETWORK.sendToServer(new SSpecialActionPacket(inv.getScreenID(), Reference.EMPTY_TANK, 2));

                if(inv.getScreenID() == Reference.ITEM_SCREEN_ID) ServerActions.emptyTank(2, inventory.player, inv.getLevel(), inv.getScreenID());
            }
        }

        if(inv.hasTileEntity())
        {
            if(BED_BUTTON.inButton(this, (int)mouseX, (int)mouseY))
            {
                TravelersBackpack.NETWORK.sendToServer(new SSleepingBagPacket(inv.getPosition()));
                return true;
            }

            if(BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, inv.getItemStack()) && ABILITY_SLIDER.inButton(this, (int)mouseX, (int)mouseY))
            {
                TravelersBackpack.NETWORK.sendToServer(new SAbilitySliderPacket(inv.getScreenID(), !inv.getAbilityValue()));
                playUIClickSound();
                return true;
            }
        }

        if(!inv.hasTileEntity())
        {
            if(!TravelersBackpack.enableCurios())
            {
                if(!CapabilityUtils.isWearingBackpack(inventory.player) && this.screenID == Reference.ITEM_SCREEN_ID)
                {
                    if(EQUIP_BUTTON.inButton(this, (int)mouseX, (int)mouseY))
                    {
                        TravelersBackpack.NETWORK.sendToServer(new SEquipBackpackPacket(true));
                        return true;
                    }
                }

                if(CapabilityUtils.isWearingBackpack(inventory.player) && this.screenID == Reference.WEARABLE_SCREEN_ID)
                {
                    if(UNEQUIP_BUTTON.inButton(this, (int)mouseX, (int)mouseY))
                    {
                        TravelersBackpack.NETWORK.sendToServer(new SEquipBackpackPacket(false));
                        return true;
                    }
                }
            }

            if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, inv.getItemStack()) && ABILITY_SLIDER.inButton(this, (int)mouseX, (int)mouseY))
            {
                TravelersBackpack.NETWORK.sendToServer(new SAbilitySliderPacket(inv.getScreenID(), !inv.getAbilityValue()));
                playUIClickSound();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void playUIClickSound()
    {
        inventory.player.level.playSound(inventory.player, inventory.player.blockPosition(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.MASTER, 1.0F, 1.0F);
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_)
    {
        if(ModClientEventHandler.OPEN_INVENTORY.isActiveAndMatches(InputMappings.getKey(p_keyPressed_1_, p_keyPressed_2_)))
        {
            ClientPlayerEntity playerEntity = this.getMinecraft().player;

            if(playerEntity != null)
            {
                this.onClose();
            }
            return true;
        }
        return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
    }
}