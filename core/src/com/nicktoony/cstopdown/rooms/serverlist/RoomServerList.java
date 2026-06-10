package com.nicktoony.cstopdown.rooms.serverlist;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.nicktoony.cstopdown.rooms.connect.CSRoomConnect;
import com.nicktoony.cstopdown.rooms.mainmenu.RoomMainMenu;
import com.nicktoony.engine.EngineConfig;
import com.nicktoony.engine.components.Room;
import com.nicktoony.engine.config.ServerlistConfig;
import com.nicktoony.engine.networking.client.ClientSocket;
import com.nicktoony.gameserver.service.GameserverConfig;
import com.nicktoony.gameserver.service.client.models.Server;
import com.nicktoony.gameserver.service.host.Host;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nick on 16/07/15.
 */
public class RoomServerList extends Room {
    private List<Host> hosts = new ArrayList<Host>();
    private CSServerList serverList;
    private Stage stage;

    @Override
    public void create(boolean render) {
        super.create(render);

        // set gameserver config.json
        GameserverConfig.setConfig(new ServerlistConfig());
        // A stage
        stage = new Stage(new StretchViewport(getGame().getGameConfig().resolution_x,
                getGame().getGameConfig().resolution_y));

        Skin skin = getAsset(EngineConfig.Skins.DEFAULT, Skin.class);

        // The server list (which is an actor)
        serverList = new CSServerList(skin);
//        serverList.setDebug(true, true);
        serverList.pad(20);
        // Add meta
        serverList.addMetaColumn("IP", "ip");
        serverList.addMetaColumn("Port", "port");
        // Listener
        serverList.setListener(new CSServerList.RowListener() {
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

        // Back button (top-right) so the player isn't stuck on the server list
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getGame().createRoom(new RoomMainMenu());
            }
        });
        Table topBar = new Table();
        topBar.setFillParent(true);
        topBar.top().right();
        topBar.add(backButton).pad(20);
        stage.addActor(topBar);
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
