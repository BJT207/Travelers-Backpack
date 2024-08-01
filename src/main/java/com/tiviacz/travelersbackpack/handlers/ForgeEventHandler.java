package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import com.tiviacz.travelersbackpack.capability.TravelersBackpackCapability;
import com.tiviacz.travelersbackpack.capability.TravelersBackpackWearable;
import com.tiviacz.travelersbackpack.capability.entity.IEntityTravelersBackpack;
import com.tiviacz.travelersbackpack.capability.entity.TravelersBackpackEntityCapability;
import com.tiviacz.travelersbackpack.capability.entity.TravelersBackpackEntityWearable;
import com.tiviacz.travelersbackpack.commands.AccessBackpackCommand;
import com.tiviacz.travelersbackpack.commands.ClearBackpackCommand;
import com.tiviacz.travelersbackpack.commands.RestoreBackpackCommand;
import com.tiviacz.travelersbackpack.commands.UnpackBackpackCommand;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.common.recipes.ShapedBackpackRecipe;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModTags;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.items.UpgradeItem;
import com.tiviacz.travelersbackpack.network.ClientboundSendMessagePacket;
import com.tiviacz.travelersbackpack.network.ClientboundSyncCapabilityPacket;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.LogHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.EnderManAngerEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.command.ConfigCommand;

import java.util.concurrent.atomic.AtomicInteger;

@Mod.EventBusSubscriber(modid = TravelersBackpack.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler
{
    @SubscribeEvent
    public static void playerSetSpawn(PlayerSetSpawnEvent event)
    {
        Level level = event.getEntity().level();

        if(event.getNewSpawn() != null)
        {
            Block block = level.getBlockState(event.getNewSpawn()).getBlock();

            if(!level.isClientSide && block instanceof SleepingBagBlock && !event.isForced())
            {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void playerRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        ItemStack stack = event.getItemStack();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Player player = event.getEntity();

        if(TravelersBackpackConfig.SERVER.backpackSettings.rightClickUnequip.get())
        {
            if(CapabilityUtils.isWearingBackpack(player) && !level.isClientSide)
            {
                if(player.isShiftKeyDown() && event.getHand() == InteractionHand.MAIN_HAND && player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty())
                {
                    ItemStack backpackStack = CapabilityUtils.getWearingBackpack(player);
                    UseOnContext context = new UseOnContext(level, player, InteractionHand.MAIN_HAND, backpackStack, event.getHitVec());

                    if(backpackStack.getItem() instanceof TravelersBackpackItem item)
                    {
                        if(item.place(new BlockPlaceContext(context)) == InteractionResult.sidedSuccess(level.isClientSide))
                        {
                            player.swing(InteractionHand.MAIN_HAND, true);
                            level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1.05F, (1.0F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F) * 0.7F);

                            CapabilityUtils.getCapability(player).ifPresent(ITravelersBackpack::removeWearable);

                            //if(TravelersBackpack.enableCurios())
                            //{
                            //    TravelersBackpackCurios.rightClickUnequip(player, backpackStack);
                           // }

                            CapabilityUtils.synchronise(player);
                            CapabilityUtils.synchroniseToOthers(player);

                            event.setCanceled(true);
                            return;
                        }
                    }
                }
            }
        }

        if(player.isShiftKeyDown() && event.getHand() == InteractionHand.MAIN_HAND && player.getItemInHand(InteractionHand.MAIN_HAND).is(ModTags.SLEEPING_BAGS) && level.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity blockEntity)
        {
            ItemStack oldSleepingBag = blockEntity.getProperSleepingBag(blockEntity.getSleepingBagColor()).getBlock().asItem().getDefaultInstance();
            blockEntity.setSleepingBagColor(ShapedBackpackRecipe.getProperColor(player.getItemInHand(InteractionHand.MAIN_HAND).getItem()));
            if(!level.isClientSide)
            {
                Containers.dropItemStack(level, pos.getX(), pos.above().getY(), pos.getZ(), oldSleepingBag);
                stack.shrink(1);
            }
            player.level().playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER.value(), SoundSource.PLAYERS, 1.0F, (1.0F + (player.level().random.nextFloat() - player.level().random.nextFloat()) * 0.2F) * 0.7F);
            player.swing(InteractionHand.MAIN_HAND, true);

            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        if(player.isShiftKeyDown() && player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == ModItems.BLANK_UPGRADE.get() && level.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity blockEntity)
        {
            NonNullList<ItemStack> list = NonNullList.create();

            for(int i = 0; i < blockEntity.getCombinedHandler().getSlots(); i++)
            {
                ItemStack stackInSlot = blockEntity.getCombinedHandler().getStackInSlot(i);

                if(!stackInSlot.isEmpty())
                {
                    list.add(stackInSlot);
                    blockEntity.getCombinedHandler().setStackInSlot(i, ItemStack.EMPTY);
                }
            }

            list.addAll(UpgradeItem.getUpgrades(blockEntity));

            //Remove unsortable slots
            if(!blockEntity.getSlotManager().getUnsortableSlots().isEmpty())
            {
                blockEntity.getSlotManager().getUnsortableSlots().clear();
            }

            //Remove memory slots
            if(!blockEntity.getSlotManager().getMemorySlots().isEmpty())
            {
                blockEntity.getSlotManager().getMemorySlots().clear();
            }

            //Drain excessive fluid
            int fluidAmountLeft = blockEntity.getLeftTank().isEmpty() ? 0 : blockEntity.getLeftTank().getFluidAmount();

            if(fluidAmountLeft > Tiers.LEATHER.getTankCapacity())
            {
                blockEntity.getLeftTank().drain(fluidAmountLeft - Tiers.LEATHER.getTankCapacity(), IFluidHandler.FluidAction.EXECUTE);
            }

            int fluidAmountRight = blockEntity.getRightTank().isEmpty() ? 0 : blockEntity.getRightTank().getFluidAmount();

            if(fluidAmountRight > Tiers.LEATHER.getTankCapacity())
            {
                blockEntity.getRightTank().drain(fluidAmountRight - Tiers.LEATHER.getTankCapacity(), IFluidHandler.FluidAction.EXECUTE);
            }

            if(!level.isClientSide)
            {
                Containers.dropContents(level, pos.above(), list);
            }

            //Change size of Tool slots and Storage slots
            blockEntity.getHandler().setSize(Tiers.LEATHER.getStorageSlots());
            blockEntity.getToolSlotsHandler().setSize(Tiers.LEATHER.getToolSlots());

            //Reset tier
            blockEntity.resetTier();

            //Reset Tanks
            blockEntity.getLeftTank().setCapacity(Tiers.LEATHER.getTankCapacity());
            blockEntity.getRightTank().setCapacity(Tiers.LEATHER.getTankCapacity());

            //Reset Settings
            blockEntity.getSettingsManager().loadDefaults();

            player.swing(InteractionHand.MAIN_HAND, true);

            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        if(event.getLevel().isClientSide) return;

        //Equip Backpack on right click with any item in hand
        if(TravelersBackpackConfig.SERVER.backpackSettings.rightClickEquip.get() && event.getLevel().getBlockState(event.getPos()).getBlock() instanceof TravelersBackpackBlock block)
        {
            if(player.isShiftKeyDown() && !CapabilityUtils.isWearingBackpack(player))
            {
                TravelersBackpackBlockEntity blockEntity = (TravelersBackpackBlockEntity)level.getBlockEntity(pos);
                ItemStack backpack = new ItemStack(block, 1);

                Direction bagDirection = level.getBlockState(pos).getValue(TravelersBackpackBlock.FACING);

               // boolean canEquipCurio;
               // if(TravelersBackpack.enableCurios())
               // {
                //    canEquipCurio = TravelersBackpackCurios.rightClickEquip(player, backpack, true);
               //     if(!canEquipCurio) return;
              //  }

                if(level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState()))
                {
                    blockEntity.transferToItemStack(backpack);

                  //  if(TravelersBackpack.enableCurios()) TravelersBackpackCurios.rightClickEquip(player, backpack, false);
                   // else
                    CapabilityUtils.equipBackpack(event.getEntity(), backpack);

                    player.swing(InteractionHand.MAIN_HAND, true);

                    if(blockEntity.isSleepingBagDeployed())
                    {
                        level.setBlockAndUpdate(pos.relative(bagDirection), Blocks.AIR.defaultBlockState());
                        level.setBlockAndUpdate(pos.relative(bagDirection).relative(bagDirection), Blocks.AIR.defaultBlockState());
                    }
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEnderManAngerEvent(EnderManAngerEvent event)
    {
        if(TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get())
        {
            BackpackAbilities.pumpkinAbility(event);
        }
    }

    @SubscribeEvent
    public static void blockBlazeProjectile(ProjectileImpactEvent event)
    {
        if(TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get())
        {
            BackpackAbilities.blazeAbility(event);
        }
    }

    @SubscribeEvent
    public static void livingChangeTarget(LivingChangeTargetEvent event)
    {
        if(TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get())
        {
            BackpackAbilities.ghastAbility(event);
        }
    }

    @SubscribeEvent
    public static void onHit(AttackEntityEvent event)
    {
        if(TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get())
        {
            BackpackAbilities.beeAbility(event);
        }
    }

    @SubscribeEvent
    public static void onExpPickup(PlayerXpEvent.PickupXp event)
    {
        if(TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get())
        {
            BackpackAbilities.ABILITIES.lapisAbility(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event)
    {
        if(event.getEntity() instanceof Player player)
        {
            CapabilityUtils.synchronise(player);
        }

        if(event.getEntity() instanceof LivingEntity living && !event.loadedFromDisk() && TravelersBackpackConfig.SERVER.world.spawnEntitiesWithBackpack.get())
        {
            LazyOptional<IEntityTravelersBackpack> cap = CapabilityUtils.getEntityCapability(living);

            if(cap.isPresent() && Reference.ALLOWED_TYPE_ENTRIES.contains(event.getEntity().getType()))
            {
                IEntityTravelersBackpack travelersBackpack = cap.resolve().get();

                if(!travelersBackpack.hasWearable() && event.getLevel().getRandom().nextInt(0, TravelersBackpackConfig.SERVER.world.spawnChance.get()) == 0)
                {
                    boolean isNether = living.getType() == EntityType.PIGLIN || living.getType() == EntityType.WITHER_SKELETON;
                    RandomSource rand = event.getLevel().random;
                    ItemStack backpack = isNether ?
                            ModItems.COMPATIBLE_NETHER_BACKPACK_ENTRIES.get(rand.nextIntBetweenInclusive(0, ModItems.COMPATIBLE_NETHER_BACKPACK_ENTRIES.size() - 1)).getDefaultInstance() :
                            ModItems.COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.get(rand.nextIntBetweenInclusive(0, ModItems.COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.size() - 1)).getDefaultInstance();

                    backpack.set(ModDataComponents.SLEEPING_BAG_COLOR.get(), DyeColor.values()[rand.nextIntBetweenInclusive(0, DyeColor.values().length - 1)].getId());

                    travelersBackpack.setWearable(backpack);
                    travelersBackpack.synchronise();
                }
            }
        }

        if(!(event.getEntity() instanceof ItemEntity itemEntity) || !TravelersBackpackConfig.SERVER.backpackSettings.invulnerableBackpack.get()) return;

        if(itemEntity.getItem().getItem() instanceof TravelersBackpackItem)
        {
            itemEntity.setUnlimitedLifetime();
            itemEntity.setInvulnerable(true);
        }
    }

    @SubscribeEvent
    public static void entityLeave(EntityLeaveLevelEvent event)
    {
        if(!(event.getEntity() instanceof ItemEntity itemEntity) || !TravelersBackpackConfig.SERVER.backpackSettings.voidProtection.get()) return;

        //Void protection
        if(itemEntity.getItem().getItem() instanceof TravelersBackpackItem)
        {
            if(event.getLevel().isClientSide) return;

            BlockPos entityPos = itemEntity.blockPosition();
            Vec3 entityPosCentered = entityPos.getCenter();
            double y = entityPosCentered.y();

            if(y < event.getLevel().getMinBuildHeight())
            {
                ItemEntity protectedItemEntity = new ItemEntity(event.getLevel(), entityPosCentered.x(), y, entityPosCentered.z(), itemEntity.getItem());

                protectedItemEntity.setNoGravity(true);
                protectedItemEntity.setDefaultPickUpDelay();

                y = event.getLevel().getMinBuildHeight();

                for(double i = y; i < event.getLevel().getHeight(); i++)
                {
                    if(event.getLevel().getBlockState(BlockPos.containing(new Vec3(entityPosCentered.x(), i, entityPosCentered.z()))).canBeReplaced())
                    {
                        y = i;
                        break;
                    }
                }

                protectedItemEntity.setPos(entityPosCentered.x(), y, entityPosCentered.z());
                protectedItemEntity.setDeltaMovement(0, 0, 0);

                event.getLevel().addFreshEntity(protectedItemEntity);
            }
        }
    }

    @SubscribeEvent
    public static void attachCapabilities(final AttachCapabilitiesEvent<Entity> event)
    {
        if(event.getObject() instanceof Player player)
        {
            final TravelersBackpackWearable travelersBackpack = new TravelersBackpackWearable(player);
            event.addCapability(TravelersBackpackCapability.ID, TravelersBackpackCapability.createProvider(travelersBackpack));
        }

        if(event.getObject() instanceof LivingEntity livingEntity)
        {
            if(Reference.ALLOWED_TYPE_ENTRIES.contains(livingEntity.getType()))
            {
                final TravelersBackpackEntityWearable travelersBackpack = new TravelersBackpackEntityWearable(livingEntity);
                event.addCapability(TravelersBackpackEntityCapability.ID, TravelersBackpackEntityCapability.createProvider(travelersBackpack));
            }
        }
    }

    @SubscribeEvent
    public static void playerDeath(LivingDeathEvent event)
    {
        if(event.getEntity() instanceof Player player)
        {
            if(CapabilityUtils.isWearingBackpack(player))
            {
                if(TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get() && BackpackAbilities.creeperAbility(event))
                {
                    return;
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerDrops(LivingDropsEvent event)
    {
        if(event.getEntity() instanceof Player player)
        {
            if(CapabilityUtils.isWearingBackpack(player))
            {
                //Keep backpack on with Keep Inventory game rule
                if(player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) return;

                ItemStack stack = CapabilityUtils.getWearingBackpack(player);

                if(BackpackUtils.onPlayerDrops(player.level(), player, stack))
                {
                    if(player.level().isClientSide) return;

                    ItemEntity itemEntity = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), stack);
                    itemEntity.setDefaultPickUpDelay();

                    TravelersBackpack.NETWORK.send(new ClientboundSendMessagePacket(true, player.blockPosition()), PacketDistributor.PLAYER.with((ServerPlayer)player));
                    //PacketDistributor.PLAYER.with((ServerPlayer)player).send(new ClientboundSendMessagePacket(true, player.blockPosition()));
                    LogHelper.info("There's no space for backpack. Dropping backpack item at" + " X: " + player.blockPosition().getX() + " Y: " + player.getY() + " Z: " + player.blockPosition().getZ());

                    //If Curios loaded - handled by Curios
                    //if(!TravelersBackpack.enableCurios())
                    //{
                    //    event.getDrops().add(itemEntity);
                    //}

                    CapabilityUtils.getCapability(player).ifPresent(ITravelersBackpack::removeWearable);
                    CapabilityUtils.synchronise(player);
                }
            }
        }

        if(Reference.ALLOWED_TYPE_ENTRIES.contains(event.getEntity().getType()))
        {
            if(CapabilityUtils.isWearingBackpack(event.getEntity()))
            {
                ItemEntity itemEntity = new ItemEntity(event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), CapabilityUtils.getWearingBackpack(event.getEntity()));
                event.getDrops().add(itemEntity);
            }
        }
    }

    @SubscribeEvent
    public static void playerClone(final PlayerEvent.Clone event)
    {
        Player oldPlayer = event.getOriginal();
        oldPlayer.revive();

        CapabilityUtils.getCapability(oldPlayer)
                .ifPresent(oldTravelersBackpack -> CapabilityUtils.getCapability(event.getEntity())
                        .ifPresent(newTravelersBackpack ->
                        {
                            newTravelersBackpack.setWearable(oldTravelersBackpack.getWearable());
                            newTravelersBackpack.setContents(oldTravelersBackpack.getWearable());
                        }));
    }

    @SubscribeEvent
    public static void playerChangeDimension(final PlayerEvent.PlayerChangedDimensionEvent event)
    {
        CapabilityUtils.synchronise(event.getEntity());
    }

    @SubscribeEvent
    public static void playerJoin(final PlayerEvent.PlayerLoggedInEvent event)
    {
        CapabilityUtils.synchronise(event.getEntity());
    }

    @SubscribeEvent
    public static void playerTracking(final PlayerEvent.StartTracking event)
    {
        if(event.getTarget() instanceof Player && !event.getTarget().level().isClientSide)
        {
            ServerPlayer target = (ServerPlayer)event.getTarget();

            CapabilityUtils.getCapability(target).ifPresent(c -> TravelersBackpack.NETWORK.send(new ClientboundSyncCapabilityPacket(target.getId(), true, CapabilityUtils.getWearingBackpack(target)),
                    PacketDistributor.PLAYER.with((ServerPlayer)event.getEntity())));
        }

        if(Reference.ALLOWED_TYPE_ENTRIES.contains(event.getTarget().getType()) && !event.getTarget().level().isClientSide)
        {
            LivingEntity target = (LivingEntity)event.getTarget();

            CapabilityUtils.getEntityCapability(target).ifPresent(c -> TravelersBackpack.NETWORK.send(new ClientboundSyncCapabilityPacket(target.getId(), false, CapabilityUtils.getWearingBackpack(target)),
                    PacketDistributor.PLAYER.with((ServerPlayer)event.getEntity())));
        }
    }

    private static boolean checkAbilitiesForRemoval = true;

    @SubscribeEvent
    public static void playerTick(final TickEvent.PlayerTickEvent.Post event)
    {
        if(TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get() && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, CapabilityUtils.getWearingBackpack(event.player)))
        {
            TravelersBackpackContainer.abilityTick(event.player);
            if(!checkAbilitiesForRemoval && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_REMOVAL_LIST, CapabilityUtils.getWearingBackpack(event.player))) checkAbilitiesForRemoval = true;
        }

        if(checkAbilitiesForRemoval && !event.player.level().isClientSide && (!CapabilityUtils.isWearingBackpack(event.player) || !TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get()))
        {
            BackpackAbilities.ABILITIES.armorAbilityRemovals(event.player);
            checkAbilitiesForRemoval = false;
        }
    }

    private static long nextBackpackCountCheck = 0;
    private static final int BACKPACK_COUNT_CHECK_COOLDOWN = 100;

    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent.Post event)
    {
        if(!TravelersBackpackConfig.SERVER.slownessDebuff.tooManyBackpacksSlowness.get() || nextBackpackCountCheck > event.level.getGameTime())
        {
            return;
        }
        nextBackpackCountCheck = event.level.getGameTime() + BACKPACK_COUNT_CHECK_COOLDOWN;

        event.level.players().forEach(player ->
        {
            if(player.isCreative() || player.isSpectator()) return;

            AtomicInteger numberOfBackpacks = checkBackpacksForSlowness(player);
            if(numberOfBackpacks.get() == 0) return;

            int maxNumberOfBackpacks = TravelersBackpackConfig.SERVER.slownessDebuff.maxNumberOfBackpacks.get();
            if(numberOfBackpacks.get() > maxNumberOfBackpacks)
            {
                int numberOfSlownessLevels = Math.min(10, (int) Math.ceil((numberOfBackpacks.get() - maxNumberOfBackpacks) * TravelersBackpackConfig.SERVER.slownessDebuff.slownessPerExcessedBackpack.get()));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, BACKPACK_COUNT_CHECK_COOLDOWN * 2, numberOfSlownessLevels - 1, false, false));
            }
        });
    }

    public static AtomicInteger checkBackpacksForSlowness(Player player)
    {
        AtomicInteger atomic = new AtomicInteger(0);

        for(int i = 0; i < player.getInventory().items.size(); i++)
        {
            if(player.getInventory().items.get(i).getItem() instanceof TravelersBackpackItem)
            {
                atomic.incrementAndGet();
            }
        }

        if(player.getInventory().offhand.get(0).getItem() instanceof TravelersBackpackItem)
        {
            atomic.incrementAndGet();
        }
        return atomic;
    }

    @SubscribeEvent
    public static void registerCommands(final RegisterCommandsEvent event)
    {
        new AccessBackpackCommand(event.getDispatcher());
        new RestoreBackpackCommand(event.getDispatcher());
        new ClearBackpackCommand(event.getDispatcher());
        new UnpackBackpackCommand(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void explosionDetonate(final ExplosionEvent.Detonate event)
    {
        for(int i = 0; i < event.getAffectedEntities().size(); i++)
        {
            Entity entity = event.getAffectedEntities().get(i);

            if(entity instanceof ItemEntity itemEntity && itemEntity.getItem().getItem() instanceof TravelersBackpackItem)
            {
                event.getAffectedEntities().remove(i);
            }
        }
    }

    @SubscribeEvent
    public static void addVillagerTrade(final VillagerTradesEvent event)
    {
        if(TravelersBackpackConfig.COMMON.enableVillagerTrade.get() && event.getType() == VillagerProfession.LIBRARIAN)
        {
            event.getTrades().get(3).add((trader, random) -> new MerchantOffer(new ItemCost(Items.EMERALD, random.nextInt(64) + 48),
                    new ItemStack(ModItems.VILLAGER_TRAVELERS_BACKPACK.get().asItem(), 1), 1, 50, 0.5F));
        }
    }
}