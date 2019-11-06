package com.alexvr.bedres.utils;

import com.alexvr.bedres.BedrockResources;
import com.alexvr.bedres.capability.abilities.IPlayerAbility;
import com.alexvr.bedres.capability.abilities.PlayerAbilityProvider;
import com.alexvr.bedres.capability.bedrock_flux.BedrockFluxProvider;
import com.alexvr.bedres.capability.bedrock_flux.IBedrockFlux;
import com.alexvr.bedres.gui.FluxOracleScreen;
import com.alexvr.bedres.gui.FluxOracleScreenGui;
import com.alexvr.bedres.items.FluxOracle;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Random;

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

        bedrockFlux.ifPresent(h -> {

            String message = String.format("Hello there, you have %s flux.",h.getBedrockFluxString());
            player.sendStatusMessage(new StringTextComponent(message),false);
            if (h.getCrafterFlux()){
                h.setScreen((FluxOracleScreen)BedrockResources.proxy.getMinecraft().ingameGUI);
                h.getScreen().flux = h;
                BedrockResources.proxy.getMinecraft().ingameGUI=h.getScreen();
            }

        });

        LazyOptional<IPlayerAbility> abilities = player.getCapability(PlayerAbilityProvider.PLAYER_ABILITY_CAPABILITY, null);
        abilities.ifPresent(h -> {
            String message = String.format("Hello there, your list of skills is: %s sword %s axe %s shovel %s hoe %s pick. And speed of %d and jump of %f",h.getSword(),h.getAxe(),h.getShovel(),h.getHoe(),h.getPick(),h.getMiningSpeedBoost(),h.getJumpBoost());
            player.sendStatusMessage(new StringTextComponent(message),false);

        });


    }


    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        PlayerEntity player = event.player;

        if (player.world.isRemote) return;

        LazyOptional<IBedrockFlux> bedrockFlux = player.getCapability(BedrockFluxProvider.BEDROCK_FLUX_CAPABILITY, null);

        bedrockFlux.ifPresent(h -> {
            if (h.getBedrockFlux()>250.32f) {
                h.count();
                if (h.getTimer() >= h.getMaxTimer()) {
                    h.changeMax();
                    //int rand = new Random().nextInt(20) + 1;
                    int rand = 30;
                    if (rand <= 4) {
                        if (h.getBedrockFlux()<400){
                            int choice =  new Random().nextInt(5);
                            System.out.println(choice);

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

                                    break;
                            }

                        }
                    }
                    String message = ("You dont feel too well");
                    player.sendStatusMessage(new StringTextComponent(message), true);
                }
            }
        });

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
    public static void PlayerBreakSpeedEvent(PlayerEvent.BreakSpeed event)
    {
        PlayerEntity player = event.getPlayer();
        LazyOptional<IPlayerAbility> abilities = player.getCapability(PlayerAbilityProvider.PLAYER_ABILITY_CAPABILITY, null);
        abilities.ifPresent(h -> {
            if (player.getHeldItemMainhand() == ItemStack.EMPTY) {
                event.setNewSpeed(event.getOriginalSpeed() + h.getMiningSpeedBoost());
            }

        });
    }

    private static int getharvestLevel(String material){
        switch (material) {
            case "wood":
               return ItemTier.WOOD.getHarvestLevel();
            case "stone":
                return  ItemTier.IRON.getHarvestLevel();
            case "iron":
                return ItemTier.IRON.getHarvestLevel();
            case "diamond":
                return  ItemTier.DIAMOND.getHarvestLevel();
        }
        return 0;
    }

    @SubscribeEvent
    public static void PlayerBreakBlockEvent(PlayerInteractEvent.HarvestCheck event)
    {
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
                    } else {
                        Material material = event.getTargetBlock().getMaterial();
                        flag = material == Material.ROCK || material == Material.IRON || material == Material.ANVIL;
                    }
                }else if (event.getTargetBlock().getHarvestTool().toString().equals(ToolType.SHOVEL.toString())) {

                    int i = getharvestLevel(h.getShovel());

                    if (i >= event.getTargetBlock().getHarvestLevel()) {
                        flag = true;
                    }
                    if (!flag){
                        flag= block == Blocks.SNOW || block == Blocks.SNOW_BLOCK;
                    }
                }else if (event.getTargetBlock().getHarvestTool().toString().equals(ToolType.AXE.toString())) {

                    int i = getharvestLevel(h.getAxe());

                    if (i >= event.getTargetBlock().getHarvestLevel()) {
                        flag = true;
                    } else {
                        Material material = event.getTargetBlock().getMaterial();
                        flag = material != Material.WOOD && material != Material.PLANTS && material != Material.TALL_PLANTS && material != Material.BAMBOO ;
                    }
                }
                System.out.println(flag);
                System.out.println(event.getTargetBlock().getMaterial().isToolNotRequired());
                System.out.println();
                if (event.getTargetBlock().getMaterial().isToolNotRequired() || flag) {
                    event.setCanHarvest(true);
                }
            });
        }

    }

    protected static final Map<Block, BlockState> HOE_LOOKUP = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.FARMLAND.getDefaultState(), Blocks.GRASS_PATH, Blocks.FARMLAND.getDefaultState(), Blocks.DIRT, Blocks.FARMLAND.getDefaultState(), Blocks.COARSE_DIRT, Blocks.DIRT.getDefaultState()));

    @SubscribeEvent
    public static void PlayerRightClickEvent( PlayerInteractEvent.RightClickBlock event)
    {
        if (!(event.getPlayer().getHeldItemMainhand().getItem() instanceof SwordItem) &&
                !(event.getPlayer().getHeldItemMainhand().getItem() instanceof AxeItem) &&
                !(event.getPlayer().getHeldItemMainhand().getItem() instanceof ShovelItem) &&
                !(event.getPlayer().getHeldItemMainhand().getItem() instanceof PickaxeItem) &&
                !(event.getPlayer().getHeldItemMainhand().getItem() instanceof HoeItem)) {
            LazyOptional<IPlayerAbility> abilities = event.getPlayer().getCapability(PlayerAbilityProvider.PLAYER_ABILITY_CAPABILITY, null);
            abilities.ifPresent(h -> {
                if(!h.getHoe().equals("no")){
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


    @SubscribeEvent
    public static void PlayerWakeUpEvent(PlayerSleepInBedEvent event)
    {
        PlayerEntity player = event.getEntityPlayer();

        if (player.world.isRemote) return;

        LazyOptional<IBedrockFlux> bedrockFlux = player.getCapability(BedrockFluxProvider.BEDROCK_FLUX_CAPABILITY, null);

        bedrockFlux.ifPresent(h -> {

            String message = ("You hear whispers as you wake up from bed.");
            h.fill(100);
            if(BedrockResources.proxy.getMinecraft().ingameGUI instanceof FluxOracleScreen){
                h.setScreen((FluxOracleScreen)BedrockResources.proxy.getMinecraft().ingameGUI);
                h.getScreen().flux = h;
            }
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
