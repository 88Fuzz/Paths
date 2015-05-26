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
import com.paths.utils.CollisionDetection;
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
    private int moveRightMin;
    private int moveLeftMax;
    private int moveUpMin;
    private int moveDownMax;
    private float timeSinceLastUpdate;
    private Tower tmpTower;
    private static final float TIMEPERFRAME = 1.0f/60.0f;
    
    
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
        tmpTower = null;
        timeSinceLastUpdate = 0;
        
        for(int j = 0; j < windowTileSize.y; j++)
        {
            for(int i = 0; i < windowTileSize.x; i++)
            {
                map.attachChild(new MapNode(atlas, i, j, tileSize, tileSize, (int)windowTileSize.x, (int)windowTileSize.y, tileSize, MapNode.Category.REGULAR));
            }
        }
        worldRenderer = new WorldRenderer(map);
        cameraMove = new Vector2();
        moveLeftMax = (int) (Gdx.graphics.getWidth() * .2);
        moveRightMin = Gdx.graphics.getWidth() - moveLeftMax;
        moveDownMax = (int) (Gdx.graphics.getHeight() * .2);
        moveUpMin = Gdx.graphics.getHeight() - moveDownMax;
        
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
        float dt = Gdx.graphics.getDeltaTime();
        
        timeSinceLastUpdate += dt;
        while(timeSinceLastUpdate > TIMEPERFRAME)
        {
            timeSinceLastUpdate -= TIMEPERFRAME;
            update(TIMEPERFRAME);
        }
//        update(dt);
        worldRenderer.render();
    }
    
    private void update(float dt)
    {
        worldRenderer.moveCamera(cameraMove);
        map.update(map, dt);
        //This is a stupid hack to add sceneNodes to the map without getting concurent modification blah blah exception
        map.insertFutureAddMap();

        time -= dt;
        if(time < 0)
        {
            if(squareType == MapNode.Category.BLOCK)
            {
                Mob tmp = new Mob(Mob.Category.BASIC, atlas, (int)windowTileSize.x, (int)windowTileSize.y, tileSize, startNode, endNode, map);
                map.layerChildNode(tmp, SceneNode.get1d((int)startNode.getTilePosition().x, (int)startNode.getTilePosition().y, (int)windowTileSize.x));
            }
            time = 1;
        }
        
        checkCameraMove();
    }
    
    private void checkCameraMove()
    {
        if(!Gdx.input.isTouched(0) || !Gdx.input.isTouched(1))
        {
            cameraMove.x = 0;
            cameraMove.y = 0;
            return;
        }
        
        moveLeftMax = (int) (Gdx.graphics.getWidth() * .2);
        moveRightMin = Gdx.graphics.getWidth() - moveLeftMax;
        moveDownMax = (int) (Gdx.graphics.getHeight() * .2);
        moveUpMin = Gdx.graphics.getHeight() - moveDownMax;

        int x1 = Gdx.input.getX(0);
        int y1 = Gdx.input.getY(0);
        int x2 = Gdx.input.getX(1);
        int y2 = Gdx.input.getY(1);
        
        if(x1 >= moveRightMin  && x2 >= moveRightMin)
            cameraMove.x -= CAMERA_MOVE_CONSTANT;
        else if(x1 <= moveLeftMax && x2 <= moveLeftMax)
            cameraMove.x += CAMERA_MOVE_CONSTANT;
        else if(y1 >= moveUpMin && y2 >= moveUpMin)
            cameraMove.y -= CAMERA_MOVE_CONSTANT;
        else if(y1 <= moveDownMax && y2 <= moveDownMax)
            cameraMove.y += CAMERA_MOVE_CONSTANT;
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
                    new Mob(Mob.Category.BASIC, atlas, (int)windowTileSize.x, (int)windowTileSize.y, tileSize, startNode, endNode, map);
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
        {
            removeTmpTower();
            return true;
        }

        int oned = SceneNode.get1d((int)touch.x/tileSize, (int)touch.y/tileSize, (int)windowTileSize.x);
        SceneNode tile = map.getChildNode(oned);
        MapNode mapTile = (MapNode) tile;

        if(tile == null)
        {
            removeTmpTower();
            return true;
        }
        
        if(mapTile.getType() != MapNode.Category.BLOCKING)
        {
            if(squareType == MapNode.Category.START)
            {
                mapTile.setType(squareType);
                squareType = MapNode.Category.END;
                startNode = mapTile;
            }
            else if(squareType == MapNode.Category.END)
            {
                mapTile.setType(squareType);
                squareType = MapNode.Category.BLOCK;
                endNode = mapTile;
                PathGenerator.findPath((MapNode) map, startNode, endNode, windowTileSize);
            }
            else if(mapTile.getType() != MapNode.Category.START && mapTile.getType() != MapNode.Category.END)
            {
                if(mapTile.getType() != MapNode.Category.BLOCK)
                {
                    if(tmpTower == null)
                    {
                        //Due to int math, /tileSize*tileSize will get us the position divisible by tileSize
                        tmpTower = new Tower(Tower.Category.BLOCK, (int)touch.x/tileSize*tileSize, (int)touch.y/tileSize*tileSize, 
                                (int)windowTileSize.x, (int)windowTileSize.y, tileSize, atlas, map);

                        tile.layerChildNode(tmpTower);
                        return true;
                    }
                    else
                    {
                        Vector2 towerPos = tmpTower.getTilePosition();
                        if(towerPos.x == (int)touch.x/tileSize && towerPos.y == (int)touch.y/tileSize)
                        {
                            mapTile.setType(squareType);
                            if(validTowerPlacement(tile))
                            {
                                PathGenerator.findPath((MapNode) map, startNode, endNode, windowTileSize);
                                tmpTower.touched();
                                tmpTower = null;
                                return true;
                            }
                            mapTile.setType(MapNode.Category.REGULAR);
                        }
                    }
                }
            }
        }
        removeTmpTower();
        return true;
    }
    
    private void removeTmpTower()
    {
        if(tmpTower == null)
            return;
        
        Vector2 towerTile = tmpTower.getTilePosition();
        int oned = SceneNode.get1d((int)towerTile.x, (int)towerTile.y, (int)windowTileSize.x);
        SceneNode node = map.getChildNode(oned);
        node.detachChild(tmpTower);
        tmpTower = null;
    }

    /*
     * This method:
     * checks if there is a mob in this square already
     * verify that putting this tower down will not block the paths to the exit
     * 
     */
    private boolean validTowerPlacement(SceneNode tile)
    {
        if(!verifyEmptyTile(tile))
            return false;

        if(PathGenerator.findPath((MapNode) map, startNode, endNode, windowTileSize))
            return true;

        return false;
    }

    private boolean verifyEmptyTile(SceneNode tile)
    {
        Vector2 tilePos = tile.getTilePosition();
        SceneNode childNode;
        
        for(int i = (int)tilePos.x-1; i <= tilePos.x+1; i++)
        {
            for(int j = (int)tilePos.y-1; j <= tilePos.y+1; j++)
            {
                if(i < 0 || j < 0)
                    continue;
                else if(i > windowTileSize.x-1 || j > windowTileSize.y-1)
                    continue;

                int oned = SceneNode.get1d(i, j, (int)windowTileSize.x);
                
                if((childNode = map.getChildNode(oned, SceneNode.Category.MOB)) != null)
                {
                    if(CollisionDetection.checkCollision(tile, childNode))
                        return false;
                }
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
