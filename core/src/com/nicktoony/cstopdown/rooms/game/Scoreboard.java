package com.nicktoony.cstopdown.rooms.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.nicktoony.cstopdown.networking.packets.helpers.PlayerDetailsWrapper;
import com.nicktoony.engine.components.Entity;
import com.nicktoony.engine.rooms.RoomGame;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tooni on 16/06/2017.
 */
public class Scoreboard extends Entity<RoomGame> {

    private Stage stage;
    private Table table;
    private boolean visible;
    private Map<Integer, TeamWrapper> teams = new LinkedHashMap<Integer, TeamWrapper>();
    private RowWrapper headers;
    private Skin skin;

    class TeamWrapper {
        List<RowWrapper> rows = new ArrayList<RowWrapper>();

        public TeamWrapper() {

        }
    }

    class RowWrapper {
        PlayerDetailsWrapper playerDetailsWrapper;
        Label name;
        Label kills;
        Label deaths;
        Label ping;

        RowWrapper(PlayerDetailsWrapper playerDetails, Skin skin) {
            name = new Label("", skin);
            kills = new Label("", skin);
            deaths = new Label("", skin);
            ping = new Label("", skin);

            this.playerDetailsWrapper = playerDetails;
        }

        void addToTable() {
            table.add(name).expandX();
            table.add(kills).prefWidth(80);
            table.add(deaths).prefWidth(80);
            table.add(ping).prefWidth(120);
            table.row().top().left();
        }

        void update() {
            name.setText(playerDetailsWrapper.name);
            kills.setText(Integer.toString(playerDetailsWrapper.kills));
            deaths.setText(Integer.toString(playerDetailsWrapper.deaths));
            ping.setText(Integer.toString(playerDetailsWrapper.ping));
        }
    }

    @Override
    protected void create(boolean render) {
        stage = new Stage(new ScreenViewport());

        table = new Table();
        table.setFillParent(true);
        table.pad(100);
        stage.addActor(table);

        skin = getAsset("skins/default/uiskin.json", Skin.class);
        headers = new RowWrapper(null, skin);
        headers.name.setText("Name");
        headers.kills.setText("Kills");
        headers.deaths.setText("Deaths");
        headers.ping.setText("Ping");
    }

    @Override
    public void step(float delta) {
        if (visible)
            stage.act(delta);

        if (getRoom().getGameManager().getScoreboardChanged()) {
            restructure();
            getRoom().getGameManager().setScoreboardChanged(false);
        }

        for (TeamWrapper teamWrapper : teams.values()) {
            for (RowWrapper rowWrapper : teamWrapper.rows) {
                rowWrapper.update();
            }
        }
    }

    private void restructure() {
        System.out.println("RESTRUCTURE");

        // Get rid of old children, re-add the headers
        table.clearChildren();
        table.row().top().left();
        headers.addToTable();

        for (PlayerDetailsWrapper playerDetails : getRoom().getGameManager().getPlayerDetails()) {
            TeamWrapper teamWrapper = teams.get(playerDetails.team);
            if (teamWrapper == null) {
                teamWrapper = new TeamWrapper();
                teams.put(playerDetails.team, teamWrapper);
                System.out.println("NEW TEAM");
            }

            RowWrapper rowWrapper = new RowWrapper(playerDetails, skin);
            teamWrapper.rows.add(rowWrapper);
            System.out.println("PLAYER ADDED TO TEAM");
        }

        for (TeamWrapper teamWrapper : teams.values()) {
            for (RowWrapper rowWrapper : teamWrapper.rows) {
                rowWrapper.addToTable();
                System.out.println("ADDING");
            }
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        if (visible)
            stage.draw();
    }

    @Override
    public void dispose(boolean render) {
        stage.dispose();
    }

    @Override
    public void resize(int x, int y) {
        super.resize(x, y);

        if (stage != null) {
            stage.getViewport().update(x,y);
        }
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
}
