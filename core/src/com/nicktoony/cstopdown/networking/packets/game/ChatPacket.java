package com.nicktoony.cstopdown.networking.packets.game;

import com.nicktoony.engine.packets.Packet;

/**
 * Created by Nick on 03/01/2016.
 */
public class ChatPacket extends Packet {

    public String message;

    public ChatPacket() {
    }

    public ChatPacket(String message) {

        this.message = message;
    }
}
