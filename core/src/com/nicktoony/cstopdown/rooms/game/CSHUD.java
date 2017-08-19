package com.nicktoony.cstopdown.rooms.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.nicktoony.cstopdown.rooms.game.entities.players.Player;
import com.nicktoony.cstopdown.rooms.game.entities.players.UserPlayer;
import com.nicktoony.engine.entities.HUD;

/**
 * Created by Nick on 18/03/2016.
 */
public class CSHUD extends HUD {

    private BitmapFont hudFont;
    private BitmapFont chatFont;
    private BitmapFont nameFont;
    private Stage stage;
    private ScrollPane chatScrollPane;
    private Table chatTable;
    private Table uiTable;
    private Container<ScrollPane> chatContainer;
    private boolean chatActive = false;
    private boolean chatJustAdded = false;
    private Label ammoLabel;
    private Label healthLabel;
    boolean leftJustPressed = false;
    boolean rightJustPressed = false;
    private OptionsMenu optionsMenu;
    private Scoreboard scoreboard;
    private Label playerLabels[];
    private FragUI fragContainer;

    @Override
    protected void create(boolean render) {
        if (render) {
            hudFont = new BitmapFont();
            hudFont.setColor(0.91f, 0.73f, 0.23f, 0.95f);
            hudFont.getData().scale(2f);

            chatFont = new BitmapFont();
            chatFont.getData().markupEnabled = true;

            nameFont = new BitmapFont();
            nameFont.setColor(0.91f, 0.73f, 0.23f, 0.95f);
            nameFont.getData().scale(1f);

            // A stage
            stage = new Stage(new ScreenViewport());
//            stage.setViewport();
            Gdx.input.setInputProcessor(stage);

            // Chat container
            chatContainer = new Container<ScrollPane>();
            stage.addActor(chatContainer);
            // Chat background
            SpriteDrawable chatBackgroundDrawable =
                    new SpriteDrawable(new Sprite(getAsset("ui/hud/chat_bg.png", Texture.class)));
            chatContainer.setBackground(chatBackgroundDrawable);
            // Set size
            chatContainer.setSize(chatBackgroundDrawable.getSprite().getWidth() * 1.5f,
                    chatBackgroundDrawable.getSprite().getHeight() * 1.5f);
            // Set position
            chatContainer.setPosition(20, 100);
            chatContainer.pad(10);

            // Chat table
            chatTable = new Table();
            chatTable.bottom();

            // Scroll pane for chat table
            chatScrollPane = new ScrollPane(chatTable);
            chatScrollPane.setScrollbarsOnTop(true);
            chatScrollPane.setFlickScroll(false);
            chatContainer.setActor(chatScrollPane);
            chatContainer.fill();
            chatContainer.align(Align.bottomLeft);

            addChatLine("[YELLOW]Chat initiated. Press Y to focus.");

            // Ammo label
            Label.LabelStyle style = new Label.LabelStyle();
            style.font = hudFont;
            style.fontColor = hudFont.getColor();
            ammoLabel = new Label("", style);
            ammoLabel.setPosition(50, 10);
            ammoLabel.setAlignment(Align.bottomLeft);
            stage.addActor(ammoLabel);

            // Health label
            healthLabel = new Label("", style);
            healthLabel.setPosition(Gdx.graphics.getWidth() - 50, 10);
            healthLabel.setAlignment(Align.bottomRight);
            stage.addActor(healthLabel);

            // Player labels
            playerLabels = new Label[getRoom().getConfig().sv_max_players];
            Label.LabelStyle playerLabelStyle = new Label.LabelStyle();
            playerLabelStyle.font = nameFont;
            playerLabelStyle.fontColor = nameFont.getColor();
            for (int i = 0; i < playerLabels.length; i++) {
                Label label = new Label("", playerLabelStyle);
                label.setVisible(false);
                label.setAlignment(Align.center);
                stage.addActor(label);
                playerLabels[i] = label;
            }

            // UI Table
            uiTable = new Table();
            uiTable.setFillParent(true);
            stage.addActor(uiTable);

            // Frag container
            fragContainer = new FragUI();
            uiTable.add(fragContainer).expandX().expandY().top().right();
//            fragContainer.setPosition(Gdx.graphics.getWidth() - 0, Gdx.graphics.getHeight() - 0);

            this.optionsMenu = (OptionsMenu) getRoom().addSelfManagedEntity(new OptionsMenu());
            this.scoreboard = (Scoreboard) getRoom().addSelfManagedEntity(new Scoreboard());
        }
    }

    public void addChatLine(String string) {
        // Add a new row
        chatTable.row().expandX().align(Align.bottom);
        // Define the label
        Label label = new Label(string,
                new Label.LabelStyle(chatFont, Color.WHITE));
        label.setWrap(true);
        chatTable.add(label).fillX().pad(2);
        chatJustAdded = true;
    }

    private void addChatLine(String name, String nameColour, String message, String messageColour) {
        addChatLine("[" + nameColour + "]" + name
                + ": [" + messageColour + "]" + message);
    }

    @Override
    public void resize(int x, int y) {
        super.resize(x, y);

        if (stage != null) {
            stage.getViewport().update(x, y, true);
        }

        optionsMenu.resize(x, y);
        scoreboard.resize(x, y);
    }

    @Override
    public void step(float delta) {
        stage.act(delta);

        fragContainer.update();

        // Show menu (toggle)
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (optionsMenu.isVisible()) {
                optionsMenu.hide();
                Gdx.input.setInputProcessor(this.stage);
            } else {
                Gdx.input.setInputProcessor(optionsMenu.getStage());
                optionsMenu.show();
            }
        }

        // Scoreboard (hold)
        boolean tab = Gdx.input.isKeyPressed(Input.Keys.TAB);
        if (tab && !scoreboard.isVisible()) {
            scoreboard.show();
            Gdx.input.setInputProcessor(scoreboard.getStage());
        } else if (!tab && scoreboard.isVisible()) {
            scoreboard.hide();
            Gdx.input.setInputProcessor(scoreboard.getStage());
        }

        if (optionsMenu.isVisible()) {
            optionsMenu.step(delta);
            return;
        }

        if (scoreboard.isVisible()) {
            scoreboard.step(delta);
//            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Y)) {
            chatActive = !chatActive;
            chatScrollPane.setFlickScroll(chatActive);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Y)) {
            chatActive = !chatActive;
            chatScrollPane.setFlickScroll(chatActive);
        }

        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) { leftJustPressed = false; }
        if (!Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) { rightJustPressed = false; }

        if (!getMouse()) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && !leftJustPressed) {
                getRoom().getGameManager().spectateNext();
                leftJustPressed = true;
            } else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT) && !rightJustPressed) {
                getRoom().getGameManager().spectatePrevious();
                rightJustPressed = true;
            }
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch) {

        if (optionsMenu.isVisible()) {
            optionsMenu.render(spriteBatch);
            return;
        }

        if (scoreboard.isVisible()) {
            scoreboard.render(spriteBatch);
//            return;
        }




        Player playerSnap = getRoom().getMap().getEntitySnap();

        if (playerSnap != null) {
            healthLabel.setText("" + playerSnap.getHealth());

            if (playerSnap.getCurrentWeaponObject() != null) {
                StringBuilder b = new StringBuilder();
                b.append(playerSnap.getCurrentWeaponObject().bulletsIn);
                b.append(" | ");
                b.append(playerSnap.getCurrentWeaponObject().bulletsOut);
                b.append("      ");
                b.append(playerSnap.getCurrentWeaponObject().getWeapon(getRoom().getWeaponManager()).getName());
                ammoLabel.setText(b.toString());
            }
        }

        // Draw names
        int i = 0;
        for (Player player : getRoom().getGameManager().getPlayers()) {
            boolean isUserPlayer = playerSnap instanceof UserPlayer;
            boolean showName = (isUserPlayer && player != playerSnap && playerSnap.getTeam() == player.getTeam())
                    || (!isUserPlayer);

            if (showName) {
                if (i < playerLabels.length) {
                    Label label = playerLabels[i];
                    float zoom = getRoom().getMap().getCameraZoom();
                    float X = (player.getX() - getRoom().getMap().getCameraCenterX())/(100*zoom)*100;
                    float Y = (player.getY() - getRoom().getMap().getCameraCenterY())/(100*zoom)*100;
                    float offsetX = ((getRoom().getMap().getCamera().viewportWidth/2));
                    float offsetY = ((getRoom().getMap().getCamera().viewportHeight/2)) + 40;
                    label.setPosition(X + offsetX,Y + offsetY);
                    label.setText(getRoom().getGameManager().getPlayerDetails(player.getId()).name);
                    label.setVisible(true);

                    i ++;
                }
            }
        }

        while (i < playerLabels.length) {
            playerLabels[i].setVisible(false);
            i++;
        }

        stage.draw();

        if (chatJustAdded) {
            chatScrollPane.scrollTo(0, 0, 0, 0);
            chatJustAdded = false;
        }



    }

    @Override
    public void dispose(boolean render) {
        hudFont.dispose();
        chatFont.dispose();
        stage.dispose();

        optionsMenu.dispose(render);
        scoreboard.dispose(render);

        if (render) {
            fragContainer.dispose();
        }
    }

    @Override
    public boolean getMouse() {
        return (chatActive || optionsMenu.isVisible());
    }

    public void addFrag(FragUI.Frag frag) {
        fragContainer.addFrag(frag);
    }
}
