package com.paths.utils;

import com.badlogic.gdx.math.Vector2;
import com.paths.drawable.SceneNode;

public class CollisionDetection
{
    public static boolean checkCollision(SceneNode obj1, SceneNode obj2)
    {
        Vector2 obj1Pos = obj1.getPosition();
        Vector2 obj1Dimension = obj1.getDimension();
        Vector2 obj2Pos = obj2.getPosition();
        Vector2 obj2Dimension = obj2.getDimension();
        if(obj1Pos.x < obj2Pos.x + obj2Dimension.x &&
                obj1Pos.x + obj1Dimension.x > obj2Pos.x)
        {
            if(obj1Pos.y < obj2Pos.y + obj2Dimension.y &&
                    obj1Pos.y + obj1Dimension.y > obj2Pos.y)
            {
                return true;
            }
        }
        return false;
    }
    
    public static float getDistance(Vector2 pos1, Vector2 pos2)
    {
        return getDistance(pos1.x, pos1.y, pos2.x, pos2.y);
    }
    
    public static float getDistance(float x1, float y1, float x2, float y2)
    {
        float xDist = x1 - x2;
        float yDist = y1 - y2;
        float retVal = xDist * xDist + yDist * yDist;

        return (float) Math.sqrt(retVal);
    }
}
