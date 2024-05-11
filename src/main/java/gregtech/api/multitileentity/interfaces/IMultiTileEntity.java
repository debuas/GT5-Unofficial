package gregtech.api.multitileentity.interfaces;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.multitileentity.MultiTileEntityRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.util.ForgeDirection;

/*
 * Heavily inspired by GT6
 */
public interface IMultiTileEntity {

    int getMetaId();

    int getRegistryId();

    @Nonnull
    ForgeDirection getFacing();

    void setFacing(ForgeDirection facing);

    void initFromNBT(@Nonnull NBTTagCompound nbt, final int registryId, final int metaId);

    @Nonnull
    ChunkCoordinates getCoords();

    @Nonnull
    String getTileEntityName();

    void addToolTip(@Nonnull final List<String> toolTips);

    default void onNeighborBlockChange(@Nonnull final Block block) {
        onBlockPlaced();
    }

    default boolean onBlockActivated(@Nonnull EntityPlayer player, ForgeDirection side, float subX, float subY,
        float subZ) {
        return false;
    }

    default void onBlockPlaced() {}

    default void onBlockBroken() {}

    default void setOwnder(EntityPlayer player) {}

    public static interface IMTE_OnRegistration extends IMultiTileEntity {
        /** Called when the TileEntity is being registered at the MultiTileEntity Registry. */
        public void onRegistration(MultiTileEntityRegistry aRegistry, int aID);
    }

    public static interface IMTE_OnRegistrationFirst extends IMultiTileEntity {
        /** Called when a TileEntity of this particular Class is being registered first at any MultiTileEntity Registry. So basically one call per Class. */
        public void onRegistrationFirst(MultiTileEntityRegistry aRegistry, int aID);
    }

    public static interface IMTE_OnRegistrationFirstOfRegister extends IMultiTileEntity {
        /** Called when a TileEntity of this particular Class is being registered first at a MultiTileEntity Registry. So basically one call per Class and Registry. */
        public void onRegistrationFirstOfRegister(MultiTileEntityRegistry aRegistry, int aID);
    }

    public static interface IMTE_OnRegistrationClient extends IMultiTileEntity {
        /** Called when the TileEntity is being registered at the MultiTileEntity Registry. */
        @SideOnly(Side.CLIENT)
        public void onRegistrationClient(MultiTileEntityRegistry aRegistry, int aID);
    }

    public static interface IMTE_OnRegistrationFirstClient extends IMultiTileEntity {
        /** Called when a TileEntity of this particular Class is being registered first at any MultiTileEntity Registry. So basically one call per Class. */
        @SideOnly(Side.CLIENT)
        public void onRegistrationFirstClient(MultiTileEntityRegistry aRegistry, int aID);
    }

    public static interface IMTE_OnRegistrationFirstOfRegisterClient extends IMultiTileEntity {
        /** Called when a TileEntity of this particular Class is being registered first at a MultiTileEntity Registry. So basically one call per Class and Registry. */
        @SideOnly(Side.CLIENT)
        public void onRegistrationFirstOfRegisterClient(MultiTileEntityRegistry aRegistry, int aID);
    }
}
