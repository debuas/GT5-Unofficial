package gregtech.api.metatileentity.implementations;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.InventoryType;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.tileentity.ITexturedTileEntity;
import gregtech.api.metatileentity.MetaTileEntityStorage;

public abstract class GT_MetaTileEntity_Storage_Chest extends MetaTileEntityStorage implements ITexturedTileEntity

{

    @NotNull
    Materials amaterial;
    @NotNull
    int aID;
    @NotNull
    String aBasicName;
    @NotNull
    String aRegionalame;

    public GT_MetaTileEntity_Storage_Chest(int aID, String aBasicName, String aRegionalName, int aInvSlotCount,
        Materials amaterial) {
        this.aID = aID;
        this.aBasicName = aBasicName;
        this.aRegionalame = aRegionalName;
        this.storageSize = aInvSlotCount;
        this.amaterial = amaterial;
    }

    @Override
    public ArrayList<ItemStack> getDrops() {
        return getStoredItems();
    }

    protected ArrayList<ItemStack> getStoredItems() {
        ItemStack[] items = getItemLogic(ForgeDirection.UNKNOWN, InventoryType.Both).getStoredItems();
        ArrayList<ItemStack> aDrops = new ArrayList<ItemStack>(Arrays.asList(items));
        return aDrops;
    }
}
