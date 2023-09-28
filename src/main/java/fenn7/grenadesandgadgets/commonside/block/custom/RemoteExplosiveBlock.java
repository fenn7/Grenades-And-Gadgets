package fenn7.grenadesandgadgets.commonside.block.custom;

import fenn7.grenadesandgadgets.commonside.block.entity.RemoteExplosiveBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class RemoteExplosiveBlock extends BlockWithEntity {
    public RemoteExplosiveBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RemoteExplosiveBlockEntity(pos, state);
    }
}
