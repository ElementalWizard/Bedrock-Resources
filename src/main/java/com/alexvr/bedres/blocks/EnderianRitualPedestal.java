package com.alexvr.bedres.blocks;

import com.alexvr.bedres.registry.ModItems;
import com.alexvr.bedres.tiles.BedrockiumPedestalTile;
import com.alexvr.bedres.tiles.EnderianRitualPedestalTile;
import com.alexvr.bedres.utils.References;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.List;

public class EnderianRitualPedestal extends Block {
    private static final VoxelShape SHAPE = Block.makeCuboidShape(5.0D, 0.0D, 5.0D, 11.0D, 12.0D, 11.0D);


    public EnderianRitualPedestal() {
        super(Properties.create(Material.IRON)
                .sound(SoundType.METAL)
                .lightValue(13).variableOpacity().hardnessAndResistance(15.0F, 36000.0F));
        setRegistryName(References.ENDERIAN_RITUAL_PEDESTAL_REGNAME);

    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }


    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {

        CompoundNBT compoundNBT = stack.getTag();
        if(compoundNBT != null) {
            String s = compoundNBT.getString("name");
            tooltip.add(new StringTextComponent(TextFormatting.DARK_GRAY + ("Item: " +s)));
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }


    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new EnderianRitualPedestalTile();
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(!worldIn.isRemote){
            TileEntity te = worldIn.getTileEntity(pos);
            if(player.getHeldItemMainhand() != ItemStack.EMPTY){
                te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
                    if (player.getHeldItemMainhand().getItem().getRegistryName().equals(ModItems.scrapesKnife.getRegistryName())){

                        if (h.getStackInSlot(0) != ItemStack.EMPTY) {
                            InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY() + 1, pos.getZ(), h.extractItem(0, 1, false));
                            player.getHeldItemMainhand().damageItem(2, player, (p_220044_0_) -> p_220044_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND));
                            ((EnderianRitualPedestalTile) te).item = "none";

                            te.markDirty();
                            ((EnderianRitualPedestalTile) te).sendUpdates();
                        }

                    }else {
                        if (h.getStackInSlot(0) == ItemStack.EMPTY) {
                            h.insertItem(0, new ItemStack(player.getHeldItemMainhand().getItem(), 1), false);
                            ((EnderianRitualPedestalTile) te).item = player.getHeldItemMainhand().getItem().getDisplayName(player.getHeldItemMainhand()).getString();
                            player.getHeldItemMainhand().shrink(1);
                            te.markDirty();
                            ((EnderianRitualPedestalTile) te).sendUpdates();
                        }
                    }
                });
                return true;
            }else{
                for(int i =0 ; i< player.inventory.getSizeInventory(); i ++) {
                    ItemStack stack = player.inventory.getStackInSlot(i);
                    if (stack.getItem().getRegistryName().equals(Items.ENDER_PEARL.getRegistryName()) && te instanceof BedrockiumPedestalTile) {
                        te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
                            if (h.getStackInSlot(0) == ItemStack.EMPTY) {
                                h.insertItem(0, new ItemStack(Items.ENDER_PEARL, 1), false);
                                stack.shrink(1);
                                ((EnderianRitualPedestalTile) te).item = stack.getItem().getDisplayName(stack).getString();
                                te.markDirty();
                                ((EnderianRitualPedestalTile) te).sendUpdates();
                            }
                        });
                    }
                }

                return true;
            }
        }

        return false;
    }


}
