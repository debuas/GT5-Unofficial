package gregtech.common.render;

import static gregtech.api.enums.Mods.GregTech;
import static org.lwjgl.opengl.GL11.GL_ALPHA_TEST;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_GREATER;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glAlphaFunc;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL12.GL_RESCALE_NORMAL;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.common.tileentities.storage.GT_MetaTileEntity_MetalChest;

public class GT_RenderChest extends TileEntitySpecialRenderer {

    private static final byte[] COMPASS_FROM_SIDE = { 0, 0, 0, 2, 3, 1, 0, 0 };
    private static final MetaTileEntityModelChest sModel = new MetaTileEntityModelChest();
    public final Map<String, ResourceLocation[]> mResources = new HashMap<>();

    public GT_RenderChest() {
        mResources.put(
            GT_MetaTileEntity_MetalChest.mTextureName,
            new ResourceLocation[] {
                new ResourceLocation(GregTech.ID, "textures/model/metatileentity/metalchest.colored.png"),
                new ResourceLocation(GregTech.ID, "textures/model/metatileentity/metalchest.plain.png") });
        ClientRegistry.bindTileEntitySpecialRenderer(GT_MetaTileEntity_MetalChest.class, this);
    }

    @Override
    public void renderTileEntityAt(TileEntity aTileEntity, double aX, double aY, double aZ, float aPartialTick) {
        if (aTileEntity instanceof GT_MetaTileEntity_MetalChest) {
            double tLidAngle = 1 - (((GT_MetaTileEntity_MetalChest) aTileEntity).oLidAngle
                + (((GT_MetaTileEntity_MetalChest) aTileEntity).mLidAngle
                    - ((GT_MetaTileEntity_MetalChest) aTileEntity).oLidAngle) * aPartialTick);
            tLidAngle = -(((1 - tLidAngle * tLidAngle * tLidAngle) * Math.PI) / 2);
            ResourceLocation[] tLocation = mResources.get(((GT_MetaTileEntity_MetalChest) aTileEntity).mTextureName);
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

            bindTexture(tLocation[1]);
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
