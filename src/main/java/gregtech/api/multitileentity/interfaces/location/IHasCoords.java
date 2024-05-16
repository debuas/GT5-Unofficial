package gregtech.api.multitileentity.interfaces.location;

import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.util.ForgeDirection;

public interface IHasCoords {
    public int getX();
    public short getY();
    public int getZ();
    public int getOffsetX (ForgeDirection aSide);
    public short getOffsetY (ForgeDirection aSide);
    public int getOffsetZ (ForgeDirection aSide);

    public int getOffsetX (ForgeDirection aSide, int aMultiplier);
    public short getOffsetY (ForgeDirection aSide, int aMultiplier);
    public int getOffsetZ (ForgeDirection aSide, int aMultiplier);

    public int getOffsetXN(ForgeDirection aSide);
    public short getOffsetYN(ForgeDirection aSide);
    public int getOffsetZN(ForgeDirection aSide);

    public int getOffsetXN(ForgeDirection aSide, int aMultiplier);
    public short getOffsetYN(ForgeDirection aSide, int aMultiplier);
    public int getOffsetZN(ForgeDirection aSide, int aMultiplier);

    /** Do not change the XYZ of the returned Coordinates Object! */
    public ChunkCoordinates getCoords();
    /** Do not change the XYZ of the returned Coordinates Object! */
    public ChunkCoordinates getOffset (ForgeDirection aSide, int aMultiplier);
    /** Do not change the XYZ of the returned Coordinates Object! */
    public ChunkCoordinates getOffsetN(ForgeDirection aSide, int aMultiplier);
}
