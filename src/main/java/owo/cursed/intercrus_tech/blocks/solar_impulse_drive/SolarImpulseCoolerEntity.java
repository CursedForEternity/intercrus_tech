package owo.cursed.intercrus_tech.blocks.solar_impulse_drive;

import g_mungus.zpl.block.thruster.ThrusterData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import owo.cursed.intercrus_tech.blocks.ModBlockEntities;
import owo.cursed.intercrus_tech.blocks.ModBlocks;

import java.util.concurrent.atomic.AtomicInteger;

public class SolarImpulseCoolerEntity extends BlockEntity {
    public SolarImpulseCoolerEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOLAR_IMPULSE_COOLER_ENTITY.get(), pos, state);
    }

    public ThrusterData thrust;

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void tick() {
        if (level == null) return;

        int prev = getBlockState().getValue(SolarImpulseCooler.POWER);
        int current = getThrusterStrength(prev);

        double force_strength = current * 2 / 15.0;

        if (current != prev) {
            level.setBlock(getBlockPos(), getBlockState().setValue(SolarImpulseCooler.POWER, current), 3);
        }
        if (thrust == null) {
            SolarImpulseCooler.addApplier(getBlockState(), (ServerLevel) level, getBlockPos());
        }
        if (thrust != null) {
            thrust.strength = force_strength;
        }
    }

    private int getThrusterStrength(int prev) {
        int output = prev;

        int target = getInputSignal();
        if (output < target) {
            output++;
        } else if (output > target) {
            output--;
        }

        double requestedStrength = output / 15.0;
        double requestedEnergy = requestedStrength * 100;

        LazyOptional<IEnergyStorage> energySource = getNeighborEnergyStorage();

        AtomicInteger extracted = new AtomicInteger(-1);
        if (requestedEnergy > 0) {
            energySource.ifPresent(it -> {
                extracted.set(it.extractEnergy((int) requestedEnergy, false));
            });
        }
        if (extracted.get() + 1 <= requestedEnergy) {
            if (prev > 0) {
                output = prev - 1;
            } else {
                output = 0;
            }
        }
        return output;
    }

    private int getInputSignal() {
        if (level == null) return 0;

        BlockPos thisPos = getBlockPos();
        BlockState thisState = level.getBlockState(thisPos);

        BlockPos neighbor = thisPos.offset(thisState.getValue(SolarImpulseCooler.FACING).getOpposite().getNormal());
        BlockState neighborState = level.getBlockState(neighbor);

        if (neighborState.is(ModBlocks.SOLAR_IMPULSE_DRIVE.get()) && neighborState.getValue(SolarImpulseDrive.FACING).equals(thisState.getValue(SolarImpulseCooler.FACING))) {
            return level.getBestNeighborSignal(neighbor);
        }
        return 0;
    }

    private @NotNull LazyOptional<IEnergyStorage> getNeighborEnergyStorage() {
        if (level == null) return LazyOptional.empty();

        BlockState state = getBlockState();
        Direction facing = state.getValue(SolarImpulseCooler.FACING);
        BlockPos neighborPos = worldPosition.relative(facing.getOpposite());
        BlockEntity neighborEntity = level.getBlockEntity(neighborPos);

        if (neighborEntity != null) {

            return neighborEntity.getCapability(ForgeCapabilities.ENERGY, facing);
        }

        return LazyOptional.empty();
    }
}