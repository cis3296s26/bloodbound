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

    protected TextureRegion currFrame;
    public float time = 0;
    Sound skeletonDead;

    protected static class AnimationBundle {
        private final Texture texture;
        private final Animation<TextureRegion> animation;

        private AnimationBundle(Texture texture, Animation<TextureRegion> animation) {
            this.texture = texture;
            this.animation = animation;
        }
    }

    protected AnimationBundle walk;
    protected AnimationBundle idle;
    protected AnimationBundle attack;
    protected AnimationBundle death;
    protected AnimationBundle hurt;
    protected String path;
    protected int[] frameCount;
    public int size = 128;

    protected enum State {
        idle, walk, attack, hurt, death
    }

    protected State currState = State.idle;
    protected float stateTime = 0;

    public Rectangle innerBoundaries;
    public float innerXOffset;
    public float innerXOffsetFacingRight;
    public float innerXOffsetFacingLeft;

    protected boolean facingRight = true;

    public float velocityY = 0f;
    public float gravity = -1500f;
    public boolean onGround = false;

    public float x;
    public float y;

    protected float speed = 150.0f;
    public float health = 100;

    protected boolean removed = false;
    protected boolean hurtActive = false;

    protected boolean pointsAwarded = false;

    public Rectangle ground = new Rectangle();
    protected float groundAhead = 10f;

    protected AnimationBundle loadAnimation(String pictureName, int frames, float frameDuration) {
        Texture spriteSheet = new Texture("Enemies/" + path + "/" + pictureName + ".png");
        TextureRegion[][] split = TextureRegion.split(spriteSheet, 128, 128);
        TextureRegion[] splitFrames = new TextureRegion[frames];
        for (int i = 0; i < frames; i++) {
            splitFrames[i] = split[0][i];
        }
        Animation<TextureRegion> animation = new Animation<>(frameDuration, splitFrames);
        return new AnimationBundle(spriteSheet, animation);
    }

    public void create() {
    }

    public void create(String path, int[] frameCount) {
        removed = false;
        hurtActive = false;
        pointsAwarded = false;
        health = 100;
        this.path = path;
        this.frameCount = frameCount;
        innerXOffset = innerXOffsetFacingLeft;

        idle = loadAnimation("Idle", frameCount[0], 0.1f);
        walk = loadAnimation("Walk", frameCount[1], 0.1f);
        attack = loadAnimation("Attack_1", frameCount[2], 0.1f);
        death = loadAnimation("Dead", frameCount[3], 0.2f);

        skeletonDead = Gdx.audio.newSound(Gdx.files.internal("Sounds/Enemy/death_3_alex.wav"));

        if (frameCount.length > 4 && frameCount[4] > 0) {
            hurt = loadAnimation("Hurt", frameCount[4], 0.1f);
        }
    }

    public void draw(SpriteBatch batch, float time, float delta) {
        if (removed) {
            return;
        }

        stateTime += delta;
        float drawX;
        float scaleX;

        switch (currState) {
            case walk:
                currFrame = walk.animation.getKeyFrame(stateTime, true);
                break;
            case attack:
                currFrame = attack.animation.getKeyFrame(stateTime, true);
                break;
            case hurt:
                if (hurt != null) {
                    currFrame = hurt.animation.getKeyFrame(stateTime, false);
                    if (hurt.animation.isAnimationFinished(stateTime)) {
                        hurtActive = false;
                        currState = State.idle;
                        stateTime = 0;
                    }
                } else {
                    currFrame = idle.animation.getKeyFrame(stateTime, true);
                }
                break;
            case death:
                currFrame = death.animation.getKeyFrame(stateTime, false);
                updateEnemyDeath();
                if (removed) {
                    return;
                }
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
        innerBoundaries.setPosition(x + innerXOffset, y);
    }

    private void updateEnemyDeath() {
        currFrame = death.animation.getKeyFrame(stateTime, false);
        if (death.animation.isAnimationFinished(stateTime)) {
            removed = true;
            dispose();
        }
    }

    public void botLogic(float playerX, float playerY, float delta) {
        if (removed) {
            return;
        }

        if (hurtActive) {
            return;
        }

        if (health <= 0) {
            if (currState != State.death) {
                currState = State.death;
                stateTime = 0;
                skeletonDead.play(0.25f);
            }
            return;
        }

        float dx = playerX - x;
        float dy = playerY - y;

        float attackRange = 80f;
        float attackVerticalRange = 80f;
        float stopRange = 80f;
        float faceDeadzone = 40f;

        if (Math.abs(dx) <= stopRange) {
            if (Math.abs(dx) <= attackRange && Math.abs(dy) <= attackVerticalRange) {
                facingRight = dx > 0;
                innerXOffset = facingRight ? innerXOffsetFacingRight : innerXOffsetFacingLeft;
                if (currState != State.attack) {
                    stateTime = 0;
                }
                currState = State.attack;
            } else {
                currState = State.idle;
            }
            return;
        }

        if (Math.abs(dx) > faceDeadzone) {
            facingRight = dx > 0;
        }

        float dirX = facingRight ? 1f : -1f;
        innerXOffset = facingRight ? innerXOffsetFacingRight : innerXOffsetFacingLeft;

        move(dirX, delta);
    }

    private void move(float dirX, float delta) {
        currState = State.walk;
        float stepX = speed * delta * dirX;
        float hitboxW = innerBoundaries.width;
        float hitboxH = innerBoundaries.height;
        float hitboxX = x + innerXOffset;

        onGround = false;
        velocityY += gravity * delta;
        float newY = y + velocityY * delta;

        if (velocityY > 0f) {
            float lowestCeiling = Float.POSITIVE_INFINITY;
            float headHeight = 2f;
            float headWidth = Math.min(10f, hitboxW);
            float headX = hitboxX + (hitboxW - headWidth) / 2f;
            Rectangle head = new Rectangle(headX, newY + hitboxH - headHeight, headWidth, headHeight);
            for (Rectangle rectangle : collisionRectangles) {
                if (head.overlaps(rectangle)) {
                    float rectBottom = rectangle.y;
                    if (rectBottom < lowestCeiling) {
                        lowestCeiling = rectBottom;
                    }
                }
            }
            if (lowestCeiling != Float.POSITIVE_INFINITY) {
                newY = lowestCeiling - hitboxH;
                velocityY = 0f;
            }
        }

        if (velocityY <= 0f) {
            float highestFloor = Float.NEGATIVE_INFINITY;
            float feetHeight = 2f;
            float feetWidth = Math.min(10f, hitboxW);
            float feetX = hitboxX + (hitboxW - feetWidth) / 2f;
            Rectangle feet = new Rectangle(feetX, newY, feetWidth, feetHeight);
            for (Rectangle rectangle : collisionRectangles) {
                if (feet.overlaps(rectangle)) {
                    float top = rectangle.y + rectangle.height;
                    if (top > highestFloor) {
                        highestFloor = top;
                    }
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

        for (Rectangle rectangle : wallRectangles) {
            if (newPosition.overlaps(rectangle)) {
                return;
            }
        }

        x += stepX;
        innerBoundaries.setPosition(x + innerXOffset, y);
    }

    public void triggerHurt() {
        if (hurt == null || removed || currState == State.death) {
            return;
        }
        hurtActive = true;
        currState = State.hurt;
        stateTime = 0;
    }

    public void dispose() {
        if (idle != null) {
            idle.texture.dispose();
        }
        if (walk != null) {
            walk.texture.dispose();
        }
        if (attack != null) {
            attack.texture.dispose();
        }
        if (death != null) {
            death.texture.dispose();
        }
        if (hurt != null) {
            hurt.texture.dispose();
        }
    }

    public boolean isRemoved() {
        return removed;
    }

    public boolean isAttacking() {
        return currState == State.attack;
    }

    public boolean shouldAwardPoints() {
        if (health <= 0 && !pointsAwarded) {
            pointsAwarded = true;
            return true;
        }
        return false;
    }
}
