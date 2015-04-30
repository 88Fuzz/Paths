package com.paths.drawable.movable;

import com.badlogic.gdx.math.Vector2;
import com.paths.drawable.SceneNode;

public class Moveable extends SceneNode
{
    protected int tileSize;
    protected int mapWidth;
    protected int mapHeight;
    protected Vector2 velocity;

    public Moveable(Category category, int mapWidth, int mapHeight, int tileSize)
    {
        super(category);
        init(mapWidth, mapHeight, tileSize);
    }

    private void init(int mapWidth, int mapHeight, int tileSize)
    {
        this.tileSize = tileSize;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.velocity = new Vector2(0,0);
    }

    // TODO figure out how to implement this method
    public void dispose()
    {
        super.dispose();
    }

    public void setMapWidth(int width)
    {
        mapWidth = width;
    }

    public int getMapWidth()
    {
        return mapWidth;
    }

    public void setMapHeight(int height)
    {
        mapHeight = height;
    }

    public int getMapHeight()
    {
        return mapHeight;
    }

    public void setTileSize(int size)
    {
        tileSize = size;
    }

    public int getTileSize()
    {
        return tileSize;
    }

}
