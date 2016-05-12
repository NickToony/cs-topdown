package com.nicktoony.cstopdown.rooms.connect;

import com.nicktoony.cstopdown.components.Room;
import com.nicktoony.cstopdown.networking.client.SBLocalSocket;
import com.nicktoony.cstopdown.networking.client.SBSocket;
import com.nicktoony.cstopdown.networking.packets.Packet;
import com.nicktoony.cstopdown.networking.packets.connection.AcceptPacket;
import com.nicktoony.cstopdown.networking.packets.connection.ConnectPacket;
import com.nicktoony.cstopdown.networking.packets.connection.RejectPacket;
import com.nicktoony.cstopdown.rooms.game.RoomGame;
import com.nicktoony.cstopdown.rooms.mainmenu.RoomMainMenu;

/**
 * Created by Nick on 03/01/2016.
 */
public class RoomConnect extends Room {

    private SBSocket socket;
    private boolean connected = false;

    public RoomConnect(SBSocket socket) {
        this.socket = socket;
    }

    @Override
    public void create(boolean render) {
        super.create(render);

        socket.addListener(new SBSocket.SBSocketListener() {
            @Override
            public void onOpen(SBSocket socket) {
                // Send a connect request
                socket.sendMessage(new ConnectPacket());
            }

            @Override
            public void onClose(SBSocket socket) {
                // Either failed to connect, or rejected
                getGame().createRoom(new RoomMainMenu());
            }

            @Override
            public void onMessage(SBSocket socket, Packet packet) {
                if (packet instanceof AcceptPacket) {
                    socket.setServerConfig(((AcceptPacket) packet).serverConfig);
                    socket.setId(((AcceptPacket) packet).id);
                    connected = true;
                } else if (packet instanceof RejectPacket) {
                    // Rejected..
                    // onClose will probably be called anyway
                }
            }

            @Override
            public void onError(SBSocket socket, Exception exception) {
                // Onclose will probably be called..
            }
        });

        socket.open();
    }

    @Override
    public void step(float delta) {
        super.step(delta);

        socket.pushNotifications();

        if (connected) {
            if (socket instanceof SBLocalSocket) {
                getGame().createRoom(new RoomGame(socket));
            } else {
                getGame().createRoom(((SBLocalSocket)socket).getServer().getRoom());
            }

        }
    }
}
