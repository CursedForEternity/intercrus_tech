package owo.cursed.intercrus_tech.client.drive;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import dev.engine_room.flywheel.api.backend.BackendManager;
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import owo.cursed.intercrus_tech.blocks.solar_impulse_drive.SolarImpulseCooler;
import owo.cursed.intercrus_tech.blocks.solar_impulse_drive.SolarImpulseCoolerEntity;

public class DriveExhaustBlockEntityRenderer implements BlockEntityRenderer<SolarImpulseCoolerEntity> {


    public DriveExhaustBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public boolean shouldRenderOffScreen(SolarImpulseCoolerEntity arg) {
        return true;
    }

    @Override
    public boolean shouldRender(SolarImpulseCoolerEntity arg, Vec3 arg2) {
        return true;
    }

    @Override
    public void render(SolarImpulseCoolerEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        Level level = blockEntity.getLevel();
        boolean flywheelDisabled = BackendManager.currentBackend().equals(BackendManager.offBackend());
        if (!flywheelDisabled && !(level instanceof PonderLevel)) {
            return;
        }

        BlockState blockState = blockEntity.getBlockState();
        float power = blockState.getValue(SolarImpulseCooler.POWER) / 15f;
        if (power < 0.0001f) return;

        poseStack.pushPose();

        try {
            float hScale = (1 + power) / 2f;

            poseStack.translate(0.5, 0.5, 0.5);

            Direction direction = blockState.getValue(SolarImpulseCooler.FACING);
            switch (direction) {
                case UP -> {}
                case DOWN -> poseStack.mulPose(Axis.XP.rotationDegrees(180f));
                case NORTH -> poseStack.mulPose(Axis.XP.rotationDegrees(-90f));
                case SOUTH -> poseStack.mulPose(Axis.XP.rotationDegrees(90f));
                case WEST -> poseStack.mulPose(Axis.ZP.rotationDegrees(90f));
                case EAST -> poseStack.mulPose(Axis.ZP.rotationDegrees(-90f));
            }

            poseStack.translate(-0.5, -0.5, -0.5);

            poseStack.translate(-0.5f * power, 1, -0.5f * power);
            poseStack.scale(2 * hScale, 6 * power, 2 * hScale);

            if (level instanceof PonderLevel) {
                DriveRenderQueuePonder.enqueue(new DriveRenderData(
                        Vec3.atCenterOf(blockEntity.getBlockPos()),
                        direction,
                        power,
                        poseStack
                ));
            } else {
                DriveRenderQueue.enqueue(new DriveRenderData(
                        Vec3.atCenterOf(blockEntity.getBlockPos()),
                        direction,
                        power,
                        poseStack
                ));
            }
        } finally {
            poseStack.popPose();
        }
    }
}