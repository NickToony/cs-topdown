package com.nick.ant.towerdefense.rooms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.nick.ant.towerdefense.components.SkinManager;
import com.nick.ant.towerdefense.networking.client.CSClient;
import com.nick.ant.towerdefense.networking.server.CSTDServer;
import com.nick.ant.towerdefense.networking.server.ServerUI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 08/09/2014.
 */
public class RoomMainMenu extends Room {

    private Stage stage;

    @Override
    public void create() {
        // A stage
        stage = new Stage();
        // Handle input, and add the actor
        Gdx.input.setInputProcessor(stage);

        // Table layout
        Table table = new Table();
        table.setFillParent(true);
        table.pad(40);

        // Define all labels
        List<Label> labels = new ArrayList<Label>()
            {{
                // Single player
                add(newLabelWithListener(
                        new Label("Single Player", SkinManager.getUiSkin()),
                        new ClickListener() {
                            @Override
                            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
                            {
                                CSTDServer server = new CSTDServer(new ServerUI(new ServerUI.UIListener() {
                                    @Override
                                    public void onClose() {
                                        System.exit(0);
                                    }
                                }));
                                navigateToRoom(new RoomConnect(new CSClient(server)));
                            }
                        }
                ));
                // Join server
                add(newLabelWithListener(
                        new Label("Join Server", SkinManager.getUiSkin()),
                        new ClickListener() {
                            @Override
                            public void touchUp(InputEvent event, float x, float y, int pointer, int button)
                            {
                                navigateToRoom(new RoomServerList());
                            }
                        }
                ));
                add(new Label("Create Server", SkinManager.getUiSkin()));
                add(new Label("Options", SkinManager.getUiSkin()));
                add(new Label("Quit", SkinManager.getUiSkin()));
            }};

        row(table).expandY();
        for (Label label : labels) {
            table.add(label);
            row(table);
        }


        stage.addActor(table);

    }

    private Label newLabelWithListener(Label label, EventListener listener) {
        label.addListener(listener);
        return label;
    }

    private Cell row(Table table) {
        return table.row().bottom().left().expandX().padBottom(20);
    }

    public void step() {
        super.step();
        stage.act(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void render() {
        super.render();
        stage.draw();
    }

    @Override
    public void dispose()   {
        super.dispose();
    }
}
