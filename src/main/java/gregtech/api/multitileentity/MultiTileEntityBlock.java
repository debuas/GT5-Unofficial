package gregtech.api.multitileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import gregtech.api.GregTech_API;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IBlockOnWalkOver;
import gregtech.api.interfaces.ITexture;
import gregtech.api.multitileentity.code.ArrayListNoNulls;
import gregtech.api.multitileentity.interfaces.IMultiBlock;
import gregtech.api.multitileentity.interfaces.IMultiBlockRetrievable;
import gregtech.api.util.GT_Util;
import gregtech.common.render.IRenderedBlock;
import gregtech.common.render.IRenderedBlockObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

import com.cricketcraft.chisel.api.IFacade;

import cpw.mods.fml.common.Optional;
import gregtech.api.metatileentity.CoverableTileEntity;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_GetBlocksMovement;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_AddCollisionBoxesToList;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_GetCollisionBoundingBoxFromPool;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_GetSelectedBoundingBoxFromPool;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_UpdateTick;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_OnBlockDestroyedByPlayer;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_OnBlockAdded;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_DropXpOnBlockBreak;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_CollisionRayTrace;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_OnBlockActivated;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_OnEntityWalking;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_OnBlockClicked;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_VelocityToAddToEntity;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_IsProvidingWeakPower;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_IsProvidingStrongPower;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_OnEntityCollidedWithBlock;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_CanBlockStay;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_OnFallenUpon;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_OnBlockHarvested;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_OnBlockPreDestroy;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_FillWithRain;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_GetComparatorInputOverride;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_GetLightValue;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_IsLadder;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_IsNormalCube;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_IsReplaceable;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_IsBurning;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_IsAir;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_RemovedByPlayer;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_CanCreatureSpawn;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_BeginLeavesDecay;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_CanSustainLeaves;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_IsLeaves;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_CanBeReplacedByLeaves;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_IsWood;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_IsReplaceableOreGen;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_CanConnectRedstone;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_CanPlaceTorchOnTop;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_IsFoliage;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_CanSustainPlant;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_OnPlantGrow;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_IsFertile;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_RotateBlock;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_GetValidRotations;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_GetEnchantPowerBonus;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_RecolourBlock;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_ShouldCheckWeakPower;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_GetWeakChanges;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_AddHitEffects;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_AddDestroyEffects;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_ShouldSideBeRendered;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_SetBlockBoundsBasedOnState;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_RandomDisplayTick;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_OnBlockExploded;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_GetStackFromBlock;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_GetFlammability;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_GetFireSpreadSpeed;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_IsFireSource;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_CanEntityDestroy;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_GetDrops;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_IsSideSolid;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_IsBeaconBase;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_GetLightOpacity;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_GetPlayerRelativeBlockHardness;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_GetBlockHardness;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_GetExplosionResistance;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_OnNeighborChange;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_OnNeighborBlockChange;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_OnWalkOver;

import gregtech.common.covers.CoverInfo;
import gregtech.common.render.GT_MultiTile_Renderer;
import net.minecraftforge.event.ForgeEventFactory;

import static gregtech.api.enums.GT_Values.OFFX;
import static gregtech.api.enums.GT_Values.OFFY;
import static gregtech.api.enums.GT_Values.OFFZ;
import static gregtech.api.enums.GT_Values.OPOS;
import static gregtech.api.enums.GT_Values.RNGSUS;
import static gregtech.api.enums.GT_Values.SIDE_TOP;
import static gregtech.api.enums.ItemList.TC_Thaumometer;
import static gregtech.api.util.GT_Util.LAST_BROKEN_TILEENTITY;

/*
 * MultiTileEntityBlock ported from GT6
 */
@Optional.Interface(iface = "com.cricketcraft.chisel.api.IFacade", modid = "ChiselAPI")
public class MultiTileEntityBlock extends BlockContainer implements IMultiBlock, IBlockOnWalkOver, IMultiBlockRetrievable, IFacade, IRenderedBlock {
    private static final Map<String, MultiTileEntityBlock> MULTITILEENTITYBLOCKMAP = new HashMap<>();
    private int mHarvestLevel, mHarvestLevelMinimum, mHarvestLevelMaximum;
    private boolean mOpaque, mNormalCube;


    private String toolName;
    private MultiTileEntityRegistry registry;

    public MultiTileEntityBlock setHarvestLevel(final int aHarvestLevel, final int aHarvestLevelMinimum, final int aHarvestLevelMaximum) {
        this.mHarvestLevel = aHarvestLevel;
        this.mHarvestLevelMinimum = aHarvestLevelMinimum;
        this.mHarvestLevelMaximum = aHarvestLevelMaximum;
        return this;
    }

    public MultiTileEntityBlock setCubeOpacity(final boolean cubeOpacity) {
        this.mOpaque = cubeOpacity;
        return this;
    }

    public MultiTileEntityBlock setNormalCube(final boolean aNormalCube) {
        this.mNormalCube = aNormalCube;
        return this;
    }

    public MultiTileEntityBlock setLightOpacity(final int lightOpacity) {
        this.lightOpacity = isOpaqueCube() ? 255 : 0;
        return this;
    }

    public MultiTileEntityBlock() {
        super(Material.anvil);
    }

    public MultiTileEntityRegistry getRegistry() {
        return registry;
    }

    public MultiTileEntityBlock setRegistry(final MultiTileEntityRegistry registry) {
        this.registry = registry;
        return this;
    }

    public MultiTileEntityBlock setTool(final String toolName) {
        this.toolName = toolName;
        return this;
    }

    @Override
    public Block getFacade(final IBlockAccess aWorld, final int aX, final int aY, final int aZ, final int ordinalSide) {
        final TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (tTileEntity instanceof final CoverableTileEntity tile) {
            final ForgeDirection side = ForgeDirection.getOrientation(ordinalSide);
            if (ordinalSide != -1) {
                final Block facadeBlock = tile.getCoverInfoAtSide(side)
                .getFacadeBlock();
                if (facadeBlock != null) return facadeBlock;
            } else {
                // we do not allow more than one type of facade per block, so no need to check every side
                // see comment in gregtech.common.covers.GT_Cover_FacadeBase.isCoverPlaceable
                for (final ForgeDirection tSide : ForgeDirection.VALID_DIRECTIONS) {
                    final Block facadeBlock = tile.getCoverInfoAtSide(tSide)
                    .getFacadeBlock();
                    if (facadeBlock != null) {
                        return facadeBlock;
                    }
                }
            }
        }
        return Blocks.air;
    }

    @Override
    public int getFacadeMetadata(final IBlockAccess aWorld, final int aX, final int aY, final int aZ, final int ordinalSide) {
        final TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (tTileEntity instanceof final CoverableTileEntity tile) {
            final ForgeDirection side = ForgeDirection.getOrientation(ordinalSide);
            if (ordinalSide != -1) {
                final CoverInfo coverInfo = tile.getCoverInfoAtSide(side);
                final Block facadeBlock = coverInfo.getFacadeBlock();
                if (facadeBlock != null) return coverInfo.getFacadeMeta();
            } else {
                // we do not allow more than one type of facade per block, so no need to check every side
                // see comment in gregtech.common.covers.GT_Cover_FacadeBase.isCoverPlaceable
                for (final ForgeDirection tSide : ForgeDirection.VALID_DIRECTIONS) {
                    final CoverInfo coverInfo = tile.getCoverInfoAtSide(tSide);
                    final Block facadeBlock = coverInfo.getFacadeBlock();
                    if (facadeBlock != null) {
                        return coverInfo.getFacadeMeta();
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        final TileEntity te = worldIn.getTileEntity(x, y, z);
        if (te != null) LAST_BROKEN_TILEENTITY.set(te);
        if (te == null || !te.shouldRefresh(this, blockBroken, meta, meta, worldIn, x, y, z)) return;
        if (te instanceof IMultiTileEntity mute) {mute.onBlockBroken();}
        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }

    private static boolean LOCK = false;

    @Override
    public final boolean getBlocksMovement(IBlockAccess aWorld, int aX, int aY, int aZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return !(aTileEntity instanceof IMTE_GetBlocksMovement) || ((IMTE_GetBlocksMovement) aTileEntity).getBlocksMovement();
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void addCollisionBoxesToList(World aWorld, int aX, int aY, int aZ, AxisAlignedBB aAABB, @SuppressWarnings("rawtypes") List aList, Entity aEntity) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (aTileEntity instanceof IMTE_AddCollisionBoxesToList)
            ((IMTE_AddCollisionBoxesToList)aTileEntity).addCollisionBoxesToList(aAABB, aList, aEntity);
        else if (aTileEntity != null)
            super.addCollisionBoxesToList(aWorld, aX, aY, aZ, aAABB, aList, aEntity);
    }

    @Override
    public final AxisAlignedBB getCollisionBoundingBoxFromPool(World aWorld, int aX, int aY, int aZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_GetCollisionBoundingBoxFromPool ?
            ((IMTE_GetCollisionBoundingBoxFromPool)aTileEntity).getCollisionBoundingBoxFromPool() :
            aTileEntity == null ? null : AxisAlignedBB.getBoundingBox(aX, aY, aZ, aX+1, aY+1, aZ+1);
    }

    @Override
    public final void updateTick(World aWorld, int aX, int aY, int aZ, Random aRandom) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (aTileEntity instanceof IMTE_UpdateTick) ((IMTE_UpdateTick)aTileEntity).updateTick(aRandom);
    }

    @Override
    public final void onBlockDestroyedByPlayer(World aWorld, int aX, int aY, int aZ, int aRandom) {
        TileEntity aTileEntity = GT_Util.getTileEntity(aWorld, aX, aY, aZ, true);
        if (aTileEntity instanceof IMTE_OnBlockDestroyedByPlayer)
            ((IMTE_OnBlockDestroyedByPlayer)aTileEntity).onBlockDestroyedByPlayer(aRandom);
    }

    @Override
    public final void onBlockAdded(World aWorld, int aX, int aY, int aZ) {
        super.onBlockAdded(aWorld, aX, aY, aZ);
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (!(aTileEntity instanceof IMTE_OnBlockAdded aMultiTileEntity)) return;
        aMultiTileEntity.onBlockAdded();
    }

    @Override public final void dropXpOnBlockBreak(World aWorld, int aX, int aY, int aZ, int aXP) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (aTileEntity instanceof IMTE_DropXpOnBlockBreak) ((IMTE_DropXpOnBlockBreak)aTileEntity).dropXpOnBlockBreak(aXP);
        else super.dropXpOnBlockBreak(aWorld, aX, aY, aZ, aXP);
    }

    @Override
    public final MovingObjectPosition collisionRayTrace(World aWorld, int aX, int aY, int aZ, Vec3 aVectorA, Vec3 aVectorB) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_CollisionRayTrace ?
            ((IMTE_CollisionRayTrace)aTileEntity).collisionRayTrace(aVectorA, aVectorB) :
            super.collisionRayTrace(aWorld, aX, aY, aZ, aVectorA, aVectorB);
    }

    @Override
    public boolean onBlockActivated(World aWorld, int aX, int aY, int aZ, EntityPlayer aPlayer, int aSide, float aHitX, float aHitY, float aHitZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (aPlayer != null &&  TC_Thaumometer.isStackEqual(aPlayer.getHeldItem(), true, true)) return false;

        if (!(aTileEntity instanceof IMTE_OnBlockActivated aMultiTileEntity)) return false;

        return aMultiTileEntity.onBlockActivated(aPlayer, ForgeDirection.getOrientation(aSide), aHitX, aHitY, aHitZ);
    }

    @Override
    public final void onEntityWalking(World aWorld, int aX, int aY, int aZ, Entity aEntity) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (aTileEntity instanceof IMTE_OnEntityWalking) ((IMTE_OnEntityWalking)aTileEntity).onEntityWalking(aEntity);
    }

    @Override
    public final void onBlockClicked(World aWorld, int aX, int aY, int aZ, EntityPlayer aPlayer) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (aTileEntity instanceof IMTE_OnBlockClicked) ((IMTE_OnBlockClicked)aTileEntity).onBlockClicked(aPlayer);
        else super.onBlockClicked(aWorld, aX, aY, aZ, aPlayer);
    }

    @Override
    public final void velocityToAddToEntity(World aWorld, int aX, int aY, int aZ, Entity aEntity, Vec3 aVector) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (aTileEntity instanceof IMTE_VelocityToAddToEntity) ((IMTE_VelocityToAddToEntity)aTileEntity).velocityToAddToEntity(aEntity, aVector);
        else super.velocityToAddToEntity(aWorld, aX, aY, aZ, aEntity, aVector);
    }

    @Override
    public final int isProvidingWeakPower(IBlockAccess aWorld, int aX, int aY, int aZ, int aSide) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_IsProvidingWeakPower ?
            ((IMTE_IsProvidingWeakPower)aTileEntity).isProvidingWeakPower(ForgeDirection.getOrientation(aSide)) :
            super.isProvidingWeakPower(aWorld, aX, aY, aZ, aSide);
    }

    @Override
    public final int isProvidingStrongPower(IBlockAccess aWorld, int aX, int aY, int aZ, int aSide) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_IsProvidingStrongPower ?
            ((IMTE_IsProvidingStrongPower)aTileEntity).isProvidingStrongPower(ForgeDirection.getOrientation(aSide)) :
            super.isProvidingStrongPower(aWorld, aX, aY, aZ, aSide);
    }

    @Override
    public final void onEntityCollidedWithBlock(World aWorld, int aX, int aY, int aZ, Entity aEntity) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (aTileEntity instanceof IMTE_OnEntityCollidedWithBlock)
            ((IMTE_OnEntityCollidedWithBlock)aTileEntity).onEntityCollidedWithBlock(aEntity);
        else super.onEntityCollidedWithBlock(aWorld, aX, aY, aZ, aEntity);
    }

    @Override
    public final boolean canBlockStay(World aWorld, int aX, int aY, int aZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return !(aTileEntity instanceof IMTE_CanBlockStay) ||
            ((IMTE_CanBlockStay)aTileEntity).canBlockStay();
    }

    @Override
    public final void onFallenUpon(World aWorld, int aX, int aY, int aZ, Entity aEntity, float aFallDistance) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (aTileEntity instanceof IMTE_OnFallenUpon) ((IMTE_OnFallenUpon)aTileEntity).onFallenUpon(aEntity, aFallDistance);
        else super.onFallenUpon(aWorld, aX, aY, aZ, aEntity, aFallDistance);
    }

    @Override
    public final void onBlockHarvested(World aWorld, int aX, int aY, int aZ, int aMetaData, EntityPlayer aPlayer) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (aTileEntity instanceof IMTE_OnBlockHarvested) ((IMTE_OnBlockHarvested)aTileEntity).onBlockHarvested(aMetaData, aPlayer);
        else super.onBlockHarvested(aWorld, aX, aY, aZ, aMetaData, aPlayer);
    }

    @Override
    public final void onBlockPreDestroy(World aWorld, int aX, int aY, int aZ, int aMetaData) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (aTileEntity instanceof IMTE_OnBlockPreDestroy)
            ((IMTE_OnBlockPreDestroy)aTileEntity).onBlockPreDestroy(aMetaData);
        else super.onBlockPreDestroy(aWorld, aX, aY, aZ, aMetaData);
    }

    @Override
    public final void fillWithRain(World aWorld, int aX, int aY, int aZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (aTileEntity instanceof IMTE_FillWithRain) ((IMTE_FillWithRain)aTileEntity).fillWithRain();
        else super.fillWithRain(aWorld, aX, aY, aZ);
    }

    @Override
    public final boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public final int getComparatorInputOverride(World aWorld, int aX, int aY, int aZ, int aSide) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_GetComparatorInputOverride ?
            ((IMTE_GetComparatorInputOverride)aTileEntity).getComparatorInputOverride(ForgeDirection.getOrientation(aSide)) :
            aTileEntity instanceof IMTE_IsProvidingWeakPower ?
                ((IMTE_IsProvidingWeakPower)aTileEntity).isProvidingWeakPower(ForgeDirection.getOrientation(OPOS[aSide])) :
                super.getComparatorInputOverride(aWorld, aX, aY, aZ, aSide);
    }

    @Override
    public final int getLightValue(IBlockAccess aWorld, int aX, int aY, int aZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ); return aTileEntity instanceof IMTE_GetLightValue ?
            ((IMTE_GetLightValue)aTileEntity).getLightValue() : super.getLightValue(aWorld, aX, aY, aZ);
    }

    @Override
    public final boolean isLadder(IBlockAccess aWorld, int aX, int aY, int aZ, EntityLivingBase aEntity) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_IsLadder && ((IMTE_IsLadder)aTileEntity).isLadder(aEntity);
    }

    @Override
    public final boolean isNormalCube(IBlockAccess aWorld, int aX, int aY, int aZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_IsNormalCube ?
            ((IMTE_IsNormalCube)aTileEntity).isNormalCube() : mNormalCube;
    }

    @Override
    public final boolean isReplaceable(IBlockAccess aWorld, int aX, int aY, int aZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_IsReplaceable ?
            ((IMTE_IsReplaceable)aTileEntity).isReplaceable() : blockMaterial.isReplaceable();
    }

    @Override
    public final boolean isBurning(IBlockAccess aWorld, int aX, int aY, int aZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_IsBurning && ((IMTE_IsBurning)aTileEntity).isBurning();
    }

    @Override
    public final boolean isAir(IBlockAccess aWorld, int aX, int aY, int aZ) {
        if (aWorld == null) return false; TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_IsAir && ((IMTE_IsAir)aTileEntity).isAir();
    }

    @Override
    public final boolean removedByPlayer(World aWorld, EntityPlayer aPlayer, int aX, int aY, int aZ, boolean aWillHarvest) {
        TileEntity aTileEntity = GT_Util.getTileEntity(aWorld, aX, aY, aZ, true);
        if (aTileEntity != null) LAST_BROKEN_TILEENTITY.set(aTileEntity);
        return aTileEntity instanceof IMTE_RemovedByPlayer ?
            ((IMTE_RemovedByPlayer)aTileEntity).removedByPlayer(aWorld, aPlayer, aWillHarvest) :
            super.removedByPlayer(aWorld, aPlayer, aX, aY, aZ, aWillHarvest);
    }

    @Override
    public final boolean canCreatureSpawn(EnumCreatureType aType, IBlockAccess aWorld, int aX, int aY, int aZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_CanCreatureSpawn &&
            ((IMTE_CanCreatureSpawn)aTileEntity).canCreatureSpawn(aType);
    }

    @Override
    public final void beginLeavesDecay(World aWorld, int aX, int aY, int aZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (aTileEntity instanceof IMTE_BeginLeavesDecay) ((IMTE_BeginLeavesDecay)aTileEntity).beginLeavesDecay();
        else super.beginLeavesDecay(aWorld, aX, aY, aZ);
    }

    @Override
    public final boolean canSustainLeaves(IBlockAccess aWorld, int aX, int aY, int aZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_CanSustainLeaves ?
            ((IMTE_CanSustainLeaves)aTileEntity).canSustainLeaves() :
            super.canSustainLeaves(aWorld, aX, aY, aZ);
    }

    @Override
    public final boolean isLeaves(IBlockAccess aWorld, int aX, int aY, int aZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_IsLeaves && ((IMTE_IsLeaves)aTileEntity).isLeaves();
    }

    @Override
    public final boolean canBeReplacedByLeaves(IBlockAccess aWorld, int aX, int aY, int aZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_CanBeReplacedByLeaves && ((IMTE_CanBeReplacedByLeaves)aTileEntity).canBeReplacedByLeaves();
    }

    @Override
    public final boolean isWood(IBlockAccess aWorld, int aX, int aY, int aZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_IsWood ?
            ((IMTE_IsWood)aTileEntity).isWood() :
            super.isWood(aWorld, aX, aY, aZ);
    }

    @Override
    public final boolean isReplaceableOreGen(World aWorld, int aX, int aY, int aZ, Block aTarget) {
        if (!GregTech_API.mServerStarted) return false;
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_IsReplaceableOreGen ?
            ((IMTE_IsReplaceableOreGen)aTileEntity).isReplaceableOreGen(aTarget) :
            super.isReplaceableOreGen(aWorld, aX, aY, aZ, aTarget);
    }

    @Override
    public final boolean canConnectRedstone(IBlockAccess aWorld, int aX, int aY, int aZ, int aSide) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_CanConnectRedstone ?
            ((IMTE_CanConnectRedstone)aTileEntity).canConnectRedstone(ForgeDirection.getOrientation(aSide)) :
            super.canConnectRedstone(aWorld, aX, aY, aZ, aSide);
    }

    @Override
    public final boolean canPlaceTorchOnTop(World aWorld, int aX, int aY, int aZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_CanPlaceTorchOnTop ?
            ((IMTE_CanPlaceTorchOnTop)aTileEntity).canPlaceTorchOnTop() :
            isSideSolid(aWorld, aX, aY, aZ, ForgeDirection.getOrientation(SIDE_TOP));
    }

    @Override
    public final boolean isFoliage(IBlockAccess aWorld, int aX, int aY, int aZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_IsFoliage ?
            ((IMTE_IsFoliage)aTileEntity).isFoliage() :
            super.isFoliage(aWorld, aX, aY, aZ);
    }

    @Override
    public final boolean canSustainPlant(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide, IPlantable aPlantable) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_CanSustainPlant ?
            ((IMTE_CanSustainPlant)aTileEntity).canSustainPlant(aSide, aPlantable) :
            super.canSustainPlant(aWorld, aX, aY, aZ, aSide, aPlantable);
    }

    @Override
    public final void onPlantGrow(World aWorld, int aX, int aY, int aZ, int sX, int sY, int sZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (aTileEntity instanceof IMTE_OnPlantGrow) ((IMTE_OnPlantGrow)aTileEntity).onPlantGrow(sX, sY, sZ);
        else super.onPlantGrow(aWorld, aX, aY, aZ, sX, sY, sZ);
    }

    @Override
    public final boolean isFertile(World aWorld, int aX, int aY, int aZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_IsFertile && ((IMTE_IsFertile)aTileEntity).isFertile();
    }

    @Override
    public final boolean rotateBlock(World aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_RotateBlock &&
            ((IMTE_RotateBlock)aTileEntity).rotateBlock(aSide);
    }

    @Override
    public final ForgeDirection[] getValidRotations(World aWorld, int aX, int aY, int aZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_GetValidRotations ? ((IMTE_GetValidRotations)aTileEntity).getValidRotations() : new ForgeDirection[0];
    }

    @Override
    public final float getEnchantPowerBonus(World aWorld, int aX, int aY, int aZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_GetEnchantPowerBonus ?
            ((IMTE_GetEnchantPowerBonus)aTileEntity).getEnchantPowerBonus() : 0;
    }

    @Override
    public final boolean recolourBlock(World aWorld, int aX, int aY, int aZ, ForgeDirection aSide, int aColor) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_RecolourBlock &&
            ((IMTE_RecolourBlock)aTileEntity).recolourBlock(aSide, (byte) aColor);
    }

    @Override
    public final boolean shouldCheckWeakPower(IBlockAccess aWorld, int aX, int aY, int aZ, int aSide) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_ShouldCheckWeakPower ?
            ((IMTE_ShouldCheckWeakPower)aTileEntity).shouldCheckWeakPower(ForgeDirection.getOrientation(aSide)) :
            isNormalCube(aWorld, aX, aY, aZ);
    }

    @Override
    public final boolean getWeakChanges(IBlockAccess aWorld, int aX, int aY, int aZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_GetWeakChanges ?
            ((IMTE_GetWeakChanges)aTileEntity).getWeakChanges() :
            super.getWeakChanges(aWorld, aX, aY, aZ);
    }

    @Override
    public final boolean addHitEffects(World aWorld, MovingObjectPosition aTarget, EffectRenderer aRenderer) {
        TileEntity aTileEntity = aWorld.getTileEntity(aTarget.blockX, aTarget.blockY, aTarget.blockZ);
        return aTileEntity instanceof IMTE_AddHitEffects &&
            ((IMTE_AddHitEffects)aTileEntity).addHitEffects(aWorld, aTarget, aRenderer);
    }

    @Override
    public final boolean addDestroyEffects(World aWorld, int aX, int aY, int aZ, int aMetaData, EffectRenderer aRenderer) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_AddDestroyEffects &&
            ((IMTE_AddDestroyEffects)aTileEntity).addDestroyEffects(aMetaData, aRenderer);
    }

    @Override
    public final boolean shouldSideBeRendered(IBlockAccess aWorld, int aX, int aY, int aZ, int aSide) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX-OFFX[aSide], aY-OFFY[aSide], aZ-OFFZ[aSide]);
        return aTileEntity instanceof IMTE_ShouldSideBeRendered ?
            ((IMTE_ShouldSideBeRendered)aTileEntity).shouldSideBeRendered(ForgeDirection.getOrientation(aSide)) :
            super.shouldSideBeRendered(aWorld, aX, aY, aZ, aSide);
    }

    @Override
    public final void setBlockBoundsBasedOnState(IBlockAccess aWorld, int aX, int aY, int aZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (aTileEntity instanceof IMTE_SetBlockBoundsBasedOnState)
            ((IMTE_SetBlockBoundsBasedOnState)aTileEntity).setBlockBoundsBasedOnState(this);
        else if (aTileEntity == null)
            setBlockBounds(-999, -999, -999, -998, -998, -998);
        else setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    @Override
    public final AxisAlignedBB getSelectedBoundingBoxFromPool(World aWorld, int aX, int aY, int aZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity == null ?
            AxisAlignedBB.getBoundingBox(-999, -999, -999, -998, -998, -998) :
            aTileEntity instanceof IMTE_GetSelectedBoundingBoxFromPool ?
                ((IMTE_GetSelectedBoundingBoxFromPool)aTileEntity).getSelectedBoundingBoxFromPool() :
                AxisAlignedBB.getBoundingBox(aX, aY, aZ, aX+1, aY+1, aZ+1);
    }

    @Override
    public final void randomDisplayTick(World aWorld, int aX, int aY, int aZ, Random aRandom) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (aTileEntity instanceof IMTE_RandomDisplayTick)
            ((IMTE_RandomDisplayTick)aTileEntity).randomDisplayTick(aRandom);
        else super.randomDisplayTick(aWorld, aX, aY, aZ, aRandom);
    }

    @Override
    public final void onBlockExploded(World aWorld, int aX, int aY, int aZ, Explosion aExplosion) {
        if (aWorld.isRemote) return;
        TileEntity aTileEntity = GT_Util.getTileEntity(aWorld, aX, aY, aZ, true);
        if (aTileEntity != null) LAST_BROKEN_TILEENTITY.set(aTileEntity);
        if (aTileEntity instanceof IMTE_OnBlockExploded) ((IMTE_OnBlockExploded)aTileEntity).onExploded(aExplosion);
        else aWorld.setBlockToAir(aX, aY, aZ);
    }

    @Override
    public ItemStack getPickBlock(final MovingObjectPosition target, final World world, final int x, final int y, final int z) {
        final TileEntity te = world.getTileEntity(x,y,z);
        if (!(te instanceof final IMultiTileEntity mute)) return null;
        return registry.getItem(mute.getMetaId());
    }

    @Override
    public ItemStack getPickBlock(final MovingObjectPosition target, final World world, final int x, final int y, final int z, final EntityPlayer player) {
        final TileEntity te = world.getTileEntity(x,y,z);
        if (!(te instanceof final IMultiTileEntity mute)) return null;
        return registry.getItem(mute.getMetaId());
    }

    @Override
    public final ItemStack getItemStackFromBlock(IBlockAccess aWorld, int aX, int aY, int aZ, byte aSide) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_GetStackFromBlock ? ((IMTE_GetStackFromBlock)aTileEntity).getStackFromBlock(aSide) : null;
    }

    @Override
    public final int getFlammability(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_GetFlammability ?
            ((IMTE_GetFlammability)aTileEntity).getFlammability(aSide, getMaterial().getCanBurn()) :
            getMaterial().getCanBurn() ? 150 : 0;
    }

    @Override
    public final int getFireSpreadSpeed(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_GetFireSpreadSpeed ?
            ((IMTE_GetFireSpreadSpeed)aTileEntity).getFireSpreadSpeed(aSide, getMaterial().getCanBurn()) :
            getMaterial().getCanBurn() ? 150 : 0;
    }

    @Override
    public final boolean isFireSource(World aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_IsFireSource &&
            ((IMTE_IsFireSource)aTileEntity).isFireSource(aSide);
    }

    @Override
    public final boolean canEntityDestroy(IBlockAccess aWorld, int aX, int aY, int aZ, Entity aEntity) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return !(aTileEntity instanceof IMTE_CanEntityDestroy) ||
            ((IMTE_CanEntityDestroy)aTileEntity).canEntityDestroy(aEntity);
    }

    @Override
    public final void dropBlockAsItemWithChance(World aWorld, int aX, int aY, int aZ, int aMeta, float aChance, int aFortune) {
        TileEntity aTileEntity = GT_Util.getTileEntity(aWorld, aX, aY, aZ, true);
        if (aTileEntity instanceof IMTE_GetDrops) {
            ArrayListNoNulls<ItemStack> tList = ((IMTE_GetDrops)aTileEntity).getDrops(aFortune, false);
            aChance = ForgeEventFactory.fireBlockHarvesting(tList, aWorld, this, aX, aY, aZ, aMeta, aFortune, aChance, false, harvesters.get());
            for (ItemStack tStack : tList)
                if (RNGSUS.nextFloat() <= aChance) dropBlockAsItem(aWorld, aX, aY, aZ, tStack);
        }
    }

    @Override
    public final void harvestBlock(World aWorld, EntityPlayer aPlayer, int aX, int aY, int aZ, int aMeta) {
        if (aPlayer == null) aPlayer = harvesters.get();
        aPlayer.addStat(StatList.mineBlockStatArray[getIdFromBlock(this)], 1);
        aPlayer.addExhaustion(0.025F);
        boolean aSilkTouch = EnchantmentHelper.getSilkTouchModifier(aPlayer);
        int aFortune = EnchantmentHelper.getFortuneModifier(aPlayer);
        float aChance = 1.0F;
        TileEntity aTileEntity = GT_Util.getTileEntity(aWorld, aX, aY, aZ, true);
        if (aTileEntity instanceof IMTE_GetDrops) {
            ArrayListNoNulls<ItemStack> tList = ((IMTE_GetDrops)aTileEntity).getDrops(aFortune, aSilkTouch);
            aChance = ForgeEventFactory.fireBlockHarvesting(tList, aWorld, this, aX, aY, aZ, aMeta, aFortune, aChance, aSilkTouch, aPlayer);
            for (ItemStack tStack : tList)
                if (RNGSUS.nextFloat() <= aChance)
                    dropBlockAsItem(aWorld, aX, aY, aZ, tStack);
        }
    }

    @Override
    public final ArrayList<ItemStack> getDrops(World aWorld, int aX, int aY, int aZ, int aUnusableMetaData, int aFortune) {
        TileEntity aTileEntity = GT_Util.getTileEntity(aWorld, aX, aY, aZ, true);
        if (aTileEntity instanceof IMTE_GetDrops)
            return ((IMTE_GetDrops)aTileEntity).getDrops(aFortune, false);
        return arraylist();
    }

    private static ArrayListNoNulls<ItemStack> arraylist(ItemStack... aStacks) {
        return new ArrayListNoNulls<>(false, aStacks);
    }

    @Override public final boolean isSideSolid(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_IsSideSolid ?
            ((IMTE_IsSideSolid)aTileEntity).isSideSolid((byte) aSide.ordinal()) : mOpaque;
    }

    @Override
    public final boolean isBeaconBase(IBlockAccess aWorld, int aX, int aY, int aZ, int aBeaconX, int aBeaconY, int aBeaconZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_IsBeaconBase &&
            ((IMTE_IsBeaconBase)aTileEntity).isBeaconBase(aBeaconX, aBeaconY, aBeaconZ);
    }

    @Override
    public final int getLightOpacity(IBlockAccess aWorld, int aX, int aY, int aZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_GetLightOpacity?((IMTE_GetLightOpacity)aTileEntity).getLightOpacity(): mOpaque ? 255 : 0;
    }

    @Override
    public final boolean isOpaqueCube() {
        return mOpaque;
    }

    @Override
    public final boolean func_149730_j() {
        return mOpaque;
    }

    @Override
    public final boolean renderAsNormalBlock() {
        return mOpaque || mNormalCube;
    }

    @Override
    public final boolean isNormalCube()  {
        return mNormalCube;
    }

    @Override
    public final boolean canProvidePower() {
        return !mNormalCube;
    }

    @Override
    public final Block getBlock() {
        return this;
    }

    @Override
    public String getHarvestTool(final int metadata) {
        return toolName;
    }

    @Override
    public final boolean isToolEffective(String aType, int aMeta) {
        return getHarvestTool(aMeta).equals(aType);
    }

    @Override
    public final int getHarvestLevel(int aMeta) {
        if (mHarvestLevel >= mHarvestLevelMaximum) return mHarvestLevelMaximum;
        return Math.max(mHarvestLevel + aMeta, mHarvestLevelMinimum);
    }

    @Override
    public final boolean canHarvestBlock(EntityPlayer aPlayer, int aMeta) {
        return super.canHarvestBlock(aPlayer, aMeta);
    }

    @Override
    public final boolean hasTileEntity(int aMeta) {
        return true;
    }
    @Override
    public final boolean canSilkHarvest() {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(final World worldIn, final int meta) {
        return registry.getNewTileEntity(meta);
    }

    @Override
    public void getSubBlocks(final Item itemIn, final CreativeTabs tab, final List<ItemStack> list) {
        for (final MultiTileEntityClassContainer container : registry.registrations) {
            list.add(new ItemStack(this, 0, container.getMetaId()));
        }
    }

    @Override
    public final boolean setBlockBounds(int aRenderPass, IBlockAccess aWorld, int aX, int aY, int aZ, boolean[] aShouldSideBeRendered) {
        return false;
    }

    @Override
    public ITexture[] getTexture(Block aBlock, ForgeDirection side, int aRenderPass, boolean[] aShouldSideBeRendered) {
        return null;
    }

    @Override
    public ITexture[] getTexture(Block aBlock, ForgeDirection side, boolean isActive, int aRenderPass) {
        return null;
    }
    @Override
    public final boolean setBlockBounds(int aRenderPass, ItemStack aStack) {
        return false;
    }

    @Override
    public final IRenderedBlockObject passRenderingToObject(ItemStack aStack) {
        return null;
    }

    @Override
    public final void registerBlockIcons(IIconRegister aIconRegister) { /**/ }

    @Override
    public final IIcon getIcon(IBlockAccess aWorld, int aX, int aY, int aZ, int aSide) {
        return Textures.BlockIcons.CFOAM_HARDENED.getIcon();
    }

    @Override public final IIcon getIcon(int aSide, int aMetaData) {
        return Textures.BlockIcons.CFOAM_HARDENED.getIcon();
    }

    //TODO: Fix the damn renderer
    @Override
    public final int getRenderPasses(IBlockAccess aWorld, int aX, int aY, int aZ, boolean[] aShouldSideBeRendered) {
        return 0;
    }

    @Override
    public final int getRenderPasses(ItemStack aStack) {
        return 0;
    }

    @Override
    public final int getRenderBlockPass() {
        return GT_MultiTile_Renderer.MC_ALPHA_BLENDING ? 1 : 0;
    }

    @Override
    public int getRenderType() {
        return GT_MultiTile_Renderer.INSTANCE == null ? super.getRenderType()
            : GT_MultiTile_Renderer.INSTANCE.getRenderId();
    }

    @Override
    public final IRenderedBlockObject passRenderingToObject(IBlockAccess aWorld, int aX, int aY, int aZ) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return tTileEntity instanceof IRenderedBlockObject ?
            (IRenderedBlockObject)tTileEntity : null;
    }

    @Override
    public final boolean onBlockEventReceived(World aWorld, int aX, int aY, int aZ, int aID, int aData) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity == null || aTileEntity.receiveClientEvent(aID, aData);
    }

    @Override
    public final float getPlayerRelativeBlockHardness(EntityPlayer aPlayer, World aWorld, int aX, int aY, int aZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_GetPlayerRelativeBlockHardness ?
            ((IMTE_GetPlayerRelativeBlockHardness)aTileEntity)
                .getPlayerRelativeBlockHardness(aPlayer, super.getPlayerRelativeBlockHardness(aPlayer, aWorld, aX, aY, aZ)) :
            super.getPlayerRelativeBlockHardness(aPlayer, aWorld, aX, aY, aZ);
    }

    @Override
    public final float getBlockHardness(World aWorld, int aX, int aY, int aZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_GetBlockHardness ? ((IMTE_GetBlockHardness)aTileEntity).getBlockHardness() : 1.0F;
    }

    @Override
    public final float getExplosionResistance(Entity aExploder, World aWorld, int aX, int aY, int aZ, double aExplosionX, double aExplosionY, double aExplosionZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_GetExplosionResistance ?
            ((IMTE_GetExplosionResistance)aTileEntity).getExplosionResistance(aExploder, aExplosionX, aExplosionY, aExplosionZ) : 1.0F;
    }

    @Override
    public final void onNeighborChange(IBlockAccess aWorld, int aX, int aY, int aZ, int aTileX, int aTileY, int aTileZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ); if (!LOCK) {
            LOCK = true;
            if (aTileEntity instanceof IMultiTileEntity)
                ((IMultiTileEntity)aTileEntity).onAdjacentBlockChange(aTileX, aTileY, aTileZ);
            LOCK = false;
        }
        if (aTileEntity instanceof IMTE_OnNeighborChange)
            ((IMTE_OnNeighborChange)aTileEntity).onNeighborChange(aWorld, aTileX, aTileY, aTileZ);
    }

    @Override
    public final void onNeighborBlockChange(World aWorld, int aX, int aY, int aZ, Block aBlock) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (!LOCK) {
            LOCK = true;
            if (aTileEntity instanceof IMultiTileEntity)
                ((IMultiTileEntity)aTileEntity).onAdjacentBlockChange(aX, aY, aZ);
            LOCK = false;
        }
        if (aTileEntity instanceof IMTE_OnNeighborBlockChange)
            ((IMTE_OnNeighborBlockChange)aTileEntity).onNeighborBlockChange(aWorld, aBlock);
        if (aTileEntity == null) aWorld.setBlockToAir(aX, aY, aZ);
    }

    @Override
    public final boolean usesRenderPass(int aRenderPass, ItemStack aStack) {
        return true;
    }

    @Override
    public final boolean usesRenderPass(int aRenderPass, IBlockAccess aWorld, int aX, int aY, int aZ, boolean[] aShouldSideBeRendered) {
        return true;
    }

    @Override
    public final void onWalkOver(EntityLivingBase aEntity, World aWorld, int aX, int aY, int aZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (aTileEntity instanceof IMTE_OnWalkOver)
            ((IMTE_OnWalkOver)aTileEntity).onWalkOver(aEntity);
    }
}
