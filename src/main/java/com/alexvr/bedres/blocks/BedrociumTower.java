package com.alexvr.bedres.blocks;

import com.alexvr.bedres.registry.ModItems;
import com.alexvr.bedres.registry.ModParticles;
import com.alexvr.bedres.tiles.BedrockiumPedestalTile;
import com.alexvr.bedres.tiles.BedrockiumTowerTile;
import com.alexvr.bedres.utils.References;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BedrociumTower extends Block {



    public BedrociumTower() {
        super(Properties.create(Material.IRON)
                .sound(SoundType.METAL)
                .lightValue(13).variableOpacity().hardnessAndResistance(-1.0F, 3600000.0F).noDrops());
        setRegistryName(References.BASE_SPIKE_REGNAME);

    }



    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        super.onBlockClicked(state, worldIn, pos, player);
    }

    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        TileEntity tileentity = builder.get(LootParameters.BLOCK_ENTITY);
        if (tileentity instanceof BedrockiumTowerTile) {
            BedrockiumTowerTile bedrockiumTowerTile = (BedrockiumTowerTile)tileentity;
            builder = builder.withDynamicDrop(ShulkerBoxBlock.field_220169_b, (p_220168_1_, p_220168_2_) -> {
                bedrockiumTowerTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
                    for (int i =0;i<h.getSlots();i++) {
                        if (h.getStackInSlot(i) != ItemStack.EMPTY) {
                            p_220168_2_.accept(h.getStackInSlot(i));
                        }
                    }
                });
            });
        }

        return super.getDrops(state, builder);
    }

    public int getIndex(PlayerEntity player,BlockRayTraceResult hit,BlockPos pos){
        double y = hit.getHitVec().y;
        int index = 0;
        switch (player.getHorizontalFacing()) {
            case SOUTH:
                if (y <= pos.getY() + 0.55) {
                    index = 0;
                } else {
                    index = 1;
                }
                break;
            case WEST:
                if (y <= pos.getY() + 0.55) {
                    index = 2;
                } else {
                    index = 3;
                }
                break;
            case EAST:
                if (y <= pos.getY() + 0.55) {
                    index = 4;
                } else {
                    index = 5;
                }
                break;
            case NORTH:
                if (y <= pos.getY() + 0.55) {
                    index = 6;
                } else {
                    index = 7;
                }
                break;
        }
        return index;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

        if(!worldIn.isRemote){
            TileEntity te = worldIn.getTileEntity(pos);
            if(player.getHeldItemMainhand() != ItemStack.EMPTY){
                te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
                    int index = getIndex(player,hit,pos);
                    if (player.getHeldItemMainhand().getItem().getRegistryName().equals(ModItems.scrapesKnife.getRegistryName())){
                        if (h.getStackInSlot(index) != ItemStack.EMPTY) {
                            InventoryHelper.spawnItemStack(worldIn,  pos.getX(), pos.getY()+1,pos.getZ(),h.extractItem(index,1,false));
                            System.out.println(h.getStackInSlot(index).getDisplayName());
                            player.getHeldItemMainhand().damageItem(2, player, (p_220044_0_) -> p_220044_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND));
                            te.markDirty();
                            ((BedrockiumTowerTile) te).sendUpdates();
                        }
                    }else {
                        if (h.getStackInSlot(index) == ItemStack.EMPTY) {
                            h.insertItem(index, new ItemStack(player.getHeldItemMainhand().getItem(), 1), false);
                            player.getHeldItemMainhand().shrink(1);
                            te.markDirty();
                            ((BedrockiumTowerTile) te).sendUpdates();
                        }
                    }
                });
                return true;
            }else{
                int index = getIndex(player,hit,pos);
                for(int i =0 ; i< player.inventory.getSizeInventory(); i ++) {
                    ItemStack stack = player.inventory.getStackInSlot(i);
                    if (stack.getItem().getRegistryName().equals(Items.ENDER_PEARL.getRegistryName()) && te instanceof BedrockiumTowerTile) {
                        te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
                            if (h.getStackInSlot(index) == ItemStack.EMPTY) {
                                h.insertItem(index, new ItemStack(Items.ENDER_PEARL, 1), false);
                                stack.shrink(1);
                                te.markDirty();
                                ((BedrockiumTowerTile) te).sendUpdates();
                            }
                        });
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        worldIn.getTileEntity(pos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {

            int total = 0;
            for (int k = 0; k < h.getSlots(); k++) {
                total += h.getStackInSlot(k).getCount();
            }

            if (total == 8) {
                ModParticles.BEDROCK_DUST.spawn(worldIn, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 0, 0, 0);

                if (worldIn.getBlockState(pos.offset(Direction.NORTH).offset(Direction.NORTH).offset(Direction.NORTH).offset(Direction.DOWN)).getBlock().hasTileEntity(worldIn.getBlockState(pos.offset(Direction.NORTH).offset(Direction.NORTH).offset(Direction.NORTH).offset(Direction.DOWN))) &&
                        worldIn.getTileEntity(pos.offset(Direction.NORTH).offset(Direction.NORTH).offset(Direction.NORTH).offset(Direction.DOWN)) instanceof BedrockiumPedestalTile) {
                    worldIn.addParticle(ParticleTypes.ENCHANT,true,pos.offset(Direction.NORTH).offset(Direction.NORTH).offset(Direction.NORTH).offset(Direction.DOWN).getX()+.5,pos.offset(Direction.NORTH).offset(Direction.NORTH).offset(Direction.NORTH).offset(Direction.DOWN).getY()+.4,pos.offset(Direction.NORTH).offset(Direction.NORTH).offset(Direction.NORTH).offset(Direction.DOWN).getZ()+1.1,0,1,0);

                }
                else if (worldIn.getBlockState(pos.offset(Direction.SOUTH).offset(Direction.SOUTH).offset(Direction.SOUTH).offset(Direction.DOWN)).getBlock().hasTileEntity(worldIn.getBlockState(pos.offset(Direction.SOUTH).offset(Direction.SOUTH).offset(Direction.SOUTH).offset(Direction.DOWN))) &&
                        worldIn.getTileEntity(pos.offset(Direction.SOUTH).offset(Direction.SOUTH).offset(Direction.SOUTH).offset(Direction.DOWN)) instanceof BedrockiumPedestalTile) {
                    worldIn.addParticle(ParticleTypes.ENCHANT,true,pos.offset(Direction.SOUTH).offset(Direction.SOUTH).offset(Direction.SOUTH).offset(Direction.DOWN).getX()+.5,pos.offset(Direction.SOUTH).offset(Direction.SOUTH).offset(Direction.SOUTH).offset(Direction.DOWN).getY()+.4,pos.offset(Direction.SOUTH).offset(Direction.SOUTH).offset(Direction.SOUTH).offset(Direction.DOWN).getZ()-.1,0,1,0);

                }
                else if (worldIn.getBlockState(pos.offset(Direction.EAST).offset(Direction.EAST).offset(Direction.EAST).offset(Direction.DOWN)).getBlock().hasTileEntity(worldIn.getBlockState(pos.offset(Direction.EAST).offset(Direction.EAST).offset(Direction.EAST).offset(Direction.DOWN))) &&
                        worldIn.getTileEntity(pos.offset(Direction.EAST).offset(Direction.EAST).offset(Direction.EAST).offset(Direction.DOWN)) instanceof BedrockiumPedestalTile) {
                    worldIn.addParticle(ParticleTypes.ENCHANT,true,pos.offset(Direction.EAST).offset(Direction.EAST).offset(Direction.EAST).offset(Direction.DOWN).getX()+-.1,pos.offset(Direction.WEST).offset(Direction.WEST).offset(Direction.WEST).offset(Direction.DOWN).getY()+.4,pos.offset(Direction.WEST).offset(Direction.WEST).offset(Direction.WEST).offset(Direction.DOWN).getZ()+.5,0,1,0);

                }
                else if (worldIn.getBlockState(pos.offset(Direction.WEST).offset(Direction.WEST).offset(Direction.WEST).offset(Direction.DOWN)).getBlock().hasTileEntity(worldIn.getBlockState(pos.offset(Direction.WEST).offset(Direction.WEST).offset(Direction.WEST).offset(Direction.DOWN))) &&
                        worldIn.getTileEntity(pos.offset(Direction.WEST).offset(Direction.WEST).offset(Direction.WEST).offset(Direction.DOWN)) instanceof BedrockiumPedestalTile) {
                    worldIn.addParticle(ParticleTypes.ENCHANT,true,pos.offset(Direction.WEST).offset(Direction.WEST).offset(Direction.WEST).offset(Direction.DOWN).getX()+1.1,pos.offset(Direction.EAST).offset(Direction.EAST).offset(Direction.EAST).offset(Direction.DOWN).getY()+.4,pos.offset(Direction.EAST).offset(Direction.EAST).offset(Direction.EAST).offset(Direction.DOWN).getZ()+.5,0,1,0);

                }
            }
        });


        super.animateTick(stateIn, worldIn, pos, rand);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new BedrockiumTowerTile();
    }


    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {


        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }
}
