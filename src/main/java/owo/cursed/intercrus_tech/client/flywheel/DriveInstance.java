package owo.cursed.intercrus_tech.client.flywheel;

import dev.engine_room.flywheel.api.instance.InstanceHandle;
import dev.engine_room.flywheel.api.instance.InstanceType;
import dev.engine_room.flywheel.lib.instance.AbstractInstance;
import org.joml.Matrix4fc;

public class DriveInstance extends AbstractInstance {
    public Matrix4fc pose;

    protected DriveInstance(InstanceType<?> type, InstanceHandle handle) {
        super(type, handle);
    }

    public void setTransform(Matrix4fc transform) {
        this.pose = transform;
        this.setChanged();
    }

}