package gregtech.common.blocks;

import java.util.List;

import gregtech.api.enums.Textures;
import gregtech.common.render.GT_RenderChest;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTech_API;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.items.GT_Generic_Block;
import gregtech.api.metatileentity.MetaTileEntityStorage;
import gregtech.api.util.GT_LanguageManager;
import gregtech.api.util.GT_Utility;
import gregtech.common.tileentities.storage.GT_MetaTileEntity_MetalChest;

/**
 * The casings are split into separate files because they are registered as regular blocks, and a regular block can have
 * 16 subtypes at most.
 */
public class GT_Block_MetalChest1 extends GT_Generic_Block implements ITileEntityProvider {
    public GT_Block_MetalChest1() {
        super(GT_Item_MetalChest1.class, "gt.block.storage.metalchest", GT_Material_Casings.INSTANCE);
        setStepSound(soundTypeMetal);
        setCreativeTab(GregTech_API.TAB_GREGTECH);
        GregTech_API.registerMachineBlock(this, -1);
        onStorageRegistration(this, -1);

        // GT_LanguageManager.addStringLocalization(getUnlocalizedName() + "." + 32767 + ".name", "Any Sub Block of
        // this");

        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".0.name", "Lead Metal Chest");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".1.name", "Null Metal Chest");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".2.name", "Null Metal Chest");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".3.name", "Null Metal Chest");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".4.name", "Null Metal Chest");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".5.name", "Null Metal Chest");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".6.name", "Null Metal Chest");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".7.name", "Null Metal Chest");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".8.name", "Null Metal Chest");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".9.name", "Null Metal Chest");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".10.name", "Null Metal Chest");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".11.name", "Null Metal Chest");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".12.name", "Null Metal Chest");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".13.name", "Null Metal Chest");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".14.name", "Null Metal Chest");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".15.name", "Null Metal Chest");
        ItemList.Storage_Chest_Metal.set(new ItemStack(this, 1, 0));
        ItemList.Storage_Chest_Metal.set(new ItemStack(this, 1, 1));
        ItemList.Storage_Chest_Metal.set(new ItemStack(this, 1, 2));
        ItemList.Storage_Chest_Metal.set(new ItemStack(this, 1, 3));
        ItemList.Storage_Chest_Metal.set(new ItemStack(this, 1, 4));
        ItemList.Storage_Chest_Metal.set(new ItemStack(this, 1, 5));
        ItemList.Storage_Chest_Metal.set(new ItemStack(this, 1, 6));
        ItemList.Storage_Chest_Metal.set(new ItemStack(this, 1, 7));
        ItemList.Storage_Chest_Metal.set(new ItemStack(this, 1, 8));
        ItemList.Storage_Chest_Metal.set(new ItemStack(this, 1, 9));
        ItemList.Storage_Chest_Metal.set(new ItemStack(this, 1, 10));
        ItemList.Storage_Chest_Metal.set(new ItemStack(this, 1, 11));
        ItemList.Storage_Chest_Metal.set(new ItemStack(this, 1, 12));
        ItemList.Storage_Chest_Metal.set(new ItemStack(this, 1, 13));
        ItemList.Storage_Chest_Metal.set(new ItemStack(this, 1, 14));
        ItemList.Storage_Chest_Metal.set(new ItemStack(this, 1, 15));
    }

    @Override
    public boolean isFlammable(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection face) {
        return false;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public String getHarvestTool(int aMeta) {
        return "pickaxe";
    }

    @Override
    public int getHarvestLevel(int aMeta) {
        return 2;
    }

    @Override
    public final boolean isToolEffective(String aType, int aMeta) {
        return getHarvestTool(aMeta).equals(aType);
    }

    @Override
    public float getBlockHardness(World aWorld, int aX, int aY, int aZ) {
        return Blocks.iron_block.getBlockHardness(aWorld, aX, aY, aZ);
    }

    @Override
    public float getExplosionResistance(Entity aTNT) {
        return Blocks.iron_block.getExplosionResistance(aTNT);
    }

    @Override
    protected boolean canSilkHarvest() {
        return false;
    }

    @Override
    public void onBlockAdded(World aWorld, int aX, int aY, int aZ) {
        if (GregTech_API.isMachineBlock(this, aWorld.getBlockMetadata(aX, aY, aZ))) {
            GregTech_API.causeMachineUpdate(aWorld, aX, aY, aZ);
        }
    }

    @Override
    public String getUnlocalizedName() {
        return this.mUnlocalizedName;
    }

    @Override
    public String getLocalizedName() {
        return StatCollector.translateToLocal(this.mUnlocalizedName + ".name");
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess aWorld, int aX, int aY, int aZ) {
        return false;
    }

    @Override
    public void breakBlock(World aWorld, int aX, int aY, int aZ, Block aBlock, int aMetaData) {
        if (GregTech_API.isMachineBlock(this, aWorld.getBlockMetadata(aX, aY, aZ))) {
            GregTech_API.causeMachineUpdate(aWorld, aX, aY, aZ);
        }
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public int damageDropped(int metadata) {
        return metadata;
    }

    @Override
    public int getDamageValue(World aWorld, int aX, int aY, int aZ) {
        return aWorld.getBlockMetadata(aX, aY, aZ);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister aIconRegister) {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item aItem, CreativeTabs aCreativeTab, List<ItemStack> aList) {
        for (int i = 0; i < 16; i++) {
            ItemStack aStack = new ItemStack(aItem, 1, i);
            if (!aStack.getDisplayName()
                .contains(".name")) aList.add(aStack);
        }
    }

    @Override
    public IIcon getIcon(int ordinalSide, int aMeta) {
        return null;
    }

    @Override
    public int getRenderType() {
        return 22;
    }

    @Override
    public int colorMultiplier(IBlockAccess aWorld, int aX, int aY, int aZ) {
        return aWorld.getBlockMetadata(aX, aY, aZ) > 9 ? super.colorMultiplier(aWorld, aX, aY, aZ)
            : gregtech.api.enums.Dyes.MACHINE_METAL.mRGBa[0] << 16 | gregtech.api.enums.Dyes.MACHINE_METAL.mRGBa[1] << 8
                | gregtech.api.enums.Dyes.MACHINE_METAL.mRGBa[2];
    }

    @Override
    public boolean onBlockEventReceived(World aWorld, int aX, int aY, int aZ, int aData1, int aData2) {
        super.onBlockEventReceived(aWorld, aX, aY, aZ, aData1, aData2);
        final TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return tTileEntity != null && tTileEntity.receiveClientEvent(aData1, aData2);
    }

    @Override
    public boolean onBlockActivated(World aWorld, int aX, int aY, int aZ, EntityPlayer aPlayer, int ordinalSide,
        float aOffsetX, float aOffsetY, float aOffsetZ) {
        final TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (tTileEntity == null) {
            return false;
        }
        if (aPlayer.isSneaking()) {
            final ItemStack tCurrentItem = aPlayer.inventory.getCurrentItem();
            if (tCurrentItem != null && !GT_Utility.isStackInList(tCurrentItem, GregTech_API.sScrewdriverList)
                && !GT_Utility.isStackInList(tCurrentItem, GregTech_API.sWrenchList)
                && !GT_Utility.isStackInList(tCurrentItem, GregTech_API.sWireCutterList)
                && !GT_Utility.isStackInList(tCurrentItem, GregTech_API.sSolderingToolList)
                && !GT_Utility.isStackInList(tCurrentItem, GregTech_API.sJackhammerList)) return false;
        }
        if (tTileEntity instanceof IGregTechTileEntity gtTE) {
            if (gtTE.getTimer() < 1L) {
                return false;
            }
            if ((!aWorld.isRemote) && !gtTE.isUseableByPlayer(aPlayer)) {
                return true;
            }
            return ((IGregTechTileEntity) tTileEntity)
                .onRightclick(aPlayer, ForgeDirection.getOrientation(ordinalSide), aOffsetX, aOffsetY, aOffsetZ);
        }
        return false;
    }

    // Rendering
    @SideOnly(Side.CLIENT)
    private static GT_RenderChest RENDERER;

    @SideOnly(Side.CLIENT)
    public void onStorageRegistration(Block block, int aMeta) {
        ClientRegistry.bindTileEntitySpecialRenderer(
            MetaTileEntityStorage.class,
            RENDERER = new GT_RenderChest());
    }

    private static final float minX = 0.0625F, minY = 0F, minZ = 0.0625F, maxX = 0.9375F, maxY = 0.875F, maxZ = 0.9375F;

    @Override
    public boolean isBlockNormalCube() {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockAccess aWorld, int aX, int aY, int aZ) {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask,
        List<net.minecraft.util.AxisAlignedBB> list, Entity collider) {
        AxisAlignedBB axisalignedbb1 = this.getCollisionBoundingBoxFromPool(worldIn, x, y, z);

        if (axisalignedbb1 != null && mask.intersectsWith(axisalignedbb1)) {
            list.add(axisalignedbb1);
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        return AxisAlignedBB.getBoundingBox(
            (double) x + minX,
            (double) y + minY,
            (double) z + minZ,
            (double) x + maxX,
            (double) y + maxY,
            (double) z + maxZ);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        this.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public void setBlockBoundsForItemRender() {
        super.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int aID) {
        return new GT_MetaTileEntity_MetalChest(aID, getUnlocalizedName(), getLocalizedName(), 27, Materials.Lead);
    }
}
