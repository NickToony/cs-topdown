package com.nicktoony.cstopdown.rooms.game;


import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;

/**
 * Created by Nick on 22/03/2016.
 */
public class ChatTextArea extends TextArea {
    public ChatTextArea(String text, Skin skin) {
        super(text, skin);
    }

    public void update() {
        setPrefRows(getLines());
    }

    public ChatTextArea(String text, Skin skin, String styleName) {
        super(text, skin, styleName);
    }

    public ChatTextArea(String text, TextFieldStyle style) {
        super(text, style);
    }
}
