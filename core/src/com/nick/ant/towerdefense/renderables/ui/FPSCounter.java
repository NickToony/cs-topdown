package com.nick.ant.towerdefense.renderables.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Nick on 10/09/2014.
 */
public class FPSCounter extends UIComponent {
    private int currentFPS = 0;
    private int counter = 0;
    private long lastTime = 0;
    private final int aSecond = 1000;
    private BitmapFont font;

    public FPSCounter() {
        font = new BitmapFont();
        font.setColor(Color.BLACK);
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        long time = System.currentTimeMillis();
        if (time > lastTime + 1000) {
            currentFPS = counter;
            counter = 0;
            lastTime = time;
        }

        counter += 1;

        font.draw(spriteBatch, "FPS: " + currentFPS, 20, 20);
    }

    @Override
    public void dispose() {
        font.dispose();
    }
}
