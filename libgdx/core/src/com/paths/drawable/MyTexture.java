package com.paths.drawable;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class MyTexture
{
    private Vector2 pos;
    private Vector2 origin;
    private Vector2 dimension;
    private Vector2 scale;
    private float rotation;
    private TextureRegion texture;

    public MyTexture(TextureRegion atlas, Vector2 pos, Vector2 origin,
            Vector2 dimension, Vector2 scale, float rotation)
    {
        this.pos = new Vector2();
        this.origin = new Vector2();
        this.dimension = new Vector2();
        this.scale = new Vector2();
        init(atlas, pos, origin, dimension, scale, rotation);
    }

    public void init(TextureRegion atlas, Vector2 pos, Vector2 origin,
            Vector2 dimension, Vector2 scale, float rotation)
    {
        setOrigin(origin);
        setDimension(dimension);
        setRotation(rotation);
        setScale(scale);
        setPos(pos);
        setTexture(atlas);
    }

    public Vector2 getPos()
    {
        return pos;
    }

    public void setPos(Vector2 pos)
    {
        this.pos.x = pos.x;
        this.pos.y = pos.y;
    }
    
    public void setPos(float x, float y)
    {
        pos.x = x;
        pos.y = y;
    }

    public Vector2 getOrigin()
    {
        return origin;
    }

    public void setOrigin(Vector2 origin)
    {
        this.origin.x = origin.x;
        this.origin.y = origin.y;
    }

    public Vector2 getDimension()
    {
        return dimension;
    }

    public void setDimension(Vector2 dimension)
    {
        this.dimension.x = dimension.x;
        this.dimension.y = dimension.y;
    }

    public Vector2 getScale()
    {
        return scale;
    }

    public void setScale(Vector2 scale)
    {
        this.scale.x = scale.x;
        this.scale.y = scale.y;
    }

    public float getRotation()
    {
        return rotation;
    }

    public void setRotation(float rotation)
    {
        this.rotation = rotation;
    }

    public TextureRegion getTextureRegion()
    {
        return texture;
    }
    
    public Texture getTexture()
    {
        return texture.getTexture();
    }

    public void setTexture(TextureRegion texture)
    {
        this.texture = texture;
    }

    public int getRegionX()
    {
        return texture.getRegionX();
    }

    public int getRegionY()
    {
        return texture.getRegionY();
    }

    public int getRegionWidth()
    {
        return texture.getRegionWidth();
    }

    public int getRegionHeight()
    {
        return texture.getRegionHeight();
    }
}
