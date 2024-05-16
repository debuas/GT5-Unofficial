package gregtech.api.multitileentity.interfaces;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public interface IBlockMultiTileEntity {
    /** can return a TileEntity for this Block even if it has no actual one, in order to provide an Interface to the Main-Block of a MultiBlock Contraption. */
    public TileEntity getTileEntity(IBlockAccess aWorld, int aX, int aY, int aZ);
}
