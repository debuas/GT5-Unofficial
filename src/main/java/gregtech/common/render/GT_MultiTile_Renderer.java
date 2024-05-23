package gregtech.common.render;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import gregtech.GT_Mod;
import gregtech.api.interfaces.ITexture;
import gregtech.api.logic.ModelRenderLogic;
import gregtech.api.logic.interfaces.ModelRenderLogicHost;
import gregtech.api.multitileentity.MultiTileEntityBlock;
import gregtech.api.multitileentity.MultiTileEntityRegistry;
import gregtech.api.multitileentity.interfaces.IMultiBlockController;
import gregtech.api.multitileentity.multiblock.base.MultiBlockPart;

public class GT_MultiTile_Renderer implements ISimpleBlockRenderingHandler {

    private final int renderID;
    public static GT_MultiTile_Renderer INSTANCE;
    static boolean tNeedsToSetBounds = true;
    public static boolean OPTIFINE_LOADED = false, GT_ALPHA_BLENDING = false, MC_ALPHA_BLENDING = false, IS_RENDERING_ALPHA = false;
    public static float OFFSET_X_POS = 0.0F, OFFSET_X_NEG = 0.0F, OFFSET_Y_POS = 0.0F, OFFSET_Y_NEG = 0.0F, OFFSET_Z_POS = 0.0F, OFFSET_Z_NEG = 0.0F, OFFSET_DEFAULT = 0.0F, OFFSET_ADD = 0.0001F, OFFSET_BREAK = 0.0001F;

    public GT_MultiTile_Renderer() {
        this.renderID = RenderingRegistry.getNextAvailableRenderId();
        INSTANCE = this;
        RenderingRegistry.registerBlockHandler(this);
    }

    @Override
    public void renderInventoryBlock(Block aBlock, int metadata, int modelId, RenderBlocks aRenderer) {
        ItemStack aStack = new ItemStack(aBlock, 1, metadata);
        startRendering(aRenderer, aBlock, null, 0, 0, 0);
        if (!(aBlock instanceof MultiTileEntityBlock mteBlock)) {
            return;
        }

        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        MultiTileEntityRegistry registry = mteBlock.getRegistry();
        if (registry == null) return;
        aRenderer.setRenderBoundsFromBlock(mteBlock);

        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            ITexture texture = registry.getCachedTileEntity(metadata).getTexture(side);

            if (((IRenderedBlock)aBlock).setBlockBounds(mteBlock.getRenderBlockPass(), aStack)) {
                tNeedsToSetBounds = true;
                aRenderer.setRenderBoundsFromBlock(aBlock);
            } else {
                if (tNeedsToSetBounds) aBlock.setBlockBounds(0, 0, 0, 1, 1, 1);
                aRenderer.setRenderBoundsFromBlock(aBlock);
                tNeedsToSetBounds = false;
            }
            if (texture == null) continue;
            switch (side) {
                case DOWN:
                    renderYNegative(null, aRenderer, aBlock, 0, 0, 0, texture, !tNeedsToSetBounds, true, aBlock);
                case UP:
                    renderYPositive(null, aRenderer, aBlock, 0, 0, 0, texture, !tNeedsToSetBounds, true, aBlock);
                case WEST:
                    renderXNegative(null, aRenderer, aBlock, 0, 0, 0, texture, !tNeedsToSetBounds, true, aBlock);
                case EAST:
                    renderXPositive(null, aRenderer, aBlock, 0, 0, 0, texture, !tNeedsToSetBounds, true, aBlock);
                case NORTH:
                    renderZNegative(null, aRenderer, aBlock, 0, 0, 0, texture, !tNeedsToSetBounds, true, aBlock);
                case SOUTH:
                    renderZPositive(null, aRenderer, aBlock, 0, 0, 0, texture, !tNeedsToSetBounds, true, aBlock);
                default: {
                    // Do nothing
                }
            }
            if (tNeedsToSetBounds) aBlock.setBlockBounds(0, 0, 0, 1, 1, 1);
        }
        aRenderer.setRenderBoundsFromBlock(aBlock);
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        endRendering(aRenderer, aBlock, null, 0, 0, 0);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess aWorld, int aX, int aY, int aZ, Block aBlock, int aModelId, RenderBlocks aRenderer) {
        Tessellator.instance.setBrightness(983055);
        boolean rReturn = false;
        aRenderer.setRenderBoundsFromBlock(aBlock);
        startRendering(aRenderer, aBlock, aWorld, aX, aY, aZ);

        TileEntity entity = aWorld.getTileEntity(aX, aY, aZ);
        if (entity == null) {
            return false;
        }

        aRenderer.enableAO = Minecraft.isAmbientOcclusionEnabled() && GT_Mod.gregtechproxy.mRenderTileAmbientOcclusion;
        aRenderer.useInventoryTint = false;

        if (entity instanceof ModelRenderLogicHost modelEntity && modelEntity.shouldRenderModel()) {
            ModelRenderLogic renderLogic = modelEntity.getRenderLogic();
            return true;
        }

        if (!(entity instanceof MultiTileBasicRender)) {
            return false;
        }

        if (entity instanceof MultiBlockPart) {
            IMultiBlockController controller = ((MultiBlockPart) entity).getTarget(false);
            if (controller instanceof ModelRenderLogicHost && ((ModelRenderLogicHost) controller).shouldRenderModel()) {
                return false;
            }
        }

        MultiTileBasicRender renderedEntity = (MultiTileBasicRender) entity;

        for (ForgeDirection aSide : ForgeDirection.VALID_DIRECTIONS) {
            ITexture aTexture = renderedEntity.getTexture(aSide);
            boolean tNeedsToSetBounds = true, tSides[] = new boolean[6];
            if (aTexture == null) continue;
            if (((IRenderedBlock)aBlock).setBlockBounds(aBlock.getRenderBlockPass(), aWorld, aX, aY, aZ, tSides)) {
                tNeedsToSetBounds = true; aRenderer.setRenderBoundsFromBlock(aBlock);
            } else {
                if (tNeedsToSetBounds) aBlock.setBlockBounds(0, 0, 0, 1, 1, 1);
                aRenderer.setRenderBoundsFromBlock(aBlock); tNeedsToSetBounds = false;
            }
            switch (aSide) {
                case DOWN:
                    renderYNegative(aWorld, aRenderer, aBlock, aX, aY, aZ, aTexture, !tNeedsToSetBounds, true, aBlock);
                case UP:
                    renderYPositive(aWorld, aRenderer, aBlock, aX, aY, aZ, aTexture, !tNeedsToSetBounds, true, aBlock);
                case WEST:
                    renderXNegative(aWorld, aRenderer, aBlock, aX, aY, aZ, aTexture, !tNeedsToSetBounds, true, aBlock);
                case EAST:
                    renderXPositive(aWorld, aRenderer, aBlock, aX, aY, aZ, aTexture, !tNeedsToSetBounds, true, aBlock);
                case NORTH:
                    renderZNegative(aWorld, aRenderer, aBlock, aX, aY, aZ, aTexture, !tNeedsToSetBounds, true, aBlock);
                case SOUTH:
                    renderZPositive(aWorld, aRenderer, aBlock, aX, aY, aZ, aTexture, !tNeedsToSetBounds, true, aBlock);
                default: {
                    // Do nothing
                }
            }
            if (tNeedsToSetBounds) aBlock.setBlockBounds(0, 0, 0, 1, 1, 1);
        }
        aRenderer.setRenderBoundsFromBlock(aBlock);
        endRendering(aRenderer, aBlock, aWorld, aX, aY, aZ);
        return rReturn;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return renderID;
    }

    private static void renderYNegative(IBlockAccess world, RenderBlocks aRenderer, Block aBlock, int x, int y, int z,
        ITexture texture, boolean aFullBlock, boolean aShouldSideBeRendered, Object aRenderedBlockObject) {
        if (texture == null || !texture.isValidTexture()) return;
        int aBrightness = 240;
        if (world != null) {
            if (aFullBlock && !aShouldSideBeRendered) return;
            aBrightness = aBlock.getMixedBrightnessForBlock(world, x, aFullBlock ? y - 1 : y, z);
            aRenderer.enableAO = true;
        }
        texture.renderYNeg(aRenderer, aBlock, x, y, z, aBrightness, !aFullBlock);
        aRenderer.flipTexture = false;
        aRenderer.colorRedTopLeft = aRenderer.colorRedBottomLeft = aRenderer.colorRedBottomRight =
        aRenderer.colorRedTopRight = aRenderer.colorGreenTopLeft = aRenderer.colorGreenBottomLeft =
        aRenderer.colorGreenBottomRight = aRenderer.colorGreenTopRight = aRenderer.colorBlueTopLeft =
        aRenderer.colorBlueBottomLeft = aRenderer.colorBlueBottomRight = aRenderer.colorBlueTopRight = 0.5F;
    }

    private static void renderYPositive(IBlockAccess world, RenderBlocks aRenderer, Block aBlock, int x, int y, int z,
        ITexture texture, boolean aFullBlock, boolean aShouldSideBeRendered, Object aRenderedBlockObject) {
        if (texture == null || !texture.isValidTexture()) return;
        int aBrightness = 240;
        if (world != null) {
            if (aFullBlock && !aShouldSideBeRendered) return;
            aBrightness = aBlock.getMixedBrightnessForBlock(world, x, aFullBlock ? y + 1 : y, z);
            aRenderer.enableAO = true;
        }
        texture.renderYPos(aRenderer, aBlock, x, y, z, aBrightness, !aFullBlock);
        aRenderer.flipTexture = false;
        aRenderer.colorRedTopLeft = aRenderer.colorRedBottomLeft = aRenderer.colorRedBottomRight =
        aRenderer.colorRedTopRight = aRenderer.colorGreenTopLeft = aRenderer.colorGreenBottomLeft =
        aRenderer.colorGreenBottomRight = aRenderer.colorGreenTopRight = aRenderer.colorBlueTopLeft =
        aRenderer.colorBlueBottomLeft = aRenderer.colorBlueBottomRight = aRenderer.colorBlueTopRight = 1.0F;
    }

    private static void renderZNegative(IBlockAccess aWorld, RenderBlocks aRenderer, Block aBlock, int x, int y, int z,
        ITexture aTexture, boolean aFullBlock, boolean aShouldSideBeRendered, Object aRenderedBlockObject) {
        if (aTexture == null || !aTexture.isValidTexture()) return;
        int aBrightness = 240;
        if (aWorld != null) {
            if (aFullBlock && !aShouldSideBeRendered) return;
            aBrightness = aBlock.getMixedBrightnessForBlock(aWorld, x, y, aFullBlock ? z - 1 : z);
            aRenderer.enableAO = true;
        }
        aRenderer.flipTexture = !aFullBlock;
        aTexture.renderZNeg(aRenderer, aBlock, x, y, z, aBrightness, !aFullBlock);
        aRenderer.flipTexture = false;
        aRenderer.colorRedTopLeft = aRenderer.colorRedBottomLeft = aRenderer.colorRedBottomRight =
        aRenderer.colorRedTopRight = aRenderer.colorGreenTopLeft = aRenderer.colorGreenBottomLeft =
        aRenderer.colorGreenBottomRight = aRenderer.colorGreenTopRight = aRenderer.colorBlueTopLeft =
        aRenderer.colorBlueBottomLeft = aRenderer.colorBlueBottomRight = aRenderer.colorBlueTopRight = 0.8F;
    }

    private static void renderZPositive(IBlockAccess aWorld, RenderBlocks aRenderer, Block aBlock, int x, int y, int z,
        ITexture aTexture, boolean aFullBlock, boolean aShouldSideBeRendered, Object aRenderedBlockObject) {
        if (aTexture == null || !aTexture.isValidTexture()) return;
        int aBrightness = 240;
        if (aWorld != null) {
            if (aFullBlock && !aShouldSideBeRendered) return;
            aBrightness = aBlock.getMixedBrightnessForBlock(aWorld, x, y, aFullBlock ? z + 1 : z);
            aRenderer.enableAO = true;
        }
        aTexture.renderZPos(aRenderer, aBlock, x, y, z, aBrightness, !aFullBlock);
        aRenderer.flipTexture = false;
        aRenderer.colorRedTopLeft = aRenderer.colorRedBottomLeft = aRenderer.colorRedBottomRight =
        aRenderer.colorRedTopRight = aRenderer.colorGreenTopLeft = aRenderer.colorGreenBottomLeft =
        aRenderer.colorGreenBottomRight = aRenderer.colorGreenTopRight = aRenderer.colorBlueTopLeft =
        aRenderer.colorBlueBottomLeft = aRenderer.colorBlueBottomRight = aRenderer.colorBlueTopRight = 0.8F;
    }

    private static void renderXNegative(IBlockAccess aWorld, RenderBlocks aRenderer, Block aBlock, int x, int y, int z,
        ITexture aTexture, boolean aFullBlock, boolean aShouldSideBeRendered, Object aRenderedBlockObject) {
        if (aTexture == null || !aTexture.isValidTexture()) return;
        int aBrightness = 240;
        if (aWorld != null) {
            if (aFullBlock && !aShouldSideBeRendered) return;
            aBrightness = aBlock.getMixedBrightnessForBlock(aWorld, aFullBlock ? x - 1 : x, y, z);
            aRenderer.enableAO = true;
        }
        aTexture.renderXNeg(aRenderer, aBlock, x, y, z, aBrightness, !aFullBlock);
        aRenderer.colorRedTopLeft = aRenderer.colorRedBottomLeft = aRenderer.colorRedBottomRight =
        aRenderer.colorRedTopRight = aRenderer.colorGreenTopLeft = aRenderer.colorGreenBottomLeft =
        aRenderer.colorGreenBottomRight = aRenderer.colorGreenTopRight = aRenderer.colorBlueTopLeft =
        aRenderer.colorBlueBottomLeft = aRenderer.colorBlueBottomRight = aRenderer.colorBlueTopRight = 0.6F;

    }



    private static void renderXPositive(IBlockAccess aWorld, RenderBlocks aRenderer, Block aBlock, int x, int y, int z,
        ITexture aTexture, boolean aFullBlock, boolean aShouldSideBeRendered, Object aRenderedBlockObject) {
        if (aTexture == null || !aTexture.isValidTexture()) return;
        int aBrightness = 240;
        if (aWorld != null) {
            if (aFullBlock && !aShouldSideBeRendered) return;
            aBrightness = aBlock.getMixedBrightnessForBlock(aWorld, aFullBlock ? x + 1 : x, y, z);
            aRenderer.enableAO = true;
        }
        aRenderer.flipTexture = !aFullBlock;
        aTexture.renderXPos(aRenderer, aBlock, x, y, z, aBrightness, !aFullBlock);
        aRenderer.colorRedTopLeft = aRenderer.colorRedBottomLeft = aRenderer.colorRedBottomRight =
        aRenderer.colorRedTopRight = aRenderer.colorGreenTopLeft = aRenderer.colorGreenBottomLeft =
        aRenderer.colorGreenBottomRight = aRenderer.colorGreenTopRight = aRenderer.colorBlueTopLeft =
        aRenderer.colorBlueBottomLeft = aRenderer.colorBlueBottomRight = aRenderer.colorBlueTopRight = 0.6F;
    }

    public static void startRendering(RenderBlocks aRenderer, Block aBlock, IBlockAccess aWorld, int aX, int aY, int aZ) {
        OFFSET_X_POS = OFFSET_X_NEG = OFFSET_Y_POS = OFFSET_Y_NEG = OFFSET_Z_POS = OFFSET_Z_NEG = aRenderer.hasOverrideBlockTexture() ? OFFSET_BREAK : OFFSET_DEFAULT;
        if (aWorld != null) {
            if (aRenderer.hasOverrideBlockTexture()) {
                if (aBlock.getRenderBlockPass() > 0) {
                    GL11.glDisable(GL11.GL_BLEND);
                }
            } else {
                if (GT_ALPHA_BLENDING && aBlock.getRenderBlockPass() < 1) {
                    IS_RENDERING_ALPHA = true;
                    Tessellator.instance.draw();
                    Tessellator.instance.startDrawingQuads();
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
                }
            }
        }
    }

    public static void endRendering(RenderBlocks aRenderer, Block aBlock, IBlockAccess aWorld, int aX, int aY, int aZ) {
        if (aWorld != null) {
            if (IS_RENDERING_ALPHA) {
                IS_RENDERING_ALPHA = false;
                Tessellator.instance.draw();
                Tessellator.instance.startDrawingQuads();
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            }
        }
    }
}
