package com.zipporah.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import static com.zipporah.game.screens.GameScreen.collisionRectangles;
import static com.zipporah.game.screens.GameScreen.spikeRectangles;
import static com.zipporah.game.screens.GameScreen.wallRectangles;
import com.badlogic.gdx.audio.Sound;

public class Enemy {

    protected TextureRegion currFrame;
    public float time = 0;
    Sound enemyDead;

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
    protected AnimationBundle jump;

    protected String path;
    protected int[] frameCount;
    protected int size = 128;

    protected enum State {
        idle, walk, attack, jump, hurt, death
    }

    protected State currState = State.idle;
    protected float stateTime = 0;

    public Rectangle innerBoundaries;
    protected float innerXOffset;
    protected float innerXOffsetFacingRight;
    protected float innerXOffsetFacingLeft;

    public Rectangle attackBox = new Rectangle();
    float attackBoxWidth = 80f;
    float attackBoxHeight = 80f;

    protected boolean facingRight = true;
    
    // Bot logic and jump tuning
    // jumpImpulse is treated as the maximum allowed impulse; actual jumps may use a smaller planned impulse.
    protected float jumpImpulse = 950f;
    protected float minJumpImpulse = 550f;
    protected float jumpCooldown = 0.75f;
    protected float jumpCooldownTimer = 0f;
    protected float jumpAirSpeedMultiplier = 2.1f;
    protected boolean jumpInProgress = false;
    protected boolean jumpsEnabled = true;
    
    protected float groundAhead = 10f;
    protected float velocityY = 0f;
    protected float gravity = -1500f;
    protected boolean onGround = false;

    public float x;
    public float y;

    protected float speed = 150.0f;
    public float health = 100;

    protected boolean removed = false;
    
    protected boolean hurtActive = false;
    protected boolean pointsAwarded = false;
    


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

    // Initialization
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

        enemyDead = Gdx.audio.newSound(Gdx.files.internal("Sounds/Enemy/death_3_alex.wav"));

        if (frameCount.length > 4 && frameCount[4] > 0) hurt = loadAnimation("Hurt", frameCount[4], 0.1f);
        if (frameCount.length > 5 && frameCount[5] > 0) jump = loadAnimation("Jump", frameCount[5], 0.09f);
    }

    // Draw and Animate
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
            case jump:
                if (jump != null) {
                    currFrame = jump.animation.getKeyFrame(stateTime, true);
                    if (jump.animation.isAnimationFinished(stateTime)) {
                        currState = State.idle;
                        stateTime = 0;
                    }
                } else currFrame = idle.animation.getKeyFrame(stateTime, true);
                break;
            case hurt:
                if (hurt != null) {
                    currFrame = hurt.animation.getKeyFrame(stateTime, true);
                    if (hurt.animation.isAnimationFinished(stateTime)) {
                        hurtActive = false;
                        currState = State.idle;
                        stateTime = 0;
                    }
                } else currFrame = idle.animation.getKeyFrame(stateTime, true);
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

        if (currState == State.attack) {
            if (facingRight) {
                attackBox.set(x + innerXOffset + innerBoundaries.width, y + 20, attackBoxWidth, attackBoxHeight);
            } else {
                attackBox.set(x + innerXOffset - attackBoxWidth, y + 20, attackBoxWidth, attackBoxHeight);
            }
        } else {
            attackBox.set(-1000, -1000, attackBoxWidth, attackBoxHeight);
        }
    }

    private void updateEnemyDeath() {
        currFrame = death.animation.getKeyFrame(stateTime, false);
        if (death.animation.isAnimationFinished(stateTime)) {
            removed = true;
            dispose();
        }
    }

    // Logic
    public void triggerHurt() {
        if (hurt == null || removed || currState == State.death) {
            return;
        }
        hurtActive = true;
        currState = State.hurt;
        stateTime = 0;
    }

    public boolean shouldAwardPoints() {
        if (health <= 0 && !pointsAwarded) {
            pointsAwarded = true;
            return true;
        }
        return false;
    }



    /* BOT LOGIC:
       - the enemy can follow the player on both x- and y-axes through regular move or jump
       - the jump should be separately set in the extension class
       - the enemy can only perform a jump upwards, won't jump downwards */

    public void botLogic(float playerX, float playerY, float delta) {
        if (removed) {
            return;
        }

        float attackRange = 80;
        float attackVerticalRange = 80;
        float stopRange = 70;
        float faceDeadzone = 40f;

        // Cooldowns tick even when hurt/dead so we don't get "stuck jumping" behavior after a state switch.
        if (jumpCooldownTimer > 0f) {
            jumpCooldownTimer -= delta;
            if (jumpCooldownTimer < 0f) {
                jumpCooldownTimer = 0f;
            }
        }

        if (hurtActive) {
            // Still apply gravity/collisions so a hurt enemy doesn't freeze mid-air.
            // Keep the hitbox aligned with the current facing direction.
            innerXOffset = facingRight ? innerXOffsetFacingRight : innerXOffsetFacingLeft;
            stepVertical(delta);
            return;
        }

        if (health <= 0) {
            if (currState != State.death) {
                currState = State.death;
                stateTime = 0;
                enemyDead.play(0.25f);
            }
            // Let dead enemies settle on the ground naturally.
            innerXOffset = facingRight ? innerXOffsetFacingRight : innerXOffsetFacingLeft;
            stepVertical(delta);
            return;
        }

        float dx = playerX - x;
        if (Math.abs(dx) > faceDeadzone) {
            facingRight = dx > 0;
        }
        innerXOffset = facingRight ? innerXOffsetFacingRight : innerXOffsetFacingLeft;

        // Vertical physics should run every tick (idle/attack/walk), otherwise jumps/falls freeze on early returns.
        stepVertical(delta);

        float dy = playerY - y;

        // Only do stop/attack logic on the ground. In-air we keep chasing so jumps can actually clear gaps.
        if (onGround && Math.abs(dx) <= stopRange) {
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

        float dirX = facingRight ? 1f : -1f;
        innerXOffset = facingRight ? innerXOffsetFacingRight : innerXOffsetFacingLeft;

        // If the next step would put us on spikes, attempt a planned jump to a safe platform.
        if (onGround && velocityY <= 0f && wouldStepOnSpike(dirX, delta)) {
            if (!jumpsEnabled) {
                currState = State.idle;
                return;
            }

            float plannedImpulse = planJumpImpulseToPlatform(dirX);
            if (jumpCooldownTimer <= 0f && plannedImpulse > 0f) {
                jump(plannedImpulse);
            } else {
                currState = State.idle;
                return;
            }
        }

        // If we're about to walk off an edge, try to jump to a reachable platform ("hanging block") ahead.
        if (onGround && velocityY <= 0f && wouldStepOffEdge(dirX, delta)) {
            if (!jumpsEnabled) {
                currState = State.idle;
                return;
            }

            float plannedImpulse = planJumpImpulseToPlatform(dirX);
            if (jumpCooldownTimer <= 0f && plannedImpulse > 0f) {
                jump(plannedImpulse);
            } else {
                currState = State.idle;
                return;
            }
        }

        moveHorizontal(dirX, delta);
    }

    private void jump(float impulse) {
        if (!onGround || jumpCooldownTimer > 0f) {
            return;
        }

        velocityY = Math.max(minJumpImpulse, Math.min(jumpImpulse, impulse));
        onGround = false;
        currState = State.jump;
        jumpInProgress = true;
        jumpCooldownTimer = jumpCooldown;
    }

    private float maxJumpHeight() {
        float g = Math.abs(gravity);
        if (g <= 0f) {
            return 0f;
        }
        // h = v^2 / (2g)
        return (jumpImpulse * jumpImpulse) / (2f * g);
    }

    private boolean wouldStepOffEdge(float dirX, float delta) {
        float stepX = speed * delta * dirX;
        float nextHitboxX = (x + innerXOffset) + stepX;
        float footX = dirX > 0
                ? nextHitboxX + innerBoundaries.width + groundAhead
                : nextHitboxX - groundAhead;

        float maxStepDown = 80f;
        float maxStepUp = 30f;

        for (Rectangle rectangle : collisionRectangles) {
            if (footX >= rectangle.x && footX <= rectangle.x + rectangle.width) {
                float top = rectangle.y + rectangle.height;
                if (top >= y - maxStepDown && top <= y + maxStepUp) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean wouldStepOnSpike(float dirX, float delta) {
        if (spikeRectangles == null || spikeRectangles.size == 0) {
            return false;
        }

        float stepX = speed * delta * dirX;
        float nextHitboxX = (x + innerXOffset) + stepX;

        float hitboxW = innerBoundaries.width;
        float feetWidth = Math.min(10f, hitboxW);
        float feetHeight = 2f;
        float feetX = nextHitboxX + (hitboxW - feetWidth) / 2f;
        Rectangle feet = new Rectangle(feetX, y, feetWidth, feetHeight);

        for (Rectangle spike : spikeRectangles) {
            if (feet.overlaps(spike)) {
                return true;
            }
        }
        return false;
    }

    private float planJumpImpulseToPlatform(float dirX) {
        if (!jumpsEnabled) {
            return -1f;
        }

        // Compute the impulse needed to land on top of a platform in front while moving forward in-air.
        // We use the same "feet probe" X position that stepVertical() uses for ground contact.
        float g = Math.abs(gravity);
        if (g <= 1f) {
            return -1f;
        }

        float hitboxW = innerBoundaries.width;
        float feetWidth = Math.min(10f, hitboxW);
        float takeoffHitboxX = x + innerXOffset;
        float takeoffFeetX = takeoffHitboxX + (hitboxW - feetWidth) / 2f;

        float airSpeed = speed * Math.max(1f, jumpAirSpeedMultiplier);
        if (airSpeed <= 1f) {
            return -1f;
        }

        // Allow landing on platforms slightly below current ground (across a dip), but avoid targeting deep drops.
        float maxTargetDrop = 120f;
        float minTop = y - maxTargetDrop;
        float maxTop = y + Math.max(30f, maxJumpHeight() + 5f);

        float margin = 6f;

        float bestImpulse = -1f;
        float bestDx = Float.POSITIVE_INFINITY;

        for (Rectangle rectangle : collisionRectangles) {
            // Only consider platforms generally in the movement direction.
            if (dirX > 0) {
                if (rectangle.x + rectangle.width < takeoffFeetX + 1f) continue;
            } else {
                if (rectangle.x > takeoffFeetX - 1f) continue;
            }

            float top = rectangle.y + rectangle.height;
            if (top < minTop || top > maxTop) {
                continue;
            }

            float minX = rectangle.x + margin;
            float maxX = rectangle.x + rectangle.width - margin;
            if (minX > maxX) {
                minX = rectangle.x;
                maxX = rectangle.x + rectangle.width;
            }

            float[] candidates = new float[] { minX, (minX + maxX) * 0.5f, maxX };
            for (float targetFeetX : candidates) {
                float dxSigned = targetFeetX - takeoffFeetX;
                if (dxSigned * dirX <= 0f) {
                    continue;
                }

                float dx = Math.abs(dxSigned);
                float t = dx / airSpeed;
                if (t <= 0.01f) {
                    continue;
                }

                float deltaY = top - y;
                // y(t) = y + v*t - 0.5*g*t^2  => v = (deltaY + 0.5*g*t^2)/t
                float requiredV = (deltaY + 0.5f * g * t * t) / t;

                if (requiredV < minJumpImpulse || requiredV > jumpImpulse) {
                    continue;
                }

                // Must be descending at landing time to actually land on the top.
                float vyAtT = requiredV - g * t;
                if (vyAtT > 0f) {
                    continue;
                }

                if (wouldLandOnSpike(targetFeetX, top)) {
                    continue;
                }

                if (dx < bestDx) {
                    bestDx = dx;
                    bestImpulse = requiredV;
                }
            }
        }

        return bestImpulse;
    }

    private boolean wouldLandOnSpike(float feetX, float topY) {
        if (spikeRectangles == null || spikeRectangles.size == 0) {
            return false;
        }

        float hitboxW = innerBoundaries.width;
        float feetWidth = Math.min(10f, hitboxW);
        float feetHeight = 2f;
        Rectangle feet = new Rectangle(feetX, topY, feetWidth, feetHeight);
        for (Rectangle spike : spikeRectangles) {
            if (feet.overlaps(spike)) {
                return true;
            }
        }
        return false;
    }

    private void stepVertical(float delta) {
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
                jumpInProgress = false;
            }
        }

        y = newY;
        innerBoundaries.setPosition(hitboxX, y);
    }

    private void moveHorizontal(float dirX, float delta) {
        currState = State.walk;
        if(jumpInProgress) currState = State.jump;
        float moveSpeed = jumpInProgress ? speed * Math.max(1f, jumpAirSpeedMultiplier) : speed;
        float stepX = moveSpeed * delta * dirX;
        float hitboxX = x + innerXOffset;

        // Don't let grounded enemies casually walk off ledges. While in-air (jumping/falling) allow movement.
        if (onGround && velocityY <= 0f && wouldStepOffEdge(dirX, delta)) {
            currState = State.idle;
            return;
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



    // Class Maintenance
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
}
