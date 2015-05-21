package com.paths.drawable.movable;

import java.util.Iterator;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.paths.constants.TextureConstants;
import com.paths.drawable.MyTexture;
import com.paths.drawable.SceneNode;

public class Bullet extends Moveable
{
    public enum Category
    {
        BASIC(180.0f, 60, true, TextureConstants.SIMPLE_BULLET_KEY);
        
        private float speed;
        private String textureKey;
        private boolean followTarget;
        private int maxTravelDistance;
        
        Category(float speed, int maxTravelDistance, boolean followTarget, String textureKey)
        {
            this.speed = speed;
            this.textureKey = textureKey;
            this.followTarget = followTarget;
            this.maxTravelDistance = maxTravelDistance;
        }
        
        public int getMaxTravelDistance()
        {
            return maxTravelDistance;
        }

        public float getSpeed()
        {
            return speed;
        }
        
        public String getTextureKey()
        {
            return textureKey;
        }
        
        public boolean isFollowTarget()
        {
            return followTarget;
        }
    }

    private TextureAtlas atlas;
    private SceneNode target;
    private boolean followTarget;
    private boolean dead;
    private int points;
    private float distanceToTravel;
    
    public Bullet()
    {
        
    }

    public Bullet(Category category, int mapTileWidth, int mapTileHeight, int tileSize, TextureAtlas atlas, SceneNode start, SceneNode target, SceneNode map)
    {
        super();
        init(category, mapTileWidth, mapTileHeight, tileSize, atlas, start, target, map);
    }
    
    public void init(Category category, int mapTileWidth, int mapTileHeight, int tileSize, TextureAtlas atlas, SceneNode start, SceneNode target, SceneNode map)
    {
        super.init(SceneNode.Category.NONE, mapTileWidth, mapTileHeight, tileSize, map);
        dead = false;
        points = 0;
        this.atlas = atlas;
        this.map = map;
        this.tilePos = start.getTilePosition();
        this.pos = start.getPosition();
        this.target = target;
        //TODO make these values configurable based on bullet type. YO
        this.sprite = new MyTexture(null, pos, new Vector2(15/2.0f, 15/2.0f), new Vector2(15, 15), new Vector2(1, 1), 0);
        setCategory(category);
        calculateVelocity();
    }
    
    protected void setCategory(Category category)
    {
        maxVelocity = category.getSpeed();
        sprite.setTexture(atlas.findRegion(category.getTextureKey()));
        followTarget = category.followTarget;
        distanceToTravel = category.getMaxTravelDistance();
    }
    
    private void calculateVelocity()
    {
        if(target == null)
            return;
        
        Vector2 distance = target.getPosition();
        
        distance.sub(pos);
        distance.nor();
        velocity.x = distance.x * maxVelocity;
        velocity.y = distance.y * maxVelocity;
    }
    
    @Override
    protected void updateCurrent(SceneNode superNode, float dt)
    {
        if(dead)
            return;

        //Check for collisions
        SceneNode tileNode = map.getChildNode(SceneNode.get1d((int)tilePos.x, (int)tilePos.y, mapTileWidth));
        if(checkCollisions(tileNode))
            return;

        Vector2 vel = new Vector2(velocity.x, velocity.y);
        Vector2 tileAdjusts = new Vector2();
        vel.nor();
        
        if(vel.x != 0)
        {
            if(vel.x < 0 && tilePos.x > 0)
                tileAdjusts.x = -1;
            else if(tilePos.x < mapTileWidth - 1)
                tileAdjusts.x = 1;

            tileNode = map.getChildNode(SceneNode.get1d((int)(tilePos.x + tileAdjusts.x), (int)tilePos.y, mapTileWidth));
            if(checkCollisions(tileNode))
                return;
        }
        if(vel.y != 0)
        {
            if(vel.y < 0 && tilePos.y > 0)
                tileAdjusts.y = -1;
            else if(tilePos.y < mapTileHeight - 1)
                tileAdjusts.y = 1;

            tileNode = map.getChildNode(SceneNode.get1d((int)tilePos.x, (int)(tilePos.y + tileAdjusts.y), mapTileWidth));
            if(checkCollisions(tileNode))
                return;
        }
        if(vel.x != 0 && vel.y != 0)
        {
            tileNode = map.getChildNode(SceneNode.get1d((int)(tilePos.x + tileAdjusts.x), (int)(tilePos.y + tileAdjusts.y), mapTileWidth));
            if(checkCollisions(tileNode))
                return;
        }

        super.updateCurrent(superNode, dt);

        float distance = velocity.x*dt * velocity.x*dt + velocity.y*dt * velocity.y*dt;
        distanceToTravel -= Math.sqrt(distance);
        
        if(distanceToTravel < 0)
        {
            dead = true;
            return;
        }

        if(followTarget)
            calculateVelocity();
    }
    
    public boolean checkCollisions(SceneNode tileNode)
    {
        Iterator<SceneNode> it = tileNode.getChildenIterator();
        while(it.hasNext())
        {
            tileNode = it.next();
            if(tileNode instanceof Mob)
            {
                Vector2 nodePos = tileNode.getPosition();
                Vector2 dimension = tileNode.getDimension();
                Vector2 bulletDimension = getDimension();
                if(nodePos.x < pos.x + bulletDimension.x &&
                        nodePos.x + dimension.x > pos.x)
                {
                    if(nodePos.y < pos.y + bulletDimension.y &&
                            nodePos.y + dimension.y > pos.y)
                    {
                        dead = true;
                        points = tileNode.kill();
                    }
                }
            }
        }

        return dead;
    }
    
    public boolean isDead()
    {
        return dead;
    }
    
    public int getPoints()
    {
        return points;
    }
}
