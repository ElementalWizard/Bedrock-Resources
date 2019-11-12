package com.alexvr.bedres.renderer;

import com.alexvr.bedres.blocks.tiles.EnderianRitualPedestalTile;
import com.alexvr.bedres.utils.RendererHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;

public class EnderianRitualPedestalTER extends TileEntityRenderer<EnderianRitualPedestalTile> {

    @Override
    public void render(EnderianRitualPedestalTile te, double x, double y, double z, float partialTicks, int destroyStage) {

        GlStateManager.pushLightingAttributes();
        GlStateManager.pushMatrix();
        // Translate to the location of our tile entity
        GlStateManager.translated(x, y, z);
        GlStateManager.disableRescaleNormal();

        te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {

            long angle = (System.currentTimeMillis() / 10) %360;

            if (h.getStackInSlot(0) != ItemStack.EMPTY){
                RendererHelper.renderItem(te,h.getStackInSlot(0),.5,.9,.5,.25,.25,.25,0,1,0,angle);
            }

        });



        GlStateManager.enableRescaleNormal();



        GlStateManager.popMatrix();

        GlStateManager.popAttributes();

    }
}
