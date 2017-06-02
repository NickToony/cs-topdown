package com.nicktoony.cstopdown.rooms.connect;

import com.nicktoony.cstopdown.rooms.game.CSRoomGame;
import com.nicktoony.engine.rooms.RoomGame;
import com.nicktoony.cstopdown.rooms.mainmenu.RoomMainMenu;
import com.nicktoony.engine.networking.client.ClientSocket;
import com.nicktoony.engine.rooms.RoomConnect;

/**
 * Created by Nick on 16/05/2016.
 */
public class CSRoomConnect extends RoomConnect {
    public CSRoomConnect(ClientSocket socket) {
        super(socket);
    }

    @Override
    public void nextRoom() {
        getGame().createRoom(new CSRoomGame(socket, map));
    }

    @Override
    public void previousRoom(ERRORS error) {
        getGame().createRoom(new RoomMainMenu());
    }


}
