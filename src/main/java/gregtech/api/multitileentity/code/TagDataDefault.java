package gregtech.api.multitileentity.code;

import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class TagDataDefault {

    /** For NEI and Creative Mode Menus */
    public static class Creative {
        /** If this Item, Prefix or Material is hidden from Creative (unless Debug Mode is on) */
        public static final TagData HIDDEN = TagData.createTagData("NEI.HIDDEN", "Hidden");
    }

    /** For the Projectile System. */
    public static class Projectiles {
        public static final TagData ARROW = TagData.createTagData("PROJECTILES.ARROW", "Arrow");
        public static final TagData BULLET_SMALL = TagData.createTagData("PROJECTILES.BULLET_SMALL", "Small Bullet");
        public static final TagData BULLET_MEDIUM = TagData.createTagData("PROJECTILES.BULLET_MEDIUM", "Medium Bullet");
        public static final TagData BULLET_LARGE = TagData.createTagData("PROJECTILES.BULLET_LARGE", "Large Bullet");
    }

    /** Energy Tag for Ordo Vis. In MilliVis Units. 10 = 1 CentiVis. 1000 = 1 Full Vis. */
    public static final TagData VIS_ORDO                                = TagData.createTagData("ENERGY.VIS_ORDO", "Ordo", "Ordo Vis", EnumChatFormatting.WHITE.toString());
    /** Energy Tag for Aer Vis. In MilliVis Units. 10 = 1 CentiVis. 1000 = 1 Full Vis. */
    public static final TagData VIS_AER                                 = TagData.createTagData("ENERGY.VIS_AER", "Aer", "Aer Vis", EnumChatFormatting.YELLOW.toString());
    /** Energy Tag for Aqua Vis. In MilliVis Units. 10 = 1 CentiVis. 1000 = 1 Full Vis. */
    public static final TagData VIS_AQUA                                = TagData.createTagData("ENERGY.VIS_AQUA", "Aqua", "Aqua Vis", EnumChatFormatting.AQUA.toString());
    /** Energy Tag for Terra Vis. In MilliVis Units. 10 = 1 CentiVis. 1000 = 1 Full Vis. */
    public static final TagData VIS_TERRA                               = TagData.createTagData("ENERGY.VIS_TERRA", "Terra", "Terra Vis", EnumChatFormatting.GREEN.toString());
    /** Energy Tag for Ignis Vis. In MilliVis Units. 10 = 1 CentiVis. 1000 = 1 Full Vis. */
    public static final TagData VIS_IGNIS                               = TagData.createTagData("ENERGY.VIS_IGNIS", "Ignis", "Ignis Vis", EnumChatFormatting.RED.toString());
    /** Energy Tag for Perditio Vis. In MilliVis Units. 10 = 1 CentiVis. 1000 = 1 Full Vis. */
    public static final TagData VIS_PERDITIO                            = TagData.createTagData("ENERGY.VIS_PERDITIO", "Perditio", "Perditio Vis", EnumChatFormatting.BLACK.toString());
    /** Set of Energy Tags which are of the Vis Type. In MilliVis Units. 10 = 1 CentiVis. 1000 = 1 Full Vis. The Acronym "VIS" stands for "Very Interesting Stuff" */
    public static final List<TagData> VIS                               = new ArrayListNoNulls<>(false, VIS_ORDO, VIS_AER, VIS_AQUA, VIS_TERRA, VIS_IGNIS, VIS_PERDITIO);


}
