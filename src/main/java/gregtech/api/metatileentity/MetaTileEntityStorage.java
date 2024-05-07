package gregtech.api.metatileentity;

import static gregtech.GT_Mod.GT_FML_LOGGER;

import java.util.UUID;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gtnewhorizons.modularui.api.screen.UIBuildContext;

import gregtech.GT_Mod;
import gregtech.api.GregTech_API;
import gregtech.api.enums.InventoryType;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.SoundResource;
import gregtech.api.gui.GUIHost;
import gregtech.api.gui.GUIProvider;
import gregtech.api.gui.modularui.GT_UIInfos;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.modularui.IGetGUITextureSet;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ItemInventoryLogic;
import gregtech.api.logic.interfaces.ItemInventoryLogicHost;
import gregtech.api.util.*;
import gregtech.common.gui.InventoryGUIProvider;

public abstract class MetaTileEntityStorage extends BaseTileEntity
    implements ItemInventoryLogicHost, GUIHost, IGetGUITextureSet, IGregTechTileEntity

{

    protected int storageSize = 27;
    protected int default_columns = 9;
    protected byte storedColor;
    protected ForgeDirection frontFacing;
    protected ForgeDirection facing;

    protected GUIProvider<?> guiProvider = createGUIProvider();
    protected IGregTechTileEntity mBaseMetaTileEntity;
    protected IMetaTileEntity mMetaTileEntity;

    protected Supplier<Boolean> getValidator() {
        return () -> !this.isDead();
    }

    protected int mId;
    @Nonnull
    protected ItemInventoryLogic storage;

    // ItemInventoryLogicHost

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

    // nbt

    @Override
    public void setInitialValuesAsNBT(NBTTagCompound aNBT, short aID) {
        writeToNBT(aNBT);
    }

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

    // ItemLogic NBT
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

    @Override
    public void setMetaTileEntity(IMetaTileEntity aMetaTileEntity) {
        if (aMetaTileEntity instanceof MetaTileEntityStorage || aMetaTileEntity == null)
            mMetaTileEntity = aMetaTileEntity;
        else {
            GT_FML_LOGGER.error(
                "Unknown meta tile entity set! Class {}, inventory name {}.",
                aMetaTileEntity.getClass(),
                aMetaTileEntity.getInventoryName());
        }
    }

    @Override
    public void onLeftclick(EntityPlayer aPlayer) {

    }

    // interaction
    @Override
    public boolean onRightclick(EntityPlayer aPlayer, ForgeDirection side, float aX, float aY, float aZ) {
        if (isClientSide()) {
            // Configure Cover, sneak can also be: screwdriver, wrench, side cutter, soldering iron
            if (aPlayer.isSneaking()) {
                final ForgeDirection tSide = (getCoverIDAtSide(side) == 0)
                    ? GT_Utility.determineWrenchingSide(side, aX, aY, aZ)
                    : side;
                return (getCoverBehaviorAtSideNew(tSide).hasCoverGUI());
            } else if (getCoverBehaviorAtSideNew(side).onCoverRightclickClient(side, this, aPlayer, aX, aY, aZ)) {
                return true;
            }

            if (!getCoverInfoAtSide(side).isGUIClickable()) return false;
        }

        if (isServerSide()) {
            {
                final ItemStack tCurrentItem = aPlayer.inventory.getCurrentItem();
                if (tCurrentItem != null) {
                    if (getColorization() >= 0
                        && GT_Utility.areStacksEqual(new ItemStack(Items.water_bucket, 1), tCurrentItem)) {
                        tCurrentItem.func_150996_a(Items.bucket);
                        setColorization((byte) (getColorization() >= 16 ? -2 : -1));
                        return true;
                    }
                    if (GT_Utility.isStackInList(tCurrentItem, GregTech_API.sWrenchList)) {
                        if (aPlayer.isSneaking() && mBaseMetaTileEntity instanceof MetaTileEntityStorage
                            && ((MetaTileEntityStorage) mBaseMetaTileEntity)
                                .setMainFacing(GT_Utility.determineWrenchingSide(side, aX, aY, aZ))) {
                            GT_ModHandler.damageOrDechargeItem(tCurrentItem, 1, 1000, aPlayer);
                            GT_Utility.sendSoundToPlayers(
                                worldObj,
                                SoundResource.IC2_TOOLS_WRENCH,
                                1.0F,
                                -1,
                                xCoord,
                                yCoord,
                                zCoord);
                        } else if (onWrenchRightClick(
                            side,
                            GT_Utility.determineWrenchingSide(side, aX, aY, aZ),
                            aPlayer,
                            aX,
                            aY,
                            aZ,
                            tCurrentItem)) {
                                GT_ModHandler.damageOrDechargeItem(tCurrentItem, 1, 1000, aPlayer);
                                GT_Utility.sendSoundToPlayers(
                                    worldObj,
                                    SoundResource.IC2_TOOLS_WRENCH,
                                    1.0F,
                                    -1,
                                    xCoord,
                                    yCoord,
                                    zCoord);
                            }
                        return true;
                    }

                    if (GT_Utility.isStackInList(tCurrentItem, GregTech_API.sScrewdriverList)) {
                        if (GT_ModHandler.damageOrDechargeItem(tCurrentItem, 1, 200, aPlayer)) {
                            setCoverDataAtSide(
                                side,
                                getCoverBehaviorAtSideNew(side).onCoverScrewdriverClick(
                                    side,
                                    getCoverIDAtSide(side),
                                    getComplexCoverDataAtSide(side),
                                    this,
                                    aPlayer,
                                    aX,
                                    aY,
                                    aZ));
                            onScrewdriverRightClick(side, aPlayer, aX, aY, aZ, tCurrentItem);
                            GT_Utility.sendSoundToPlayers(
                                worldObj,
                                SoundResource.IC2_TOOLS_WRENCH,
                                1.0F,
                                -1,
                                xCoord,
                                yCoord,
                                zCoord);
                        }
                        return true;
                    }

                    /*
                     * //Cover Logic
                     * ForgeDirection coverSide = side;
                     * if (getCoverIDAtSide(side) == 0) coverSide = GT_Utility.determineWrenchingSide(side, aX, aY, aZ);
                     * if (getCoverIDAtSide(coverSide) == 0) {
                     * if (false %%GT_Utility.isStackInList(tCurrentItem, GregTech_API.sCovers.keySet())) {
                     * final GT_CoverBehaviorBase<?> coverBehavior = GregTech_API
                     * .getCoverBehaviorNew(tCurrentItem);
                     * if (coverBehavior.isCoverPlaceable(coverSide, tCurrentItem, this)
                     * && mMetaTileEntity.allowCoverOnSide(coverSide, new GT_ItemStack(tCurrentItem))) {
                     * setCoverItemAtSide(coverSide, tCurrentItem);
                     * coverBehavior.onPlayerAttach(aPlayer, tCurrentItem, this, coverSide);
                     * if (!aPlayer.capabilities.isCreativeMode) tCurrentItem.stackSize--;
                     * GT_Utility.sendSoundToPlayers(
                     * worldObj,
                     * SoundResource.IC2_TOOLS_WRENCH,
                     * 1.0F,
                     * -1,
                     * xCoord,
                     * yCoord,
                     * zCoord);
                     * sendClientData();
                     * }
                     * return true;
                     * }
                     * } else {
                     * if (GT_Utility.isStackInList(tCurrentItem, GregTech_API.sCrowbarList)) {
                     * if (GT_ModHandler.damageOrDechargeItem(tCurrentItem, 1, 1000, aPlayer)) {
                     * GT_Utility.sendSoundToPlayers(
                     * worldObj,
                     * SoundResource.RANDOM_BREAK,
                     * 1.0F,
                     * -1,
                     * xCoord,
                     * yCoord,
                     * zCoord);
                     * dropCover(coverSide, side, false);
                     * }
                     * return true;
                     * } else if (GT_Utility.isStackInList(tCurrentItem, GregTech_API.sJackhammerList)) {
                     * // Configuration of delicate electronics calls for a tool with precision and subtlety.
                     * if (GT_ModHandler.damageOrDechargeItem(tCurrentItem, 1, 1000, aPlayer)) {
                     * final CoverInfo info = getCoverInfoAtSide(coverSide);
                     * if (info != CoverInfo.EMPTY_INFO) {
                     * final GT_CoverBehaviorBase<?> behavior = info.getCoverBehavior();
                     * if (behavior.allowsTickRateAddition()) {
                     * info.onCoverJackhammer(aPlayer);
                     * GT_Utility.sendSoundToPlayers(
                     * worldObj,
                     * SoundResource.IC2_TOOLS_DRILL_DRILL_SOFT,
                     * 1.0F,
                     * 1,
                     * xCoord,
                     * yCoord,
                     * zCoord);
                     * } else {
                     * GT_Utility.sendChatToPlayer(
                     * aPlayer,
                     * StatCollector.translateToLocal("gt.cover.info.chat.tick_rate_not_allowed"));
                     * }
                     * return true;
                     * }
                     * }
                     * }
                     * }
                     */
                    // End item != null
                } else if (aPlayer.isSneaking()) { // Sneak click, no tool -> open cover config if possible.
                    side = (getCoverIDAtSide(side) == 0) ? GT_Utility.determineWrenchingSide(side, aX, aY, aZ) : side;
                    return getCoverIDAtSide(side) > 0 && getCoverBehaviorAtSideNew(side).onCoverShiftRightClick(
                        side,
                        getCoverIDAtSide(side),
                        getComplexCoverDataAtSide(side),
                        this,
                        aPlayer);
                }

                if (getCoverBehaviorAtSideNew(side).onCoverRightClick(
                    side,
                    getCoverIDAtSide(side),
                    getComplexCoverDataAtSide(side),
                    this,
                    aPlayer,
                    aX,
                    aY,
                    aZ)) return true;

                if (!getCoverInfoAtSide(side).isGUIClickable()) return false;

                if (isUpgradable() && tCurrentItem != null) {
                    if (ItemList.Upgrade_Muffler.isStackEqual(aPlayer.inventory.getCurrentItem())) {
                        if (addMufflerUpgrade()) {
                            GT_Utility.sendSoundToPlayers(
                                worldObj,
                                SoundResource.RANDOM_CLICK,
                                1.0F,
                                -1,
                                xCoord,
                                yCoord,
                                zCoord);
                            if (!aPlayer.capabilities.isCreativeMode) aPlayer.inventory.getCurrentItem().stackSize--;
                        }
                        return true;
                    }
                }
            }
        }

        try {
            if (!aPlayer.isSneaking() && mBaseMetaTileEntity != null && mBaseMetaTileEntity == this)
                return this.onRightclick(this, aPlayer, side, aX, aY, aZ);
        } catch (Throwable e) {
            GT_Log.err.println(
                "Encountered Exception while rightclicking TileEntity, the Game should've crashed now, but I prevented that. Please report immediately to GregTech Intergalactical!!!");
            e.printStackTrace(GT_Log.err);
            e.printStackTrace();
        }

        return false;
    }

    private boolean setMainFacing(ForgeDirection forgeDirection) {
        return false;
    }

    private boolean onWrenchRightClick(ForgeDirection side, ForgeDirection forgeDirection, EntityPlayer aPlayer,
        float aX, float aY, float aZ, ItemStack tCurrentItem) {
        return false;
    }

    private boolean onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack tCurrentItem) {
        return false;
    }

    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer, ForgeDirection side,
        float aX, float aY, float aZ) {
        return onRightclick(aBaseMetaTileEntity, aPlayer);
    }

    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (aBaseMetaTileEntity.isClientSide()) return true;
        if (!GT_Mod.gregtechproxy.mForceFreeFace) {
            GT_UIInfos.openGTTileEntityUI(aBaseMetaTileEntity, aPlayer);
            return true;
        }
        for (final ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            if (aBaseMetaTileEntity.getAirAtSide(side)) {
                GT_UIInfos.openGTTileEntityUI(aBaseMetaTileEntity, aPlayer);
                return true;
            }
        }
        GT_Utility.sendChatToPlayer(aPlayer, "No free Side!");
        return true;
    }

    // Enet False

    @Override
    public boolean shouldJoinIc2Enet() {
        return false;
    }

    // ModularUI

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
        // Slot Size 18px
        // Border sizes for player inventory 7px
        // Rendered Storage Inventory size will atleast be 1 slot heigh + border at max 6 slots height + border
        return Math.max(
            (18 * 4 + 3 * 7 + 18),
            (18 * 4 + 3 * 7 + (Math.max(1, Math.min(6, this.storageSize / this.default_columns))) * 18 + 4));
    }

    // Texture

    // IGregtechTileEntity

    public int getErrorDisplayID() {
        return 0;
    }

    public void setErrorDisplayID(int aErrorID) {};

    public int getMetaTileID() {
        return mId;
    }

    public int setMetaTileID(short aID) {
        mId = (int) aID;
        return mId;
    }

    @Override
    public String getOwnerName() {
        return "";
    }

    @Override
    public UUID getOwnerUuid() {
        return null;
    }

    public String setOwnerName(String aName) {
        return aName;
    };

    @Override
    public void onEntityCollidedWithBlock(World aWorld, int aX, int aY, int aZ, Entity collider) {

    }

    @Override
    public IMetaTileEntity getMetaTileEntity() {
        return mMetaTileEntity;
    }

    @Override
    public void issueTextureUpdate() {

    }

    @Override
    public void issueClientUpdate() {

    }

    @Override
    public void doExplosion(long aExplosionEU) {

    }

    @Override
    public void setOwnerUuid(UUID uuid) {

    }

    // Cover Blank for now
    @Override
    public byte getInternalInputRedstoneSignal(ForgeDirection side) {
        return 0;
    }

    /**
     * For use by the regular MetaTileEntities. This makes it not conflict with Cover based Redstone Signals. Don't use
     * this if you are a Cover Behavior. Only for MetaTileEntities.
     */
    @Override
    public void setInternalOutputRedstoneSignal(ForgeDirection side, byte aStrength) {

    }

    /**
     * Causes a general Cover Texture update. Sends 6 Integers to Client + causes @issueTextureUpdate()
     */
    public void issueCoverUpdate(ForgeDirection side) {

    }

    /**
     * Receiving a packet with cover data.
     */
    public void receiveCoverData(ForgeDirection coverSide, int coverID, int coverData) {

    }

    public int getCoverIDAtSide(ForgeDirection side) {
        return 0;
    }

    public ItemStack getCoverItemAtSide(ForgeDirection side) {
        return null;
    }

    public GT_CoverBehavior getCoverBehaviorAtSide(ForgeDirection side) {
        return null;
    }

    public void setCoverIdAndDataAtSide(ForgeDirection side, int aId, ISerializableObject aData) {

    }

    public void setCoverIDAtSide(ForgeDirection side, int aID) {

    }

    public boolean setCoverIDAtSideNoUpdate(ForgeDirection side, int aID) {
        return false;
    }

    public void setCoverItemAtSide(ForgeDirection side, ItemStack aCover) {

    }

    public int getCoverDataAtSide(ForgeDirection side) {
        return 0;
    }

    public boolean canPlaceCoverIDAtSide(ForgeDirection side, int aID) {
        return false;
    }

    public boolean canPlaceCoverItemAtSide(ForgeDirection side, ItemStack aCover) {
        return false;
    }

    public boolean dropCover(ForgeDirection side, ForgeDirection droppedSide, boolean aForced) {
        return false;
    }

    public void setCoverDataAtSide(ForgeDirection side, int aData) {

    }

    // GearEnergyTile

    public boolean acceptsRotationalEnergy(ForgeDirection side) {
        return false;
    }

    public boolean injectRotationalEnergy(ForgeDirection side, long aSpeed, long aEnergy) {
        return false;
    }

    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return 0;
    }

    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return null;
    }

    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return null;
    }

    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return false;
    }

    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return null;
    }

    public boolean isUniversalEnergyStored(long aEnergyAmount) {
        return false;
    }

    /**
     * Gets the stored electric, kinetic or steam Energy (with EU as reference Value) Always returns the largest one.
     */
    public long getUniversalEnergyStored() {
        return 0;
    }

    /**
     * Gets the largest electric, kinetic or steam Energy Capacity (with EU as reference Value)
     */
    public long getUniversalEnergyCapacity() {
        return 0;
    }

    /**
     * Gets the amount of Energy Packets per tick.
     */
    public long getOutputAmperage() {
        return 0;
    }

    /**
     * Gets the Output in EU/p.
     */
    public long getOutputVoltage() {
        return 0;
    }

    /**
     * Gets the amount of Energy Packets per tick.
     */
    public long getInputAmperage() {
        return 0;
    }

    /**
     * Gets the maximum Input in EU/p.
     */
    public long getInputVoltage() {
        return 0;
    }

    /**
     * Decreases the Amount of stored universal Energy. If ignoring too less Energy, then it just sets the Energy to 0
     * and returns false.
     */
    public boolean decreaseStoredEnergyUnits(long aEnergy, boolean aIgnoreTooLessEnergy) {
        return false;
    }

    /**
     * Increases the Amount of stored electric Energy. If ignoring too much Energy, then the Energy Limit is just being
     * ignored.
     */
    public boolean increaseStoredEnergyUnits(long aEnergy, boolean aIgnoreTooMuchEnergy) {
        return false;
    }

    /**
     * Drain Energy Call for Electricity.
     */
    public boolean drainEnergyUnits(ForgeDirection side, long aVoltage, long aAmperage) {
        return false;
    }

    /**
     * returns the amount of Electricity, accepted by this Block the last 5 ticks as Average.
     */
    public long getAverageElectricInput() {
        return 0;
    }

    /**
     * returns the amount of Electricity, outputted by this Block the last 5 ticks as Average.
     */
    public long getAverageElectricOutput() {
        return 0;
    }

    /**
     * returns the amount of electricity contained in this Block, in EU units!
     */
    public long getStoredEU() {
        return 0;
    }

    /**
     * returns the amount of electricity containable in this Block, in EU units!
     */
    @Override
    public long getEUCapacity() {
        return 0;
    }

    /**
     * if the Inventory of this TileEntity got modified this tick
     */
    public boolean hasInventoryBeenModified() {
        return false;
    }

    /**
     * if this is just a Holoslot
     */
    public boolean isValidSlot(int aIndex) {
        return false;
    }

    /**
     * Tries to add a Stack to the Slot. It doesn't matter if the Slot is valid or invalid as described at the Function
     * above.
     *
     * @return true if aStack == null, then false if aIndex is out of bounds, then false if aStack cannot be added, and
     *         then true if aStack has been added
     */
    public boolean addStackToSlot(int aIndex, ItemStack aStack) {
        return false;
    }

    /**
     * Tries to add X Items of a Stack to the Slot. It doesn't matter if the Slot is valid or invalid as described at
     * the Function above.
     *
     * @return true if aStack == null, then false if aIndex is out of bounds, then false if aStack cannot be added, and
     *         then true if aStack has been added
     */
    public boolean addStackToSlot(int aIndex, ItemStack aStack, int aAmount) {
        return false;
    }

    public void setGenericRedstoneOutput(boolean aOnOff) {

    }

    /**
     * Causes a general Block update. Sends nothing to Client, just causes a Block Update.
     */
    public void issueBlockUpdate() {

    }

    /**
     * gets the Redstone Level the TileEntity should emit to the given Output Side
     *
     * @param side the {@link ForgeDirection} side
     * @return the Redstone Level the TileEntity
     */
    public byte getOutputRedstoneSignal(ForgeDirection side) {
        return 0;
    }

    /**
     * sets the Redstone Level the TileEntity should emit to the given Output Side
     * <p/>
     * Do not use this if ICoverable is implemented. ICoverable has @getInternalOutputRedstoneSignal for Machine
     * internal Output Redstone, so that it doesnt conflict with Cover Redstone. This sets the true Redstone Output
     * Signal. Only Cover Behaviors should use it, not MetaTileEntities.
     */
    public void setOutputRedstoneSignal(ForgeDirection side, byte aStrength) {

    }

    /**
     * gets the Redstone Level the TileEntity should emit to the given Output Side
     */
    public byte getStrongOutputRedstoneSignal(ForgeDirection side) {
        return 0;
    }

    /**
     * sets the Redstone Level the TileEntity should emit to the given Output Side
     * <p/>
     * Do not use this if ICoverable is implemented. ICoverable has @getInternalOutputRedstoneSignal for Machine
     * internal Output Redstone, so that it doesnt conflict with Cover Redstone. This sets the true Redstone Output
     * Signal. Only Cover Behaviors should use it, not MetaTileEntities.
     */
    public void setStrongOutputRedstoneSignal(ForgeDirection side, byte aStrength) {

    }

    /**
     * Gets the Output for the comparator on the given Side
     */
    public byte getComparatorValue(ForgeDirection side) {
        return 0;
    }

    /**
     * gets the Redstone Level of the TileEntity to the given Input Side
     * <p/>
     * Do not use this if ICoverable is implemented. ICoverable has @getInternalInputRedstoneSignal for Machine internal
     * Input Redstone This returns the true incoming Redstone Signal. Only Cover Behaviors should check it, not
     * MetaTileEntities.
     */
    public byte getInputRedstoneSignal(ForgeDirection side) {
        return 0;
    }

    /**
     * gets the strongest Redstone Level the TileEntity receives
     */
    public byte getStrongestRedstone() {
        return 0;
    }

    /**
     * gets if the TileEntity receives Redstone
     */
    public boolean getRedstone() {
        return false;
    }

    /**
     * gets if the TileEntity receives Redstone at this Side
     */
    public boolean getRedstone(ForgeDirection side) {
        return false;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isMufflerUpgradable() {
        return false;
    }

    @Override
    public boolean isSteamEngineUpgradable() {
        return false;
    }

    @Override
    public boolean addMufflerUpgrade() {
        return false;
    }

    @Override
    public boolean addSteamEngineUpgrade() {
        return false;
    }

    @Override
    public boolean hasMufflerUpgrade() {
        return false;
    }

    @Override
    public boolean hasSteamEngineUpgrade() {
        return false;
    }

    @Override
    public int getProgress() {
        return 0;
    }

    @Override
    public int getMaxProgress() {
        return 0;
    }

    @Override
    public boolean increaseProgress(int aProgressAmountInTicks) {
        return false;
    }

    @Override
    public boolean hasThingsToDo() {
        return false;
    }

    @Override
    public boolean hasWorkJustBeenEnabled() {
        return false;
    }

    @Override
    public void enableWorking() {

    }

    @Override
    public void disableWorking() {

    }

    @Override
    public boolean isAllowedToWork() {
        return false;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void setActive(boolean aActive) {

    }

    @Override
    public boolean isDigitalChest() {
        return false;
    }

    @Override
    public ItemStack[] getStoredItemData() {
        return new ItemStack[0];
    }

    @Override
    public void setItemCount(int aCount) {

    }

    @Override
    public int getMaxItemCount() {
        return 0;
    }

    @Override
    public boolean isGivingInformation() {
        return false;
    }

    @Override
    public String[] getInfoData() {
        return new String[0];
    }

    @Override
    public byte getColorization() {
        return storedColor;
    }

    @Override
    public byte setColorization(byte aColor) {
        storedColor = aColor;
        return storedColor;
    }

    @Override
    public float getBlastResistance(ForgeDirection side) {
        return 0;
    }

    @Override
    public long injectEnergyUnits(ForgeDirection side, long aVoltage, long aAmperage) {
        return 0;
    }

    @Override
    public boolean inputEnergyFrom(ForgeDirection side) {
        return false;
    }

    @Override
    public boolean outputsEnergyTo(ForgeDirection side) {
        return false;
    }

    @Override
    public String[] getDescription() {
        return new String[0];
    }

    @Override
    public ForgeDirection getFrontFacing() {
        return frontFacing;
    }

    public void setFacing(ForgeDirection facing) {
        this.facing = facing;
    }

    @Override
    public void setFrontFacing(ForgeDirection frontFacing) {
        this.frontFacing = frontFacing;
    }

    public ForgeDirection getFacing() {
        return facing;
    }

    @Override
    public ForgeDirection getBackFacing() {
        return getFrontFacing().getOpposite();
    }

    @Override
    public boolean isValidFacing(ForgeDirection side) {
        return true;
    }

}
