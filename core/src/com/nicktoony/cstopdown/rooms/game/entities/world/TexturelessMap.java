package com.nicktoony.cstopdown.rooms.game.entities.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.XmlReader;

import java.io.IOException;

/**
 * Created by Nick on 15/02/2015.
 */
public class TexturelessMap extends Map {
    protected XmlReader xml = new XmlReader();
    private XmlReader.Element base;

    public TexturelessMap(String mapName) {
        FileHandle file = Gdx.files.internal("maps/" + mapName + "/map.tmx");

        try {
            base = xml.parse(file);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        mapWidth = Integer.parseInt(base.getAttribute("width")) * CELL_SIZE;
        mapHeight = Integer.parseInt(base.getAttribute("height")) * CELL_SIZE;

        // Setup pathfinding
        pathfindingGraph = new PathfindingGraph(Integer.parseInt(base.getAttribute("width")),
                Integer.parseInt(base.getAttribute("height")));
    }

    @Override
    protected void addCollisionWalls(World world) {
        for (XmlReader.Element layer : base.getChildrenByName("objectgroup")) {
            XmlReader.Element properties = layer.getChildByName("properties");

            // Determine if collision layer
            boolean isCollision = false;
            for (XmlReader.Element property : properties.getChildrenByName("property")) {
                if (property.getAttribute("name").contains("collision")) {
                    if (property.getAttribute("value").contains("true")) {
                        isCollision = true;
                        System.out.println("Found collision layer");
                    }
                }
            }

            // It's the collision layer, awesome
            if (isCollision) {
                for (XmlReader.Element object : layer.getChildrenByName("object")) {
                    int x = Integer.parseInt(object.getAttribute("x"));
                    int y = mapHeight - Integer.parseInt(object.getAttribute("y"));

                    addCollisionWall(world, x, y);
                }
            }
        }
    }
}
