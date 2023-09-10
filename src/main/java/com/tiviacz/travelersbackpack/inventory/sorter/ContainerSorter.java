package com.tiviacz.travelersbackpack.inventory.sorter;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackBaseMenu;
import com.tiviacz.travelersbackpack.util.ItemStackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class ContainerSorter
{
    public static final byte SORT_BACKPACK = 0;
    public static final byte QUICK_STACK = 1;
    public static final byte TRANSFER_TO_BACKPACK = 2;
    public static final byte TRANSFER_TO_PLAYER = 3;
    public static final byte SORT = 4;
    public static final byte MEMORY = 5;

    public static void selectSort(ITravelersBackpackContainer container, Player player, byte button, boolean shiftPressed)
    {
        if(button == SORT_BACKPACK)
        {
            sortBackpack(container, player, SortType.Type.CATEGORY, shiftPressed);
        }
        else if(button == QUICK_STACK)
        {
            quickStackToBackpackNoSort(container, player, shiftPressed);
        }
        else if(button == TRANSFER_TO_BACKPACK)
        {
            transferToBackpackNoSort(container, player, shiftPressed);
        }
        else if(button == TRANSFER_TO_PLAYER)
        {
            transferToPlayer(container, player);
        }
        else if(button == SORT)
        {
            setUnsortable(container, player, shiftPressed);
        }
        else if(button == MEMORY)
        {
            setMemory(container, player, shiftPressed);
        }
    }

    public static void sortBackpack(ITravelersBackpackContainer container, Player player, SortType.Type type, boolean shiftPressed)
    {
        //if(shiftPressed)
        ////{
        //    container.getSlotManager().setSelectorActive(SlotManager.UNSORTABLE, !container.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE));
        //}
        if(!container.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE))
        {
            List<ItemStack> stacks = new ArrayList<>();
            //CustomRangedWrapper rangedWrapper = new CustomRangedWrapper(container, container.getSettingsManager().isCraftingGridLocked() ? container.getHandler() : container.getCombinedHandler(), 0, container.getSettingsManager().isCraftingGridLocked() ? container.getTier().getStorageSlots() : container.getTier().getStorageSlotsWithCrafting());

            CustomRangedWrapper rangedWrapper = new CustomRangedWrapper(container, container.getCombinedHandler(), 0, container.getTier().getStorageSlotsWithCrafting());

            for(int i = 0; i < rangedWrapper.getSlots(); i++)
            {
                if(container.getSettingsManager().isCraftingGridLocked() && container.getSlotManager().isSlot(SlotManager.CRAFTING, i)) continue;

                addStackWithMerge(stacks, container.getSlotManager().isSlot(SlotManager.UNSORTABLE, i) ? ItemStack.EMPTY : rangedWrapper.getStackInSlot(i));
            }

            if(!stacks.isEmpty())
            {
                stacks.sort(Comparator.comparing(stack -> SortType.getStringForSort(stack, type)));
            }

            if(stacks.size() == 0) return;

            int j = 0;

            for(int i = 0; i < rangedWrapper.getSlots(); i++)
            {
                if(container.getSettingsManager().isCraftingGridLocked() && container.getSlotManager().isSlot(SlotManager.CRAFTING, i)) continue;

                if(container.getSlotManager().isSlot(SlotManager.UNSORTABLE, i)) continue;

                rangedWrapper.setStackInSlot(i, j < stacks.size() ? stacks.get(j) : ItemStack.EMPTY);
                j++;
            }
            if(player.containerMenu instanceof TravelersBackpackBaseMenu menu)
            {
                menu.slotsChanged(menu.craftSlots);
            }
            container.setDataChanged(ITravelersBackpackContainer.COMBINED_INVENTORY_DATA);
        }
    }

    public static void quickStackToBackpackNoSort(ITravelersBackpackContainer container, Player player, boolean shiftPressed)
    {
        IItemHandler playerStacks = new InvWrapper(player.getInventory());

        for(int i = shiftPressed ? 0 : 9; i < 36; ++i)
        {
            ItemStack playerStack = playerStacks.getStackInSlot(i);
            if(playerStack.isEmpty() || (container.getScreenID() == Reference.ITEM_SCREEN_ID && i == player.getInventory().selected)) continue;
            CustomRangedWrapper rangedWrapper = new CustomRangedWrapper(container, container.getSettingsManager().isCraftingGridLocked() ? container.getHandler() : container.getCombinedHandler(), 0, container.getSettingsManager().isCraftingGridLocked() ? container.getTier().getStorageSlots() : container.getTier().getStorageSlotsWithCrafting());

            boolean hasExistingStack = IntStream.range(0, rangedWrapper.getSlots()).mapToObj(rangedWrapper::getStackInSlot).filter(existing -> !existing.isEmpty()).anyMatch(existing -> existing.getItem() == playerStack.getItem());
            if(!hasExistingStack) continue;

            ItemStack ext = playerStacks.extractItem(i, Integer.MAX_VALUE, false);

            for(int j = 0; j < rangedWrapper.getSlots(); ++j)
            {
                ext = rangedWrapper.insertItem(j, ext, false);
                if(ext.isEmpty()) break;
            }

            if(!ext.isEmpty())
            {
                playerStacks.insertItem(i, ext, false);
            }
        }

        if(player.containerMenu instanceof TravelersBackpackBaseMenu menu)
        {
            menu.slotsChanged(menu.craftSlots);
        }
    }

    public static void transferToBackpackNoSort(ITravelersBackpackContainer container, Player player, boolean shiftPressed)
    {
        IItemHandler playerStacks = new InvWrapper(player.getInventory());

        //Run for Memory Slots
        if(!container.getSlotManager().getMemorySlots().isEmpty())
        {
            for(Pair<Integer, ItemStack> pair : container.getSlotManager().getMemorySlots())
            {
                if(container.getSettingsManager().isCraftingGridLocked())
                {
                    int i = pair.getFirst();
                    int firstCraftSlot = (container.getTier().getStorageSlots() - Tiers.LEATHER.getStorageSlots()) + 5;
                    if(i == firstCraftSlot || i == firstCraftSlot + 1 || i == firstCraftSlot + 2 ||
                            i == firstCraftSlot + 8 || i == firstCraftSlot + 9 || i == firstCraftSlot + 10 ||
                            i == firstCraftSlot + 16 || i == firstCraftSlot + 17 || i == firstCraftSlot + 18)
                    {
                        continue;
                    }
                }

                for(int i = shiftPressed ? 0 : 9; i < 36; ++i)
                {
                    ItemStack playerStack = playerStacks.getStackInSlot(i);

                    if(playerStack.isEmpty() || (container.getScreenID() == Reference.ITEM_SCREEN_ID && i == player.getInventory().selected)) continue;
                    CustomRangedWrapper rangedWrapper = new CustomRangedWrapper(container, container.getCombinedHandler(), 0, container.getTier().getStorageSlotsWithCrafting());

                    ItemStack extSimulate = playerStacks.extractItem(i, Integer.MAX_VALUE, true);
                    ItemStack ext = ItemStack.EMPTY; //playerStacks.extractItem(i, Integer.MAX_VALUE, false);

                    if(ItemStackUtils.isSameItemSameTags(pair.getSecond(), extSimulate))
                    {
                        ext = playerStacks.extractItem(i, Integer.MAX_VALUE, false);

                        ext = rangedWrapper.insertItem(pair.getFirst(), ext, false);
                        if(ext.isEmpty()) continue;
                    }

                    if(!ext.isEmpty())
                    {
                        playerStacks.insertItem(i, ext, false);
                    }
                }
            }
        }

        //Run for Normal Slots
        for(int i = shiftPressed ? 0 : 9; i < 36; ++i)
        {
            ItemStack playerStack = playerStacks.getStackInSlot(i);

            if(playerStack.isEmpty() || (container.getScreenID() == Reference.ITEM_SCREEN_ID && i == player.getInventory().selected)) continue;
            CustomRangedWrapper rangedWrapper = new CustomRangedWrapper(container, container.getSettingsManager().isCraftingGridLocked() ? container.getHandler() : container.getCombinedHandler(), 0, container.getSettingsManager().isCraftingGridLocked() ? container.getTier().getStorageSlots() : container.getTier().getStorageSlotsWithCrafting());

            ItemStack ext = playerStacks.extractItem(i, Integer.MAX_VALUE, false);

            for(int j = 0; j < rangedWrapper.getSlots(); ++j)
            {
                ext = rangedWrapper.insertItem(j, ext, false);
                if(ext.isEmpty()) break;
            }

            if(!ext.isEmpty())
            {
                playerStacks.insertItem(i, ext, false);
            }
        }

        if(player.containerMenu instanceof TravelersBackpackBaseMenu menu)
        {
            menu.slotsChanged(menu.craftSlots);
        }
    }

    public static void transferToPlayer(ITravelersBackpackContainer container, Player player)
    {
        IItemHandler playerStacks = new InvWrapper(player.getInventory());
        CustomRangedWrapper rangedWrapper = new CustomRangedWrapper(container, container.getSettingsManager().isCraftingGridLocked() ? container.getHandler() : container.getCombinedHandler(), 0, container.getSettingsManager().isCraftingGridLocked() ? container.getTier().getStorageSlots() : container.getTier().getStorageSlotsWithCrafting());

        for(int i = 0; i < rangedWrapper.getSlots(); ++i)
        {
            ItemStack stack = rangedWrapper.getStackInSlot(i);

            if(stack.isEmpty()) continue;

            ItemStack ext = rangedWrapper.extractItem(i, Integer.MAX_VALUE, false);

            for(int j = 9; j < 36; ++j)
            {
                ext = playerStacks.insertItem(j, ext, false);
                if(ext.isEmpty()) break;
            }

            if(!ext.isEmpty())
            {
                rangedWrapper.isTransferToPlayer = true;
                rangedWrapper.insertItem(i, ext, false);
                rangedWrapper.isTransferToPlayer = false;
            }
        }

        if(player.containerMenu instanceof TravelersBackpackBaseMenu menu)
        {
            menu.slotsChanged(menu.craftSlots);
        }
    }

    public static void setUnsortable(ITravelersBackpackContainer container, Player player, boolean shiftPressed)
    {
        container.getSlotManager().setSelectorActive(SlotManager.UNSORTABLE, !container.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE));
    }

    public static void setMemory(ITravelersBackpackContainer container, Player player, boolean shiftPressed)
    {
        container.getSlotManager().setSelectorActive(SlotManager.MEMORY, !container.getSlotManager().isSelectorActive(SlotManager.MEMORY));
    }

    private static void addStackWithMerge(List<ItemStack> stacks, ItemStack newStack)
    {
        if(newStack.isEmpty()) return;

        if(newStack.isStackable() && newStack.getCount() != newStack.getMaxStackSize())
        {
            for(int j = stacks.size() - 1; j >= 0; j--)
            {
                ItemStack oldStack = stacks.get(j);

                if(canMergeItems(newStack, oldStack))
                {
                    combineStacks(newStack, oldStack);

                    if(oldStack.isEmpty() || oldStack.getCount() == 0)
                    {
                        stacks.remove(j);
                    }
                }
            }
        }
        stacks.add(newStack);
    }

    private static void combineStacks(ItemStack stack, ItemStack stack2)
    {
        if(stack.getMaxStackSize() >= stack.getCount() + stack2.getCount())
        {
            stack.grow(stack2.getCount());
            stack2.setCount(0);
        }

        int maxInsertAmount = Math.min(stack.getMaxStackSize() - stack.getCount(), stack2.getCount());
        stack.grow(maxInsertAmount);
        stack2.shrink(maxInsertAmount);
    }

    private static boolean canMergeItems(ItemStack stack1, ItemStack stack2)
    {
        if(!stack1.isStackable() || !stack2.isStackable())
        {
            return false;
        }
        if(stack1.getCount() == stack2.getMaxStackSize() || stack2.getCount() == stack2.getMaxStackSize())
        {
            return false;
        }
        if(stack1.getItem() != stack2.getItem())
        {
            return false;
        }
        if(stack1.getDamageValue() != stack2.getDamageValue())
        {
            return false;
        }
        return ItemStack.isSameItemSameTags(stack1, stack2);
    }

    public static class CustomRangedWrapper extends RangedWrapper
    {
        private final ITravelersBackpackContainer container;
        public boolean isTransferToPlayer;

        public CustomRangedWrapper(ITravelersBackpackContainer container, IItemHandlerModifiable compose, int minSlot, int maxSlotExclusive)
        {
            this(container, compose, minSlot, maxSlotExclusive, false);
        }

        public CustomRangedWrapper(ITravelersBackpackContainer container, IItemHandlerModifiable compose, int minSlot, int maxSlotExclusive, boolean isTransferToPlayer)
        {
            super(compose, minSlot, maxSlotExclusive);
            this.container = container;
            this.isTransferToPlayer = isTransferToPlayer;
        }

        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
        {
            if(container.getSlotManager().isSlot(SlotManager.MEMORY, slot))
            {
                return container.getSlotManager().getMemorySlots().stream().noneMatch(pair -> pair.getFirst() == slot && ItemStackUtils.isSameItemSameTags(pair.getSecond(), stack)) && !isTransferToPlayer ? stack : super.insertItem(slot, stack, simulate);
            }
            return container.getSlotManager().isSlot(SlotManager.UNSORTABLE, slot) ? stack : super.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            return container.getSlotManager().isSlot(SlotManager.UNSORTABLE, slot) ? ItemStack.EMPTY : super.extractItem(slot, amount, simulate);
        }
    }
}