package com.tiviacz.travelersbackpack.blockentity;

import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModBlockEntityTypes;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.InventoryActions;
import com.tiviacz.travelersbackpack.inventory.InventoryImproved;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackBlockEntityScreenHandler;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.util.InventoryUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.impl.transfer.fluid.FluidVariantImpl;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SideShapeType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class TravelersBackpackBlockEntity extends BlockEntity implements ITravelersBackpackInventory, BlockEntityClientSerializable, Nameable
{
    public InventoryImproved inventory = createInventory(Tiers.LEATHER.getStorageSlots());
    public InventoryImproved craftingInventory = createInventory(Reference.CRAFTING_GRID_SIZE);
    public SingleVariantStorage<FluidVariant> leftTank = createFluidTank(Tiers.LEATHER.getTankCapacity());
    public SingleVariantStorage<FluidVariant> rightTank = createFluidTank(Tiers.LEATHER.getTankCapacity());
    private final SlotManager slotManager = new SlotManager(this);
    private PlayerEntity player = null;
    private boolean isSleepingBagDeployed = false;
    private int color = 0;
    private int sleepingBagColor = DyeColor.RED.getId();
    private Tiers.Tier tier = Tiers.LEATHER;
    private boolean ability = false;
    private int lastTime = 0;
    private Text customName = null;
    private final String LEFT_TANK = "LeftTank";
    private final String LEFT_TANK_AMOUNT = "LeftTankAmount";
    private final String RIGHT_TANK = "RightTank";
    private final String RIGHT_TANK_AMOUNT = "RightTankAmount";
    private final String SLEEPING_BAG = "SleepingBag";
    private final String COLOR = "Color";
    private final String SLEEPING_BAG_COLOR = "SleepingBagColor";
    private final String ABILITY = "Ability";
    private final String LAST_TIME = "LastTime";
    private final String CUSTOM_NAME = "CustomName";

    public TravelersBackpackBlockEntity(BlockPos pos, BlockState state)
    {
        super(ModBlockEntityTypes.TRAVELERS_BACKPACK_BLOCK_ENTITY_TYPE, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.readAllData(nbt);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        this.writeAllData(tag);
        return tag;
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
    public SingleVariantStorage<FluidVariant> getLeftTank() {
        return this.leftTank;
    }

    @Override
    public SingleVariantStorage<FluidVariant> getRightTank() {
        return this.rightTank;
    }

    public void writeTier(NbtCompound compound)
    {
        compound.putString(Tiers.TIER, this.tier.getName());
    }

    public void readTier(NbtCompound compound)
    {
        this.tier = compound.contains(Tiers.TIER) ? Tiers.of(compound.getString(Tiers.TIER)) : compound.contains("Inventory") ? Tiers.DIAMOND : TravelersBackpackConfig.enableTierUpgrades ? Tiers.LEATHER : Tiers.DIAMOND;
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
        this.inventory = createInventory(this.tier.getStorageSlots());
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
    public void writeColor(NbtCompound compound)
    {
        compound.putInt(COLOR, this.color);
    }

    @Override
    public void readColor(NbtCompound compound)
    {
        this.color = compound.getInt(COLOR);
    }

    @Override
    public void writeSleepingBagColor(NbtCompound compound)
    {
        compound.putInt(SLEEPING_BAG_COLOR, this.sleepingBagColor);
    }

    @Override
    public void readSleepingBagColor(NbtCompound compound)
    {
        this.sleepingBagColor = compound.contains(SLEEPING_BAG_COLOR) ? compound.getInt(SLEEPING_BAG_COLOR) : DyeColor.RED.getId();
    }

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

    public void writeSleepingBag(NbtCompound compound)
    {
        compound.putBoolean(SLEEPING_BAG, this.isSleepingBagDeployed);
    }

    public void readSleepingBag(NbtCompound compound)
    {
        this.isSleepingBagDeployed = compound.getBoolean(SLEEPING_BAG);
    }

    public void writeName(NbtCompound compound)
    {
        if(this.customName != null)
        {
            compound.putString(CUSTOM_NAME, Text.Serializer.toJson(this.customName));
        }
    }

    public void readName(NbtCompound compound)
    {
        if(compound.contains(CUSTOM_NAME, 8))
        {
            this.customName = Text.Serializer.fromJson(compound.getString(CUSTOM_NAME));
        }
    }

    @Override
    public void writeAllData(NbtCompound compound)
    {
        writeTier(compound);
        writeItems(compound);
        writeTanks(compound);
        writeSleepingBag(compound);
        writeColor(compound);
        writeSleepingBagColor(compound);
        writeAbility(compound);
        writeTime(compound);
        writeName(compound);
        this.slotManager.writeUnsortableSlots(compound);
        this.slotManager.writeMemorySlots(compound);
    }

    @Override
    public void readAllData(NbtCompound compound)
    {
        readTier(compound);
        readItems(compound);
        readTanks(compound);
        readSleepingBag(compound);
        readColor(compound);
        readSleepingBagColor(compound);
        readAbility(compound);
        readTime(compound);
        readName(compound);
        this.slotManager.readUnsortableSlots(compound);
        this.slotManager.readMemorySlots(compound);
    }

    @Override
    public boolean updateTankSlots()
    {
        return InventoryActions.transferContainerTank(this, getLeftTank(), this.tier.getSlotIndex(Tiers.SlotType.BUCKET_IN_LEFT), this.player) || InventoryActions.transferContainerTank(this, getRightTank(), this.tier.getSlotIndex(Tiers.SlotType.BUCKET_IN_RIGHT), this.player);
    }

    @Override
    public boolean hasColor()
    {
        return this.color != 0;
    }

    @Override
    public int getColor()
    {
        return this.color;
    }

    @Override
    public boolean hasSleepingBagColor()
    {
        return this.sleepingBagColor != DyeColor.RED.getId();
    }

    @Override
    public int getSleepingBagColor()
    {
        if(hasSleepingBagColor())
        {
            return this.sleepingBagColor;
        }
        return DyeColor.RED.getId();
    }

    public void setSleepingBagColor(int colorId)
    {
        this.sleepingBagColor = colorId;
    }

    @Override
    public boolean getAbilityValue()
    {
        return TravelersBackpackConfig.enableBackpackAbilities ? this.ability : false;
    }

    @Override
    public void setAbility(boolean value)
    {
        this.ability = value;
        this.markDirty();
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
        return true;
    }

    @Override
    public boolean isSleepingBagDeployed()
    {
        return this.isSleepingBagDeployed;
    }

    @Override
    public SlotManager getSlotManager()
    {
        return slotManager;
    }

    @Override
    public Tiers.Tier getTier()
    {
        return this.tier;
    }

    public void resetTier()
    {
        this.tier = Tiers.LEATHER;
        this.markDirty();
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        ItemStack itemstack = Inventories.splitStack(getInventory().getStacks(), index, count);

        if(!itemstack.isEmpty())
        {
            this.markDirty();
        }
        return itemstack;
    }

    @Override
    public World getWorld()
    {
        return super.getWorld();
    }

    @Override
    public BlockPos getPosition()
    {
        return this.pos;
    }

    @Override
    public byte getScreenID()
    {
        return Reference.BLOCK_ENTITY_SCREEN_ID;
    }

    @Override
    public ItemStack getItemStack()
    {
        if(world.getBlockState(getPos()).getBlock() instanceof TravelersBackpackBlock block)
        {
            return new ItemStack(block);
        }
        return new ItemStack(ModBlocks.STANDARD_TRAVELERS_BACKPACK);
    }

    @Override
    public void setUsingPlayer(@Nullable PlayerEntity player)
    {
        this.player = player;
    }

    @Override
    public void markDataDirty(byte... dataIds) {}

    @Override
    public void markDirty()
    {
        if(!world.isClient)
        {
            super.markDirty();
            sync();
        }
    }

    public void setSleepingBagDeployed(boolean isSleepingBagDeployed)
    {
        this.isSleepingBagDeployed = isSleepingBagDeployed;
    }

    public boolean deploySleepingBag(World world, BlockPos pos)
    {
        Direction direction = this.getBlockDirection(world.getBlockEntity(getPos()));
        this.isThereSleepingBag(direction);

        if(!this.isSleepingBagDeployed)
        {
            BlockPos sleepingBagPos1 = pos.offset(direction);
            BlockPos sleepingBagPos2 = sleepingBagPos1.offset(direction);

            if(world.isAir(sleepingBagPos2) && world.isAir(sleepingBagPos1))
            {
                if(world.getBlockState(sleepingBagPos1.down()).isSideSolid(world, sleepingBagPos1.down(), Direction.UP, SideShapeType.FULL) && world.getBlockState(sleepingBagPos2.down()).isSideSolid(world, sleepingBagPos2.down(), Direction.UP, SideShapeType.FULL))
                {
                    world.playSound(null, sleepingBagPos2, SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.BLOCKS, 0.5F, 1.0F);

                    if(!world.isClient)
                    {
                        BlockState sleepingBagState = getProperSleepingBag(getSleepingBagColor());
                        world.setBlockState(sleepingBagPos1, sleepingBagState.with(SleepingBagBlock.FACING, direction).with(SleepingBagBlock.PART, BedPart.FOOT), 3);
                        world.setBlockState(sleepingBagPos2, sleepingBagState.with(SleepingBagBlock.FACING, direction).with(SleepingBagBlock.PART, BedPart.HEAD), 3);

                        world.updateNeighborsAlways(pos, sleepingBagState.getBlock());
                        world.updateNeighborsAlways(sleepingBagPos2, sleepingBagState.getBlock());
                    }

                    this.isSleepingBagDeployed = true;
                    this.markDirty();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean removeSleepingBag(World world)
    {
        Direction blockFacing = this.getBlockDirection(world.getBlockEntity(getPos()));

        this.isThereSleepingBag(blockFacing);

        if(this.isSleepingBagDeployed)
        {
            BlockPos sleepingBagPos1 = pos.offset(blockFacing);
            BlockPos sleepingBagPos2 = sleepingBagPos1.offset(blockFacing);

            if(world.getBlockState(sleepingBagPos1).getBlock() instanceof SleepingBagBlock && world.getBlockState(sleepingBagPos2).getBlock() instanceof SleepingBagBlock)
            {
                world.playSound(null, sleepingBagPos2, SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.BLOCKS, 0.5F, 1.0F);
                world.setBlockState(sleepingBagPos2, Blocks.AIR.getDefaultState());
                world.setBlockState(sleepingBagPos1, Blocks.AIR.getDefaultState());
                this.isSleepingBagDeployed = false;
                this.markDirty();
                return true;
            }
        }
        else
        {
            this.isSleepingBagDeployed = false;
            this.markDirty();
            return true;
        }
        return false;
    }

    public boolean isThereSleepingBag(Direction direction)
    {
        if(world.getBlockState(pos.offset(direction)).getBlock() instanceof SleepingBagBlock && world.getBlockState(pos.offset(direction).offset(direction)).getBlock() instanceof SleepingBagBlock)
        {
            return true;
        }
        else
        {
            this.isSleepingBagDeployed = false;
            return false;
        }
    }

    public BlockState getProperSleepingBag(int colorId)
    {
        return switch (colorId) {
            case 0 -> ModBlocks.WHITE_SLEEPING_BAG.getDefaultState();
            case 1 -> ModBlocks.ORANGE_SLEEPING_BAG.getDefaultState();
            case 2 -> ModBlocks.MAGENTA_SLEEPING_BAG.getDefaultState();
            case 3 -> ModBlocks.LIGHT_BLUE_SLEEPING_BAG.getDefaultState();
            case 4 -> ModBlocks.YELLOW_SLEEPING_BAG.getDefaultState();
            case 5 -> ModBlocks.LIME_SLEEPING_BAG.getDefaultState();
            case 6 -> ModBlocks.PINK_SLEEPING_BAG.getDefaultState();
            case 7 -> ModBlocks.GRAY_SLEEPING_BAG.getDefaultState();
            case 8 -> ModBlocks.LIGHT_GRAY_SLEEPING_BAG.getDefaultState();
            case 9 -> ModBlocks.CYAN_SLEEPING_BAG.getDefaultState();
            case 10 -> ModBlocks.PURPLE_SLEEPING_BAG.getDefaultState();
            case 11 -> ModBlocks.BLUE_SLEEPING_BAG.getDefaultState();
            case 12 -> ModBlocks.BROWN_SLEEPING_BAG.getDefaultState();
            case 13 -> ModBlocks.GREEN_SLEEPING_BAG.getDefaultState();
            case 14 -> ModBlocks.RED_SLEEPING_BAG.getDefaultState();
            case 15 -> ModBlocks.BLACK_SLEEPING_BAG.getDefaultState();
            default -> ModBlocks.RED_SLEEPING_BAG.getDefaultState();
        };
    }

    public boolean isUsableByPlayer(PlayerEntity player)
    {
        if(this.world.getBlockEntity(this.pos) != this)
        {
            return false;
        }
        else
        {
            return this.player == player && player.squaredDistanceTo((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    public Direction getBlockDirection(BlockEntity blockEntity)
    {
        if(blockEntity instanceof TravelersBackpackBlockEntity)
        {
            if(world == null || !(world.getBlockState(getPos()).getBlock() instanceof TravelersBackpackBlock))
            {
                return Direction.NORTH;
            }
            return world.getBlockState(getPos()).get(TravelersBackpackBlock.FACING);
        }
        return Direction.NORTH;
    }

    public ItemStack transferToItemStack(ItemStack stack)
    {
        NbtCompound compound = new NbtCompound();
        writeTier(compound);
        writeTanks(compound);
        writeItems(compound);
        if(this.hasColor()) this.writeColor(compound);
        if(this.hasSleepingBagColor()) this.writeSleepingBagColor(compound);
        writeAbility(compound);
        writeTime(compound);
        slotManager.writeUnsortableSlots(compound);
        slotManager.writeMemorySlots(compound);
        stack.setNbt(compound);
        return stack;
    }

    public boolean drop(WorldAccess world, BlockPos pos, Item item)
    {
        ItemStack stack = new ItemStack(item, 1);
        transferToItemStack(stack);
        if(this.hasCustomName())
        {
            stack.setCustomName(this.getCustomName());
        }
        ItemEntity droppedItem = new ItemEntity((World)world, pos.getX(), pos.getY(), pos.getZ(), stack);

        return world.spawnEntity(droppedItem);
    }

    @Override
    public void fromClientTag(NbtCompound compoundTag)
    {
        this.readNbt(compoundTag);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound compoundTag)
    {
        return this.writeNbt(compoundTag);
    }

    @Override
    public Text getName()
    {
        return this.customName != null ? this.customName : this.getDisplayName();
    }

    @Nullable
    @Override
    public Text getCustomName()
    {
        return this.customName;
    }

    public void setCustomName(Text customName)
    {
        this.customName = customName;
    }

    @Override
    public Text getDisplayName()
    {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    public static void tick(World world, BlockPos pos, BlockState state, TravelersBackpackBlockEntity blockEntity)
    {
        if(blockEntity.getAbilityValue() && BackpackAbilities.isOnList(BackpackAbilities.BLOCK_ABILITIES_LIST, blockEntity.getItemStack()))
        {
            if(blockEntity.getLastTime() > 0)
            {
                blockEntity.setLastTime(blockEntity.getLastTime() - 1);
                blockEntity.markDirty();
            }

            BackpackAbilities.ABILITIES.abilityTick(null, null, blockEntity);
        }
    }

    public void openHandledScreen(PlayerEntity player)
    {
        if(!player.world.isClient)
        {
            player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf)
                {
                    buf.writeBlockPos(TravelersBackpackBlockEntity.this.pos);
                }

                @Override
                public Text getDisplayName()
                {
                    return new TranslatableText(getCachedState().getBlock().getTranslationKey());
                }

                @Nullable
                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
                {
                    return new TravelersBackpackBlockEntityScreenHandler(syncId, inv, TravelersBackpackBlockEntity.this);
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
                TravelersBackpackBlockEntity.this.markDirty();
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
                return TravelersBackpackBlockEntity.this.tier.getTankCapacity();
            }

            @Override
            protected void onFinalCommit()
            {
                TravelersBackpackBlockEntity.this.markDirty();
            }
        };
    }
}