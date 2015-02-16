package com.nick.ant.towerdefense.rooms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.nick.ant.towerdefense.components.SkinManager;
import com.nick.ant.towerdefense.serverlist.ServerlistConfig;
import com.nicktoony.gameserver.service.GameserverConfig;
import com.nicktoony.gameserver.service.host.Host;
import com.nicktoony.gameserver.service.libgdx.ServerList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 08/09/2014.
 */
public class RoomServerList extends Room {

    private List<Host> hosts = new ArrayList();
    private ServerList serverList;
    private Stage stage;

    @Override
    public void create() {
        // set gameserver config.json
        GameserverConfig.setConfig(new ServerlistConfig());
        // A stage
        stage = new Stage();

        // The server list (which is an actor)
        serverList = new ServerList(SkinManager.getUiSkin());
//        serverList.setDebug(true, true);
        serverList.pad(20);
        // Add meta
        serverList.addMetaColumn("Map", "map");
        // Setup
        serverList.setup();

        // Handle input, and add the actor
        Gdx.input.setInputProcessor(stage);
        stage.addActor(serverList);
    }

    public void step()  {
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
