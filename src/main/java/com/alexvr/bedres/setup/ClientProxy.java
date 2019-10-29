package com.alexvr.bedres.setup;

import com.alexvr.bedres.capability.BedrockFlux;
import com.alexvr.bedres.capability.BedrockFluxStorage;
import com.alexvr.bedres.capability.IBedrockFlux;
import com.alexvr.bedres.gui.ScrapeTankScreen;
import com.alexvr.bedres.multiblocks.bedrockscraper.BedrockScraperScreen;
import com.alexvr.bedres.registry.ModBlocks;
import com.alexvr.bedres.registry.ModFeatures;
import com.alexvr.bedres.world.ModFlowerFeature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.NetherBiome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class ClientProxy implements IProxy{

    @Override
    public void init() {
        FlowersFeature MODFLOWER_FEATURE = new ModFlowerFeature(NoFeatureConfig::deserialize);
        ScreenManager.registerFactory(ModBlocks.scrapeTankContainerType, ScrapeTankScreen::new);
        ScreenManager.registerFactory(ModBlocks.bedrockScraperControllerContainer, BedrockScraperScreen::new);
        CapabilityManager.INSTANCE.register(IBedrockFlux.class, new BedrockFluxStorage(), BedrockFlux::new);

        for (BiomeManager.BiomeType btype : BiomeManager.BiomeType.values()) {
            for (BiomeManager.BiomeEntry biomeEntry : BiomeManager.getBiomes(btype)) {
                biomeEntry.biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Biome.createDecoratedFeature(Feature.ORE, new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE,
                        ModBlocks.enderianOre.getDefaultState(), 6), Placement.COUNT_RANGE, new CountRangeConfig(2,0,0,16)));
                biomeEntry.biome.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Biome.createDecoratedFeature(MODFLOWER_FEATURE, IFeatureConfig.NO_FEATURE_CONFIG, Placement.COUNT_HEIGHTMAP_32, new FrequencyConfig(1)));
                biomeEntry.biome.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, Biome.createDecoratedFeature(ModFeatures.ALTAR, IFeatureConfig.NO_FEATURE_CONFIG, Placement.NOPE, IPlacementConfig.NO_PLACEMENT_CONFIG));
                biomeEntry.biome.addStructure(ModFeatures.ALTAR, IFeatureConfig.NO_FEATURE_CONFIG);

            }
        }

        Biomes.NETHER.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION,NetherBiome.createDecoratedFeature(MODFLOWER_FEATURE, IFeatureConfig.NO_FEATURE_CONFIG, Placement.COUNT_HEIGHTMAP_32, new FrequencyConfig(5)));


    }

    @Override
    public World getClientWorld() {
        return Minecraft.getInstance().world;
    }

    @Override
    public PlayerEntity getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    public Minecraft getMinecraft() {
        return Minecraft.getInstance();
    }


}
