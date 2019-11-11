package com.alexvr.bedres.blocks;

import com.alexvr.bedres.registry.ModItems;
import com.alexvr.bedres.tiles.BedrockiumPedestalTile;
import com.alexvr.bedres.tiles.EnderianRitualPedestalTile;
import com.alexvr.bedres.tiles.ItemPlatformTile;
import com.alexvr.bedres.utils.References;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.List;

public class ItemPlatform extends DirectionalBlock {
    private static final VoxelShape SHAPE = Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10, 1.0D, 10.0D);

    protected static final VoxelShape ITEM_PLATFORM_EAST_AABB = Block.makeCuboidShape(15.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);
    protected static final VoxelShape ITEM_PLATFORM_WEST_AABB = Block.makeCuboidShape(0.0D, 6.0D, 6.0D, 1.0D, 10.0D, 10.0D);
    protected static final VoxelShape ITEM_PLATFORM_SOUTH_AABB = Block.makeCuboidShape(6.0D, 6.0D, 15.0D, 10.0D, 10.0D, 16.0D);
    protected static final VoxelShape ITEM_PLATFORM_NORTH_AABB = Block.makeCuboidShape(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 1.0D);
    protected static final VoxelShape ITEM_PLATFORM_UP_AABB = Block.makeCuboidShape(6.0D, 15.0D, 6.0D, 10, 16.0D, 10.0D);
    protected static final VoxelShape ITEM_PLATFORM_DOWN_AABB = Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10, 1.0D, 10.0D);

    public ItemPlatform() {
        super(Properties.create(Material.IRON)
                .sound(SoundType.METAL).lightValue(8).variableOpacity().hardnessAndResistance(15.0F, 36000.0F));
        setRegistryName(References.ITEM_PLATFORM_REGNAME);

    }



    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        TileEntity tileentity = builder.get(LootParameters.BLOCK_ENTITY);
        if (tileentity instanceof EnderianRitualPedestalTile) {
            EnderianRitualPedestalTile enderianRitualPedestalTile = (EnderianRitualPedestalTile)tileentity;
            builder = builder.withDynamicDrop(ShulkerBoxBlock.field_220169_b, (p_220168_1_, p_220168_2_) -> {
                enderianRitualPedestalTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
                    if (h.getStackInSlot(0) != ItemStack.EMPTY) {
                        p_220168_2_.accept(h.getStackInSlot(0));
                    }
                });
            });
        }

        return super.getDrops(state, builder);
    }
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }


    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        CompoundNBT compoundnbt = stack.getChildTag("BlockEntityTag");
        if (compoundnbt != null) {
            if (compoundnbt.contains("LootTable", 8)) {
                tooltip.add(new StringTextComponent("???????"));
            }
            if (compoundnbt.contains("Items", 9)) {
                NonNullList<ItemStack> nonnulllist = NonNullList.withSize(1, ItemStack.EMPTY);
                ItemStackHelper.loadAllItems(compoundnbt, nonnulllist);
                if (!nonnulllist.get(0).isEmpty()) {
                    ITextComponent itextcomponent = nonnulllist.get(0).getDisplayName().deepCopy();
                    tooltip.add(itextcomponent);
                }
            }
        }

    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch(state.get(FACING)) {
            case DOWN:
                return ITEM_PLATFORM_DOWN_AABB;
            case UP:
            default:
                return ITEM_PLATFORM_UP_AABB;
            case NORTH:
                return ITEM_PLATFORM_NORTH_AABB;
            case SOUTH:
                return ITEM_PLATFORM_SOUTH_AABB;
            case WEST:
                return ITEM_PLATFORM_WEST_AABB;
            case EAST:
                return ITEM_PLATFORM_EAST_AABB;
        }
    }

    @Override
    public void onBlockClicked(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
        if (!worldIn.isRemote){
            TileEntity te = worldIn.getTileEntity(pos);
            te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
                if (h.getStackInSlot(0) != ItemStack.EMPTY) {
                    InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY() + 1, pos.getZ(), h.extractItem(0, 1, false));
                    player.getHeldItemMainhand().damageItem(2, player, (p_220044_0_) -> p_220044_0_.sendBreakAnimation(EquipmentSlotType.MAINHAND));
                    ((ItemPlatformTile) te).item = "none";
                    te.markDirty();
                    ((ItemPlatformTile) te).sendUpdates();
                    state.updateNeighbors(worldIn,pos,32);
                }
            });
        }
        super.onBlockClicked(state, worldIn, pos, player);
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
        return new ItemPlatformTile();
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
                            ((ItemPlatformTile) te).item = "none";
                            te.markDirty();
                            ((ItemPlatformTile) te).sendUpdates();
                        }
                    }else {
                        if (h.getStackInSlot(0) == ItemStack.EMPTY) {
                            h.insertItem(0, new ItemStack(player.getHeldItemMainhand().getItem(), 1), false);
                            ((ItemPlatformTile) te).item = player.getHeldItemMainhand().getItem().getRegistryName().toString();
                            player.getHeldItemMainhand().shrink(1);
                            te.markDirty();
                            ((ItemPlatformTile) te).sendUpdates();
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
                                ((ItemPlatformTile) te).item = stack.getItem().getRegistryName().toString();
                                te.markDirty();
                                ((ItemPlatformTile) te).sendUpdates();

                            }
                        });
                    }
                }
                return true;
            }
        }

        return false;
    }

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getNearestLookingDirection());
    }


    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    public BlockState rotate(BlockState state, net.minecraft.world.IWorld world, BlockPos pos, Rotation direction) {
        return super.rotate(state, world, pos, direction);
    }

    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.toRotation(state.get(FACING)));
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

}
