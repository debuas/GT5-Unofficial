package gregtech.api.multitileentity.interfaces.location;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

import static gregtech.api.enums.GT_Values.ALL_VALID_SIDES;
import static gregtech.api.enums.GT_Values.INVALID_SIDES;
import static gregtech.api.enums.GT_Values.OFFX;
import static gregtech.api.enums.GT_Values.OFFY;
import static gregtech.api.enums.GT_Values.OFFZ;
import static gregtech.api.enums.GT_Values.OPOS;
import static gregtech.api.enums.GT_Values.RNGSUS;

/**
 *
 * Contains simple Utility Functions based on the In-World-Coordinates
 */
public class WorldAndCoords implements IHasWorldAndCoords, Comparable<WorldAndCoords> {
    public final int mX, mY, mZ;
    public final World mWorld;

    public WorldAndCoords(World aWorld, int aX, int aY, int aZ) {mWorld = aWorld; mX = aX; mY = aY; mZ = aZ;}
    public WorldAndCoords(World aWorld, ChunkCoordinates aCoords) {mWorld = aWorld; mX = aCoords.posX; mY = aCoords.posY; mZ = aCoords.posZ;}
    public WorldAndCoords(TileEntity aTileEntity) {mWorld = aTileEntity.getWorldObj(); mX = aTileEntity.xCoord; mY = aTileEntity.yCoord; mZ = aTileEntity.zCoord;}

    @Override public World getWorld() {return mWorld;}
    @Override public int getX() {return mX;}
    @Override public short getY() {return (short) mY;}
    @Override public int getZ() {return mZ;}
    @Override public int getOffsetX (ForgeDirection aSide) {return mX + OFFX[aSide.ordinal()];}
    @Override public short getOffsetY (ForgeDirection aSide) {return (short) (mY + OFFY[aSide.ordinal()]);}
    @Override public int getOffsetZ (ForgeDirection aSide) {return mZ + OFFZ[aSide.ordinal()];}
    @Override public int getOffsetX (ForgeDirection aSide, int aMultiplier) {return mX + OFFX[aSide.ordinal()] * aMultiplier;}
    @Override public short getOffsetY (ForgeDirection aSide, int aMultiplier) {return (short) (mY + OFFY[aSide.ordinal()] * aMultiplier);}
    @Override public int getOffsetZ (ForgeDirection aSide, int aMultiplier) {return mZ + OFFZ[aSide.ordinal()] * aMultiplier;}
    @Override public int getOffsetXN(ForgeDirection aSide) {return mX - OFFX[aSide.ordinal()];}
    @Override public short getOffsetYN(ForgeDirection aSide) {return (short) (mY - OFFY[aSide.ordinal()]);}
    @Override public int getOffsetZN(ForgeDirection aSide) {return mZ - OFFZ[aSide.ordinal()];}
    @Override public int getOffsetXN(ForgeDirection aSide, int aMultiplier) {return mX - OFFX[aSide.ordinal()] * aMultiplier;}
    @Override public short getOffsetYN(ForgeDirection aSide, int aMultiplier) {return (short) (mY - OFFY[aSide.ordinal()] * aMultiplier);}
    @Override public int getOffsetZN(ForgeDirection aSide, int aMultiplier) {return mZ - OFFZ[aSide.ordinal()] * aMultiplier;}
    @Override public ChunkCoordinates getCoords() {return new ChunkCoordinates(mX, mY, mZ);}
    @Override public ChunkCoordinates getOffset (ForgeDirection aSide, int aMultiplier) {return new ChunkCoordinates(getOffsetX (aSide, aMultiplier), getOffsetY (aSide, aMultiplier), getOffsetZ (aSide, aMultiplier));}
    @Override public ChunkCoordinates getOffsetN(ForgeDirection aSide, int aMultiplier) {return new ChunkCoordinates(getOffsetXN(aSide, aMultiplier), getOffsetYN(aSide, aMultiplier), getOffsetZN(aSide, aMultiplier));}
    @Override public boolean isServerSide() {return mWorld == null ? cpw.mods.fml.common.FMLCommonHandler.instance().getEffectiveSide().isServer() : !mWorld.isRemote;}
    @Override public boolean isClientSide() {return mWorld == null ? cpw.mods.fml.common.FMLCommonHandler.instance().getEffectiveSide().isClient() :  mWorld.isRemote;}
    @Override public int rng(int aRange) {return RNGSUS.nextInt(aRange);}
    @Override public int getRandomNumber(int aRange) {return RNGSUS.nextInt(aRange);}
    @Override public TileEntity getTileEntity   (int aX, int aY, int aZ) {return mWorld==null?null:mWorld.getTileEntity(aX, aY, aZ);}
    @Override public Block getBlock             (int aX, int aY, int aZ) {return mWorld==null? Blocks.air :mWorld.getBlock(aX, aY, aZ);}
    @Override public byte getMetaData           (int aX, int aY, int aZ) {return mWorld==null?0: (byte) mWorld.getBlockMetadata(aX, aY, aZ);}
    @Override public byte getLightLevel         (int aX, int aY, int aZ) {return (byte) (mWorld==null?0: mWorld.getLightBrightness(aX, aY, aZ)*15);}
    @Override public boolean getOpacity         (int aX, int aY, int aZ) {return mWorld!=null&&mWorld.getBlock(aX, aY, aZ).isOpaqueCube();}
    @Override public boolean getSky             (int aX, int aY, int aZ) {return mWorld==null||mWorld.canBlockSeeTheSky(aX, aY, aZ);}
    @Override public boolean getRain            (int aX, int aY, int aZ) {return mWorld==null||mWorld.getPrecipitationHeight(aX, aZ) <= aY;}
    @Override public boolean getAir             (int aX, int aY, int aZ) {return mWorld==null||mWorld.getBlock(aX, aY, aZ).isAir(mWorld, aX, aY, aZ);}
    @Override public BiomeGenBase getBiome() {return getBiome(mX, mZ);}
    @Override public BiomeGenBase getBiome      (int aX, int aZ) {return mWorld==null?null:mWorld.getBiomeGenForCoords(aX, aZ);}
    @Override public BiomeGenBase getBiome      (ChunkCoordinates aCoords) {return mWorld==null?null:mWorld.getBiomeGenForCoords(aCoords.posX, aCoords.posZ);}
    @Override public TileEntity getTileEntity   (ChunkCoordinates aCoords) {return mWorld==null?null:mWorld.getTileEntity(aCoords.posX, aCoords.posY, aCoords.posZ);}
    @Override public Block getBlock             (ChunkCoordinates aCoords) {return mWorld==null?Blocks.air:mWorld.getBlock(aCoords.posX, aCoords.posY, aCoords.posZ);}
    @Override public byte getMetaData           (ChunkCoordinates aCoords) {return (byte) (mWorld==null?0:mWorld.getBlockMetadata(aCoords.posX, aCoords.posY, aCoords.posZ));}
    @Override public byte getLightLevel         (ChunkCoordinates aCoords) {return (byte) (mWorld==null?0:mWorld.getLightBrightness(aCoords.posX, aCoords.posY, aCoords.posZ)*15);}
    @Override public boolean getOpacity         (ChunkCoordinates aCoords) {return mWorld!=null&&mWorld.getBlock(aCoords.posX, aCoords.posY, aCoords.posZ).isOpaqueCube();}
    @Override public boolean getSky             (ChunkCoordinates aCoords) {return mWorld==null||mWorld.canBlockSeeTheSky(aCoords.posX, aCoords.posY, aCoords.posZ);}
    @Override public boolean getRain            (ChunkCoordinates aCoords) {return mWorld==null||mWorld.getPrecipitationHeight(aCoords.posX, aCoords.posZ) <= aCoords.posY;}
    @Override public boolean getAir             (ChunkCoordinates aCoords) {return mWorld==null||mWorld.getBlock(aCoords.posX, aCoords.posY, aCoords.posZ).isAir(mWorld, aCoords.posX, aCoords.posY, aCoords.posZ);}
    @Override public Block getBlockOffset(int aX, int aY, int aZ) {return getBlock(mX+aX, mY+aY, mZ+aZ);}
    @Override public Block getBlockAtSide(ForgeDirection aSide) {return getBlockAtSideAndDistance(aSide, 1);}
    @Override public Block getBlockAtSideAndDistance(ForgeDirection aSide, int aDistance) {return getBlock(getOffsetX(aSide, aDistance), getOffsetY(aSide, aDistance), getOffsetZ(aSide, aDistance));}
    @Override public byte getMetaDataOffset(int aX, int aY, int aZ) {return getMetaData(mX+aX, mY+aY, mZ+aZ);}
    @Override public byte getMetaDataAtSide(ForgeDirection aSide) {return getMetaDataAtSideAndDistance(aSide, 1);}
    @Override public byte getMetaDataAtSideAndDistance(ForgeDirection aSide, int aDistance) {return getMetaData(getOffsetX(aSide, aDistance), getOffsetY(aSide, aDistance), getOffsetZ(aSide, aDistance));}
    @Override public byte getLightLevelOffset(int aX, int aY, int aZ) {return getLightLevel(mX+aX, mY+aY, mZ+aZ);}
    @Override public byte getLightLevelAtSide(ForgeDirection aSide) {return getLightLevelAtSideAndDistance(aSide, 1);}
    @Override public byte getLightLevelAtSideAndDistance(ForgeDirection aSide, int aDistance) {return getLightLevel(getOffsetX(aSide, aDistance), getOffsetY(aSide, aDistance), getOffsetZ(aSide, aDistance));}
    @Override public boolean getOpacityOffset(int aX, int aY, int aZ) {return getOpacity(mX+aX, mY+aY, mZ+aZ);}
    @Override public boolean getOpacityAtSide(ForgeDirection aSide) {return getOpacityAtSideAndDistance(aSide, 1);}
    @Override public boolean getOpacityAtSideAndDistance(ForgeDirection aSide, int aDistance) {return getOpacity(getOffsetX(aSide, aDistance), getOffsetY(aSide, aDistance), getOffsetZ(aSide, aDistance));}
    @Override public boolean getRainOffset(int aX, int aY, int aZ) {return getRain(mX+aX, mY+aY, mZ+aZ);}
    @Override public boolean getRainAtSide(ForgeDirection aSide) {return getRainAtSideAndDistance(aSide, 1);}
    @Override public boolean getRainAtSideAndDistance(ForgeDirection aSide, int aDistance) {return getRain(getOffsetX(aSide, aDistance), getOffsetY(aSide, aDistance), getOffsetZ(aSide, aDistance));}
    @Override public boolean getSkyOffset(int aX, int aY, int aZ) {return getSky(mX+aX, mY+aY, mZ+aZ);}
    @Override public boolean getSkyAtSide(ForgeDirection aSide) {return getSkyAtSideAndDistance(aSide, 1);}
    @Override public boolean getSkyAtSideAndDistance(ForgeDirection aSide, int aDistance) {return getSky(getOffsetX(aSide, aDistance), getOffsetY(aSide, aDistance), getOffsetZ(aSide, aDistance));}
    @Override public boolean getAirOffset(int aX, int aY, int aZ) {return getAir(mX+aX, mY+aY, mZ+aZ);}
    @Override public boolean getAirAtSide(ForgeDirection aSide) {return getAirAtSideAndDistance(aSide, 1);}
    @Override public boolean getAirAtSideAndDistance(ForgeDirection aSide, int aDistance) {return getAir(getOffsetX(aSide, aDistance), getOffsetY(aSide, aDistance), getOffsetZ(aSide, aDistance));}
    @Override public TileEntity getTileEntityOffset(int aX, int aY, int aZ) {return getTileEntity(mX+aX, mY+aY, mZ+aZ);}
    @Override public TileEntity getTileEntityAtSideAndDistance(ForgeDirection aSide, int aDistance) {return getTileEntity(getOffsetX(aSide, aDistance), getOffsetY(aSide, aDistance), getOffsetZ(aSide, aDistance));}


    @Override
    public boolean hasRedstoneIncoming() {
        for (byte tSide : ALL_VALID_SIDES) if (getRedstoneIncoming(tSide) > 0) return true;
        return false;
    }

    @Override
    public byte getRedstoneIncoming(byte aSide) {
        if (INVALID_SIDES[aSide]) {
            byte rRedstone = 0;
            for (byte tSide : ALL_VALID_SIDES) {
                rRedstone = (byte)Math.max(rRedstone, mWorld.getIndirectPowerLevelTo(getOffsetX(ForgeDirection.getOrientation(tSide)), getOffsetY(ForgeDirection.getOrientation(tSide)), getOffsetZ(ForgeDirection.getOrientation(tSide)), tSide));
                if (rRedstone >= 15) return 15;
            }
            return rRedstone;
        }
        return (byte) mWorld.getIndirectPowerLevelTo(getOffsetX(ForgeDirection.getOrientation(aSide)), getOffsetY(ForgeDirection.getOrientation(aSide)), getOffsetZ(ForgeDirection.getOrientation(aSide)), aSide);
    }

    @Override
    public byte getComparatorIncoming(byte aSide) {
        Block tBlock = getBlockAtSide(ForgeDirection.getOrientation(aSide));
        return tBlock.hasComparatorInputOverride() ? (byte) tBlock.getComparatorInputOverride(mWorld, getOffsetX(ForgeDirection.getOrientation(aSide)), getOffsetY(ForgeDirection.getOrientation(aSide)), getOffsetZ(ForgeDirection.getOrientation(aSide)), OPOS[aSide]) :getRedstoneIncoming(aSide);
    }

    @Override public boolean equals(Object aObject) {return aObject instanceof WorldAndCoords && ((WorldAndCoords)aObject).mWorld == mWorld && ((WorldAndCoords)aObject).mX == mX && ((WorldAndCoords)aObject).mY == mY && ((WorldAndCoords)aObject).mZ == mZ;}
    @Override public int hashCode() {return mX + mZ << 8 + mY << 16;}
    @Override public int compareTo(WorldAndCoords aObject) {return mY == aObject.mY ? mZ == aObject.mZ ? mX - aObject.mX : mZ - aObject.mZ : mY - aObject.mY;}
    @Override public String toString() {return "Pos{x=" + mX + ", y=" + mY + ", z=" + mZ + '}';}
}
