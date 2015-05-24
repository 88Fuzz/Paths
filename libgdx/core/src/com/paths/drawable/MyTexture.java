package com.paths.drawable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class MyTexture
{
    private Vector2 pos;
    private Vector2 origin;
    private Vector2 dimension;
    private Vector2 scale;
    private float rotation;
    private Sprite sprite;

    public MyTexture(TextureRegion atlas, Vector2 pos, Vector2 origin,
            Vector2 dimension, Vector2 scale, float rotation)
    {
        this.pos = new Vector2();
        this.origin = new Vector2();
        this.dimension = new Vector2();
        this.scale = new Vector2();
        this.sprite = new Sprite();
        init(atlas, pos, origin, dimension, scale, rotation);
    }

    public void init(TextureRegion atlas, Vector2 pos, Vector2 origin,
            Vector2 dimension, Vector2 scale, float rotation)
    {
//        setOrigin(origin);
//        setDimension(dimension);
//        setRotation(rotation);
//        setScale(scale);
        setPos(pos);
        if(atlas != null)
            setTexture(atlas.getTexture());
        
//        setTexture(new Texture(Gdx.files.internal("OpenTile.png")));
//        setPos(new Vector2(0, 0));
    }

    public Vector2 getPos()
    {
        return getOrigin();
    }

    public void setPos(Vector2 pos)
    {
        sprite.setPosition(pos.x, pos.y);
//        this.pos.x = pos.x;
//        this.pos.y = pos.y;
    }
    
    public void setPos(float x, float y)
    {
        sprite.setPosition(x, y);
//        pos.x = x;
//        pos.y = y;
    }

    public Vector2 getOrigin()
    {
        return new Vector2(sprite.getOriginX(), sprite.getOriginY());
    }

    public void setOrigin(Vector2 origin)
    {
        sprite.setOrigin(origin.x, origin.y);
//        this.origin.x = origin.x;
//        this.origin.y = origin.y;
    }

    public Vector2 getDimension()
    {
        return dimension;
    }

    public void setDimension(Vector2 dimension)
    {
        sprite.setSize(dimension.x, dimension.y);
        this.dimension.x = dimension.x;
        this.dimension.y = dimension.y;
    }

    public Vector2 getScale()
    {
        return new Vector2(sprite.getScaleX(), sprite.getScaleY());
//        return scale;
    }

    public void setScale(Vector2 scale)
    {
        sprite.setScale(scale.x, scale.y);
//        this.scale.x = scale.x;
//        this.scale.y = scale.y;
    }

    public float getRotation()
    {
        return sprite.getRotation();
//        return rotation;
    }

    public void setRotation(float rotation)
    {
        sprite.setRotation(rotation);
//        this.rotation = rotation;
    }
    
    public void draw(Batch batch)
    {
        sprite.draw(batch);
    }

//    public TextureRegion getTextureRegion()
//    {
//        return texture;
//    }
//    
//    public Texture getTexture()
//    {
//        return texture.getTexture();
//    }
//
    public void setTexture(Texture texture)
    {
        sprite.setTexture(texture);
//        this.texture = texture;
    }
//
//    public int getRegionX()
//    {
//        return texture.getRegionX();
//    }
//
//    public int getRegionY()
//    {
//        return texture.getRegionY();
//    }
//
//    public int getRegionWidth()
//    {
//        return texture.getRegionWidth();
//    }
//
//    public int getRegionHeight()
//    {
//        return texture.getRegionHeight();
//    }
}
