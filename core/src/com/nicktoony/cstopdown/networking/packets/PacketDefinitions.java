package com.nicktoony.cstopdown.networking.packets;

import com.nicktoony.cstopdown.networking.packets.connection.*;
import com.nicktoony.cstopdown.networking.packets.game.CreatePlayerPacket;
import com.nicktoony.cstopdown.networking.packets.game.DestroyPlayerPacket;
import com.nicktoony.cstopdown.networking.packets.player.PlayerInputPacket;
import com.nicktoony.cstopdown.networking.packets.player.PlayerUpdatePacket;

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
        put(12, LoadedPacket.class);

        // Game
        put(9, CreatePlayerPacket.class);
        put(13, DestroyPlayerPacket.class);

        // Player
        put(10, PlayerInputPacket.class);
        put(11, PlayerUpdatePacket.class);

    }};

}
