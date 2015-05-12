package com.paths.GameStates;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.paths.constants.TextureConstants;
import com.paths.drawable.MapNode;
import com.paths.drawable.SceneNode;
import com.paths.drawable.MapNode.Category;
import com.paths.drawable.movable.Mob;
import com.paths.utils.GraphicsUtils;
import com.paths.utils.PathGenerator;

public class GameState extends ApplicationAdapter implements InputProcessor
{
    private SceneNode map;
    private int tileSize;
    private TextureAtlas atlas;
    private SpriteBatch batch;
    private AssetManager assMan;
    private MapNode.Category squareType;
    private MapNode startNode;
    private MapNode endNode;
    private Vector2 windowTileSize;
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
		batch = new SpriteBatch();
        tileSize = 30;
        windowTileSize = new Vector2();
        windowTileSize.x = Gdx.graphics.getWidth() / tileSize;
        windowTileSize.y = Gdx.graphics.getHeight() / tileSize;
        map = new MapNode(null, 0, 0, 0, 0, (int)windowTileSize.x, (int)windowTileSize.y, tileSize, MapNode.Category.NONE);
        squareType = MapNode.Category.START;
        startNode = null;
        endNode = null;
        
        for(int j = 0; j < windowTileSize.y; j++)
        {
            for(int i = 0; i < windowTileSize.x; i++)
            {
                map.attachChild(new MapNode(atlas, i, j, tileSize, tileSize, (int)windowTileSize.x, (int)windowTileSize.y, tileSize, MapNode.Category.REGULAR));
            }
        }
        
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
        //TODO add the while buffer in case dt gets waaaaay too large
        map.update(map, Gdx.graphics.getDeltaTime());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//TODO figure out if the batch.begin()/batch.end() should be moved to the individual draw methods?
		batch.begin();
        map.draw(batch);
        batch.end();
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
        if(keycode == Input.Keys.ENTER)
        {
            PathGenerator.findPath((MapNode) map, startNode, endNode, windowTileSize);
            map.layerChildNode(
                    new Mob(SceneNode.Category.NONE, atlas, (int)windowTileSize.x, (int)windowTileSize.y, tileSize, startNode, endNode, map),
                    SceneNode.get1d((int)startNode.getTilePosition().x, (int)startNode.getTilePosition().y, (int)windowTileSize.x));
        }
            
        map.printDebug();
        return true;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        // TODO Auto-generated method stub
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
        screenY = GraphicsUtils.flipY(screenY);
        MapNode tile = (MapNode) map.getChildNode(SceneNode.get1d(screenX/tileSize, screenY/tileSize, (int)windowTileSize.x));
        
        System.out.println("touched at x: " + screenX + " y: " + screenY + " 1d " +SceneNode.get1d(screenX/tileSize, screenY/tileSize, (int)windowTileSize.x));
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
        screenY = GraphicsUtils.flipY(screenY);
        MapNode tile = (MapNode) map.getChildNode(SceneNode.get1d(screenX/tileSize, screenY/tileSize, (int)windowTileSize.x));
        if(tile == null)
            return true;

        if(tile.getType() != MapNode.Category.START && tile.getType() != MapNode.Category.END)
            tile.setType(squareType);
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
