package com.nicktoony.cstopdown.rooms.serverlist;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.nicktoony.cstopdown.rooms.connect.CSRoomConnect;
import com.nicktoony.engine.EngineConfig;
import com.nicktoony.engine.components.Room;
import com.nicktoony.engine.config.ServerlistConfig;
import com.nicktoony.engine.networking.client.ClientSocket;
import com.nicktoony.gameserver.service.GameserverConfig;
import com.nicktoony.gameserver.service.client.models.Server;
import com.nicktoony.gameserver.service.host.Host;
import com.nicktoony.gameserver.service.libgdx.ServerList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nick on 16/07/15.
 */
public class RoomServerList extends Room {
    private List<Host> hosts = new ArrayList<Host>();
    private ServerList serverList;
    private Stage stage;

    @Override
    public void create(boolean render) {
        super.create(render);

        // set gameserver config.json
        GameserverConfig.setConfig(new ServerlistConfig());
        // A stage
        stage = new Stage(new StretchViewport(getGame().getGameConfig().resolution_x,
                getGame().getGameConfig().resolution_y));

        // The server list (which is an actor)
        serverList = new ServerList(getAsset(EngineConfig.Skins.DEFAULT, Skin.class));
//        serverList.setDebug(true, true);
        serverList.pad(20);
        // Add meta
        serverList.addMetaColumn("IP", "ip");
        serverList.addMetaColumn("Port", "port");
        // Listener
        serverList.setListener(new ServerList.RowListener() {
            @Override
            public void onSelected(Server server) {
                if (server.getMeta().containsKey("ip") && server.getMeta().containsKey("port")) {
                    ClientSocket socket = getGame().getPlatformProvider().getWebSocket(
                            server.getMeta().get("ip"), Integer.parseInt(server.getMeta().get("port"))
                    );

                    getGame().createRoom(new CSRoomConnect(socket));
                }
            }
        });
        // Setup
        serverList.setup();

        // Handle input, and add the actor
        Gdx.input.setInputProcessor(stage);
        stage.addActor(serverList);
    }

    public void step(float delta)  {
        super.step(delta);
        stage.act(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        super.render(spriteBatch);
        stage.draw();
    }

    @Override
    public void dispose(boolean render)   {
        super.dispose(render);
    }
}
