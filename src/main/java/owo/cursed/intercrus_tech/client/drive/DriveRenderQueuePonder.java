package owo.cursed.intercrus_tech.client.drive;

import java.util.ArrayList;
import java.util.List;

public class DriveRenderQueuePonder {
    private static final List<DriveRenderData> QUEUE = new ArrayList<>();

    public static void enqueue(DriveRenderData data) {
        QUEUE.add(data);
    }

    public static List<DriveRenderData> getQueue() {
        return QUEUE;
    }

    public static void clear() {
        QUEUE.clear();
    }
}