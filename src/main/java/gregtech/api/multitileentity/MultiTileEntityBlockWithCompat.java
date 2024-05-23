package gregtech.api.multitileentity;

import cpw.mods.fml.common.Optional;
import gregtech.api.enums.Mods;
import gregtech.api.multitileentity.compat.galacticraft.IBlockSealable;
import micdoodle8.mods.galacticraft.api.block.IOxygenReliantBlock;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openblocks.api.IPaintableBlock;
import vazkii.botania.api.mana.IManaTrigger;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_OnPainting;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_IsSealable;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_OnOxygenRemoved;
import gregtech.api.multitileentity.interfaces.IMultiTileEntity.IMTE_OnOxygenAdded;

import static gregtech.api.enums.GT_Values.SIDE_UNKNOWN;
import static gregtech.api.enums.ItemList.BOTA_Paintslinger;

@Optional.InterfaceList(value = {
    @Optional.Interface(iface = "openblocks.api.IPaintableBlock", modid = Mods.Names.OPEN_BLOCKS)
    , @Optional.Interface(iface = "micdoodle8.mods.galacticraft.api.block.IOxygenReliantBlock", modid = Mods.Names.GALACTICRAFT_CORE)
    , @Optional.Interface(iface = "vazkii.botania.api.mana.IManaTrigger", modid = Mods.Names.BOTANIA)
})
@SuppressWarnings("deprecation")
public abstract class MultiTileEntityBlockWithCompat extends MultiTileEntityBlock implements IBlockSealable, IOxygenReliantBlock, IPaintableBlock, IManaTrigger {
    /*
    protected static MultiTileEntityBlock create(String aModID, String aNameOfVanillaMaterialField,
    Material aVanillaMaterial, SoundType aSoundType, String aTool, int aHarvestLevelOffset, int aHarvestLevelMinimum,
    int aHarvestLevelMaximum, boolean aOpaque, boolean aNormalCube) {
        return new MultiTileEntityBlockWithCompat(aModID, aNameOfVanillaMaterialField, aVanillaMaterial, aSoundType,
            aTool, aHarvestLevelOffset, aHarvestLevelMinimum, aHarvestLevelMaximum, aOpaque, aNormalCube);
    }

    protected MultiTileEntityBlockWithCompat(String aModID, String aNameOfVanillaMaterialField,
    Material aVanillaMaterial, SoundType aSoundType, String aTool, int aHarvestLevelOffset, int aHarvestLevelMinimum,
    int aHarvestLevelMaximum, boolean aOpaque, boolean aNormalCube) {
        super(aModID, aNameOfVanillaMaterialField, aVanillaMaterial, aSoundType, aTool, aHarvestLevelOffset,
            aHarvestLevelMinimum, aHarvestLevelMaximum, aOpaque, aNormalCube);
    }

    @Override
    public final boolean recolourBlockRGB(World aWorld, int aX, int aY, int aZ, ForgeDirection aDirection, int aRGB) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_OnPainting && ((IMTE_OnPainting)aTileEntity).onPainting(aDirection, aRGB);
    }

    @Override
    public final boolean isSealed(World aWorld, int aX, int aY, int aZ, ForgeDirection aDirection) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return aTileEntity instanceof IMTE_IsSealable && ((IMTE_IsSealable)aTileEntity).isSealable(ForgeDirection.getOrientation(aDirection.ordinal() ^ 1));
    }

    @Override
    public final void onOxygenAdded(World aWorld, int aX, int aY, int aZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (aTileEntity instanceof IMTE_OnOxygenAdded) ((IMTE_OnOxygenAdded)aTileEntity).onOxygenAdded();
    }

    @Override
    public final void onOxygenRemoved(World aWorld, int aX, int aY, int aZ) {
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (aTileEntity instanceof IMTE_OnOxygenRemoved)
            ((IMTE_OnOxygenRemoved)aTileEntity).onOxygenRemoved();
    }

    @Override
    @Optional.Method(modid = Mods.Names.BOTANIA)
    public final void onBurstCollision(vazkii.botania.api.internal.IManaBurst aMana, World aWorld, int aX, int aY, int aZ) {
        if (aWorld.isRemote) return;
        if (aMana.isFake() || !BOTA_Paintslinger.isStackEqual(aMana.getSourceLens(), false, true) ||
            !aMana.getSourceLens().hasTagCompound() ||
            !aMana.getSourceLens().getTagCompound().hasKey("color") ||
            aMana.getSourceLens().getTagCompound().getInteger("color") == -1) return;
        TileEntity aTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (aTileEntity instanceof IMTE_OnPainting)
            ((IMTE_OnPainting)aTileEntity).onPainting(ForgeDirection.getOrientation(SIDE_UNKNOWN), (aMana.getColor() & 0x00ffffff));
    }
     */
}
