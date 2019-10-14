package com.alexvr.bedres;

import com.alexvr.bedres.blocks.ScrapeTank;
import com.alexvr.bedres.capability.BedrockFluxProvider;
import com.alexvr.bedres.capability.IBedrockFlux;
import com.alexvr.bedres.containers.ScrapeTankContainer;
import com.alexvr.bedres.items.BedrockScrape;
import com.alexvr.bedres.registry.ModBlocks;
import com.alexvr.bedres.setup.ClientProxy;
import com.alexvr.bedres.setup.IProxy;
import com.alexvr.bedres.setup.ModSetup;
import com.alexvr.bedres.setup.ServerProxy;
import com.alexvr.bedres.tiles.ScrapeTankTile;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(BedrockResources.MODID)
public class BedrockResources {


    public static final String MODID = "bedres";

    public static IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(),() -> () -> new ServerProxy());

    private static final Logger LOGGER = LogManager.getLogger();

    public static ModSetup setup = new ModSetup();

    public BedrockResources() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);


    }

    private void setup(final FMLCommonSetupEvent event) {
        setup.init();
        proxy.init();
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
            event.getRegistry().register(new ScrapeTank());
        }

        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> event) {
            Item.Properties properties = new Item.Properties()
                    .group(setup.itemgroup);
            event.getRegistry().register(new BlockItem(ModBlocks.scrapeTank, properties).setRegistryName("scrape_tank"));
            event.getRegistry().register(new BedrockScrape());

        }

        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
            event.getRegistry().register(TileEntityType.Builder.create(ScrapeTankTile::new, ModBlocks.scrapeTank).build(null).setRegistryName("scrape_tank"));

        }

        @SubscribeEvent
        public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> event) {
            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                return new ScrapeTankContainer(windowId,BedrockResources.proxy.getClientWorld(),data.readBlockPos(),inv,BedrockResources.proxy.getClientPlayer());
            }).setRegistryName("scrape_tank"));

        }



    }


}
