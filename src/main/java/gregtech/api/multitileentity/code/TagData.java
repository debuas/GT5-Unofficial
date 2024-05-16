package gregtech.api.multitileentity.code;

import cpw.mods.fml.common.registry.LanguageRegistry;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.ICondition;
import gregtech.api.util.GT_LanguageManager;
import net.minecraft.client.resources.Language;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 *
 * Useful for tagging things. It could Tag anything.
 * Better than Strings for tagging Stuff since you can do an == Check rather than needing to use equals.
 */
@SuppressWarnings("rawtypes")
public final class TagData implements ICondition<ITagDataContainer> {
    private static final List<TagData> TAGS_INTERNAL = new ArrayList<>();
    public static final List<TagData> TAGS = new ArrayList<>();

    public final int mTagID;
    public final String mName;
    public String mChatFormat = "";

    @SuppressWarnings("unchecked")
    public final ICondition<Materials> NOT = new ICondition.Not(this);

    public final List<TagData> AS_LIST = Collections.unmodifiableList(Arrays.asList(this));

    private TagData(String aName) {
        mTagID = TAGS_INTERNAL.size();
        mName = aName;
        TAGS_INTERNAL.add(this);
        TAGS.add(this);
    }

    public static TagData createTagData(String aName, String aLocalShort, String aLocalLong, String aChatFormat) {
        TagData rTagData = createTagData(aName, aLocalShort, aLocalLong);
        rTagData.mChatFormat = aChatFormat;
        return rTagData;
    }

    public static TagData createTagData(String aName, String aLocalShort, String aLocalLong) {
        TagData rTagData = createTagData(aName);
        GT_LanguageManager.addStringLocalization(rTagData.getTranslatableNameShort(), aLocalShort);
        GT_LanguageManager.addStringLocalization(rTagData.getTranslatableNameLong(), aLocalLong);
        return rTagData;
    }

    public static TagData createTagData(String aName, String aLocal) {
        return createTagData(aName, aLocal, aLocal);
    }

    public static TagData createTagData(String aName) {
        aName = aName.toUpperCase();
        for (TagData tSubTag : TAGS_INTERNAL) if (tSubTag.mName.equals(aName)) return tSubTag;
        return new TagData(aName);
    }

    public String getTranslatableNameLong() {
        return "gt.td.long."+mName.toLowerCase();
    }

    public String getLocalisedNameLong() {
        return GT_LanguageManager.getTranslation(getTranslatableNameLong()); //LH.get(getTranslatableNameLong(), mName);
    }

    public String getLocalisedChatNameLong() {
        return getChatFormat() + getLocalisedNameLong();
    }

    public String getTranslatableNameShort() {
        return "gt.td.short."+mName.toLowerCase();
    }

    public String getLocalisedNameShort() {
        return GT_LanguageManager.getTranslation(getTranslatableNameShort());
    }

    public String getLocalisedChatNameShort() {
        return getChatFormat() + getLocalisedNameShort();
    }

    public String getChatFormat() {
        return mChatFormat;
    }

    @Override
    public String toString() {
        return mName;
    }

    @Override
    public boolean isTrue(ITagDataContainer aObject) {
        return aObject.contains(this);
    }

    @Override
    public boolean equals(Object aObject) {
        return (aObject instanceof TagData && ((TagData)aObject).mTagID == mTagID);
    }

    @Override
    public int hashCode() {
        return mTagID;
    }
}
