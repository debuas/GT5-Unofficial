package gregtech.api.multitileentity.interfaces.location;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Contains simple Utility Functions based on the In-World-Coordinates of the Implementor for MultiTileEntities.
 */
public interface IHasWorldAndCoords extends IHasWorld, IHasCoords {
    public TileEntity getTileEntityOffset(int aX, int aY, int aZ);
    public TileEntity getTileEntityAtSideAndDistance(ForgeDirection aSide, int aDistance);

    public Block getBlockOffset(int aX, int aY, int aZ);
    public Block getBlockAtSide(ForgeDirection aSide);
    public Block getBlockAtSideAndDistance(ForgeDirection aSide, int aDistance);

    public byte getMetaDataOffset(int aX, int aY, int aZ);
    public byte getMetaDataAtSide(ForgeDirection aSide);
    public byte getMetaDataAtSideAndDistance(ForgeDirection aSide, int aDistance);

    public byte getLightLevelOffset(int aX, int aY, int aZ);
    public byte getLightLevelAtSide(ForgeDirection aSide);
    public byte getLightLevelAtSideAndDistance(ForgeDirection aSide, int aDistance);

    public boolean getOpacityOffset(int aX, int aY, int aZ);
    public boolean getOpacityAtSide(ForgeDirection aSide);
    public boolean getOpacityAtSideAndDistance(ForgeDirection aSide, int aDistance);

    public boolean getSkyOffset(int aX, int aY, int aZ);
    public boolean getSkyAtSide(ForgeDirection aSide);
    public boolean getSkyAtSideAndDistance(ForgeDirection aSide, int aDistance);

    public boolean getRainOffset(int aX, int aY, int aZ);
    public boolean getRainAtSide(ForgeDirection aSide);
    public boolean getRainAtSideAndDistance(ForgeDirection aSide, int aDistance);

    public boolean getAirOffset(int aX, int aY, int aZ);
    public boolean getAirAtSide(ForgeDirection aSide);
    public boolean getAirAtSideAndDistance(ForgeDirection aSide, int aDistance);

    public BiomeGenBase getBiome();

    public boolean hasRedstoneIncoming();
    public byte getRedstoneIncoming(byte aSide);
    public byte getComparatorIncoming(byte aSide);
}
