package com.nicktoony.cstopdown.rooms.game.entities.players;

import java.util.Random;

/**
 * Created by Nick on 24/03/2016.
 */
public class BotPlayer extends Player {

    Random random = new Random();

    @Override
    public void step(float delta) {
        super.step(delta);

        directionTo = random.nextInt(360);
    }
}
