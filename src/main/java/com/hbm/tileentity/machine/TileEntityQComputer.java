package com.hbm.tileentity.machine;

import java.util.List;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.lib.Library;
import com.hbm.tileentity.IGUIProvider;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.util.fauxpointtwelve.DirPos;

import api.hbm.energy.IEnergyUser;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import api.hbm.fluid.IFluidStandardTransceiver;


public class TileEntityQComputer extends TileEntityMachineBase implements IGUIProvider, IEnergyUser, IFluidStandardTransceiver {

    public FluidTank[] tanks;
    public long power;
    public static final long maxPower = 1_000_000_000;

    public int progress;
    public static final int processTimeBase = 200;
    public int processTime = processTimeBase;
    public static final int consumptionBase = 10_000;
    public int consumption = consumptionBase;
    public boolean isOn = false;
    public float rotation;
    public float prevRotation;

    public TileEntityQComputer() {
        /*
         * 0: Particle
         * 1: Particle internal
         * 2: Particle container
         * 3: Ingredient
         * 4: Output
         * 5: Battery
         * 6-7: Upgrades
         */
        super(8);
        tanks[0] = new FluidTank(Fluids.HELIUM4, 50000, 0);
        tanks[1] = new FluidTank(Fluids.HELIUM4, 50000, 0);
    }

    @Override
    public String getName() {
        return "container.qcomputer";
    }

    @Override
    public void updateEntity() {

        if (!worldObj.isRemote) {

            this.updateConnections();

            if(hasPower() && hasEnoughWater() && tanks[1].getMaxFill() > tanks[1].getFill()) {
                int convert = Math.min(tanks[1].getMaxFill(), tanks[0].getFill()) / 50;
                convert = Math.min(convert, tanks[1].getMaxFill() - tanks[1].getFill());

                tanks[0].setFill(tanks[0].getFill() - convert * 50); //dividing first, then multiplying, will remove any rounding issues
                tanks[1].setFill(tanks[1].getFill() + convert);
                power -= this.getMaxPower() / 20;
            }

            this.subscribeToAllAround(tanks[0].getTankType(), this);
            this.sendFluidToAll(tanks[1], this);

            NBTTagCompound data = new NBTTagCompound();
            data.setLong("power", power);
            tanks[0].writeToNBT(data, "water");
            tanks[1].writeToNBT(data, "heavyWater");

            this.networkPack(data, 50);

            this.isOn = false;
            this.power = Library.chargeTEFromItems(slots, 5, power, maxPower);

            if (worldObj.getTotalWorldTime() % 20 == 0) {
                for (DirPos pos : getConPos())
                    this.trySubscribe(worldObj, pos.getX(), pos.getY(), pos.getZ(), pos.getDir());
            }
        }
    }

    public DirPos[] getConPos() {
        ForgeDirection dir = ForgeDirection.getOrientation(this.getBlockMetadata() - 10);
        ForgeDirection rot = dir.getRotation(ForgeDirection.UP).getOpposite();
        return new DirPos[]{
                new DirPos(xCoord + rot.offsetX * 7 + dir.offsetX * 2, yCoord, zCoord + rot.offsetZ * 7 + dir.offsetZ * 2, dir),
                new DirPos(xCoord + rot.offsetX * 7 - dir.offsetX * 2, yCoord, zCoord + rot.offsetZ * 7 - dir.offsetZ * 2, dir.getOpposite()),
                new DirPos(xCoord + rot.offsetX * 8 + dir.offsetX * 2, yCoord, zCoord + rot.offsetZ * 8 + dir.offsetZ * 2, dir),
                new DirPos(xCoord + rot.offsetX * 8 - dir.offsetX * 2, yCoord, zCoord + rot.offsetZ * 8 - dir.offsetZ * 2, dir.getOpposite()),
                new DirPos(xCoord + rot.offsetX * 9, yCoord, zCoord + rot.offsetZ * 9, rot)
        };
    }

    AxisAlignedBB bb = null;

    @Override
    public AxisAlignedBB getRenderBoundingBox() {

        if (bb == null) {
            bb = AxisAlignedBB.getBoundingBox(
                    xCoord - 1,
                    yCoord,
                    zCoord - 1,
                    xCoord + 1,
                    yCoord + 6,
                    zCoord + 1
            );
        }

        return bb;
    }
    protected void updateConnections() {

        for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS)
            this.trySubscribe(worldObj, xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ, dir);
    }
    public boolean hasPower() {
        return power >= this.getMaxPower() / 20;
    }
    public boolean hasEnoughWater() {
        return tanks[0].getFill() >= 100;
    }
    @Override
    public FluidTank[] getSendingTanks() {
        return new FluidTank[] { tanks[1] };
    }
    @Override
    public FluidTank[] getReceivingTanks() {
        return new FluidTank[] { tanks[0] };
    }
    @Override
    public FluidTank[] getAllTanks() {
        return tanks;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 65536.0D;
    }

    @Override
    public long getPower() {
        return 0;
    }

    @Override
    public long getMaxPower() {
        return 1000000000;
    }

    @Override
    public void setPower(long power) {

    }

    @Override
    public Container provideContainer(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen provideGUI(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }
}