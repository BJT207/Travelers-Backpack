package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blocks.SleepingBagBlock;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlocks
{

    //Backpacks
    public static Block STANDARD_TRAVELERS_BACKPACK;

    public static Block NETHERITE_TRAVELERS_BACKPACK;
    public static Block DIAMOND_TRAVELERS_BACKPACK;
    public static Block GOLD_TRAVELERS_BACKPACK;
    public static Block EMERALD_TRAVELERS_BACKPACK;
    public static Block IRON_TRAVELERS_BACKPACK;
    public static Block LAPIS_TRAVELERS_BACKPACK;
    public static Block REDSTONE_TRAVELERS_BACKPACK;
    public static Block COAL_TRAVELERS_BACKPACK;

    public static Block QUARTZ_TRAVELERS_BACKPACK;
    public static Block BOOKSHELF_TRAVELERS_BACKPACK;
    public static Block END_TRAVELERS_BACKPACK;
    public static Block NETHER_TRAVELERS_BACKPACK;
    public static Block SANDSTONE_TRAVELERS_BACKPACK;
    public static Block SNOW_TRAVELERS_BACKPACK;
    public static Block SPONGE_TRAVELERS_BACKPACK;

    public static Block CAKE_TRAVELERS_BACKPACK;

    public static Block CACTUS_TRAVELERS_BACKPACK;
    public static Block HAY_TRAVELERS_BACKPACK;
    public static Block MELON_TRAVELERS_BACKPACK;
    public static Block PUMPKIN_TRAVELERS_BACKPACK;

    public static Block CREEPER_TRAVELERS_BACKPACK;
    public static Block DRAGON_TRAVELERS_BACKPACK;
    public static Block ENDERMAN_TRAVELERS_BACKPACK;
    public static Block BLAZE_TRAVELERS_BACKPACK;
    public static Block GHAST_TRAVELERS_BACKPACK;
    public static Block MAGMA_CUBE_TRAVELERS_BACKPACK;
    public static Block SKELETON_TRAVELERS_BACKPACK;
    public static Block SPIDER_TRAVELERS_BACKPACK;
    public static Block WITHER_TRAVELERS_BACKPACK;

    public static Block BAT_TRAVELERS_BACKPACK;
    public static Block BEE_TRAVELERS_BACKPACK;
    public static Block WOLF_TRAVELERS_BACKPACK;
    public static Block FOX_TRAVELERS_BACKPACK;
    public static Block OCELOT_TRAVELERS_BACKPACK;
    public static Block HORSE_TRAVELERS_BACKPACK;
    public static Block COW_TRAVELERS_BACKPACK;
    public static Block PIG_TRAVELERS_BACKPACK;
    public static Block SHEEP_TRAVELERS_BACKPACK;
    public static Block CHICKEN_TRAVELERS_BACKPACK;
    public static Block SQUID_TRAVELERS_BACKPACK;
    public static Block VILLAGER_TRAVELERS_BACKPACK;
    public static Block IRON_GOLEM_TRAVELERS_BACKPACK;

    public static Block WHITE_SLEEPING_BAG;
    public static Block ORANGE_SLEEPING_BAG;
    public static Block MAGENTA_SLEEPING_BAG;
    public static Block LIGHT_BLUE_SLEEPING_BAG;
    public static Block YELLOW_SLEEPING_BAG;
    public static Block LIME_SLEEPING_BAG;
    public static Block PINK_SLEEPING_BAG;
    public static Block GRAY_SLEEPING_BAG;
    public static Block LIGHT_GRAY_SLEEPING_BAG;
    public static Block CYAN_SLEEPING_BAG;
    public static Block PURPLE_SLEEPING_BAG;
    public static Block BLUE_SLEEPING_BAG;
    public static Block BROWN_SLEEPING_BAG;
    public static Block GREEN_SLEEPING_BAG;
    public static Block RED_SLEEPING_BAG;
    public static Block BLACK_SLEEPING_BAG;

    public static void init()
    {
        STANDARD_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "standard"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.BROWN).build()).sounds(BlockSoundGroup.WOOL)));

        NETHERITE_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "netherite"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.BLACK).build()).sounds(BlockSoundGroup.NETHERITE)));
        DIAMOND_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "diamond"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.DIAMOND_BLUE).build()).sounds(BlockSoundGroup.METAL)));
        GOLD_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "gold"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.GOLD).build()).sounds(BlockSoundGroup.METAL).luminance(10)));
        EMERALD_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "emerald"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.EMERALD_GREEN).build()).sounds(BlockSoundGroup.METAL)));
        IRON_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "iron"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.IRON_GRAY).build()).sounds(BlockSoundGroup.METAL)));
        LAPIS_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "lapis"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.LAPIS_BLUE).build()).sounds(BlockSoundGroup.STONE)));
        REDSTONE_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "redstone"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.BRIGHT_RED).build()).sounds(BlockSoundGroup.METAL).solidBlock((blockState, blockView, pos) -> false)));
        COAL_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "coal"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.BLACK).build()).sounds(BlockSoundGroup.STONE)));

        QUARTZ_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "quartz"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.OFF_WHITE).build()).sounds(BlockSoundGroup.STONE)));
        BOOKSHELF_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "bookshelf"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.BROWN).build()).sounds(BlockSoundGroup.WOOD)));
        END_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "end"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.GREEN).build()).sounds(BlockSoundGroup.GLASS).luminance(1)));
        NETHER_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "nether"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.DARK_RED).build()).sounds(BlockSoundGroup.NETHER_BRICKS).luminance(11)));
        SANDSTONE_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "sandstone"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.PALE_YELLOW).build()).sounds(BlockSoundGroup.STONE)));
        SNOW_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "snow"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.WHITE).build()).sounds(BlockSoundGroup.SNOW)));
        SPONGE_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "sponge"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.YELLOW).build()).sounds(BlockSoundGroup.GRASS)));

        CAKE_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "cake"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.CLEAR).build()).sounds(BlockSoundGroup.WOOL)));

        CACTUS_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "cactus"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.DARK_GREEN).build()).sounds(BlockSoundGroup.WOOL)));
        HAY_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "hay"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.YELLOW).build()).sounds(BlockSoundGroup.GRASS)));
        MELON_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "melon"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.LIME).build()).sounds(BlockSoundGroup.WOOD)));
        PUMPKIN_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "pumpkin"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.ORANGE).build()).sounds(BlockSoundGroup.WOOD)));

        CREEPER_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "creeper"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.GREEN).build()).sounds(BlockSoundGroup.WOOL)));
        DRAGON_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "dragon"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.TERRACOTTA_BLACK).build()).sounds(BlockSoundGroup.METAL)));
        ENDERMAN_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "enderman"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.BLACK).build()).sounds(BlockSoundGroup.WOOL)));
        BLAZE_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "blaze"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.TERRACOTTA_ORANGE).build()).sounds(BlockSoundGroup.METAL)));
        GHAST_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "ghast"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.LIGHT_GRAY).build()).sounds(BlockSoundGroup.WOOL)));
        MAGMA_CUBE_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "magma_cube"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.DARK_RED).build()).sounds(BlockSoundGroup.SLIME)));
        SKELETON_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "skeleton"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.WHITE).build()).sounds(BlockSoundGroup.BONE)));
        SPIDER_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "spider"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.BLACK).build()).sounds(BlockSoundGroup.WOOL)));
        WITHER_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "wither"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.BLACK).build()).sounds(BlockSoundGroup.BONE)));

        BAT_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "bat"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.BROWN).build()).sounds(BlockSoundGroup.WOOL)));
        BEE_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "bee"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.YELLOW).build()).sounds(BlockSoundGroup.WOOL)));
        WOLF_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "wolf"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.GRAY).build()).sounds(BlockSoundGroup.WOOL)));
        FOX_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "fox"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.TERRACOTTA_ORANGE).build()).sounds(BlockSoundGroup.WOOL)));
        OCELOT_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "ocelot"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.TERRACOTTA_ORANGE).build()).sounds(BlockSoundGroup.WOOL)));
        HORSE_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "horse"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.BROWN).build()).sounds(BlockSoundGroup.WOOL)));
        COW_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "cow"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.BROWN).build()).sounds(BlockSoundGroup.SLIME)));
        PIG_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "pig"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.PINK).build()).sounds(BlockSoundGroup.SLIME)));
        SHEEP_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "sheep"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.TERRACOTTA_WHITE).build()).sounds(BlockSoundGroup.WOOL)));
        SQUID_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "squid"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.TERRACOTTA_BLUE).build()).sounds(BlockSoundGroup.SLIME)));
        CHICKEN_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "chicken"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.TERRACOTTA_WHITE).build()).sounds(BlockSoundGroup.WOOL)));
        VILLAGER_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "villager"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.GRAY).build()).sounds(BlockSoundGroup.WOOL)));
        IRON_GOLEM_TRAVELERS_BACKPACK = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "iron_golem"), new TravelersBackpackBlock(FabricBlockSettings.of(new Material.Builder(MapColor.IRON_GRAY).build()).sounds(BlockSoundGroup.METAL)));

        WHITE_SLEEPING_BAG = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "white_sleeping_bag"), new SleepingBagBlock(DyeColor.WHITE, FabricBlockSettings.of(new Material.Builder(MapColor.WHITE).build()).sounds(BlockSoundGroup.WOOL).strength(0.2F)));
        ORANGE_SLEEPING_BAG = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "orange_sleeping_bag"), new SleepingBagBlock(DyeColor.ORANGE, FabricBlockSettings.of(new Material.Builder(MapColor.ORANGE).build()).sounds(BlockSoundGroup.WOOL).strength(0.2F)));
        MAGENTA_SLEEPING_BAG = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "magenta_sleeping_bag"), new SleepingBagBlock(DyeColor.MAGENTA, FabricBlockSettings.of(new Material.Builder(MapColor.MAGENTA).build()).sounds(BlockSoundGroup.WOOL).strength(0.2F)));
        LIGHT_BLUE_SLEEPING_BAG = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "light_blue_sleeping_bag"), new SleepingBagBlock(DyeColor.LIGHT_BLUE, FabricBlockSettings.of(new Material.Builder(MapColor.LIGHT_BLUE).build()).sounds(BlockSoundGroup.WOOL).strength(0.2F)));
        YELLOW_SLEEPING_BAG = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "yellow_sleeping_bag"), new SleepingBagBlock(DyeColor.YELLOW, FabricBlockSettings.of(new Material.Builder(MapColor.YELLOW).build()).sounds(BlockSoundGroup.WOOL).strength(0.2F)));
        LIME_SLEEPING_BAG = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "lime_sleeping_bag"), new SleepingBagBlock(DyeColor.LIME, FabricBlockSettings.of(new Material.Builder(MapColor.LIME).build()).sounds(BlockSoundGroup.WOOL).strength(0.2F)));
        PINK_SLEEPING_BAG = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "pink_sleeping_bag"), new SleepingBagBlock(DyeColor.PINK, FabricBlockSettings.of(new Material.Builder(MapColor.PINK).build()).sounds(BlockSoundGroup.WOOL).strength(0.2F)));
        GRAY_SLEEPING_BAG = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "gray_sleeping_bag"), new SleepingBagBlock(DyeColor.GRAY, FabricBlockSettings.of(new Material.Builder(MapColor.GRAY).build()).sounds(BlockSoundGroup.WOOL).strength(0.2F)));
        LIGHT_GRAY_SLEEPING_BAG = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "light_gray_sleeping_bag"), new SleepingBagBlock(DyeColor.LIGHT_GRAY, FabricBlockSettings.of(new Material.Builder(MapColor.LIGHT_GRAY).build()).sounds(BlockSoundGroup.WOOL).strength(0.2F)));
        CYAN_SLEEPING_BAG = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "cyan_sleeping_bag"), new SleepingBagBlock(DyeColor.CYAN, FabricBlockSettings.of(new Material.Builder(MapColor.CYAN).build()).sounds(BlockSoundGroup.WOOL).strength(0.2F)));
        PURPLE_SLEEPING_BAG = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "purple_sleeping_bag"), new SleepingBagBlock(DyeColor.PURPLE, FabricBlockSettings.of(new Material.Builder(MapColor.PURPLE).build()).sounds(BlockSoundGroup.WOOL).strength(0.2F)));
        BLUE_SLEEPING_BAG = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "blue_sleeping_bag"), new SleepingBagBlock(DyeColor.BLUE, FabricBlockSettings.of(new Material.Builder(MapColor.BLUE).build()).sounds(BlockSoundGroup.WOOL).strength(0.2F)));
        BROWN_SLEEPING_BAG = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "brown_sleeping_bag"), new SleepingBagBlock(DyeColor.BROWN, FabricBlockSettings.of(new Material.Builder(MapColor.BROWN).build()).sounds(BlockSoundGroup.WOOL).strength(0.2F)));
        GREEN_SLEEPING_BAG = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "green_sleeping_bag"), new SleepingBagBlock(DyeColor.GREEN, FabricBlockSettings.of(new Material.Builder(MapColor.GREEN).build()).sounds(BlockSoundGroup.WOOL).strength(0.2F)));
        RED_SLEEPING_BAG = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "red_sleeping_bag"), new SleepingBagBlock(DyeColor.RED, FabricBlockSettings.of(new Material.Builder(MapColor.RED).build()).sounds(BlockSoundGroup.WOOL).strength(0.2F)));
        BLACK_SLEEPING_BAG = Registry.register(Registry.BLOCK, new Identifier(TravelersBackpack.MODID, "black_sleeping_bag"), new SleepingBagBlock(DyeColor.BLACK, FabricBlockSettings.of(new Material.Builder(MapColor.BLACK).build()).sounds(BlockSoundGroup.WOOL).strength(0.2F)));
    }
}