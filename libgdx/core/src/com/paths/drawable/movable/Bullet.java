package com.paths.drawable.movable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.paths.constants.TextureConstants;
import com.paths.drawable.MyTexture;
import com.paths.drawable.SceneNode;

public class Bullet extends Moveable
{
    public enum Category
    {
        BASIC(100.0f, TextureConstants.SIMPLE_BULLET_KEY, true);
        
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
        super.init(SceneNode.Category.NONE, mapTileWidth, mapTileHeight, tileSize, map);
        this.atlas = atlas;
        this.map = map;
        this.tilePos = start.getTilePosition();
        this.pos = start.getPosition();
        this.target = target;
        //TODO make these values configurable based on bullet type. YO
        this.sprite = new MyTexture(null, pos, new Vector2(15/2.0f, 15/2.0f), new Vector2(15, 15), new Vector2(1, 1), 0);
//        this.sprite = new MyTexture(null, pos, new Vector2(150, 150), new Vector2(300, 300), new Vector2(1, 1), 0);
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
    protected void updateCurrent(SceneNode superNode, float dt)
    {
        super.updateCurrent(superNode, dt);
        if(followTarget)
            calculateVelocity();
    }
}
