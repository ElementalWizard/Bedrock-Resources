package com.alexvr.bedres.capability;

import com.alexvr.bedres.BedrockResources;
import com.alexvr.bedres.capability.abilities.PlayerAbilityProvider;
import com.alexvr.bedres.capability.bedrock_flux.BedrockFluxProvider;
import com.alexvr.bedres.utils.References;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

/**
 * Capability handler
 *
 * This class is responsible for attaching our capabilities
 */

@EventBusSubscriber
public class CapabilityHandler
{
    private static final ResourceLocation FLUX_CAP = new ResourceLocation(BedrockResources.MODID, References.FLUX_CAPABILITY_NAME_RESOURCE);
    private static final ResourceLocation ABILITY_CAP = new ResourceLocation(BedrockResources.MODID, References.PLAYER_ABILITY_CAP_NAME_RESOURCE);

    @SubscribeEvent
    public static void attachCapability(AttachCapabilitiesEvent<Entity> event)
    {
        if (!(event.getObject() instanceof PlayerEntity)) return;

        event.addCapability(FLUX_CAP, new BedrockFluxProvider());
        event.addCapability(ABILITY_CAP, new PlayerAbilityProvider());
    }
}