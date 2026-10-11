package owo.cursed.intercrus_tech.blocks.autocannon;

import g_mungus.zpl.ZeroPointLabsMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import owo.cursed.intercrus_tech.blocks.ModBlockEntities;
import owo.cursed.intercrus_tech.client.ModSounds;
import owo.cursed.intercrus_tech.entity.AutocannonAmmoEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AutocannonBlockEntity extends BlockEntity {
    private static final int MAX_AMMO_CAPACITY = 512;
    private static final int AMMO_PER_SHOT = 1;

    private int tickCounter = 0;
    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        public int getSlotLimit(int slot) {
            return MAX_AMMO_CAPACITY;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.is(Items.ARROW);
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final LazyOptional<ItemStackHandler> handlerLazyOptional = LazyOptional.of(() -> itemHandler);

    public AutocannonBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.AUTOCANNON_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    public <T> @Nonnull LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return handlerLazyOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        handlerLazyOptional.invalidate();
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Inventory")) {
            itemHandler.deserializeNBT(tag.getCompound("Inventory"));
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", itemHandler.serializeNBT());
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AutocannonBlockEntity blockEntity) {
        if (level.isClientSide) return;

        boolean isPowered = level.hasNeighborSignal(pos);

        if (isPowered) {
            if (blockEntity.tickCounter == 0) {
                ItemStack storedAmmo = blockEntity.itemHandler.getStackInSlot(0);
                if (!storedAmmo.isEmpty() && storedAmmo.getCount() >= AMMO_PER_SHOT) {
                    blockEntity.itemHandler.extractItem(0, AMMO_PER_SHOT, false);
                    blockEntity.fireEnergyOrb(level, pos, state);
                }
            }
        }

        if (blockEntity.tickCounter != 0 || isPowered) {
            int TICKS_PER_SHOT = 8;
            blockEntity.tickCounter = (blockEntity.tickCounter + 1) % TICKS_PER_SHOT;
        }
    }

    private void fireEnergyOrb(Level level, BlockPos pos, BlockState state) {
        Vec3 direction = AutocannonBlock.getAimVector(state);

        double scale = ZeroPointLabsMod.getDimensionScale(level);

        Vec3 blockCenter = Vec3.atCenterOf(pos);
        Vec3 spawnPos = blockCenter.add(direction.scale(0.6));

        Vec3 velocity = direction.scale(7.0);

        AutocannonAmmoEntity energyOrb;

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

            energyOrb = new AutocannonAmmoEntity(level, newPosition.x, newPosition.y, newPosition.z, VectorConversionsMCKt.toMinecraft(finalVelocity));

            energyOrb.setExcludedShipId(ship.getId());
        } else {
            energyOrb = new AutocannonAmmoEntity(level, spawnPos.x, spawnPos.y, spawnPos.z, velocity.scale(scale));
        }

        level.addFreshEntity(energyOrb);

        level.playSound(
                null,
                pos,
                ModSounds.AUTOCANNON_SHOT.get(),
                SoundSource.BLOCKS,
                1.0F,
                1.0F
        );
    }
}