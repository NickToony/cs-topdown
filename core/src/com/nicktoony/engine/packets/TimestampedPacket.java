package com.nicktoony.engine.packets;

/**
 * Created by Nick on 15/03/2016.
 */
public class TimestampedPacket extends Packet {
    private float timestamp;

    public float getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
