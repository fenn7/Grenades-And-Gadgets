package fenn7.grenadesandgadgets.client.entity.block.renderer;

import fenn7.grenadesandgadgets.commonside.block.entity.HiddenExplosiveBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class HiddenExplosiveBlockRenderer extends SimpleBlockRenderer<HiddenExplosiveBlockEntity> {
    private static final String NAME = "hidden_explosive_block";

    public HiddenExplosiveBlockRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx, NAME);
    }

    @Override
    public void render(HiddenExplosiveBlockEntity tile, float partialTicks, MatrixStack stack, VertexConsumerProvider bufferIn, int packedLightIn) {
        super.render(tile, partialTicks, stack, bufferIn, packedLightIn);
        stack.push();
        var item = tile.getDisguiseBlockItem();
        var block = Block.getBlockFromItem(item);
        var bblock = Blocks.FURNACE;
        RenderLayer renderType = this.getRenderType(tile, partialTicks, stack, bufferIn, null, packedLightIn, this.getTextureLocation(tile));
        MinecraftClient.getInstance().getBlockRenderManager().renderBlock(block.getDefaultState(), tile.getPos(), tile.getWorld(), stack, bufferIn.getBuffer(renderType), false, tile.getWorld().random);
        MinecraftClient.getInstance().getBlockRenderManager().renderBlock(block.getDefaultState(), tile.getPos().up(),tile.getWorld(), stack, bufferIn.getBuffer(RenderLayers.getBlockLayer(block.getDefaultState())), false, tile.getWorld().getRandom());
        stack.pop();
    }
}
