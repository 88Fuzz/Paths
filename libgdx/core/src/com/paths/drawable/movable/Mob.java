package com.paths.drawable.movable;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.paths.constants.TextureConstants;
import com.paths.drawable.MapNode;
import com.paths.drawable.SceneNode;
import com.paths.utils.CollisionDetection;

public class Mob extends Moveable
{
    public enum Category
    {
        BASIC(200, 100, 100, 500, 10, new Vector2(30, 30), TextureConstants.CIRCLE_KEY, 1.0f);
        private float speed;
        private int health;
        private int damage;
        private int points;
        private int crumbs;
        private Vector2 dimension;
        private Vector2 hitbox;
        private String textureKey;
        private float globalSpawnDelay;
        
        Category(float speed, int health, int damage, int points, int crumbs, Vector2 dimension, String textureKey, float globalSpawnDelay)
        {
            this.speed = speed;
            this.health = health;
            this.damage = damage;
            this.dimension = dimension;
            this.hitbox = dimension;
            this.textureKey = textureKey;
            this.points = points;
            this.globalSpawnDelay = globalSpawnDelay;
            this.crumbs = crumbs;
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

        public float getGlobalSpawnDelay()
        {
            return globalSpawnDelay;
        }
        
        public int getCrumbs()
        {
            return crumbs;
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
    private int crumbs;
    private int maxHealth;
    private boolean dead;
    private Sprite healthBar;
    private float globalSpawnDelay;

    public Mob(Mob.Category category, TextureAtlas atlas, int mapWidth, int mapHeight, int tileSize, MapNode start, MapNode end, SceneNode map)
    {
        super();
        init(category, atlas, start, end, map, mapWidth, mapHeight, tileSize);
    }

    public void init(Mob.Category category, TextureAtlas atlas, MapNode start, MapNode end, SceneNode map, int mapWidth, int mapHeight, int tileSize)
    {
        super.init(SceneNode.Category.MOB, mapTileWidth, mapTileHeight, tileSize, map);
        setCategoryProperties(category);

        //TODO change this to have a configurable everything for when there are multiple types of mobs. YO
        this.startNode = start;
        this.endNode = end;
        this.map = map;
        this.parentNode = start;
        this.currentNode = start;
        this.distanceToTravel = 0;

        tilePos = startNode.getTilePosition();
        pos = startNode.getPosition();
//        this.pos = start.getCenteredPosition();
//        this.pos.x -= category.getDimension().x/2;
//        this.pos.y -= category.getDimension().y/2;

        //TODO Create an enum with these properties
        //TODO and this score
        //TODO and the above dimensions
        dead = false;

        sprite = new Sprite(atlas.findRegion(category.getTextureKey()));
        sprite.setPosition(pos.x, pos.y);

        healthBar = new Sprite(atlas.findRegion(TextureConstants.WHITE_PIXEL));
        healthBar.setColor(127f/255f, 231f/255f, 4f/255f, 1.0f);
        updateHealthBar();

        calculateVelocity();
    }
    
    private void setCategoryProperties(Mob.Category category)
    {
        this.maxVelocity = category.getSpeed();
        points = category.getPoints();
        health = maxHealth = category.getHealth();
        globalSpawnDelay = category.getGlobalSpawnDelay();
        crumbs = category.getCrumbs();
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

//            sprite.setPos(pos.x, pos.y);
            sprite.setPosition(pos.x, pos.y);
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

        distanceToTravel = CollisionDetection.getDistance(futurePos, pos);
    }
    @Override
    public void drawCurrent(SpriteBatch batch)
    {
        super.drawCurrent(batch);
        
        healthBar.draw(batch);
    }

    @Override
    protected void updateCurrent(SceneNode superNode, float dt)
    {
        super.updateCurrent(superNode, dt);
        updateHealthBar();
        
        float distance = velocity.x*dt * velocity.x*dt + velocity.y*dt * velocity.y*dt;
        distanceToTravel -= Math.sqrt(distance);

        if(distanceToTravel <= 0)
        {
            calculateVelocity();
        }
    }
    
    private void updateHealthBar()
    {
        Vector2 tmp = new Vector2();
        Vector2 bounds = new Vector2();
        Rectangle spriteRect = sprite.getBoundingRectangle();
        tmp.y = spriteRect.y + spriteRect.height + 5;
        tmp.x = spriteRect.x;
        
        float percentHealth = (float)health / maxHealth;
        bounds.x = spriteRect.width * percentHealth;
        bounds.y = 5;
        healthBar.setBounds(tmp.x, tmp.y, bounds.x, bounds.y);
    }
    
    @Override
    public int kill()
    {
        dead = true;
        return points;
    }

    public int attack(int damage)
    {
        health -= damage;
        if(health <= 0)
            return kill();
        return 0;
    }
    
    @Override
    public int getPoints()
    {
        return points;
    }
    
    @Override
    public int getCrumbs()
    {
        return crumbs;
    }
    
    @Override
    public boolean isDead()
    {
        return dead;
    }

    public float getGlobalSpawnDelay()
    {
        return globalSpawnDelay;
    }
}