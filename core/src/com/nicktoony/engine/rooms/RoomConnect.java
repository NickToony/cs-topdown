package com.nicktoony.engine.rooms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.nicktoony.engine.components.Room;
import com.nicktoony.engine.networking.client.ClientSocket;
import com.nicktoony.engine.packets.Packet;
import com.nicktoony.engine.packets.connection.AcceptPacket;
import com.nicktoony.engine.packets.connection.ConnectPacket;
import com.nicktoony.engine.packets.connection.RejectPacket;

/**
 * Created by Nick on 03/01/2016.
 */
public abstract class RoomConnect extends Room {

    protected ClientSocket socket;
    private boolean connected = false;
    private boolean thrownError = false;

    protected enum ERRORS {
        UNKNOWN_MAP,
        NO_CONNECTION,
        REJECTED,
        EXCEPTION,
        DISCONNECTED
    }

    public RoomConnect(ClientSocket socket) {
        this.socket = socket;
    }

    @Override
    public void create(boolean render) {
        super.create(render);

        socket.addListener(new ClientSocket.SBSocketListener() {
            @Override
            public void onOpen(ClientSocket socket) {
                // Send a connect request
                socket.sendMessage(new ConnectPacket());
            }

            @Override
            public void onClose(ClientSocket socket) {
                // Either failed to connect, or rejected
                triggerPreviousRoom(ERRORS.DISCONNECTED);
            }

            @Override
            public void onMessage(ClientSocket socket, Packet packet) {
                if (packet instanceof AcceptPacket) {
                    socket.setServerConfig(((AcceptPacket) packet).serverConfig);
                    socket.setId(((AcceptPacket) packet).id);
                    connected = true;
                } else if (packet instanceof RejectPacket) {
                    // Rejected..
                    // onClose will probably be called anyway
                    triggerPreviousRoom(ERRORS.REJECTED);
                }
            }

            @Override
            public void onError(ClientSocket socket, Exception exception) {
                // Onclose will probably be called..
                triggerPreviousRoom(ERRORS.EXCEPTION);
            }
        });

        socket.open();
    }

    @Override
    public void step(float delta) {
        super.step(delta);

        socket.pushNotifications();

        if (connected) {
            FileHandle file = Gdx.files.internal("maps/" + socket.getServerConfig().sv_map + "/map.tmx");

            if (file.exists()) {
                nextRoom();
            } else {
                triggerPreviousRoom(ERRORS.UNKNOWN_MAP);
            }
        }
    }

    public abstract void nextRoom();
    public abstract void previousRoom(ERRORS error);

    private void triggerPreviousRoom(ERRORS error) {
        if (!thrownError) {
            thrownError = true;

            previousRoom(error);
        }
    }
}
