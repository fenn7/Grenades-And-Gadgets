package fenn7.grenadesandgadgets.commonside.block.custom;

import java.util.Map;

import fenn7.grenadesandgadgets.commonside.block.GrenadesModBlockEntities;
import fenn7.grenadesandgadgets.commonside.block.entity.RemoteExplosiveBlockEntity;
import fenn7.grenadesandgadgets.commonside.item.GrenadesModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RemoteExplosiveBlock extends AbstractDisguisedExplosiveBlock {
    public static final DirectionProperty FACING = FacingBlock.FACING;
    private static final Map<Direction, VoxelShape> shapeMap = Map.of(
        Direction.NORTH, Block.createCuboidShape(3, 1.5, 12, 13, 14.5, 16),
        Direction.SOUTH, Block.createCuboidShape(3, 1.5, 0, 13, 14.5, 4),
        Direction.EAST, Block.createCuboidShape(0, 1.5, 3, 4, 14.5, 13),
        Direction.WEST, Block.createCuboidShape(12, 1.5, 3, 16, 14.5, 13),
        Direction.UP, Block.createCuboidShape(3, 0, 1.5, 13, 4, 14.5),
        Direction.DOWN, Block.createCuboidShape(3, 12, 1.5, 13, 16, 14.5)
    );

    public RemoteExplosiveBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.UP).with(ARMED, false));
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, GrenadesModBlockEntities.REMOTE_EXPLOSIVE_BLOCK_ENTITY, RemoteExplosiveBlockEntity::tick);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.getStackInHand(hand).getItem().equals(GrenadesModItems.REMOTE_DETONATOR)) {
            return ActionResult.PASS;
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Nullable
    @Override
    @SuppressWarnings("ConstantConditions")
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(FACING, ctx.getSide());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RemoteExplosiveBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return shapeMap.get(state.get(FACING));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(FACING));
    }
}
