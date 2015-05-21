package com.paths.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class GraphicsUtils
{
    //Libgdx is fucking stupid and has the touch positions based on origin on top left. All others have origin on bottom left. Flip y
    public static int flipY(int y)
    {
        return Gdx.graphics.getHeight()-y;
    }
    
    public static Vector2 getNormalizedScreenTouch(int screenX, int screenY, Vector2 cameraPosition)
    {
        screenY = GraphicsUtils.flipY(screenY);
        return new Vector2(screenX + cameraPosition.x, screenY + cameraPosition.y);
    }
}
