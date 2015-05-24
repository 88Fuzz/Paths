package com.paths.drawable.towers;

import java.util.Iterator;
import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.paths.constants.TextureConstants;
import com.paths.drawable.SceneNode;
import com.paths.drawable.movable.Bullet;
import com.paths.drawable.movable.Mob;
import com.paths.utils.GraphicsUtils;

public class Tower extends SceneNode
{
    public enum Category
    {
        BLOCK(2, 1, 1, TextureConstants.BLOCK_TILE_KEY, Bullet.Category.BASIC);
        
        private int shootRadius;
        private int maxBullets;
        private float shootDelay;
        private String textureKey;
        private Bullet.Category bulletType;

        Category(int shootRadius, float shootDelay, int maxBullets, String textureKey, Bullet.Category bulletType)
        {
            this.shootRadius = shootRadius;
            this.shootDelay = shootDelay;
            this.textureKey = textureKey;
            this.bulletType = bulletType;
            this.maxBullets = maxBullets;
        }

        public int getShootRadius()
        {
            return shootRadius;
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
    
    protected int shootRadius;
    protected float maxShootDelay;
    protected float shootDelay;
    protected TextureAtlas atlas;
    protected SceneNode map;
    protected Bullet.Category bulletType;
    private LinkedList<Bullet> freeBullets;
    private LinkedList<Bullet> activeBullets;
    
    /*
     * Init needs to be called after this
     */
    public Tower() { }
    
    public Tower(Category category, int x, int y, int mapTileWidth, int mapTileHeight, int tileSize, TextureAtlas atlas, SceneNode map)
    {
        super();
        init(category, x, y, mapTileWidth, mapTileHeight, tileSize, atlas, map);
    }
    
    public void init(Category category, int x, int y, int mapTileWidth, int mapTileHeight, int tileSize, TextureAtlas atlas, SceneNode map)
    {
        super.init(SceneNode.Category.NONE, mapTileWidth, mapTileHeight, tileSize, new Vector2(x,y), null);
        this.atlas = atlas;
        this.map = map;
        sprite = new Sprite();
        sprite.setPosition(pos.x, pos.y);

        freeBullets = new LinkedList<Bullet>();
        activeBullets = new LinkedList<Bullet>();
        setCategory(category);
    }
    
    protected void setCategory(Category category)
    {
        shootRadius = category.getShootRadius();
        shootDelay = maxShootDelay = category.getShootDelay();
        bulletType = category.getBulletType();
        GraphicsUtils.applyTextureRegion(sprite, atlas.findRegion(category.getTextureKey()));
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
        bullet.init(bulletType, mapTileWidth, mapTileHeight, tileSize, atlas, this, node, map);
        map.futureLayerChildNode(bullet, SceneNode.get1d((int)tilePos.x, (int)tilePos.y, mapTileWidth));
        activeBullets.add(bullet);
    }
    
    @Override
    protected void updateCurrent(SceneNode superNode, float dt)
    {
        Bullet bullet;
        for(Iterator<Bullet> iterator = activeBullets.iterator(); iterator.hasNext();)
        {
            bullet = iterator.next();
            if(bullet.isDead())
            {
                iterator.remove();
                
//                System.out.println(bullet.getPoints());
                freeBullets.add(bullet);
            }
        }
        
        shootDelay -= dt;

        if(freeBullets.size() == 0 || shootDelay > 0)
            return;
        
        
        //TODO towers should attack mobs that are closest to the exit
        for(int i = (int) (tilePos.x -shootRadius); i <= tilePos.x + shootRadius; i++)
        {
            for(int j = (int) (tilePos.y - shootRadius); j <= tilePos.y + shootRadius; j++)
            {
                if(i < 0 || j < 0)
                    continue;
                else if(i > mapTileWidth - 1 || j > mapTileWidth -1)
                    continue;
                else if(i == tilePos.x && j == tilePos.y)
                    continue;
                
                SceneNode tileNode = map.getChildNode(SceneNode.get1d(i, j, mapTileWidth));
                SceneNode child;
                Iterator<SceneNode> it = tileNode.getChildenIterator();
                while(it.hasNext())
                {
                    child = it.next();
                    if(child instanceof Mob)
                    {
                        //Pass in child if it should follow mob
                        shootBullet(tileNode);
                        shootDelay = maxShootDelay;
                        return;
                    }
                }
            }
        }
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
}