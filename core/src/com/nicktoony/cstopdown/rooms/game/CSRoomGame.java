package com.nicktoony.cstopdown.rooms.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nicktoony.cstopdown.rooms.game.ui.CSHUD;
import com.nicktoony.engine.entities.HUD;
import com.nicktoony.engine.entities.world.Map;
import com.nicktoony.engine.entities.world.TexturelessMap;
import com.nicktoony.engine.networking.client.ClientSocket;
import com.nicktoony.engine.rooms.RoomGame;

/**
 * Created by Nick on 08/09/2014.
 */
public class CSRoomGame extends RoomGame {
    private CSHUD hud;

    public CSRoomGame(ClientSocket socket) {
        super(socket);
    }

    public CSRoomGame(ClientSocket socket, Map map) {
        super(socket);
        this.map = map;
    }

    @Override
    public void create(boolean render) {
        hud = new CSHUD();

        super.create(render);
    }

    public void step(float delta)  {
        super.step(delta);
    }

    @Override
    public void dispose(boolean render) {
        super.dispose(render);

        hud.dispose(render);
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        super.render(spriteBatch);
    }

    @Override
    protected HUD defineHud() {
        return hud;
    }

    @Override
    protected Map defineMap(boolean render) {
        Map map;
        if (!render) {
            map = new TexturelessMap(socket.getServerConfig(), socket.getServerConfig().sv_map);
        } else {
            if (this.map == null) {
                map = new Map(socket.getServerConfig(), socket.getServerConfig().sv_map, getGame().getGameConfig());
            } else {
                map = this.map;
            }
        }
        return map;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }
}
