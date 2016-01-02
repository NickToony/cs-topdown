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
    }

    @Override
    public void addCollisionObjects(World world) {
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

                    // Resize it to the correct size and location
                    BodyDef bodyDef = new BodyDef();
                    bodyDef.type = BodyDef.BodyType.StaticBody;
                    bodyDef.position.set(x + (CELL_SIZE /2), y + (CELL_SIZE /2));

                    Body body = world.createBody(bodyDef);

                    PolygonShape shape = new PolygonShape();
                    shape.setAsBox(CELL_SIZE /2, CELL_SIZE /2);

                    FixtureDef fixtureDef = new FixtureDef();
                    fixtureDef.shape = shape;
                    fixtureDef.density = 1f;

                    body.createFixture(fixtureDef);
                    shape.dispose();

                }
            }
        }

        int[][] values =  {
                { 0, 0,     1, 0 },
                { 1, 0,     1, 1 },
                { 1, 1,     0, 1},
                { 0, 1,     0, 0}
        };

        for (int[] value : values) {
            BodyDef bodyDef2 = new BodyDef();
            bodyDef2.type = BodyDef.BodyType.StaticBody;
            bodyDef2.position.set(0,0);
            FixtureDef fixtureDef2 = new FixtureDef();
            EdgeShape edgeShape = new EdgeShape();
            edgeShape.set(mapWidth * value[0], mapHeight * value[1], mapWidth * value[2], mapHeight * value[3]);
            fixtureDef2.shape = edgeShape;

            Body bodyEdgeScreen = world.createBody(bodyDef2);
            bodyEdgeScreen.createFixture(fixtureDef2);
            edgeShape.dispose();
        }
    }
}
