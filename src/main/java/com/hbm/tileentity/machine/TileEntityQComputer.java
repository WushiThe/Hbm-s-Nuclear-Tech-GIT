package com.hbm.tileentity.machine;

import java.util.ArrayList;
import java.util.List;

import api.hbm.fluid.IFluidStandardReceiver;
import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.interfaces.IFluidAcceptor;
import com.hbm.interfaces.IFluidSource;
import com.hbm.inventory.RecipesCommon;
import com.hbm.inventory.container.ContainerMachineChemplant;
import com.hbm.inventory.fluid.FluidType;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTank;
import com.hbm.lib.Library;
import com.hbm.tileentity.IGUIProvider;
import com.hbm.inventory.container.ContainerMachineQComputer;
import com.hbm.inventory.gui.GUIQComputer;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.util.fauxpointtwelve.DirPos;

import api.hbm.energy.IEnergyUser;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import api.hbm.fluid.IFluidStandardTransceiver;


public class TileEntityQComputer extends TileEntityMachineBase implements IGUIProvider, IEnergyUser, IFluidStandardTransceiver, IFluidAcceptor {

    public FluidTank[] tanks;
    public long power;
    public static final long maxPower = 1000000000L;

    public int progress;
    public int maxProgress = 1_000;
    public static final int consumptionBase = 1_000;
    public int consumption = consumptionBase;
    public long demand = 1000000;
    public boolean isOn = false;

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
        this.tanks = new FluidTank[2];
        this.tanks[0] = new FluidTank(Fluids.COOLANT, 16_000, 0);
        this.tanks[1] = new FluidTank(Fluids.COOLANT_HOT, 16_000, 1);
    }

    @Override
    public String getName() {
        return "container.qcomputer";
    }

    @Override
    public void updateEntity() {

        if (!worldObj.isRemote) {

            this.updateStandardConnections(worldObj, xCoord, yCoord, zCoord);

            this.subscribeToAllAround(tanks[0].getTankType(), this);
            this.subscribeToAllAround(tanks[1].getTankType(), this);

            tanks[0].setType(0, 0, slots);
            tanks[1].setType(0, 0, slots);

            if(isOn) {

                //i.e. 50,000,000 HE = 10,000 SPK
                //1 SPK = 5,000HE

                if (power >= demand) {
                    power -= demand;
                }
            }

            if (worldObj.getTotalWorldTime() % 20 == 0) {
                for (DirPos pos : getConPos())
                    this.trySubscribe(worldObj, pos.getX(), pos.getY(), pos.getZ(), pos.getDir());
            }

            if(worldObj.getTotalWorldTime() % 20 == 0) {
                this.updateConnections();
            }

            this.markDirty();
            tanks[0].updateTank(xCoord, yCoord, zCoord, worldObj.provider.dimensionId);
            tanks[1].updateTank(xCoord, yCoord, zCoord, worldObj.provider.dimensionId);

            NBTTagCompound data = new NBTTagCompound();
            data.setLong("power", power);
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
    public void networkUnpack(NBTTagCompound data) {

        power = data.getLong("power");
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


    // FLUIDS

    ////
    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        power = nbt.getLong("power");
        tanks[0].readFromNBT(nbt, "coolantcold");
        tanks[1].readFromNBT(nbt, "coolanthot");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setLong("power", power);
        tanks[0].writeToNBT(nbt, "coolantcold");
        tanks[1].writeToNBT(nbt, "coolanthot");
    }
    @Override
    public int getMaxFluidFill(FluidType type) {
        if (type.name().equals(tanks[0].getTankType().name()))
            return tanks[0].getMaxFill();
        else if (type.name().equals(tanks[1].getTankType().name()))
            return tanks[1].getMaxFill();
        else
            return 0;
    }

    @Override
    public void setFluidFill(int i, FluidType type) {
        if (type.name().equals(tanks[0].getTankType().name()))
            tanks[0].setFill(i);
        else if (type.name().equals(tanks[1].getTankType().name()))
            tanks[1].setFill(i);
    }

    @Override
    public int getFluidFill(FluidType type) {
        if (type.name().equals(tanks[0].getTankType().name()))
            return tanks[0].getFill();
        else if (type.name().equals(tanks[1].getTankType().name()))
            return tanks[1].getFill();
        else
            return 0;
    }

    @Override
    public void setFillForSync(int fill, int index) {
        if (index < 2 && tanks[index] != null)
            tanks[index].setFill(fill);
    }

    @Override
    public void setTypeForSync(FluidType type, int index) {
        if (index < 2 && tanks[index] != null)
            tanks[index].setTankType(type);
    }
    ////
    @Override
    public FluidTank[] getSendingTanks() {
        return new FluidTank[] {tanks[1]};
    }

    @Override
    public FluidTank[] getReceivingTanks() {
        return new FluidTank[] {tanks[0]};
    }
    @Override
    public FluidTank[] getAllTanks() {
        return tanks;
    }


    //
    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 65536.0D;
    }

    // POWER
    @Override
    public long getPower() {
        return this.power;
    }

    @Override
    public long getMaxPower() {
        return this.maxPower;
    }

    @Override
    public void setPower(long power) {
        this.power = power;
    }

    @Override
    public Container provideContainer(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new ContainerMachineQComputer(player.inventory, this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen provideGUI(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return new GUIQComputer(player.inventory, this);
    }

}