package com.hbm.inventory.container;

import com.hbm.inventory.SlotCraftingOutput;
import com.hbm.inventory.SlotTakeOnly;

import com.hbm.tileentity.machine.TileEntityQComputer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerMachineQComputer extends Container {

    private TileEntityQComputer qcomputer;

    public ContainerMachineQComputer(InventoryPlayer invPlayer, TileEntityQComputer tedf) {
        qcomputer = tedf;

        // Outputs
        this.addSlotToContainer(new SlotCraftingOutput(invPlayer.player, tedf, 0, 133, 68));
        this.addSlotToContainer(new SlotCraftingOutput(invPlayer.player, tedf, 1, 151, 68));
        // Input
        this.addSlotToContainer(new Slot(tedf, 2, 9, 68));
        this.addSlotToContainer(new Slot(tedf, 3, 27, 68));

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + 56));
            }
        }

        for(int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 142 + 56));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int par2) {
        ItemStack var3 = null;
        Slot var4 = (Slot) this.inventorySlots.get(par2);

        if(var4 != null && var4.getHasStack()) {
            ItemStack var5 = var4.getStack();
            var3 = var5.copy();
            SlotCraftingOutput.checkAchievements(p_82846_1_, var5);

            if(par2 <= 20) {
                if(!this.mergeItemStack(var5, 21, this.inventorySlots.size(), true)) {
                    return null;
                }
            } else if(!this.mergeItemStack(var5, 4, 5, false))
                if(!this.mergeItemStack(var5, 13, 19, false))
                    return null;

            if(var5.stackSize == 0) {
                var4.putStack((ItemStack) null);
            } else {
                var4.onSlotChanged();
            }
        }

        return var3;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return qcomputer.isUseableByPlayer(player);
    }
}
