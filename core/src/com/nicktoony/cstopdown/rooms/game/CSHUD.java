package com.nicktoony.cstopdown.rooms.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.nicktoony.cstopdown.rooms.game.entities.players.Player;
import com.nicktoony.engine.entities.HUD;

/**
 * Created by Nick on 18/03/2016.
 */
public class CSHUD extends HUD {

    private final int UI_SIZE_X = 1024;
    private final int UI_SIZE_Y = 768;
    private BitmapFont hudFont;
    private BitmapFont chatFont;
    private Stage stage;
    private ScrollPane chatScrollPane;
    private Table chatTable;
    private Container<ScrollPane> chatContainer;
    private boolean chatActive = false;
    private boolean chatJustAdded = false;
    private Label ammoLabel;
    private Label healthLabel;
    boolean leftJustPressed = false;
    boolean rightJustPressed = false;

    @Override
    protected void create(boolean render) {
        if (render) {
            hudFont = new BitmapFont();
            hudFont.setColor(0.91f, 0.73f, 0.23f, 0.95f);
            hudFont.getData().scale(2f);

            chatFont = new BitmapFont();
            chatFont.getData().markupEnabled = true;

            // A stage
            stage = new Stage();
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
    public void step(float delta) {
        stage.act(delta);

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

        Player player = getRoom().getMap().getEntitySnap();
        if (player != null ) {
            healthLabel.setText("" + player.getHealth());

            if (player.getCurrentWeaponObject() != null) {
                StringBuilder b = new StringBuilder();
                b.append(player.getCurrentWeaponObject().bulletsIn);
                b.append(" | ");
                b.append(player.getCurrentWeaponObject().bulletsOut);
                b.append("      ");
                b.append(player.getCurrentWeaponObject().getWeapon(getRoom().getWeaponManager()).getName());
                ammoLabel.setText(b.toString());
            }
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
    }

    @Override
    public boolean getMouse() {
        return (chatActive);
    }
}
