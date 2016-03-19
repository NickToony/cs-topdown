package com.nicktoony.cstopdown.rooms.mainmenu;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
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
import com.nicktoony.cstopdown.services.TextureManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nick on 19/07/15.
 */
public class RoomMainMenu extends Room {

    private final int UI_SIZE_X = 1024;
    private final int UI_SIZE_Y = 768;


    private Stage stage;
    private Viewport viewport;

    @Override
    public void create(boolean render) {
        super.create(render);

        Gdx.app.setLogLevel(Application.LOG_INFO);
        Gdx.app.log("LogTest", "Hello World");

        // A stage
        stage = new Stage(new ExtendViewport(UI_SIZE_X, UI_SIZE_Y));

        // Handle input, and add the actor
        Gdx.input.setInputProcessor(stage);

        // Table layout
        Table table = new Table();
        table.setFillParent(true);
        table.pad(40);

        // Add background
        Image background = new Image(TextureManager.getTexture("ui/main_menu/background.png"));
        background.setFillParent(true);
        stage.addActor(background);

        // Add background
        Actor menuBackground = new Image(TextureManager.getTexture("ui/main_menu/left_side_bg.png"));
        menuBackground.setPosition(30, 0);
        stage.addActor(menuBackground);

        // Define all labels
        List<Actor> labels = new ArrayList<Actor>()
        {{
                // Single player
                add(buttonWithListener(
                        new Label("Single Player", SkinManager.getUiSkin()),
                        new ClickListener() {
                            @Override
                            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                                startSinglePlayer();
                            }

                        }
                ));
                // Join server
                add(buttonWithListener(
                        new Label("Join Server", SkinManager.getUiSkin()),
                        new ClickListener() {
                            @Override
                            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                                getGame().createRoom(new RoomServerList());
                            }
                        }
                ));
                add(buttonWithListener(
                        new Label("Join Local", SkinManager.getUiSkin()),
                        new ClickListener() {
                            @Override
                            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                                SBSocket socket = getGame().getPlatformProvider().getWebSocket(
                                        "127.0.0.1", new ServerConfig().sv_port);
                                getGame().createRoom(new RoomConnect(socket));
                            }
                        }
                ));
                // Create server
                if (getGame().getPlatformProvider().canHost()) {
                    add(buttonWithListener(
                            new Label("Create Server", SkinManager.getUiSkin()),
                            new ClickListener() {
                                @Override
                                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                                    startMultiPlayer();
                                }
                            }
                    ));
                }
                // Options
                add(buttonWithListener(
                        new Label("Options", SkinManager.getUiSkin()),
                        new ClickListener() {
                            @Override
                            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                                // do nothing
                            }
                        }
                ));
                // Quit
                add(buttonWithListener(
                        new Label("Quit", SkinManager.getUiSkin()),
                        new ClickListener() {
                            @Override
                            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                                Gdx.app.exit();
                            }
                        }
                ));
            }


        };

        row(table).expandY();
        for (Actor label : labels) {
            table.add(label);
            row(table);
        }


        stage.addActor(table);
    }

    private Actor buttonWithListener(Label label, EventListener listener) {
        Button.ButtonStyle buttonStyle = new Button.ButtonStyle();
        buttonStyle.over = new SpriteDrawable(new Sprite(TextureManager.getTexture("ui/main_menu/button_hover.png")));
        buttonStyle.up = new SpriteDrawable(new Sprite(TextureManager.getTexture("ui/main_menu/button_normal.png")));
        buttonStyle.down = new SpriteDrawable(new Sprite(TextureManager.getTexture("ui/main_menu/button_active.png")));
        Button button = new Button(buttonStyle);
        button.add(label);
        button.addListener(listener);
        return button;
    }

    private Cell row(Table table) {
        return table.row().bottom().left().expandX().padBottom(20);
    }

    public void step(float delta) {
        super.step(delta);
        stage.act(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        super.render(spriteBatch);
        stage.draw();
    }

    @Override
    public void dispose(boolean render)   {
        super.dispose(render);

        stage.dispose();
    }

    private void startSinglePlayer() {

        SBServer.LoopManager loopManager = getGame().getPlatformProvider().getLoopManager();
        // If a looper isn't presented (i.e. a threading system)
        if (loopManager == null) {
            // Use the game loop instead
            loopManager = getGame();
        }
        final SBServer server = new SBLocalServer(new Logger() {
                                    @Override
                                    public void log(String string) {
                                        System.out.println(string);
                                    }

                                    @Override
                                    public void log(Exception exception) {
                                        System.out.println(exception.getMessage());
                                    }
                                }, new ServerConfig(), loopManager);


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
