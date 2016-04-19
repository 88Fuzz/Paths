package com.paths.drawable.movable.mob;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.paths.drawable.MapNode;
import com.paths.drawable.SceneNode;

public class Splitter extends Mob
{
    private static final int MAX_SPLITS = 3;
    private int numSplits;

    public Splitter(Category category, TextureAtlas atlas, Vector2 windowTileSize, int tileSize,
            MapNode start, MapNode end, SceneNode map)
    {
        this(category, atlas, windowTileSize, tileSize, start, end, map, MAX_SPLITS);
    }
    
    public Splitter(Category category, TextureAtlas atlas, Vector2 windowTileSize, int tileSize,
            MapNode start, MapNode end, SceneNode map, int numSplits)
    {
        super();
        init(category, atlas, windowTileSize, tileSize, start, end, map, numSplits);
    }
    
    public void init(Category category, TextureAtlas atlas, Vector2 windowTileSize, int tileSize,
            MapNode start, MapNode end, SceneNode map, int numSplits)
    {
        super.init(category, atlas, start, end, map, windowTileSize, tileSize);
        this.atlas = atlas;
        this.numSplits = numSplits;
    }
    
    
    public void onDeath()
    {
        super.onDeath();
        if(numSplits > 0)
        {
            //Add a new Splitter in the current tile
            MapNode tmpNode = (MapNode) getParentNode();
            Vector2 tmpPos = tmpNode.getTilePosition();
            int width = getWindowTileSizeWidth();
            Mob splitter = new Splitter(Mob.Category.SPLITTER, atlas, getWindowTileSize(), tileSize, tmpNode, endNode, map);
            map.futureLayerChildNode(splitter, SceneNode.get1d((int)tmpPos.x, (int)tmpPos.y, width));
        
            //Add a second splitter to the next tile ahead
            tmpNode = tmpNode.getChildPathNode();
            tmpPos = tmpNode.getTilePosition();
            setSwap(true);
            init(Mob.Category.SPLITTER, atlas, getWindowTileSize(), tileSize, tmpNode, endNode, map, numSplits--);
            map.futureLayerChildNode(this, SceneNode.get1d((int)tmpPos.x, (int)tmpPos.y, width));
        }
    }
}
