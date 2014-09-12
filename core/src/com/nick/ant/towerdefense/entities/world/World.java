package com.nick.ant.towerdefense.entities.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nick.ant.towerdefense.entities.Entity;

/**
 * Created by Nick on 10/09/2014.
 */
public class World extends Entity {
    private int width;
    private int height;
    private int cellSize;
    private Tile[][] tiles;

    public World(int width, int height, int cellSize) {
        this.width = width;
        this.height = height;
        this.cellSize = cellSize;

        this.tiles = new Tile[width][height];
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        for (Tile[] entities: tiles)  {
            for (Tile entity : entities)  {
                if (entity != null) {
                    entity.render(spriteBatch);
                }
            }
        }
    }

    @Override
    public void step() {
        for (Tile[] entities: tiles)  {
            for (Tile entity : entities)  {
                if (entity != null) {
                    entity.step();
                }
            }
        }
    }

    public int getCellSize()    {
        return cellSize;
    }

    public int getTileX(int x) {
        return getCellSize() * x;
    }

    public int getTileY(int y)  {
        return getCellSize() * y;
    }

    public void setTile(int x, int y, String texture)   {
        if (tiles[x][y] == null)    {
            tiles[x][y] = new Tile(texture, this, x, y);
        }

        tiles[x][y].setTexture(texture);
    }

    public void setArea(int x1, int y1, int x2, int y2, String texture)   {
        for (int x = x1; x <= x2; x ++)    {
            for (int y = y1; y <= y2; y++)  {
                setTile(x, y, texture);
            }
        }
    }
}
