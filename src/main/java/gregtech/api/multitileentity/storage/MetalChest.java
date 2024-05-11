package gregtech.api.multitileentity.storage;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_RESCALE_NORMAL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gregtech.api.util.GT_Util;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Mods;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.multitileentity.MultiTileEntityRegistry;
import gregtech.api.render.TextureFactory;

// TODO("Get the chest Model working")
// TODO("Adjust chest texture to align with the Model")
// TODO("Work on the chest open animation")
// TODO("Play open/close sound for chest")

public class MetalChest extends MultiTileBasicStorage {

    // protected static final float[] PX_BOX = { 0.0625F, 0, 0.0625F, 0.9375F, 0.875F, 0.9375F };
    private static final float minX = 0.0625F, minY = 0F, minZ = 0.0625F, maxX = 0.9375F, maxY = 0.875F, maxZ = 0.9375F;
    private String mTextureName;


    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool() {
        return box(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool() {
        return box(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public void setBlockBoundsBasedOnState(Block aBlock) {
        box(aBlock, minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return super.getRenderBoundingBox();
    }

    @Override
    public ItemStack getAsItem() {
        return null;
    }

    @SideOnly(Side.CLIENT)
    private static MultiTileEntityRendererChest RENDERER;

    @Override
    public void onRegistrationFirst(MultiTileEntityRegistry registry, short id) {
        super.onRegistrationFirst(registry, id);

        ClientRegistry.bindTileEntitySpecialRenderer(getClass(), RENDERER = new MultiTileEntityRendererChest());

    }

    @Override
    public String getMachineName() {
        return StatCollector.translateToLocal(this.material.mDefaultLocalName) + " "
            + StatCollector.translateToLocal("Chest");
    }

    @Override
    public void setLightValue(byte aLightValue) {

    }

    @Override
    public void readMultiTileNBT(NBTTagCompound nbt) {
        super.readMultiTileNBT(nbt);
    }

    @Override
    public byte getComparatorValue(ForgeDirection side) {
        return 0;
    }

    @Override
    public String getTileEntityName() {
        return "gt.multitileentity.storage.metalchest";
    }

    @Override
    protected void addDebugInfo(EntityPlayer aPlayer, int aLogLevel, ArrayList<String> tList) {
        super.addDebugInfo(aPlayer, aLogLevel, tList);
        tList.add("Material : " + this.material);
    }

    // Textures


    private ITexture baseTexture = null;
    private ITexture topOverlayTexture = null;
    private ITexture bottomOverlayTexture = null;
    private ITexture leftOverlayTexture = null;
    private ITexture rightOverlayTexture = null;
    private ITexture backOverlayTexture = null;
    private ITexture frontOverlayTexture = null;


    protected byte mFacing = 3, mUsingPlayers = 0, oUsingPlayers = 0;
    protected float mLidAngle = 0, oLidAngle = 0, mHardness = 6, mResistance = 3;

    private static final byte[] COMPASS_FROM_SIDE = { 0, 0, 0, 2, 3, 1, 0, 0 };

    @Override
    public void loadTextures(String folder) {
        // Loading the registry
        for (SidedTextureNames textureName : SidedTextureNames.TEXTURES) {
            ITexture texture;
            try {
                Minecraft.getMinecraft()
                    .getResourceManager()
                    .getResource(
                        new ResourceLocation(
                            Mods.GregTech.ID,
                            "textures/blocks/multitileentity/" + folder + "/" + textureName.getName() + ".png"));
                texture = TextureFactory.of(
                    new Textures.BlockIcons.CustomIcon("multitileentity/" + folder + "/" + textureName.getName()),
                    this.material.getRGBA());
            } catch (IOException ignored) {
                texture = TextureFactory.of(Textures.BlockIcons.VOID);
            }
            switch (textureName) {
                case Top -> this.topOverlayTexture = texture;
                case Bottom -> this.bottomOverlayTexture = texture;
                case Back -> this.backOverlayTexture = texture;
                case Front -> this.frontOverlayTexture = texture;
                case Left -> this.leftOverlayTexture = texture;
                case Right -> this.rightOverlayTexture = texture;
                case Base -> this.baseTexture = texture;
            }
        }

    }

    @Override
    public void copyTextures() {
        // Loading an instance
        final TileEntity tCanonicalTileEntity = MultiTileEntityRegistry
            .getCanonicalTileEntity(getMultiTileEntityRegistryID(), getMultiTileEntityID());
        if (!(tCanonicalTileEntity instanceof MetalChest)) {
            return;
        }
        final MetalChest canonicalEntity = (MetalChest) tCanonicalTileEntity;
        baseTexture = canonicalEntity.baseTexture;
        topOverlayTexture = canonicalEntity.topOverlayTexture;
        bottomOverlayTexture = canonicalEntity.bottomOverlayTexture;
        leftOverlayTexture = canonicalEntity.leftOverlayTexture;
        rightOverlayTexture = canonicalEntity.rightOverlayTexture;
        backOverlayTexture = canonicalEntity.backOverlayTexture;
        frontOverlayTexture = canonicalEntity.frontOverlayTexture;
    }

    @Override
    public ITexture getTexture(ForgeDirection side) {
        if (facing == side) {
            return TextureFactory.of(baseTexture, frontOverlayTexture);
        }

        if (facing.getOpposite() == side) {
            return TextureFactory.of(baseTexture, backOverlayTexture);
        }

        if (side == ForgeDirection.UP) {
            return TextureFactory.of(baseTexture, topOverlayTexture);
        }

        if (side == ForgeDirection.DOWN) {
            return TextureFactory.of(baseTexture, bottomOverlayTexture);
        }

        if (facing.getRotation(ForgeDirection.DOWN) == side) {
            return TextureFactory.of(baseTexture, rightOverlayTexture);
        } else {
            return TextureFactory.of(baseTexture, leftOverlayTexture);
        }
    }

    @SideOnly(Side.CLIENT)
    public static class MultiTileEntityRendererChest extends TileEntitySpecialRenderer {

        private static final MultiTileEntityModelChest sModel = new MultiTileEntityModelChest();
        public final Map<String, ResourceLocation[]> mResources = new HashMap<>();

        @Override
        public void renderTileEntityAt(TileEntity aTileEntity, double aX, double aY, double aZ, float aPartialTick) {
            if (aTileEntity instanceof MetalChest) {

                double tLidAngle = 1 - (((MetalChest) aTileEntity).oLidAngle
                    + (((MetalChest) aTileEntity).mLidAngle - ((MetalChest) aTileEntity).oLidAngle) * aPartialTick);
                tLidAngle = -(((1 - tLidAngle * tLidAngle * tLidAngle) * Math.PI) / 2);
                ResourceLocation[] tLocation = mResources.get(((MetalChest)aTileEntity).mTextureName);
                bindTexture(tLocation[0]);
                glPushMatrix();
                glEnable(GL_BLEND);
                glEnable(GL_LIGHTING);
                glEnable(GL_ALPHA_TEST);
                glEnable(GL_RESCALE_NORMAL);
                glAlphaFunc(GL_GREATER, 0.1F);
                OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                short[] tRGBa = GT_Util.getRGBaArray(((MetalChest)aTileEntity).rgba);
                glColor4f(tRGBa[0] / 255.0F, tRGBa[1] / 255.0F, tRGBa[2] / 255.0F, 1);
                glTranslated(aX, aY + 1, aZ + 1);
                glScalef(1, -1, -1);
                glTranslated(0.5, 0.5, 0.5);
                glRotatef(COMPASS_FROM_SIDE[((MetalChest) aTileEntity).mFacing] * 90 - 180, 0, 1, 0);
                glTranslated(-0.5, -0.5, -0.5);
                sModel.render(tLidAngle);
                glDisable(GL_RESCALE_NORMAL);
                glPopMatrix();
                glEnable(GL_RESCALE_NORMAL);
                glColor4f(1, 1, 1, 1);

                // bindTexture(tLocation[1]);
                glPushMatrix();
                glTranslated(aX, aY + 1, aZ + 1);
                glScalef(1, -1, -1);
                glTranslated(0.5, 0.5, 0.5);
                glRotatef(COMPASS_FROM_SIDE[((MetalChest) aTileEntity).mFacing] * 90 - 180, 0, 1, 0);
                glTranslated(-0.5, -0.5, -0.5);
                sModel.render(tLidAngle);
                glDisable(GL_RESCALE_NORMAL);
                glPopMatrix();
                glEnable(GL_RESCALE_NORMAL);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static class MultiTileEntityModelChest extends ModelBase {

        private final ModelRenderer mLid, mBottom, mKnob;

        public MultiTileEntityModelChest() {
            mLid = (new ModelRenderer(this, 0, 0)).setTextureSize(64, 64);
            mLid.addBox(0, -5, -14, 14, 5, 14, 0);
            mLid.rotationPointX = 1;
            mLid.rotationPointY = 7;
            mLid.rotationPointZ = 15;
            mKnob = (new ModelRenderer(this, 0, 0)).setTextureSize(64, 64);
            mKnob.addBox(-1, -2, -15, 2, 4, 1, 0);
            mKnob.rotationPointX = 8;
            mKnob.rotationPointY = 7;
            mKnob.rotationPointZ = 15;
            mBottom = (new ModelRenderer(this, 0, 19)).setTextureSize(64, 64);
            mBottom.addBox(0, 0, 0, 14, 10, 14, 0);
            mBottom.rotationPointX = 1;
            mBottom.rotationPointY = 6;
            mBottom.rotationPointZ = 1;
        }

        public void render(double aLidAngle) {
            mKnob.rotateAngleX = mLid.rotateAngleX = (float) aLidAngle;
            mLid.render(0.0625F);
            mKnob.render(0.0625F);
            mBottom.render(0.0625F);
        }
    }

}
