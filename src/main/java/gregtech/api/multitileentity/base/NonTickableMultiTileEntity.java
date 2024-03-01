package gregtech.api.multitileentity.base;

import net.minecraft.network.Packet;

public abstract class NonTickableMultiTileEntity extends MultiTileEntity {

    boolean mConstructed = false; // Keeps track of whether this TE has been constructed and placed in the world

    public NonTickableMultiTileEntity() {
        super(false);
    }

    @Override
    public void issueClientUpdate() {
        if (worldObj != null && !worldObj.isRemote) {
            sendGraphicPacket();
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        // We should have a world object and have been constructed by this point
        mConstructed = true;

        super.getDescriptionPacket();
        // We don't get ticked, so if we have any cover data that needs to be sent, send it now
        // sendCoverDataIfNeeded();
        return null;
    }
}
