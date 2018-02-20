package com.nicktoony.engine.packets;

import java.util.Map;

/**
 * Created by nick on 17/07/15.
 *
 * Provides the core packet parameters for all messages
 */
public class Packet {
    private int message_id;
    private float timestamp;

    public void prepareMessageId() {
        for (Map.Entry<Integer, Class> entrySet : PacketDefinitions.PACKET_DEFITIONS.entrySet()) {
            if (entrySet.getValue() == getClass()) {
                message_id = entrySet.getKey();
                return;
            }
        }
    }

    public int getMessage_id() {
        return message_id;
    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }

    public float getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
