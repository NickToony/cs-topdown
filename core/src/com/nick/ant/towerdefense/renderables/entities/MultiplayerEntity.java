package com.nick.ant.towerdefense.renderables.entities;

/**
 * Created by Nick on 15/09/2014.
 */
public abstract class MultiplayerEntity extends Entity {
    private static final int MP_STATE_NOCHANGE = 0;
    private static final int MP_STATE_MINORCHANGE = 1;
    private static final int MP_STATE_MAJORCHANGE = 2;

    protected boolean stateUpdated = false;
}
