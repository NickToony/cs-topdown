package com.nick.ant.towerdefense.rooms;

import com.nick.ant.towerdefense.networking.client.CSClient;

/**
 * Created by Nick on 15/02/2015.
 */
public class RoomConnect extends Room {

    private CSClient client;

    public RoomConnect(CSClient client) {
        this.client = client;
    }

    @Override
    public void create() {

        RoomGameRender roomGame = new RoomGameRender(client);
        if (client.connect(roomGame)) {
            navigateToRoom(roomGame);
        }
    }
}
