package gregtech.api.multitileentity.compat.galacticraft;

import cpw.mods.fml.common.Optional;
import gregtech.api.enums.Mods;
import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock;

/**
 * @author Gregorius Techneticies
 */
@Optional.InterfaceList(value = {
    @Optional.Interface(iface = "micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock", modid = Mods.Names.GALACTICRAFT_CORE)})
public interface IBlockSealable extends IPartialSealableBlock { /**/ }
