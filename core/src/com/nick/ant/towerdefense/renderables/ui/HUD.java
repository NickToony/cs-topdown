package com.nick.ant.towerdefense.renderables.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nick.ant.towerdefense.renderables.entities.players.Player;
import com.nick.ant.towerdefense.renderables.entities.players.UserPlayer;

/**
 * Created by harry on 28/09/14.
 */
public class HUD extends UIComponent {

    private BitmapFont font;
    private Player userPlayer;

    public HUD(Player player) {
        font = new BitmapFont();
        font.setColor(Color.BLACK);

        userPlayer = (UserPlayer) player;
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        int ammo = userPlayer.getGun().getClipSize();

        font.draw(spriteBatch, "Ammo: " + ammo, 20, 80);
    }

    @Override
    public void dispose() {
        font.dispose();
    }

}
