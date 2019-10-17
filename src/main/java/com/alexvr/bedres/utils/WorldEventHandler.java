package com.alexvr.bedres.utils;

import com.alexvr.bedres.BedrockResources;
import com.alexvr.bedres.capability.BedrockFluxProvider;
import com.alexvr.bedres.capability.IBedrockFlux;
import com.alexvr.bedres.gui.FluxOracleScreen;
import com.alexvr.bedres.items.FluxOracle;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.text.DecimalFormat;

@EventBusSubscriber(modid = BedrockResources.MODID, value = Dist.CLIENT)
public class WorldEventHandler {

    public static FluxOracleScreen fx = new FluxOracleScreen(new StringTextComponent("fluxScreen"));
    static Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    static void renderWorldLastEvent(RenderWorldLastEvent evt) {
        if(mc.player.getHeldItemMainhand().getItem() instanceof FluxOracle && ((FluxOracle)mc.player.getHeldItemMainhand().getItem()).beingUsed) {
            mc.displayGuiScreen(fx);

        }

    }

    @SubscribeEvent
    public static void onPlayerLogsIn(PlayerEvent.PlayerLoggedInEvent event)
    {

        PlayerEntity player = event.getPlayer();
        LazyOptional<IBedrockFlux> bedrockFlux = player.getCapability(BedrockFluxProvider.BEDROCK_FLUX_CAPABILITY, null);

        bedrockFlux.ifPresent(h -> {

            String message = String.format("Hello there, you have %s flux.",h.getBedrockFluxString());
            player.sendStatusMessage(new StringTextComponent(message),false);

        });


    }

    @SubscribeEvent
    public static void PlayerWakeUpEvent(PlayerSleepInBedEvent event)
    {
        PlayerEntity player = event.getEntityPlayer();

        if (player.world.isRemote) return;

        LazyOptional<IBedrockFlux> bedrockFlux = player.getCapability(BedrockFluxProvider.BEDROCK_FLUX_CAPABILITY, null);

        bedrockFlux.ifPresent(h -> {

            String message = ("You hear whispers as you wake up from bed.");

            h.fill(100);
            player.sendStatusMessage(new StringTextComponent(message),true);
            player.sendStatusMessage(new StringTextComponent(TextFormatting.RED + new TranslationTextComponent("message.bedres.whispers").getUnformattedComponentText()), false);
            player.world.addEntity(new LightningBoltEntity(player.world,player.posX,player.posY,player.posZ,true));


        });

    }

    @SubscribeEvent
    public static void onPlayerFalls(LivingFallEvent event)
    {
        Entity entity = event.getEntity();

        if (entity.world.isRemote || !(entity instanceof PlayerEntity) || event.getDistance() < 3) return;

        PlayerEntity player = (PlayerEntity) entity;
        LazyOptional<IBedrockFlux> bedrockFlux = player.getCapability(BedrockFluxProvider.BEDROCK_FLUX_CAPABILITY, null);

        bedrockFlux.ifPresent(h -> {
            float points = h.getBedrockFlux();
            float cost = event.getDistance() * 2;

            if (points > cost)
            {


                h.consume(cost);

                String number2AsString = new DecimalFormat("#.00").format(cost);
                String message = String.format("You absorbed fall damage. It costed %s mana, you have %s mana left.", number2AsString, h.getBedrockFluxString());
                player.sendStatusMessage(new StringTextComponent(message),true);

                event.setCanceled(true);
            }

        });

    }

    /**
     * Copy data from dead player to the new player
     */
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event)
    {
        PlayerEntity player = event.getEntityPlayer();
        LazyOptional<IBedrockFlux> bedrockFlux = player.getCapability(BedrockFluxProvider.BEDROCK_FLUX_CAPABILITY, null);
        LazyOptional<IBedrockFlux> oldbedrockFlux =  event.getOriginal().getCapability(BedrockFluxProvider.BEDROCK_FLUX_CAPABILITY, null);

        bedrockFlux.ifPresent(h -> oldbedrockFlux.ifPresent(o -> h.set(o.getBedrockFlux())));
    }
}
