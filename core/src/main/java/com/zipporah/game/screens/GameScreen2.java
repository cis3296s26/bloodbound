package com.zipporah.game.screens;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.zipporah.game.Enemy;
import com.zipporah.game.Player;
import com.zipporah.game.enemies.Skeleton;

import java.util.Iterator;

public class GameScreen2 extends GameScreen {
    public GameScreen2(ScreenManager game) {
        super(game);
    }

    // potion
    Texture potionTexture;
    Vector2 potionPosition = new Vector2();
    boolean potionSipped = false;


    // music
    Music music2;

    // sounds
    Sound drink;

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
        getPotionPosition();

        //music
        music2 = Gdx.audio.newMusic(Gdx.files.internal("Music/lightyeartraxx-kim-lightyear-kings-and-dragons-275238.mp3"));
        music2.setLooping(true);
        music2.setVolume(0.30f);
        music2.play();


        //load user
        player.x = 100;
        player.y = 128;
        OrthographicCamera cam = (OrthographicCamera) viewport.getCamera();
        cam.position.set(player.x + player.sprit_size / 2f, player.y + player.sprit_size / 2f, 0);
        cam.update();
        game.batch.setProjectionMatrix(cam.combined);

        // add music
    }

    private void getPotionPosition() {
        MapLayer layer1 = map.getLayers().get("potion");
        for (MapObject obj : layer1.getObjects()) {
            if (obj instanceof RectangleMapObject) {
                Rectangle r = ((RectangleMapObject) obj).getRectangle();
                potionPosition.set(r.x * scale, r.y * scale);
            }
        }
    }

    @Override
    protected void input(float delta) {
        // this will keep the movements and chest interact
        super.input(delta);



        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (!lastDoorOpen && haveChest2key) {
                float distToDoor2 = Vector2.dst(player.x, player.y, lastDoorPosition.x, lastDoorPosition.y);
                if (distToDoor2 < doorInteractionRange) {
                    lastDoorOpen = true;
                    wallRectangles.removeValue(lastDoorRect, true);
                    doorOpenTime = 0.1f;
                    keyCount--;
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {

            // final door of level 2 sends player back to homescreen
            // ** ONLY TO TEST RANKINGS, CHANGE THIS TO GO TO BOSS LEVEL
            if (!finalDoorOpen) {
                float distToFinal = Vector2.dst(player.x, player.y, finalDoorPosition.x, finalDoorPosition.y);
                if (distToFinal < doorInteractionRange) {
                    finalDoorOpen = true;
                    wallRectangles.removeValue(finalDoorRect, true);
                    doorOpenTime = 0.1f;
                    keyCount--;
                    if (music1 != null) {
                        music1.stop();
                    }

                    game.timer.stop(); // we need to add this method to CurrentRun
                    game.playerData.saveRun(game.timer.getElapsedTime(), (int) game.timer.getPoints());

                    game.setScreen(new HomeScreen(game));
                }
            }
        }

        // potion interaction, find a drink sound
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (!potionSipped) {
                float dist1 = Vector2.dst(player.x, player.y, potionPosition.x, potionPosition.y);
                if (dist1 < 150f) {
                    drink.play(40);
                    potionSipped = true;

                    // player will enter boss battle wiht full health idk if this actually works tho
                    player.curr_health = player.max_health;
                    // for recalculation
                    player.health_percentage = player.curr_health / player.max_health;
                }
            }
        }
        // level 2 specific interactions (probably wont be any we shall see)
    }

    @Override
    protected void draw(float delta) {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();

        OrthographicCamera cam = (OrthographicCamera) viewport.getCamera();

        // draw the tiled map, renders at scale of 4 so tiles are 64 units each
        renderer.setView(cam);
        renderer.render();

        game.batch.setProjectionMatrix(cam.combined);

        game.batch.begin();

        // draw chests
        float chestWidth = 16 * 2 * scale;
        float chestHeight = 32 * 2 * scale;
        game.batch.draw(chest1Open ? openChestTexture : closeChestTexture, chest1Position.x, chest1Position.y,
                chestWidth, chestHeight);
        game.batch.draw(chest2Open ? openChestTexture : closeChestTexture, chest2Position.x, chest2Position.y,
                chestWidth, chestHeight);

        // draw doors
        float doorWidth = 16 * 2 * scale;
        float doorHeight = 32 * 2 * scale;
        game.batch.draw(firstDoorOpen ? openDoor2Texture : closeDoor2Texture, firstDoorPosition.x, firstDoorPosition.y,
                doorWidth, doorHeight);
        game.batch.draw(lastDoorOpen ? openDoor2Texture : closeDoor2Texture, lastDoorPosition.x, lastDoorPosition.y,
                doorWidth, doorHeight);

        // draw  potion
        if (!potionSipped) {
            float potionWidth = 20;
            float potionHeight = 32;
            game.batch.draw(potionTexture, potionPosition.x, potionPosition.y, potionWidth, potionHeight);
        }

        for (Enemy enemy : enemies) {
            if (enemy != null && !enemy.isRemoved()) {
                enemy.draw(game.batch, enemy.time, delta);
            }
        }

        float drawX;
        float scaleX;

        if (player.facing_right) {
            drawX = player.x;
            scaleX = 1;
        } else {
            drawX = player.x + player.sprit_size;
            scaleX = -1;
        }

        if (player.attacking)
            player.updateSpriteAttack(delta);

        Iterator<Player.Projectile> projectilesIterator = player.projectiles.iterator();
        while (projectilesIterator.hasNext()) {
            Player.Projectile projectile = projectilesIterator.next();
            projectile.update(delta);
            if (projectile.lifetime <= 0) {
                projectile.dispose();
                projectilesIterator.remove();
            } else {
                TextureRegion projectileFrame = projectile.projectileAnimation.animation.getKeyFrame(projectile.animationDuration,
                        true);
                game.batch.draw(projectileFrame, projectile.x, projectile.y, 0, 0, 64, 48, projectile.scaleX, 1, 0);
            }
        }

        game.batch.draw(player.currFrame, drawX, player.y, 0, 0, player.sprit_size, player.sprit_size, scaleX, 1, 0);

        game.batch.end();

        viewportHUD.apply();
        game.batch.setProjectionMatrix(viewportHUD.getCamera().combined);
        player.render_health();

        game.batch.begin();
        game.timer.draw(game.batch);
        game.batch.draw(player.hpBackground1, 10, 700, player.hpBackground1.getWidth() * player.w_scale, player.hpBackground1.getHeight());
        game.batch.draw(player.hpForeground1, 11, 700, player.bar_width * player.w_scale, player.hpForeground1.getHeight());
        game.batch.draw(keyTexture, 882, 672, 64f, 64f);
        game.timer.font.draw(game.batch, String.format("%dx", keyCount), 946, 700);
        game.batch.draw(homeButtonTexture, homeButtonX, homeButtonY, homeButtonWidth, homeButtonHeight);
        game.batch.end();

        // Test Projectile and Karasu Hitboxes with these
        // shapeRenderer.setProjectionMatrix(cam.combined);
        // shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        // for (Player.Projectile projectile : player.projectiles) {
        // shapeRenderer.rect(projectile.box.x, projectile.box.y, projectile.box.width,
        // projectile.box.height);
        // }
        // shapeRenderer.end();

    }

    @Override
    public void show() {
        super.show();
        lastDoorTransitions = false;

        // clear old enemies
        enemies.clear();

        for(int i = 0; i < dmg; i++) {
            player.curr_health -= 10f * game.getDamageMultiplier();
        }
        // add level 2 enemies
        enemies.add(new Skeleton(10, 400, 200, 60f, 70f, 62, 120));
        enemies.add(new Skeleton(1300, 200, 200, 60f, 70f, 62, 120));
        enemies.add(new Skeleton(3000, 200, 200, 60f, 70f, 62, 120));

        // potion texture
        potionTexture = new Texture(Gdx.files.internal("Maps/potion.png"));

        // Sounds
        drink = Gdx.audio.newSound(Gdx.files.internal("Sounds/Interactables/drink_slurp.wav"));


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
    public void hide() {
        if (music2 != null) {
            music2.stop();
        }
    }
    public void dispose() {
        if (music2 != null) {
            music2.dispose();
        }
    }
}
