package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackItemScreenHandler;
import com.tiviacz.travelersbackpack.inventory.screen.slot.BackpackSlot;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.inventory.sorter.wrappers.CombinedInvWrapper;
import com.tiviacz.travelersbackpack.inventory.sorter.wrappers.RangedWrapper;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.InventoryUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.util.NbtType;
import net.fabricmc.fabric.impl.transfer.fluid.FluidVariantImpl;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TravelersBackpackInventory implements ITravelersBackpackInventory
{
    private InventoryImproved inventory = createInventory(Tiers.LEATHER.getAllSlots());
    private InventoryImproved craftingInventory = createInventory(Reference.CRAFTING_GRID_SIZE);
    private InventoryImproved fluidSlots = createTemporaryInventory();
    public SingleVariantStorage<FluidVariant> leftTank = createFluidTank(Tiers.LEATHER.getTankCapacity());
    public SingleVariantStorage<FluidVariant> rightTank = createFluidTank(Tiers.LEATHER.getTankCapacity());
    private final SlotManager slotManager = new SlotManager(this);
    private final SettingsManager settingsManager = new SettingsManager(this);
    private final PlayerEntity player;
    private ItemStack stack;
    private Tiers.Tier tier;
    private boolean ability;
    private int lastTime;
    private final byte screenID;

    private final String INVENTORY = "Inventory";
    private final String CRAFTING_INVENTORY = "CraftingInventory";
    private final String LEFT_TANK = "LeftTank";
    private final String LEFT_TANK_AMOUNT = "LeftTankAmount";
    private final String RIGHT_TANK = "RightTank";
    private final String RIGHT_TANK_AMOUNT = "RightTankAmount";
    private final String COLOR = "Color";
    private final String SLEEPING_BAG_COLOR = "SleepingBagColor";
    private final String ABILITY = "Ability";
    private final String LAST_TIME = "LastTime";

    public TravelersBackpackInventory(@Nullable ItemStack stack, PlayerEntity player, byte screenID)
    {
        this.player = player;
        this.stack = stack;
        this.screenID = screenID;

        if(stack != null)
        {
            this.readAllData(stack.getOrCreateTag());
        }
    }

    public void readTier(NbtCompound compound)
    {
        if(!compound.contains(Tiers.TIER))
        {
            compound.putInt(Tiers.TIER, TravelersBackpackConfig.enableTierUpgrades ? Tiers.LEATHER.getOrdinal() : Tiers.DIAMOND.getOrdinal());
        }
        if(compound.contains(Tiers.TIER, NbtType.STRING))
        {
            Tiers.Tier tier = Tiers.of(compound.getString(Tiers.TIER));
            compound.remove(Tiers.TIER);
            compound.putInt(Tiers.TIER, tier.getOrdinal());
        }
        this.tier = Tiers.of(compound.getInt(Tiers.TIER));
    }

    public void setStack(ItemStack stack)
    {
        this.stack = stack;
    }

    @Override
    public InventoryImproved getInventory()
    {
        return this.inventory;
    }

    @Override
    public InventoryImproved getCraftingGridInventory()
    {
        return this.craftingInventory;
    }

    @Override
    public InventoryImproved getFluidSlotsInventory()
    {
        return this.fluidSlots;
    }

    @Override
    public Inventory getCombinedInventory()
    {
        RangedWrapper additional = null;
        if(this.tier != Tiers.LEATHER)
        {
            additional = new RangedWrapper(this, getInventory(), 0, this.tier.getStorageSlots() - 18);
        }
        if(additional != null)
        {
            return new CombinedInvWrapper(this,
                    additional,
                    new RangedWrapper(this, getInventory(), additional.size(), additional.size() + 6),
                    new RangedWrapper(this, getCraftingGridInventory(), 0, 3),
                    new RangedWrapper(this, getInventory(), additional.size() + 6, additional.size() + 12),
                    new RangedWrapper(this, getCraftingGridInventory(), 3, 6),
                    new RangedWrapper(this, getInventory(), additional.size() + 12, additional.size() + 18),
                    new RangedWrapper(this, getCraftingGridInventory(), 6, 9));
        }
        else
        {
            return new CombinedInvWrapper(this,
                    new RangedWrapper(this, getInventory(), 0, 6),
                    new RangedWrapper(this, getCraftingGridInventory(), 0, 3),
                    new RangedWrapper(this, getInventory(), 6, 12),
                    new RangedWrapper(this, getCraftingGridInventory(), 3, 6),
                    new RangedWrapper(this, getInventory(), 12, 18),
                    new RangedWrapper(this, getCraftingGridInventory(), 6, 9));
        }
    }

    @Override
    public SingleVariantStorage<FluidVariant> getLeftTank()
    {
        return leftTank;
    }

    @Override
    public SingleVariantStorage<FluidVariant> getRightTank() {
        return rightTank;
    }

    @Override
    public void writeAllData(NbtCompound compound)
    {
        writeItems(compound);
        writeTanks(compound);
        writeAbility(compound);
        writeTime(compound);
        slotManager.writeUnsortableSlots(compound);
        slotManager.writeMemorySlots(compound);
        settingsManager.writeSettings(compound);
    }

    @Override
    public void readAllData(NbtCompound compound)
    {
        readTier(compound);
        readItems(compound);
        readTanks(compound);
        readAbility(compound);
        readTime(compound);
        slotManager.readUnsortableSlots(compound);
        slotManager.readMemorySlots(compound);
        settingsManager.readSettings(compound);
    }

    @Override
    public void writeItems(NbtCompound compound)
    {
        InventoryUtils.writeNbt(compound, this.inventory.getStacks(), true, false);
        InventoryUtils.writeNbt(compound, this.craftingInventory.getStacks(), true, true);
    }

    @Override
    public void readItems(NbtCompound compound)
    {
        this.inventory = createInventory(this.tier.getAllSlots());
        this.craftingInventory = createInventory(Reference.CRAFTING_GRID_SIZE);
        InventoryUtils.readNbt(compound, this.inventory.getStacks(), false);
        InventoryUtils.readNbt(compound, this.craftingInventory.getStacks(), true);
    }

    @Override
    public void writeTanks(NbtCompound compound)
    {
        compound.put(LEFT_TANK, getLeftTank().variant.toNbt());
        compound.put(RIGHT_TANK, getRightTank().variant.toNbt());
        compound.putLong(LEFT_TANK_AMOUNT, getLeftTank().amount);
        compound.putLong(RIGHT_TANK_AMOUNT, getRightTank().amount);
    }

    @Override
    public void readTanks(NbtCompound compound)
    {
        this.leftTank.variant = FluidVariantImpl.fromNbt(compound.getCompound(LEFT_TANK));
        this.rightTank.variant = FluidVariantImpl.fromNbt(compound.getCompound(RIGHT_TANK));
        this.leftTank.amount = compound.getLong(LEFT_TANK_AMOUNT);
        this.rightTank.amount = compound.getLong(RIGHT_TANK_AMOUNT);
    }

    @Override
    public void writeColor(NbtCompound compound) {}
    @Override
    public void readColor(NbtCompound compound) {}

    @Override
    public void writeSleepingBagColor(NbtCompound compound) {}

    @Override
    public void readSleepingBagColor(NbtCompound compound) {}

    @Override
    public void writeAbility(NbtCompound compound)
    {
        compound.putBoolean(ABILITY, this.ability);
    }

    @Override
    public void readAbility(NbtCompound compound)
    {
        this.ability = !compound.contains(ABILITY) && TravelersBackpackConfig.forceAbilityEnabled || compound.getBoolean(ABILITY);
    }

    @Override
    public void writeTime(NbtCompound compound)
    {
        compound.putInt(LAST_TIME, this.lastTime);
    }

    @Override
    public void readTime(NbtCompound compound)
    {
        this.lastTime = compound.getInt(LAST_TIME);
    }

    @Override
    public boolean updateTankSlots()
    {
        return InventoryActions.transferContainerTank(this, getLeftTank(), this.tier.getSlotIndex(Tiers.SlotType.BUCKET_IN_LEFT), this.player) || InventoryActions.transferContainerTank(this, getRightTank(), this.tier.getSlotIndex(Tiers.SlotType.BUCKET_IN_RIGHT), this.player);
    }

    public void sendPackets()
    {
        if(screenID == Reference.WEARABLE_SCREEN_ID)
        {
            ComponentUtils.sync(this.player);
            ComponentUtils.syncToTracking(player);
        }
    }

    @Override
    public boolean hasColor()
    {
        return stack.getOrCreateTag().contains(COLOR);
    }

    @Override
    public int getColor()
    {
        if(hasColor())
        {
            return stack.getOrCreateTag().getInt(COLOR);
        }
        return 0;
    }

    public boolean hasSleepingBagColor()
    {
        return this.stack.getOrCreateTag().contains(SLEEPING_BAG_COLOR);
    }

    @Override
    public int getSleepingBagColor()
    {
        if(hasSleepingBagColor())
        {
            return this.stack.getOrCreateTag().getInt(SLEEPING_BAG_COLOR);
        }
        return DyeColor.RED.getId();
    }

    @Override
    public boolean getAbilityValue()
    {
        return TravelersBackpackConfig.enableBackpackAbilities ? (BackpackAbilities.ALLOWED_ABILITIES.contains(getItemStack().getItem()) ? this.ability : false) : false;
    }

    @Override
    public void setAbility(boolean value)
    {
        this.ability = value;
    }

    @Override
    public int getLastTime()
    {
        return this.lastTime;
    }

    @Override
    public void setLastTime(int time)
    {
        this.lastTime = time;
    }

    @Override
    public boolean hasTileEntity()
    {
        return false;
    }

    @Override
    public boolean isSleepingBagDeployed()
    {
        return false;
    }

    @Override
    public SlotManager getSlotManager()
    {
        return slotManager;
    }

    @Override
    public SettingsManager getSettingsManager()
    {
        return settingsManager;
    }

    @Override
    public Tiers.Tier getTier()
    {
        return this.tier;
    }

    @Override
    public World getWorld()
    {
        return this.player.world;
    }

    @Override
    public BlockPos getPosition()
    {
        return this.player.getBlockPos();
    }

    @Override
    public byte getScreenID()
    {
        return this.screenID;
    }

    @Override
    public ItemStack getItemStack()
    {
        return this.stack == null ? ItemStack.EMPTY : this.stack;
    }

    @Override
    public void setUsingPlayer(@Nullable PlayerEntity player) {}

    @Override
    public void markDataDirty(byte... dataIds)
    {
        if(getWorld().isClient || stack == null) return;

        for(byte data : dataIds)
        {
            switch(data)
            {
                case INVENTORY_DATA: InventoryUtils.writeNbt(stack.getOrCreateTag(), this.inventory.getStacks(), true, false);
                case CRAFTING_INVENTORY_DATA: InventoryUtils.writeNbt(stack.getOrCreateTag(), this.craftingInventory.getStacks(), true, true);
                case COMBINED_INVENTORY_DATA: writeItems(stack.getOrCreateTag());
                case TANKS_DATA: writeTanks(stack.getOrCreateTag());
                case COLOR_DATA: writeColor(stack.getOrCreateTag());
                case SLEEPING_BAG_COLOR_DATA: writeSleepingBagColor(this.stack.getOrCreateTag());
                case ABILITY_DATA: writeAbility(stack.getOrCreateTag());
                case LAST_TIME_DATA: writeTime(stack.getOrCreateTag());
                case SLOT_DATA: slotManager.writeUnsortableSlots(stack.getOrCreateTag());
                                slotManager.writeMemorySlots(stack.getOrCreateTag());
                case SETTINGS_DATA: settingsManager.writeSettings(stack.getOrCreateTag());
                case ALL_DATA: writeAllData(stack.getOrCreateTag());
            }
        }
        sendPackets();
    }

    @Override
    public void markDirty() {}

    public static void abilityTick(PlayerEntity player, boolean onlyTankUpdate)
    {
        if(player.isAlive() && ComponentUtils.isWearingBackpack(player))
        {
            TravelersBackpackInventory inv = ComponentUtils.getBackpackInv(player);

            if(onlyTankUpdate)
            {
                if(player.currentScreenHandler instanceof TravelersBackpackItemScreenHandler && ((TravelersBackpackItemScreenHandler)player.currentScreenHandler).inventory.getScreenID() == Reference.WEARABLE_SCREEN_ID && onlyTankUpdate)
                {
                    //#TODO HAS TO BE THERE, BECAUSE FLUID SLOT IS NOT UPDATED ON TIME
                    if(!inv.getInventory().getStack(inv.getTier().getSlotIndex(Tiers.SlotType.BUCKET_IN_LEFT)).isEmpty() || !inv.getInventory().getStack(inv.getTier().getSlotIndex(Tiers.SlotType.BUCKET_IN_RIGHT)).isEmpty()) inv.updateTankSlots();
                }
            }

            else if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, ComponentUtils.getWearingBackpack(player)))
            {
                if(!inv.getWorld().isClient)
                {
                    if(inv.getLastTime() > 0)
                    {
                        inv.setLastTime(inv.getLastTime() - 1);
                        inv.markDataDirty(LAST_TIME_DATA);
                    }
                }
                if(inv.getAbilityValue())
                {
                    BackpackAbilities.ABILITIES.abilityTick(ComponentUtils.getWearingBackpack(player), player, null);
                }
            }
        }
    }

    public static void openHandledScreen(PlayerEntity player, ItemStack stack, byte screenID)
    {
        if(!player.world.isClient)
        {
            player.openHandledScreen(new ExtendedScreenHandlerFactory()
            {
                @Override
                public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf)
                {
                    buf.writeByte(screenID);
                }

                @Override
                public Text getDisplayName()
                {
                    return new TranslatableText("screen.travelersbackpack.item");
                }

                @Nullable
                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
                {
                    if(screenID == Reference.WEARABLE_SCREEN_ID)
                    {
                        return new TravelersBackpackItemScreenHandler(syncId, inv, ComponentUtils.getBackpackInv(player));
                    }
                    else
                    {
                        return new TravelersBackpackItemScreenHandler(syncId, inv, new TravelersBackpackInventory(stack, player, screenID));
                    }
                }
            });
        }
    }

    public InventoryImproved createInventory(int size)
    {
        return new InventoryImproved(DefaultedList.ofSize(size, ItemStack.EMPTY))
        {
            @Override
            public void markDirty()
            {
                markDataDirty(COMBINED_INVENTORY_DATA);
            }

            @Override
            public boolean isValid(int slot, ItemStack stack)
            {
                Identifier blacklistedItems = new Identifier(TravelersBackpack.MODID, "blacklisted_items");

                if(BackpackSlot.BLACKLISTED_ITEMS.contains(stack.getItem())) return false;

                return !(stack.getItem() instanceof TravelersBackpackItem) && !stack.getItem().isIn(ItemTags.getTagGroup().getTag(blacklistedItems)) && (TravelersBackpackConfig.allowShulkerBoxes || !(Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock));
            }
        };
    }

    public SingleVariantStorage<FluidVariant> createFluidTank(long capacity)
    {
        return new SingleVariantStorage<FluidVariant>()
        {
            @Override
            protected FluidVariant getBlankVariant() {
                return FluidVariant.blank();
            }

            @Override
            protected long getCapacity(FluidVariant variant)
            {
                return TravelersBackpackInventory.this.tier.getTankCapacity();
            }

            @Override
            protected void onFinalCommit()
            {
                markDataDirty(TANKS_DATA);
            }
        };
    }
}