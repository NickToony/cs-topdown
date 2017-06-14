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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.VisUI;
import com.nicktoony.cstopdown.Strings;
import com.nicktoony.cstopdown.networking.CSLocalClientSocket;
import com.nicktoony.cstopdown.networking.server.CSServer;
import com.nicktoony.cstopdown.networking.server.CSServerLocal;
import com.nicktoony.cstopdown.rooms.connect.CSRoomConnect;
import com.nicktoony.cstopdown.rooms.serverlist.RoomServerList;
import com.nicktoony.engine.EngineConfig;
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

    private Stage stage;
    private Table table;
    private Dialog dialog;

    @Override
    public void create(boolean render) {
        super.create(render);

        Gdx.app.setLogLevel(Application.LOG_INFO);
//        Gdx.app.log("LogTest", "Hello World");

        VisUI.load();

        // A stage
        stage = new Stage(new ScreenViewport());

        // Handle input, and add the actor
        Gdx.input.setInputProcessor(stage);

        // Table layout
        table = new Table();
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
                            public void clicked(InputEvent event, float x, float y) {
                                super.clicked(event, x, y);

                                showNewGameDialog();
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
                                public void clicked(InputEvent event, float x, float y) {
                                    super.clicked(event, x, y);

                                    showCreateServerDialog();
                                }
                            }
                    ));
                }
                // Options
                add(buttonWithListener(
                        new Label("Options", skin),
                        new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                super.clicked(event, x, y);

                                showOptionsDialog();
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

        row(table);
        Image logo = new Image(getAsset("logo.png", Texture.class));
        logo.setAlign(Align.center);
        table.add(logo).left().fillX().height(150);

        for (Actor label : labels) {
            row(table);
            table.add(label).left();
        }

        stage.addActor(table);

        if (getGame().getGameConfig().name.isEmpty()) {
            showNameDialog();
        }
    }

    @Override
    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
            System.out.println(stage.getWidth());
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

    private void startSinglePlayer(ServerConfig serverConfig) {

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
                                }, serverConfig, loopManager, getGame().getPlatformProvider());


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

    private void startMultiPlayer(ServerConfig serverConfig) {
        final CSServer server = getGame().getPlatformProvider().getLocalServer(null, serverConfig);

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

    private void showNameDialog() {
        Skin skin =  getAsset(EngineConfig.Skins.SGX, Skin.class);

        // Dialog
        dialog = new Dialog("Enter name", skin);

        // Contents
        dialog.row().prefWidth(600);
        Label label = new Label(Strings.NAME_DIALOG_TEXT, skin);
        label.setWrap(true);
        label.setAlignment(Align.center);
        dialog.add(label);
        dialog.row();

        final TextField fieldName = new TextField("Player", skin);
        dialog.add(fieldName).prefWidth(300);
        dialog.row();

        Button button = new Button(skin);
        button.add(new Label(Strings.NAME_DIALOG_BUTTON, skin));
        button.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                if (!fieldName.getText().isEmpty()) {
                    dialog.hide();

                    getGame().getGameConfig().name = fieldName.getText();
                    getGame().getGameConfig().save();
                }

                return super.touchDown(event, x, y, pointer, button);
            }
        });
        dialog.add(button).padBottom(6);
        // Show
        dialog.show(stage);
    }

    private void showNewGameDialog() {
        Skin skin = getAsset(EngineConfig.Skins.SGX, Skin.class);
        SinglePlayerDialogWrapper dialogWrapper = new SinglePlayerDialogWrapper(skin);
        dialogWrapper.setListener(new SinglePlayerDialogWrapper.SuccessListener() {
            @Override
            public void onSuccess(ServerConfig serverConfig) {
                startSinglePlayer(serverConfig);
            }
        });
        dialogWrapper.show(stage);
    }

    private void showOptionsDialog() {
        Skin skin = getAsset(EngineConfig.Skins.SGX, Skin.class);
        OptionsDialogWrapper dialogWrapper = new OptionsDialogWrapper(getGame().getGameConfig(), skin);
        dialogWrapper.setListener(new OptionsDialogWrapper.SuccessListener() {
            @Override
            public void saved() {
                getGame().reconfigure();
            }
        });
        dialogWrapper.show(stage);
    }

    private void showCreateServerDialog() {
        Skin skin = getAsset(EngineConfig.Skins.SGX, Skin.class);
        CreateServerDialogWrapper dialogWrapper = new CreateServerDialogWrapper(skin);
        dialogWrapper.setListener(new SinglePlayerDialogWrapper.SuccessListener() {
            @Override
            public void onSuccess(ServerConfig serverConfig) {
                startMultiPlayer(serverConfig);
            }
        });
        dialogWrapper.show(stage);
    }
}
