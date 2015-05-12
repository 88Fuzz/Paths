package com.paths.drawable;

import java.util.Iterator;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.paths.constants.TextureConstants;

public class MapNode extends SceneNode
{
    public enum Category
    {
        START(1<<0, TextureConstants.START_TILE_KEY),
        END(1<<1, TextureConstants.END_TILE_KEY),
        BLOCK(1<<2, TextureConstants.BLOCK_TILE_KEY),
        REGULAR(1<<3, TextureConstants.GRASS_TILE_KEY),
        PATH(1<<4, TextureConstants.PATH_TILE_KEY),
        OPEN(1<<5, (TextureConstants.DEBUG) ? TextureConstants.OPEN_TILE_KEY: TextureConstants.GRASS_TILE_KEY),
        CLOSED(1<<6, (TextureConstants.DEBUG) ? TextureConstants.CLOSED_TILE_KEY: TextureConstants.GRASS_TILE_KEY),
        NONE(1<<7, TextureConstants.BLOCK_TILE_KEY),
        BLOCKING(BLOCK.getValue() | NONE.getValue(), ""),
        START_OR_END(START.getValue() | END.getValue(), "");
        
        private final int value;
        private final String textureKey;

        Category(int value, String textureKey)
        {
            this.value = value;
            this.textureKey = textureKey;
        }
        
        public int getValue()
        {
            return value;
        }

        public String getTextureKey()
        {
            return textureKey;
        }
    }

    private Category nodeType;
    private float gValue;
    private float hValue;
    private MapNode parentPathNode;
    private MapNode childPathNode;
    private TextureAtlas atlas;
    private Vector2 deleteMe;

    public MapNode(TextureAtlas atlas, int x, int y, int width, int height, int mapTileWidth, int mapTileHeight, int tileSize, Category type)
    {
        super();
        init(atlas, x, y, width, height, mapTileWidth, mapTileHeight, tileSize, type);
    }
    
    public void init(TextureAtlas atlas, int x, int y, int width, int height, int mapTileWidth, int mapTileHeight, int tileSize, Category type)
    {
        super.init(SceneNode.Category.NONE, mapTileWidth, mapTileHeight, tileSize, null, null);
        deleteMe = new Vector2(x, y);
        pos = new Vector2(x * width,y * height);
        gValue = 0;
        hValue = 0;
        parentPathNode = null;
        childPathNode = null;
        this.atlas = atlas;
        sprite = new MyTexture(null, pos, new Vector2(width/2, height/2),new Vector2(tileSize, tileSize), new Vector2(1,1), 0);
        setType(type);
    }

    public void setType(Category type)
    {
        nodeType = type;
        setTexture(type);
    }
    
    public void drawCurrent(SpriteBatch batch)
    {
        if(nodeType == Category.NONE)
            return;

        Vector2 texturePos = sprite.getPos();
        Vector2 origin = sprite.getOrigin();
        Vector2 dimension = sprite.getDimension();
        Vector2 scale = sprite.getScale();

		batch.draw(sprite.getTexture(), texturePos.x, texturePos.y,
		        origin.x, origin.y, dimension.x, dimension.y,
		        scale.x, scale.y, sprite.getRotation(), sprite.getRegionX(), sprite.getRegionY(),
		        sprite.getRegionWidth(), sprite.getRegionHeight(), false, false);
    }

    public Category getType()
    {
        return nodeType;
    }

    public Vector2 getCenteredPosition()
    {
        Vector2 tmpPos = new Vector2(pos.x + mapTileWidth/2, pos.y + mapTileHeight/2);
        return tmpPos;
    }

    //gValue + hValue
    public float getFValue()
    {
        return gValue + hValue;
    }

    public float getDistanceFromStart()
    {
        return gValue;
    }
    public float getDistanceToEnd()
    {
        return hValue;
    }

    public void setDistanceFromStart(float value)
    {
        gValue = value;
    }

    public void setDistanceToEnd(float value)
    {
        hValue = value;
    }

    public MapNode getParentPathNode()
    {
        return parentPathNode;
    }

    public void setParentPathNode(MapNode node)
    {
        parentPathNode = node;
    }

    public MapNode getChildPathNode()
    {
        return childPathNode;
    }

    public void setChildPathNode(MapNode node)
    {
        childPathNode = node;
    }

    private void setTexture(MapNode.Category type)
    {
        if(atlas != null)
            sprite.setTexture(atlas.findRegion(type.getTextureKey()));
    }
    
    public Iterator<SceneNode> getChildrenIterator()
    {
        return children.iterator();
    }

    @Override
    public void printDebugCurrent()
    {
        System.out.println("tile number " + deleteMe);
    }
}
