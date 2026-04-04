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

    // Animations
    private static class AnimationBundle {
        private final Texture texture;
        private final Animation<TextureRegion> animation;

        private AnimationBundle(Texture texture, Animation<TextureRegion> animation) {
            this.texture = texture;
            this.animation = animation;
        }
    }
    
    AnimationBundle walk, idle, attack, death;
    String path;
    int[] frameCount;
    public int size = 128;

    // States
    enum State {idle, walk, attack, death}
    State currState = State.idle;
    float stateTime = 0;

    // Inner Boundaries / HitBox
    public Rectangle innerBoundaries;
    public float innerXOffset;
    public float innerXOffsetFacingRight;
    public float innerXOffsetFacingLeft;

    // Flipping
    boolean facingRight = true;

    // Gravity Simulation
    public float velocityY = 0f;
    public float gravity = -1500f;
    public boolean onGround = false;

    // Current Position
    public float x;
    public float y;

    // Characteristics
    float speed = 100.0f;
    public float health = 100;

    // Disposal
    boolean removed = false;







    /////// //////////// /// //// // // / // / //








    // hitbox idk if these sizes are right check later
//    public float width = 80f;
//    public float height = 110f;
//    public Rectangle enemyBox = new Rectangle();

    // check for ground
    public Rectangle ground = new Rectangle();
    float groundAhead = 10f;













    // Load Animations
    private AnimationBundle loadAnimation(String pictureName, int frames, float frameDuration) {
        Texture spriteSheet = new Texture("Enemies/" + path + "/" + pictureName + ".png");
        TextureRegion[][] split = TextureRegion.split(spriteSheet, 128, 128);
        TextureRegion[] splitFrames = new TextureRegion[frames];
        for (int i = 0; i < frames; i++)
            splitFrames[i] = split[0][i];
        Animation<TextureRegion> animation = new Animation<>(frameDuration, splitFrames);
        return new AnimationBundle(spriteSheet, animation);
    }

    public void create(String path, int[] frameCount){
        removed = false;
        health = 100;
        this.path = path;
        this.frameCount = frameCount;
        innerXOffset = innerXOffsetFacingLeft;

        // Load Animations
        idle = loadAnimation("Idle", frameCount[0], 0.1f);
        walk = loadAnimation("Walk", frameCount[1], 0.1f);
        attack = loadAnimation("Attack_1", frameCount[2], 0.1f);
        death = loadAnimation("Dead", frameCount[3], 0.2f);

    }

    public void draw(SpriteBatch batch, float time, float delta){
        if (removed) return;
        stateTime += delta;
        float drawX;
        float scaleX;

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

        if (facingRight) {
            drawX = x;
            scaleX = 1;
            innerXOffset = innerXOffsetFacingRight;
        } else {
            drawX = x + size;
            scaleX = -1;
            innerXOffset = innerXOffsetFacingLeft;
        }

        batch.draw(currFrame, drawX, y, 0, 0, size, size, scaleX, 1, 0);

        // Move Inner Boundaries
        innerBoundaries.setPosition(x + innerXOffset, y);
    }

    private void updateEnemyDeath(float delta) {
        currFrame = death.animation.getKeyFrame(stateTime, false);
        if (death.animation.isAnimationFinished(stateTime)) {
            removed = true;
            dispose();
        }
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
        float dirX = facingRight ? 1f : -1f;

        velocityY += gravity * delta;
        y += velocityY * delta;

        onGround = false;
        innerBoundaries.setPosition(x + innerXOffset, y);


        for (Rectangle rect : GameScreen.collisionRectangles) {
            if (innerBoundaries.overlaps(rect)) {
                float enemyFeetY = y;
                float platformTopY = rect.y + rect.height;

                if (velocityY <= 0 && enemyFeetY >= platformTopY - 20) {
                    y = platformTopY;
                    velocityY = 0;
                    onGround = true;
                    innerBoundaries.setPosition(x + innerXOffset, y);
                    break;
                }
            }
        }

        // if far close the distance and once close attack
        if (distance > 150f) {
            if (groundAhead(GameScreen.collisionRectangles) || !onGround) {
                currState = State.walk;
                x += dirX * speed * delta;
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


    // ground ahead?
    private boolean groundAhead(Array<Rectangle> floor) {
        float groundX;

        if (facingRight) {
            groundX = x + innerBoundaries.width + 5;
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
