package com.nicktoony.cstopdown.rooms.game.ui;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.nicktoony.engine.components.Entity;
import com.nicktoony.engine.rooms.RoomGame;
import com.nicktoony.engine.services.weapons.Weapon;
import com.nicktoony.engine.services.weapons.WeaponCategory;
import com.nicktoony.engine.services.weapons.WeaponManager;

/**
 * Created by tooni on 16/06/2017.
 */
public class BuyMenu extends Entity<RoomGame> {

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
    private Label descriptionLabel;

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


        innerTable.add(new Label("Buy Menu", skin))
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

        addCategories();
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

    private WeaponManager getWeaponManager() {
        return getRoom().getWeaponManager();
    }

    private void addCategories() {
        leftTable.clearChildren();
        leftTable.align(Align.topLeft);

        // Add this.. cause... it makes fillX work ??
        leftTable.add().expandX().row();

        for (final WeaponCategory category : getWeaponManager().getWeaponCategories()) {
            leftTable.add(leftButton(category.getName(), new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);

                addWeapons(category);
                }

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    super.enter(event, x, y, pointer, fromActor);

                    descriptionLabel.setText(category.getDescription());
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    super.exit(event, x, y, pointer, toActor);

                    descriptionLabel.setText("");
                }
            })).fillX().pad(16).row();
        }

        leftTable.add(leftButton("Close", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                hide();
            }
        })).fillX().pad(16).row();

        leftTable.add().fillY().row();
    }

    private void addWeapons(WeaponCategory category) {
        leftTable.clearChildren();
        leftTable.align(Align.topLeft);
        descriptionLabel.setText("");

        // Add this.. cause... it makes fillX work ??
        leftTable.add().expandX().row();

        for (final Weapon weapon : category.getWeapons()) {
            leftTable.add(leftButton(weapon.getName(), new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);

                    buyWeapon(weapon);
                }

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    super.enter(event, x, y, pointer, fromActor);

                    descriptionLabel.setText(constructWeaponDescription(weapon));
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    super.exit(event, x, y, pointer, toActor);

                    descriptionLabel.setText("");
                }
            })).fillX().pad(16).row();
        }

        leftTable.add(leftButton("Back", new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                addCategories();
            }
        })).fillX().pad(16).row();

        leftTable.add().fillY().row();
    }

    private void addDescription() {
        rightTable.clearChildren();
        rightTable.align(Align.topLeft);
        Label desc = new Label("This is a placeholder text. A team/skin description should be here... eventually.", skin);
        desc.setWrap(true);
        rightTable.add(desc).width(Value.percentWidth(1f, rightTable));

        descriptionLabel = new Label("", skin);
        descriptionLabel.setWrap(true);
        rightTable.row();
        rightTable.add(descriptionLabel)
                .width(Value.percentWidth(1f, rightTable))
                .padTop(24);
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

    private int between(int min, int max, float value) {
        int val = Math.round(value);
        return Math.min(max, Math.max(min, val));
    }

    private void buyWeapon(Weapon weapon) {
        getRoom().getGameManager().buyWeapon(weapon);
        this.hide();
    }

    private String constructWeaponDescription(Weapon weapon) {
        StringBuilder sb = new StringBuilder();
        String newline = "\n";

        // Weapon name
        sb.append("Name: ").append(weapon.getName()).append(newline).append(newline);

        // Stats
        sb.append("Damage:         \t")
                .append(between(0, 100, weapon.getDamage() * weapon.getBullets()))
                .append("%").append(newline);
        sb.append("Accuracy:       \t")
                .append(Math.min(100, weapon.getAccuracy()))
                .append("%").append(newline);

        float rof = weapon.getRateOfFire() != - 1 ?
                weapon.getRateOfFire() :
                weapon.getCockDuration() * 20; // we reduce the impact of cocking.. otherwise our scale range would be too extreme
        sb.append("Rate of Fire:   \t")
                .append(between(0, 100, ((30f-rof)/30f)*100))
                .append("%").append(newline);

        // no idea yet!
        sb.append("Effective Range:\t")
                .append(between(0, 100, 50))
                .append("%").append(newline);
        sb.append("Speed:          \t")
                .append(between(0, 100, 50))
                .append("%").append(newline);
        sb.append("Penetration:    \t")
                .append(between(0, 100, 50))
                .append("%").append(newline);

        sb.append(newline).append(newline);
        sb.append("Clip Size:      \t").append(weapon.getClipSize()).append(newline);
        sb.append("Ammo:           \t").append(weapon.getClipTotal()).append(newline);

        sb.append(newline).append(newline);
        sb.append("Cost:           \t").append(weapon.getCost());

        return sb.toString();
    }

    public void show() {
        visible = true;
        setup();
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
