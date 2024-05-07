package gregtech.common.tileentities.storage;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import gregtech.api.enums.Materials;
import gregtech.api.interfaces.ITexture;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Storage_Chest;

public class GT_MetaTileEntity_MetalChest extends GT_MetaTileEntity_Storage_Chest {

    public GT_MetaTileEntity_MetalChest(int aID, String aBasicName, String aRegionalName, int aInvSlotCount,
        Materials aMaterial) {
        super(aID, aBasicName, aRegionalName, aInvSlotCount, aMaterial);
        rgba = aMaterial.getRGBA();
    }

    public GT_MetaTileEntity_MetalChest() {
        this(0,"","",27,Materials._NULL);
    }


    @Override
    public ItemStack getAsItem() {
        return null;
    }

    @Override
    public String getMachineName() {
        return "Metal Chest";
    }

    @Override
    public void setLightValue(byte aLightValue) {}

    @Override
    public ITexture[] getTexture(Block aBlock, ForgeDirection side) {
        return new ITexture[0];
    }

    public byte mFacing = 3;
    protected byte mUsingPlayers = 0;
    protected byte oUsingPlayers = 0;
    public float mLidAngle = 0;
    public float oLidAngle = 0;
    protected float mHardness = 6;
    protected float mResistance = 3;

    public short[] rgba;
    public static String mTextureName;

    @Override
    public boolean shouldDropItemAt(int index) {
        return true;
    }

    @Override
    public int getLightOpacity() {
        return 0;
    }

    @Override
    public void addCollisionBoxesToList(World aWorld, int aX, int aY, int aZ, AxisAlignedBB inputAABB,
        List<AxisAlignedBB> outputAABB, Entity collider) {}

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World aWorld, int aX, int aY, int aZ) {
        return null;
    }
}
