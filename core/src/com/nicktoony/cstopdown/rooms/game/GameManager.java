package com.nicktoony.cstopdown.rooms.game;

import com.nicktoony.cstopdown.networking.client.SBSocket;
import com.nicktoony.cstopdown.networking.packets.game.CreatePlayer;
import com.nicktoony.cstopdown.networking.packets.Packet;

/**
 * Created by Nick on 03/01/2016.
 */
public class GameManager implements SBSocket.SBSocketListener {
    private RoomGame roomGame;
    private SBSocket socket;

    public GameManager(RoomGame roomGame, SBSocket socket) {
        this.roomGame = roomGame;
        this.socket = socket;
    }

    @Override
    public void onOpen(SBSocket socket) {
        // will never see
    }

    @Override
    public void onClose(SBSocket socket) {
        // the RoomConnect setup a listener for us to deal with this situation
    }

    @Override
    public void onMessage(SBSocket socket, Packet packet) {
        if (packet instanceof CreatePlayer) {
            roomGame.createPlayer(((CreatePlayer) packet).id,
                    ((CreatePlayer) packet).x,
                    ((CreatePlayer) packet).y);
        }
    }

    @Override
    public void onError(SBSocket socket, Exception exception) {

    }
}
