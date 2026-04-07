package com.zipporah.game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import static com.zipporah.game.screens.GameScreen.collisionRectangles;
import static com.zipporah.game.screens.GameScreen.wallRectangles;
import com.badlogic.gdx.audio.Sound;

public class Enemy {

    TextureRegion currFrame;
    public float time = 0;
    Sound skeletonDead;


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
    int[] frameCount;   // 0 - idle, 1 - walk, 2 - attack, 3 - dead
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
    float speed = 150.0f;
    public float health = 100;

    // Disposal
    boolean removed = false;

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

    public void create() {
    }

    public void create(String path, int[] frameCount){
        removed = false;
        health = 100;
        this.path = path;
        this.frameCount = frameCount; // 0 - idle, 1 - walk, 2 - attack, 3 - dead
        innerXOffset = innerXOffsetFacingLeft;

        // Load Animations
        idle = loadAnimation("Idle", frameCount[0], 0.1f);
        walk = loadAnimation("Walk", frameCount[1], 0.1f);
        attack = loadAnimation("Attack_1", frameCount[2], 0.1f);
        death = loadAnimation("Dead", frameCount[3], 0.2f);

        skeletonDead = Gdx.audio.newSound(Gdx.files.internal("Sounds/Enemy/death_3_alex.wav"));


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
                skeletonDead.play(0.25f);
            }
            return;
        }

        // Set direction
        float dx = playerX - x;
        float dy = playerY - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        // Don't move and switch animation if near the player or can't move ahead
        float attackRange = 80f;
        float attackVerticalRange = 80f;
        float stopRange = 80f;
        float faceDeadzone = 40f;

        if (Math.abs(dx) <= stopRange) {
            if (Math.abs(dx) <= attackRange && Math.abs(dy) <= attackVerticalRange) {
                facingRight = dx > 0;
                innerXOffset = facingRight ? innerXOffsetFacingRight : innerXOffsetFacingLeft;
                if (currState != State.attack) stateTime = 0;
                currState = State.attack;
            } else currState = State.idle;
            return;
        }

        if (Math.abs(dx) > faceDeadzone) facingRight = dx > 0;
        float dirX = facingRight ? 1f : -1f;
        innerXOffset = facingRight ? innerXOffsetFacingRight : innerXOffsetFacingLeft;

        move(dirX, delta);
    }

    /// !!! AI GENERATED LOGIC <- USE TEMPORARY AND REPLACE OR ACKNOWLEDGE IN REPORT / ASK PROFESSOR !!!
    private void move(float dirX, float delta) {
        currState = State.walk;
        float stepX = speed * delta * dirX;
        float hitboxW = innerBoundaries.width;
        float hitboxH = innerBoundaries.height;
        float hitboxX = x + innerXOffset;

        onGround = false;
        velocityY += gravity * delta;
        float newY = y + velocityY * delta;

        // ceiling collision (use a thin head slice)
        if (velocityY > 0f) {
            float lowestCeiling = Float.POSITIVE_INFINITY;
            float headHeight = 2f;
            float headWidth = Math.min(10f, hitboxW);
            float headX = hitboxX + (hitboxW - headWidth) / 2f;
            Rectangle head = new Rectangle(headX, newY + hitboxH - headHeight, headWidth, headHeight);
            for (Rectangle rectangle : collisionRectangles) {
                if (head.overlaps(rectangle)) {
                    float rectBottom = rectangle.y;
                    if (rectBottom < lowestCeiling)
                        lowestCeiling = rectBottom;
                }
            }
            if (lowestCeiling != Float.POSITIVE_INFINITY) {
                newY = lowestCeiling - hitboxH;
                velocityY = 0f;
            }
        }

        // floor collision (use a thin centered feet slice to avoid stair popping)
        if (velocityY <= 0f) {
            float highestFloor = Float.NEGATIVE_INFINITY;
            float feetHeight = 2f;
            float feetWidth = Math.min(10f, hitboxW);
            float feetX = hitboxX + (hitboxW - feetWidth) / 2f;
            Rectangle feet = new Rectangle(feetX, newY, feetWidth, feetHeight);
            for (Rectangle rectangle : collisionRectangles) {
                if (feet.overlaps(rectangle)) {
                    float top = rectangle.y + rectangle.height;
                    if (top > highestFloor)
                        highestFloor = top;
                }
            }
            if (highestFloor != Float.NEGATIVE_INFINITY) {
                newY = highestFloor;
                velocityY = 0f;
                onGround = true;
            }
        }

        y = newY;
        innerBoundaries.setPosition(hitboxX, y);

        if (onGround) {
            float footX = facingRight
                    ? innerBoundaries.x + innerBoundaries.width + groundAhead
                    : innerBoundaries.x - groundAhead;
            float maxStepDown = 80f;
            float maxStepUp = 30f;
            boolean hasGroundAhead = false;
            for (Rectangle rectangle : collisionRectangles) {
                if (footX >= rectangle.x && footX <= rectangle.x + rectangle.width) {
                    float top = rectangle.y + rectangle.height;
                    if (top >= y - maxStepDown && top <= y + maxStepUp) {
                        hasGroundAhead = true;
                        break;
                    }
                }
            }
            if (!hasGroundAhead) {
                currState = State.idle;
                return;
            }
        }

        Rectangle newPosition = new Rectangle(innerBoundaries);
        newPosition.setPosition(hitboxX + stepX, y);

        for (Rectangle rectangle : wallRectangles)
            if (newPosition.overlaps(rectangle))
                return;

        x += stepX;
        innerBoundaries.setPosition(x + innerXOffset, y);
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
