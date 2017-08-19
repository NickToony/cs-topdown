package com.nicktoony.cstopdown.rooms.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Nick on 19/08/2017.
 */
public class FragUI extends Table {

    public static class Frag {
        String killer;
        String killed;
        String weapon;
        Label label = null;
        long added;

        public Frag(String killer, String killed, String weapon) {
            this.killer = killer;
            this.killed = killed;
            this.weapon = weapon;
            this.added = System.currentTimeMillis();
        }
    }

    public List<Frag> frags = new ArrayList<Frag>();
    private BitmapFont fragFont = new BitmapFont();

    public FragUI() {
        fragFont.getData().markupEnabled = true;
        fragFont.getData().scale(1.5f);
    }

    public void update() {
//        addFrag(new Frag("", "", ""));

        int count = frags.size();

        Iterator<Frag> fragIterator = frags.iterator();
        Frag frag;
        while (fragIterator.hasNext()) {
            frag = fragIterator.next();
            if (frag.label != null &&
                    (count > 5 || frag.added + 4000 < System.currentTimeMillis())) {
                    frag.label.getColor().a -= 0.2f;
                    if (frag.label.getColor().a <= 0) {
                        frag.label.remove();
                        fragIterator.remove();
                    }
            } else {
                if (frag.label == null) {
                    frag.label =  new Label(frag.killer +
                            "   [YELLOW]" + frag.weapon +
                            "   " + frag.killed,
                            new Label.LabelStyle(fragFont, Color.WHITE));
                    frag.label.setAlignment(Align.right);
                    this.row();
                    this.add(frag.label).expandX()
                            .padBottom(1)
                             .padLeft(8).padRight(8);

                }

            }

            count --;
        }
    }

    public void addFrag(Frag frag) {
        frags.add(frag);
    }

    public void dispose() {
        fragFont.dispose();
    }

}
