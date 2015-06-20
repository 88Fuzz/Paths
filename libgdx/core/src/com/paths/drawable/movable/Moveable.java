package com.paths.drawable.movable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.paths.drawable.SceneNode;
import com.paths.drawable.movable.mob.Mob;

public class Moveable extends SceneNode
{
    protected int tileSize;
    protected float maxVelocity;
    protected Vector2 velocity;
    protected SceneNode map;
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

    public void init(Category category, int mapTileWidth, int mapTileHeight, int tileSize, SceneNode map)
    {
        super.init(category, mapTileWidth, mapTileHeight, tileSize, new Vector2(), null);
        this.map = map;
        this.tileSize = tileSize;
        this.velocity = new Vector2(0,0);
        this.swap= false;
    }

    // TODO figure out how to implement this method
    public void dispose()
    {
        super.dispose();
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
            setSwap(true);
        }
        sprite.setPosition(pos.x, pos.y);
    }
    
    public Vector2 getTilePos()
    {
        Vector2 tmp = new Vector2();
        tmp.x = tilePos.x;
        tmp.y = tilePos.y;
        
        return tmp;
    }

    public void setSwap(boolean newSwap)
    {
        swap = newSwap;
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
    public void drawCurrent(SpriteBatch batch)
    {
        sprite.draw(batch);
//        Vector2 texturePos = sprite.getPos();
//        Vector2 origin = sprite.getOrigin();
//        Vector2 dimension = sprite.getDimension();
//        Vector2 scale = sprite.getScale();
//
//        batch.draw(sprite.getTexture(), texturePos.x, texturePos.y,
//                origin.x, origin.y, dimension.x, dimension.y,
//                scale.x, scale.y, sprite.getRotation(), sprite.getRegionX(), sprite.getRegionY(),
//                sprite.getRegionWidth(), sprite.getRegionHeight(), false, false);
    }

    @Override
    public void printDebugCurrent()
    {
        System.out.println("\thmmmmmm, moveable");
    }
}
