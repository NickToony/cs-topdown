package com.nicktoony.cstopdown.rooms.serverlist;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.nicktoony.gameserver.service.GameserverConfig;
import com.nicktoony.gameserver.service.client.models.Server;
import com.nicktoony.gameserver.service.host.Host;
import com.nicktoony.gameserver.service.libgdx.ServerList;
import com.nicktoony.cstopdown.components.Room;
import com.nicktoony.cstopdown.config.ServerlistConfig;
import com.nicktoony.cstopdown.networking.client.SBSocket;
import com.nicktoony.cstopdown.networking.packets.ConnectPacket;
import com.nicktoony.cstopdown.networking.packets.DisconnectPacket;
import com.nicktoony.cstopdown.networking.packets.Packet;
import com.nicktoony.cstopdown.services.SkinManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nick on 16/07/15.
 */
public class RoomServerList extends Room {
    private List<Host> hosts = new ArrayList();
    private ServerList serverList;
    private Stage stage;

    @Override
    public void create(boolean render) {
        super.create(render);

        // set gameserver config.json
        GameserverConfig.setConfig(new ServerlistConfig());
        // A stage
        stage = new Stage(new StretchViewport(getGame().getGameConfig().game_resolution_x,
                getGame().getGameConfig().game_resolution_y));

        // The server list (which is an actor)
        serverList = new ServerList(SkinManager.getUiSkin());
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
                    SBSocket socket = getGame().getPlatformProvider().getWebSocket(
                            server.getMeta().get("ip"), Integer.parseInt(server.getMeta().get("port"))
                    );
                    socket.open();

                    socket.addListener(new SBSocket.SBSocketListener() {

                        @Override
                        public void onOpen(SBSocket socket) {
                            socket.sendMessage(new ConnectPacket());
                            socket.sendMessage(new DisconnectPacket());
                            socket.close();
                        }

                        @Override
                        public void onClose(SBSocket socket) {

                        }

                        @Override
                        public void onMessage(SBSocket socket, Packet packet) {

                        }

                        @Override
                        public void onError(SBSocket socket, Exception exception) {

                        }
                    });
                }
            }
        });
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
    public void render(SpriteBatch spriteBatch) {
        super.render(spriteBatch);
        stage.draw();
    }

    @Override
    public void dispose()   {
        super.dispose();
    }
}
