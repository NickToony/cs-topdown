package com.nicktoony.cstopdown.rooms.game;

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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.nicktoony.cstopdown.rooms.mainmenu.OptionsDialogWrapper;
import com.nicktoony.engine.EngineConfig;
import com.nicktoony.engine.components.Entity;
import com.nicktoony.engine.rooms.RoomGame;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 14/06/2017.
 */
public class OptionsMenu extends Entity<RoomGame> {

    private Stage stage;
    private Table table;
    private boolean visible;

    @Override
    protected void create(boolean render) {
        Gdx.app.setLogLevel(Application.LOG_INFO);
//        Gdx.app.log("LogTest", "Hello World");

        // A stage
        stage = new Stage(new ScreenViewport());

        // Table layout
        table = new Table();
        table.pad(40).left().bottom();

        // Add background
        Image background = new Image(getAsset("ui/main_menu/background.jpg", Texture.class));
        background.setFillParent(true);
        background.setColor(1,1,1,.5f);
        stage.addActor(background);

        // Add background
        Actor menuBackground = new Image(getAsset("ui/main_menu/left_side_bg.png", Texture.class));
        menuBackground.setPosition(30, 0);
        table.setBackground(new SpriteDrawable(new Sprite(getAsset("ui/main_menu/background.jpg", Texture.class))));
        stage.addActor(menuBackground);

        final Skin skin = getAsset("skins/default/uiskin.json", Skin.class);
        // Define all labels
        List<Actor> labels = new ArrayList<Actor>()
        {{
            // Disconnect
            add(buttonWithListener(
                    new Label("Resume", skin),
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            super.clicked(event, x, y);

                            hide();
                        }
                    }
            ));
            // Disconnect
            add(buttonWithListener(
                    new Label("Disconnect", skin),
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            super.clicked(event, x, y);

                            // Let's get out of here
                            getRoom().getGameManager().disconnect();
                        }
                    }
            ));
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
                        public void clicked(InputEvent event, float x, float y) {
                            super.clicked(event, x, y);

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
    }

    private Actor buttonWithListener(Label label, EventListener listener) {
        Button.ButtonStyle buttonStyle = new Button.ButtonStyle();
        buttonStyle.over = new SpriteDrawable(new Sprite(getAsset("ui/main_menu/button_hover.png", Texture.class)));
        buttonStyle.up = new SpriteDrawable(new Sprite(getAsset("ui/main_menu/button_normal.png", Texture.class)));
        buttonStyle.down = new SpriteDrawable(new Sprite(getAsset("ui/main_menu/button_active.png", Texture.class)));
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
        if (visible)
            stage.act(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        if (visible) {
            stage.draw();
        }

    }

    @Override
    public void dispose(boolean render)   {
        stage.dispose();
    }

    @Override
    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
        System.out.println("RESIZED");
    }

    public void show() {
        visible = true;
        System.out.println("SHOW");
    }

    public void hide() {
        visible = false;
        System.out.println("HIDE");
    }

    public boolean isVisible() {
        return visible;
    }

    public Stage getStage() {
        return stage;
    }

    private void showOptionsDialog() {
        Skin skin = getAsset(EngineConfig.Skins.SGX, Skin.class);
        OptionsDialogWrapper dialogWrapper = new OptionsDialogWrapper(getRoom().getGame().getGameConfig(), skin);
        dialogWrapper.setListener(new OptionsDialogWrapper.SuccessListener() {
            @Override
            public void saved() {
                getRoom().getGame().reconfigure();
            }
        });
        dialogWrapper.show(stage);
    }
}
