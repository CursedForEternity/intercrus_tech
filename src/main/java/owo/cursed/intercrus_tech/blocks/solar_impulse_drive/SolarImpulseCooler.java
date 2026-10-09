package owo.cursed.intercrus_tech.blocks.solar_impulse_drive;

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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import owo.cursed.intercrus_tech.ship.IntercrusShipAttachment;

public class SolarImpulseCooler extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final IntegerProperty POWER = BlockStateProperties.POWER;

    public SolarImpulseCooler(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWER, 0)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWER);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction;
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            direction = context.getNearestLookingDirection();
        } else {
            direction = context.getNearestLookingDirection().getOpposite();
        }
        return this.defaultBlockState().setValue(FACING, direction);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SolarImpulseCoolerEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : (level1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof SolarImpulseCoolerEntity exampleBlockEntity) {
                exampleBlockEntity.tick();
            }
        };
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
            if (blockEntity instanceof SolarImpulseCoolerEntity thrusterBlockEntity) {
                thrusterBlockEntity.thrust = new ThrusterData(VectorConversionsMCKt.toJOMLD(state.getValue(FACING).getOpposite().getNormal()), 0.0);

                ThrusterForceApplier applier = new ThrusterForceApplier(thrusterBlockEntity.thrust);
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