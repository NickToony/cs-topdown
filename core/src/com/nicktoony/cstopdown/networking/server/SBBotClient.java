package com.nicktoony.cstopdown.networking.server;

import com.nicktoony.cstopdown.networking.packets.Packet;
import com.nicktoony.cstopdown.rooms.game.entities.players.BotPlayer;

/**
 * Created by Nick on 24/03/2016.
 */
public class SBBotClient extends SBClient {
    public SBBotClient(SBServer server) {
        super(server);
    }

    @Override
    public void sendPacket(Packet packet) {

    }

    @Override
    public void close() {

    }

    @Override
    public boolean isBot() {
        return true;
    }

    @Override
    protected void createPlayer(float x, float y) {
        super.createPlayer(x, y);

        if (player != null) {
            ((BotPlayer) player).setupBot(server, this);
        }
    }
}
