package com.hbm.blocks.machine;

import com.hbm.blocks.BlockDummyable;
import com.hbm.handler.MultiblockHandlerXR;
import com.hbm.main.MainRegistry;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.machine.TileEntityMachinePyrolysis;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;


public class MachinePyrolysis extends BlockDummyable {

    @SideOnly(Side.CLIENT)
    private IIcon iconTop;

    public MachinePyrolysis(Material mat) {
        super(mat);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        if(meta >= 12) return new TileEntityMachinePyrolysis();
        if(meta >= 6) return new TileEntityProxyCombo().inventory().power().fluid();
        return null;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if(world.isRemote) {
            return true;
        } else if(!player.isSneaking()) {
            int[] pos = this.findCore(world, x, y, z);

            if(pos == null)
                return false;

            TileEntityMachinePyrolysis entity = (TileEntityMachinePyrolysis) world.getTileEntity(pos[0], pos[1], pos[2]);
            if(entity != null) {
                FMLNetworkHandler.openGui(player, MainRegistry.instance, 0, world, pos[0], pos[1], pos[2]);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int[] getDimensions() {
        return new int[] { 1, 0, 1, 0, 4, 2 };
    }

    @Override
    public int getOffset() {
        return 0;
    }

    @Override
    public void fillSpace(World world, int x, int y, int z, ForgeDirection dir, int o) {
        super.fillSpace(world, x, y, z, dir, o);

        x += dir.offsetX * o;
        z += dir.offsetZ * o;

        // up,down;forward,backward;left,right
        MultiblockHandlerXR.fillSpace(world, x, y, z, new int[] { 1, 0, 1, 0, 4, 2 }, this, dir);

    }
}
