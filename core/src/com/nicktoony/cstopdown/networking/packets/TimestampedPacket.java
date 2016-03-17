package com.nicktoony.cstopdown.networking.packets;

/**
 * Created by Nick on 15/03/2016.
 */
public interface TimestampedPacket {
    public long getTimestamp();
    public void setTimestamp(long timestamp);
}
