package com.paths.drawable.movable;

import com.badlogic.gdx.math.Vector2;
import com.paths.drawable.SceneNode;

public class Moveable extends SceneNode
{
    protected int tileSize;
    protected float maxVelocity;
    protected Vector2 velocity;
    protected SceneNode map;
    protected Vector2 tilePos;
    private boolean swap;
    
    /*
     * init must be called
     */
    public Moveable() { }

    public Moveable(Category category, int mapTileWidth, int mapTileHeight, int tileSize, SceneNode map)
    {
        super();
        init(category, mapTileWidth, mapTileHeight, tileSize, map);
    }

    private void init(Category category, int mapTileWidth, int mapTileHeight, int tileSize, SceneNode map)
    {
        super.init(category, mapTileWidth, mapTileHeight, tileSize, null, null);
        this.map = map;
        this.tileSize = tileSize;
        this.velocity = new Vector2(0,0);
        this.swap= false;
        this.tilePos = new Vector2();
    }

    // TODO figure out how to implement this method
    public void dispose()
    {
        super.dispose();
    }

    public void setMapTileWidth(int width)
    {
        mapTileWidth = width;
    }

    public int getMapTileWidth()
    {
        return mapTileWidth;
    }

    public void setMapTileHeight(int height)
    {
        mapTileHeight = height;
    }

    public int getMapTileHeight()
    {
        return mapTileHeight;
    }

    public void setTileSize(int size)
    {
        tileSize = size;
    }

    public int getTileSize()
    {
        return tileSize;
    }
    
    @Override
    protected void updateCurrent(SceneNode superNode, float dt)
    {
        Vector2 tmpVector = new Vector2();
        tmpVector.x = velocity.x * dt;
        tmpVector.y = velocity.y * dt;
        pos.x += tmpVector.x;
        pos.y += tmpVector.y;
        
        tmpVector.x = tilePos.x;
        tmpVector.y = tilePos.y;
        
        tilePos.x = (int) pos.x / tileSize;
        tilePos.y = (int) pos.y / tileSize;
        
        if(!tmpVector.equals(tilePos))
        {
            //have to mark this for removal later cause of bullshit concurrent modification exceptions
            swap = true;
        }
    }
    
    public Vector2 getTilePos()
    {
        Vector2 tmp = new Vector2();
        tmp.x = tilePos.x;
        tmp.y = tilePos.y;
        
        return tmp;
    }

    public boolean needToSwap()
    {
        if(swap)
        {
            swap = false;
            return true;
        }
        return false;
    }
    
    @Override
    public void printDebugCurrent()
    {
        System.out.println("\thmmmmmm, moveable");
    }
}