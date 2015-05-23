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
    public enum Category
    {
        BASIC(100, 100, 100, 500, new Vector2(30, 30), TextureConstants.CIRCLE_KEY);
        private float speed;
        private int health;
        private int damage;
        private int points;
        private Vector2 dimension;
        private Vector2 hitbox;
        private String textureKey;
        
        Category(float speed, int health, int damage, int points, Vector2 dimension, String textureKey)
        {
            this.speed = speed;
            this.health = health;
            this.damage = damage;
            this.dimension = dimension;
            this.hitbox = dimension;
            this.textureKey = textureKey;
            this.points = points;
        }
        
        public int getPoints()
        {
            return points;
        }

        public float getSpeed()
        {
            return speed;
        }
        
        public int getHealth()
        {
            return health;
        }
        
        public int getDamage()
        {
            return damage;
        }
        
        public Vector2 getDimension()
        {
            return dimension;
        }
        
        public Vector2 getHitbox()
        {
            return hitbox;
        }
        
        public String getTextureKey()
        {
            return textureKey;
        }
    }

    private MapNode startNode;
    private MapNode endNode;
    private float distanceToTravel;
    
    //TODO fix these so that it can contain multiple parents and currents!
    private MapNode parentNode;
    private MapNode currentNode;
    private int points;
    private int health;
    private int maxHealth;
    private boolean dead;
    private MyTexture healthBar;

    public Mob(Mob.Category category, TextureAtlas atlas, int mapWidth, int mapHeight, int tileSize, MapNode start, MapNode end, SceneNode map)
    {
        super();
        init(category, atlas, start, end, map, mapWidth, mapHeight, tileSize);
    }

    public void init(Mob.Category category, TextureAtlas atlas, MapNode start, MapNode end, SceneNode map, int mapWidth, int mapHeight, int tileSize)
    {
        super.init(SceneNode.Category.NONE, mapTileWidth, mapTileHeight, tileSize, map);
        //TODO change this to have a configurable everything for when there are multiple types of mobs. YO
        this.startNode = start;
        this.endNode = end;
        this.map = map;
        this.maxVelocity = category.getSpeed();
        this.parentNode = start;
        this.currentNode = start;
        this.distanceToTravel = 0;

        tilePos = startNode.getTilePosition();
        pos = startNode.getCenteredPosition();
        this.pos = start.getCenteredPosition();
        this.pos.x -= category.getDimension().x/2;
        this.pos.y -= category.getDimension().y/2;

        //TODO Create an enum with these properties
        //TODO and this score
        //TODO and the above dimensions
        points = category.getPoints();
        health = maxHealth = category.getHealth();
        dead = false;

        Vector2 origin = new Vector2(category.getDimension().x/2, category.getDimension().y/2);
        sprite = new MyTexture(atlas.findRegion(category.getTextureKey()), pos, origin, category.getDimension(), new Vector2(1, 1), 0);

        Vector2 healthPos = new Vector2(pos);
        Vector2 healthDimension = new Vector2(category.getDimension());
        healthDimension.y = 5;
        healthBar = new MyTexture(atlas.findRegion(TextureConstants.HEALTH_PIXEL), healthPos, new Vector2(), healthDimension, new Vector2(1,1), 0);
        updateHealthBar();

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
            pos = startNode.getPosition();

            sprite.setPos(pos.x, pos.y);
            currentNode = startNode;
        }

        nextNode = currentNode.getChildPathNode();
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
    public void drawCurrent(SpriteBatch batch)
    {
        super.drawCurrent(batch);
        
        Vector2 texturePos = healthBar.getPos();
        Vector2 origin = healthBar.getOrigin();
        Vector2 dimension = healthBar.getDimension();
        Vector2 scale = healthBar.getScale();

        batch.draw(healthBar.getTexture(), texturePos.x, texturePos.y,
                origin.x, origin.y, dimension.x, dimension.y,
                scale.x, scale.y, healthBar.getRotation(), healthBar.getRegionX(), healthBar.getRegionY(),
                healthBar.getRegionWidth(), healthBar.getRegionHeight(), false, false);
    }

    @Override
    protected void updateCurrent(SceneNode superNode, float dt)
    {
        super.updateCurrent(superNode, dt);
        updateHealthBar();
        
        //TODO remove this stuff
//        pos.x=0;
//        pos.y=0;
//        sprite.setPos(pos);
//        tilePos.x = (int) pos.x / tileSize;
//        tilePos.y = (int) pos.y / tileSize;

//        if(tilePos.x - dimension.x/2 < pos.x + bulletDimension.x/2 &&
//                tilePos.x + dimension.x/2 > pos.x - bulletDimension.x/2)
//
//       if(tilePos.y - dimension.y/2 < pos.y + bulletDimension.y/2 &&
//       tilePos.y + dimension.y/2 > pos.y - bulletDimension.y/2)

        float distance = velocity.x*dt * velocity.x*dt + velocity.y*dt * velocity.y*dt;

        distanceToTravel -= Math.sqrt(distance);

        if(distanceToTravel <= 0)
        {
            calculateVelocity();
        }
    }
    
    private void updateHealthBar()
    {
        Vector2 tmp = healthBar.getPos();
        Vector2 spriteDimension = new Vector2(sprite.getDimension());
        tmp.y = sprite.getPos().y + spriteDimension.y + 5;
        tmp.x = sprite.getPos().x;
        float percentHealth = (float)health / maxHealth;
        spriteDimension.x *= percentHealth;
        tmp = healthBar.getDimension();
        tmp.x = spriteDimension.x;
    }
    
    @Override
    public int kill()
    {
        dead = true;
        return points;
    }

    public int attack(int damage)
    {
        System.out.println("health " + health + " damage " + damage);
        health -= damage;
        if(health <= 0)
            return kill();
        return 0;
    }
    
    @Override
    public boolean isDead()
    {
        return dead;
    }
}