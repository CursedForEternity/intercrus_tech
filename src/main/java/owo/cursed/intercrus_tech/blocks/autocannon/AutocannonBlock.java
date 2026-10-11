package owo.cursed.intercrus_tech.blocks.autocannon;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import owo.cursed.intercrus_tech.blocks.ModBlockEntities;

public class AutocannonBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public AutocannonBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }
    public static Vec3 getAimVector(BlockState state) {
        Direction facing = state.getValue(FACING);

        Vec3 localDirection = new Vec3(0, 0, -1);
        Vec3 worldDirection = transformLocalToWorld(localDirection, facing);
        return worldDirection.normalize();
    }

    private static Vec3 transformLocalToWorld(Vec3 local, Direction facing) {
        return switch (facing) {
            case NORTH -> new Vec3(local.x, local.y, local.z);      // No rotation
            case SOUTH -> new Vec3(-local.x, local.y, -local.z);    // Rotate 180° around Y
            case EAST -> new Vec3(-local.z, local.y, local.x);      // Rotate 90° around Y
            case WEST -> new Vec3(local.z, local.y, -local.x);      // Rotate -90° around Y
            case UP -> new Vec3(local.x, -local.z, local.y);        // Rotate 90° around X
            case DOWN -> new Vec3(local.x, local.z, -local.y);      // Rotate -90° around X
        };
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AutocannonBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, ModBlockEntities.AUTOCANNON_BLOCK_ENTITY.get(), AutocannonBlockEntity::serverTick);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
            BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }
}