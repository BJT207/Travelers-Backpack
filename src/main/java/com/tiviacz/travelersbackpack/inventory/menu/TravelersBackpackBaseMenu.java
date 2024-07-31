package com.tiviacz.travelersbackpack.inventory.menu;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.CraftingContainerImproved;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.menu.slot.BackpackSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.menu.slot.FluidSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.menu.slot.ResultSlotExt;
import com.tiviacz.travelersbackpack.inventory.menu.slot.ToolSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.network.ClientboundUpdateRecipePacket;
import com.tiviacz.travelersbackpack.util.ItemStackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;

public class TravelersBackpackBaseMenu extends AbstractContainerMenu
{
    public Inventory inventory;
    public ITravelersBackpackContainer container;
    public CraftingContainerImproved craftSlots;
    public ResultContainer resultSlots = new ResultContainer();

    private final int BACKPACK_INV_START = 1, BACKPACK_INV_END;
    private final int TOOL_START, TOOL_END;
    private final int BUCKET_LEFT_IN, BUCKET_LEFT_OUT;
    private final int BUCKET_RIGHT_IN, BUCKET_RIGHT_OUT;
    private final int PLAYER_INV_START, PLAYER_HOT_END;
    private final int CRAFTING_GRID_START, CRAFTING_GRID_END;

    public TravelersBackpackBaseMenu(final MenuType<?> type, final int windowID, final Inventory inventory, final ITravelersBackpackContainer container)
    {
        super(type, windowID);
        this.inventory = inventory;
        this.container = container;
        this.craftSlots = new CraftingContainerImproved(container, this);

        //Craft result = 0;
        this.BACKPACK_INV_END = BACKPACK_INV_START + container.getHandler().getSlots() - 1;
        this.TOOL_START = BACKPACK_INV_END + 1;
        this.TOOL_END = TOOL_START + container.getToolSlotsHandler().getSlots() - 1;
        this.BUCKET_LEFT_IN = TOOL_END + 1;
        this.BUCKET_LEFT_OUT = BUCKET_LEFT_IN + 1;
        this.BUCKET_RIGHT_IN = BUCKET_LEFT_OUT + 1;
        this.BUCKET_RIGHT_OUT = BUCKET_RIGHT_IN + 1;
        this.CRAFTING_GRID_START = BUCKET_RIGHT_OUT + 1;
        this.CRAFTING_GRID_END = CRAFTING_GRID_START + 8;
        this.PLAYER_INV_START = CRAFTING_GRID_END + 1;
        this.PLAYER_HOT_END =  PLAYER_INV_START + 35;

        //Craft Result
        this.addCraftResult();

        //Backpack Inventory
        this.addBackpackInventory(container);

        //Functional Slots
        this.addToolSlots(container);
        this.addFluidSlots(container);

        //Crafting Widget
        this.addCraftingSlots();

        //Player Inventory
        this.addPlayerInventoryAndHotbar(inventory, inventory.selected);
    }

    public void addCraftResult()
    {
        this.addSlot(new ResultSlotExt(container, inventory.player, this.craftSlots, this.resultSlots, 0, 270, 113));
    }

    public void addBackpackInventory(ITravelersBackpackContainer container)
    {
        int slot = 0;

        for(int i = 0; i < container.getRows(); i++)
        {
            int lastRowSlots = container.getHandler().getSlots() % 9;
            if(lastRowSlots == 0) lastRowSlots = 9;

            int slotsInRow = i == container.getRows() - 1 ? lastRowSlots : 9;

            for(int j = 0; j < slotsInRow; j++)
            {
                this.addSlot(new BackpackSlotItemHandler(container.getHandler(), slot++, 44 + j * 18, 7 + i * 18));
            }
        }
    }

    public void addFluidSlots(ITravelersBackpackContainer container)
    {
        //Left In bucket
        this.addSlot(new FluidSlotItemHandler(container, 0, 6, 7)
        {
            @Override
            public boolean isActive()
            {
                return !TravelersBackpackBaseMenu.this.container.getSettingsManager().showToolSlots() && super.isActive();
            }
        });

        //Left Out bucket
        this.addSlot(new FluidSlotItemHandler(container, 1, 6, 37)
        {
            @Override
            public boolean isActive()
            {
                return !TravelersBackpackBaseMenu.this.container.getSettingsManager().showToolSlots() && super.isActive();
            }
        });

        //Right In bucket
        this.addSlot(new FluidSlotItemHandler(container, 2, 226, 7));

        //Right Out bucket
        this.addSlot(new FluidSlotItemHandler(container, 3, 226, 37));
    }

    public void addToolSlots(ITravelersBackpackContainer container)
    {
        for(int i = 0; i < container.getToolSlotsHandler().getSlots(); i++)
        {
            this.addSlot(new ToolSlotItemHandler(inventory.player, container, i, 6, 7 + 18 * i));
        }
    }

    public void addCraftingSlots()
    {
        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 3; j++)
            {
                this.addSlot(new Slot(this.craftSlots, j + i * 3, 252 + j * 18, 47 + i * 18)
                {
                    @Override
                    public boolean isActive()
                    {
                        return TravelersBackpackBaseMenu.this.container.getSettingsManager().showCraftingGrid();
                    }
                });
            }
        }
    }

    public void addPlayerInventoryAndHotbar(Inventory inventory, int currentItemIndex)
    {
        for(int y = 0; y < 3; y++)
        {
            for(int x = 0; x < 9; x++)
            {
                this.addSlot(new Slot(inventory, x + y * 9 + 9, 44 + x*18, (71 + this.container.getYOffset()) + y*18));
            }
        }

        for(int x = 0; x < 9; x++)
        {
            this.addSlot(new Slot(inventory, x, 44 + x*18, 129 + this.container.getYOffset()));
        }
    }

    protected void canCraft(Level level, Player player)
    {
        if(container.getSettingsManager().hasCraftingGrid())
        {
            slotChangedCraftingGrid(level, player);
        }
    }

    @Override
    public void slotsChanged(Container container)
    {
        super.slotsChanged(container);
        canCraft(inventory.player.level(), inventory.player);
    }

    @Override
    public void sendAllDataToRemote()
    {
        super.sendAllDataToRemote();

        //Sync on opening
        canCraft(inventory.player.level(), inventory.player);
        ///this.slotsChanged(new RecipeWrapper(container.getCraftingGridHandler()));
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot)
    {
        return slot.container != this.resultSlots && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index)
    {
        Slot slot = getSlot(index);
        ItemStack result = ItemStack.EMPTY;

        if(slot != null && slot.hasItem())
        {
            ItemStack stack = slot.getItem();
            result = stack.copy();

            if(index >= 0 && index <= CRAFTING_GRID_END) //BUCKETRIGHTOUT??
            {
                if(index == 0)
                {
                    return handleShiftCraft(player, slot);
                }

                else if(!moveItemStackTo(stack, PLAYER_INV_START, PLAYER_HOT_END + 1, true))
                {
                    return ItemStack.EMPTY;
                }
            }

            if(index >= PLAYER_INV_START)
            {
                //Check Memory Slots
                if(!container.getSlotManager().getMemorySlots().isEmpty())
                {
                    for(Pair<Integer, ItemStack> pair : container.getSlotManager().getMemorySlots())
                    {
                        if(ItemStackUtils.isSameItemSameTags(pair.getSecond(), stack) && getSlot(pair.getFirst() + 1).getItem().getCount() != getSlot(pair.getFirst() + 1).getItem().getMaxStackSize())
                        {
                            if(moveItemStackTo(stack, pair.getFirst() + 1, pair.getFirst() + 2, false))
                            {
                                break;
                            }
                        }
                    }
                }

                if(ToolSlotItemHandler.isValid(stack))
                {
                    if(!moveItemStackTo(stack, TOOL_START, TOOL_END + 1, false))
                    {
                        if(!moveItemStackTo(stack, BACKPACK_INV_START, BACKPACK_INV_END + 1, false))
                        {
                            return ItemStack.EMPTY;
                        }
                    }
                }

                if(!moveItemStackTo(stack, BACKPACK_INV_START, BACKPACK_INV_END + 1, false))
                {
                    return ItemStack.EMPTY;
                }
            }

            if(stack.isEmpty())
            {
                slot.set(ItemStack.EMPTY);
            }

            else
            {
                slot.setChanged();
            }

            if(stack.getCount() == result.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }
        return result;
    }

    public ItemStack handleShiftCraft(Player player, Slot resultSlot)
    {
        ItemStack outputCopy = ItemStack.EMPTY;
        CraftingInput input = craftSlots.asCraftInput();

        if (resultSlot != null && resultSlot.hasItem()) {
            craftSlots.checkChanges = false;
            RecipeHolder<CraftingRecipe> recipe = (RecipeHolder<CraftingRecipe>) resultSlots.getRecipeUsed();
            while (recipe != null && recipe.value().matches(input, player.level())) {
                ItemStack recipeOutput = recipe.value().assemble(input, player.level().registryAccess());
                //#TODO
                if (recipeOutput.isEmpty()) {
                    throw new RuntimeException("A recipe matched but produced an empty output - Offending Recipe : " + recipe.id() + " - This is NOT a bug in Traveler's Backpack!");
                }
                //#TODO
                outputCopy = recipeOutput.copy();

                recipeOutput.onCraftedBy(player.level(), player, 1);
                EventHooks.firePlayerCraftingEvent(player, recipeOutput, craftSlots);

                if(!player.level().isClientSide)
                {
                    if(container.getSettingsManager().shiftClickToBackpack())
                    {
                        //TODO TRANSFER TO MEMORY SLOTS HERE
                        if(!moveItemStackTo(recipeOutput, BACKPACK_INV_START, BACKPACK_INV_END + 1, false))
                        {
                            if(!moveItemStackTo(recipeOutput, PLAYER_INV_START, PLAYER_HOT_END + 1, true))
                            {
                                craftSlots.checkChanges = true;
                                return ItemStack.EMPTY;
                            }
                        }
                    }
                    else
                    {
                        if(!moveItemStackTo(recipeOutput, PLAYER_INV_START, PLAYER_HOT_END + 1, true))
                        {
                            if(!moveItemStackTo(recipeOutput, BACKPACK_INV_START, BACKPACK_INV_END + 1, false))
                            {
                                craftSlots.checkChanges = true;
                                return ItemStack.EMPTY;
                            }
                        }
                    }
                }

                ((ResultSlot) resultSlot).removeCount += outputCopy.getCount();
                // Handles the actual work of removing the input items.
                resultSlot.onTake(player, recipeOutput);
                resetStackedContents(input);
                //#TODO
            }
            craftSlots.checkChanges = true;
            slotChangedCraftingGrid(player.level(), player);

            //#TODO
            //Have to setDataChanged here because it's getting called from client natively
            //if(!player.level().isClientSide) container.setDataChanged(ITravelersBackpackContainer.INVENTORY_DATA);
        }
        return outputCopy;
    }

    //#TODO

    public void resetStackedContents(CraftingInput input)
    {
        StackedContents contents = input.stackedContents();
        contents.clear();
        for(ItemStack i : input.items())
        {
            if(!i.isEmpty())
            {
                contents.accountStack(i, 1);
            }
        }
    }

    public void slotChangedCraftingGrid(Level world, Player player)
    {
        if(!world.isClientSide && craftSlots.checkChanges)
        {
            ItemStack itemstack = ItemStack.EMPTY;
            CraftingInput input = craftSlots.asCraftInput();

            RecipeHolder<CraftingRecipe> oldRecipe = (RecipeHolder<CraftingRecipe>) resultSlots.getRecipeUsed();
            RecipeHolder<CraftingRecipe> recipe = oldRecipe;
            if (recipe == null || !recipe.value().matches(input, world)) recipe = world.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, world).orElse(null);

            if (recipe != null) itemstack = recipe.value().assemble(input, world.registryAccess());

            // Need to check if the output is empty, because if the recipe book is being used, the recipe will already be set.
            if (oldRecipe != recipe || resultSlots.getItem(0).isEmpty()) {
                PacketDistributor.sendToPlayer((ServerPlayer) player, new ClientboundUpdateRecipePacket(recipe, itemstack));
                resultSlots.setItem(0, itemstack);
                resultSlots.setRecipeUsed(recipe);
            }
            else if (recipe != null) {
                // https://github.com/Shadows-of-Fire/FastWorkbench/issues/72 - Some modded recipes may update the output and not mark themselves as special, moderately
                // annoying but... bleh
                if (recipe.value().isSpecial() || !recipe.getClass().getName().startsWith("net.minecraft") && !ItemStack.matches(itemstack, resultSlots.getItem(0))) {
                    PacketDistributor.sendToPlayer((ServerPlayer) player, new ClientboundUpdateRecipePacket(recipe, itemstack));
                    resultSlots.setItem(0, itemstack);
                    resultSlots.setRecipeUsed(recipe);
                }
            }
        }
    }

   /* public void slotChangedCraftingGrid(Level level, Player player)
    {
        if(!level.isClientSide && craftSlots.checkChanges)
        {
            CraftingInput input = craftSlots.asCraftInput();

            ItemStack itemstack = ItemStack.EMPTY;

            RecipeHolder<CraftingRecipe> oldRecipe = (RecipeHolder<CraftingRecipe>)resultSlots.getRecipeUsed();
            RecipeHolder<CraftingRecipe> recipe = oldRecipe;

            if(recipe == null || !recipe.value().matches(input, level))
            {
                recipe = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level).orElse(null);
            }

            if(recipe != null)
            {
                itemstack = recipe.value().assemble(input, level.registryAccess());
            }

            if(oldRecipe != recipe)
            {
                PacketDistributor.sendToPlayer((ServerPlayer)player, new ClientboundUpdateRecipePacket(recipe == null ? ResourceLocation.fromNamespaceAndPath("null", "null") : recipe.id(), itemstack));
                //TravelersBackpack.NETWORK.send(new ClientboundUpdateRecipePacket(recipe, itemstack), PacketDistributor.PLAYER.with((ServerPlayer)player));
                resultSlots.setItem(0, itemstack);
                resultSlots.setRecipeUsed(recipe);
            }
            else if(recipe != null)
            {
                if(recipe.value().isSpecial() || !recipe.getClass().getName().startsWith("net.minecraft") && !ItemStack.matches(itemstack, resultSlots.getItem(0)))
                {
                    PacketDistributor.sendToPlayer((ServerPlayer)player, new ClientboundUpdateRecipePacket(recipe.id(), itemstack));
                    //TravelersBackpack.NETWORK.send(new ClientboundUpdateRecipePacket(recipe, itemstack), PacketDistributor.PLAYER.with((ServerPlayer)player));
                    resultSlots.setItem(0, itemstack);
                    resultSlots.setRecipeUsed(recipe);
                }
            }
        }
    } */

    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, Player player)
    {
        if(container.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE) || container.getSlotManager().isSelectorActive(SlotManager.MEMORY))
        {
            return;
        }
        super.clicked(slotId, dragType, clickType, player);
    }

    @Override
    public void removed(Player player)
    {
        //if(container.getScreenID() == Reference.ITEM_SCREEN_ID)
        //{
         //   this.container.setDataChanged(ITravelersBackpackContainer.ALL_DATA);
        //}

        if(container.getScreenID() == Reference.BLOCK_ENTITY_SCREEN_ID)
        {
            if(container.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE) || container.getSlotManager().isSelectorActive(SlotManager.MEMORY)) container.getSlotManager().setChanged();

            this.container.setUsingPlayer(null);
        }

        if(container.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE)) container.getSlotManager().setSelectorActive(SlotManager.UNSORTABLE, false);
        if(container.getSlotManager().isSelectorActive(SlotManager.MEMORY)) container.getSlotManager().setSelectorActive(SlotManager.MEMORY, false);

        clearSlotsAndPlaySound(player, this.container.getFluidSlotsHandler(), 4);
        shiftTools(this.container);

        if(!TravelersBackpackConfig.SERVER.backpackSettings.craftingUpgrade.savesItems.get())
        {
            clearSlotsAndPlaySound(player, this.container.getCraftingGridHandler(), 9);
        }
        else
        {
            checkCraftingGridAndPlaySound(player);
        }

        super.removed(player);
    }

    public void clearSlotsAndPlaySound(Player player, ItemStackHandler handler, int size)
    {
        boolean playSound = false;

        for(int i = 0; i < size; i++)
        {
            boolean flag = clearSlot(player, handler, i);
            if(flag) playSound = true;
        }

        if(playSound)
        {
            this.playSound(player);
        }
    }

    public boolean clearSlot(Player player, ItemStackHandler handler, int index)
    {
        if(!handler.getStackInSlot(index).isEmpty())
        {
            if(player == null) return false;

            if(!player.isAlive() || (player instanceof ServerPlayer serverPlayer && serverPlayer.hasDisconnected()))
            {
                ItemStack stack = handler.getStackInSlot(index).copy();
                handler.setStackInSlot(index, ItemStack.EMPTY);

                player.drop(stack, false);
                return false;
            }
            else
            {
                ItemStack stack = handler.getStackInSlot(index);
                handler.setStackInSlot(index, ItemStack.EMPTY);

                player.getInventory().placeItemBackInInventory(stack);
                return true;
            }
        }
        return false;
    }

    public void playSound(Player player)
    {
        player.level().playSound(player, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, (1.0F + (player.level().getRandom().nextFloat() - player.level().getRandom().nextFloat()) * 0.2F) * 0.7F);
    }

    public void shiftTools(ITravelersBackpackContainer container)
    {
        boolean foundEmptySlot = false;
        boolean needsShifting = false;

        for(int i = 0; i < container.getToolSlotsHandler().getSlots(); i++)
        {
            if(foundEmptySlot)
            {
                if(!container.getToolSlotsHandler().getStackInSlot(i).isEmpty())
                {
                    needsShifting = true;
                }
            }

            if(container.getToolSlotsHandler().getStackInSlot(i).isEmpty() && !foundEmptySlot)
            {
                foundEmptySlot = true;
            }
        }

        if(needsShifting)
        {
            NonNullList<ItemStack> tools = NonNullList.withSize(container.getToolSlotsHandler().getSlots(), ItemStack.EMPTY);
            int j = 0;

            for(int i = 0; i < container.getToolSlotsHandler().getSlots(); i++)
            {
                if(!container.getToolSlotsHandler().getStackInSlot(i).isEmpty())
                {
                    tools.set(j, container.getToolSlotsHandler().getStackInSlot(i));
                    j++;
                }
            }

            j = 0;

            for(int i = 0; i < container.getToolSlotsHandler().getSlots(); i++)
            {
                if(!tools.isEmpty())
                {
                    container.getToolSlotsHandler().setStackInSlot(i, tools.get(j));
                    j++;
                }
            }
        }
    }

    //Remove forbidden items from crafting grid, if saving enabled
    public void checkCraftingGridAndPlaySound(Player player)
    {
        boolean playSound = false;

        for(int i = 0; i < container.getCraftingGridHandler().getSlots(); i++)
        {
            boolean flag = clearCraftingGridSlot(player, i);
            if(flag) playSound = true;
        }

        if(playSound)
        {
            this.playSound(player);
        }
    }

    public boolean clearCraftingGridSlot(Player player, int index)
    {
        if(!BackpackSlotItemHandler.isItemValid(container.getCraftingGridHandler().getStackInSlot(index)))
        {
            if(player == null) return false;

            if(!player.isAlive() || (player instanceof ServerPlayer serverPlayer && serverPlayer.hasDisconnected()))
            {
                ItemStack stack = container.getCraftingGridHandler().getStackInSlot(index).copy();
                container.getCraftingGridHandler().setStackInSlot(index, ItemStack.EMPTY);

                player.drop(stack, false);
                return false;
            }
            else
            {
                ItemStack stack = container.getCraftingGridHandler().getStackInSlot(index);
                container.getCraftingGridHandler().setStackInSlot(index, ItemStack.EMPTY);

                player.getInventory().placeItemBackInInventory(stack);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean stillValid(Player player)
    {
        return true;
    }
}