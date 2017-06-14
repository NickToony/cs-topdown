package com.nicktoony.cstopdown.networking.packets.game;

import com.nicktoony.cstopdown.networking.packets.helpers.PlayerDetailsWrapper;
import com.nicktoony.engine.packets.Packet;

/**
 * Created by Nick on 14/06/2017.
 */
public class PlayerDetailsPacket extends Packet {
    public PlayerDetailsWrapper[] playerDetails;
    public int[] left;
}
