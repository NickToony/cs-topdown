package com.nicktoony.cstopdown.networking.packets;

import com.nicktoony.cstopdown.networking.packets.connection.AcceptPacket;
import com.nicktoony.cstopdown.networking.packets.connection.ConnectPacket;
import com.nicktoony.cstopdown.networking.packets.connection.DisconnectPacket;
import com.nicktoony.cstopdown.networking.packets.connection.RejectPacket;
import com.nicktoony.cstopdown.networking.packets.game.CreatePlayer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nick on 17/07/15.
 */
public class PacketDefinitions {

    /**
     * If you add a new packet, you MUST add it to this list with a unique ID!!!!
     */
    public static final Map<Integer, Class> PACKET_DEFITIONS
            = new HashMap<Integer, Class>() {{

        // Connection
        put(5, ConnectPacket.class);
        put(6, DisconnectPacket.class);
        put(7, AcceptPacket.class);
        put(8, RejectPacket.class);

        // Game
        put(9, CreatePlayer.class);

    }};

}
