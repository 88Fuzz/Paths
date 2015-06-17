package com.paths.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.paths.constants.TextureConstants;
import com.paths.drawable.FuckMe;
import com.paths.drawable.MyTexture;
import com.paths.drawable.SceneNode;
import com.paths.utils.GameStats;
import com.paths.utils.GraphicsUtils;

public class WorldRenderer implements Disposable
{
    private static final float MIN_CAMERA_ZOOM = 1;
    private OrthographicCamera camera;
    private OrthographicCamera cameraGUI;
    private SpriteBatch batch;
    private BitmapFont font;
    private SceneNode map;
    private GameStats stats;
//    private WorldController worldController;

    public WorldRenderer(SceneNode map, float cameraZoom, GameStats stats)
    {
        font = new BitmapFont(Gdx.files.internal("images/arial-15.fnt"), true);
        font.setScale(1.25f);
        font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
        font.setColor(1, 1, 1, 1);
        init(map, cameraZoom, stats);
    }

    private void init(SceneNode map, float cameraZoom, GameStats stats)
    {
        batch = new SpriteBatch();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0 + Gdx.graphics.getWidth()/2, 0 + Gdx.graphics.getHeight()/2, 0);
        camera.update();
        cameraGUI = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        cameraGUI.position.set(0, 0, 0);
        cameraGUI.position.set(0 + Gdx.graphics.getWidth()/2, 0 + Gdx.graphics.getHeight()/2, 0);
        cameraGUI.setToOrtho(true);
        cameraGUI.update();
        this.map = map;
        this.stats = stats;
        zoom(cameraZoom);
    }

    public void render()
    {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderWorld(batch);
        renderGui(batch);
    }

    private void renderWorld(SpriteBatch batch)
    {
//        worldController.cameraHelper.applyTo(camera);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        map.drawTree(batch);

//        worldController.level.render(batch);
        batch.end();
    }
    
    public void moveCamera(Vector2 vector)
    {
		camera.position.x += vector.x;
		camera.position.y += vector.y;
		camera.update();
    }
    
    public void zoom(float zoom)
    {
        float tmpZoom = camera.zoom + zoom;
        camera.zoom = Math.max(MIN_CAMERA_ZOOM, tmpZoom);
        camera.update();
    }
    
    public float getCameraZoom()
    {
        return camera.zoom;
    }
    
    public Vector2 getCameraPosition()
    {
//        Vector2 tmpVector = new Vector2();
//        System.out.println("width " + Gdx.graphics.getWidth() + " height " + Gdx.graphics.getHeight());
//        float tmp = camera.position.x / camera.zoom;
//        tmp = tmp - (Gdx.graphics.getWidth()/2);
//        tmpVector.x = tmp;
//        tmp = camera.position.y = camera.zoom;
//        tmp = tmp - (Gdx.graphics.getHeight()/2);
//        tmpVector.y = tmp;

        Vector2 tmpVector = new Vector2(camera.position.x/camera.zoom - Gdx.graphics.getWidth()/2, camera.position.y/camera.zoom - Gdx.graphics.getHeight()/2);
        return tmpVector;
    }

    private void renderGui(SpriteBatch batch)
    {
        batch.setProjectionMatrix(cameraGUI.combined);
        batch.begin();
        renderGuiFpsCounter(batch);
        renserGameStats(batch);

        /*
        // draw collected gold coins icon + text (anchored to top left edge)
        renderGuiScore(batch);
        // draw collected feather icon (anchored to top left edge)
        renderGuiFeatherPowerup(batch);
        // draw extra lives icon + text (anchored to top right edge)
        renderGuiExtraLive(batch);
        // draw FPS text (anchored to bottom right edge)
        renderGuiFpsCounter(batch);
        // draw game over text
        renderGuiGameOverMessage(batch);
        */

        batch.end();
    }
    
    /*private void renderGuiScore(SpriteBatch batch)
    {
        float x = -15;
        float y = -15;
        batch.draw(Assets.instance.goldCoin.goldCoin, x, y, 50, 50, 100, 100, 0.35f, -0.35f, 0);
        Assets.instance.fonts.defaultBig.draw(batch, "" + worldController.score, x + 75, y + 37);
    }

    private void renderGuiFeatherPowerup(SpriteBatch batch)
    {
        float x = -15;
        float y = 30;
        float timeLeftFeatherPowerup = worldController.level.bunnyHead.timeLeftFeatherPowerup;
        if (timeLeftFeatherPowerup > 0) {
            // Start icon fade in/out if the left power-up time
            // is less than 4 seconds. The fade interval is set
            // to 5 changes per second.
            if (timeLeftFeatherPowerup < 4) {
                if (((int)(timeLeftFeatherPowerup * 5) % 2) != 0) {
                    batch.setColor(1, 1, 1, 0.5f);
                }
            }
            batch.draw(Assets.instance.feather.feather, x, y, 50, 50, 100, 100, 0.35f, -0.35f, 0);
            batch.setColor(1, 1, 1, 1);
            Assets.instance.fonts.defaultSmall.draw(batch, "" + (int)timeLeftFeatherPowerup, x + 60, y + 57);
        }
    }

    private void renderGuiExtraLive(SpriteBatch batch)
    {
        float x = cameraGUI.viewportWidth - 50 - Constants.LIVES_START * 50;
        float y = -15;
        for (int i = 0; i < Constants.LIVES_START; i++) {
            if (worldController.lives <= i) batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
            batch.draw(Assets.instance.bunny.head, x + i * 50, y, 50, 50, 120, 100, 0.35f, -0.35f, 0);
            batch.setColor(1, 1, 1, 1);
        }
    }
    */

    private void renderGuiFpsCounter(SpriteBatch batch)
    {
        float x = camera.viewportWidth - 75;
        float y = camera.viewportHeight - 25;
        int fps = Gdx.graphics.getFramesPerSecond();

        font.draw(batch, "FPS: " + fps, x, y);
    }

    private void renserGameStats(SpriteBatch batch)
    {
        //TODO make points and crumbs a picture
        String points = "Points: ";
        String crumbs = "Crumbs: ";

        float x = 3;
        float y = 3;
        
        font.draw(batch, points + (int)stats.getPoints(), x, y);
        x = camera.viewportWidth - 120;
        font.draw(batch, crumbs + (int)stats.getCrumbs(), x, y);
    }

    /*
    private void renderGuiGameOverMessage(SpriteBatch batch)
    {
        float x = cameraGUI.viewportWidth / 2;
        float y = cameraGUI.viewportHeight / 2;
        if (worldController.isGameOver()) {
            BitmapFont fontGameOver = Assets.instance.fonts.defaultBig;
            fontGameOver.setColor(1, 0.75f, 0.25f, 1);
            fontGameOver.drawMultiLine(batch, "GAME OVER", x, y, 1, BitmapFont.HAlignment.CENTER);
            fontGameOver.setColor(1, 1, 1, 1);
        }
    }

    public void resize(int width, int height)
    {
        camera.viewportWidth = (Constants.VIEWPORT_HEIGHT / (float)height) * (float)width;
        camera.update();
        cameraGUI.viewportHeight = Constants.VIEWPORT_GUI_HEIGHT;
        cameraGUI.viewportWidth = (Constants.VIEWPORT_GUI_HEIGHT / (float)height) * (float)width;
        cameraGUI.position.set(cameraGUI.viewportWidth / 2, cameraGUI.viewportHeight / 2, 0);
        cameraGUI.update();
    }

    */
    @Override
    public void dispose()
    {
        batch.dispose();
    }

}