package com.nicktoony.cstopdown.networking.server;

import com.nicktoony.cstopdown.rooms.game.entities.players.Player;
import com.nicktoony.engine.networking.server.Server;
import com.nicktoony.engine.networking.server.ServerClientHandler;
import com.nicktoony.engine.packets.Packet;
import com.nicktoony.cstopdown.rooms.game.entities.players.BotPlayer;

/**
 * Created by Nick on 24/03/2016.
 */
public class ServerBotClientHandler extends CSServerClientHandler {
    public ServerBotClientHandler(CSServer server) {
        super(server);
    }

    @Override
    public void sendPacket(Packet packet) {

    }

    @Override
    public void close() {

    }

    @Override
    public boolean bot() {
        return true;
    }

    @Override
    public void createPlayer() {
        super.createPlayer();
        ((BotPlayer) getPlayer()).setupBot(server, this);
    }
}
