package com.nicktoony.engine.packets.connection;

import com.nicktoony.engine.packets.Packet;

/**
 * Created by Nick on 03/01/2016.
 */
public class JoinTeamPacket extends Packet {

    // Blank required for JSON reading
    public JoinTeamPacket() {

    }

    public JoinTeamPacket(int team) {
        this.team = team;
    }

    public int team;
}
