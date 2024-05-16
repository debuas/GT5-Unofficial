package gregtech.api.multitileentity.enums;

import gregtech.api.enums.GT_Values;

public enum GT_MultiTileChests {
    Iron_Chest(1),
    Gold_Chest(2),
    Platinum_Chest(3),
    Lead_Chest(4),
    Granite_Chest(5),

    NONE(GT_Values.W);

    private final int meta;
    GT_MultiTileChests(int meta) {
        this.meta = meta;
    }

    public int getId() {
        return meta;
    }
}
