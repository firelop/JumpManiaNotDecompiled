package fr.firelop.jumpmania.config;

import java.io.Serializable;

public class Location implements Serializable {
    public int x;
    public int y;
    public int z;
    public float yaw;
    public float pitch;
    public String world;

    public Location(int x, int y, int z, float yaw, float pitch, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.world = world;
    }

    public Location() {}
}
