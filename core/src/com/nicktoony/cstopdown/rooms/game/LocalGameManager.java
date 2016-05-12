package com.nicktoony.cstopdown.rooms.game;

import com.nicktoony.cstopdown.networking.client.SBSocket;
import com.nicktoony.cstopdown.networking.packets.Packet;

/**
 * Created by Nick on 13/05/2016.
 */
public class LocalGameManager extends GameManager {
    public LocalGameManager(RoomGame roomGame, SBSocket socket) {
        super(roomGame, socket);
    }

    @Override
    public void onMessage(SBSocket socket, Packet packet) {
        // Do nothing.
    }
}
