package gregtech.common.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * @author Gregorius Techneticies
 */
public interface IRenderedBlockObject {
    /** @return the Textures to be rendered */
    @SideOnly(Side.CLIENT)
    public ITexture[] getTexture(Block aBlock, int aRenderPass, ForgeDirection aSide, boolean[] aShouldSideBeRendered);

    /** if this uses said Render Pass or if it can be skipped entirely. */
    @SideOnly(Side.CLIENT)
    public boolean usesRenderPass(int aRenderPass, boolean[] aShouldSideBeRendered);

    /** sets the Block Size rendered; return false for letting it select the normal Block Bounds. */
    @SideOnly(Side.CLIENT)
    public boolean setBlockBounds(Block aBlock, int aRenderPass, boolean[] aShouldSideBeRendered);

    /** gets the Amount of Render Passes for this TileEntity or similar Handler. Only gets called once per Rendering. */
    @SideOnly(Side.CLIENT)
    public int getRenderPasses(Block aBlock, boolean[] aShouldSideBeRendered);

    /** returning true stops all the other Rendering from happening. */
    @SideOnly(Side.CLIENT)
    public boolean renderItem(Block aBlock, RenderBlocks aRenderer);

    /** returning true stops all the other Rendering from happening. */
    @SideOnly(Side.CLIENT)
    public boolean renderBlock(Block aBlock, RenderBlocks aRenderer, IBlockAccess aWorld, int aX, int aY, int aZ);

    /** return "this" if you want to use the functions above. */
    @SideOnly(Side.CLIENT)
    public IRenderedBlockObject passRenderingToObject(ItemStack aStack);

    /** return "this" if you want to use the functions above. */
    @SideOnly(Side.CLIENT)
    public IRenderedBlockObject passRenderingToObject(IBlockAccess aWorld, int aX, int aY, int aZ);

    public static class ErrorRenderer implements IRenderedBlockObjectSideCheck, IRenderedBlockObject {
        public static final ErrorRenderer INSTANCE = new ErrorRenderer();
        public ITexture[] mErrorTexture = Textures.BlockIcons.ERROR_RENDERING;
        @Override public ITexture[] getTexture(Block aBlock, int aRenderPass, ForgeDirection aSide, boolean[] aShouldSideBeRendered) {return mErrorTexture;}
        @Override public boolean usesRenderPass(int aRenderPass, boolean[] aShouldSideBeRendered) {return true;}
        @Override public boolean setBlockBounds(Block aBlock, int aRenderPass, boolean[] aShouldSideBeRendered) {aBlock.setBlockBounds(-0.25F, -0.25F, -0.25F, 1.25F, 1.25F, 1.25F); return true;}
        @Override public int getRenderPasses(Block aBlock, boolean[] aShouldSideBeRendered) {return 1;}
        @Override public boolean renderItem(Block aBlock, RenderBlocks aRenderer) {return false;}
        @Override public boolean renderFullBlockSide(Block aBlock, RenderBlocks aRenderer, ForgeDirection aSide) {return true;}
        @Override public IRenderedBlockObject passRenderingToObject(ItemStack aStack) {return this;}
        @Override public IRenderedBlockObject passRenderingToObject(IBlockAccess aWorld, int aX, int aY, int aZ) {return this;}

        @Override
        public boolean renderBlock(Block aBlock, RenderBlocks aRenderer, IBlockAccess aWorld, int aX, int aY, int aZ) {
            aBlock.setBlockBounds(-0.25F, -0.25F, -0.25F, 1.25F, 1.25F, 1.25F);
            GT_Renderer_Block.renderNegativeYFacing(aWorld, aRenderer, aBlock, aX, aY, aZ, mErrorTexture, false);
            GT_Renderer_Block.renderPositiveYFacing(aWorld, aRenderer, aBlock, aX, aY, aZ, mErrorTexture, false);
            GT_Renderer_Block.renderNegativeZFacing(aWorld, aRenderer, aBlock, aX, aY, aZ, mErrorTexture, false);
            GT_Renderer_Block.renderPositiveZFacing(aWorld, aRenderer, aBlock, aX, aY, aZ, mErrorTexture, false);
            GT_Renderer_Block.renderNegativeXFacing(aWorld, aRenderer, aBlock, aX, aY, aZ, mErrorTexture, false);
            GT_Renderer_Block.renderPositiveXFacing(aWorld, aRenderer, aBlock, aX, aY, aZ, mErrorTexture, false);
            return true;
        }
    }
}
