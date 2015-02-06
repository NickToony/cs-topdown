package com.nick.ant.towerdefense.renderables.rooms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.nick.ant.towerdefense.renderables.entities.collisions.CollisionManager;
import com.nick.ant.towerdefense.renderables.entities.players.Player;
import com.nick.ant.towerdefense.renderables.entities.players.UserPlayer;
import com.nick.ant.towerdefense.renderables.entities.world.World;
import com.nick.ant.towerdefense.renderables.ui.TextLabel;
import com.nick.ant.towerdefense.serverlist.GameConfig;
import com.nicktoony.gameserver.service.GameserverConfig;
import com.nicktoony.gameserver.service.host.Host;
import com.nicktoony.gameserver.service.libgdx.ServerList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Nick on 08/09/2014.
 */
public class RoomMainMenu extends Room {

    private List<Host> hosts = new ArrayList();
    private ServerList serverList;
    private Stage stage;
    private SpriteBatch spriteBatch;

    public RoomMainMenu()   {
        // set gameserver config.json
        GameserverConfig.setConfig(new GameConfig());
        // A stage
        stage = new Stage();

        // The server list (which is an actor)
        serverList = new ServerList(new Skin(Gdx.files.internal("skins/default/uiskin.json")));
//        serverList.setDebug(true, true);
        serverList.pad(20);

        // Handle input, and add the actor
        Gdx.input.setInputProcessor(stage);
        stage.addActor(serverList);

        spriteBatch = new SpriteBatch();
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public void step()  {
        super.step();
        stage.act(Gdx.graphics.getDeltaTime());
    }

    public float getMouseX() {
        return Gdx.input.getX();
    }

    public float getMouseY() {
        return Gdx.input.getY();
    }

    @Override
    public float getViewX() {
        return 0;
    }

    @Override
    public float getViewY() {
        return 0;
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        super.render(spriteBatch);
        stage.draw();
    }

    @Override
    public void dispose()   {
        super.dispose();
    }
}
