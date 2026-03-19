package com.zipporah.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import static jdk.internal.icu.lang.UCharacter.getDirection;
/** {link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {
    Texture background;

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

    // enemy (crow dude)
    Karasu karasu;

    // Animation<TextureRegion> reversedWalkFrame;

    // Attack 1 with Blood Charge 2

    float time = 0;
    float x = 0;
    float y = 150;
    float spriteSpeed = 200.0f;
    float sprintMultiplier = 2.00f;

    // camera
    FitViewport viewport;

    // sprites
    SpriteBatch batch;

    // boolean to keep track of idle character direction
    boolean facing_right = true;

    @Override
    public void create() {
        // characters
        batch = new SpriteBatch();

        // viewport (fixed)
        viewport = new FitViewport(1280, 720);
        batch.setProjectionMatrix(viewport.getCamera().combined);

        // background
        background = new Texture("Battleground2.png");

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
        batch.setProjectionMatrix(viewport.getCamera().combined);
    }

    @Override
    public void render() {
        // Draw your application here.
        input();
        logic();
        draw();
    }

    private void input() {
        // default frame idle
        currFrame = idle.getKeyFrame(time, true);
        boolean isWalking = false;
        boolean flip = (Gdx.input.isKeyPressed(Input.Keys.A)|| Gdx.input.isKeyPressed(Input.Keys.LEFT));
        float delta = Gdx.graphics.getDeltaTime();
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

        // Sprite Attacks
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            attacking = true;
            attackTime = 0f;
        }

        // Finish Attack Animation
        if (attacking) {
            attackTime += delta;
            currFrame = attack.getKeyFrame(attackTime, false);
            if (attack.isAnimationFinished(attackTime))
                attacking = false;
        }

        // GUI FOR MENU
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
        }

//        if (!isWalking) {
//            currFrame = idle.getKeyFrame(time, true);
//        }

    }

    private void logic() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        time += Gdx.graphics.getDeltaTime();

    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();

        batch.begin();

        // draw background
        batch.draw(background, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        //enemy
        karasu.draw(batch, time);

        // draw animated character keeping in mind the characters direction
        float drawX;
        float scaleX;

        if(facing_right) {
            drawX = x;
            scaleX = 1;
        } else{
            drawX = x + 250;
            scaleX = -1;
        }

        batch.draw(currFrame, drawX, y, 0, 0, 250, 250, scaleX, 1, 0);

        batch.end();

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
}