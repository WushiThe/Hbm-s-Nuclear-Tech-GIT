package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.render.item.ItemRenderBase;
import net.minecraft.item.Item;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

import com.hbm.main.ResourceManager;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class RenderQComputer extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5D, y, z + 0.5D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glRotatef(180, 0F, 1F, 0F);

        GL11.glShadeModel(GL11.GL_SMOOTH);
        bindTexture(ResourceManager.machine_quantum_computer_tex);
        ResourceManager.machine_quantum_computer.renderAll();
        GL11.glShadeModel(GL11.GL_FLAT);

        GL11.glPopMatrix();
    }

    public Item getItemForRenderer () {
        return Item.getItemFromBlock(ModBlocks.machine_qcomputer);
    }

    public IItemRenderer getRenderer() {
        return new ItemRenderBase( ) {
            public void renderInventory() {
                GL11.glTranslated(0, -1.5, 0);
                GL11.glScaled(3, 3, 3);
            }
            public void renderCommon() {
                GL11.glTranslated(1.5, 0, 0);
                GL11.glRotated(90, 0, 1, 0);
                GL11.glScaled(0.5, 0.5, 0.5);
                GL11.glDisable(GL11.GL_CULL_FACE);
                GL11.glShadeModel(GL11.GL_SMOOTH);
                bindTexture(ResourceManager.exposure_chamber_tex);
                ResourceManager.exposure_chamber.renderAll();
                GL11.glShadeModel(GL11.GL_FLAT);
                GL11.glEnable(GL11.GL_CULL_FACE);
            }};
        }
    }
