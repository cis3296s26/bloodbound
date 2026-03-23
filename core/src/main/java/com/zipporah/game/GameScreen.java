package com.zipporah.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import org.w3c.dom.Text;

/** {link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameScreen implements Screen {

    private final ScreenManager game;

    TiledMap map;
    OrthogonalTiledMapRenderer renderer;

    // character animations
    TextureRegion currFrame;

    Texture walkSpriteSheet;
    Animation<TextureRegion> walk;

    // Animation<TextureRegion> run;

    Texture jumpSpriteSheet;
    Animation<TextureRegion> jump;
    boolean jumping = false;
    float jumptime = 0f;

    Texture idleSpriteSheet;
    Animation<TextureRegion> idle;

    Texture sprintSpriteSheet;
    Animation<TextureRegion> sprint;

    Texture attackSpriteSheet;
    Animation<TextureRegion> attack;
    boolean attacking = false;
    float attackTime = 0f;

    Texture projectileSpriteSheet;
    Animation<TextureRegion> projectile;

    // enemy (crow dude)
    Karasu karasu;

    // Animation<TextureRegion> reversedWalkFrame;

    // Attack 1 with Blood Charge 2

    float time = 0;
    float x = 100f;
    float y = 65f;
    float spriteSpeed = 200.0f;
    float sprintMultiplier = 2.00f;
    float scale = 4f;
    float sprit_size = 200f;

    // camera
    FitViewport viewport;

    // boolean to keep track of idle character direction
    boolean facing_right = true;

    public GameScreen(ScreenManager game){
        this.game = game;
    }

    @Override
    public void show() {
        // viewport (fixed)
        viewport = new FitViewport(1280, 720);
        game.batch.setProjectionMatrix(viewport.getCamera().combined);

        // render in map
        map = new TmxMapLoader().load("test2.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, scale);

        // set camera to start at far left
        OrthographicCamera cam = (OrthographicCamera) viewport.getCamera();
        cam.position.set(640, 360, 0);
        cam.update();
        game.batch.setProjectionMatrix(cam.combined);

        // idle sprite sheet
        idleSpriteSheet = new Texture("Idle.png");
        TextureRegion[][] tmp2 = TextureRegion.split(idleSpriteSheet, 128, 128);
        TextureRegion[] idleFrames = new TextureRegion[5];
        for (int i = 0; i < 5; i++) {
            idleFrames[i] = tmp2[0][i];
        }
        idle = new Animation<>(0.1f, idleFrames);

        // walk sprite sheet
        walkSpriteSheet = new Texture("Walk.png");
        TextureRegion[][] tmp = TextureRegion.split(walkSpriteSheet, 128, 128);
        TextureRegion[] walkFrames = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            walkFrames[i] = tmp[0][i];
        }
        walk = new Animation<>(0.1f, walkFrames);

        // reversed walk

        //jump spritesheet
        jumpSpriteSheet = new Texture("Jump.png");
        TextureRegion[][] tmp3 = TextureRegion.split(jumpSpriteSheet, 128, 128);
        TextureRegion[] jumpFrames = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            jumpFrames[i] = tmp3[0][i];
        }
        jump = new Animation<>(0.075f, jumpFrames);

        //sprint spritesheet
        sprintSpriteSheet = new Texture("Run.png");
        TextureRegion[][] tmp4 = TextureRegion.split(sprintSpriteSheet, 128, 128);
        TextureRegion[] sprintFrames = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            sprintFrames[i] = tmp4[0][i];
        }
        sprint = new Animation<>(0.125f, sprintFrames);

        // Sprite Attack
        attackSpriteSheet = new Texture("Attack_1.png");
        TextureRegion[][] attackTmp = TextureRegion.split(attackSpriteSheet, 128, 128);
        TextureRegion[] attackFrames = new TextureRegion[6];
        for (int i = 0; i < 6; ++i)
            attackFrames[i] = attackTmp[0][i];
        attack = new Animation<>(0.075f, attackFrames);

        // Projectile Animation
        projectileSpriteSheet = new Texture("Blood_Charge_1.png");
        TextureRegion[][] projectileTemp = TextureRegion.split(projectileSpriteSheet, 64, 48);
        TextureRegion[] projectileFrames = new TextureRegion[3];
        for (int i = 0; i < 3; ++i)
            projectileFrames[i] = projectileTemp[0][i];
        projectile = new Animation<>(0.075f, projectileFrames);

        //enemy init
        karasu = new Karasu();
        karasu.create();
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if(width <= 0 || height <= 0) return;

        // Resize your application here. The parameters represent the new window size.
        viewport.update(width, height, true);
        game.batch.setProjectionMatrix(viewport.getCamera().combined);
    }

    @Override
    public void render(float delta) {
        input(delta);
        logic(delta);
        draw(delta);
    }

    private void input(float delta) {
        // default frame idle
        currFrame = idle.getKeyFrame(time, true);
        boolean isWalking = false;
        boolean flip = (Gdx.input.isKeyPressed(Input.Keys.A)|| Gdx.input.isKeyPressed(Input.Keys.LEFT));
//        delta = Gdx.graphics.getDeltaTime();
        float spriteSpeedSprint;

        // Movement
        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)){
            spriteSpeedSprint = spriteSpeed * sprintMultiplier;
            if (Gdx.input.isKeyPressed(Input.Keys.D)|| Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                x += delta * spriteSpeedSprint;
                currFrame = sprint.getKeyFrame(time, true);
                facing_right = true;
            }
            if (flip) {
                x -= delta  * spriteSpeedSprint;
                currFrame = sprint.getKeyFrame(time, true);
                facing_right = false;
            }
        }
        else{
            spriteSpeedSprint = spriteSpeed;
            if (Gdx.input.isKeyPressed(Input.Keys.D)|| Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                x += delta * spriteSpeedSprint;
                currFrame = walk.getKeyFrame(time, true);
                isWalking = true;
                facing_right = true;
            }
            if (flip) {
                x -= delta  * spriteSpeedSprint;
                currFrame = walk.getKeyFrame(time, true);
                isWalking = true;
                facing_right = false;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
             jumping = true;
             jumptime = 0f;
        }
        if(jumping) {
            jumptime += delta;
            float prev = y;
            y += delta * spriteSpeed;
            currFrame = jump.getKeyFrame(time, true);
            y = prev;
            if(jump.isAnimationFinished((jumptime))) {
                jumping = false;
            }
        }

        // Sprite Attack
        if (!attacking && Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            attacking = true;
            attackTime = 0f;
        }



        // GUI FOR MENU
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
        }

//        if (!isWalking) {
//            currFrame = idle.getKeyFrame(time, true);
//        }

    }



    private void logic(float delta) {
        time += Gdx.graphics.getDeltaTime();

        // camera follows sprite
        OrthographicCamera cam = (OrthographicCamera) viewport.getCamera();

        // get important map values for the camera
        float mapWorldWidth  = 240 * 16 * scale;
        float mapWorldHeight = 13  * 16 * scale;
        float half_of_width = viewport.getWorldWidth()  / 2f;
        float half_of_height = viewport.getWorldHeight() / 2f;

        float targetX = x + sprit_size / 2f;
        float targetY = y + sprit_size / 2f;

        // make it so the fatherst the camera will go is in the bounds of the map
        targetX = Math.max(half_of_width, Math.min(targetX, mapWorldWidth  - half_of_width));
        targetY = Math.max(half_of_height, Math.min(targetY, mapWorldHeight - half_of_height));

        // fix problem with laggy camera
        cam.position.x += (targetX - cam.position.x) * 0.1f;
        cam.position.y += (targetY - cam.position.y) * 0.1f;
        cam.update();

        game.batch.setProjectionMatrix(cam.combined);

    }

    private void draw(float delta) {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();

        OrthographicCamera cam = (OrthographicCamera) viewport.getCamera();

        // draw the tiled map, renders at scale of 4 so tiles are 64 units each
        renderer.setView(cam);
        renderer.render();

        game.batch.begin();

        //enemy
        karasu.draw(game.batch, time);

        // draw animated character keeping in mind the characters direction
        float drawX;
        float scaleX;

        if(facing_right) {
            drawX = x;
            scaleX = 1;
        } else{
            drawX = x + sprit_size;
            scaleX = -1;
        }

        // Update / Finish Attack Animation
        if(attacking) updateSpriteAttack(delta);

        TextureRegion projectileFrame = projectile.getKeyFrame(0, true);
        game.batch.draw(projectileFrame, 100, 100, 64, 48);


        game.batch.draw(currFrame, drawX, y, 0, 0, sprit_size, sprit_size, scaleX, 1, 0);

        game.batch.end();

    }

    private void updateSpriteAttack(float delta) {
        attackTime += delta;
        currFrame = attack.getKeyFrame(attackTime, false);
        if (attack.isAnimationFinished(attackTime)) {
            attacking = false;

            // Set projectile in motion
//            currFrame = projectile.getKeyFrame(attackTime, false);


        }

        // Attack finishes when the projectile hits some object or stays longer than its specified lifetime
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.

    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void dispose() {
        // Destroy application's resources here.
    }

    @Override
    public void hide() {
    }
}
