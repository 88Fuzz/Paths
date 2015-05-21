package com.paths.GameStates;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.paths.constants.TextureConstants;
import com.paths.drawable.MapNode;
import com.paths.drawable.SceneNode;
import com.paths.drawable.movable.Mob;
import com.paths.drawable.towers.Tower;
import com.paths.rendering.WorldRenderer;
import com.paths.utils.GraphicsUtils;
import com.paths.utils.PathGenerator;

public class GameState extends ApplicationAdapter implements InputProcessor
{
    private WorldRenderer worldRenderer;
    private SceneNode map;
    private int tileSize;
    private TextureAtlas atlas;
    private AssetManager assMan;
    private MapNode.Category squareType;
    private MapNode startNode;
    private MapNode endNode;
    private Vector2 windowTileSize;
    private Vector2 cameraMove;
    private float time;
    private static final float CAMERA_MOVE_CONSTANT = 1.5f;
    
    // @Override
    // public void create() {
    // batch = new SpriteBatch();
    // img = new Texture("badlogic.jpg");
    // }
    //
    // @Override
    // public void render() {
    // Gdx.gl.glClearColor(1, 0, 0, 1);
    // Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    // batch.begin();
    // batch.draw(img, 0, 0);
    // batch.end();
    // }

    @Override
    public void create()
    {
        assMan = new AssetManager();
		assMan.load(TextureConstants.TILE_TEXTURES, TextureAtlas.class);
		assMan.finishLoading();
		atlas = assMan.get(TextureConstants.TILE_TEXTURES);
        tileSize = 30;
        windowTileSize = new Vector2();
        windowTileSize.x = Gdx.graphics.getWidth() / tileSize;
        windowTileSize.y = Gdx.graphics.getHeight() / tileSize;
        map = new MapNode(null, 0, 0, 0, 0, (int)windowTileSize.x, (int)windowTileSize.y, tileSize, MapNode.Category.NONE);
        squareType = MapNode.Category.START;
        startNode = null;
        endNode = null;
        time = 1;
        
        for(int j = 0; j < windowTileSize.y; j++)
        {
            for(int i = 0; i < windowTileSize.x; i++)
            {
                map.attachChild(new MapNode(atlas, i, j, tileSize, tileSize, (int)windowTileSize.x, (int)windowTileSize.y, tileSize, MapNode.Category.REGULAR));
            }
        }
        worldRenderer = new WorldRenderer(map);
        cameraMove = new Vector2();
        
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void dispose()
    {
        //TODO remove once implemented
        atlas.dispose();
    }

    @Override
    public void pause()
    {
        //TODO remove once implemented
        super.pause();
    }

    @Override
    public void render()
    {
        worldRenderer.moveCamera(cameraMove);
        worldRenderer.render();

        //TODO add the while buffer in case dt gets waaaaay too large
        float dt = Gdx.graphics.getDeltaTime();
        map.update(map, dt);
        //This is a stupid hack to add sceneNodes to the map without getting concurent modification blah blah exception
        map.insertFutureAddMap();

        time -= dt;
        if(time < 0)
        {
            if(squareType == MapNode.Category.BLOCK)
            {
                PathGenerator.findPath((MapNode) map, startNode, endNode, windowTileSize);
                Mob tmp = 
                    new Mob(SceneNode.Category.NONE, atlas, (int)windowTileSize.x, (int)windowTileSize.y, tileSize, startNode, endNode, map);
                map.layerChildNode(tmp, SceneNode.get1d((int)startNode.getTilePosition().x, (int)startNode.getTilePosition().y, (int)windowTileSize.x));
            }
            time = 1;
        }
    }

    @Override
    public void resize(int width, int height)
    {
        //TODO remove once implemented
        super.resize(width, height);
    }

    @Override
    public void resume()
    {
        //TODO remove once implemented
        super.resume();

    }

    @Override
    public boolean keyDown(int keycode)
    {
        switch(keycode)
        {
        case Input.Keys.ENTER:
            PathGenerator.findPath((MapNode) map, startNode, endNode, windowTileSize);
            Mob tmp = 
                    new Mob(SceneNode.Category.NONE, atlas, (int)windowTileSize.x, (int)windowTileSize.y, tileSize, startNode, endNode, map);
            map.layerChildNode(tmp, SceneNode.get1d((int)startNode.getTilePosition().x, (int)startNode.getTilePosition().y, (int)windowTileSize.x));
            break;
        case Input.Keys.LEFT:
            cameraMove.x += CAMERA_MOVE_CONSTANT;
            break;
        case Input.Keys.RIGHT:
            cameraMove.x -= CAMERA_MOVE_CONSTANT;
            break;
        case Input.Keys.UP:
            cameraMove.y -= CAMERA_MOVE_CONSTANT;
            break;
        case Input.Keys.DOWN:
            cameraMove.y += CAMERA_MOVE_CONSTANT;
            break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        switch(keycode)
        {
        case Input.Keys.LEFT:
            cameraMove.x -= CAMERA_MOVE_CONSTANT;
            break;
        case Input.Keys.RIGHT:
            cameraMove.x += CAMERA_MOVE_CONSTANT;
            break;
        case Input.Keys.UP:
            cameraMove.y += CAMERA_MOVE_CONSTANT;
            break;
        case Input.Keys.DOWN:
            cameraMove.y -= CAMERA_MOVE_CONSTANT;
            break;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        Vector2 touch = GraphicsUtils.getNormalizedScreenTouch(screenX, screenY, worldRenderer.getCameraPosition());

        if(touch.x < 0 || touch.x >= Gdx.graphics.getWidth()
                || touch.y < 0 || touch.y >= Gdx.graphics.getHeight())
            return true;

        MapNode tile = (MapNode) map.getChildNode(SceneNode.get1d((int)touch.x/tileSize, (int)touch.y/tileSize, (int)windowTileSize.x));

        if(tile == null)
            return true;
        
        if(tile.getType() != MapNode.Category.BLOCKING)
        {
            if(squareType == MapNode.Category.START)
            {
                tile.setType(squareType);
                squareType = MapNode.Category.END;
                startNode = tile;
            }
            else if(squareType == MapNode.Category.END)
            {
                tile.setType(squareType);
                squareType = MapNode.Category.BLOCK;
                endNode = tile;
            }
        }
        return true;
    }
    


    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        Vector2 touch = GraphicsUtils.getNormalizedScreenTouch(screenX, screenY, worldRenderer.getCameraPosition());
        
        if(touch.x < 0 || touch.x >= Gdx.graphics.getWidth()
                || touch.y < 0 || touch.y >= Gdx.graphics.getHeight())
            return true;

        int oned = SceneNode.get1d((int)touch.x/tileSize, (int)touch.y/tileSize, (int)windowTileSize.x);
        MapNode tile = (MapNode) map.getChildNode(oned);
        if(tile == null)
            return true;

        if(tile.getType() != MapNode.Category.START && tile.getType() != MapNode.Category.END && tile.getType() != MapNode.Category.BLOCK)
        {
            tile.setType(squareType);

            //Due to int math, /tileSize*tileSize will get us the position divisible by tileSize
            Tower tmpTower = new Tower(Tower.Category.BLOCK, (int)touch.x/tileSize*tileSize, (int)touch.y/tileSize*tileSize, 
                    (int)windowTileSize.x, (int)windowTileSize.y, tileSize, atlas, map);

            tile.layerChildNode(tmpTower);
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean scrolled(int amount)
    {
        // TODO Auto-generated method stub
        return false;
    }
}
