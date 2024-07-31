package com.tiviacz.travelersbackpack;

import com.tiviacz.travelersbackpack.compat.accessories.TravelersBackpackAccessory;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpack.handlers.ModClientEventHandler;
import com.tiviacz.travelersbackpack.init.*;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.ResourceUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("travelersbackpack")
public class TravelersBackpack
{
    public static final String MODID = "travelersbackpack";
    public static final Logger LOGGER = LogManager.getLogger();

    public static boolean accessoriesLoaded;
    public static boolean craftingTweaksLoaded;

    public static boolean corpseLoaded;
    public static boolean gravestoneLoaded;

    public static boolean comfortsLoaded;
    public static boolean endermanOverhaulLoaded;

    public TravelersBackpack(IEventBus eventBus, ModContainer modContainer)
    {
        NeoForgeMod.enableMilkFluid();

        modContainer.registerConfig(ModConfig.Type.SERVER, TravelersBackpackConfig.serverSpec);
        modContainer.registerConfig(ModConfig.Type.COMMON, TravelersBackpackConfig.commonSpec);
        modContainer.registerConfig(ModConfig.Type.CLIENT, TravelersBackpackConfig.clientSpec);

        if(FMLEnvironment.dist == Dist.CLIENT) modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        eventBus.addListener(this::setup);
        eventBus.addListener(this::doClientStuff);
        eventBus.addListener(this::onFinish);

        ModItems.ITEMS.register(eventBus);
        ModBlocks.BLOCKS.register(eventBus);
        ModBlockEntityTypes.BLOCK_ENTITY_TYPES.register(eventBus);
        ModMenuTypes.MENU_TYPES.register(eventBus);
        ModRecipeSerializers.SERIALIZERS.register(eventBus);
        ModFluids.FLUID_TYPES.register(eventBus);
        ModFluids.FLUIDS.register(eventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(eventBus);
        ModLootModifiers.LOOT_MODIFIER_SERIALIZERS.register(eventBus);
        ModAttachmentTypes.ATTACHMENT_TYPES.register(eventBus);
        ModDataComponents.DATA_COMPONENT_TYPES.register(eventBus);

        accessoriesLoaded = ModList.get().isLoaded("accessories");
        craftingTweaksLoaded = ModList.get().isLoaded("craftingtweaks");

        corpseLoaded = ModList.get().isLoaded("corpse");
        gravestoneLoaded = ModList.get().isLoaded("gravestone");

        comfortsLoaded = ModList.get().isLoaded("comforts");
        endermanOverhaulLoaded = ModList.get().isLoaded("endermanoverhaul");
    }
    //#TODO accessory here
    private void setup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() ->
        {
            EffectFluidRegistry.initEffects();
            enableCraftingTweaks();
            TravelersBackpackItem.registerCauldronInteraction();
            if(accessoriesLoaded) TravelersBackpackAccessory.init();
        });
    }

    private void doClientStuff(final FMLClientSetupEvent event)
    {
        ModClientEventHandler.registerBlockEntityRenderers();
        ModClientEventHandler.registerItemModelProperties();
        if(accessoriesLoaded) TravelersBackpackAccessory.clientInit();
    }

    private void onFinish(final FMLLoadCompleteEvent event)
    {
        ModItems.addBackpacksToList();
        ResourceUtils.createTextureLocations();
        ResourceUtils.createSleepingBagTextureLocations();
    }

    public static boolean enableAccessories()
    {
        return accessoriesLoaded && TravelersBackpackConfig.SERVER.backpackSettings.accessoriesIntegration.get();
    }

    public static void enableCraftingTweaks()
    {
        if(craftingTweaksLoaded)
        {
            try {
                Class.forName("com.tiviacz.travelersbackpack.compat.craftingtweaks.TravelersBackpackCraftingGridProvider").getConstructor().newInstance();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isAnyGraveModInstalled()
    {
        return TravelersBackpack.corpseLoaded || TravelersBackpack.gravestoneLoaded;
    }
}