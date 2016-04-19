package com.paths.drawable;

import java.util.Iterator;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.paths.constants.TextureConstants;
import com.paths.utils.GraphicsUtils;
import com.paths.utils.PathGenerator.AStarNodeType;

public class MapNode extends SceneNode
{
    public enum Category
    {
        START(1<<0, TextureConstants.START_TILE_KEY),
        END(1<<1, TextureConstants.END_TILE_KEY),
        BLOCK(1<<2, TextureConstants.GRASS_TILE_KEY),
        REGULAR(1<<3, TextureConstants.GRASS_TILE_KEY),
        PATH(1<<4, TextureConstants.PATH_TILE_KEY),
//        OPEN(1<<5, (TextureConstants.DEBUG) ? TextureConstants.OPEN_TILE_KEY: TextureConstants.GRASS_TILE_KEY),
//        CLOSED(1<<6, (TextureConstants.DEBUG) ? TextureConstants.CLOSED_TILE_KEY: TextureConstants.GRASS_TILE_KEY),
        NONE(1<<7, TextureConstants.BLOCK_TILE_KEY),
        BLOCKING(BLOCK.getValue() | NONE.getValue(), ""),
        START_OR_END(START.getValue() | END.getValue(), ""),
        BLOCKING_START_EXIT(BLOCKING.getValue() | START_OR_END.getValue(), "");
        
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
    //An invalid path is used to determine is a mob has deviated from the real path to the exit and needs to recalculate
    private boolean invalidPath;
    private TextureAtlas atlas;
    private AStarNodeType pathFindingType;

    public MapNode(TextureAtlas atlas, int x, int y, int width, int height, Vector2 windowTileSize, int tileSize, Category type)
    {
        super();
        init(atlas, x, y, width, height, windowTileSize, tileSize, type);
    }
    
    public void init(TextureAtlas atlas, int x, int y, int width, int height, Vector2 windowTileSize, int tileSize, Category type)
    {
        super.init(SceneNode.Category.NONE, windowTileSize, tileSize, new Vector2(x*width, y*height), null);
        pathFindingType = AStarNodeType.NONE;
        gValue = 0;
        hValue = 0;
        parentPathNode = null;
        childPathNode = null;
        this.atlas = atlas;
        sprite = new Sprite();
        sprite.setPosition(pos.x, pos.y);
        invalidPath = true;
        setType(type);
    }

    public void setType(Category type)
    {
        if(nodeType == Category.BLOCK)
            System.out.println("replacing a block");
        else if(nodeType == Category.PATH)
            System.out.println("replacing a path");
        nodeType = type;
        setTexture(type);
    }
    
    public void drawCurrent(SpriteBatch batch)
    {
        if(nodeType == Category.NONE)
            return;
        
        sprite.draw(batch);

//        Vector2 texturePos = sprite.getPos();
//        Vector2 origin = sprite.getOrigin();
//        Vector2 dimension = sprite.getDimension();
//        Vector2 scale = sprite.getScale();
//
//		batch.draw(sprite.getTexture(), texturePos.x, texturePos.y,
//		        origin.x, origin.y, dimension.x, dimension.y,
//		        scale.x, scale.y, sprite.getRotation(), sprite.getRegionX(), sprite.getRegionY(),
//		        sprite.getRegionWidth(), sprite.getRegionHeight(), false, false);
    }

    public Category getType()
    {
        return nodeType;
    }

//    public Vector2 getCenteredPosition()
//    {
//        Vector2 tmpPos = new Vector2(pos.x + mapTileWidth/2, pos.y + mapTileHeight/2);
//        return tmpPos;
//    }

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
            GraphicsUtils.applyTextureRegion(sprite, atlas.findRegion(type.getTextureKey()));
    }
    
    public Iterator<SceneNode> getChildrenIterator()
    {
        return children.iterator();
    }
    
    public boolean isValidPath()
    {
        return !invalidPath;
    }
    
    public void validPath()
    {
        invalidPath = false;
    }
    
    public void invalidatePath()
    {
        invalidPath = true;
    }

    @Override
    public void printDebugCurrent()
    {
        //System.out.println("tile number " + deleteMe);
    }
    
    public void setPathFindingType(AStarNodeType type)
    {
        pathFindingType = type;
    }
    
    public AStarNodeType getPathFindingType()
    {
        return pathFindingType;
    }
}
