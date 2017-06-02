package com.nicktoony.engine.rooms;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.nicktoony.engine.components.Room;
import com.nicktoony.engine.networking.client.ClientSocket;
import com.nicktoony.engine.packets.Packet;
import com.nicktoony.engine.packets.connection.AcceptPacket;
import com.nicktoony.engine.packets.connection.ConnectPacket;
import com.nicktoony.engine.packets.connection.MapPacket;
import com.nicktoony.engine.packets.connection.RejectPacket;
import com.nicktoony.engine.services.SkinManager;

/**
 * Created by Nick on 03/01/2016.
 */
public abstract class RoomConnect extends Room {

    private final int UI_SIZE_X = 1920;
    private final int UI_SIZE_Y = 1080;

    private Stage uiStage;
    private Table uiTable;
    private Label uiLabel;

    protected ClientSocket socket;
    private boolean connected = false;
    private boolean thrownError = false;
    private String currentTask = "";
    protected MapPacket mapWrapper = null;
    enum STAGE {
        REQUEST_MAP,
        RECEIVE_MAP,
        FINISHED
    }
    private STAGE stage = STAGE.REQUEST_MAP;

    protected enum ERRORS {
        UNKNOWN_MAP,
        NO_CONNECTION,
        REJECTED,
        EXCEPTION,
        DISCONNECTED
    }

    public RoomConnect(ClientSocket socket) {
        this.socket = socket;
        this.currentTask = "Connecting...";

        uiStage = new Stage(new StretchViewport(UI_SIZE_X, UI_SIZE_Y));

        // Table layout
        uiTable = new Table();
//        table.setFillParent(true);
        uiTable.pad(40).left().bottom();

        uiLabel = new Label("", SkinManager.getUiSkin());
        uiLabel.setColor(Color.WHITE);
        uiStage.addActor(uiLabel);
        uiLabel.setPosition(UI_SIZE_X/2, UI_SIZE_Y/2);

//        uiStage.addActor(uiTable);

    }

    @Override
    public void create(boolean render) {
        super.create(render);

        socket.addListener(new ClientSocket.SBSocketListener() {
            @Override
            public void onOpen(ClientSocket socket) {
                // Send a connect request
                socket.sendMessage(new ConnectPacket());
                currentTask = "Sending client info...";
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
                } else if (packet instanceof MapPacket) {
                    mapWrapper = ((MapPacket) packet);
                    currentTask = "Processing map...";
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
        uiLabel.setText(currentTask);

        if (connected) {

            switch (stage) {
                case REQUEST_MAP:
                    socket.sendMessage(new MapPacket());
                    stage = STAGE.RECEIVE_MAP;
                    currentTask = "Downloading map...";
                    break;

                case RECEIVE_MAP:
                    if (mapWrapper != null) {
                        stage = STAGE.FINISHED;
                    }
                    break;

                case FINISHED:
                    nextRoom();
                    break;
            }

//            FileHandle file = Gdx.files.internal("maps/" + socket.getServerConfig().sv_map + "/map.tmx");
//
//            if (file.exists()) {
//                nextRoom();
//            } else {
//                triggerPreviousRoom(ERRORS.UNKNOWN_MAP);
//            }
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        super.render(spriteBatch);
        uiStage.draw();
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
