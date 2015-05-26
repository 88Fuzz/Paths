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
}
