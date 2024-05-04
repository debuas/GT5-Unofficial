package gregtech.api.metatileentity.implementations;

import net.minecraft.util.AxisAlignedBB;

import org.jetbrains.annotations.NotNull;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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

    private static final float minX = 0.0625F, minY = 0F, minZ = 0.0625F, maxX = 0.9375F, maxY = 0.875F, maxZ = 0.9375F;

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

}
