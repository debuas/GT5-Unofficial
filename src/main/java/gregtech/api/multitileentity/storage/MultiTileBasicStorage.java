package gregtech.api.multitileentity.storage;

import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import gregtech.api.enums.InventoryType;
import gregtech.api.gui.GUIHost;
import gregtech.api.gui.GUIProvider;
import gregtech.api.logic.ItemInventoryLogic;
import gregtech.api.logic.interfaces.ItemInventoryLogicHost;
import gregtech.api.multitileentity.MultiTileEntityRegistry;
import gregtech.api.multitileentity.base.TickableMultiTileEntity;
import gregtech.common.gui.InventoryGUIProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;


public abstract class MultiTileBasicStorage extends TickableMultiTileEntity implements ItemInventoryLogicHost, GUIHost {


    @Nonnull
    protected int storageSize = 27;

    protected int default_columns = 9;

    @Nonnull
    protected ItemInventoryLogic storage;
    @Nonnull
    protected GUIProvider<?> guiProvider = createGUIProvider();

    public MultiTileBasicStorage() {
        super();
        this.storage = new ItemInventoryLogic(storageSize);
    }


    @Override
    public boolean useModularUI() {
        return true;
    }

    @Override
    public void writeToNBT(NBTTagCompound aNBT) {
        super.writeToNBT(aNBT);
        saveItemLogic(aNBT);

    }

    protected void saveItemLogic(NBTTagCompound aNBT) {
        NBTTagCompound nbtListInput = storage.saveToNBT();
        aNBT.setTag("gt.inventory.storage.inv", nbtListInput);
        aNBT.setInteger("gt.inventory.storage.size", this.storageSize);
    }

    @Override
    public void readFromNBT(NBTTagCompound aNBT) {
        super.readFromNBT(aNBT);
        loadItemLogic(aNBT);

    }

    protected void loadItemLogic(NBTTagCompound aNBT) {
        if (aNBT.hasKey("gt.inventory.storage.size")) {
            this.storageSize = aNBT.getInteger("gt.inventory.storage.size");
        }
        storage = new ItemInventoryLogic(this.storageSize);

        if (aNBT.hasKey("gt.inventory.storage.inv")) {
            storage.loadFromNBT(aNBT.getCompoundTag("gt.inventory.storage.inv"));
        }


    }

    @Nullable
    @Override
    public ItemInventoryLogic getItemLogic(@NotNull ForgeDirection side, @NotNull InventoryType type) {
        return storage;
    }

    @Nonnull
    protected GUIProvider<?> createGUIProvider() {
        return new InventoryGUIProvider<>(this);
    }

    @Nonnull
    public GUIProvider<?> getGUI(@Nonnull UIBuildContext uiContext) {
        return guiProvider;
    }


    @Override
    public ItemStack getAsItem() {
        return MultiTileEntityRegistry.getRegistry(getRegistryId())
            .getItem(getRegistryId());
    }

    public boolean hasItemInput() {
        return true;
    }

    public boolean hasItemOutput() {
        return true;
    }

    //@Override
    //public boolean hasGui(ForgeDirection side) {
    //return true;
    //}

    @Override
    public int getSizeInventory() {
        InventoryType type = getItemInventoryType();
        ItemInventoryLogic logic = getItemLogic(ForgeDirection.UNKNOWN, type == null ? InventoryType.Output : type);
        if (logic == null) return 0;
        return logic.getSlots();
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        InventoryType type = getItemInventoryType();
        ItemInventoryLogic logic = getItemLogic(ForgeDirection.UNKNOWN, type == null ? InventoryType.Output : type);
        if (logic == null) return new int[0];
        int[] indexes = new int[logic.getSlots()];
        for (int i = 0; i < logic.getSlots(); i++) {
            indexes[i] = i;
        }
        return indexes;
    }

    @Override
    public boolean canExtractItem(int ignoredSlot, ItemStack ignoredItem, int side) {
        InventoryType type = getItemInventoryType();

        return getItemLogic(ForgeDirection.getOrientation(side), type == null ? InventoryType.Output : type) != null;
    }

    @Override
    public boolean canInsertItem(int ignoredSlot, ItemStack ignoredItem, int side) {
        InventoryType type = getItemInventoryType();

        return getItemInventoryType() != InventoryType.Output
            && getItemLogic(ForgeDirection.getOrientation(side), type == null ? InventoryType.Output : type) != null;
    }

    @Override
    protected void addTitleToUI(ModularWindow.Builder builder) {
        super.addTitleToUI(builder);
    }

    @Override
    protected void addDebugInfo(EntityPlayer aPlayer, int aLogLevel, ArrayList<String> tList) {
        tList.add("storage : " + storage.getSlots());
        tList.add("expected storage size : " + this.storageSize);
        tList.add("UI enables : " + this.useModularUI());
    }

    @Override
    public int getHeight() {
        //Slot Size 18px
        //Border sizes for player inventory 7px
        //Rendered Storage Inventory size will atleast be 1 slot heigh + border at max 6 slots height + border
        return Math.max((18 * 4 + 3 * 7 + 18), (18 * 4 + 3 * 7 + (Math.max(1, Math.min(6,this.storageSize / this.default_columns))) * 18 + 4));
    }


    @Override
    public ArrayList<ItemStack> getDrops(int aFortune, boolean aSilkTouch) {
        var drops = super.getDrops(aFortune, aSilkTouch);
        drops.addAll(this.storage.getInventory().getStacks());
        return drops;
    }



}


