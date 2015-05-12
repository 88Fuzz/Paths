package com.paths.drawable.towers;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.paths.constants.TextureConstants;
import com.paths.drawable.MapNode;
import com.paths.drawable.SceneNode;
import com.paths.drawable.movable.Bullet;

public class Tower extends SceneNode
{
    public enum Category
    {
        BLOCK(1, 0.5f, TextureConstants.BLOCK_TILE_KEY, Bullet.Category.BASIC);
        
        private int shootRadius;
        private float shootDelay;
        private String textureKey;
        private Bullet.Category bulletType;

        Category(int shootRadius, float shootDelay, String textureKey, Bullet.Category bulletType)
        {
            this.shootRadius = shootRadius;
            this.shootDelay = shootDelay;
            this.textureKey = textureKey;
            this.bulletType = bulletType;
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
        
        public Bullet.Category getBulletType()
        {
            return bulletType;
        }
    }
    
    protected int shootRadius;
    protected float shootDelay;
    protected TextureAtlas atlas;
    protected MapNode map;
    
    /*
     * Init needs to be called after this
     */
    public Tower() { }
    
    public Tower(Category category, int mapWidth, int mapHeight, int tileSize, TextureAtlas atlas, MapNode map)
    {
        super();
        init(category, mapWidth, mapHeight, tileSize, atlas, map);
    }
    
    public void init(Category category, int mapWidth, int mapHeight, int tileSize, TextureAtlas atlas, MapNode map)
    {
        super.init(SceneNode.Category.NONE, mapWidth, mapHeight, tileSize, null, null);
        this.atlas = atlas;
        this.map = map;
        setCategory(category);
    }
    
    protected void setCategory(Category category)
    {
        shootRadius = category.getShootRadius();
        shootDelay = category.getShootDelay();
        sprite.setTexture(atlas.findRegion(category.getTextureKey()));
    }
}