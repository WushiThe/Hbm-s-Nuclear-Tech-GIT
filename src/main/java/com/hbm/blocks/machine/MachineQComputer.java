package com.hbm.blocks.machine;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ILookOverlay;
import com.hbm.handler.MultiblockHandlerXR;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.machine.TileEntityDeuteriumTower;
import com.hbm.tileentity.machine.TileEntityQComputer;

import com.hbm.util.BobMathUtil;
import com.hbm.util.I18nUtil;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

public class MachineQComputer extends BlockDummyable implements ILookOverlay {

    public MachineQComputer(Material mat) {
        super(mat);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        if(meta >= 12) return new TileEntityQComputer();
        if(meta >= 6) return new TileEntityProxyCombo().inventory().power();
        return null;
    }

    @Override
    public int[] getDimensions() {
        return new int[] {6, 0, 1, 0, 0, 1};
    }

    @Override
    public int getOffset() {
        return 0;
    }

    @Override
    public void fillSpace(World world, int x, int y, int z, ForgeDirection dir, int o) {
        super.fillSpace(world, x, y, z, dir, o);

        x = x + dir.offsetX * o;
        z = z + dir.offsetZ * o;

        ForgeDirection dr2 = dir.getRotation(ForgeDirection.UP);

        this.makeExtra(world, x - dir.offsetX - dr2.offsetX, y, z - dir.offsetZ - dr2.offsetZ);
        this.makeExtra(world, x, y, z - dir.offsetZ - dr2.offsetZ);
        this.makeExtra(world, x - dir.offsetX - dr2.offsetX, y, z);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        return super.standardOpenBehavior(world, x, y, z, player, 0);
    }
    @Override
    public void printHook(RenderGameOverlayEvent.Pre event, World world, int x, int y, int z) {
        int[] pos = this.findCore(world, x, y, z);

        if(pos == null)
            return;

        TileEntity te = world.getTileEntity(pos[0], pos[1], pos[2]);

        if(!(te instanceof TileEntityDeuteriumTower))
            return;

        TileEntityDeuteriumTower tower = (TileEntityDeuteriumTower) te;

        List<String> text = new ArrayList();
        text.add((tower.power < tower.getMaxPower() / 20 ? EnumChatFormatting.RED : EnumChatFormatting.GREEN) + "Power: " + BobMathUtil.getShortNumber(tower.power) + "HE");

        for(int i = 0; i < tower.tanks.length; i++)
            text.add((i < 1 ? (EnumChatFormatting.GREEN + "-> ") : (EnumChatFormatting.RED + "<- ")) + EnumChatFormatting.RESET + tower.tanks[i].getTankType().getLocalizedName() + ": " + tower.tanks[i].getFill() + "/" + tower.tanks[i].getMaxFill() + "mB");

        ILookOverlay.printGeneric(event, I18nUtil.resolveKey(getUnlocalizedName() + ".name"), 0xffff00, 0x404000, text);
    }
}