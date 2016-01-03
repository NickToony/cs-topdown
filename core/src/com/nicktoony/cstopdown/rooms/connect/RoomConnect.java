package com.nicktoony.cstopdown.rooms.connect;

import com.badlogic.gdx.Gdx;
import com.nicktoony.cstopdown.components.Room;
import com.nicktoony.cstopdown.networking.client.SBSocket;
import com.nicktoony.cstopdown.networking.packets.AcceptPacket;
import com.nicktoony.cstopdown.networking.packets.ConnectPacket;
import com.nicktoony.cstopdown.networking.packets.Packet;
import com.nicktoony.cstopdown.networking.packets.RejectPacket;
import com.nicktoony.cstopdown.rooms.game.RoomGame;
import com.nicktoony.cstopdown.rooms.mainmenu.RoomMainMenu;

/**
 * Created by Nick on 03/01/2016.
 */
public class RoomConnect extends Room {

    private SBSocket socket;

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
                    getGame().createRoom(new RoomGame());

                    Gdx.app.log("CONNECTED", "SUCCESS");
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
}
