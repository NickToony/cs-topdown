package com.nick.ant.towerdefense.renderables.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nick.ant.towerdefense.renderables.entities.players.Player;
import com.nick.ant.towerdefense.rooms.RoomGameRender;

/**
 * Created by harry on 28/09/14.
 */
public class HUD extends UIComponent {

    private BitmapFont font;
    private int x;
    private int y;
    private Player player;
    private String ammoCount;
    private FPSCounter counter;

    public HUD(RoomGameRender room) {
        counter = new FPSCounter();
    }

    @Override
    public void createGL() {
        super.createGL();

        font = new BitmapFont();
        font.setColor(0.91f, 0.73f, 0.23f, 0.95f);
        // Scaling works really well with bitmap fonts
        font.setScale(2f);
    }

    @Override
    public void step(){
        if (player != null) {
            ammoCount = player.getGun().getClipSize() + "|" + player.getGun().getClipTotal();
        }
        counter.step();
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        super.render(spriteBatch);

        // TODO: Define HUD elements with XML?
        if (player != null && ammoCount != null) {
            font.draw(spriteBatch, ammoCount, 10, 40);
        }
        counter.render(spriteBatch);
    }

    @Override
    public void dispose() {
        font.dispose();
    }

}
