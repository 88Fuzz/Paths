package com.paths.drawable.movable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.paths.constants.TextureConstants;
import com.paths.drawable.MapNode;
import com.paths.drawable.MyTexture;
import com.paths.drawable.SceneNode;

public class Mob extends Moveable
{
    private MapNode startNode;
    private MapNode endNode;
    private float distanceToTravel;
    
    //TODO fix these so that it can contain multiple parents and currents!
    private MapNode parentNode;
    private MapNode currentNode;

    public Mob(Category category, TextureAtlas atlas, int mapWidth, int mapHeight, int tileSize, MapNode start, MapNode end, SceneNode map)
    {
        super(category, mapWidth, mapHeight, tileSize, map);
        init(atlas, start, end, map);
    }

    public void init(TextureAtlas atlas, MapNode start, MapNode end, SceneNode map)
    {
        //TODO change this to have a configurable everything for when there are multiple types of mobs. YO
        this.startNode = start;
        this.endNode = end;
        this.map = map;
        this.maxVelocity = 180;
        this.parentNode = start;
        this.currentNode = start;
        this.distanceToTravel = 0;

        tilePos = startNode.getTilePosition();
        pos = startNode.getPosition();
//        pos = startNode.getCenteredPosition();

        sprite = new MyTexture(atlas.findRegion(TextureConstants.CIRCLE_KEY), pos, new Vector2(15, 15), new Vector2(30, 30), new Vector2(1, 1), 0);
        calculateVelocity();
    }

    //TODO figure out how to implement this method
    public void dispose()
    {
        super.dispose();
    }

    private void calculateVelocity()
    {
        MapNode nextNode;
        if(currentNode == endNode)
        {
            tilePos = startNode.getTilePosition();
//            pos = startNode.getCenteredPosition();
            pos = startNode.getPosition();

            sprite.setPos(pos.x, pos.y);
            currentNode = startNode;
        }

        nextNode = currentNode.getChildPathNode();
//        Vector2 futurePos = nextNode.getCenteredPosition();
        Vector2 futurePos = nextNode.getPosition();
        futurePos.sub(pos);
        futurePos.nor();
        velocity.x = futurePos.x * maxVelocity;
        velocity.y = futurePos.y * maxVelocity;

        calculateDistanceToTravel();

        //TODO the parent should be changed too. and the parent should now point to something new
        currentNode = nextNode;
    }

    private void calculateDistanceToTravel()
    {
        MapNode nextNode = currentNode.getChildPathNode();
//        Vector2 futurePos = nextNode.getCenteredPosition();
        Vector2 futurePos = nextNode.getPosition();

        float xDist = futurePos.x - pos.x;
        float yDist = futurePos.y - pos.y;
        float retVal = xDist * xDist + yDist * yDist;

        distanceToTravel = (float) Math.sqrt(retVal);
    }

    @Override
    protected void updateCurrent(SceneNode superNode, float dt)
    {
        super.updateCurrent(superNode, dt);

        float distance = velocity.x*dt * velocity.x*dt + velocity.y*dt * velocity.y*dt;

        distanceToTravel -= Math.sqrt(distance);

        if(distanceToTravel <= 0)
        {
            calculateVelocity();
        }
    }
}
