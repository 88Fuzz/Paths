package com.paths.drawable.towers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.paths.constants.TextureConstants;
import com.paths.drawable.MyTexture;
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
    protected SceneNode map;
    protected Bullet.Category bulletType;
    
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
        System.out.println("(x,y) " + x + "," + y);
        super.init(SceneNode.Category.NONE, mapTileWidth, mapTileHeight, tileSize, new Vector2(x,y), null);
        this.atlas = atlas;
        this.map = map;
        sprite = new MyTexture(null, pos, new Vector2(15, 15), new Vector2(30, 30), new Vector2(1, 1), 0);
        setCategory(category);
    }
    
    protected void setCategory(Category category)
    {
        shootRadius = category.getShootRadius();
        shootDelay = category.getShootDelay();
        bulletType = category.getBulletType();
        sprite.setTexture(atlas.findRegion(category.getTextureKey()));
    }
    
    public void attackTower(SceneNode node)
    {
        map.layerChildNode(new Bullet(bulletType, mapTileWidth, mapTileHeight, tileSize, atlas, this, node, map), 
                SceneNode.get1d((int)pos.x/tileSize, (int)pos.y/tileSize, mapTileWidth));
    }

    @Override
    public void drawCurrent(SpriteBatch batch)
    {
        Vector2 texturePos = sprite.getPos();
        Vector2 origin = sprite.getOrigin();
        Vector2 dimension = sprite.getDimension();
        Vector2 scale = sprite.getScale();

        batch.draw(sprite.getTexture(), texturePos.x, texturePos.y,
                origin.x, origin.y, dimension.x, dimension.y,
                scale.x, scale.y, sprite.getRotation(), sprite.getRegionX(), sprite.getRegionY(),
                sprite.getRegionWidth(), sprite.getRegionHeight(), false, false);
    }
}