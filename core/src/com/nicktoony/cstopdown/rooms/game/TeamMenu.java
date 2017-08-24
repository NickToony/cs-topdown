package com.nicktoony.cstopdown.rooms.game;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.nicktoony.cstopdown.mods.gamemode.PlayerModInterface;
import com.nicktoony.engine.components.Entity;
import com.nicktoony.engine.rooms.RoomGame;

/**
 * Created by tooni on 16/06/2017.
 */
public class TeamMenu extends Entity<RoomGame> {

    private Stage stage;
    private Table table;
    private Table innerTable;
    private Table leftTable;
    private Table rightTable;
    private boolean visible;
    private Skin skin;

    private Pixmap backgroundPixmap;
    private Texture backgroundTexture;
    private TextureRegion backgroundTextureRegion;
    private TextureRegionDrawable background;

    @Override
    protected void create(boolean render) {

        createBackground();
        skin = getAsset("skins/tracerui/tracer-ui.json", Skin.class);

        setup();
    }

    private void setup() {
        // Create stage
        stage = new Stage(new ScreenViewport());

        // Create base table
        table = new Table(skin);
        table.setFillParent(true);
        stage.addActor(table);

        innerTable = new Table(skin);
        innerTable.setBackground(background);

        // Set to 70% of screen
        table.add(innerTable)
                .width(Value.percentWidth(.70f, table))
                .height(Value.percentHeight(.70f, table))
                .maxWidth(1000)
                .minWidth(700)
                .minHeight(600);


        innerTable.add(new Label("Join a Team", skin))
                .align(Align.left).pad(16)
                .row();

        // Add left side
        leftTable = new Table(skin);
        innerTable.add(leftTable)
                .fillY()
                .expandY()
                .width(Value.percentWidth(.30f, innerTable));

        // Add right side
        rightTable = new Table(skin);
        innerTable.add(rightTable).fillY().width(Value.percentWidth(.70f, innerTable));

        addTeams();
        addDescription();
    }

    private void createBackground() {
        backgroundPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA4444);
        Color color = Color.BLACK;
        color.a = 0.7f;
        backgroundPixmap.setColor(color);
        backgroundPixmap.fill();

        backgroundTexture = new Texture(backgroundPixmap);
        backgroundTextureRegion = new TextureRegion(backgroundTexture);
        background = new TextureRegionDrawable(backgroundTextureRegion);
    }

    private void addTeams() {
        leftTable.clearChildren();
        leftTable.align(Align.topLeft);

        // Add this.. cause... it makes fillX work ??
        leftTable.add().expandX().row();

        leftTable.add(leftButton("Counter-Terrorists", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                joinTeam(PlayerModInterface.TEAM_CT);
            }
        })).fillX().pad(16).row();

        leftTable.add(leftButton("Terrorists", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                joinTeam(PlayerModInterface.TEAM_T);
            }
        })).fillX().pad(16).row();

        leftTable.add(leftButton("Spectators", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                joinTeam(PlayerModInterface.TEAM_SPECTATE);
            }
        })).fillX().pad(16).row();

        leftTable.add().fillY().row();
    }

    private void joinTeam(int team) {
        getRoom().getGameManager().joinTeam(team);
        this.hide();
    }

    private void addDescription() {
        rightTable.clearChildren();
        rightTable.align(Align.topLeft);
        Label desc = new Label("This is a placeholder text. A team/skin description should be here... eventually.", skin);
        desc.setWrap(true);
        rightTable.add(desc).width(Value.percentWidth(1f, rightTable));
    }

    private TextButton leftButton(String text, EventListener listener) {
        TextButton button = new TextButton(text, skin);
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

    @Override
    public void step(float delta) {
        if (visible)
            stage.act(delta);
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        if (visible)
            stage.draw();
    }

    @Override
    public void dispose(boolean render) {
        stage.dispose();
        backgroundPixmap.dispose();
        backgroundTexture.dispose();
    }

    @Override
    public void resize(int x, int y) {
        super.resize(x, y);

        if (stage != null) {
            stage.dispose();
            setup();
        }
    }

    public void show() {
        visible = true;
    }

    public void hide() {
        visible = false;
    }

    public boolean isVisible() {
        return visible;
    }

    public Stage getStage() {
        return stage;
    }


}
