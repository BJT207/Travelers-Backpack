package com.tiviacz.travelersbackpack.loot;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class AddItemModifier extends LootModifier
{
    public static final Supplier<MapCodec<AddItemModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder
            .mapCodec(inst -> codecStart(inst).and(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(m -> m.item))
                    .apply(inst, AddItemModifier::new)));

    private final Item item;

    protected AddItemModifier(LootItemCondition[] conditionsIn, Item item)
    {
        super(conditionsIn);
        this.item = item;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context)
    {
        if(!TravelersBackpackConfig.COMMON.enableLoot.get()) return generatedLoot;

        if(context.getQueriedLootTableId().equals(BuiltInLootTables.ABANDONED_MINESHAFT.location()))
        {
            if(this.item == ModItems.BAT_TRAVELERS_BACKPACK.get() && context.getRandom().nextFloat() <= 0.05F)
            {
                generatedLoot.add(new ItemStack(item));
            }

            if(this.item == ModItems.STANDARD_TRAVELERS_BACKPACK.get() && context.getRandom().nextFloat() <= 0.06F)
            {
                generatedLoot.add(new ItemStack(item));
            }

            if(this.item == ModItems.IRON_TIER_UPGRADE.get() && context.getRandom().nextFloat() <= 0.05F)
            {
                generatedLoot.add(new ItemStack(item));
            }

            if(this.item == ModItems.GOLD_TIER_UPGRADE.get() && context.getRandom().nextFloat() <= 0.04F)
            {
                generatedLoot.add(new ItemStack(item));
            }
        }

        if(context.getQueriedLootTableId().equals(BuiltInLootTables.VILLAGE_ARMORER.location()))
        {
            if(item == ModItems.IRON_GOLEM_TRAVELERS_BACKPACK.get() && context.getRandom().nextFloat() <= 0.1F)
            {
                generatedLoot.add(new ItemStack(item));
            }
        }

        if(context.getQueriedLootTableId().equals(BuiltInLootTables.SIMPLE_DUNGEON.location()))
        {
            if(item == ModItems.STANDARD_TRAVELERS_BACKPACK.get() && context.getRandom().nextFloat() <= 0.06F)
            {
                generatedLoot.add(new ItemStack(item));
            }

            if(this.item == ModItems.IRON_TIER_UPGRADE.get() && context.getRandom().nextFloat() <= 0.05F)
            {
                generatedLoot.add(new ItemStack(item));
            }
        }

        if(context.getQueriedLootTableId().equals(BuiltInLootTables.DESERT_PYRAMID.location()))
        {
            if(item == ModItems.STANDARD_TRAVELERS_BACKPACK.get() && context.getRandom().nextFloat() <= 0.06F)
            {
                generatedLoot.add(new ItemStack(item));
            }

            if(this.item == ModItems.IRON_TIER_UPGRADE.get() && context.getRandom().nextFloat() <= 0.05F)
            {
                generatedLoot.add(new ItemStack(item));
            }

            if(this.item == ModItems.GOLD_TIER_UPGRADE.get() && context.getRandom().nextFloat() <= 0.04F)
            {
                generatedLoot.add(new ItemStack(item));
            }
        }

        if(context.getQueriedLootTableId().equals(BuiltInLootTables.SHIPWRECK_TREASURE.location()))
        {
            if(this.item == ModItems.IRON_TIER_UPGRADE.get() && context.getRandom().nextFloat() <= 0.06F)
            {
                generatedLoot.add(new ItemStack(item));
            }

            if(this.item == ModItems.GOLD_TIER_UPGRADE.get() && context.getRandom().nextFloat() <= 0.05F)
            {
                generatedLoot.add(new ItemStack(item));
            }
        }

        if(context.getQueriedLootTableId().equals(BuiltInLootTables.WOODLAND_MANSION.location()))
        {
            if(this.item == ModItems.IRON_TIER_UPGRADE.get() && context.getRandom().nextFloat() <= 0.06F)
            {
                generatedLoot.add(new ItemStack(item));
            }

            if(this.item == ModItems.GOLD_TIER_UPGRADE.get() && context.getRandom().nextFloat() <= 0.05F)
            {
                generatedLoot.add(new ItemStack(item));
            }
        }

        if(context.getQueriedLootTableId().equals(BuiltInLootTables.NETHER_BRIDGE.location()))
        {
            if(this.item == ModItems.IRON_TIER_UPGRADE.get() && context.getRandom().nextFloat() <= 0.07F)
            {
                generatedLoot.add(new ItemStack(item));
            }

            if(this.item == ModItems.GOLD_TIER_UPGRADE.get() && context.getRandom().nextFloat() <= 0.06F)
            {
                generatedLoot.add(new ItemStack(item));
            }
        }

        if(context.getQueriedLootTableId().equals(BuiltInLootTables.BASTION_TREASURE.location()))
        {
            if(this.item == ModItems.IRON_TIER_UPGRADE.get() && context.getRandom().nextFloat() <= 0.07F)
            {
                generatedLoot.add(new ItemStack(item));
            }

            if(this.item == ModItems.GOLD_TIER_UPGRADE.get() && context.getRandom().nextFloat() <= 0.06F)
            {
                generatedLoot.add(new ItemStack(item));
            }
        }

        if(context.getQueriedLootTableId().equals(BuiltInLootTables.END_CITY_TREASURE.location()))
        {
            if(this.item == ModItems.GOLD_TIER_UPGRADE.get() && context.getRandom().nextFloat() <= 0.07F)
            {
                generatedLoot.add(new ItemStack(item));
            }

            if(this.item == ModItems.DIAMOND_TIER_UPGRADE.get() && context.getRandom().nextFloat() <= 0.06F)
            {
                generatedLoot.add(new ItemStack(item));
            }
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec()
    {
        return CODEC.get();
    }
}