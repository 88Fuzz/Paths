package com.paths.drawable.towers;

import java.util.Iterator;
import java.util.LinkedList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.paths.constants.TextureConstants;
import com.paths.drawable.SceneNode;
import com.paths.drawable.movable.Bullet;
import com.paths.drawable.movable.mob.Mob;

public class Tower extends SceneNode
{
    public enum Category
    {
        BLOCK(2, 10000, 3, 5, 20, TextureConstants.BLOCK_TILE_KEY, Bullet.Category.BASIC);
        
        private int shootRadius;
        private int maxBullets;
        private int crumbCost;
        private float shootDelay;
        private float buildSpeed;
        private String textureKey;
        private Bullet.Category bulletType;

        Category(int shootRadius, float shootDelay, int maxBullets, float buildSpeed, int crumbCost, String textureKey, Bullet.Category bulletType)
        {
            this.shootRadius = shootRadius;
            this.shootDelay = shootDelay;
            this.textureKey = textureKey;
            this.bulletType = bulletType;
            this.maxBullets = maxBullets;
            this.buildSpeed = buildSpeed;
            this.crumbCost = crumbCost;
        }
        
        public int getCrumbCost()
        {
            return crumbCost;
        }

        public int getShootRadius()
        {
            return shootRadius;
        }
        
        public float getBuildSpeed()
        {
            return buildSpeed;
        }

        public String getTextureKey()
        {
            return textureKey;
        }
        
        public float getShootDelay()
        {
            return shootDelay;
        }
        
        public int getMaxBullets()
        {
            return maxBullets;
        }
        
        public Bullet.Category getBulletType()
        {
            return bulletType;
        }
    }
    
    private enum State
    {
        INITIAL,
        BUILDING,
        ACTIVE;
    }
    
    private static final float INITIAL_ALPHA = 0.2f;
    protected int shootRadius;
    protected float maxShootDelay;
    protected float shootDelay;
    protected float buildSpeed;
    protected TextureAtlas atlas;
    protected SceneNode map;
    protected Bullet.Category bulletType;
    private LinkedList<Bullet> freeBullets;
    private LinkedList<Bullet> activeBullets;
    private Sprite buildBar;
    private State state;
    private int crumbCost;
    
    /*
     * Init needs to be called after this
     */
    public Tower() { }
    
    public Tower(Category category, int x, int y, Vector2 windowTileSize, int tileSize, TextureAtlas atlas, SceneNode map)
    {
        super();
        init(category, x, y, windowTileSize, tileSize, atlas, map);
    }
    
    public void init(Category category, int x, int y, Vector2 windowTileSize, int tileSize, TextureAtlas atlas, SceneNode map)
    {
        super.init(SceneNode.Category.TOWER, windowTileSize, tileSize, new Vector2(x,y), null);
        state = State.INITIAL;
        this.atlas = atlas;
        this.map = map;
        sprite = new Sprite(atlas.findRegion(category.getTextureKey()));
        sprite.setPosition(pos.x, pos.y);
        sprite.setAlpha(INITIAL_ALPHA);
        
        buildBar = new Sprite(atlas.findRegion(TextureConstants.WHITE_PIXEL));
        buildBar.setColor(1, 21f/255f, 21f/255f, 0);

        freeBullets = new LinkedList<Bullet>();
        activeBullets = new LinkedList<Bullet>();
        setCategory(category);
    }
    
    protected void setCategory(Category category)
    {
        shootRadius = category.getShootRadius();
        shootDelay = maxShootDelay = category.getShootDelay();
        bulletType = category.getBulletType();
        buildSpeed = category.getBuildSpeed();
        crumbCost = category.getCrumbCost();
        for(int count = 0; count < category.getMaxBullets(); count++)
        {
            freeBullets.add(new Bullet());
        }
    }
    
    public void attackTower(SceneNode node)
    {
        shootBullet(node);
    }
    
    /*
     * Used for homing bullets
     */
    private void shootBullet(SceneNode node)
    {
        if(freeBullets.size() == 0)
            return;
        
        Bullet bullet = freeBullets.pop();
        bullet.init(bulletType, getWindowTileSize(), tileSize, atlas, this, node, map);
        map.futureLayerChildNode(bullet, SceneNode.get1d((int)tilePos.x, (int)tilePos.y, getWindowTileSizeWidth()));
        activeBullets.add(bullet);
        shootDelay = maxShootDelay;
    }
    
    @Override
    public void touched()
    {
        if(state.equals(State.INITIAL))
        {
            sprite.setAlpha(0);
            state = State.BUILDING;
        }
    }
    
    @Override
    protected void updateCurrent(SceneNode superNode, float dt)
    {
        if(state.equals(State.INITIAL))
        {
            buildBar.setAlpha(1);
            return;
        }
        else if(state.equals(State.BUILDING))
        {
            Color color = sprite.getColor();
            color.a += buildSpeed * dt;
            if(color.a >= 1)
            {
                color.a = 1;
                buildBar.setAlpha(0);
                state = State.ACTIVE;
            }
            sprite.setColor(color);
            updateBuildBar();

            return;
        }

        Bullet bullet;
        for(Iterator<Bullet> iterator = activeBullets.iterator(); iterator.hasNext();)
        {
            bullet = iterator.next();
            if(bullet.isDead())
            {
                iterator.remove();
                freeBullets.add(bullet);
            }
        }
        
        shootDelay -= dt;

        if(freeBullets.size() == 0 || shootDelay > 0)
            return;
        
        Vector2 tileSize = getWindowTileSize();
        
        //TODO towers should attack mobs that are closest to the exit
        for(int i = (int) (tilePos.x -shootRadius); i <= tilePos.x + shootRadius; i++)
        {
            for(int j = (int) (tilePos.y - shootRadius); j <= tilePos.y + shootRadius; j++)
            {
                if(i < 0 || j < 0)
                    continue;
                else if(i > tileSize.x - 1 || j > tileSize.y -1)
                    continue;
                else if(i == tilePos.x && j == tilePos.y)
                    continue;
                
                SceneNode tileNode = map.getChildNode(SceneNode.get1d(i, j, (int)tileSize.x));
                if(tileNode != null)
                {
                    SceneNode child;
                    Iterator<SceneNode> it = tileNode.getChildenIterator();
                    while(it.hasNext())
                    {
                        child = it.next();
                        if(child instanceof Mob)
                        {
                            //Pass in child if it should follow mob
                            shootBullet(tileNode);
                            return;
                        }
                    }
                }
            }
        }
    }
    
    public void updateBuildBar()
    {
        Color color = sprite.getColor();
        Rectangle spriteBounds = sprite.getBoundingRectangle();
        float percent = color.a / 1.0f;
        int percentTop = (int) (spriteBounds.height * 5/30f);
        buildBar.setBounds(spriteBounds.x, spriteBounds.y + spriteBounds.height - percentTop, spriteBounds.width * percent, percentTop);
    }

    @Override
    public void drawCurrent(SpriteBatch batch)
    {
        sprite.draw(batch);
        buildBar.draw(batch);
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
    
    public int getCrumbCost()
    {
        return crumbCost;
    }
}
