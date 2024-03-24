package com.tiviacz.travelersbackpack.config;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.datagen.ModLootTableProvider;
import com.tiviacz.travelersbackpack.datagen.ModRecipeProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Mod.EventBusSubscriber(modid = TravelersBackpack.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TravelersBackpackConfig
{
    //Backpack Settings
    public static TravelersBackpackConfig.Common.BackpackSettings.Tier leatherTier;
    public static TravelersBackpackConfig.Common.BackpackSettings.Tier ironTier;
    public static TravelersBackpackConfig.Common.BackpackSettings.Tier goldTier;
    public static TravelersBackpackConfig.Common.BackpackSettings.Tier diamondTier;
    public static TravelersBackpackConfig.Common.BackpackSettings.Tier netheriteTier;
    public static boolean enableTierUpgrades;
    public static boolean enableCraftingUpgrade;
    public static boolean craftingUpgradeByDefault;
    public static boolean craftingSavesItems;
    public static boolean enableBackpackBlockWearable;
    public static boolean enableBackpackRightClickUnequip;
    public static boolean invulnerableBackpack;
    public static boolean toolSlotsAcceptSwords;
    public static boolean toolSlotsAcceptEverything;
    public static List<? extends String> toolSlotsAcceptableItems;
    public static List<? extends String> blacklistedItems;
    public static boolean allowShulkerBoxes;
    public static boolean voidProtection;
    public static boolean backpackDeathPlace;
    public static boolean backpackForceDeathPlace;
    public static boolean enableSleepingBagSpawnPoint;
    public static boolean curiosIntegration;

    //World
    public static boolean enableLoot;
    public static boolean spawnEntitiesWithBackpack;
    public static List<? extends String> possibleOverworldEntityTypes;
    public static List<? extends String> possibleNetherEntityTypes;
    public static int spawnChance;
    public static List<? extends String> overworldBackpacks;
    public static List<? extends String> netherBackpacks;
    public static boolean enableVillagerTrade;

    //Abilities
    public static boolean enableBackpackAbilities;
    public static boolean forceAbilityEnabled;
    public static List<? extends String> allowedAbilities;

    //Slowness Debuff
    public static boolean tooManyBackpacksSlowness;
    public static int maxNumberOfBackpacks;
    public static double slownessPerExcessedBackpack;

    //Client Settings
    public static boolean enableLegacyGui;
    public static boolean enableToolCycling;
    public static boolean disableScrollWheel;
    public static boolean obtainTips;
    public static boolean renderTools;
    public static boolean renderBackpackWithElytra;
    public static boolean disableBackpackRender;

    //Overlay
    public static boolean enableOverlay;
    public static int offsetX;
    public static int offsetY;

    public static class Common
    {
        private static final String REGISTRY_NAME_MATCHER = "([a-z0-9_.-]+:[a-z0-9_/.-]+)";

        BackpackSettings backpackSettings;
        World world;
        BackpackAbilities backpackAbilities;
        SlownessDebuff slownessDebuff;

        Common(final ModConfigSpec.Builder builder)
        {
            builder.comment("Common config settings")
                    .push("common");

            //Backpack Settings
            backpackSettings = new BackpackSettings(builder, "backpackSettings");

            //World
            world = new World(builder, "world");

            //Abilities
            backpackAbilities = new BackpackAbilities(builder, "backpackAbilities");

            //Slowness Debuff
            slownessDebuff = new SlownessDebuff(builder, "slownessDebuff");

            builder.pop();
        }

        public static class BackpackSettings
        {
            public final TierConfig leather;
            public final TierConfig iron;
            public final TierConfig gold;
            public final TierConfig diamond;
            public final TierConfig netherite;
            public final ModConfigSpec.BooleanValue enableTierUpgrades;
            public final ModConfigSpec.BooleanValue enableCraftingUpgrade;
            public final ModConfigSpec.BooleanValue craftingUpgradeByDefault;
            public final ModConfigSpec.BooleanValue craftingSavesItems;
            public final ModConfigSpec.BooleanValue enableBackpackBlockWearable;
            public final ModConfigSpec.BooleanValue enableBackpackRightClickUnequip;
            public final ModConfigSpec.BooleanValue invulnerableBackpack;
            public final ModConfigSpec.BooleanValue toolSlotsAcceptSwords;
            public final ModConfigSpec.BooleanValue toolSlotsAcceptEverything;
            public final ModConfigSpec.ConfigValue<List<? extends String>> toolSlotsAcceptableItems;
            public final ModConfigSpec.ConfigValue<List<? extends String>> blacklistedItems;
            public final ModConfigSpec.BooleanValue allowShulkerBoxes;
            public final ModConfigSpec.BooleanValue voidProtection;
            public final ModConfigSpec.BooleanValue backpackDeathPlace;
            public final ModConfigSpec.BooleanValue backpackForceDeathPlace;
            public final ModConfigSpec.BooleanValue enableSleepingBagSpawnPoint;
            public final ModConfigSpec.BooleanValue curiosIntegration;

            BackpackSettings(final ModConfigSpec.Builder builder, final String path)
            {
                builder.push(path);

                //Backpack Settings

                leather = new TierConfig(builder, "Leather", 27, 2, 3000);
                iron = new TierConfig(builder, "Iron", 36, 3, 4000);
                gold = new TierConfig(builder, "Gold", 45, 4, 5000);
                diamond = new TierConfig(builder, "Diamond", 54, 5, 6000);
                netherite = new TierConfig(builder, "Netherite", 63, 6, 7000);

                enableTierUpgrades = builder
                        .define("enableTierUpgrades", true);

                enableCraftingUpgrade = builder
                        .define("enableCraftingUpgrade", true);

                craftingUpgradeByDefault = builder
                        .comment("New backpacks will have crafting grid by default")
                        .define("craftingUpgradeByDefault", false);

                craftingSavesItems = builder
                        .define("craftingSavesItems", true);

                enableBackpackBlockWearable = builder
                        .comment("Enables wearing backpack directly from ground")
                        .define("enableBackpackBlockWearable", true);

                enableBackpackRightClickUnequip = builder
                        .comment("Enables unequipping the backpack when player clicks with empty hand on the ground")
                        .define("enableBackpackRightClickUnequip", false);

                invulnerableBackpack = builder
                        .comment("Backpack immune to any damage source (lava, fire), can't be destroyed, never disappears as floating item")
                        .define("invulnerableBackpack", true);

                toolSlotsAcceptSwords = builder
                        .define("toolSlotsAcceptSwords", true);

                toolSlotsAcceptEverything = builder
                        .comment("Tool slots accept any item")
                        .define("toolSlotsAcceptEverything", false);

                toolSlotsAcceptableItems = builder
                        .comment("List of items that can be put in tool slots (Use registry names, for example: minecraft:apple, minecraft:flint)")
                        .defineList("toolSlotsAcceptableItems", Collections.emptyList(), mapping -> ((String)mapping).matches(REGISTRY_NAME_MATCHER));

                blacklistedItems = builder
                        .comment("List of items that can't be put in backpack inventory (Use registry names, for example: minecraft:apple, minecraft:flint)")
                        .defineList("blacklistedItems", Collections.emptyList(), mapping -> ((String)mapping).matches(REGISTRY_NAME_MATCHER));

                allowShulkerBoxes = builder
                        .define("allowShulkerBoxes", false);

                voidProtection = builder
                        .comment("Prevents backpack disappearing in void")
                        .define("voidProtection", true);

                backpackDeathPlace = builder
                        .comment("Places backpack at place where player died")
                        .define("backpackDeathPlace", true);

                backpackForceDeathPlace = builder
                        .comment("Places backpack at place where player died, replacing all blocks that are breakable and do not have inventory (backpackDeathPlace must be true in order to work)")
                        .define("backpackForceDeathPlace", false);

                enableSleepingBagSpawnPoint = builder
                        .define("enableSleepingBagSpawnPoint", false);

                curiosIntegration = builder
                        .comment("If true, backpack can only be worn by placing it in curios 'Back' slot", "WARNING - Remember to TAKE OFF BACKPACK BEFORE enabling or disabling this integration!! - if not you'll lose your backpack")
                        .define("curiosIntegration", false);

                builder.pop();
            }

            public static class TierConfig
            {
                public final ModConfigSpec.IntValue inventorySlotCount;
                public final ModConfigSpec.IntValue toolSlotCount;
                public final ModConfigSpec.IntValue tankCapacity;

                public TierConfig(ModConfigSpec.Builder builder, String tier, int inventorySlotCountDefault, int toolSlotCountDefault, int tankCapacityDefault)
                {
                    builder.comment(tier + " Tier Backpack Settings").push(tier.toLowerCase(Locale.ENGLISH) + "TierBackpack");

                    inventorySlotCount =
                            builder.comment("Number of inventory slots for the tier")
                                    .defineInRange("inventorySlotCount", inventorySlotCountDefault, 1, 63);

                    toolSlotCount =
                            builder.comment("Number of tool slots for the tier")
                                    .defineInRange("toolSlotCount", toolSlotCountDefault, 0, 6);

                    tankCapacity =
                            builder.comment("Tank capacity for the tier, 1000 equals 1 Bucket")
                                    .defineInRange("tankCapacity", tankCapacityDefault, 1, 128000);

                    builder.pop();
                }
            }

            public record Tier(int inventorySlotCount, int toolSlotCount, int tankCapacity) { }
        }

        public static class World
        {
            public final ModConfigSpec.BooleanValue enableLoot;
            public final ModConfigSpec.BooleanValue spawnEntitiesWithBackpack;
            public final ModConfigSpec.ConfigValue<List<? extends String>> possibleOverworldEntityTypes;
            public final ModConfigSpec.ConfigValue<List<? extends String>> possibleNetherEntityTypes;
            public final ModConfigSpec.IntValue spawnChance;
            public final ModConfigSpec.ConfigValue<List<? extends String>> overworldBackpacks;
            public final ModConfigSpec.ConfigValue<List<? extends String>> netherBackpacks;
            public final ModConfigSpec.BooleanValue enableVillagerTrade;

            World(final ModConfigSpec.Builder builder, final String path)
            {
                builder.push(path);

                enableLoot = builder
                        .comment("Enables backpacks spawning in loot chests")
                        .define("enableLoot", true);

                spawnEntitiesWithBackpack = builder
                        .comment("Enables chance to spawn Zombie, Skeleton, Wither Skeleton, Piglin or Enderman with random backpack equipped")
                        .define("spawnEntitiesWithBackpack", true);

                possibleOverworldEntityTypes = builder
                        .comment("List of overworld entity types that can spawn with equipped backpack. DO NOT ADD anything to this list, because the game will crash, remove entries if mob should not spawn with backpack")
                        .defineList("possibleOverworldEntityTypes", this::getPossibleOverworldEntityTypes, mapping -> ((String)mapping).matches(REGISTRY_NAME_MATCHER));

                possibleNetherEntityTypes = builder
                        .comment("List of nether entity types that can spawn with equipped backpack. DO NOT ADD anything to this list, because the game will crash, remove entries if mob should not spawn with backpack")
                        .defineList("possibleNetherEntityTypes", this::getPossibleNetherEntityTypes, mapping -> ((String)mapping).matches(REGISTRY_NAME_MATCHER));


                spawnChance = builder
                        .comment("Defines spawn chance of entity with backpack (1 in [selected value])")
                        .defineInRange("spawnChance", 500, 0, Integer.MAX_VALUE);

                overworldBackpacks = builder
                        .comment("List of backpacks that can spawn on overworld mobs")
                        .defineList("overworldBackpacks", this::getOverworldBackpacksList, mapping -> ((String)mapping).matches(REGISTRY_NAME_MATCHER));

                netherBackpacks = builder
                        .comment("List of backpacks that can spawn on nether mobs")
                        .defineList("netherBackpacks", this::getNetherBackpacksList, mapping -> ((String)mapping).matches(REGISTRY_NAME_MATCHER));

                enableVillagerTrade = builder
                        .comment("Enables trade for Villager Backpack in Librarian villager trades")
                        .define("enableVillagerTrade", true);

                builder.pop();
            }

            private List<String> getPossibleOverworldEntityTypes()
            {
                List<String> ret = new ArrayList<>();
                ret.add("minecraft:zombie");
                ret.add("minecraft:skeleton");
                ret.add("minecraft:enderman");
                return ret;
            }

            private List<String> getPossibleNetherEntityTypes()
            {
                List<String> ret = new ArrayList<>();
                ret.add("minecraft:wither_skeleton");
                ret.add("minecraft:piglin");
                return ret;
            }


            private List<String> getOverworldBackpacksList()
            {
                List<String> ret = new ArrayList<>();
                ret.add("travelersbackpack:standard");
                ret.add("travelersbackpack:diamond");
                ret.add("travelersbackpack:gold");
                ret.add("travelersbackpack:emerald");
                ret.add("travelersbackpack:iron");
                ret.add("travelersbackpack:lapis");
                ret.add("travelersbackpack:redstone");
                ret.add("travelersbackpack:coal");
                ret.add("travelersbackpack:bookshelf");
                ret.add("travelersbackpack:sandstone");
                ret.add("travelersbackpack:snow");
                ret.add("travelersbackpack:sponge");
                ret.add("travelersbackpack:cake");
                ret.add("travelersbackpack:cactus");
                ret.add("travelersbackpack:hay");
                ret.add("travelersbackpack:melon");
                ret.add("travelersbackpack:pumpkin");
                ret.add("travelersbackpack:creeper");
                ret.add("travelersbackpack:enderman");
                ret.add("travelersbackpack:skeleton");
                ret.add("travelersbackpack:spider");
                ret.add("travelersbackpack:bee");
                ret.add("travelersbackpack:wolf");
                ret.add("travelersbackpack:fox");
                ret.add("travelersbackpack:ocelot");
                ret.add("travelersbackpack:horse");
                ret.add("travelersbackpack:cow");
                ret.add("travelersbackpack:pig");
                ret.add("travelersbackpack:sheep");
                ret.add("travelersbackpack:chicken");
                ret.add("travelersbackpack:squid");
                return ret;
            }

            private List<String> getNetherBackpacksList()
            {
                List<String> ret = new ArrayList<>();
                ret.add("travelersbackpack:quartz");
                ret.add("travelersbackpack:nether");
                ret.add("travelersbackpack:blaze");
                ret.add("travelersbackpack:ghast");
                ret.add("travelersbackpack:magma_cube");
                ret.add("travelersbackpack:wither");
                return ret;
            }
        }

        public static class BackpackAbilities
        {
            public final ModConfigSpec.BooleanValue enableBackpackAbilities;
            public final ModConfigSpec.BooleanValue forceAbilityEnabled;
            public final ModConfigSpec.ConfigValue<List<? extends String>> allowedAbilities;

            BackpackAbilities(final ModConfigSpec.Builder builder, final String path)
            {
                builder.push(path);

                enableBackpackAbilities = builder
                        .define("enableBackpackAbilities", true);

                forceAbilityEnabled = builder
                        .define("forceAbilityEnabled", false);

                allowedAbilities = builder
                        .comment("List of backpacks that are allowed to have an ability. DO NOT ADD anything to this list, because the game will crash, remove entries if backpack should not have ability")
                        .defineList("allowedAbilities", this::getAllowedAbilities, mapping -> ((String)mapping).matches(REGISTRY_NAME_MATCHER));

                builder.pop();
            }

            private List<String> getAllowedAbilities()
            {
                List<String> ret = new ArrayList<>();
                ret.add("travelersbackpack:netherite");
                ret.add("travelersbackpack:diamond");
                ret.add("travelersbackpack:gold");
                ret.add("travelersbackpack:emerald");
                ret.add("travelersbackpack:iron");
                ret.add("travelersbackpack:lapis");
                ret.add("travelersbackpack:redstone");
                ret.add("travelersbackpack:bookshelf");
                ret.add("travelersbackpack:sponge");
                ret.add("travelersbackpack:cake");
                ret.add("travelersbackpack:cactus");
                ret.add("travelersbackpack:melon");
                ret.add("travelersbackpack:pumpkin");
                ret.add("travelersbackpack:creeper");
                ret.add("travelersbackpack:dragon");
                ret.add("travelersbackpack:enderman");
                ret.add("travelersbackpack:blaze");
                ret.add("travelersbackpack:ghast");
                ret.add("travelersbackpack:magma_cube");
                ret.add("travelersbackpack:spider");
                ret.add("travelersbackpack:wither");
                ret.add("travelersbackpack:bat");
                ret.add("travelersbackpack:bee");
                ret.add("travelersbackpack:ocelot");
                ret.add("travelersbackpack:cow");
                ret.add("travelersbackpack:chicken");
                ret.add("travelersbackpack:squid");
                return ret;
            }
        }

        public static class SlownessDebuff
        {
            public final ModConfigSpec.BooleanValue tooManyBackpacksSlowness;
            public final ModConfigSpec.IntValue maxNumberOfBackpacks;
            public final ModConfigSpec.DoubleValue slownessPerExcessedBackpack;

            SlownessDebuff(final ModConfigSpec.Builder builder, final String path)
            {
                builder.push(path);

                tooManyBackpacksSlowness = builder
                        .comment("Player gets slowness effect, if carries too many backpacks in inventory")
                        .define("tooManyBackpacksSlowness", false);

                maxNumberOfBackpacks = builder
                        .comment("Maximum number of backpacks, which can be carried in inventory, without slowness effect")
                        .defineInRange("maxNumberOfBackpacks", 3, 1, 37);

                slownessPerExcessedBackpack = builder
                        .defineInRange("slownessPerExcessedBackpack", 1, 0.1, 5);

                builder.pop();
            }
        }

        public void loadItemsFromConfig(List<? extends String> configList, List<Item> targetList)
        {
            for(String registryName : configList)
            {
                ResourceLocation res = ResourceLocation.tryParse(registryName);

                if(BuiltInRegistries.ITEM.containsKey(res))
                {
                    targetList.add(BuiltInRegistries.ITEM.get(res));
                }
            }
        }

        public void loadEntityTypesFromConfig(List<? extends String> configList, List<EntityType> targetList)
        {
            for(String registryName : configList)
            {
                ResourceLocation res = ResourceLocation.tryParse(registryName);

                if(BuiltInRegistries.ENTITY_TYPE.containsKey(res))
                {
                    targetList.add(BuiltInRegistries.ENTITY_TYPE.get(res));
                }
            }
        }
    }

    public static class Client
    {
        public final ModConfigSpec.BooleanValue enableLegacyGui;
        public final ModConfigSpec.BooleanValue enableToolCycling;
        public final ModConfigSpec.BooleanValue disableScrollWheel;
        public final ModConfigSpec.BooleanValue obtainTips;
        public final ModConfigSpec.BooleanValue renderTools;
        public final ModConfigSpec.BooleanValue renderBackpackWithElytra;
        public final ModConfigSpec.BooleanValue disableBackpackRender;
        public final Overlay overlay;

        Client(final ModConfigSpec.Builder builder)
        {
            builder.comment("Client-only settings")
                    .push("client");

            enableLegacyGui = builder
                    .comment("Enables legacy GUI (Blue slots for storage, brown for crafting and green for tools)")
                    .define("enableLegacyGui", false);


            enableToolCycling = builder
                                        .comment("Enables tool cycling via keybind (Default Z) + scroll combination, while backpack is worn")
                                        .define("enableToolCycling", true);

            disableScrollWheel = builder
                                        .comment("Allows tool cycling using keybinding only (Default Z)")
                                        .define("disableScrollWheel", false);

            obtainTips = builder
                                        .comment("Enables tip, how to obtain a backpack, if there's no crafting recipe for it")
                                        .define("obtainTips", true);

            renderTools = builder
                                        .comment("Render tools in tool slots on the backpack, while worn")
                                        .define("renderTools", true);

            renderBackpackWithElytra = builder
                                        .comment("Render backpack if elytra is present")
                                        .define("renderBackpackWithElytra", true);

            disableBackpackRender = builder
                                        .comment("Disable backpack rendering")
                                        .define("disableBackpackRender", false);

            overlay = new Overlay(
                                        builder,
                                        "The position of the Overlay on the screen",
                                        "overlay",
                                        true, 20, 30
            );

            builder.pop();
        }

        public static class Overlay
        {
            public final ModConfigSpec.BooleanValue enableOverlay;
            public final ModConfigSpec.IntValue offsetX;
            public final ModConfigSpec.IntValue offsetY;

            Overlay(final ModConfigSpec.Builder builder, final String comment, final String path, final boolean defaultOverlay, final int defaultX, final int defaultY)
            {
                builder.comment(comment)
                                .push(path);

                enableOverlay = builder
                                .comment("Enables tanks and tool slots overlay, while backpack is worn")
                                .define("enableOverlay", defaultOverlay);

                offsetX = builder
                                .comment("Offsets to left side")
                                .defineInRange("offsetX", defaultX, Integer.MIN_VALUE, Integer.MAX_VALUE);

                offsetY = builder
                                .comment("Offsets to up")
                                .defineInRange("offsetY", defaultY, Integer.MIN_VALUE, Integer.MAX_VALUE);

                builder.pop();
            }
        }
    }

    //COMMON
    private static final ModConfigSpec commonSpec;
    public static final Common COMMON;

    static {
        final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
        commonSpec = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    //CLIENT
    private static final ModConfigSpec clientSpec;
    public static final Client CLIENT;

    static {
        final Pair<Client, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Client::new);
        clientSpec = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    //REGISTRY
    public static void register(final ModLoadingContext context)
    {
        context.registerConfig(ModConfig.Type.COMMON, commonSpec);
        context.registerConfig(ModConfig.Type.CLIENT, clientSpec);
    }

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfigEvent.Loading configEvent)
    {
        if(configEvent.getConfig().getSpec() == TravelersBackpackConfig.commonSpec)
        {
            bakeCommonConfig();
        }
        if(configEvent.getConfig().getSpec() == TravelersBackpackConfig.clientSpec)
        {
            bakeClientConfig();
        }
    }

    public static void bakeCommonConfig()
    {
        //Backpack Settings
        leatherTier = new TravelersBackpackConfig.Common.BackpackSettings.Tier(COMMON.backpackSettings.leather.inventorySlotCount.get(), COMMON.backpackSettings.leather.toolSlotCount.get(), COMMON.backpackSettings.leather.tankCapacity.get());
        ironTier = new TravelersBackpackConfig.Common.BackpackSettings.Tier(COMMON.backpackSettings.iron.inventorySlotCount.get(), COMMON.backpackSettings.iron.toolSlotCount.get(), COMMON.backpackSettings.iron.tankCapacity.get());
        goldTier = new TravelersBackpackConfig.Common.BackpackSettings.Tier(COMMON.backpackSettings.gold.inventorySlotCount.get(), COMMON.backpackSettings.gold.toolSlotCount.get(), COMMON.backpackSettings.gold.tankCapacity.get());
        diamondTier = new TravelersBackpackConfig.Common.BackpackSettings.Tier(COMMON.backpackSettings.diamond.inventorySlotCount.get(), COMMON.backpackSettings.diamond.toolSlotCount.get(), COMMON.backpackSettings.diamond.tankCapacity.get());
        netheriteTier = new TravelersBackpackConfig.Common.BackpackSettings.Tier(COMMON.backpackSettings.netherite.inventorySlotCount.get(), COMMON.backpackSettings.netherite.toolSlotCount.get(), COMMON.backpackSettings.netherite.tankCapacity.get());
        enableTierUpgrades = COMMON.backpackSettings.enableTierUpgrades.get();
        enableCraftingUpgrade = COMMON.backpackSettings.enableCraftingUpgrade.get();
        craftingUpgradeByDefault = COMMON.backpackSettings.craftingUpgradeByDefault.get();
        craftingSavesItems = COMMON.backpackSettings.craftingSavesItems.get();
        enableBackpackBlockWearable = COMMON.backpackSettings.enableBackpackBlockWearable.get();
        enableBackpackRightClickUnequip = COMMON.backpackSettings.enableBackpackRightClickUnequip.get();
        invulnerableBackpack = COMMON.backpackSettings.invulnerableBackpack.get();
        toolSlotsAcceptSwords = COMMON.backpackSettings.toolSlotsAcceptSwords.get();
        toolSlotsAcceptableItems = COMMON.backpackSettings.toolSlotsAcceptableItems.get();
        toolSlotsAcceptEverything = COMMON.backpackSettings.toolSlotsAcceptEverything.get();
        blacklistedItems = COMMON.backpackSettings.blacklistedItems.get();
        allowShulkerBoxes = COMMON.backpackSettings.allowShulkerBoxes.get();
        voidProtection = COMMON.backpackSettings.voidProtection.get();
        backpackDeathPlace = COMMON.backpackSettings.backpackDeathPlace.get();
        backpackForceDeathPlace = COMMON.backpackSettings.backpackForceDeathPlace.get();
        enableSleepingBagSpawnPoint = COMMON.backpackSettings.enableSleepingBagSpawnPoint.get();
        curiosIntegration = COMMON.backpackSettings.curiosIntegration.get();

        //World
        enableLoot = COMMON.world.enableLoot.get();
        spawnEntitiesWithBackpack = COMMON.world.spawnEntitiesWithBackpack.get();
        possibleOverworldEntityTypes = COMMON.world.possibleOverworldEntityTypes.get();
        possibleNetherEntityTypes = COMMON.world.possibleNetherEntityTypes.get();
        spawnChance = COMMON.world.spawnChance.get();
        overworldBackpacks = COMMON.world.overworldBackpacks.get();
        netherBackpacks = COMMON.world.netherBackpacks.get();
        enableVillagerTrade = COMMON.world.enableVillagerTrade.get();

        //Abilities
        enableBackpackAbilities = COMMON.backpackAbilities.enableBackpackAbilities.get();
        forceAbilityEnabled = COMMON.backpackAbilities.forceAbilityEnabled.get();
        allowedAbilities = COMMON.backpackAbilities.allowedAbilities.get();

        //Slowness Debuff
        tooManyBackpacksSlowness = COMMON.slownessDebuff.tooManyBackpacksSlowness.get();
        maxNumberOfBackpacks = COMMON.slownessDebuff.maxNumberOfBackpacks.get();
        slownessPerExcessedBackpack = COMMON.slownessDebuff.slownessPerExcessedBackpack.get();
    }

    public static void bakeClientConfig()
    {
        enableLegacyGui = CLIENT.enableLegacyGui.get();
        enableToolCycling = CLIENT.enableToolCycling.get();
        disableScrollWheel = CLIENT.disableScrollWheel.get();
        obtainTips = CLIENT.obtainTips.get();
        renderTools = CLIENT.renderTools.get();
        renderBackpackWithElytra = CLIENT.renderBackpackWithElytra.get();
        disableBackpackRender = CLIENT.disableBackpackRender.get();

        //Overlay
        enableOverlay = CLIENT.overlay.enableOverlay.get();
        offsetX = CLIENT.overlay.offsetX.get();
        offsetY = CLIENT.overlay.offsetY.get();
    }

    //GATHER DATA
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        boolean includeServer = event.includeServer();

        generator.addProvider(includeServer, new ModRecipeProvider(output));
        generator.addProvider(includeServer, ModLootTableProvider.create(output));
    }
}