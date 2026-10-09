package owo.cursed.intercrus_tech.client.flywheel;

import dev.engine_room.flywheel.api.instance.InstanceType;
import dev.engine_room.flywheel.api.layout.FloatRepr;
import dev.engine_room.flywheel.api.layout.LayoutBuilder;
import dev.engine_room.flywheel.lib.instance.SimpleInstanceType;
import dev.engine_room.flywheel.lib.util.ExtraMemoryOps;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static owo.cursed.intercrus_tech.IntercrusTechMod.asResource;


@OnlyIn(Dist.CLIENT)
public class InstanceTypes {
    public static final InstanceType<DriveInstance> SOLAR_DRIVE
            = SimpleInstanceType.builder(DriveInstance::new)
            .cullShader(asResource("instance/cull/drive.glsl"))
            .vertexShader(asResource("instance/drive.vert"))
            .layout(LayoutBuilder.create()
                    .matrix("pose", FloatRepr.FLOAT, 4)
                    .build()
            )
            .writer((ptr, instance) -> {
                ExtraMemoryOps.putMatrix4f(ptr, instance.pose);
            })
            .build();
}