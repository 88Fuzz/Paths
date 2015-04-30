package com.paths.utils;

import com.badlogic.gdx.Gdx;

public class GraphicsUtils
{
    //Libgdx is fucking stupid and has the touch positions based on origin on top left. All others have origin on bottom left. Flip y
    public static int flipY(int y)
    {
        return Gdx.graphics.getHeight()-y;
    }
}
