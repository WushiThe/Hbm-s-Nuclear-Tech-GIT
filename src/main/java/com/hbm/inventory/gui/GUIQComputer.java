package com.hbm.inventory.gui;

import com.hbm.inventory.container.ContainerMachineQComputer;
import com.hbm.tileentity.machine.TileEntityQComputer;
import org.lwjgl.opengl.GL11;

import com.hbm.inventory.container.ContainerMachineChemplant;
import com.hbm.items.ModItems;
import com.hbm.lib.RefStrings;
import com.hbm.tileentity.machine.TileEntityMachineChemplant;
import com.hbm.util.I18nUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GUIQComputer extends GuiInfoContainer {

    private static ResourceLocation texture = new ResourceLocation(RefStrings.MODID + ":textures/gui/gui_qcomputer.png");
    private TileEntityQComputer qcomputer;

    public GUIQComputer(InventoryPlayer invPlayer, TileEntityQComputer tedf) {
        super(new ContainerMachineQComputer(invPlayer, tedf));
        qcomputer = tedf;

        this.xSize = 176;
        this.ySize = 222;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {
        super.drawScreen(mouseX, mouseY, f);

        qcomputer.tanks[0].renderTankInfo(this, mouseX, mouseY, guiLeft + 8, guiTop + 52 - 34, 16, 34);
        qcomputer.tanks[1].renderTankInfo(this, mouseX, mouseY, guiLeft + 152, guiTop + 52 - 34, 16, 34);
        this.drawElectricityInfo(this, mouseX, mouseY, guiLeft + 106, guiTop + 70 - 20, 4, 20, qcomputer.power, qcomputer.maxPower);

        }

    @Override
    protected void drawGuiContainerForegroundLayer( int i, int j) {
        String name = this.qcomputer.hasCustomInventoryName() ? this.qcomputer.getInventoryName() : I18n.format(this.qcomputer.getInventoryName());

        this.fontRendererObj.drawString(name, this.xSize / 2 - this.fontRendererObj.getStringWidth(name) / 2, 0, 4210752); // Quantum Computer Text
        this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752); // Inventory Text
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        int i = (int) (qcomputer.power * 20 / qcomputer.maxPower);
        drawTexturedModalRect(guiLeft + 106, guiTop + 70 - i, 176, 20 - i, 4, i); // POWER GAUGE

        int j = qcomputer.progress * 90 / qcomputer.maxProgress;
        drawTexturedModalRect(guiLeft + 43, guiTop + 89, 0, 222, j, 18); // Progress Bar

        qcomputer.tanks[0].renderTank(guiLeft + 8, guiTop + 52, this.zLevel, 16, 34);
        qcomputer.tanks[1].renderTank(guiLeft + 26, guiTop + 52, this.zLevel, 16, 34); // Coolant Tanks

    }
}
