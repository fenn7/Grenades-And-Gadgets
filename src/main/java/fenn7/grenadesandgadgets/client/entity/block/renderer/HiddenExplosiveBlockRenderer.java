package fenn7.grenadesandgadgets.client.entity.block.renderer;

import fenn7.grenadesandgadgets.client.entity.block.model.HiddenExplosiveBlockModel;
import fenn7.grenadesandgadgets.commonside.block.custom.HiddenExplosiveBlock;
import fenn7.grenadesandgadgets.commonside.block.entity.HiddenExplosiveBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class HiddenExplosiveBlockRenderer extends GeoBlockRenderer<HiddenExplosiveBlockEntity> {
    public HiddenExplosiveBlockRenderer(BlockEntityRendererFactory.Context ctx) {
        super(new HiddenExplosiveBlockModel());
    }

    @Override
    public void render(BlockEntity tile, float partialTicks, MatrixStack stack, VertexConsumerProvider bufferIn, int combinedLightIn, int combinedOverlayIn) {
        var block = Block.getBlockFromItem(((HiddenExplosiveBlockEntity) tile).getDisguiseBlockItem());
        if (block.equals(Blocks.AIR)) {
            super.render(tile, partialTicks, stack, bufferIn, combinedLightIn, combinedOverlayIn);
        } else {
            stack.push();
            switch(tile.getCachedState().get(HiddenExplosiveBlock.FACING)) {
                case SOUTH -> {
                    stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
                    stack.translate(-1, 0, -1);
                }
                case WEST -> {
                    stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
                    stack.translate(-1, 0, 0);
                }
                case NORTH -> {
                    stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(0.0F));
                }
                case EAST -> {
                    stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(270.0F));
                    stack.translate(0, 0, -1);
                }
            }
            MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(block.getDefaultState(), stack, bufferIn, combinedLightIn, combinedOverlayIn);
            stack.pop();
        }
    }
}
