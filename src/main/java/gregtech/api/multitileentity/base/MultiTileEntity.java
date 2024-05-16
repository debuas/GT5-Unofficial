package gregtech.api.multitileentity.base;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;

import appeng.api.movable.IMovableTile;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.ITexture;
import gregtech.api.multitileentity.interfaces.IBlockMultiTileEntity;
import gregtech.api.multitileentity.interfaces.IMultiTileEntityUnloadable;
import gregtech.api.multitileentity.interfaces.location.ExplosionGT;
import gregtech.api.multitileentity.interfaces.location.IHasWorldAndCoords;
import gregtech.common.render.IRenderedBlock;
import gregtech.common.render.IRenderedBlockObject;
import gregtech.common.render.IRenderedBlockObjectSideCheck;
import gregtech.common.render.MultiTileBasicRender;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockRailBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.EnumSkyBlock;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizons.modularui.common.internal.network.NetworkUtils;

import gregtech.api.GregTech_API;
import gregtech.api.enums.GT_Values;
import gregtech.api.enums.GT_Values.NBT;
import gregtech.api.enums.Mods;
import gregtech.api.enums.Textures;
import gregtech.api.enums.Textures.BlockIcons.CustomIcon;
import gregtech.api.interfaces.IToolStats;
import gregtech.api.items.GT_MetaGenerated_Tool;
import gregtech.api.multitileentity.MultiTileEntityClassContainer;
import gregtech.api.multitileentity.MultiTileEntityRegistry;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity;
import gregtech.api.multitileentity.interfaces.SyncedMultiTileEntity;
import gregtech.api.net.GT_Packet_MultiTileEntity;
import gregtech.api.net.data.CommonData;
import gregtech.api.net.data.CoordinateData;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_Util;
import gregtech.api.util.GT_Utility;
import gregtech.common.tools.GT_Tool_Crowbar;
import gregtech.common.tools.GT_Tool_HardHammer;
import gregtech.common.tools.GT_Tool_Screwdriver;
import gregtech.common.tools.GT_Tool_SoftHammer;
import gregtech.common.tools.GT_Tool_Soldering_Iron;
import gregtech.common.tools.GT_Tool_WireCutter;
import gregtech.common.tools.GT_Tool_Wrench;
import net.minecraftforge.fluids.IFluidBlock;
import org.jetbrains.annotations.NotNull;

import static gregtech.GT_Mod.GT_FML_LOGGER;
import static gregtech.api.enums.GT_Values.ALL_SIDES;
import static gregtech.api.enums.GT_Values.ALL_SIDES_BUT_BOTTOM;
import static gregtech.api.enums.GT_Values.ALL_VALID_SIDES;
import static gregtech.api.enums.GT_Values.INVALID_SIDES;
import static gregtech.api.enums.GT_Values.NW;
import static gregtech.api.enums.GT_Values.OFFX;
import static gregtech.api.enums.GT_Values.OFFY;
import static gregtech.api.enums.GT_Values.OFFZ;
import static gregtech.api.enums.GT_Values.OPOS;
import static gregtech.api.enums.GT_Values.RNGSUS;
import static gregtech.api.util.GT_Util.LAST_BROKEN_TILEENTITY;

@Optional.InterfaceList(value = {
    @Optional.Interface(iface = "appeng.api.movable.IMovableTile", modid = Mods.Names.APPLIED_ENERGISTICS2)})
public abstract class MultiTileEntity extends TileEntity
    implements MultiTileBasicRender, SyncedMultiTileEntity, IMultiTileEntity, IHasWorldAndCoords, IMultiTileEntityUnloadable, IMovableTile {

    // MultTileEntity variables
    /** If this TileEntity checks for the Chunk to be loaded before returning World based values. If this is set to true, this TileEntity will not cause worfin' Chunks, uhh I mean orphan Chunks. */
    public boolean mIgnoreUnloadedChunks = true;
    /** This Variable checks if this TileEntity is dead, because Minecraft is too stupid to have proper TileEntity unloading. */
    public boolean mIsDead = false;
    /** This Variable checks if this TileEntity should refresh when the Block is being set. That way you can turn this check off any time you need it. */
    public boolean mShouldRefresh = true;
    /** This Variable is for a buffered Block Update. */
    public boolean mDoesBlockUpdate = false;
    /** This Variable is for forcing the Selection Box to be full. */
    public boolean FORCE_FULL_SELECTION_BOXES = false;
    private final boolean mIsTicking; // If this TileEntity is ticking at all
    private int metaId = -1; // The MuTE ID of the entity inside the registry
    private int registryId = -1; // The registry ID of the entity
    @Nonnull
    private ForgeDirection facing = ForgeDirection.WEST; // Default to WEST, so it renders facing Left in the
    @Nonnull
    private final ChunkCoordinates cachedCoordinates = new ChunkCoordinates();

    // MultTileBasicRender variables
    private ITexture baseTexture = null;
    private ITexture topOverlayTexture = null;
    private ITexture bottomOverlayTexture = null;
    private ITexture leftOverlayTexture = null;
    private ITexture rightOverlayTexture = null;
    private ITexture backOverlayTexture = null;
    private ITexture frontOverlayTexture = null;

    // SyncedMultiTileEntity variables
    private final GT_Packet_MultiTileEntity fullPacket = new GT_Packet_MultiTileEntity(false);
    private final GT_Packet_MultiTileEntity timedPacket = new GT_Packet_MultiTileEntity(false);
    private final GT_Packet_MultiTileEntity graphicPacket = new GT_Packet_MultiTileEntity(false);

    public MultiTileEntity(boolean aIsTicking) {
        this.mIsTicking = aIsTicking;
    }

    // TileEntity methods


    @Override
    public void onTileEntityPlaced() { /**/ }

    @Override
    public void onAdjacentBlockChange(int aTileX, int aTileY, int aTileZ) { /**/ }

    @Override
    public Packet getDescriptionPacket() {
        sendGraphicPacket();
        return super.getDescriptionPacket();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (getMetaId() == -1 || getRegistryId() == -1) {
            metaId = nbt.getInteger(NBT.MTE_ID);
            registryId = nbt.getInteger(NBT.MTE_REG);
            MultiTileEntityRegistry registry = MultiTileEntityRegistry.getRegistry(registryId);
            MultiTileEntityClassContainer clazz = registry.getClassContainer(metaId);
            nbt = GT_Util.fuseNBT(nbt, clazz.getParameters());
        }
        if (nbt.hasKey(NBT.FACING)) setFacing(ForgeDirection.getOrientation(nbt.getInteger(NBT.FACING)));
        if (NetworkUtils.isDedicatedClient()) {
            if (GregTech_API.sBlockIcons == null && nbt.hasKey(NBT.TEXTURE_FOLDER)) {
                loadTextures(nbt.getString(NBT.TEXTURE_FOLDER));
            } else {
                copyTextures();
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger(NBT.FACING, facing.ordinal());
        nbt.setInteger(NBT.MTE_ID, getMetaId());
        nbt.setInteger(NBT.MTE_REG, getRegistryId());
    }

    // MultiTileEntity methods
    @Override
    public int getRegistryId() {
        return registryId;
    }

    @Override
    public int getMetaId() {
        return metaId;
    }

    @Override
    @Nonnull
    public ForgeDirection getFacing() {
        return facing;
    }

    @Override
    public void setFacing(ForgeDirection facing) {
        this.facing = facing;
    }

    public abstract String getTileEntityName();

    @Override public void markDirty() { /**/ }
    @Override public World getWorld() {return worldObj;}
    @Override public int getX() {return xCoord;}
    @Override public short getY() {return (short) yCoord;}
    @Override public int getZ() {return zCoord;}
    @Override public int getOffsetX (ForgeDirection aSide) {return xCoord + OFFX[aSide.ordinal()];}
    @Override public short getOffsetY (ForgeDirection aSide) {return (short) (yCoord + OFFY[aSide.ordinal()]);}
    @Override public int getOffsetZ (ForgeDirection aSide) {return zCoord + OFFZ[aSide.ordinal()];}
    @Override public int getOffsetX (ForgeDirection aSide, int aMultiplier) {return xCoord + OFFX[aSide.ordinal()] * aMultiplier;}
    @Override public short getOffsetY (ForgeDirection aSide, int aMultiplier) {return (short) (yCoord + OFFY[aSide.ordinal()] * aMultiplier);}
    @Override public int getOffsetZ (ForgeDirection aSide, int aMultiplier) {return zCoord + OFFZ[aSide.ordinal()] * aMultiplier;}
    @Override public int getOffsetXN(ForgeDirection aSide) {return xCoord - OFFX[aSide.ordinal()];}
    @Override public short getOffsetYN(ForgeDirection aSide) {return (short) (yCoord - OFFY[aSide.ordinal()]);}
    @Override public int getOffsetZN(ForgeDirection aSide) {return zCoord - OFFZ[aSide.ordinal()];}
    @Override public int getOffsetXN(ForgeDirection aSide, int aMultiplier) {return xCoord - OFFX[aSide.ordinal()] * aMultiplier;}
    @Override public short getOffsetYN(ForgeDirection aSide, int aMultiplier) {return (short) (yCoord - OFFY[aSide.ordinal()] * aMultiplier);}
    @Override public int getOffsetZN(ForgeDirection aSide, int aMultiplier) {return zCoord - OFFZ[aSide.ordinal()] * aMultiplier;}
    @Override public @NotNull ChunkCoordinates getCoords() {cachedCoordinates.posX = xCoord; cachedCoordinates.posY = yCoord; cachedCoordinates.posZ = zCoord; return cachedCoordinates;}
    @Override public ChunkCoordinates getOffset (ForgeDirection aSide, int aMultiplier) {return new ChunkCoordinates(getOffsetX (aSide, aMultiplier), getOffsetY (aSide, aMultiplier), getOffsetZ (aSide, aMultiplier));}
    @Override public ChunkCoordinates getOffsetN(ForgeDirection aSide, int aMultiplier) {return new ChunkCoordinates(getOffsetXN(aSide, aMultiplier), getOffsetYN(aSide, aMultiplier), getOffsetZN(aSide, aMultiplier));}
    @Override public boolean isServerSide() {return worldObj == null ? cpw.mods.fml.common.FMLCommonHandler.instance().getEffectiveSide().isServer() : !worldObj.isRemote;}
    @Override public boolean isClientSide() {return worldObj == null ? cpw.mods.fml.common.FMLCommonHandler.instance().getEffectiveSide().isClient() :  worldObj.isRemote;}
    @Override public int getRandomNumber(int aRange) {return RNGSUS.nextInt(aRange);}
    @Override public int rng(int aRange) {return RNGSUS.nextInt(aRange);}
    public boolean rng() {return RNGSUS.nextBoolean();}
    @Override public BiomeGenBase getBiome(ChunkCoordinates aCoords) {return worldObj==null?BiomeGenBase.plains:worldObj.getBiomeGenForCoords(aCoords.posX, aCoords.posZ);}
    @Override public BiomeGenBase getBiome(int aX, int aZ) {return worldObj==null?BiomeGenBase.plains:worldObj.getBiomeGenForCoords(aX, aZ);}
    @Override public BiomeGenBase getBiome() {return getBiome(xCoord, zCoord);}
    @Override public Block getBlockOffset(int aX, int aY, int aZ) {return getBlock(xCoord+aX, yCoord+aY, zCoord+aZ);}
    @Override public Block getBlockAtSide(ForgeDirection aSide) {return getBlockAtSideAndDistance(aSide, 1);}
    @Override public Block getBlockAtSideAndDistance(ForgeDirection aSide, int aDistance) {return getBlock(getOffsetX(aSide, aDistance), getOffsetY(aSide, aDistance), getOffsetZ(aSide, aDistance));}
    @Override public byte getMetaDataOffset(int aX, int aY, int aZ) {return getMetaData(xCoord + aX, yCoord + aY, zCoord + aZ);}
    @Override public byte getMetaDataAtSide(ForgeDirection aSide) {return getMetaDataAtSideAndDistance(aSide, 1);}
    @Override public byte getMetaDataAtSideAndDistance(ForgeDirection aSide, int aDistance) {return getMetaData(getOffsetX(aSide, aDistance), getOffsetY(aSide, aDistance), getOffsetZ(aSide, aDistance));}
    @Override public byte getLightLevelOffset(int aX, int aY, int aZ) {return getLightLevel(xCoord+aX, yCoord+aY, zCoord+aZ);}
    @Override public byte getLightLevelAtSide(ForgeDirection aSide) {return getLightLevelAtSideAndDistance(aSide, 1);}
    @Override public byte getLightLevelAtSideAndDistance(ForgeDirection aSide, int aDistance) {return getLightLevel(getOffsetX(aSide, aDistance), getOffsetY(aSide, aDistance), getOffsetZ(aSide, aDistance));}
    @Override public boolean getOpacityOffset(int aX, int aY, int aZ) {return getOpacity(xCoord+aX, yCoord+aY, zCoord+aZ);}
    @Override public boolean getOpacityAtSide(ForgeDirection aSide) {return getOpacityAtSideAndDistance(aSide, 1);}
    @Override public boolean getOpacityAtSideAndDistance(ForgeDirection aSide, int aDistance) {return getOpacity(getOffsetX(aSide, aDistance), getOffsetY(aSide, aDistance), getOffsetZ(aSide, aDistance));}
    @Override public boolean getRainOffset(int aX, int aY, int aZ) {return getRain(xCoord+aX, yCoord+aY, zCoord+aZ);}
    @Override public boolean getRainAtSide(ForgeDirection aSide) {return getRainAtSideAndDistance(aSide, 1);}
    @Override public boolean getRainAtSideAndDistance(ForgeDirection aSide, int aDistance) {return getRain(getOffsetX(aSide, aDistance), getOffsetY(aSide, aDistance), getOffsetZ(aSide, aDistance));}
    @Override public boolean getSkyOffset(int aX, int aY, int aZ) {return getSky(xCoord+aX, yCoord+aY, zCoord+aZ);}
    @Override public boolean getSkyAtSide(ForgeDirection aSide) {return getSkyAtSideAndDistance(aSide, 1);}
    @Override public boolean getSkyAtSideAndDistance(ForgeDirection aSide, int aDistance) {return getSky(getOffsetX(aSide, aDistance), getOffsetY(aSide, aDistance), getOffsetZ(aSide, aDistance));}
    @Override public boolean getAirOffset(int aX, int aY, int aZ) {return getAir(xCoord+aX, yCoord+aY, zCoord+aZ);}
    @Override public boolean getAirAtSide(ForgeDirection aSide) {return getAirAtSideAndDistance(aSide, 1);}
    @Override public boolean getAirAtSideAndDistance(ForgeDirection aSide, int aDistance) {return getAir(getOffsetX(aSide, aDistance), getOffsetY(aSide, aDistance), getOffsetZ(aSide, aDistance));}
    @Override public TileEntity getTileEntityOffset(int aX, int aY, int aZ) {return getTileEntity(xCoord+aX, yCoord+aY, zCoord+aZ);}
    @Override public TileEntity getTileEntityAtSideAndDistance(ForgeDirection aSide, int aDistance) {return getTileEntity(getOffsetX(aSide, aDistance), getOffsetY(aSide, aDistance), getOffsetZ(aSide, aDistance));}

    @Override
    public Block getBlock(int aX, int aY, int aZ) {
        if (worldObj == null) return Blocks.air;
        if (mIgnoreUnloadedChunks && crossedChunkBorder(aX, aZ) && !worldObj.blockExists(aX, aY, aZ)) return Blocks.air;
        return worldObj.getBlock(aX, aY, aZ);
    }

    @Override
    public byte getMetaData(int aX, int aY, int aZ) {
        if (worldObj == null) return 0;
        if (mIgnoreUnloadedChunks && crossedChunkBorder(aX, aZ) && !worldObj.blockExists(aX, aY, aZ)) return 0;
        return (byte) worldObj.getBlockMetadata(aX, aY, aZ);
    }

    @Override
    public byte getLightLevel(int aX, int aY, int aZ) {
        if (worldObj == null) return 14;
        if (mIgnoreUnloadedChunks && crossedChunkBorder(aX, aZ) && !worldObj.blockExists(aX, aY, aZ)) return 0;
        return (byte) (worldObj.getLightBrightness(aX, aY, aZ) * 15);
    }

    @Override
    public boolean getSky(int aX, int aY, int aZ) {
        if (worldObj == null) return true;
        if (mIgnoreUnloadedChunks && crossedChunkBorder(aX, aZ) && !worldObj.blockExists(aX, aY, aZ)) return true;
        return worldObj.canBlockSeeTheSky(aX, aY, aZ);
    }

    @Override
    public boolean getRain(int aX, int aY, int aZ) {
        if (worldObj == null) return true;
        if (mIgnoreUnloadedChunks && crossedChunkBorder(aX, aZ) && !worldObj.blockExists(aX, aY, aZ)) return true;
        return worldObj.getPrecipitationHeight(aX, aZ) <= aY;
    }

    @Override
    public boolean getOpacity(int aX, int aY, int aZ) {
        if (worldObj == null) return false;
        if (mIgnoreUnloadedChunks && crossedChunkBorder(aX, aZ) && !worldObj.blockExists(aX, aY, aZ)) return false;
        return worldObj.getBlock(aX, aY, aZ).isOpaqueCube();
    }

    @Override
    public boolean getAir(int aX, int aY, int aZ) {
        if (worldObj == null) return true;
        if (mIgnoreUnloadedChunks && crossedChunkBorder(aX, aZ) && !worldObj.blockExists(aX, aY, aZ)) return true;
        return worldObj.getBlock(aX, aY, aZ).isAir(worldObj, aX, aY, aZ);
    }

    @Override
    public TileEntity getTileEntity(int aX, int aY, int aZ) {
        if (worldObj == null) return null;
        if (mIgnoreUnloadedChunks && crossedChunkBorder(aX, aZ) && !worldObj.blockExists(aX, aY, aZ)) return null;
        return GT_Util.getTileEntity(worldObj, aX, aY, aZ, true);
    }

    @Override
    public Block getBlock(ChunkCoordinates aCoords) {
        if (worldObj == null) return Blocks.air;
        if (mIgnoreUnloadedChunks && crossedChunkBorder(aCoords) && !worldObj.blockExists(aCoords.posX, aCoords.posY, aCoords.posZ)) return Blocks.air;
        return worldObj.getBlock(aCoords.posX, aCoords.posY, aCoords.posZ);
    }

    @Override
    public byte getMetaData(ChunkCoordinates aCoords) {
        if (worldObj == null) return 0;
        if (mIgnoreUnloadedChunks && crossedChunkBorder(aCoords) && !worldObj.blockExists(aCoords.posX, aCoords.posY, aCoords.posZ)) return 0;
        return (byte)worldObj.getBlockMetadata(aCoords.posX, aCoords.posY, aCoords.posZ);
    }

    @Override
    public byte getLightLevel(ChunkCoordinates aCoords) {
        if (worldObj == null) return 14;
        if (mIgnoreUnloadedChunks && crossedChunkBorder(aCoords) && !worldObj.blockExists(aCoords.posX, aCoords.posY, aCoords.posZ)) return 0;
        return (byte) ((byte) worldObj.getLightBrightness(aCoords.posX, aCoords.posY, aCoords.posZ)*15);
    }

    @Override
    public boolean getSky(ChunkCoordinates aCoords) {
        if (worldObj == null) return true;
        if (mIgnoreUnloadedChunks && crossedChunkBorder(aCoords) && !worldObj.blockExists(aCoords.posX, aCoords.posY, aCoords.posZ)) return true;
        return worldObj.canBlockSeeTheSky(aCoords.posX, aCoords.posY, aCoords.posZ);
    }

    @Override
    public boolean getRain(ChunkCoordinates aCoords) {
        if (worldObj == null) return true;
        if (mIgnoreUnloadedChunks && crossedChunkBorder(aCoords) && !worldObj.blockExists(aCoords.posX, aCoords.posY, aCoords.posZ)) return true;
        return worldObj.getPrecipitationHeight(aCoords.posX, aCoords.posZ) <= aCoords.posY;
    }

    @Override
    public boolean getOpacity(ChunkCoordinates aCoords) {
        if (worldObj == null) return false;
        if (mIgnoreUnloadedChunks && crossedChunkBorder(aCoords) && !worldObj.blockExists(aCoords.posX, aCoords.posY, aCoords.posZ)) return false;
        return worldObj.getBlock(aCoords.posX, aCoords.posY, aCoords.posZ).isOpaqueCube();
    }

    @Override
    public boolean getAir(ChunkCoordinates aCoords) {
        if (worldObj == null) return true;
        if (mIgnoreUnloadedChunks && crossedChunkBorder(aCoords) && !worldObj.blockExists(aCoords.posX, aCoords.posY, aCoords.posZ)) return true;
        return worldObj.getBlock(aCoords.posX, aCoords.posY, aCoords.posZ).isAir(worldObj, aCoords.posX, aCoords.posY, aCoords.posZ);
    }

    @Override
    public TileEntity getTileEntity(ChunkCoordinates aCoords) {
        if (worldObj == null) return null;
        if (mIgnoreUnloadedChunks && crossedChunkBorder(aCoords) && !worldObj.blockExists(aCoords.posX, aCoords.posY, aCoords.posZ)) return null;
        return GT_Util.getTileEntity(worldObj, aCoords, true);
    }

    @Override
    public void sendBlockEvent(byte aID, byte aValue) {
        NW.sendPacketToAllPlayersInRange(worldObj, fullPacket, aID, aValue);
    }

    @Override
    public boolean isDead() {
        return mIsDead;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        setDead();
    }

    @Override
    public void validate() {
        super.validate();
        setAlive();
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        setDead();
    }

    public void setDead() {
        mIsDead = true;
    }

    public void setAlive() {
        mIsDead = false;
    }

    @Override
    public void updateEntity() {
        // Well, if the TileEntity gets ticked, it is alive.
        if (isDead()) setAlive();

        if (mExplosionStrength > 0) {
            setToAir();
            if (mExplosionStrength < 1) {
                GT_Utility.sendSoundToPlayers(worldObj, SoundResource.RANDOM_EXPLODE, 1, 1, getX(), getY(), getZ());
            } else {
                ExplosionGT.explode(worldObj, null, xCoord, yCoord, zCoord, mExplosionStrength, false, true);
            }
            setDead();
            return;
        }
        if (mDoesBlockUpdate) doBlockUpdate();
    }

    @Override
    public boolean canUpdate() {
        return mIsTicking && mShouldRefresh;
    }

    @Override
    public boolean shouldRefresh(Block aOldBlock, Block aNewBlock, int aOldMeta, int aNewMeta, World aWorld, int aX, int aY, int aZ) {
        return mShouldRefresh || aOldBlock != aNewBlock;
    }

    /** Simple Function to prevent Block Updates from happening multiple times within the same Tick. */
    public final void causeBlockUpdate() {
        if (mIsTicking) mDoesBlockUpdate = true; else doBlockUpdate();
    }

    public void doBlockUpdate() {
        Block tBlock = getBlock(getX(), getY(), getZ());
        worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, tBlock);
        if (this instanceof IMTE_IsProvidingStrongPower) for (byte tSide : ALL_VALID_SIDES) {
            if (getBlockAtSide(ForgeDirection.getOrientation(tSide)).isNormalCube(worldObj, xCoord+OFFX[tSide], yCoord+OFFY[tSide], zCoord+OFFZ[tSide])) {
                worldObj.notifyBlocksOfNeighborChange(xCoord+OFFX[tSide], yCoord+OFFY[tSide], zCoord+OFFZ[tSide], tBlock, OPOS[tSide]);
            }
        }
        mDoesBlockUpdate = false;
    }

    public final boolean crossedChunkBorder(int aX, int aZ) {
        return aX >> 4 != xCoord >> 4 || aZ >> 4 != zCoord >> 4;
    }

    public final boolean crossedChunkBorder(ChunkCoordinates aCoords) {
        return aCoords.posX >> 4 != xCoord >> 4 || aCoords.posZ >> 4 != zCoord >> 4;
    }

    public float mExplosionStrength = 0;

    public final void explode() {
        explode(!mIsTicking);
    }

    public final void explode(double aStrength) {
        explode(!mIsTicking, aStrength);
    }

    public void explode(boolean aInstant) {
        // 4 Seems to be a reasonable Default Explosion. This Function can of course be overridden.
        explode(aInstant, 4);
    }

    public void explode(boolean aInstant, double aStrength) {
        mExplosionStrength = (float)Math.max(aStrength, mExplosionStrength);
        if (aInstant) {
            setToAir();
            mExplodeSpamCooldown = 0;
            if (mExplosionStrength < 1) {
                GT_Utility.sendSoundToPlayers(worldObj, SoundResource.RANDOM_EXPLODE, 1, 1, getX(), getY(), getZ());
            } else {
                ExplosionGT.explode(worldObj, null, xCoord, yCoord, zCoord, mExplosionStrength, false, true);
            }
        }
    }

    public float getExplosionResistance(Entity aExploder, double aExplosionX, double aExplosionY, double aExplosionZ) {
        return mExplosionStrength > 0 ? 0 : getExplosionResistance2(aExploder, aExplosionX, aExplosionY, aExplosionZ);
    }
    public float getExplosionResistance() {
        return mExplosionStrength > 0 ? 0 : getExplosionResistance2();
    }

    public float getExplosionResistance2(Entity aExploder, double aExplosionX, double aExplosionY, double aExplosionZ) {
        return getExplosionResistance2();
    }

    public float getExplosionResistance2() {
        return 0;
    }

    public boolean needsToSyncEverything() {return false;}

    public boolean shouldSideBeRendered(ForgeDirection aSide) {
        TileEntity tTileEntity = getTileEntityAtSideAndDistance(aSide, 1);
        return tTileEntity instanceof IMultiTileEntity ? !((IMultiTileEntity) tTileEntity).isSurfaceOpaque(OPOS[aSide.ordinal()]): false;
    }

    @SideOnly(Side.CLIENT) public boolean renderItem(Block aBlock, RenderBlocks aRenderer) {return false;}
    @SideOnly(Side.CLIENT) public boolean renderBlock(Block aBlock, RenderBlocks aRenderer, IBlockAccess aWorld, int aX, int aY, int aZ) {return false;}
    @SideOnly(Side.CLIENT) public boolean usesRenderPass(int aRenderPass, boolean[] aShouldSideBeRendered) {return true;}
    @SideOnly(Side.CLIENT) public boolean renderFullBlockSide(Block aBlock, RenderBlocks aRenderer, byte aSide) {return shouldSideBeRendered(ForgeDirection.getOrientation(aSide));}
    @SideOnly(Side.CLIENT) public final IRenderedBlockObject passRenderingToObject(ItemStack aStack) {return ERROR_MESSAGE == null ? passRenderingToObject2(aStack) : IRenderedBlockObject.ErrorRenderer.INSTANCE;}
    @SideOnly(Side.CLIENT) public final IRenderedBlockObject passRenderingToObject(IBlockAccess aWorld, int aX, int aY, int aZ) {return ERROR_MESSAGE == null ? passRenderingToObject2(aWorld, aX, aY, aZ) : IRenderedBlockObject.ErrorRenderer.INSTANCE;}
    @SideOnly(Side.CLIENT) public IRenderedBlockObject passRenderingToObject2(ItemStack aStack) {return (IRenderedBlockObject) this;}
    @SideOnly(Side.CLIENT) public IRenderedBlockObject passRenderingToObject2(IBlockAccess aWorld, int aX, int aY, int aZ) {return (IRenderedBlockObject) this;}

    public void playClick() {
        GT_Utility.sendSoundToPlayers(worldObj, SoundResource.RANDOM_CLICK, 1, 1, getX(), getY(), getZ());
    }
    public void playCollect() {
        GT_Utility.sendSoundToPlayers(worldObj, SoundResource.RANDOM_POP, 0.2F, ((RNGSUS.nextFloat() - RNGSUS.nextFloat()) * 0.7F + 1) * 2, getX(), getY(), getZ());
    }

    public void updateLightValue() {
        if (this instanceof IMTE_GetLightValue) {
            worldObj.setLightValue(EnumSkyBlock.Block, xCoord, yCoord, zCoord, ((IMTE_GetLightValue)this).getLightValue());
            for (byte tSide : ALL_SIDES) worldObj.updateLightByType(EnumSkyBlock.Block, xCoord+OFFX[tSide], yCoord+OFFY[tSide], zCoord+OFFZ[tSide]);
        }
    }

    public boolean shouldCheckWeakPower(byte aSide) {
        return false;
    }

    @Override
    public boolean hasRedstoneIncoming() {
        return hasRedstoneIncoming(ALL_VALID_SIDES);
    }

    public boolean hasRedstoneIncoming(byte[] aSides) {
        for (byte tSide : aSides) if (getRedstoneIncoming(tSide) > 0) return true;
        return false;
    }

    public boolean hasRedstoneIncomingFromNonRail() {
        return hasRedstoneIncomingFromNonRail(ALL_VALID_SIDES);
    }

    public boolean hasRedstoneIncomingFromNonRail(byte[] aSides) {
        for (byte tSide : aSides) if (!(getBlockAtSide(ForgeDirection.getOrientation(tSide)) instanceof BlockRailBase) && getRedstoneIncoming(tSide) > 0) return true;
        return false;
    }

    @Override
    public byte getRedstoneIncoming(byte aSide) {
        if (worldObj == null) return 0;
        if (INVALID_SIDES[aSide]) {
            byte rRedstone = 0;
            for (byte tSide : ALL_VALID_SIDES) {
                rRedstone = (byte)Math.max(rRedstone, worldObj.getIndirectPowerLevelTo(getOffsetX(ForgeDirection.getOrientation(tSide), 1), getOffsetY(ForgeDirection.getOrientation(tSide), 1), getOffsetZ(ForgeDirection.getOrientation(tSide), 1), tSide));
                if (rRedstone >= 15) return 15;
            }
            return rRedstone;
        }
        return (byte) worldObj.getIndirectPowerLevelTo(getOffsetX(ForgeDirection.getOrientation(aSide)), getOffsetY(ForgeDirection.getOrientation(aSide)), getOffsetZ(ForgeDirection.getOrientation(aSide)), aSide);
    }

    @Override
    public byte getComparatorIncoming(byte aSide) {
        if (worldObj == null) return 0;
        Block tBlock = getBlockAtSide(ForgeDirection.getOrientation(aSide));
        return tBlock.hasComparatorInputOverride() ? (byte) tBlock.getComparatorInputOverride(worldObj, getOffsetX(ForgeDirection.getOrientation(aSide)), getOffsetY(ForgeDirection.getOrientation(aSide)), getOffsetZ(ForgeDirection.getOrientation(aSide)), OPOS[aSide]) : getRedstoneIncoming(aSide);
    }

    private byte mExplodeSpamCooldown = 0;
    private static boolean liquid(Block aBlock) {return aBlock instanceof BlockLiquid || aBlock instanceof IFluidBlock;}

    public boolean doDefaultStructuralChecks() {
        for (byte tSide : ALL_VALID_SIDES) {
            if (!isFireProof(tSide) && getBlockAtSide(ForgeDirection.getOrientation(tSide)) instanceof BlockFire && rng(10) == 0) {
                explode(0.1);
            }
            if (mExplodeSpamCooldown++ == 0) {
                GT_Utility.sendSoundToPlayers(worldObj, SoundResource.RANDOM_EXPLODE, 1, 1, getX(),getY(),getZ());
            }
            return false;
        }
        for (byte tSide : ALL_SIDES_BUT_BOTTOM) {
            if (!isWaterProof(tSide) && liquid(getBlockAtSide(ForgeDirection.getOrientation(tSide)))) {
                explode(0.1);
                if (mExplodeSpamCooldown++ == 0) {
                    GT_Utility.sendSoundToPlayers(worldObj, SoundResource.RANDOM_EXPLODE, 1, 1, getX(), getY(), getZ());
                }
                return false;
            }
            if (!isRainProof(tSide) && worldObj.isRaining() && getBiome().rainfall > 0 && rng(100) == 0 && getRainAtSide(ForgeDirection.getOrientation(tSide))) {
                explode(0.1);
                if (mExplodeSpamCooldown++ == 0) {
                    GT_Utility.sendSoundToPlayers(worldObj, SoundResource.RANDOM_EXPLODE, 1, 1, getX(), getY(), getZ());
                }
                return false;
            }
        }
        for (byte tSide : ALL_SIDES_BUT_BOTTOM) {
            if (!isThunderProof(tSide) && worldObj.isThundering() && rng(1000) == 0 && getRainAtSide(ForgeDirection.getOrientation(tSide))) {
                explode(0.1);
                if (mExplodeSpamCooldown++ == 0) {
                    GT_Utility.sendSoundToPlayers(worldObj, SoundResource.RANDOM_EXPLODE, 1, 1, getX(), getY(), getZ());
                }
                return false;
            }
        }
        return true;
    }

    public boolean BlockCanBurn = true;
    public boolean isFireProof (byte aSide) {return false;}
    public boolean isRainProof (byte aSide) {return false;}
    public boolean isWaterProof (byte aSide) {return false;}
    public boolean isThunderProof (byte aSide) {return false;}

    // A Default implementation of the Surface behavior.

    public float getSurfaceSize             (byte aSide) {return 1;}
    public float getSurfaceSizeAttachable   (byte aSide) {return getSurfaceSize(aSide);}
    public float getSurfaceDistance         (byte aSide) {return 0;}
    public boolean isSurfaceSolid           (byte aSide) {return isSurfaceOpaque(aSide);}
    public boolean isSurfaceOpaque          (byte aSide) {return true;}
    public boolean isSealable               (byte aSide) {return false;}
    public AxisAlignedBB getCollisionBoundingBoxFromPool() {return box();}
    public AxisAlignedBB getSelectedBoundingBoxFromPool () {if (FORCE_FULL_SELECTION_BOXES) return box(); return box(shrunkBox());}
    public void setBlockBoundsBasedOnState(Block aBlock) {if (FORCE_FULL_SELECTION_BOXES) box(aBlock); else box(aBlock, shrunkBox());}
    public boolean ignorePlayerCollisionWhenPlacing(ItemStack aStack, EntityPlayer aPlayer, World aWorld, int aX, int aY, int aZ, byte aSide, float aHitX, float aHitY, float aHitZ) {return ignorePlayerCollisionWhenPlacing();}
    public boolean ignorePlayerCollisionWhenPlacing() {return false;}
    public void onCoordinateChange() { /**/ }

    @Override
    public boolean prepareToMove() {
        return true;
    }

    @Override
    public void doneMoving() {
        onCoordinateChange();
    }

    //Fire and Burning, NAPALM!
    public int getFireSpreadSpeed(byte aSide, boolean aDefault) {
        return aDefault ? 150 : 0;
    }

    public int getFlammability(byte aSide, boolean aDefault) {
        return aDefault ? 150 : 0;
    }

    public void setOnFire() {
        if (BlockCanBurn) {
            GT_Utility.setCoordsOnFire(worldObj, getX(), getY(), getZ(), false);
        }
    }

    public boolean setToFire() {
        return worldObj.setBlock(xCoord, yCoord, zCoord, Blocks.fire, 0, 3);
    }

    public boolean setToAir () {
        return worldObj.setBlock(xCoord, yCoord, zCoord, Blocks.air , 0, 3);
    }

    // Useful to check if a Player or any other Entity is actually allowed to access or interact with this Block.

    @Override
    public boolean allowInteraction(Entity aEntity) {
        return true;
    }


    public boolean allowRightclick(Entity aEntity) {
        return allowInteraction(aEntity);
    }

    public float getPlayerRelativeBlockHardness(EntityPlayer aPlayer, float aOriginal) {
        return allowInteraction(aPlayer) ? Math.max(aOriginal, 0.0001F) : 0;
    }

    // Regarding Multiblocks. If true it will always send a machineblock update whenever something relevant changes, such as facing or connectivity.
    public boolean hasMultiBlockMachineRelevantData() {
        return false;
    }

    // Makes a Bounding Box without having to constantly specify the Offset Coordinates.
    /** Default Size Box. */
    public static final float[] PX_BOX = {0,0,0,1,1,1};

    public AxisAlignedBB box(double aMinX, double aMinY, double aMinZ, double aMaxX, double aMaxY, double aMaxZ) {return AxisAlignedBB.getBoundingBox(xCoord+aMinX, yCoord+aMinY, zCoord+aMinZ, xCoord+aMaxX, yCoord+aMaxY, zCoord+aMaxZ);}
    public AxisAlignedBB box(double[] aBox) {return AxisAlignedBB.getBoundingBox(xCoord+aBox[0], yCoord+aBox[1], zCoord+aBox[2], xCoord+aBox[3], yCoord+aBox[4], zCoord+aBox[5]);}
    public AxisAlignedBB box(float[] aBox) {return AxisAlignedBB.getBoundingBox(xCoord+aBox[0], yCoord+aBox[1], zCoord+aBox[2], xCoord+aBox[3], yCoord+aBox[4], zCoord+aBox[5]);}
    public AxisAlignedBB box() {return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord+1, yCoord+1, zCoord+1);}

    public boolean box(AxisAlignedBB aAABB, List<AxisAlignedBB> aList, double aMinX, double aMinY, double aMinZ, double aMaxX, double aMaxY, double aMaxZ) {
        AxisAlignedBB tBox = box(aMinX, aMinY, aMinZ, aMaxX, aMaxY, aMaxZ);
        return tBox.intersectsWith(aAABB) && aList.add(tBox);
    }

    public boolean box(AxisAlignedBB aAABB, List<AxisAlignedBB> aList, double[] aBox) {
        AxisAlignedBB tBox = box(aBox[0], aBox[1], aBox[2], aBox[3], aBox[4], aBox[5]);
        return tBox.intersectsWith(aAABB) && aList.add(tBox);
    }

    public boolean box(AxisAlignedBB aAABB, List<AxisAlignedBB> aList, float[] aBox) {
        AxisAlignedBB tBox = box(aBox[0], aBox[1], aBox[2], aBox[3], aBox[4], aBox[5]);
        return tBox.intersectsWith(aAABB) && aList.add(tBox);
    }

    public boolean box(AxisAlignedBB aAABB, List<AxisAlignedBB> aList) {
        AxisAlignedBB tBox = box(PX_BOX);
        return tBox.intersectsWith(aAABB) && aList.add(tBox);
    }

    public boolean box(AxisAlignedBB aBox, AxisAlignedBB aAABB, List<AxisAlignedBB> aList) {
        return aBox != null && aBox.intersectsWith(aAABB) && aList.add(aBox);
    }

    @SideOnly(Side.CLIENT)
    public boolean renderFullBlockSide(Block aBlock, RenderBlocks aRenderer, ForgeDirection aSide) {
        return shouldSideBeRendered(aSide);
    }

    public boolean box(Block aBlock) {
        aBlock.setBlockBounds(0,0,0,1,1,1);
        return true;
    }

    public boolean box(Block aBlock, double[] aBox) {
        aBlock.setBlockBounds((float)aBox[0], (float)aBox[1], (float)aBox[2], (float)aBox[3], (float)aBox[4], (float)aBox[5]);
        return true;
    }

    public boolean box(Block aBlock, float[] aBox) {
        aBlock.setBlockBounds(aBox[0], aBox[1], aBox[2], aBox[3], aBox[4], aBox[5]); return true;
    }

    public boolean box(Block aBlock, double aMinX, double aMinY, double aMinZ, double aMaxX, double aMaxY, double aMaxZ) {
        aBlock.setBlockBounds((float)aMinX, (float)aMinY, (float)aMinZ, (float)aMaxX, (float)aMaxY, (float)aMaxZ);
        return true;
    }

    public float[] shrunkBox() {
        return PX_BOX;

    }

    protected void loadTextures(@Nonnull String folder) {
        // Loading the registry
        for (SidedTextureNames textureName : SidedTextureNames.TEXTURES) {
            ITexture texture;
            try {
                Minecraft.getMinecraft()
                    .getResourceManager()
                    .getResource(
                        new ResourceLocation(
                            Mods.GregTech.ID,
                            "textures/blocks/multitileentity/" + folder + "/" + textureName.getName() + ".png"));
                texture = (ITexture) TextureFactory.of(new CustomIcon("multitileentity/" + folder + "/" + textureName.getName()));
            } catch (IOException ignored) {
                texture = (ITexture) TextureFactory.of(Textures.BlockIcons.VOID);
            }
            switch (textureName) {
                case Top -> topOverlayTexture = texture;
                case Bottom -> bottomOverlayTexture = texture;
                case Back -> backOverlayTexture = texture;
                case Front -> frontOverlayTexture = texture;
                case Left -> leftOverlayTexture = texture;
                case Right -> rightOverlayTexture = texture;
                case Base -> baseTexture = texture;
            }
        }
    }

    protected void copyTextures() {
        // Loading an instance
        MultiTileEntity canonicalEntity = MultiTileEntityRegistry.getRegistry(registryId)
            .getCachedTileEntity(metaId);
        baseTexture = canonicalEntity.baseTexture;
        topOverlayTexture = canonicalEntity.topOverlayTexture;
        bottomOverlayTexture = canonicalEntity.bottomOverlayTexture;
        leftOverlayTexture = canonicalEntity.leftOverlayTexture;
        rightOverlayTexture = canonicalEntity.rightOverlayTexture;
        backOverlayTexture = canonicalEntity.backOverlayTexture;
        frontOverlayTexture = canonicalEntity.frontOverlayTexture;
    }

    @Override
    public void initFromNBT(@Nonnull final NBTTagCompound nbt, final int registryId, final int metaId) {
        if (this.registryId == registryId && this.metaId == metaId) {
            return;
        }

        this.registryId = registryId;
        this.metaId = metaId;
        if (nbt != null) readFromNBT(nbt);
    }

    @Override
    public void addToolTip(@Nonnull final List<String> toolTips) {}

    @Override
    public final boolean onBlockActivated(EntityPlayer player, ForgeDirection side, float subX, float subY,
        float subZ) {
        final ItemStack heldItem = player.getHeldItem();
        final ForgeDirection wrenchSide = GT_Utility.determineWrenchingSide(side, subX, subY, subZ);
        if (heldItem == null) return onRightClick(player, side, wrenchSide);

        if (heldItem.getItem() instanceof ItemBlock) return false;

        if (heldItem.getItem() instanceof GT_MetaGenerated_Tool toolItem) {
            IToolStats tool = toolItem.getToolStats(heldItem);
            if (tool instanceof GT_Tool_Wrench) return onRightClickWithWrench(player, side, wrenchSide);
            if (tool instanceof GT_Tool_HardHammer) return onRightClickWithHammer(player, side, wrenchSide);
            if (tool instanceof GT_Tool_SoftHammer) return onRightClickWithMallet(player, side, wrenchSide);
            if (tool instanceof GT_Tool_Screwdriver) return onRightClickWithScrewdriver(player, side, wrenchSide);
            if (tool instanceof GT_Tool_Soldering_Iron) return onRightClickWithSolderinIron(player, side, wrenchSide);
            if (tool instanceof GT_Tool_WireCutter) return onRightClickWithWireCutters(player, side, wrenchSide);
            if (tool instanceof GT_Tool_Crowbar) return onRightClickWithCrowbar(player, side, wrenchSide);
        }

        return onRightClick(player, side, wrenchSide);
    }

    protected boolean onRightClick(EntityPlayer player, ForgeDirection side, ForgeDirection wrenchSide) {
        return false;
    }

    protected boolean onRightClickWithHammer(EntityPlayer player, ForgeDirection side, ForgeDirection wrenchSide) {
        return false;
    }

    protected boolean onRightClickWithMallet(EntityPlayer player, ForgeDirection side, ForgeDirection wrenchSide) {
        return false;
    }

    protected boolean onRightClickWithWrench(EntityPlayer player, ForgeDirection side, ForgeDirection wrenchSide) {
        if (player.isSneaking()) {
            setFacing(wrenchSide);
            sendGraphicPacket();
            markDirty();
            return true;
        }
        return false;
    }

    protected boolean onRightClickWithScrewdriver(EntityPlayer player, ForgeDirection side, ForgeDirection wrenchSide) {
        return false;
    }

    protected boolean onRightClickWithWireCutters(EntityPlayer player, ForgeDirection side, ForgeDirection wrenchSide) {
        return false;
    }

    protected boolean onRightClickWithSolderinIron(EntityPlayer player, ForgeDirection side,
        ForgeDirection wrenchSide) {
        return false;
    }

    protected boolean onRightClickWithCrowbar(EntityPlayer player, ForgeDirection side, ForgeDirection wrenchSide) {
        return false;
    }

    // MultiTileBasicRender methods
    @Override
    @Nonnull
    public final ITexture getTexture(@Nonnull ForgeDirection side) {
        if (getFacing() == side) {
            return getFrontTexture();
        }

        if (getFacing().getOpposite() == side) {
            return getBackTexture();
        }

        if (getFacing().getRotation(getFacing().getRotation(ForgeDirection.UP)) == side) {
            return getTopTexture();
        }

        if (getFacing().getRotation(getFacing().getRotation(ForgeDirection.DOWN)) == side) {
            return getBottomTexture();
        }

        if (getFacing().getRotation(getFacing().getRotation(ForgeDirection.EAST)) == side) {
            return getRightTexture();
        }

        if (getFacing().getRotation(getFacing().getRotation(ForgeDirection.WEST)) == side) {
            return getLeftTexture();
        }

        return baseTexture;
    }

    @Nonnull
    protected ITexture getFrontTexture() {
        return TextureFactory.of(baseTexture, frontOverlayTexture);
    }

    @Nonnull
    protected ITexture getBackTexture() {
        return TextureFactory.of(baseTexture, backOverlayTexture);
    }

    @Nonnull
    protected ITexture getTopTexture() {
        return TextureFactory.of(baseTexture, topOverlayTexture);
    }

    @Nonnull
    protected ITexture getBottomTexture() {
        return TextureFactory.of(baseTexture, bottomOverlayTexture);
    }

    @Nonnull
    protected ITexture getRightTexture() {
        return TextureFactory.of(baseTexture, rightOverlayTexture);
    }

    @Nonnull
    protected ITexture getLeftTexture() {
        return TextureFactory.of(baseTexture, leftOverlayTexture);
    }

    // SyncedMultiTileEntity methods
    @Override
    public void getFullPacketData(GT_Packet_MultiTileEntity packet) {
        packet.addData(new CoordinateData(getX(), getY(), getZ()));
        packet.addData(new CommonData(facing));
    }

    @Override
    public void getTimedPacketData(GT_Packet_MultiTileEntity packet) {
        packet.addData(new CoordinateData(getX(), getY(), getZ()));
        packet.addData(new CommonData(facing));
    }

    @Override
    public void getGraphicPacketData(GT_Packet_MultiTileEntity packet) {
        packet.addData(new CoordinateData(getX(), getY(), getZ()));
        packet.addData(new CommonData(facing));
    }

    @Override
    public void sendFullPacket(@Nonnull EntityPlayerMP player) {
        if (!isServerSide()) return;
        fullPacket.clearData();
        getFullPacketData(fullPacket);
        GT_Values.NW.sendToPlayer(fullPacket, player);
    }

    @Override
    public void sendTimedPacket() {
        if (!isServerSide()) return;
        timedPacket.clearData();
        getTimedPacketData(timedPacket);
        GT_Values.NW.sendPacketToAllPlayersInRange(getWorldObj(), timedPacket, getX(), getZ());
    }

    @Override
    public void sendGraphicPacket() {
        if (!isServerSide()) return;
        graphicPacket.clearData();
        getGraphicPacketData(graphicPacket);
        GT_Values.NW.sendPacketToAllPlayersInRange(getWorldObj(), graphicPacket, getX(), getZ());
    }

    // Helper classes/enums
    protected enum SidedTextureNames {

        Base("base"),
        Left("left"),
        Right("right"),
        Top("top"),
        Bottom("bottom"),
        Back("back"),
        Front("front");

        private final String name;
        public static final SidedTextureNames[] TEXTURES = { Base, Left, Right, Top, Bottom, Back, Front };

        SidedTextureNames(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    protected enum StatusTextures {

        Active("active", false),
        ActiveWithGlow("active_glow", true),
        Inactive("inactive", false),
        InactiveWithGlow("inactive_glow", true);

        private final String name;
        private final boolean hasGlow;
        public static final StatusTextures[] TEXTURES = { Active, ActiveWithGlow, Inactive, InactiveWithGlow };

        StatusTextures(String name, boolean hasGlow) {
            this.name = name;
            this.hasGlow = hasGlow;
        }

        public String getName() {
            return name;
        }

        public boolean hasGlow() {
            return hasGlow;
        }
    }

    // Error things

    public String ERROR_MESSAGE = null;
    public void setError(String aError) {
        ERROR_MESSAGE = aError;
        GT_FML_LOGGER.error(ERROR_MESSAGE);
    }
}
