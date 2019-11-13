package com.alexvr.bedres.utils;

import com.alexvr.bedres.BedrockResources;
import com.alexvr.bedres.blocks.tiles.EnderianRitualPedestalTile;
import com.alexvr.bedres.capability.abilities.IPlayerAbility;
import com.alexvr.bedres.capability.abilities.PlayerAbilityProvider;
import com.alexvr.bedres.capability.bedrock_flux.BedrockFluxProvider;
import com.alexvr.bedres.capability.bedrock_flux.IBedrockFlux;
import com.alexvr.bedres.gui.FluxOracleScreen;
import com.alexvr.bedres.gui.FluxOracleScreenGui;
import com.alexvr.bedres.items.FluxOracle;
import com.alexvr.bedres.registry.ModBlocks;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.*;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import static com.alexvr.bedres.utils.RitalCrafting.*;

@EventBusSubscriber(modid = BedrockResources.MODID, value = Dist.CLIENT)
public class WorldEventHandler {

    public static FluxOracleScreenGui fxG = new FluxOracleScreenGui();
    static Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    static void renderWorldLastEvent(RenderWorldLastEvent evt) {
        if(mc.player.getHeldItemMainhand().getItem() instanceof FluxOracle && ((FluxOracle)mc.player.getHeldItemMainhand().getItem()).beingUsed) {
            mc.displayGuiScreen(fxG);

        }

    }

    @SubscribeEvent
    public static void onInitGuiEvent(final InitGuiEvent event) {
        final Screen gui = event.getGui();
        if (gui instanceof FluxOracleScreenGui) {

        }
    }


    @SubscribeEvent
    public static void onPlayerLogsIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        PlayerEntity player = event.getPlayer();
        LazyOptional<IBedrockFlux> bedrockFlux = player.getCapability(BedrockFluxProvider.BEDROCK_FLUX_CAPABILITY, null);
        bedrockFlux.ifPresent(flux -> {
            player.sendStatusMessage(new StringTextComponent(String.format(TextFormatting.AQUA + " %s" + TextFormatting.DARK_RED+" Flux",flux.getBedrockFluxString())),false);
            if (flux.getCrafterFlux()){
                flux.setScreen((FluxOracleScreen)BedrockResources.proxy.getMinecraft().ingameGUI);
                flux.getScreen().flux = flux;
                BedrockResources.proxy.getMinecraft().ingameGUI=flux.getScreen();
            }
        });
    }

    static ArrayList RECEPI = new ArrayList(){{

        add(DIAMOND_PICKAXE_UPGRADE);
        add(GOLD_PICKAXE_UPGRADE);
        add(IRON_PICKAXE_UPGRADE);
        add(STONE_PICKAXE_UPGRADE);
        add(WOOD_PICKAXE_UPGRADE);

        add(DIAMOND_AXE_UPGRADE);
        add(GOLD_AXE_UPGRADE);
        add(IRON_AXE_UPGRADE);
        add(STONE_AXE_UPGRADE);
        add(WOOD_AXE_UPGRADE);

        add(DIAMOND_SHOVEL_UPGRADE);
        add(GOLD_SHOVEL_UPGRADE);
        add(IRON_SHOVEL_UPGRADE);
        add(STONE_SHOVEL_UPGRADE);
        add(WOOD_SHOVEL_UPGRADE);

        add(DIAMOND_SWORD_UPGRADE);
        add(GOLD_SWORD_UPGRADE);
        add(IRON_SWORD_UPGRADE);
        add(STONE_SWORD_UPGRADE);
        add(WOOD_SWORD_UPGRADE);

        add(ACTIVE_HOE_UPGRADE);

        add(ACTIVE_SPEED_UPGRADE);

        add(ACTIVE_JUMP_UPGRADE);

        add(ACTIVE_GRAVITY_UPGRADE);

        add(CLEAR_UPGRADE);

    }};


    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event){
        BlockPos playerPos = new BlockPos(event.getEntityLiving().posX,event.getEntityLiving().posY,event.getEntityLiving().posZ);
        LazyOptional<IPlayerAbility> abilities = event.getEntityLiving().getCapability(PlayerAbilityProvider.PLAYER_ABILITY_CAPABILITY, null);
        abilities.ifPresent(iPlayerAbility -> {
            if (!iPlayerAbility.getChecking() && !iPlayerAbility.getInRitual()){
                if (event.getEntityLiving() instanceof PlayerEntity && event.getSource() == DamageSource.IN_FIRE && event.getEntityLiving().world.getBlockState(playerPos.offset(Direction.DOWN)).getBlock() == ModBlocks.enderianBlock){
                    ArrayList<EnderianRitualPedestalTile> listOfTIles;
                    iPlayerAbility.flipChecking();
                    for (int i =0;i<RECEPI.size();i++) {

                        listOfTIles = new ArrayList<>();
                        boolean skip = false;
                        for (int x = -3; x < 4; x++) {
                            if (skip){
                                break;
                            }
                            for (int y = -3; y < 4; y++) {
                                if (x==0&&y==0){
                                    continue;
                                }
                                Character key = ((String)((ArrayList)((ArrayList)RECEPI.get(i)).get(1)).get(x+3)).charAt(y+3);

                                if (key == ' '){

                                    if (event.getEntityLiving().world.getBlockState(playerPos.east(x).south(y).down()).getBlock() != ModBlocks.enderianBrick ){

                                        skip =true;
                                        break;
                                    }
                                    continue;
                                }
                                ItemStack stack = ItemStack.EMPTY;
                                for (int j =0;j<((ArrayList)((ArrayList)RECEPI.get(i)).get(2)).size();j++) {
                                    Character value = ((String)((ArrayList)((ArrayList)RECEPI.get(i)).get(2)).get(j)).charAt(0);

                                    if (key == value){
                                        String ss = ((String)((ArrayList)((ArrayList)RECEPI.get(i)).get(2)).get(j)).substring(2);
                                        ResourceLocation locatoin = new ResourceLocation(ss);
                                        stack = new ItemStack( ForgeRegistries.ITEMS.getValue(locatoin));

                                        break;
                                    }
                                }
                                if (stack == ItemStack.EMPTY || stack.getItem().getRegistryName().equals(ItemStack.EMPTY.getItem().getRegistryName())){

                                    skip =true;
                                    break;
                                }
                                if (stack.getItem().getRegistryName().equals(new ItemStack((ModBlocks.bedrockWire)).getItem().getRegistryName()) &&event.getEntityLiving().world.getBlockState(playerPos.east(x).south(y)).getBlock() != ModBlocks.bedrockWire) {

                                    skip = true;
                                    break;
                                }
                                if (!stack.getItem().getRegistryName().equals(new ItemStack((ModBlocks.bedrockWire)).getItem().getRegistryName()) &&event.getEntityLiving().world.getBlockState(playerPos.east(x).south(y)).getBlock() != ModBlocks.enderianRitualPedestal) {
                                    skip = true;
                                    break;
                                }else if (!stack.getItem().getRegistryName().equals(new ItemStack((ModBlocks.bedrockWire)).getItem().getRegistryName()) &&event.getEntityLiving().world.getBlockState(playerPos.east(x).south(y)).getBlock() == ModBlocks.enderianRitualPedestal) {

                                    if (event.getEntityLiving().world.getTileEntity(playerPos.east(x).south(y)) instanceof EnderianRitualPedestalTile && !((EnderianRitualPedestalTile)event.getEntityLiving().world.getTileEntity(playerPos.east(x).south(y))).item.equals(stack.getItem().getRegistryName().toString())){

                                        skip = true;
                                        break;
                                    }else{

                                        listOfTIles.add(((EnderianRitualPedestalTile)event.getEntityLiving().world.getTileEntity(playerPos.east(x).south(y))));
                                    }
                                }
                            }
                        }


                        if(!skip){

                            event.getEntityLiving().world.setBlockState(playerPos,Blocks.AIR.getDefaultState());
                            iPlayerAbility.setRitualCraftingResult((String)(((ArrayList)RECEPI.get(i)).get(0)));
                            iPlayerAbility.setRitualPedestals(listOfTIles);
                            iPlayerAbility.flipRitual();
                            iPlayerAbility.setRitualTotalTimer(listOfTIles.size()*120);
                            iPlayerAbility.setFOV(Minecraft.getInstance().gameSettings.fov);
                            event.getEntityLiving().lookAt(EntityAnchorArgument.Type.EYES,new Vec3d(.999,event.getEntityLiving().getLookVec().y+6,-0.999));
                            event.getEntityLiving().setFire(0);
                            iPlayerAbility.setLookPos(new Vec3d(listOfTIles.get(0).getPos().getX(),listOfTIles.get(0).getPos().getY()+2,listOfTIles.get(0).getPos().getZ()));
                            event.setCanceled(true);
                        }
                    }
                    iPlayerAbility.flipChecking();
                }
            }
        });
    }


    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        PlayerEntity player = event.player;

        if (player.world.isRemote) return;
        LazyOptional<IPlayerAbility> abilities = player.getCapability(PlayerAbilityProvider.PLAYER_ABILITY_CAPABILITY, null);
        abilities.ifPresent(iPlayerAbility -> {
            if (iPlayerAbility.getInRitual()){
                KeyBinding.unPressAllKeys();
                player.setVelocity(0,0,0);
                player.setJumping(false);
                player.addPotionEffect(new EffectInstance(Effects.SLOWNESS,1,999));
                Minecraft.getInstance().gameSettings.thirdPersonView = 2;
                Minecraft.getInstance().gameSettings.hideGUI = true;
                Minecraft.getInstance().gameSettings.fov = 195;
                Minecraft.getInstance().gameSettings.mouseSensitivity = -1F/3F;
                BlockPos thisblock = iPlayerAbility.getListOfPedestals().get(0).getPos();

                if (iPlayerAbility.getListOfPedestals().size()>1){
                    BlockPos nextblock = iPlayerAbility.getListOfPedestals().get(1).getPos();
                    double xDif =((thisblock.getX()-nextblock.getX())/120.0)  ;
                    double zDif = ((thisblock.getZ()-nextblock.getZ())/120.0)  ;
                    double speed = ((0.1 * iPlayerAbility.getRitualTimer()%60) /10);
                    if (xDif >= 0) {
                        iPlayerAbility.setLookPos(iPlayerAbility.getlookPos().add(speed, 0, 0));
                    } else {
                        iPlayerAbility.setLookPos(iPlayerAbility.getlookPos().add(-speed, 0, 0));
                    }

                    if (zDif >= 0) {
                        iPlayerAbility.setLookPos(iPlayerAbility.getlookPos().add(0, 0, speed));
                    } else {
                        iPlayerAbility.setLookPos(iPlayerAbility.getlookPos().add(0, 0, -speed));
                    }
                    player.lookAt(EntityAnchorArgument.Type.EYES,iPlayerAbility.getlookPos());
                }else{
                    BlockPos nextblock = iPlayerAbility.getListOfPedestals().get(0).getPos().west(2).north(1);
                    double xDif =((thisblock.getX()-nextblock.getX())/120.0)  ;
                    double zDif = ((thisblock.getZ()-nextblock.getZ())/120.0)  ;
                    double speed = ((0.1 * iPlayerAbility.getRitualTimer()%60) /10);
                    if (xDif >= 0) {
                        iPlayerAbility.setLookPos(iPlayerAbility.getlookPos().add(speed, 0, 0));
                    } else {
                        iPlayerAbility.setLookPos(iPlayerAbility.getlookPos().add(-speed, 0, 0));
                    }

                    if (zDif >= 0) {
                        iPlayerAbility.setLookPos(iPlayerAbility.getlookPos().add(0, 0, speed));
                    } else {
                        iPlayerAbility.setLookPos(iPlayerAbility.getlookPos().add(0, 0, -speed));
                    }
                    player.lookAt(EntityAnchorArgument.Type.EYES,iPlayerAbility.getlookPos());
                }

                BlockPos particlePos =  iPlayerAbility.getListOfPedestals().get(0).getPos();
                Minecraft.getInstance().worldRenderer.addParticle(ParticleTypes.PORTAL,false,(double)particlePos.getX()+0.5,(double)particlePos.getY()+.8,(double)particlePos.getZ()+.5,(new Random().nextFloat()-0.7),new Random().nextFloat()-0.7,new Random().nextFloat()-0.7);
                if (iPlayerAbility.getRitualTimer()%120 == 0){
                    iPlayerAbility.getListOfPedestals().get(0).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
                        if (h.getStackInSlot(0) != ItemStack.EMPTY) {
                            h.extractItem(0,1, false);
                            ( iPlayerAbility.getListOfPedestals().get(0)).markDirty();
                            ( iPlayerAbility.getListOfPedestals().get(0)).sendUpdates();
                        }
                    });
                    iPlayerAbility.getListOfPedestals().remove(0);
                }
                if (iPlayerAbility.getRitualTimer()>=iPlayerAbility.getRitualTotalTimer()){
                    BlockPos playerPos = new BlockPos(event.player.posX,event.player.posY,event.player.posZ);

                    for (int x = -3; x < 4; x++) {

                        for (int y = -3; y < 4; y++) {
                            if (event.player.world.getBlockState(playerPos.east(x).south(y)).getBlock() == ModBlocks.bedrockWire){
                                player.world.setBlockState(playerPos.east(x).south(y),Blocks.FIRE.getDefaultState());
                            }
                        }
                    }
                    Minecraft.getInstance().worldRenderer.addParticle(ParticleTypes.PORTAL,false,(double)player.posX,(double)player.posY+3,(double)player.posZ,new Random().nextFloat()-0.5,new Random().nextFloat()-0.5,new Random().nextFloat()-0.5);
                    Minecraft.getInstance().worldRenderer.addParticle(ParticleTypes.PORTAL,false,(double)player.posX,(double)player.posY+3,(double)player.posZ,new Random().nextFloat()-0.5,new Random().nextFloat()-0.5,new Random().nextFloat()-0.5);
                    Minecraft.getInstance().worldRenderer.addParticle(ParticleTypes.PORTAL,false,(double)player.posX,(double)player.posY+3,(double)player.posZ,new Random().nextFloat()-0.5,new Random().nextFloat()-0.5,new Random().nextFloat()-0.5);
                    iPlayerAbility.flipRitual();
                    Minecraft.getInstance().gameSettings.mouseSensitivity = 0.5D;
                    event.player.world.addEntity(new LightningBoltEntity(event.player.world,player.posX,player.posY,player.posZ,true));
                    Minecraft.getInstance().gameSettings.thirdPersonView = 0;
                    Minecraft.getInstance().gameSettings.hideGUI = false;
                    Minecraft.getInstance().gameSettings.fov = iPlayerAbility.getFOV();
                    if (iPlayerAbility.getRitualCraftingResult().contains("Upgrade")) {
                        if (iPlayerAbility.getRitualCraftingResult().contains("stickUpgrade")) {
                            iPlayerAbility.clear();
                        }else if (iPlayerAbility.getRitualCraftingResult().contains("pickUpgrade")) {
                            if (iPlayerAbility.getRitualCraftingResult().contains("wood")) {
                                iPlayerAbility.setPick("wood");
                            } else if (iPlayerAbility.getRitualCraftingResult().contains("iron")) {
                                iPlayerAbility.setPick("iron");
                            } else if (iPlayerAbility.getRitualCraftingResult().contains("gold")) {
                                iPlayerAbility.setPick("golden");
                            } else if (iPlayerAbility.getRitualCraftingResult().contains("diamond")) {
                                iPlayerAbility.setPick("diamond");
                            }
                        } else if (iPlayerAbility.getRitualCraftingResult().contains("axeUpgrade")) {
                            if (iPlayerAbility.getRitualCraftingResult().contains("wood")) {
                                iPlayerAbility.setAxe("wood");
                            } else if (iPlayerAbility.getRitualCraftingResult().contains("iron")) {
                                iPlayerAbility.setAxe("iron");
                            } else if (iPlayerAbility.getRitualCraftingResult().contains("gold")) {
                                iPlayerAbility.setAxe("golden");
                            } else if (iPlayerAbility.getRitualCraftingResult().contains("diamond")) {
                                iPlayerAbility.setAxe("diamond");
                            }
                        } else if (iPlayerAbility.getRitualCraftingResult().contains("shovelUpgrade")) {
                            if (iPlayerAbility.getRitualCraftingResult().contains("wood")) {
                                iPlayerAbility.setShovel("wood");
                            } else if (iPlayerAbility.getRitualCraftingResult().contains("iron")) {
                                iPlayerAbility.setShovel("iron");
                            } else if (iPlayerAbility.getRitualCraftingResult().contains("gold")) {
                                iPlayerAbility.setShovel("golden");
                            } else if (iPlayerAbility.getRitualCraftingResult().contains("diamond")) {
                                iPlayerAbility.setShovel("diamond");
                            }
                        } else if (iPlayerAbility.getRitualCraftingResult().contains("swordUpgrade")) {
                            if (iPlayerAbility.getRitualCraftingResult().contains("wood")) {
                                iPlayerAbility.setSword("wood");
                            } else if (iPlayerAbility.getRitualCraftingResult().contains("iron")) {
                                iPlayerAbility.setSword("iron");
                            } else if (iPlayerAbility.getRitualCraftingResult().contains("gold")) {
                                iPlayerAbility.setSword("golden");
                            } else if (iPlayerAbility.getRitualCraftingResult().contains("diamond")) {
                                iPlayerAbility.setSword("diamond");
                            }
                        } else if (iPlayerAbility.getRitualCraftingResult().contains("hoeUpgrade")) {
                            iPlayerAbility.setHoe("active");
                        } else if (iPlayerAbility.getRitualCraftingResult().contains("speedUpgrade")) {
                            iPlayerAbility.setMiningSpeedBoost(iPlayerAbility.getMiningSpeedBoost() + 5);
                        } else if (iPlayerAbility.getRitualCraftingResult().contains("gravityUpgrade")) {
                            iPlayerAbility.setGRavityMultiplier(iPlayerAbility.getGravityMultiplier() + 2.5f);
                        } else if (iPlayerAbility.getRitualCraftingResult().contains("jumpUpgrade")) {
                            iPlayerAbility.addJump(1.2f);
                        }
                        player.sendStatusMessage(new StringTextComponent(TextFormatting.DARK_RED + new TranslationTextComponent("message.bedres.stat_change").getUnformattedComponentText()), true);
                        player.sendStatusMessage(new StringTextComponent(TextFormatting.DARK_RED + "Skills is:"),false);
                        player.sendStatusMessage(new StringTextComponent(String.format(TextFormatting.AQUA + " %s" + TextFormatting.DARK_RED+" Sword",iPlayerAbility.getSword())),false);
                        player.sendStatusMessage(new StringTextComponent(String.format(TextFormatting.AQUA + " %s" + TextFormatting.DARK_RED+" Axe",iPlayerAbility.getAxe())),false);
                        player.sendStatusMessage(new StringTextComponent(String.format(TextFormatting.AQUA + " %s" + TextFormatting.DARK_RED+" Shovel",iPlayerAbility.getShovel())),false);
                        player.sendStatusMessage(new StringTextComponent(String.format(TextFormatting.AQUA + " %s" + TextFormatting.DARK_RED+" Hoe",iPlayerAbility.getHoe())),false);
                        player.sendStatusMessage(new StringTextComponent(String.format(TextFormatting.AQUA + " %s" + TextFormatting.DARK_RED+" Pickaxe",iPlayerAbility.getPick())),false);
                        player.sendStatusMessage(new StringTextComponent(TextFormatting.DARK_RED + "Passive: "),false);
                        player.sendStatusMessage(new StringTextComponent(String.format(TextFormatting.AQUA + " %s" + TextFormatting.DARK_RED+" Speed",iPlayerAbility.getMiningSpeedBoost())),false);
                        player.sendStatusMessage(new StringTextComponent(String.format(TextFormatting.AQUA + " %s" + TextFormatting.DARK_RED+" Jump",iPlayerAbility.getJumpBoost())),false);
                        LazyOptional<IBedrockFlux> bedrockFlux = player.getCapability(BedrockFluxProvider.BEDROCK_FLUX_CAPABILITY, null);
                        bedrockFlux.ifPresent(flux -> player.sendStatusMessage(new StringTextComponent(String.format(TextFormatting.AQUA + " %s" + TextFormatting.DARK_RED+" Flux",flux.getBedrockFluxString())),false));
                    }else{

                        InventoryHelper.spawnItemStack(event.player.world,player.posX,player.posY,player.posZ,new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(iPlayerAbility.getRitualCraftingResult()))));
                    }

                    iPlayerAbility.setRitualTimer(1);


                }else{
                    iPlayerAbility.incrementRitualTimer();

                }

            }
        });

        LazyOptional<IBedrockFlux> bedrockFlux = player.getCapability(BedrockFluxProvider.BEDROCK_FLUX_CAPABILITY, null);

        bedrockFlux.ifPresent(h -> {
            if (h.getBedrockFlux()>250.32f) {
                h.count();
                if (h.getTimer() >= h.getMaxTimer()) {
                    h.changeMax();
                    int rand = new Random().nextInt(20) + 1;
                    //int rand = 30;
                    if (rand <= 4) {
                        if (h.getBedrockFlux()<400){
                            int choice =  new Random().nextInt(5);
                            switch (choice){
                                case 0:
                                    player.setFire(40);
                                    break;
                                case 1:
                                    player.addPotionEffect(new EffectInstance(Effects.LEVITATION,40,2));
                                    break;
                                case 2:
                                    if (player.getRidingEntity() != null){
                                        player.stopRiding();
                                    }
                                    player.dropItem(player.getHeldItemMainhand(),false);
                                    player.getHeldItemMainhand().shrink(player.getHeldItemMainhand().getCount());
                                    player.jump();
                                    break;
                                case 3:
                                    player.addPotionEffect(new EffectInstance(Effects.BLINDNESS,100,2));
                                    break;
                                case 4:
                                    player.addPotionEffect(new EffectInstance(Effects.SLOWNESS,100,2));
                                    break;
                            }

                        }else if (h.getBedrockFlux()>=400 && h.getBedrockFlux()<950){
                            int choice =  new Random().nextInt(5);

                            switch (choice){
                                case 0:
                                    event.player.world.setBlockState(player.getPosition(),ModBlocks.fluxedSpores.getDefaultState());
                                    break;
                                case 1:
                                    event.player.world.setBlockState(player.getPosition(),ModBlocks.fluxedSpores.getDefaultState());
                                    break;
                                case 2:
                                    event.player.world.setBlockState(player.getPosition(),Blocks.FIRE.getDefaultState());
                                    break;
                                case 3:
                                    player.addPotionEffect(new EffectInstance(Effects.POISON,160,3));
                                    break;
                                case 4:
                                    player.addPotionEffect(new EffectInstance(Effects.INSTANT_DAMAGE,2,1));
                                    player.addPotionEffect(new EffectInstance(Effects.HUNGER,60,2));
                                    break;
                            }

                        }else if (h.getBedrockFlux()>=950 && h.getBedrockFlux()<1350){
                            int choice =  new Random().nextInt(7);
                            switch (choice){
                                case 0:
                                    event.player.world.setBlockState(player.getPosition(),ModBlocks.fluxedSpores.getDefaultState());
                                    break;
                                case 1:
                                    event.player.world.setBlockState(player.getPosition(),ModBlocks.fluxedSpores.getDefaultState());
                                    break;
                                case 2:
                                    event.player.world.setBlockState(player.getPosition(),ModBlocks.dfDirt.getDefaultState());
                                    break;
                                case 3:
                                    event.player.world.setBlockState(player.getPosition(),ModBlocks.dfGrass.getDefaultState());
                                    break;
                                case 4:
                                    event.player.world.setBlockState(player.getPosition(),ModBlocks.dfCobble.getDefaultState());
                                    break;
                                case 5:
                                    player.addPotionEffect(new EffectInstance(Effects.BAD_OMEN,999,2));
                                    break;
                                case 6:
                                    player.addPotionEffect(new EffectInstance(Effects.UNLUCK,120,4));
                                    break;
                                case 7:
                                    player.addPotionEffect(new EffectInstance(Effects.INSTANT_DAMAGE,2,1));
                                    player.addPotionEffect(new EffectInstance(Effects.HUNGER,85,3));
                                    break;
                            }
                        }else if (h.getBedrockFlux()>=1350 && h.getBedrockFlux()<1800){
                            int choice =  new Random().nextInt(9);

                            switch (choice){
                                case 0:
                                    event.player.world.setBlockState(player.getPosition(),ModBlocks.fluxedSpores.getDefaultState());
                                    break;
                                case 1:
                                    event.player.world.setBlockState(player.getPosition(),ModBlocks.fluxedSpores.getDefaultState());
                                    break;
                                case 2:
                                    event.player.world.setBlockState(player.getPosition(),ModBlocks.dfDirt.getDefaultState());
                                    break;
                                case 3:
                                    event.player.world.setBlockState(player.getPosition(),ModBlocks.dfGrass.getDefaultState());
                                    break;
                                case 4:
                                    event.player.world.setBlockState(player.getPosition(),ModBlocks.dfCobble.getDefaultState());
                                    break;
                                case 5:
                                    player.addPotionEffect(new EffectInstance(Effects.BAD_OMEN,999,3));
                                    break;
                                case 6:
                                    player.addPotionEffect(new EffectInstance(Effects.POISON,80,4));
                                    break;
                                case 7:
                                    player.addPotionEffect(new EffectInstance(Effects.LEVITATION,80,4));
                                    break;
                                case 8:
                                    player.addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE,80,4));
                                    break;
                            }
                        }else if (h.getBedrockFlux()>=1800 && h.getBedrockFlux()<h.getMaxBedrockFlux()){
                            int choice =  new Random().nextInt(10);

                            switch (choice){
                                case 0:
                                    event.player.world.setBlockState(player.getPosition(),ModBlocks.fluxedSpores.getDefaultState());
                                    break;
                                case 1:
                                    event.player.world.setBlockState(player.getPosition(),ModBlocks.fluxedSpores.getDefaultState());
                                    break;
                                case 2:
                                    event.player.world.setBlockState(player.getPosition(),ModBlocks.dfDirt.getDefaultState());
                                    break;
                                case 3:
                                    event.player.world.setBlockState(player.getPosition(),ModBlocks.dfGrass.getDefaultState());
                                    break;
                                case 4:
                                    event.player.world.setBlockState(player.getPosition(),ModBlocks.dfCobble.getDefaultState());
                                    break;
                                case 5:
                                    player.addPotionEffect(new EffectInstance(Effects.BAD_OMEN,999,5));
                                    break;
                                case 6:
                                    player.addPotionEffect(new EffectInstance(Effects.WITHER,120,3));
                                    break;
                                case 7:
                                    player.addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE,120,3));
                                    break;
                                case 8:
                                    player.addPotionEffect(new EffectInstance(Effects.HUNGER,120,4));
                                    break;
                                case 9:
                                    player.addPotionEffect(new EffectInstance(Effects.NAUSEA,120,4));
                                    break;
                            }
                        }
                        String message = ("You dont feel well");
                        player.sendStatusMessage( new StringTextComponent(TextFormatting.RED + message), true);
                    }
                }
            }
        });

    }


    /**
     * Cancel the FOV decrease caused by the decreasing speed due to player penalties.
     * Original FOV value given by the event is never used, we start from scratch 1.0F value.
     * Edited from AbstractClientPlayer.getFovModifier()
     * @param event
     */
    @SubscribeEvent
    public void onFOVUpdate(FOVUpdateEvent event) {
        PlayerEntity player = event.getEntity();
        float modifier = 0;

        float f = 1.0F;


        IAttributeInstance iattributeinstance = player.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        double oldAttributeValue = iattributeinstance.getValue() / modifier;
        f = (float)((double)f * ((oldAttributeValue / (double)player.getAIMoveSpeed() + 1.0D) / 2.0D));

        if (player.getAIMoveSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f))
        {
            f = 1.0F;
        }

        if (player.isHandActive() && player.getActiveItemStack() != null && player.getActiveItemStack().getItem() == Items.BOW)
        {
            int i = player.getItemInUseMaxCount();
            float f1 = (float)i / 20.0F;

            if (f1 > 1.0F)
            {
                f1 = 1.0F;
            }
            else
            {
                f1 = f1 * f1;
            }

            f *= 1.0F - f1 * 0.15F;
        }

        event.setNewfov(f);
    }

    @SubscribeEvent
    public static void onPlayerCraft(PlayerEvent.ItemCraftedEvent event){
        if (event.getCrafting().getItem() instanceof FluxOracle && !event.getPlayer().world.isRemote){
            PlayerEntity player = event.getPlayer();
            LazyOptional<IBedrockFlux> bedrockFlux = player.getCapability(BedrockFluxProvider.BEDROCK_FLUX_CAPABILITY, null);
            FluxOracleScreen fx = new FluxOracleScreen();
            bedrockFlux.ifPresent(h -> {
                h.setCrafterFlux();
                fx.flux = h;
                h.setScreen(fx);
                BedrockResources.proxy.getMinecraft().ingameGUI=fx;
                String message = ("You out of nowhere understand flux and can sense the amount of flux on you");
                player.sendStatusMessage(new StringTextComponent(message),true);
            });
        }
    }

    @SubscribeEvent
    public static void PlayerBreakSpeedEvent(PlayerEvent.BreakSpeed event) {
        PlayerEntity player = event.getPlayer();
        LazyOptional<IPlayerAbility> abilities = player.getCapability(PlayerAbilityProvider.PLAYER_ABILITY_CAPABILITY, null);
        abilities.ifPresent(h -> {
            if (!(event.getPlayer().getHeldItemMainhand().getItem() instanceof SwordItem) &&
                    !(event.getPlayer().getHeldItemMainhand().getItem() instanceof AxeItem) &&
                    !(event.getPlayer().getHeldItemMainhand().getItem() instanceof ShovelItem) &&
                    !(event.getPlayer().getHeldItemMainhand().getItem() instanceof PickaxeItem) &&
                    !(event.getPlayer().getHeldItemMainhand().getItem() instanceof HoeItem) &&
                    event.getState().getHarvestTool() != null) {
                float speeed = 0;

                if (event.getState().getHarvestTool().toString().equals(net.minecraftforge.common.ToolType.PICKAXE.toString())) {
                    speeed = getSpeed(h.getPick());
                }else if (event.getState().getHarvestTool().toString().equals(ToolType.SHOVEL.toString())) {
                    speeed = getSpeed(h.getShovel());

                }else if (event.getState().getHarvestTool().toString().equals(ToolType.AXE.toString())) {
                    speeed = getSpeed(h.getAxe());
                }
                speeed *=1.35;
                if (event.getState().getMaterial().isToolNotRequired()){
                    event.setNewSpeed(((event.getOriginalSpeed()+h.getMiningSpeedBoost()+speeed)));

                }else {
                    event.setNewSpeed((float) ((event.getOriginalSpeed() + h.getMiningSpeedBoost()  + speeed) * (100.0 / 30.0)));
                }


            }else{
                event.setNewSpeed(event.getOriginalSpeed() + h.getMiningSpeedBoost());

            }

        });
    }

    private static float getSpeed(String material) {
        switch (material) {
            case "wood":
                return ItemTier.WOOD.getEfficiency();
            case "stone":
                return  ItemTier.STONE.getEfficiency();
            case "iron":
                return ItemTier.IRON.getEfficiency();
            case "golden":
                return ItemTier.GOLD.getEfficiency();
            case "diamond":
                return  ItemTier.DIAMOND.getEfficiency();
        }
        return 0;
    }

    private static int getharvestLevel(String material){
        switch (material) {
            case "wood":
                return ItemTier.WOOD.getHarvestLevel();
            case "stone":
                return  ItemTier.STONE.getHarvestLevel();
            case "iron":
                return ItemTier.IRON.getHarvestLevel();
            case "golden":
                return ItemTier.GOLD.getHarvestLevel();
            case "diamond":
                return  ItemTier.DIAMOND.getHarvestLevel();
        }
        return -1;
    }

    @SubscribeEvent
    public static void PlayerBreakBlockEvent(PlayerInteractEvent.HarvestCheck event) {
        if (!(event.getPlayer().getHeldItemMainhand().getItem() instanceof SwordItem) &&
                !(event.getPlayer().getHeldItemMainhand().getItem() instanceof AxeItem) &&
                !(event.getPlayer().getHeldItemMainhand().getItem() instanceof ShovelItem) &&
                !(event.getPlayer().getHeldItemMainhand().getItem() instanceof PickaxeItem) &&
                !(event.getPlayer().getHeldItemMainhand().getItem() instanceof HoeItem) &&
                event.getTargetBlock().getHarvestTool() != null) {
            LazyOptional<IPlayerAbility> abilities = event.getPlayer().getCapability(PlayerAbilityProvider.PLAYER_ABILITY_CAPABILITY, null);
            abilities.ifPresent(h -> {
                boolean flag = false;
                Block block = event.getTargetBlock().getBlock();
                if (event.getTargetBlock().getHarvestTool().toString().equals(net.minecraftforge.common.ToolType.PICKAXE.toString())) {
                    int i = getharvestLevel(h.getPick());
                    if (i >= event.getTargetBlock().getHarvestLevel()) {
                        flag = true;
                    } else if (!h.getPick().equals("no")){

                        Material material = event.getTargetBlock().getMaterial();
                        flag = material == Material.ROCK || material == Material.IRON || material == Material.ANVIL;
                    }
                }else if (event.getTargetBlock().getHarvestTool().toString().equals(ToolType.SHOVEL.toString())) {
                    int i = getharvestLevel(h.getShovel());
                    if (i >= event.getTargetBlock().getHarvestLevel()) {
                        flag = true;
                    }
                    if (!flag&&!h.getShovel().equals("no")){
                        flag= block == Blocks.SNOW || block == Blocks.SNOW_BLOCK;
                    }
                }else if (event.getTargetBlock().getHarvestTool().toString().equals(ToolType.AXE.toString())) {
                    int i = getharvestLevel(h.getAxe());
                    if (i >= event.getTargetBlock().getHarvestLevel()) {
                        flag = true;
                    } else if (!h.getAxe().equals("no")){
                        Material material = event.getTargetBlock().getMaterial();
                        flag = material != Material.WOOD && material != Material.PLANTS && material != Material.TALL_PLANTS && material != Material.BAMBOO ;
                    }
                }
                if (event.getTargetBlock().getMaterial().isToolNotRequired() || flag) {
                    event.setCanHarvest(true);
                }else{
                    event.setCanHarvest(false);
                }
            });
        }
    }

    private static final Map<Block, BlockState> HOE_LOOKUP = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.FARMLAND.getDefaultState(),
            Blocks.GRASS_PATH, Blocks.FARMLAND.getDefaultState(), Blocks.DIRT, Blocks.FARMLAND.getDefaultState(), Blocks.COARSE_DIRT, Blocks.DIRT.getDefaultState()));

    protected static final Map<Block, BlockState> SHOVEL_LOOKUP = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.GRASS_PATH.getDefaultState()));

    @SubscribeEvent
    public static void PlayerRightClickEvent( PlayerInteractEvent.RightClickBlock event) {

        if (!(event.getPlayer().getHeldItemMainhand().getItem() instanceof SwordItem) &&
                !(event.getPlayer().getHeldItemMainhand().getItem() instanceof AxeItem) &&
                !(event.getPlayer().getHeldItemMainhand().getItem() instanceof ShovelItem) &&
                !(event.getPlayer().getHeldItemMainhand().getItem() instanceof PickaxeItem) &&
                !(event.getPlayer().getHeldItemMainhand().getItem() instanceof HoeItem)) {
            LazyOptional<IPlayerAbility> abilities = event.getPlayer().getCapability(PlayerAbilityProvider.PLAYER_ABILITY_CAPABILITY, null);
            abilities.ifPresent(h -> {
                if(!h.getHoe().equals("no") && !event.getPlayer().isSneaking()){
                    World world = event.getWorld();
                    BlockPos blockpos = event.getPos();
                    int hook = net.minecraftforge.event.ForgeEventFactory.onHoeUse(new ItemUseContext(event.getPlayer(),event.getHand(),selectBlock(event.getPlayer())));
                    if (hook != 0){
                        if (hook<0){
                            return;
                        }
                    }
                    if (event.getFace() != Direction.DOWN && world.isAirBlock(blockpos.up())) {
                        BlockState blockstate = HOE_LOOKUP.get(world.getBlockState(blockpos).getBlock());
                        if (blockstate != null) {
                            PlayerEntity playerentity = event.getPlayer();
                            world.playSound(playerentity, blockpos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                            if (!world.isRemote) {
                                world.setBlockState(blockpos, blockstate, 11);

                            }
                        }
                    }
                }
                if(h.getShovel().equals("diamond")){
                    World world = event.getWorld();
                    BlockPos blockpos = event.getPos();
                    if (event.getFace() != Direction.DOWN && world.getBlockState(blockpos.up()).isAir(world, blockpos.up()) && event.getPlayer().isSneaking()) {
                        BlockState blockstate = SHOVEL_LOOKUP.get(world.getBlockState(blockpos).getBlock());
                        if (blockstate != null) {
                            PlayerEntity playerentity = event.getPlayer();
                            world.playSound(playerentity, blockpos, SoundEvents.ITEM_SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                            if (!world.isRemote) {
                                world.setBlockState(blockpos, blockstate, 11);
                            }
                        }
                    }
                }
            });
        }
    }


    private static BlockRayTraceResult selectBlock(PlayerEntity player) {
        // Used to find which block the player is looking at, and store it in NBT on the tool.
        World world = player.world;
        BlockRayTraceResult lookingAt = VectorHelper.getLookingAt(player, RayTraceContext.FluidMode.NONE);
        if (lookingAt == null || (world.getBlockState(VectorHelper.getLookingAt(player).getPos()) == Blocks.AIR.getDefaultState())) return null;

        BlockState state = world.getBlockState(lookingAt.getPos());

        return lookingAt;
    }


    /**
     * Copy data from dead player to the new player
     */
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        PlayerEntity player = event.getEntityPlayer();

        LazyOptional<IBedrockFlux> bedrockFlux = player.getCapability(BedrockFluxProvider.BEDROCK_FLUX_CAPABILITY, null);
        LazyOptional<IBedrockFlux> oldbedrockFlux =  event.getOriginal().getCapability(BedrockFluxProvider.BEDROCK_FLUX_CAPABILITY, null);
        bedrockFlux.ifPresent(h -> oldbedrockFlux.ifPresent(o -> h.set(o.getBedrockFlux())));

        LazyOptional<IPlayerAbility> playerAbility = player.getCapability(PlayerAbilityProvider.PLAYER_ABILITY_CAPABILITY, null);
        LazyOptional<IPlayerAbility> oldplayerAbility =  event.getOriginal().getCapability(PlayerAbilityProvider.PLAYER_ABILITY_CAPABILITY, null);
        playerAbility.ifPresent(h -> oldplayerAbility.ifPresent(o -> {
            h.setJumpBoost(o.getJumpBoost());
            h.setGRavityMultiplier(o.getGravityMultiplier());
            h.setMiningSpeedBoost(o.getMiningSpeedBoost());
            h.setHoe(o.getHoe());
            h.setSword(o.getSword());
            h.setShovel(o.getShovel());
            h.setAxe(o.getAxe());
            h.setPick(o.getPick());
            h.setRitualTimer(0);
            h.setRitualTotalTimer(0);
            h.setRitualPedestals(new ArrayList<>());
            h.setFOV(o.getFOV());
            h.setLookPos(o.getlookPos());
            h.setname(o.getNAme());
        }));

    }
}
