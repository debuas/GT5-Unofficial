package gregtech.common.tileentities.storage;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL12.GL_RESCALE_NORMAL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.ITexture;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Storage_Chest;
import gregtech.api.multitileentity.storage.MetalChest;

public class GT_MetaTileEntity_MetalChest extends GT_MetaTileEntity_Storage_Chest {

    public GT_MetaTileEntity_MetalChest(int aID, String aBasicName, String aRegionalName, int aInvSlotCount,
        Materials aMaterial) {
        super(aID, aBasicName, aRegionalName, aInvSlotCount, aMaterial);
        rgba = aMaterial.getRGBA();
    }

    @SideOnly(Side.CLIENT)
    private static MetalChest.MultiTileEntityRendererChest RENDERER;

    @Override
    public ItemStack getAsItem() {
        return null;
    }

    @Override
    public String getMachineName() {
        return "Metal Chest";
    }

    @Override
    public long getTimer() {
        return 0;
    }

    @Override
    public void setLightValue(byte aLightValue) {}

    @Override
    public ITexture[] getTexture(Block aBlock, ForgeDirection side) {
        return new ITexture[0];
    }

    protected byte mFacing = 3, mUsingPlayers = 0, oUsingPlayers = 0;
    protected float mLidAngle = 0, oLidAngle = 0, mHardness = 6, mResistance = 3;

    private static final byte[] COMPASS_FROM_SIDE = { 0, 0, 0, 2, 3, 1, 0, 0 };

    short[] rgba;
    protected String mTextureName;


    @Override
    public boolean shouldDropItemAt(int index) {
        return true;
    }

    @Override
    public int getLightOpacity() {
        return 0;
    }

    @Override
    public void addCollisionBoxesToList(World aWorld, int aX, int aY, int aZ, AxisAlignedBB inputAABB, List<AxisAlignedBB> outputAABB, Entity collider) {

    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World aWorld, int aX, int aY, int aZ) {
        return null;
    }


    @SideOnly(Side.CLIENT)
    public static class MetaTileEntityRendererChest extends TileEntitySpecialRenderer {

        private static final MetalChest.MultiTileEntityModelChest sModel = new MetalChest.MultiTileEntityModelChest();
        public final Map<String, ResourceLocation[]> mResources = new HashMap<>();

        @Override
        public void renderTileEntityAt(TileEntity aTileEntity, double aX, double aY, double aZ, float aPartialTick) {
            if (aTileEntity instanceof GT_MetaTileEntity_MetalChest) {

                double tLidAngle = 1 - (((GT_MetaTileEntity_MetalChest) aTileEntity).oLidAngle
                    + (((GT_MetaTileEntity_MetalChest) aTileEntity).mLidAngle
                        - ((GT_MetaTileEntity_MetalChest) aTileEntity).oLidAngle) * aPartialTick);
                tLidAngle = -(((1 - tLidAngle * tLidAngle * tLidAngle) * Math.PI) / 2);
                ResourceLocation[] tLocation = mResources
                    .get(((GT_MetaTileEntity_MetalChest) aTileEntity).mTextureName);
                // ResourceLocation[] tLocation = (MetalChest)((GT_MetaTileEntity_MetalChest) aTileEntity).getTexture()
                bindTexture(tLocation[0]);
                glPushMatrix();
                glEnable(GL_BLEND);
                glEnable(GL_LIGHTING);
                glEnable(GL_ALPHA_TEST);
                glEnable(GL_RESCALE_NORMAL);
                glAlphaFunc(GL_GREATER, 0.1F);
                OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                short[] tRGBa = ((GT_MetaTileEntity_MetalChest) aTileEntity).rgba;
                glColor4f(tRGBa[0] / 255.0F, tRGBa[1] / 255.0F, tRGBa[2] / 255.0F, 1);
                glTranslated(aX, aY + 1, aZ + 1);
                glScalef(1, -1, -1);
                glTranslated(0.5, 0.5, 0.5);
                glRotatef(COMPASS_FROM_SIDE[((GT_MetaTileEntity_MetalChest) aTileEntity).mFacing] * 90 - 180, 0, 1, 0);
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
                glRotatef(COMPASS_FROM_SIDE[((GT_MetaTileEntity_MetalChest) aTileEntity).mFacing] * 90 - 180, 0, 1, 0);
                glTranslated(-0.5, -0.5, -0.5);
                sModel.render(tLidAngle);
                glDisable(GL_RESCALE_NORMAL);
                glPopMatrix();
                glEnable(GL_RESCALE_NORMAL);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static class MetaTileEntityModelChest extends ModelBase {

        private final ModelRenderer mLid, mBottom, mKnob;

        public MetaTileEntityModelChest() {
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
