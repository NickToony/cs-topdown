package com.nicktoony.cstopdown.rooms.game;

import com.nicktoony.cstopdown.networking.client.SBSocket;
import com.nicktoony.cstopdown.networking.packets.connection.LoadedPacket;
import com.nicktoony.cstopdown.networking.packets.game.CreatePlayerPacket;
import com.nicktoony.cstopdown.networking.packets.Packet;
import com.nicktoony.cstopdown.networking.packets.player.PlayerUpdatePacket;
import com.nicktoony.cstopdown.rooms.game.entities.players.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nick on 03/01/2016.
 */
public class GameManager implements SBSocket.SBSocketListener {
    private RoomGame roomGame;
    private SBSocket socket;
    private Map<Integer, Player> playerIdMap = new HashMap<Integer, Player>();
    private long initialTimestamp;

    public GameManager(RoomGame roomGame, SBSocket socket) {
        this.roomGame = roomGame;
        this.socket = socket;

        socket.sendMessage(new LoadedPacket());

        initialTimestamp = System.currentTimeMillis();
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
        if (packet instanceof CreatePlayerPacket) {
            Player player = roomGame.createPlayer(((CreatePlayerPacket) packet).id,
                    ((CreatePlayerPacket) packet).x,
                    ((CreatePlayerPacket) packet).y);
            playerIdMap.put(((CreatePlayerPacket) packet).id, player);
        } else if (packet instanceof PlayerUpdatePacket) {
            PlayerUpdatePacket castPacket = (PlayerUpdatePacket) packet;
            if (socket.getId() != castPacket.id) {
                Player player = playerIdMap.get(castPacket.id);
                if (player != null) {
                    player.setPosition(castPacket.x, castPacket.y);
                    player.setDirection(castPacket.direction);
                }
            }
        }
    }

    @Override
    public void onError(SBSocket socket, Exception exception) {

    }

    public long getTimestamp() {
        return (System.currentTimeMillis() - initialTimestamp);
    }
}
