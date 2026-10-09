package owo.cursed.intercrus_tech.client.flywheel;

import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.material.*;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.material.SimpleMaterial;
import dev.engine_room.flywheel.lib.material.SimpleMaterialShaders;
import dev.engine_room.flywheel.lib.model.SimpleModel;
import dev.engine_room.flywheel.lib.model.SimpleQuadMesh;
import dev.engine_room.flywheel.lib.visual.AbstractBlockEntityVisual;
import dev.engine_room.flywheel.lib.visual.SimpleTickableVisual;
import g_mungus.zpl.client.flywheel.PosTexVertexView;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import owo.cursed.intercrus_tech.blocks.solar_impulse_drive.SolarImpulseCooler;
import owo.cursed.intercrus_tech.blocks.solar_impulse_drive.SolarImpulseCoolerEntity;

import java.util.List;
import java.util.function.Consumer;

import static owo.cursed.intercrus_tech.IntercrusTechMod.asResource;

public class DriveVisual extends AbstractBlockEntityVisual<SolarImpulseCoolerEntity> implements SimpleTickableVisual {

    DriveInstance instance;

    public DriveVisual(VisualizationContext ctx, SolarImpulseCoolerEntity blockEntity, float partialTick) {
        super(ctx, blockEntity, partialTick);

        instance = instancerProvider().instancer(InstanceTypes.SOLAR_DRIVE, new SimpleModel(List.of(
                new Model.ConfiguredMesh(MATERIAL, getMesh())
        ))).createInstance();

        // Set initial transform
        updateTransform();
    }

    private void updateTransform() {
        BlockState blockState = blockEntity.getBlockState();
        float power = blockState.getValue(SolarImpulseCooler.POWER) / 15f;

        float hScale = (1 + power) / 2f;
        Direction direction = blockState.getValue(SolarImpulseCooler.FACING);

        Matrix4f matrix = new Matrix4f();

        // Translate to block position
        BlockPos pos1 = getVisualPosition();
        matrix.translate(pos1.getX(), pos1.getY(), pos1.getZ());

        // Translate to center of block
        matrix.translate(0.5f, 0.5f, 0.5f);

        // Apply rotation based on facing direction
        switch (direction) {
            case UP -> {}
            case DOWN -> matrix.rotateX((float) Math.toRadians(180));
            case NORTH -> matrix.rotateX((float) Math.toRadians(-90));
            case SOUTH -> matrix.rotateX((float) Math.toRadians(90));
            case WEST -> matrix.rotateZ((float) Math.toRadians(90));
            case EAST -> matrix.rotateZ((float) Math.toRadians(-90));
        }

        // Translate back from center
        matrix.translate(-0.5f, -0.5f, -0.5f);

        // Apply power-based offset and scale
        matrix.translate(-0.5f * power, 1, -0.5f * power);
        matrix.scale(2 * hScale, 6 * power, 2 * hScale);

        instance.setTransform(matrix);
        instance.setVisible(power > 0.01);
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {}

    @Override
    public void updateLight(float v) {}

    @Override
    protected void _delete() {
        instance.delete();
    }

    public static final SimpleMaterial MATERIAL = SimpleMaterial.builder()
            .shaders(new SimpleMaterialShaders(asResource("material/drive.vert"), asResource("material/drive.frag")))
            .transparency(Transparency.LIGHTNING)
            .depthTest(DepthTest.LEQUAL)
            .writeMask(WriteMask.COLOR)
            .backfaceCulling(true)
            .useOverlay(true)
            .useLight(false)
            .cardinalLightingMode(CardinalLightingMode.OFF)
            .texture(asResource("textures/misc/white.png"))
            .build();



    @Override
    public void tick(Context context) {
        updateTransform();
    }

    private static SimpleQuadMesh getMesh() {
        if (MESH == null) {
            MESH = build();
        }
        return MESH;
    }

    private static SimpleQuadMesh MESH = null;

    public static SimpleQuadMesh build() {
        PosTexVertexView vertexList = new PosTexVertexView();
        vertexList.vertexCount(48);

        int vertexIndex = 0;
        int[] offsets = {0, 7};

        for (int offset : offsets) {
            vertexIndex = addQuad(vertexList, vertexIndex, shape.get(0 + offset), shape.get(1 + offset), shape.get(8 + offset), shape.get(7 + offset));
            vertexIndex = addQuad(vertexList, vertexIndex, shape.get(1 + offset), shape.get(2 + offset), shape.get(9 + offset), shape.get(8 + offset));
            vertexIndex = addQuad(vertexList, vertexIndex, shape.get(2 + offset), shape.get(3 + offset), shape.get(10 + offset), shape.get(9 + offset));
            vertexIndex = addQuad(vertexList, vertexIndex, shape.get(3 + offset), shape.get(4 + offset), shape.get(11 + offset), shape.get(10 + offset));
            vertexIndex = addQuad(vertexList, vertexIndex, shape.get(4 + offset), shape.get(5 + offset), shape.get(12 + offset), shape.get(11 + offset));
            vertexIndex = addQuad(vertexList, vertexIndex, shape.get(5 + offset), shape.get(6 + offset), shape.get(13 + offset), shape.get(12 + offset));
        }

        return new SimpleQuadMesh(vertexList, "drive");
    }


    private static int addQuad(PosTexVertexView vertexList, int startIndex, Vector4f a, Vector4f b, Vector4f c, Vector4f d) {
        vertexList.x(startIndex, d.x); vertexList.y(startIndex, d.y); vertexList.z(startIndex, d.z); vertexList.u(startIndex, d.y); vertexList.v(startIndex, d.w); startIndex++;
        vertexList.x(startIndex, c.x); vertexList.y(startIndex, c.y); vertexList.z(startIndex, c.z); vertexList.u(startIndex, c.y); vertexList.v(startIndex, c.w); startIndex++;
        vertexList.x(startIndex, b.x); vertexList.y(startIndex, b.y); vertexList.z(startIndex, b.z); vertexList.u(startIndex, b.y); vertexList.v(startIndex, b.w); startIndex++;
        vertexList.x(startIndex, a.x); vertexList.y(startIndex, a.y); vertexList.z(startIndex, a.z); vertexList.u(startIndex, a.y); vertexList.v(startIndex, a.w); startIndex++;
        return startIndex;
    }

    private static final List<Vector4f> shape = List.of(
            new Vector4f(1.0f, 0.1f, 0.5f, 0f),
            new Vector4f(0.75f, 0.1f, 0.9333f, 0.167f),
            new Vector4f(0.25f, 0.1f, 0.9333f, 0.333f),
            new Vector4f(0.0f, 0.1f, 0.5f, 0.5f),
            new Vector4f(0.25f, 0.1f, 0.0667f, 0.667f),
            new Vector4f(0.75f, 0.1f, 0.0667f, 0.833f),
            new Vector4f(1.0f, 0.1f, 0.5f, 1f),

            new Vector4f(0.6667f, 1, 0.5f, 0f),
            new Vector4f(0.5833f, 1, 0.6443f, 0.167f),
            new Vector4f(0.4167f, 1, 0.6443f, 0.333f),
            new Vector4f(0.3333f, 1, 0.5f, 0.5f),
            new Vector4f(0.4167f, 1, 0.3557f, 0.667f),
            new Vector4f(0.5833f, 1, 0.3557f, 0.833f),
            new Vector4f(0.6667f, 1, 0.5f, 1f)
    );
}