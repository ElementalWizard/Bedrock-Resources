package com.alexvr.bedres.renderer;

import com.alexvr.bedres.blocks.tiles.BedrockiumTowerTile;
import com.alexvr.bedres.utils.RendererHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;

public class BedrockiumTowerTER extends TileEntityRenderer<BedrockiumTowerTile> {

    public BedrockiumTowerTER(TileEntityRendererDispatcher p_i226006_1_) {
        super(p_i226006_1_);
    }

    @Override
    public void render(BedrockiumTowerTile te, float v, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int i1) {
        GlStateManager.pushLightingAttributes();
        GlStateManager.pushMatrix();
        // Translate to the location of our tile entity
        GlStateManager.translated(te.getPos().getX(), te.getPos().getY(), te.getPos().getZ());
        GlStateManager.disableRescaleNormal();

        te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {


            if (h.getStackInSlot(0) != ItemStack.EMPTY){
                RendererHelper.renderItem(te,h.getStackInSlot(0),.5,.25,.13,.22,.22,.22,0,0,0,0,matrixStack,i,iRenderTypeBuffer);
            }
            if (h.getStackInSlot(1) != ItemStack.EMPTY){
                RendererHelper.renderItem(te,h.getStackInSlot(1),.5,.75,.245,.22,.22,.22,0,0,0,0,matrixStack,i,iRenderTypeBuffer);
            }
            if (h.getStackInSlot(2) != ItemStack.EMPTY){
                RendererHelper.renderItem(te,h.getStackInSlot(2),.87,.25,.5,.22,.22,.22,0,1,0,90,matrixStack,i,iRenderTypeBuffer);
            }
            if (h.getStackInSlot(3) != ItemStack.EMPTY){
                RendererHelper.renderItem(te,h.getStackInSlot(3),0.755,.75,.5,.22,.22,.22,0,1,0,90,matrixStack,i,iRenderTypeBuffer);
            }
            if (h.getStackInSlot(4) != ItemStack.EMPTY){
                RendererHelper.renderItem(te,h.getStackInSlot(4),.13,.25,.5,.22,.22,.22,0,1,0,90,matrixStack,i,iRenderTypeBuffer);
            }
            if (h.getStackInSlot(5) != ItemStack.EMPTY){
                RendererHelper.renderItem(te,h.getStackInSlot(5),.245,.75,.5,0.22,.22,.22,0,1,0,90,matrixStack,i,iRenderTypeBuffer);
            }
            if (h.getStackInSlot(6) != ItemStack.EMPTY){
                RendererHelper.renderItem(te,h.getStackInSlot(6),.5,.25,.87,.22,.22,.22,0,0,0,0,matrixStack,i,iRenderTypeBuffer);
            }
            if (h.getStackInSlot(7) != ItemStack.EMPTY){
                RendererHelper.renderItem(te,h.getStackInSlot(7),.5,.75,0.755,.22,.22,.22,0,0,0,0,matrixStack,i,iRenderTypeBuffer);
            }


        });



        GlStateManager.enableRescaleNormal();



        GlStateManager.popMatrix();

        GlStateManager.popAttributes();
    }


}
