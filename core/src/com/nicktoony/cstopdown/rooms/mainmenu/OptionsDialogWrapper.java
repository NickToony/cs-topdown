package com.nicktoony.cstopdown.rooms.mainmenu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.nicktoony.cstopdown.Strings;
import com.nicktoony.engine.EngineConfig;
import com.nicktoony.engine.config.GameConfig;
import com.nicktoony.engine.config.ServerConfig;
import com.nicktoony.engine.config.ServerConfigValidator;

/**
 * Created by Nick on 14/06/2017.
 */
public class OptionsDialogWrapper {
    private SelectBox<Resolution> fieldResolutions;
    private TextField fieldName;
    private CheckBox fieldFullscreen;
    private Dialog dialog;
    private Skin skin;
    private GameConfig gameConfig;
    private SuccessListener listener;

    public interface SuccessListener {
        void saved();
    }

    private class Resolution {
        public int x;
        public int y;

        public Resolution(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return x + "x" + y;
        }
    }
    private Resolution[] resolutions = new Resolution[] {
            new Resolution(0,0),
            new Resolution(800, 600),
            new Resolution(1024, 768),
            new Resolution(1200, 900),
            new Resolution(1280, 1024),
            new Resolution(1280, 720),
            new Resolution(1280, 800),
            new Resolution(1366, 768),
            new Resolution(1440, 900),
            new Resolution(1600, 900),
            new Resolution(1600, 1200),
            new Resolution(1680, 1050),
            new Resolution(1920, 1080)
    };

    public OptionsDialogWrapper(GameConfig gameConfig, Skin skin) {
        // Dialog
        dialog = new Dialog(Strings.OPTIONS_DIALOG_TITLE, skin);
        this.skin = skin;
        this.gameConfig = gameConfig;

        Button closeButton = new TextButton("X", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                dialog.hide();
            }
        });
        dialog.getTitleTable().add(closeButton).size(26, 26).padTop(-2);

        // Contents
        dialog.row();

        final int leftSize = 200;
        final int rightSize = 300;
        addFields(leftSize, rightSize);

        Button button = new Button(skin);
        button.add(new Label("Save", skin));
        button.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (validate() == null) {
                    save();
                    dialog.hide();
                }

                return super.touchDown(event, x, y, pointer, button);
            }
        });
        dialog.add(button).colspan(2).padBottom(6);
    }

    protected void addFields(int leftSize, int rightSize) {
        dialog.add(new Label("Player name", skin)).prefWidth(leftSize);
        fieldName = new TextField(gameConfig.name, skin);
        dialog.add(fieldName).prefWidth(rightSize);
        dialog.row();

        resolutions[0].x = gameConfig.resolution_x;
        resolutions[0].y = gameConfig.resolution_y;
        dialog.add(new Label("Resolution", skin)).prefWidth(leftSize);
        fieldResolutions = new SelectBox<Resolution>(skin);
        fieldResolutions.setItems(resolutions);
        dialog.add(fieldResolutions).prefWidth(rightSize);
        dialog.row();

        dialog.add(new Label("Fullscreen", skin)).prefWidth(leftSize);
        fieldFullscreen = new CheckBox("", skin);
        fieldFullscreen.setChecked(gameConfig.fullscreen);
        dialog.add(fieldFullscreen).prefWidth(rightSize);
        dialog.row();
    }

    public void show(Stage stage) {
        dialog.show(stage);
    }
    public void hide() {
        dialog.hide();
    }

    protected String validate() {
        String error = null;

        error = ServerConfigValidator.validateName(fieldName.getText());
        if (error != null) return error;

        return null;
    }

    protected void save() {
        gameConfig.name = fieldName.getText();
        gameConfig.fullscreen = fieldFullscreen.isChecked();
        gameConfig.resolution_x = fieldResolutions.getSelected().x;
        gameConfig.resolution_y = fieldResolutions.getSelected().y;
        gameConfig.save();

        listener.saved();
    }

    public void setListener(SuccessListener listener) {
        this.listener = listener;
    }
}
