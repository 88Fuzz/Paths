package com.paths.drawable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.paths.GameStates.GameState;
import com.paths.drawable.movable.Moveable;

/*
 * Lowest level drawable object
 */
public class SceneNode
{
    public static int get1d(int x, int y, int width)
    {
        return width * y + x;
    }

    protected static final LinkedList<SceneNode> drawingQ = new LinkedList<SceneNode>();

    public enum Category
    {
        PARENT, MOB, TOWER, NONE
    }

    private Category type;
    protected ArrayList<SceneNode> children;
    protected SceneNode parentNode;
    protected int mapTileWidth;
    protected int mapTileHeight;
    protected int tileSize;
    protected Vector2 pos;
    protected Sprite sprite;
    protected Vector2 tilePos;
    private HashMap<SceneNode, Integer> futureAddMap;
    
    /*
     * This should only be used if you plan to call init() after!
     */
    public SceneNode() { }

    public SceneNode(Category category, int mapWidth, int mapHeight, int tileSize, Vector2 pos, Sprite sprite)
    {
        init(category, mapWidth, mapHeight, tileSize, pos, sprite);
    }

    public void init(Category category, int mapTileWidth, int mapTileHeight, int tileSize, Vector2 pos, Sprite sprite)
    {
        type = category;
        children = new ArrayList<SceneNode>();
        parentNode = null;
        this.tileSize = tileSize;
        this.mapTileWidth = mapTileWidth;
        this.mapTileHeight = mapTileHeight;
        this.pos = pos;
        this.tilePos = new Vector2();
        tilePos.x = pos.x / tileSize;
        tilePos.y = pos.y / tileSize;
        this.sprite = sprite;
        futureAddMap = new HashMap<SceneNode, Integer>();
    }

    public void futureLayerChildNode(SceneNode node, int pos)
    {
        futureAddMap.put(node, pos);
    }

    public SceneNode detachChild(SceneNode node, int pos)
    {
        SceneNode result = null;
        
        if(children.size() < pos)
            return result;
        
        return children.get(pos).detachChild(node);
    }
    
    /*
     * used if the object is touched/clicked on
     */
    public void touched()
    {
        //Do nothing by default
    }

    public SceneNode detachChild(SceneNode node)
    {
        SceneNode result = null;
        for(Iterator<SceneNode> iterator = children.iterator(); iterator
                .hasNext();)
        {
            if(node == iterator.next())
            {
                result = node;
                iterator.remove();
                break;
            }
        }

        if(result != null)
        {
            result.detachParent();
        }

        return result;
    }

    public void detachParent()
    {
        parentNode = null;
    }

    public void attachChild(SceneNode node)
    {
        children.add(node);
        node.attachParent(this);
    }

    public void attachParent(SceneNode node)
    {
        parentNode = node;
    }
    
    public void drawTree(SpriteBatch batch)
    {
        draw(batch);

        while(!drawingQ.isEmpty())
            drawingQ.poll().draw(batch);
    }

    public void draw(SpriteBatch batch)
    {
        drawCurrent(batch);
        for(SceneNode child : children)
            drawingQ.add(child);
    }

    protected void drawCurrent(SpriteBatch batch)
    {
        // Do nothing by default
    }

    private void drawChildren(SpriteBatch batch)
    {
        for(SceneNode child : children)
        {
            child.draw(batch);
        }

    }

    public void update(SceneNode superNode, float dt)
    {
        //TODO add the while buffer in case dt gets waaaaay too large
        updateCurrent(superNode, dt);
        updateChildren(superNode, dt);
    }
    
    public void insertFutureAddMap()
    {
        for(Iterator<Map.Entry<SceneNode, Integer>> it = futureAddMap.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry<SceneNode, Integer> entry = it.next();
            layerChildNode(entry.getKey(), entry.getValue());
            it.remove();
        }
    }

    protected void updateCurrent(SceneNode superNode, float dt)
    {
        // Do nothing by default
    }

    private void updateChildren(SceneNode superNode, float dt)
    {
        SceneNode node;

        for(Iterator<SceneNode> it = children.iterator(); it.hasNext();)
        {
            node = it.next();
            node.update(superNode, dt);

            if(node.needToSwap())
            {
                it.remove();
                
                Vector2 tmpVector = ((Moveable) node).getTilePos();
                superNode.layerChildNode(node, SceneNode.get1d((int)tmpVector.x, (int)tmpVector.y, mapTileWidth));
            }
            else if(node.isDead())
            {
                node.onDeath();
                it.remove();
            }
            else if(node.isEndReached())
            {
                node.onEndReach();
                it.remove();
            }
        }
    }
    
    public int getPoints()
    {
        return 0;
    }
    
    public int getCrumbs()
    {
        return 0;
    }

    public SceneNode getChildNode(int pos)
    {
        if(children.size() <= pos)
            return null;
        return children.get(pos);
    }

    public SceneNode getChildNode(int pos, Category category)
    {
        if(children.size() <= pos)
            return null;

        if(children.get(pos).getCategory().equals(category))
            return children.get(pos);

        return children.get(pos).getChildNodeByCategory(category);
    }
    
    public SceneNode getChildNodeByCategory(Category category)
    {
        for(SceneNode child : children)
        {
            if(child.getCategory().equals(category))
                return child;
        }

        return null;
    }

    public boolean layerChildNode(SceneNode node, int pos)
    {
        if(children.size() <= pos)
            return false;

        children.get(pos).attachChild(node);
        return true;
    }
    
    public boolean layerChildNode(SceneNode node)
    {
        attachChild(node);
        return true;
    }

    public Iterator<SceneNode> getChildenIterator()
    {
        return children.iterator();
    }
    
    
    public SceneNode getParentNode()
    {
        return parentNode;
    }
    
    public Category getCategory()
    {
        return type;
    }
    
    public Vector2 getDimension()
    {
        //TODO things should have a different hitbox than drawing dimensions
        return new Vector2(sprite.getWidth(), sprite.getHeight());
//        return sprite.getDimension();
    }
    
    //TODO figure out how to implement this method
    public void dispose()
    {
        
    }

    public boolean needToSwap()
    {
        return false;
    }
    
    public void printDebug()
    {
        printDebugCurrent();
        printDebugChildren();
    }
    
    public void printDebugCurrent()
    {
        System.out.println("Master printer");
    }
    
    public void printDebugChildren()
    {
        for(Iterator<SceneNode> it = children.iterator(); it.hasNext();)
        {
            it.next().printDebug();
            
        }
    }

    public Vector2 getPosition()
    {
        Vector2 tmpPos = new Vector2(pos.x, pos.y);
        return tmpPos;
    }
    
    public Vector2 getCenteredPosition()
    {
        if(sprite == null)
            return getPosition();
        
        Vector2 dim = getDimension();
        
        Vector2 tmpPos = new Vector2(pos.x + dim.x/2, pos.y + dim.y/2);
        return tmpPos;
    }

    public Vector2 getTilePosition()
    {
        Vector2 tmpPos = new Vector2(pos.x / tileSize, pos.y / tileSize);
        return tmpPos;
    }
    
    public int getTileSize()
    {
        return tileSize;
    }
    
    public int getMapTileWidth()
    {
        return mapTileWidth;
    }
    
    public int getMapTileHeight()
    {
        return mapTileHeight;
    }
    
    public int kill()
    {
        return 0;
    }
    
    public void onDeath()
    {
        GameState.stats.addPoints(getPoints() * GameState.stats.getPointsMultiplier());
        GameState.stats.addCrumbs(getCrumbs() * GameState.stats.getCrumbMultiplier());
    }
    
    public float getDamage()
    {
        return 0;
    }
    
    public boolean isEndReached()
    {
        return false;
    }
    
    public void onEndReach()
    {
        GameState.stats.addHealth(getDamage());
    }
    
    public boolean isDead()
    {
        return false;
    }
}
