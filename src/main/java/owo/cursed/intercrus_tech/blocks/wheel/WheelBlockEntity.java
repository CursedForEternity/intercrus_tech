package owo.cursed.intercrus_tech.blocks.wheel;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import owo.cursed.intercrus_tech.blocks.ModBlockEntities;

public class WheelBlockEntity extends BlockEntity {
    public WheelDriveData wheelDrive;

    public WheelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WHEEL_BLOCK_ENTITY.get(), pos, state);
        this.wheelDrive = new WheelDriveData(new Vector3d(0, 0, 1), 0.0);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, WheelBlockEntity blockEntity) {
        if (level == null || level.isClientSide) return;

        Ship genericShip = VSGameUtilsKt.getShipManagingPos(level, pos);
        if (!(genericShip instanceof ServerShip ship)) return;

        Direction facing = state.getValue(WheelBlock.FACING);
        BlockPos forwardPos = pos.relative(facing);
        BlockPos stepUpPos = forwardPos.above();

        // Ensure we don't treat blocks belonging to our *own* ship as obstacles
        Ship forwardShip = VSGameUtilsKt.getShipManagingPos(level, forwardPos);
        boolean isExternalObstacle = forwardShip == null || forwardShip.getId() != ship.getId();

        boolean hitObstacleAtFeet = isExternalObstacle && !level.getBlockState(forwardPos).isAir();
        boolean clearAboveStep = level.getBlockState(stepUpPos).isAir();

        // Get ship mass to scale forces properly (prevents heavy ships from ignoring wheels)
        double shipMass = ship.getInertiaData().getMass();

        // Base motion amplification direction (forward relative to wheel facing)
        Vector3d driveDirection = new Vector3d(facing.getStepX(), 0.0, facing.getStepZ()).normalize();

        double targetStrength = 0.0;

        // Check if the ship is actually trying to move
        Vector3dc velocity = ship.getVelocity();
        boolean isMoving = velocity.length() > 0.05;

        if (hitObstacleAtFeet && clearAboveStep && isMoving) {
            // Ledge detected: Adjust vector upward to pop the ship over the 1-block step
            driveDirection.y = 0.8;
            driveDirection.normalize();

            // Scale force with ship mass so it reliably climbs
            targetStrength = shipMass * 25.0;
        } else if (isMoving) {
            // Continuous motion amplification when rolling normally
            targetStrength = shipMass * 5.0;
        }

        // Update the drive data handled by the ship attachment applier
        if (blockEntity.wheelDrive != null) {
            blockEntity.wheelDrive.direction.set(driveDirection);
            blockEntity.wheelDrive.strength = targetStrength;
        }
    }
}