package owo.cursed.intercrus_tech.blocks.cannon;

import g_mungus.zpl.ZeroPointLabsMod;
import g_mungus.zpl.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemStackHandler;
import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import owo.cursed.intercrus_tech.blocks.ModBlockEntities;
import owo.cursed.intercrus_tech.entity.CannonAmmoEntity;

public class CannonBarrelEntity extends BlockEntity {
    private static final int AMMO_PER_SHOT = 1;
    private int tickCounter = 0;

    public CannonBarrelEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CANNON_BARREL_BLOCK_ENTITY.get(), pos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CannonBarrelEntity blockEntity) {
        if (level.isClientSide) return;

        // Find the matching loader block located directly behind the barrel (opposite to facing)
        Direction facing = state.getValue(CannonBarrelBlock.FACING);
        BlockPos loaderPos = pos.relative(facing.getOpposite());
        BlockEntity loaderEntity = level.getBlockEntity(loaderPos);

        if (loaderEntity instanceof CannonLoaderEntity loader) {
            boolean isPowered = loader.isPowered();

            if (isPowered) {
                if (blockEntity.tickCounter == 0) {
                    ItemStackHandler inventory = loader.getItemHandler();
                    ItemStack storedAmmo = inventory.getStackInSlot(0);

                    if (!storedAmmo.isEmpty() && storedAmmo.getCount() >= AMMO_PER_SHOT) {
                        inventory.extractItem(0, AMMO_PER_SHOT, false);
                        blockEntity.fireAmmo(level, pos, state);
                    }
                }
            }

            if (blockEntity.tickCounter != 0 || isPowered) {
                int TICKS_PER_SHOT = 30;
                blockEntity.tickCounter = (blockEntity.tickCounter + 1) % TICKS_PER_SHOT;
            }
        }
    }

    private void fireAmmo(Level level, BlockPos pos, BlockState state) {
        Vec3 direction = CannonBarrelBlock.getAimVector(state);
        double scale = ZeroPointLabsMod.getDimensionScale(level);

        Vec3 blockCenter = Vec3.atCenterOf(pos);
        Vec3 spawnPos = blockCenter.add(direction.scale(0.6));
        Vec3 velocity = direction.scale(7.0);

        CannonAmmoEntity cannonAmmo;
        Ship ship = VSGameUtilsKt.getShipManagingPos(level, pos);

        if (ship != null) {
            Matrix4dc transform = ship.getTransform().getShipToWorld();
            Vector3d newVelocity = transform.transformDirection(VectorConversionsMCKt.toJOML(velocity));
            Vector3d newPosition = transform.transformPosition(VectorConversionsMCKt.toJOML(blockCenter));
            newPosition = newPosition.add(newVelocity.normalize(new Vector3d()).mul(0.6 * scale));

            Vector3dc shipVelocity = ship.getVelocity();
            Vector3d velocityDirection = newVelocity.normalize(new Vector3d());
            double parallelComponent = shipVelocity.dot(velocityDirection);
            Vector3d parallelVelocity = velocityDirection.mul(parallelComponent, new Vector3d());

            Vector3d finalVelocity;
            if (scale < 0.5) {
                finalVelocity = newVelocity.add(parallelVelocity.mul(scale), new Vector3d());
            } else {
                finalVelocity = newVelocity;
            }

            cannonAmmo = new CannonAmmoEntity(level, newPosition.x, newPosition.y, newPosition.z, VectorConversionsMCKt.toMinecraft(finalVelocity));
            cannonAmmo.setExcludedShipId(ship.getId());
        } else {
            cannonAmmo = new CannonAmmoEntity(level, spawnPos.x, spawnPos.y, spawnPos.z, velocity.scale(scale));
        }

        level.addFreshEntity(cannonAmmo);
        level.playSound(null, pos, ModSounds.ENERGY_ORB_SHOOT.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}