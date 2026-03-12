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

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {
    Texture background;

    // character animations
    TextureRegion currFrame;

    Texture walkSpriteSheet;
    Animation<TextureRegion> walk;

    // Animation<TextureRegion> run;

    Texture jumpSpriteSheet;
    Animation<TextureRegion> jump;

    Texture idleSpriteSheet;
    Animation<TextureRegion> idle;

    // Animation<TextureRegion> reversedWalkFrame;

    // Attack 1 with Blood Charge 2

    float time = 0;
    float x = 0;
    float y = 150;
    float spriteSpeed = 200.0f;

    // camera
    FitViewport viewport;

    // sprites
    SpriteBatch batch;

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
        boolean flip = (Gdx.input.isKeyPressed(Input.Keys.A));

        float width = currFrame.getRegionWidth();
        float height = currFrame.getRegionHeight();
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += delta * spriteSpeed;
            currFrame = walk.getKeyFrame(time, true);
            isWalking = true;
        }
        if (flip) {
            x -= delta  * spriteSpeed;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            float prev = y;
            y += delta * spriteSpeed;
            currFrame = jump.getKeyFrame(time, true);
            y = prev;
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

        // draw animated character
        batch.draw(currFrame, x, y, 250, 250);

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
