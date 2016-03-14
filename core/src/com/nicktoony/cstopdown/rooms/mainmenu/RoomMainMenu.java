package com.nicktoony.cstopdown.rooms.mainmenu;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.nicktoony.cstopdown.components.Room;
import com.nicktoony.cstopdown.networking.client.SBLocalSocket;
import com.nicktoony.cstopdown.networking.client.SBSocket;
import com.nicktoony.cstopdown.networking.packets.Packet;
import com.nicktoony.cstopdown.networking.server.SBLocalServer;
import com.nicktoony.cstopdown.networking.server.SBServer;
import com.nicktoony.cstopdown.config.ServerConfig;
import com.nicktoony.cstopdown.rooms.connect.RoomConnect;
import com.nicktoony.cstopdown.rooms.serverlist.RoomServerList;
import com.nicktoony.cstopdown.services.Logger;
import com.nicktoony.cstopdown.services.SkinManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nick on 19/07/15.
 */
public class RoomMainMenu extends Room {

    private Stage stage;

    @Override
    public void create(boolean render) {
        super.create(render);

        Gdx.app.setLogLevel(Application.LOG_INFO);
        Gdx.app.log("LogTest", "Hello World");

        // A stage
        stage = new Stage(new StretchViewport(getGame().getGameConfig().game_resolution_x,
                getGame().getGameConfig().game_resolution_y));

        // Handle input, and add the actor
        Gdx.input.setInputProcessor(stage);

        // Table layout
        Table table = new Table();
        table.setFillParent(true);
        table.pad(40);

        // Define all labels
        List<Label> labels = new ArrayList<Label>()
        {{
                // Single player
                add(newLabelWithListener(
                        new Label("Single Player", SkinManager.getUiSkin()),
                        new ClickListener() {
                            @Override
                            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                                startSinglePlayer();
                            }

                        }
                ));
                // Join server
                add(newLabelWithListener(
                        new Label("Join Server", SkinManager.getUiSkin()),
                        new ClickListener() {
                            @Override
                            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
                            {
                                getGame().createRoom(new RoomServerList());
                            }
                        }
                ));
                // Create server
                if (getGame().getPlatformProvider().canHost()) {
                    add(newLabelWithListener(
                            new Label("Create Server", SkinManager.getUiSkin()),
                            new ClickListener() {
                                @Override
                                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                                    startMultiPlayer();
                                }
                            }
                    ));
                }
                add(new Label("Options", SkinManager.getUiSkin()));
                add(new Label("Quit", SkinManager.getUiSkin()));
            }


        };

        row(table).expandY();
        for (Label label : labels) {
            table.add(label);
            row(table);
        }


        stage.addActor(table);
    }

    private Label newLabelWithListener(Label label, EventListener listener) {
        label.addListener(listener);
        return label;
    }

    private Cell row(Table table) {
        return table.row().bottom().left().expandX().padBottom(20);
    }

    public void step() {
        super.step();
        stage.act(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        super.render(spriteBatch);
        stage.draw();
    }

    @Override
    public void dispose()   {
        super.dispose();
    }

    private void startSinglePlayer() {
        final SBServer server = new SBLocalServer(new Logger() {
                                    @Override
                                    public void log(String string) {
                                        System.out.println(string);
                                    }

                                    @Override
                                    public void log(Exception exception) {
                                        System.out.println(exception.getMessage());
                                    }
                                }, new ServerConfig(), getGame().getPlatformProvider().getLoopManager());


        SBSocket socket = new SBLocalSocket(server);
        socket.addListener(new SBSocket.SBSocketListener() {
            @Override
            public void onOpen(SBSocket socket) {
                // Nothing
            }

            @Override
            public void onClose(SBSocket socket) {
                // When the player leaves in single player, then should just close the server!
                server.dispose();
            }

            @Override
            public void onMessage(SBSocket socket, Packet packet) {
                // do nothing
            }

            @Override
            public void onError(SBSocket socket, Exception exception) {
                // nothing?
            }
        });

        getGame().createRoom(new RoomConnect(socket));
    }

    private void startMultiPlayer() {
        final SBServer server = getGame().getPlatformProvider().getLocalServer(null, new ServerConfig());

        SBSocket socket = new SBLocalSocket(server);
        socket.addListener(new SBSocket.SBSocketListener() {
            @Override
            public void onOpen(SBSocket socket) {
                // Nothing
            }

            @Override
            public void onClose(SBSocket socket) {
                // When the player leaves his own server, then should just close the server!
                server.dispose();
            }

            @Override
            public void onMessage(SBSocket socket, Packet packet) {
                // do nothing
            }

            @Override
            public void onError(SBSocket socket, Exception exception) {
                // nothing?
            }
        });

        getGame().createRoom(new RoomConnect(socket));
    }
}
