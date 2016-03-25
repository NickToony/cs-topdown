package com.nicktoony.cstopdown.rooms.game.entities.world;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedNode;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Nick on 24/03/2016.
 */
public class PathfindingNode implements IndexedNode<PathfindingNode> {

    private int myIndex;
    private int x;
    private int y;
    private boolean solid = false;
    private Array<Connection<PathfindingNode>> mConnections = new Array<Connection<PathfindingNode>>();

    public PathfindingNode(int myIndex, int myX, int myY) {
        this.myIndex = myIndex;
        this.x = myX;
        this.y = myY;
    }

    @Override
    public int getIndex() {
        return myIndex;
    }

    @Override
    public Array<Connection<PathfindingNode>> getConnections() {
        return mConnections;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isSolid() {
        return solid;
    }

    public void setSolid(boolean solid) {
        this.solid = solid;
    }

    public int getWorldX() {
        return (x * 32) + 16;
    }

    public int getWorldY() {
        return (y * 32) + 16;
    }

    public Vector2 getWorldPos() {
        return new Vector2(getWorldX(), getWorldY());
    }
}
