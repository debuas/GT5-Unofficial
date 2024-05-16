package gregtech.api.multitileentity.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.interfaces.tileentity.IHasWorldObjectAndCoords;
import gregtech.api.multitileentity.MultiTileEntityRegistry;
import gregtech.api.multitileentity.code.ArrayListNoNulls;
import net.minecraft.block.Block;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

/*
 * Heavily inspired by GT6
 */
public interface IMultiTileEntity {

    int getMetaId();

    int getRegistryId();

    void sendBlockEvent(byte aID, byte aValue);
    void onAdjacentBlockChange(int aTileX, int aTileY, int aTileZ);

    /** Called after the TileEntity has been placed and set up. */
    void onTileEntityPlaced();

    boolean allowInteraction(Entity aEntity);

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

    default void setOwner(EntityPlayer player) {}

    /** If this Side is Opaque, like a facing of a full and non-transparent Block. This is also used Serverside! */
    public boolean isSurfaceOpaque(byte aSide);


    /** Hooks into the Block Class. Implement them in order to overwrite the Default Behaviours.*/
    public static interface IMTE_GetBlocksMovement extends IMultiTileEntity {public boolean getBlocksMovement();}
    public static interface IMTE_AddCollisionBoxesToList extends IMultiTileEntity {public void addCollisionBoxesToList(AxisAlignedBB aAABB, List<AxisAlignedBB> aList, Entity aEntity);}
    public static interface IMTE_GetCollisionBoundingBoxFromPool extends IMultiTileEntity {public AxisAlignedBB getCollisionBoundingBoxFromPool();}
    public static interface IMTE_GetSelectedBoundingBoxFromPool extends IMultiTileEntity {public AxisAlignedBB getSelectedBoundingBoxFromPool();}
    public static interface IMTE_UpdateTick extends IMultiTileEntity {public void updateTick(Random aRandom);}
    public static interface IMTE_OnBlockDestroyedByPlayer extends IMultiTileEntity {public void onBlockDestroyedByPlayer(int aRandom);}
    public static interface IMTE_OnBlockAdded extends IMultiTileEntity {public void onBlockAdded();}
    public static interface IMTE_DropXpOnBlockBreak extends IMultiTileEntity {public void dropXpOnBlockBreak(int aXP);}
    public static interface IMTE_CollisionRayTrace extends IMultiTileEntity {public MovingObjectPosition collisionRayTrace(Vec3 aVectorA, Vec3 aVectorB);}
    public static interface IMTE_OnBlockActivated extends IMultiTileEntity {public boolean onBlockActivated(EntityPlayer aPlayer, ForgeDirection aSide, float aHitX, float aHitY, float aHitZ);}
    public static interface IMTE_OnEntityWalking extends IMultiTileEntity {public void onEntityWalking(Entity aEntity);}
    public static interface IMTE_OnBlockClicked extends IMultiTileEntity {public void onBlockClicked(EntityPlayer aPlayer);}
    public static interface IMTE_VelocityToAddToEntity extends IMultiTileEntity {public void velocityToAddToEntity(Entity aEntity, Vec3 aVector);}
    /** Remember that it passes the opposite Side due to the way vanilla works! */
    public static interface IMTE_IsProvidingWeakPower extends IMultiTileEntity {public int isProvidingWeakPower(ForgeDirection aOppositeSide);}
    public static interface IMTE_IsProvidingStrongPower extends IMultiTileEntity {public int isProvidingStrongPower(ForgeDirection aOppositeSide);}
    public static interface IMTE_OnEntityCollidedWithBlock extends IMultiTileEntity { public void onEntityCollidedWithBlock(Entity aEntity);}
    public static interface IMTE_CanBlockStay extends IMultiTileEntity {public boolean canBlockStay();}
    public static interface IMTE_OnFallenUpon extends IMultiTileEntity {public void onFallenUpon(Entity aEntity, float aFallDistance);}
    public static interface IMTE_OnBlockHarvested extends IMultiTileEntity {public void onBlockHarvested(int aMetaData, EntityPlayer aPlayer);}
    public static interface IMTE_OnBlockPreDestroy extends IMultiTileEntity {public void onBlockPreDestroy(int aMetaData);}
    public static interface IMTE_FillWithRain extends IMultiTileEntity {public void fillWithRain();}
    public static interface IMTE_GetComparatorInputOverride extends IMultiTileEntity {public int getComparatorInputOverride(ForgeDirection aSide);}
    public static interface IMTE_GetLightValue extends IMultiTileEntity {public int getLightValue();}
    public static interface IMTE_IsLadder extends IMultiTileEntity {public boolean isLadder(EntityLivingBase aEntity);}
    public static interface IMTE_IsNormalCube extends IMultiTileEntity {public boolean isNormalCube();}
    public static interface IMTE_IsReplaceable extends IMultiTileEntity {public boolean isReplaceable();}
    public static interface IMTE_IsBurning extends IMultiTileEntity {public boolean isBurning();}
    public static interface IMTE_IsAir extends IMultiTileEntity {public boolean isAir();}
    public static interface IMTE_RemovedByPlayer extends IMultiTileEntity {public boolean removedByPlayer(World aWorld, EntityPlayer aPlayer, boolean aWillHarvest);}
    public static interface IMTE_CanCreatureSpawn extends IMultiTileEntity {public boolean canCreatureSpawn(EnumCreatureType aType);}
    public static interface IMTE_BeginLeavesDecay extends IMultiTileEntity {public void beginLeavesDecay();}
    public static interface IMTE_CanSustainLeaves extends IMultiTileEntity {public boolean canSustainLeaves();}
    public static interface IMTE_IsLeaves extends IMultiTileEntity {public boolean isLeaves();}
    public static interface IMTE_CanBeReplacedByLeaves extends IMultiTileEntity {public boolean canBeReplacedByLeaves();}
    public static interface IMTE_IsWood extends IMultiTileEntity {public boolean isWood();}
    public static interface IMTE_IsReplaceableOreGen extends IMultiTileEntity {public boolean isReplaceableOreGen(Block aTarget);}
    public static interface IMTE_CanConnectRedstone extends IMultiTileEntity {public boolean canConnectRedstone(ForgeDirection aSide);}
    public static interface IMTE_CanPlaceTorchOnTop extends IMultiTileEntity {public boolean canPlaceTorchOnTop();}
    public static interface IMTE_IsFoliage extends IMultiTileEntity {public boolean isFoliage();}
    public static interface IMTE_CanSustainPlant extends IMultiTileEntity {public boolean canSustainPlant(ForgeDirection aSide, IPlantable aPlantable);}
    public static interface IMTE_OnPlantGrow extends IMultiTileEntity {public void onPlantGrow(int sX, int sY, int sZ);}
    public static interface IMTE_IsFertile extends IMultiTileEntity {public boolean isFertile();}
    public static interface IMTE_RotateBlock extends IMultiTileEntity {public boolean rotateBlock(ForgeDirection aSide);}
    public static interface IMTE_GetValidRotations extends IMultiTileEntity {public ForgeDirection[] getValidRotations();}
    public static interface IMTE_GetEnchantPowerBonus extends IMultiTileEntity {public float getEnchantPowerBonus();}
    public static interface IMTE_RecolourBlock extends IMultiTileEntity {public boolean recolourBlock(ForgeDirection aSide, byte aColor);}
    public static interface IMTE_ShouldCheckWeakPower extends IMultiTileEntity {public boolean shouldCheckWeakPower(ForgeDirection aSide);}
    public static interface IMTE_GetWeakChanges extends IMultiTileEntity {public boolean getWeakChanges();}
    public static interface IMTE_AddHitEffects extends IMultiTileEntity {@SideOnly(Side.CLIENT) public boolean addHitEffects(World aWorld, MovingObjectPosition aTarget, EffectRenderer aRenderer);}
    public static interface IMTE_AddDestroyEffects extends IMultiTileEntity {@SideOnly(Side.CLIENT) public boolean addDestroyEffects(int aMetaData, EffectRenderer aRenderer);}
    public static interface IMTE_ShouldSideBeRendered extends IMultiTileEntity {public boolean shouldSideBeRendered(ForgeDirection aSide);}
    public static interface IMTE_SetBlockBoundsBasedOnState extends IMultiTileEntity {public void setBlockBoundsBasedOnState(Block aBlock);}
    public static interface IMTE_RandomDisplayTick extends IMultiTileEntity {public void randomDisplayTick(Random aRandom);}
    public static interface IMTE_OnBlockExploded extends IMultiTileEntity {public void onExploded(Explosion aExplosion);}
    public static interface IMTE_GetStackFromBlock extends IMultiTileEntity {public ItemStack getStackFromBlock(byte aSide);}
    public static interface IMTE_GetFlammability extends IMultiTileEntity {public int getFlammability(ForgeDirection aSide, boolean aDefault);}
    public static interface IMTE_GetFireSpreadSpeed extends IMultiTileEntity {public int getFireSpreadSpeed(ForgeDirection aSide, boolean aDefault);}
    public static interface IMTE_IsFireSource extends IMultiTileEntity {public boolean isFireSource(ForgeDirection aSide);}
    public static interface IMTE_CanEntityDestroy extends IMultiTileEntity {public boolean canEntityDestroy(Entity aEntity);}
 public static interface IMTE_GetDrops extends IMultiTileEntity {public ArrayListNoNulls<ItemStack> getDrops(int aFortune, boolean aSilkTouch);}
    public static interface IMTE_IsSideSolid extends IMultiTileEntity {public boolean isSideSolid(byte aSide);}
    public static interface IMTE_IsBeaconBase extends IMultiTileEntity {public boolean isBeaconBase(int aBeaconX, int aBeaconY, int aBeaconZ);}
    public static interface IMTE_GetLightOpacity extends IMultiTileEntity {public int getLightOpacity();}
    public static interface IMTE_GetPlayerRelativeBlockHardness extends IMultiTileEntity {public float getPlayerRelativeBlockHardness(EntityPlayer aPlayer, float aOriginal);}
    public static interface IMTE_GetBlockHardness extends IMultiTileEntity {public float getBlockHardness();}
    public static interface IMTE_GetExplosionResistance extends IMultiTileEntity {public float getExplosionResistance(Entity aExploder, double aExplosionX, double aExplosionY, double aExplosionZ); public float getExplosionResistance();}
    public static interface IMTE_OnNeighborChange extends IMultiTileEntity {public void onNeighborChange(IBlockAccess aWorld, int aTileX, int aTileY, int aTileZ);}
    public static interface IMTE_OnNeighborBlockChange extends IMultiTileEntity {public void onNeighborBlockChange(World aWorld, Block aBlock);}
    public static interface IMTE_OnWalkOver extends IMultiTileEntity {public void onWalkOver(EntityLivingBase aEntity);}
    public static interface IMTE_OnPainting extends IMultiTileEntity {public boolean onPainting(ForgeDirection aSide, int aRGB);}
    public static interface IMTE_IsSealable extends IMultiTileEntity {public boolean isSealable(ForgeDirection aSide);}
    public static interface IMTE_OnOxygenRemoved extends IMultiTileEntity {public void onOxygenRemoved();}
    public static interface IMTE_OnOxygenAdded extends IMultiTileEntity {public void onOxygenAdded();}
//End of Block Functions

    public static interface IMTE_HasMultiBlockMachineRelevantData extends IMultiTileEntity {
        /** Return true to mark this Block as a Machine Block for Multiblocks. */
        public boolean hasMultiBlockMachineRelevantData();
    }

    //Registration Interfaces
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
