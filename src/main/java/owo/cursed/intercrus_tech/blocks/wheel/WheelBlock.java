package owo.cursed.intercrus_tech.blocks.wheel;

import g_mungus.zpl.block.thruster.ThrusterData;
import g_mungus.zpl.block.thruster.ThrusterForceApplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import owo.cursed.intercrus_tech.blocks.ModBlockEntities;
import owo.cursed.intercrus_tech.ship.IntercrusShipAttachment;

public class WheelBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public WheelBlock(Properties properties) {
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
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WheelBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, ModBlockEntities.WHEEL_BLOCK_ENTITY.get(), WheelBlockEntity::serverTick);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
            BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        if (level instanceof ServerLevel serverLevel) {
            addApplier(state, serverLevel, pos);
        }
        super.onPlace(state, level, pos, oldState, isMoving);
    }

    public static void addApplier(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos) {
        IntercrusShipAttachment.getOrCreate(level, pos).ifPresent(attachment -> {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof WheelBlockEntity wheelBlockEntity) {
                // Initialize default wheel drive data pointing forward
                Vector3d dir = VectorConversionsMCKt.toJOMLD(state.getValue(FACING).getNormal());
                wheelBlockEntity.wheelDrive = new WheelDriveData(dir, 0.0);

                // If WheelDriveData shares behavior or you created a WheelForceApplier, use it here.
                // Alternatively, if reusing ThrusterForceApplier, map wheelDrive to a ThrusterData format.
                ThrusterForceApplier applier = new ThrusterForceApplier(
                        new ThrusterData(wheelBlockEntity.wheelDrive.direction, wheelBlockEntity.wheelDrive.strength)
                );
                attachment.addApplier(pos, applier);
            }
        });
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        removeApplier(level, pos);
        super.onRemove(state, level, pos, newState, isMoving);
    }

    private static void removeApplier(@NotNull Level level, @NotNull BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            IntercrusShipAttachment.getOrCreate(serverLevel, pos).ifPresent(attachment ->
                    attachment.removeApplier(serverLevel, pos)
            );
        }
    }
}