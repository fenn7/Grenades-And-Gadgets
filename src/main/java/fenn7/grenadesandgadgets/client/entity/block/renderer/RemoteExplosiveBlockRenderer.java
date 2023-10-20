package fenn7.grenadesandgadgets.client.entity.block.renderer;

import fenn7.grenadesandgadgets.client.entity.block.model.RemoteExplosiveBlockModel;
import fenn7.grenadesandgadgets.commonside.block.custom.RemoteExplosiveBlock;
import fenn7.grenadesandgadgets.commonside.block.entity.RemoteExplosiveBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer;

public class RemoteExplosiveBlockRenderer extends GeoBlockRenderer<RemoteExplosiveBlockEntity> {
    public RemoteExplosiveBlockRenderer(BlockEntityRendererFactory.Context ctx) {
        super(new RemoteExplosiveBlockModel());
    }

    @Override
    public void render(BlockEntity tile, float partialTicks, MatrixStack stack, VertexConsumerProvider bufferIn, int combinedLightIn, int combinedOverlayIn) {
        RemoteExplosiveBlockEntity remoteTile = (RemoteExplosiveBlockEntity) tile;
        var block = Block.getBlockFromItem(remoteTile.getDisguiseBlockItem());
        if (block.equals(Blocks.AIR)) {
            this.transformStack(remoteTile, stack, false);
            super.render(tile, partialTicks, stack, bufferIn, combinedLightIn, combinedOverlayIn);
        } else {
            stack.push();
            this.transformStack(remoteTile, stack, true);
            MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(block.getDefaultState(), stack, bufferIn, combinedLightIn, combinedOverlayIn);
            stack.pop();
        }
    }

    private void transformStack(RemoteExplosiveBlockEntity tile, MatrixStack stack, boolean renderDisguise) {
        switch(tile.getCachedState().get(RemoteExplosiveBlock.FACING)) {
            case UP -> {
                if (!renderDisguise) {
                    stack.translate(0, -0.5, 0.5);
                    stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90));
                }
            }
            case DOWN -> {
                if (!renderDisguise) {
                    stack.translate(0, 0.5, 0.5);
                    stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90));
                } else {
                    stack.translate(0, 1, 1);
                    stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));
                }
            }
            case NORTH -> {
                stack.translate(0, 0, 1);
                stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90));
            }
            case SOUTH -> {
                stack.translate(0, 1, 0);
                stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));
            }
            case EAST -> {
                stack.translate(0, 1, 0);
                stack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-90));
            }
            case WEST -> {
                stack.translate(1, 0, 0);
                stack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90));
            }
        }
    }
}
