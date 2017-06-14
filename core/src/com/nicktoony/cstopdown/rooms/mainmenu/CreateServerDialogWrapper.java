package com.nicktoony.cstopdown.rooms.mainmenu;

import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.nicktoony.engine.config.ServerConfig;
import com.nicktoony.engine.config.ServerConfigValidator;

/**
 * Created by Nick on 14/06/2017.
 */
public class CreateServerDialogWrapper extends SinglePlayerDialogWrapper {
    private TextField fieldPort;
    private TextField fieldIP;
    private CheckBox fieldServerlist;

    public CreateServerDialogWrapper(Skin skin) {
        super(skin);
    }

    @Override
    protected void addFields(ServerConfig serverConfig, int leftSize, int rightSize) {
        super.addFields(serverConfig, leftSize, rightSize);

        dialog.add(new Label("Public", skin)).prefWidth(leftSize);
        fieldServerlist = new CheckBox("", skin);
        fieldServerlist.setChecked(serverConfig.sv_server_list);
        dialog.add(fieldServerlist).prefWidth(rightSize);
        dialog.row();

        dialog.add(new Label("Server port", skin)).prefWidth(leftSize);
        fieldPort = new TextField(Integer.toString(serverConfig.sv_port), skin);
        dialog.add(fieldPort).prefWidth(rightSize);
        dialog.row();

        dialog.add(new Label("Server IP", skin)).prefWidth(leftSize);
        fieldIP = new TextField(serverConfig.sv_ip, skin);
        dialog.add(fieldIP).prefWidth(rightSize);
        dialog.row();
    }

    @Override
    protected String validate() {
        String error = super.validate();
        if (error != null) return error;

        error = ServerConfigValidator.validateIP(fieldIP.getText());
        if (error != null) return error;

        error = ServerConfigValidator.validatePort(fieldPort.getText());
        if (error != null) return error;

        return null;
    }

    @Override
    protected ServerConfig output() {
        ServerConfig config = super.output();

        config.sv_port = Integer.parseInt(fieldPort.getText());
        config.sv_ip = fieldIP.getText();
        config.sv_server_list = fieldServerlist.isChecked();

        return config;
    }
}
