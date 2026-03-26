package com.zipporah.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
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

import java.util.ArrayList;
import java.util.Iterator;

public class GameScreen implements Screen {

    private final ScreenManager game;

    TiledMap map;
    OrthogonalTiledMapRenderer renderer;
    float scale = 4f;
    ExtendViewport viewport;
    FitViewport viewportHUD;


    Player player = new Player();


    float time = 0;

    public GameScreen(ScreenManager game) {
        this.game = game;
    }

    @Override
    public void show() {
        viewport = new ExtendViewport(1280, 720);
        viewportHUD = new FitViewport(1280, 720);
        game.batch.setProjectionMatrix(viewport.getCamera().combined);

        // render in map
        map = new TmxMapLoader().load("test2.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, scale);
        OrthographicCamera cam = (OrthographicCamera) viewport.getCamera();
        cam.position.set(640, 360, 0);
        cam.update();
        game.batch.setProjectionMatrix(cam.combined);

        player.sprite_init();
    }



    public void logic(float delta) {
        time += Gdx.graphics.getDeltaTime();

        game.timer.update();
    }


    private void draw(float delta) {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();

        OrthographicCamera cam = (OrthographicCamera) viewport.getCamera();

        // draw the tiled map, renders at scale of 4 so tiles are 64 units each
        renderer.setView(cam);
        renderer.render();

        game.batch.setProjectionMatrix(cam.combined);
        game.batch.begin();
        float drawX;
        float scaleX;

        if(player.facing_right) {
            drawX = player.x;
            scaleX = 1;
        } else{
            drawX = player.x + player.sprit_size;
            scaleX = -1;
        }

        if(player.attacking) player.updateSpriteAttack(delta);

        Iterator<Player.Projectile> projectilesIterator = player.projectiles.iterator();
        while (projectilesIterator.hasNext()) {
            Player.Projectile projectile = projectilesIterator.next();
            projectile.update(delta);
            if(projectile.lifetime <= 0) projectilesIterator.remove();
            else {
                TextureRegion projectileFrame = projectile.projectileAnimation.getKeyFrame(projectile.animationDuration, true);
                game.batch.draw(projectileFrame, projectile.x, projectile.y, 0, 0, 64, 48, projectile.scaleX, 1, 0);
            }
        }

        game.batch.draw(player.currFrame, drawX, player.y, 0, 0, player.sprit_size, player.sprit_size, scaleX, 1, 0);

        game.batch.end();

        viewportHUD.apply();
        game.batch.setProjectionMatrix(viewportHUD.getCamera().combined);

        game.batch.begin();
        game.timer.draw(game.batch);
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        // Resize your application here. The parameters represent the new window size.
        viewport.update(width, height, true);
        viewportHUD.update(width, height, true);
    }
    @Override
    public void render(float delta) {
        player.input(delta);
        logic(delta);
        draw(delta);
    }



    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}