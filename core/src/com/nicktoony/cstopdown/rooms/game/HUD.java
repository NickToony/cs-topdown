package com.nicktoony.cstopdown.rooms.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nicktoony.cstopdown.components.Entity;
import com.nicktoony.cstopdown.rooms.game.entities.players.Player;

/**
 * Created by Nick on 18/03/2016.
 */
public class HUD extends Entity<RoomGame> {

    private BitmapFont font;

    @Override
    protected void create(boolean render) {
        if (render) {
            font = new BitmapFont();
            font.setColor(Color.BLACK);
            font.setColor(0.91f, 0.73f, 0.23f, 0.95f);
            font.getData().scale(2f);
        }
    }

    @Override
    public void step(float delta) {

    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        Player player = getRoom().getMap().getEntitySnap();
        if (player != null) {
            StringBuilder b = new StringBuilder();
            b.append(player.getGun().bulletsIn);
            b.append(" | ");
            b.append(player.getGun().bulletsOut);
            b.append("      ");
            b.append(player.getGun().weapon.getName());
            font.draw(spriteBatch, b.toString(), 50, 50);
        }
    }

    @Override
    public void dispose(boolean render) {
        font.dispose();
    }
}
