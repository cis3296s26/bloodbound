package com.zipporah.game.screens;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.zipporah.game.Enemy;
import com.zipporah.game.Player;
import com.zipporah.game.enemies.Karasu;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Preferences;
import java.util.ArrayList;
import java.util.Iterator;
import com.badlogic.gdx.math.Vector2;
import com.zipporah.game.enemies.Skeleton;
import com.badlogic.gdx.Game;

public class BossScreen extends GameScreen {
    public BossScreen(ScreenManager game) {
        super(game);
    }

    Texture bossBackground;
    // FitViewport viewport;

    @Override
    protected void initLevel(){
        this.scale = 4f;

        map = new TmxMapLoader().load("boss.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, scale);

        //restores player health
        player.curr_health = prefs.getFloat("hp");

        bossBackground = new Texture(Gdx.files.internal("Maps/Battleground2.png"));

        enemies.clear();
        // add music here later


        enemies.add(new Karasu(20, 50, 230, 60f, 70f, 90, 150));
    }


    @Override
    protected void drawBackground() {
        // bossBackground = new Texture(Gdx.files.internal("Maps/Battleground2.png"));
        OrthographicCamera cam = (OrthographicCamera) viewport.getCamera();
        game.batch.setProjectionMatrix(cam.combined);
        game.batch.begin();
        // game.batch.draw(bossBackground, 0, 0, 20 * 16 * scale, 12 * 16 * scale);

        game.batch.draw(bossBackground,
                cam.position.x - viewport.getWorldWidth() / 2f,   // start from camera left edge
                cam.position.y - viewport.getWorldHeight() / 2f,  // start from camera bottom edge
                viewport.getWorldWidth(),
                viewport.getWorldHeight());

        game.batch.end();

    }

    @Override
    protected void logic(float delta) {
        super.logic(delta);

        OrthographicCamera cam = (OrthographicCamera) viewport.getCamera();
        float mapCenterX = (20 * 16 * scale) / 2f;  // 640
        float mapCenterY = (12 * 16 * scale) / 2f;  // 384
        cam.position.set(mapCenterX, mapCenterY, 0);
        cam.update();
        game.batch.setProjectionMatrix(cam.combined);
    }

    @Override
    public void show() {
        super.show();
        lastDoorTransitions = false;


        // change viewport

    }

    @Override
    public void dispose() {
        if (bossBackground != null) bossBackground.dispose();
        super.dispose();
    }
}
