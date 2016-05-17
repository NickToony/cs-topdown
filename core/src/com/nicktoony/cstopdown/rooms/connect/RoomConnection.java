package com.nicktoony.cstopdown.rooms.connect;

import com.nicktoony.cstopdown.rooms.game.RoomGame;
import com.nicktoony.cstopdown.rooms.mainmenu.RoomMainMenu;
import com.nicktoony.engine.networking.client.ClientSocket;
import com.nicktoony.engine.rooms.connect.RoomConnect;

/**
 * Created by Nick on 16/05/2016.
 */
public class RoomConnection extends RoomConnect {
    public RoomConnection(ClientSocket socket) {
        super(socket);
    }

    @Override
    public void nextRoom() {
        getGame().createRoom(new RoomGame(socket));
    }

    @Override
    public void previousRoom(ERRORS error) {
        getGame().createRoom(new RoomMainMenu());
    }


}
