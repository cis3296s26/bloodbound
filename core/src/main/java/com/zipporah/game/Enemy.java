package com.zipporah.game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.zipporah.game.screens.GameScreen;

public class Enemy {

    TextureRegion currFrame;
    public float time = 0;

    // Animations and States
    String path;
    int[] frameCount;
    
    private static class AnimationBundle {
        private final Texture texture;
        private final Animation<TextureRegion> animation;

        private AnimationBundle(Texture texture, Animation<TextureRegion> animation) {
            this.texture = texture;
            this.animation = animation;
        }
    }
    
    AnimationBundle walk, idle, attack, death;


    enum State {idle, walk, attack, death}
    State currState = State.idle;
    float stateTime = 0;

    // Inner Boundaries / HitBox
    public static Rectangle innerBoundaries;
    public float innerXOffset;

    // Flipping
    boolean facingRight = true;

    // Current Position
    public float x;
    public float y;

    // Characteristics
    float spriteSpeed = 150.0f;
    public static float health = 100;







    /////// //////////// /// //// // // / // / //



    boolean removed = false;




    // add gravity
    public float velocityY = 0f;
    public float gravity = -1500f;
    public boolean onGround = false;

    // hitbox idk if these sizes are right check later
    public float width = 80f;
    public float height = 110f; // had to shrink bc he as hitting ceiling

    public Rectangle enemyBox = new Rectangle();

    // check for ground
    public Rectangle ground = new Rectangle();
    float groundAhead = 10f;



    private AnimationBundle loadAnimation(String pictureName, int frames, float frameDuration) {
        Texture spriteSheet = new Texture("Enemies/" + path + "/" + pictureName + ".png");
        TextureRegion[][] split = TextureRegion.split(spriteSheet, 128, 128);
        TextureRegion[] splitFrames = new TextureRegion[frames];
        for (int i = 0; i < frames; i++) {
            splitFrames[i] = split[0][i];
        }
        Animation<TextureRegion> animation = new Animation<>(frameDuration, splitFrames);
        return new AnimationBundle(spriteSheet, animation);
    }

    public void create(String path, int[] frameCount){
        removed = false;
        health = 100;
        this.path = path;
        this.frameCount = frameCount;

        // Animations
        idle = loadAnimation("Idle", frameCount[0], 0.1f);
        walk = loadAnimation("Walk", frameCount[1], 0.1f);
        attack = loadAnimation("Attack_1", frameCount[2], 0.1f);
        death = loadAnimation("Dead", frameCount[3], 0.2f);

    }

    // follow player sprite
    public void botLogic(float playerX, float playerY, float delta) {
        if(removed) return;

        if (health <= 0) {
            if (currState != State.death) {
                currState = State.death;
                stateTime = 0;
            }
            return;
        }

        float dx = playerX - x;
        float dy = playerY - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        facingRight = dx > 0;
        float dirX = facingRight ? 1 : -1;

        velocityY += gravity * delta;
        y += velocityY * delta;

        enemyBox.set(x + 10, y, width, height);
        onGround = false;


        for (Rectangle rect : GameScreen.collisionRectangles) {
            if (enemyBox.overlaps(rect)) {
                float enemyFeetY = y;
                float platformTopY = rect.y + rect.height;

                if (velocityY <= 0 && enemyFeetY >= platformTopY - 20) {
                    y = platformTopY;
                    velocityY = 0;
                    onGround = true;
                    enemyBox.set(x + 10, y, width, height);
                    break;
                }
            }
        }

        // if far close the distance and once closee attack
        if (distance > 150f) {
            if (groundAhead(GameScreen.collisionRectangles) || !onGround) {
                currState = State.walk;
                x += dirX * spriteSpeed * delta;
            } else {
                currState = State.idle;
            }
        }
        else if (distance < 150f) {
            currState = State.attack;
        }
        else {
            currState = State.idle;
        }
        innerBoundaries.setPosition(x + innerXOffset, y);
    }

    public void draw(SpriteBatch batch, float time, float delta){
        if (removed) return;
        stateTime += delta;
        float drawX;
        float scaleX;


        // check if the logic makes sense
        switch (currState){
            case walk:
                currFrame = walk.animation.getKeyFrame(stateTime, true);
                break;

            case attack:
                currFrame = attack.animation.getKeyFrame(stateTime, true);
                break;

            case death:
                currFrame = death.animation.getKeyFrame(stateTime, false);
                updateEnemyDeath(delta);
                if (removed) return;
                break;

            default:
                currFrame = idle.animation.getKeyFrame(stateTime, true);
        }


        // correct flipping logic
        if (facingRight) {
            drawX = x;
            scaleX = 1;
            innerXOffset = 80;
        } else {
            drawX = x + 250; // idk if this is right width check
            scaleX = -1;
        }

        batch.draw(currFrame, drawX, y, 0, 0, 250, 250, scaleX, 1, 0);

        // Move Karasu's Inner Boundaries
        innerBoundaries.setPosition(x + innerXOffset, y);
    }

    // ground ahead?
    private boolean groundAhead(Array<Rectangle> floor) {
        float groundX;

        if (facingRight) {
            groundX = x + width + 5;
        } else {
            groundX = x - 25;
        }

        // idk if right check later
        float groundY = y - 100;

        ground.set(groundX, groundY, 20, 40);

        for (Rectangle rect : floor) {
            if (ground.overlaps(rect)) {
                return true;
            }
        }
        return false;
    }


    private void updateEnemyDeath(float delta) {
        currFrame = death.animation.getKeyFrame(stateTime, false);
        if (death.animation.isAnimationFinished(stateTime)) {
            removed = true;
            dispose();
        }
    }

    public void dispose() {
        if (idle != null) idle.texture.dispose();
        if (walk != null) walk.texture.dispose();
        if (attack != null) attack.texture.dispose();
        if (death != null) death.texture.dispose();
    }

    public boolean isRemoved() {
        return removed;
    }


}
