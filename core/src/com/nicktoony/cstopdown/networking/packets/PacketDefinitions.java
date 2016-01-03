package com.nicktoony.cstopdown.networking.packets;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nick on 17/07/15.
 */
public class PacketDefinitions {

    public static final Map<Integer, Class> PACKET_DEFITIONS
            = new HashMap<Integer, Class>() {{

        put(5, ConnectPacket.class);
        put(6, DisconnectPacket.class);
        put(7, AcceptPacket.class);
        put(8, RejectPacket.class);

    }};

}
