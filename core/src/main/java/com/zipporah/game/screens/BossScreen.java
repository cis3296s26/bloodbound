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

    @Override
    public void initLevel(){
        this.scale = 4f;

        map = new TmxMapLoader().load("boss.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, scale);

        //restores player health
        player.curr_health = prefs.getFloat("hp");

        // add music here later
    }

    @Override
    public void show() {
        super.show();
        lastDoorTransitions = false; // boss room has no exit door transition

        enemies.clear();

        // add karasu scaled up
    }

    @Override
    protected void draw(float delta) {
        bossBackground = new Texture(Gdx.files.internal("Maps/Battleground2.png"));
        super.draw(delta);
        game.batch.begin();
        game.batch.draw(bossBackground, 0, 0, 20 * 16 * scale, 12 * 16 * scale);
        game.batch.end();

        super.draw(delta);
    }
}
