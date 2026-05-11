package net.steveson.stackedarmortrimsforgeport;

import com.mojang.logging.LogUtils;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.steveson.stackedarmortrimsforgeport.recipe.ModRecipes;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(StackedArmorTrimsForgeMod.MOD_ID)
public class StackedArmorTrimsForgeMod
{
    //always false because no such mod exists for forge
    public static final Boolean isBetterTrimTooltipsEnables = ModList.get().isLoaded("better-trim-tooltips");


    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "stacked_armor_trims_for_forge";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public StackedArmorTrimsForgeMod(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();


        ModRecipes.register(modEventBus);


        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(new StackedArmorTrimsForgeGameRules());
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {

        }
    }
}
