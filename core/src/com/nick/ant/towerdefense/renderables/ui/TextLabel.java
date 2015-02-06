package com.nick.ant.towerdefense.renderables.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * Created by Nick on 15/09/2014.
 */
public class TextLabel extends UIComponent {
    Label label;

    public TextLabel(String text)   {
        label = new Label(text, Styles.Label.Normal);
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        label.draw(spriteBatch, 1);
    }

    @Override
    public void dispose() {
        label.getStage().dispose();
    }
}
