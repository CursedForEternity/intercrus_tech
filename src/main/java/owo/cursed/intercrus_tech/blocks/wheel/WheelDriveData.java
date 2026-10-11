package owo.cursed.intercrus_tech.blocks.wheel;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import g_mungus.zpl.util.Vector3dDeserializer;
import g_mungus.zpl.util.Vector3dSerializer;
import org.joml.Vector3d;

public class WheelDriveData {
    @JsonSerialize(
        using = Vector3dSerializer.class
    )
    @JsonDeserialize(
        using = Vector3dDeserializer.class
    )
    public Vector3d direction;
    public double strength;

    public WheelDriveData() {
    }

    public WheelDriveData(Vector3d direction, double strength) {
        this.direction = direction;
        this.strength = strength;
    }
}