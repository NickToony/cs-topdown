package com.nicktoony.cstopdown.rooms.game.entities.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.XmlReader;
import com.nicktoony.cstopdown.mods.gamemode.PlayerModInterface;
import com.nicktoony.cstopdown.rooms.game.entities.world.objectives.Spawn;
import com.nicktoony.engine.EngineConfig;
import com.nicktoony.engine.config.GameConfig;
import com.nicktoony.engine.config.ServerConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Nick on 15/02/2015.
 */
public class TexturelessMap extends Map {
    protected XmlReader xml = new XmlReader();
    private XmlReader.Element base;

    public TexturelessMap(ServerConfig gameConfig, String mapName) {
        super(gameConfig);
        FileHandle file = Gdx.files.internal("maps/" + mapName + "/map.tmx");

        try {
            base = xml.parse(file);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        mapWidth = Integer.parseInt(base.getAttribute("width")) * EngineConfig.CELL_SIZE;
        mapHeight = Integer.parseInt(base.getAttribute("height")) * EngineConfig.CELL_SIZE;

        // Find objectives
        findObjectives();

        // Setup pathfinding
        pathfindingGraph = new PathfindingGraph(Integer.parseInt(base.getAttribute("width")),
                Integer.parseInt(base.getAttribute("height")));
    }

    @Override
    protected void findObjectives() {
        spawns = new HashMap<Integer, List<Spawn>>();
        spawns.put(PlayerModInterface.TEAM_CT, new ArrayList<Spawn>());
        spawns.put(PlayerModInterface.TEAM_T, new ArrayList<Spawn>());
        for (XmlReader.Element layer : base.getChildrenByName("objectgroup")) {
            XmlReader.Element properties = layer.getChildByName("properties");

            // Determine if collision layer
            boolean isObjective = false;
            for (XmlReader.Element property : properties.getChildrenByName("property")) {
                if (property.getAttribute("name").contains("objectives")) {
                    if (property.getAttribute("value").contains("true")) {
                        isObjective = true;
                        System.out.println("Found objective layer");
                    }
                }
            }

            // It's the collision layer, awesome
            if (isObjective) {
                for (XmlReader.Element object : layer.getChildrenByName("object")) {
                    int x = Integer.parseInt(object.getAttribute("x"));
                    int y = mapHeight - Integer.parseInt(object.getAttribute("y"));

                    if (object.getAttribute("type").contentEquals("spawn")) {
                        int team = 0;
                        for (XmlReader.Element property : object.getChildByName("properties").getChildrenByName("property")) {
                            if (property.getAttribute("name").contains("team")) {
                                team = Integer.parseInt(property.getAttribute("value"));
                            }
                        }

                        spawns.get(team).add(new Spawn(x + 16, y + 16, team));
                    }
                }
            }
        }
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
