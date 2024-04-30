package gregtech.api.metatileentity;

import com.gtnewhorizons.modularui.api.screen.ITileWithModularUI;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import gregtech.api.enums.InventoryType;
import gregtech.api.enums.Textures;
import gregtech.api.gui.GUIHost;
import gregtech.api.gui.GUIProvider;
import gregtech.api.interfaces.IConfigurationCircuitSupport;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.modularui.IGetGUITextureSet;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.interfaces.tileentity.IHasWorldObjectAndCoords;
import gregtech.api.logic.ItemInventoryLogic;
import gregtech.api.logic.interfaces.ItemInventoryLogicHost;
import gregtech.common.gui.InventoryGUIProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.function.Supplier;

public abstract class MetaTileEntityStorage
    extends BaseTileEntity
    implements ItemInventoryLogicHost,
    GUIHost,
    IGetGUITextureSet

{


    protected int storageSize = 27;
    protected int default_cols = 9;

    protected GUIProvider<?> guiProvider = createGUIProvider();

    protected Supplier<Boolean> getValidator() {
        return () -> !this.isDead();
    }

    @Nonnull
    protected ItemInventoryLogic storage;


    //ItemInventoryLogicHost

    @Nullable
    @Override
    public ItemInventoryLogic getItemLogic(@NotNull ForgeDirection side, @NotNull InventoryType type) {
        return storage;
    }

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
    public boolean isUseableByPlayer(@Nonnull EntityPlayer player) {
        return true;
    }


    //nbt

    @Override
    public void writeToNBT(NBTTagCompound aNBT) {
        super.writeToNBT(aNBT);
        saveItemLogic(aNBT);
    }

    @Override
    public void readFromNBT(NBTTagCompound aNBT) {
        super.readFromNBT(aNBT);
        loadItemLogic(aNBT);
    }

    //ItemLogic NBT
    protected void saveItemLogic(NBTTagCompound nbt) {
        NBTTagCompound nbtListInput = storage.saveToNBT();
        nbt.setTag("gt.inventory.storage.inv", nbtListInput);
        nbt.setInteger("gt.inventory.storage.size", this.storageSize);
    }

    protected void loadItemLogic(NBTTagCompound nbt) {
        if (nbt.hasKey("gt.inventory.storage.size")) {
            this.storageSize = nbt.getInteger("gt.inventory.storage.size");
        }
        storage = new ItemInventoryLogic(this.storageSize);

        if (nbt.hasKey("gt.inventory.storage.inv")) {
            storage.loadFromNBT(nbt.getCompoundTag("gt.inventory.storage.inv"));
        }


    }

    // Enet False

    @Override
    public boolean shouldJoinIc2Enet(){
        return false;
    }

    //ModularUI


    @Override
    public boolean useModularUI() {
        return true;
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
    public int getHeight() {
        //Slot Size 18px
        //Border sizes for player inventory 7px
        //Rendered Storage Inventory size will atleast be 1 slot heigh + border at max 6 slots height + border
        return Math.max((18 * 4 + 3 * 7 + 18), (18 * 4 + 3 * 7 + (Math.max(1, Math.min(6,this.storageSize / this.default_cols))) * 18 + 4));
    }

    //Texture


}
