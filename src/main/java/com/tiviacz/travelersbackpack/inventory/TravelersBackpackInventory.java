package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackItemScreenHandler;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.inventory.sorter.wrappers.CombinedInvWrapper;
import com.tiviacz.travelersbackpack.inventory.sorter.wrappers.RangedWrapper;
import com.tiviacz.travelersbackpack.util.InventoryUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.impl.transfer.fluid.FluidVariantImpl;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
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
            this.readAllData(stack.getOrCreateNbt());
        }
    }

    public void setStack(ItemStack stack)
    {
        this.stack = stack;
    }

    public void readTier(NbtCompound compound)
    {
        if(!compound.contains(Tiers.TIER))
        {
            compound.putInt(Tiers.TIER, TravelersBackpackConfig.enableTierUpgrades ? Tiers.LEATHER.getOrdinal() : Tiers.DIAMOND.getOrdinal());
        }
        if(compound.contains(Tiers.TIER, NbtElement.STRING_TYPE))
        {
            Tiers.Tier tier = Tiers.of(compound.getString(Tiers.TIER));
            compound.remove(Tiers.TIER);
            compound.putInt(Tiers.TIER, tier.getOrdinal());
        }
        this.tier = Tiers.of(compound.getInt(Tiers.TIER));
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
        this.slotManager.writeUnsortableSlots(compound);
        this.slotManager.writeMemorySlots(compound);
        this.settingsManager.writeSettings(compound);
    }

    @Override
    public void readAllData(NbtCompound compound)
    {
        readTier(compound);
        readItems(compound);
        readTanks(compound);
        readAbility(compound);
        readTime(compound);
        this.slotManager.readUnsortableSlots(compound);
        this.slotManager.readMemorySlots(compound);
        this.settingsManager.readSettings(compound);
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
        return stack.getOrCreateNbt().contains(COLOR);
    }

    @Override
    public int getColor()
    {
        if(hasColor())
        {
            return stack.getOrCreateNbt().getInt(COLOR);
        }
        return 0;
    }

    public boolean hasSleepingBagColor()
    {
        return this.stack.getOrCreateNbt().contains(SLEEPING_BAG_COLOR);
    }

    @Override
    public int getSleepingBagColor()
    {
        if(hasSleepingBagColor())
        {
            return this.stack.getOrCreateNbt().getInt(SLEEPING_BAG_COLOR);
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
                case INVENTORY_DATA: InventoryUtils.writeNbt(stack.getOrCreateNbt(), this.inventory.getStacks(), true, false);
                case CRAFTING_INVENTORY_DATA: InventoryUtils.writeNbt(stack.getOrCreateNbt(), this.craftingInventory.getStacks(), true, true);
                case COMBINED_INVENTORY_DATA: writeItems(stack.getOrCreateNbt());
                case TANKS_DATA: writeTanks(stack.getOrCreateNbt());
                case COLOR_DATA: writeColor(stack.getOrCreateNbt());
                case SLEEPING_BAG_COLOR_DATA: writeSleepingBagColor(this.stack.getOrCreateNbt());
                case ABILITY_DATA: writeAbility(stack.getOrCreateNbt());
                case LAST_TIME_DATA: writeTime(stack.getOrCreateNbt());
                case SLOT_DATA: slotManager.writeUnsortableSlots(stack.getOrCreateNbt());
                                slotManager.writeMemorySlots(stack.getOrCreateNbt());
                case SETTINGS_DATA: settingsManager.writeSettings(stack.getOrCreateNbt());
                case ALL_DATA: writeAllData(stack.getOrCreateNbt());
            }
        }
        sendPackets();
    }

    @Override
    public void markDirty() {}

    public static void abilityTick(PlayerEntity player)
    {
        if(player.isAlive() && ComponentUtils.isWearingBackpack(player) && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, ComponentUtils.getWearingBackpack(player)))
        {
            TravelersBackpackInventory inv = ComponentUtils.getBackpackInv(player);

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
                public Text getDisplayName() {
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