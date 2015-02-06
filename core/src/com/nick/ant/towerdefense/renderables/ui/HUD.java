package com.nick.ant.towerdefense.renderables.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nick.ant.towerdefense.renderables.entities.players.Player;
import com.nick.ant.towerdefense.rooms.RoomGame;

/**
 * Created by harry on 28/09/14.
 */
public class HUD extends UIComponent {

    private BitmapFont font;
    private RoomGame room;
    private int x;
    private int y;

    private Player player;

    private String ammoCount;

    public HUD(RoomGame room) {
        font = new BitmapFont();
        font.setColor(0.91f, 0.73f, 0.23f, 0.95f);
        // Scaling works really well with bitmap fonts
        font.setScale(2f);

        this.room = room;
        this.player = room.getUserPlayer();

    }

    @Override
    public void step(){
        ammoCount = player.getGun().getClipSize() + "|" + player.getGun().getClipTotal();
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        // Should hopefully return 0,0 of the window
        this.x = Math.round(room.getViewX()) - Gdx.graphics.getWidth()/2;
        this.y = Math.round(room.getViewY()) - Gdx.graphics.getHeight()/2;

        // TODO: Define HUD elements with XML?
        font.draw(spriteBatch, ammoCount, x + 10, y + 40);
    }

    @Override
    public void dispose() {
        font.dispose();
    }

}
