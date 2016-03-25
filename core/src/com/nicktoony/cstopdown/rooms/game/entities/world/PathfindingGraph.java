package com.nicktoony.cstopdown.rooms.game.entities.world;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Nick on 24/03/2016.
 */
public class PathfindingGraph implements IndexedGraph<PathfindingNode> {

    protected Array<PathfindingNode> nodes;
    protected int width;
    protected int height;

    public PathfindingGraph(int width, int height) {
        this.width = width;
        this.height = height;
        this.nodes = new Array<>();

        for (int x = 0; x < width; x++) {
            int idx = x * height;
            for (int y = 0; y < height; y++) {
                int idy = idx + y;
                nodes.add(new PathfindingNode(idy, x, y));
            }
        }
    }

    public void setupConnections() {
        for (int x = 0; x < width; x++) {
            int idx = x * height;
            for (int y = 0; y < height; y++) {
                int idy = idx + y;
                PathfindingNode n = nodes.get(idy);
                if (x > 0) addConnection(n, -1, 0);
                if (y > 0) addConnection(n, 0, -1);
                if (x < width - 1) addConnection(n, 1, 0);
                if (y < height - 1) addConnection(n, 0, 1);
            }
        }
    }

    @Override
    public Array<Connection<PathfindingNode>> getConnections(PathfindingNode fromNode) {
        return fromNode.getConnections();
    }

    private void addConnection (PathfindingNode node, int xOffset, int yOffset) {
        PathfindingNode target = getNode(node.getX() + xOffset, node.getY() + yOffset);
        if (!target.isSolid() && !node.isSolid()) {
            node.getConnections().add(new DefaultConnection<>(node, target));
        }
    }

    public PathfindingNode getNode (int index) {
        return nodes.get(index);
    }

    public PathfindingNode getNode (int x, int y) {
        return nodes.get(x * height + y);
    }

    public PathfindingNode getNodeByWorld(float x, float y) {
        return getNode((int) Math.floor((x) / 32f), (int) Math.floor((y) / 32f));
    }

    @Override
    public int getNodeCount() {
        return nodes.size;
    }

    public Array<PathfindingNode> getNodes() {
        return nodes;
    }
}
