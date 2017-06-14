package com.nicktoony.cstopdown.rooms.mainmenu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.nicktoony.cstopdown.Strings;
import com.nicktoony.engine.EngineConfig;
import com.nicktoony.engine.config.ServerConfig;
import com.nicktoony.engine.config.ServerConfigValidator;

/**
 * Created by Nick on 14/06/2017.
 */
public class SinglePlayerDialogWrapper {
    private TextField fieldMaxPlayers;
    private TextField fieldBots;
    private SelectBox<String> fieldMap;
    private CheckBox fieldPlayerCollisions;
    private TextField fieldPlayerSpeed;
    private TextField fieldFreezeTime;
    private TextField fieldRoundTime;
    private TextField fieldName;
    protected Dialog dialog;
    protected Skin skin;
    private SuccessListener listener;

    public interface SuccessListener {
        void onSuccess(ServerConfig serverConfig);
    }

    public SinglePlayerDialogWrapper(Skin skin) {
        // Dialog
        dialog = new Dialog(Strings.SP_DIALOG_TITLE, skin);
        this.skin = skin;

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
        ServerConfig serverConfig = new ServerConfig();
        addFields(serverConfig, leftSize, rightSize);

        Button button = new Button(skin);
        button.add(new Label("Start", skin));
        button.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (validate() == null) {
                    dialog.hide();
                    listener.onSuccess(output());
                } else {

                }

                return super.touchDown(event, x, y, pointer, button);
            }
        });
        dialog.add(button).colspan(2).padBottom(6);
    }

    protected void addFields(ServerConfig serverConfig, int leftSize, int rightSize) {
        dialog.add(new Label("Server name", skin)).prefWidth(leftSize);
        fieldName = new TextField(serverConfig.sv_name, skin);
        dialog.add(fieldName).prefWidth(rightSize);
        dialog.row();

        dialog.add(new Label("Max players", skin)).prefWidth(leftSize);
        fieldMaxPlayers = new TextField(Integer.toString(serverConfig.sv_max_players), skin);
        dialog.add(fieldMaxPlayers).prefWidth(rightSize);
        dialog.row();

        dialog.add(new Label("Bots", skin)).prefWidth(leftSize);
        fieldBots = new TextField(Integer.toString(serverConfig.sv_bots), skin);
        dialog.add(fieldBots).prefWidth(rightSize);
        dialog.row();

        dialog.add(new Label("Map", skin)).prefWidth(leftSize);
        fieldMap = new SelectBox<String>(skin);
        fieldMap.setItems(EngineConfig.MAPS);
        dialog.add(fieldMap).prefWidth(rightSize);
        dialog.row();

        dialog.add(new Label("Player Collisions", skin)).prefWidth(leftSize);
        fieldPlayerCollisions = new CheckBox("", skin);
        fieldPlayerCollisions.setChecked(serverConfig.mp_player_collisions);
        dialog.add(fieldPlayerCollisions).prefWidth(rightSize);
        dialog.row();

        dialog.add(new Label("Player speed", skin)).prefWidth(leftSize);
        fieldPlayerSpeed = new TextField(Float.toString(serverConfig.mp_player_move_speed), skin);
        dialog.add(fieldPlayerSpeed).prefWidth(rightSize);
        dialog.row();

        dialog.add(new Label("Freeze time", skin)).prefWidth(leftSize);
        fieldFreezeTime = new TextField(Integer.toString(serverConfig.mp_freeze_time), skin);
        dialog.add(fieldFreezeTime).prefWidth(rightSize);
        dialog.row();

        dialog.add(new Label("Round time", skin)).prefWidth(leftSize);
        fieldRoundTime = new TextField(Integer.toString(serverConfig.mp_round_time), skin);
        dialog.add(fieldRoundTime).prefWidth(rightSize);
        dialog.row();
    }

    public void setListener(SuccessListener listener) {
        this.listener = listener;
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

        error = ServerConfigValidator.validateMaxPlayers(fieldMaxPlayers.getText());
        if (error != null) return error;

        error = ServerConfigValidator.validateBots(fieldBots.getText(), Integer.parseInt(fieldMaxPlayers.getText()));
        if (error != null) return error;

        error = ServerConfigValidator.validatePlayerMoveSpeed(fieldPlayerSpeed.getText());
        if (error != null) return error;

        error = ServerConfigValidator.validateFreezeTime(fieldFreezeTime.getText());
        if (error != null) return error;

        error = ServerConfigValidator.validateRoundTime(fieldRoundTime.getText());
        if (error != null) return error;

        error = ServerConfigValidator.validateMap(fieldMap.getSelected());
        if (error != null) return error;

        return null;
    }

    protected ServerConfig output() {
        ServerConfig serverConfig = new ServerConfig();

        serverConfig.sv_name = fieldName.getName();
        serverConfig.sv_max_players = Integer.parseInt(fieldMaxPlayers.getText());
        serverConfig.sv_bots = Integer.parseInt(fieldBots.getText());
        serverConfig.mp_player_move_speed = Float.parseFloat(fieldPlayerSpeed.getText());
        serverConfig.mp_player_collisions = fieldPlayerCollisions.isChecked();
        serverConfig.mp_freeze_time = Integer.parseInt(fieldFreezeTime.getText());
        serverConfig.mp_round_time = Integer.parseInt(fieldRoundTime.getText());
        serverConfig.sv_map = fieldMap.getSelected();

        return serverConfig;
    }
}
