package com.zipporah.game.screens;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class GameScreen2 extends GameScreen {
    public GameScreen2(ScreenManager game) {
        super(game);
    }

//    TiledMap map;
//    OrthogonalTiledMapRenderer renderer;
//    OrthographicCamera camera;

    // game screen 2 will have its own init level
    @Override
    protected void initLevel() {
        this.scale = 2f;

        // load second map
        map = new TmxMapLoader().load("level_2.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, scale);


        //load user
        player.x = 100;
        player.y = 128;

        OrthographicCamera cam = (OrthographicCamera) viewport.getCamera();
        cam.position.set(player.x + player.sprit_size / 2f, player.y + player.sprit_size / 2f, 0);
        cam.update();
        game.batch.setProjectionMatrix(cam.combined);


        // add music
    }

    @Override
    protected void input(float delta) {
        // this will keep the movements and chest interact
        super.input(delta);

        // level 2 specific interactions (probably wont be any we shall see)
    }


    @Override
    public void show() {
        super.show();

        // clear old enemies
        enemies.clear();

        // add level 2 enemies

    }
// let render be handled in game screen 1
//    @Override
//    public void render(float delta) {
//        ScreenUtils.clear(0, 0, 0, 1);
//
//        camera.update();
//        renderer.setView(camera);
//        renderer.render();
//    }


//    @Override public void resize(int width, int height) {}
//    @Override public void pause() {}
//    @Override public void resume() {}
//    @Override public void hide() {}
//    @Override public void dispose() {}
}
