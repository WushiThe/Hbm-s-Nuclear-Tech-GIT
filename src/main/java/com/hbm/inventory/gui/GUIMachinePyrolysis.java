package com.hbm.inventory.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.hbm.inventory.container.ContainerMachinePyrolysis;
import com.hbm.items.ModItems;
import com.hbm.lib.RefStrings;
import com.hbm.tileentity.machine.TileEntityMachinePyrolysis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GUIMachinePyrolysis extends GuiInfoContainer {

    private static ResourceLocation texture = new ResourceLocation(RefStrings.MODID + ":textures/gui/processing/gui_hydrotreater.png");
    private TileEntityMachinePyrolysis pyrolysis;

    public GUIMachinePyrolysis(InventoryPlayer invPlayer, TileEntityMachinePyrolysis tedf) {
        super(new ContainerMachinePyrolysis(invPlayer, tedf));
        pyrolysis = tedf;

        this.xSize = 176;
        this.ySize = 238;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {
        super.drawScreen(mouseX, mouseY, f);

        pyrolysis.tanks[0].renderTankInfo(this, mouseX, mouseY, guiLeft + 35, guiTop + 70 - 52, 16, 52);
        pyrolysis.tanks[1].renderTankInfo(this, mouseX, mouseY, guiLeft + 53, guiTop + 70 - 52, 16, 52);
        pyrolysis.tanks[2].renderTankInfo(this, mouseX, mouseY, guiLeft + 125, guiTop + 70 - 52, 16, 52);
        pyrolysis.tanks[3].renderTankInfo(this, mouseX, mouseY, guiLeft + 143, guiTop + 70 - 52, 16, 52);
        this.drawElectricityInfo(this, mouseX, mouseY, guiLeft + 17, guiTop + 70 - 52, 16, 52, pyrolysis.power, pyrolysis.maxPower);

        if(this.mc.thePlayer.inventory.getItemStack() == null && this.isMouseOverSlot(this.inventorySlots.getSlot(10), mouseX, mouseY) && !this.inventorySlots.getSlot(10).getHasStack()) {
            List<Object[]> lines = new ArrayList();
            ItemStack converter = new ItemStack(ModItems.catalytic_converter);
            lines.add(new Object[] {converter});
            lines.add(new Object[] {converter.getDisplayName()});
            this.drawStackText(lines, mouseX, mouseY, this.fontRendererObj);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int i, int j) {
        String name = this.pyrolysis.hasCustomInventoryName() ? this.pyrolysis.getInventoryName() : I18n.format(this.pyrolysis.getInventoryName());

        this.fontRendererObj.drawString(name, this.xSize / 2 - this.fontRendererObj.getStringWidth(name) / 2, 5, 0xffffff);
        this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        int j = (int) (pyrolysis.power * 54 / pyrolysis.maxPower);
        drawTexturedModalRect(guiLeft + 17, guiTop + 70 - j, 176, 52 - j, 16, j);

        pyrolysis.tanks[0].renderTank(guiLeft + 35, guiTop + 70, this.zLevel, 16, 52);
        pyrolysis.tanks[1].renderTank(guiLeft + 53, guiTop + 70, this.zLevel, 16, 52);
        pyrolysis.tanks[2].renderTank(guiLeft + 125, guiTop + 70, this.zLevel, 16, 52);
        pyrolysis.tanks[3].renderTank(guiLeft + 143, guiTop + 70, this.zLevel, 16, 52);
    }
}
