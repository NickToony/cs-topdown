package com.nicktoony.cstopdown.rooms.mainmenu;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nicktoony.cstopdown.networking.CSLocalClientSocket;
import com.nicktoony.cstopdown.networking.server.CSServer;
import com.nicktoony.cstopdown.networking.server.CSServerLocal;
import com.nicktoony.cstopdown.rooms.connect.CSRoomConnect;
import com.nicktoony.cstopdown.rooms.serverlist.RoomServerList;
import com.nicktoony.engine.components.Room;
import com.nicktoony.engine.config.ServerConfig;
import com.nicktoony.engine.networking.client.ClientSocket;
import com.nicktoony.engine.networking.server.Server;
import com.nicktoony.engine.packets.Packet;
import com.nicktoony.engine.services.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nick on 19/07/15.
 */
public class RoomMainMenu extends Room {

    private final int UI_SIZE_X = 1920;
    private final int UI_SIZE_Y = 1080;

    private Stage stage;
    private Table table;

    @Override
    public void create(boolean render) {
        super.create(render);

        Gdx.app.setLogLevel(Application.LOG_INFO);
//        Gdx.app.log("LogTest", "Hello World");

        // A stage
        stage = new Stage(new StretchViewport(UI_SIZE_X, UI_SIZE_Y));

        // Handle input, and add the actor
        Gdx.input.setInputProcessor(stage);

        // Table layout
        table = new Table();
//        table.setFillParent(true);
        table.pad(40).left().bottom();

        // Add background
        Image background = new Image(getGame().getAsset("ui/main_menu/background.jpg", Texture.class));
        background.setFillParent(true);
        stage.addActor(background);

        // Add background
        Actor menuBackground = new Image(getGame().getAsset("ui/main_menu/left_side_bg.png", Texture.class));
        menuBackground.setPosition(30, 0);
        table.setBackground(new SpriteDrawable(new Sprite(getGame().getAsset("ui/main_menu/background.jpg", Texture.class))));
        stage.addActor(menuBackground);

        final Skin skin = getAsset("skins/default/uiskin.json", Skin.class);
        // Define all labels
        List<Actor> labels = new ArrayList<Actor>()
        {{
                // Single player
                add(buttonWithListener(
                        new Label("Single Player", skin),
                        new ClickListener() {
                            @Override
                            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                                startSinglePlayer();
                            }

                        }
                ));
                // Join server
                add(buttonWithListener(
                        new Label("Join Server", skin),
                        new ClickListener() {
                            @Override
                            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                                getGame().createRoom(new RoomServerList());
                            }
                        }
                ));
                add(buttonWithListener(
                        new Label("Join Local", skin),
                        new ClickListener() {
                            @Override
                            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                                ClientSocket socket = getGame().getPlatformProvider().getWebSocket(
                                        "127.0.0.1", new ServerConfig().sv_port);
                                getGame().createRoom(new CSRoomConnect(socket));
                            }
                        }
                ));
                // Create server
                if (getGame().getPlatformProvider().canHost()) {
                    add(buttonWithListener(
                            new Label("Create Server", skin),
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
                        new Label("Options", skin),
                        new ClickListener() {
                            @Override
                            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                                // do nothing
                            }
                        }
                ));
                // Quit
                add(buttonWithListener(
                        new Label("Quit", skin),
                        new ClickListener() {
                            @Override
                            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                                Gdx.app.exit();
                            }
                        }
                ));
            }


        };

        for (Actor label : labels) {
            row(table);
            table.add(label).left();
        }

        stage.addActor(table);
    }

    @Override
    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height);
        }
    }

    private Actor buttonWithListener(Label label, EventListener listener) {
        Button.ButtonStyle buttonStyle = new Button.ButtonStyle();
        buttonStyle.over = new SpriteDrawable(new Sprite(getGame().getAsset("ui/main_menu/button_hover.png", Texture.class)));
        buttonStyle.up = new SpriteDrawable(new Sprite(getGame().getAsset("ui/main_menu/button_normal.png", Texture.class)));
        buttonStyle.down = new SpriteDrawable(new Sprite(getGame().getAsset("ui/main_menu/button_active.png", Texture.class)));
        Button button = new Button(buttonStyle);
        button.add(label);
        button.addListener(listener);

        button.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);

                if (event.getTarget() == event.getListenerActor()) {
                    getAsset("sounds/rollover.ogg", Sound.class).play();
                }
            }

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                getAsset("sounds/click.ogg", Sound.class).play();

                return super.touchDown(event, x, y, pointer, button);
            }
        });
        return button;
    }

    private Cell row(Table table) {
        return table.row().padBottom(20);
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

        Server.LoopManager loopManager = getGame().getPlatformProvider().getLoopManager();
        // If a looper isn't presented (i.e. a threading system)
        if (loopManager == null) {
            // Use the game loop instead
            loopManager = getGame();
        }
        final CSServer server = new CSServerLocal(new Logger() {
                                    @Override
                                    public void log(String string) {
                                        System.out.println(string);
                                    }

                                    @Override
                                    public void log(Exception exception) {
                                        System.out.println(exception.getMessage());
                                    }
                                }, new ServerConfig(), loopManager, getGame().getPlatformProvider());


        ClientSocket socket = new CSLocalClientSocket(server);
        socket.addListener(new ClientSocket.SBSocketListener() {
            @Override
            public void onOpen(ClientSocket socket) {
                // Nothing
            }

            @Override
            public void onClose(ClientSocket socket) {
                // When the player leaves in single player, then should just close the server!
                server.dispose();
            }

            @Override
            public void onMessage(ClientSocket socket, Packet packet) {
                // do nothing
            }

            @Override
            public void onError(ClientSocket socket, Exception exception) {
                // nothing?
            }
        });

        getGame().createRoom(new CSRoomConnect(socket));
    }

    private void startMultiPlayer() {
        final CSServer server = getGame().getPlatformProvider().getLocalServer(null, new ServerConfig());

        ClientSocket socket = new CSLocalClientSocket(server);
        socket.addListener(new ClientSocket.SBSocketListener() {
            @Override
            public void onOpen(ClientSocket socket) {
                // Nothing
            }

            @Override
            public void onClose(ClientSocket socket) {
                // When the player leaves his own server, then should just close the server!
                server.dispose();
            }

            @Override
            public void onMessage(ClientSocket socket, Packet packet) {
                // do nothing
            }

            @Override
            public void onError(ClientSocket socket, Exception exception) {
                // nothing?
            }
        });

        getGame().createRoom(new CSRoomConnect(socket));
    }
}
