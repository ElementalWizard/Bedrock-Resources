package com.alexvr.bedres.tiles;

import com.alexvr.bedres.containers.ScrapeTankContainer;
import com.alexvr.bedres.items.BedrockScrape;
import com.alexvr.bedres.registry.ModBlocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ScrapeTankTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

    private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);
    int spawned =0;
    private final long INVALID_TIME = 0;
    private long lastTime = INVALID_TIME;  // used for animation
    private double lastAngularPosition; // used for animation


    public ScrapeTankTile() {
        super(ModBlocks.scrapeTankType);

    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return Math.pow(3, 2);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos());
    }


    @Override
    public void tick() {

    }

    @Override
    public void read(CompoundNBT tag) {
        CompoundNBT invTag = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(invTag));

        super.read(tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        handler.ifPresent(h ->{
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>)h).serializeNBT();
            tag.put("inv",compound);

        });

        return super.write(tag);
    }

    private IItemHandler createHandler() {
        return new ItemStackHandler(1) {

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return stack.getItem() instanceof BedrockScrape;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if(!(stack.getItem() instanceof BedrockScrape)){
                    return stack;
                }
                return super.insertItem(slot, stack, simulate);
            }
        };

    }

    public double getNextAngularPosition(double revsPerSecond)
    {
        // we calculate the next position as the angular speed multiplied by the elapsed time since the last position.
        // Elapsed time is calculated using the system clock, which means the animations continue to
        //  run while the game is paused.
        // Alternatively, the elapsed time can be calculated as
        //  time_in_seconds = (number_of_ticks_elapsed + partialTick) / 20.0;
        //  where your tileEntity's update() method increments number_of_ticks_elapsed, and partialTick is passed by vanilla
        //   to your TESR renderTileEntityAt() method.
        long timeNow = System.nanoTime();
        if (lastTime == INVALID_TIME) {   // automatically initialise to 0 if not set yet
            lastTime = timeNow;
            lastAngularPosition = 0.0;
        }
        final double DEGREES_PER_REV = 360.0;
        final double NANOSECONDS_PER_SECOND = 1e9;
        double nextAngularPosition = lastAngularPosition + (timeNow - lastTime) * revsPerSecond * DEGREES_PER_REV / NANOSECONDS_PER_SECOND;
        nextAngularPosition = nextAngularPosition % DEGREES_PER_REV;
        lastAngularPosition = nextAngularPosition;
        lastTime = timeNow;
        return nextAngularPosition;
    }

    public int getTotalItems(){
        int total =0 ;
        for (int i = 0; i < createHandler().getSlots(); i++) {
            total += createHandler().getStackInSlot(i).getCount();
        }
        return total;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return handler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent(getType().getRegistryName().getPath());
    }

    @Nullable
    @Override
    public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {

        return new ScrapeTankContainer(p_createMenu_1_,world,pos,p_createMenu_2_,p_createMenu_3_);
    }
}
