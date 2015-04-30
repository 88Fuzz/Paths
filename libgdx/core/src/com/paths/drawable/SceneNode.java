package com.paths.drawable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

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
        PARENT, MOB, NONE
    }

    private Category type;
    protected ArrayList<SceneNode> children;
    protected ArrayList<SceneNode> parents;

    public SceneNode(Category category)
    {
        init(category);
    }

    public void init(Category category)
    {
        type = category;
        children = new ArrayList<SceneNode>();
        parents = new ArrayList<SceneNode>();
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
            result.detachParent(this);
        }

        return result;
    }

    public void detachParent(SceneNode node)
    {
        for(Iterator<SceneNode> iterator = parents.iterator(); iterator
                .hasNext();)
        {
            if(node == iterator.next())
            {
                iterator.remove();
                break;
            }
        }
    }

    public void attachChild(SceneNode node)
    {
        children.add(node);
        node.attachParent(this);
    }

    public void attachParent(SceneNode node)
    {
        parents.add(node);
    }

    public void draw(SpriteBatch batch)
    {
        // Top of Queue will be current, pop it off
        // Check if empty for root node, which wont be pushed into queue
        if(!drawingQ.isEmpty())
            drawingQ.poll();

        drawCurrent(batch);

        for(SceneNode child : children)
        {
            drawingQ.add(child);
        }

        if(!drawingQ.isEmpty())
            drawingQ.peek().draw(batch);

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

    public void update(float dt)
    {
        updateCurrent(dt);
        updateChildren(dt);
    }

    protected void updateCurrent(float dt)
    {
        // Do nothing by default
    }

    private void updateChildren(float dt)
    {
        for(SceneNode child : children)
        {
            child.update(dt);
        }
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

        if(children.get(pos).getCategory() == category)
            return children.get(pos);

        return children.get(pos).getChildNode(pos, category);
    }

    public boolean layerChildNode(SceneNode node, int pos)
    {
        if(children.size() <= pos)
            return false;

        children.get(pos).attachChild(node);
        return true;
    }

    public Iterator<SceneNode> getChildenIterator()
    {
        return children.iterator();
    }

    public Iterator<SceneNode> getParentsIterator()
    {
        return parents.iterator();
    }

    public Category getCategory()
    {
        return type;
    }
    
    //TODO figure out how to implement this method
    public void dispose()
    {
        
    }
}