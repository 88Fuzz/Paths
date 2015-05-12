package com.paths.drawable.movable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.paths.constants.TextureConstants;
import com.paths.drawable.SceneNode;

public class Bullet extends Moveable
{
    public enum Category
    {
        BASIC(1.0f, TextureConstants.SIMPLE_BULLET_KEY, false);
        
        private float speed;
        private String textureKey;
        private boolean followTarget;
        
        Category(float speed, String textureKey, boolean followTarget)
        {
            this.speed = speed;
            this.textureKey = textureKey;
            this.followTarget = followTarget;
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

    protected TextureAtlas atlas;
    protected SceneNode target;
    protected boolean followTarget;

    public Bullet(Category category, int mapTileWidth, int mapTileHeight, int tileSize, TextureAtlas atlas, SceneNode start, SceneNode target, SceneNode map)
    {
        super();
        init(category, mapTileWidth, mapTileHeight, tileSize, atlas, start, target, map);
    }
    
        public void init(Category category, int mapTileWidth, int mapTileHeight, int tileSize, TextureAtlas atlas, SceneNode start, SceneNode target, SceneNode map)
    {
        super.init(SceneNode.Category.NONE, mapTileWidth, mapTileHeight, tileSize, null, null);
        this.atlas = atlas;
        this.map = map;
        this.tilePos = start.getTilePosition();
        this.pos = start.getPosition();
        this.target = target;
        setCategory(category);
        calculateVelocity();
    }
    
    protected void setCategory(Category category)
    {
        maxVelocity = category.getSpeed();
        sprite.setTexture(atlas.findRegion(category.getTextureKey()));
        followTarget = category.followTarget;
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
    
    @Override
    protected void updateCurrent(SceneNode superNode, float dt)
    {
        super.updateCurrent(superNode, dt);
        if(followTarget)
            calculateVelocity();
    }
}
