package com.zipporah.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import static com.zipporah.game.screens.GameScreen.collisionRectangles;
import static com.zipporah.game.screens.GameScreen.spikeRectangles;
import static com.zipporah.game.screens.GameScreen.wallRectangles;

public class Enemy {

    protected TextureRegion currFrame;
    public float time = 0;

    public static float sfxVolume = 0.40f;
    private Sound deathSound;

    protected AnimationBundle walk;
    protected AnimationBundle idle;
    protected AnimationBundle attack;
    protected AnimationBundle death;
    protected AnimationBundle hurt;
    protected AnimationBundle jump;

    protected String path;
    protected int[] frameCount;
    public int size = 128;

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
    private static final float ATTACK_BOX_WIDTH = 80f;
    private static final float ATTACK_BOX_HEIGHT = 80f;

    protected boolean facingRight = true;

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

    public void create(String path, int[] frameCount) {
        removed = false;
        hurtActive = false;
        pointsAwarded = false;
        health = 100;
        this.path = path;
        this.frameCount = frameCount;
        innerXOffset = innerXOffsetFacingLeft;

        idle = new AnimationBundle(path + "Idle", frameCount[0], 0.1f, size, size);
        walk = new AnimationBundle(path + "Walk", frameCount[1], 0.1f, size, size);
        attack = new AnimationBundle(path + "Attack_1", frameCount[2], 0.1f, size, size);
        death = new AnimationBundle(path + "Dead", frameCount[3], 0.2f, size, size);

        setDeathSound("Sounds/Enemy/death_3_alex.wav");

        if (frameCount.length > 4 && frameCount[4] > 0) {
            hurt = new AnimationBundle(path + "Hurt", frameCount[4], 0.1f, size, size);
        }
        if (frameCount.length > 5 && frameCount[5] > 0) {
            jump = new AnimationBundle(path + "Jump", frameCount[5], 0.09f, size, size);
        }

        innerBoundaries = new Rectangle(x, y, size, size);
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
            case jump:
                if (jump != null) {
                    currFrame = jump.animation.getKeyFrame(stateTime, true);
                    if (jump.animation.isAnimationFinished(stateTime)) {
                        currState = State.idle;
                        stateTime = 0;
                    }
                } else {
                    currFrame = idle.animation.getKeyFrame(stateTime, true);
                }
                break;
            case hurt:
                if (hurt != null) {
                    currFrame = hurt.animation.getKeyFrame(stateTime, true);
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

        syncFacingOffset();
        if (facingRight) {
            drawX = x;
            scaleX = 1;
        } else {
            drawX = x + size;
            scaleX = -1;
        }

        batch.draw(currFrame, drawX, y, 0, 0, size, size, scaleX, 1, 0);
        innerBoundaries.setPosition(x + innerXOffset, y);

        if (currState == State.attack) {
            if (facingRight) {
                attackBox.set(x + innerXOffset + innerBoundaries.width, y + 20, ATTACK_BOX_WIDTH, ATTACK_BOX_HEIGHT);
            } else {
                attackBox.set(x + innerXOffset - ATTACK_BOX_WIDTH, y + 20, ATTACK_BOX_WIDTH, ATTACK_BOX_HEIGHT);
            }
        } else {
            attackBox.set(-1000, -1000, ATTACK_BOX_WIDTH, ATTACK_BOX_HEIGHT);
        }
    }

    private void updateEnemyDeath() {
        if (death.animation.isAnimationFinished(stateTime)) {
            removed = true;
            dispose();
        }
    }

    public void triggerHurt() {
        if (hurt == null || removed || currState == State.death) {
            return;
        }
        hurtActive = true;
        currState = State.hurt;
        stateTime = 0;
    }

    protected void setDeathSound(String soundPath) {
        if (deathSound != null) {
            deathSound.dispose();
        }
        deathSound = Gdx.audio.newSound(Gdx.files.internal(soundPath));
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
       - the enemy can perform planned jumps to reachable platforms ahead (including landing on lower platforms
         across a gap when a safe landing exists). */

    public void botLogic(float playerX, float playerY, float delta) {
        if (removed) return;

        float attackRange = 80;
        float attackVerticalRange = 80;
        float stopRange = 70;
        float faceDeadzone = 40f;

        tickJumpCooldown(delta);

        if (handleInactiveState(delta)) return;

        float dx = playerX - x;
        updateFacing(dx, faceDeadzone);

        stepVertical(delta);

        float dy = playerY - y;

        // Only do stop/attack logic on the ground. In-air we keep chasing so jumps can actually clear gaps.
        if (handleGroundedCombat(dx, dy, stopRange, attackRange, attackVerticalRange)) return;

        float dirX = directionX();
        if (shouldStopForHazardAhead(dirX, delta)) {
            currState = State.idle;
            return;
        }

        if (tryJumpToPlayerHeight(playerX, playerY, dirX)) {
            return;
        }

        moveHorizontal(dirX, delta);
    }

    private void tickJumpCooldown(float delta) {
        // Cooldowns tick even when hurt/dead so we don't get "stuck jumping" behavior after a state switch.
        if (jumpCooldownTimer > 0f) jumpCooldownTimer = Math.max(0f, jumpCooldownTimer - delta);
    }

    private boolean handleInactiveState(float delta) {
        if (hurtActive) {
            settleVertically(delta);
            return true;
        }

        if (health > 0) return false;

        if (currState != State.death) {
            currState = State.death;
            stateTime = 0;
            deathSound.play(sfxVolume);
        }

        // Let dead enemies settle on the ground naturally.
        settleVertically(delta);
        return true;
    }

    private void settleVertically(float delta) {
        syncFacingOffset();
        stepVertical(delta);
    }

    private void updateFacing(float dx, float deadzone) {
        if (Math.abs(dx) > deadzone) {
            facingRight = dx > 0;
        }
        syncFacingOffset();
    }

    private boolean handleGroundedCombat(float dx, float dy, float stopRange, float attackRange, float attackVerticalRange) {
        if (!onGround || Math.abs(dx) > stopRange) return false;

        if (Math.abs(dx) <= attackRange && Math.abs(dy) <= attackVerticalRange) {
            facingRight = dx > 0;
            syncFacingOffset();
            if (currState != State.attack) {
                stateTime = 0;
            }
            currState = State.attack;
        } else {
            currState = State.idle;
        }

        return true;
    }

    private boolean shouldStopForHazardAhead(float dirX, float delta) {
        if (!onGround || velocityY > 0f) {
            return false;
        }

        if (!wouldStepOnSpike(dirX, delta) && !wouldStepOffEdge(dirX, delta)) {
            return false;
        }

        return !tryPlannedJump(dirX);
    }

    private boolean tryPlannedJump(float dirX) {
        if (!jumpsEnabled || jumpCooldownTimer > 0f) {
            return false;
        }

        float plannedImpulse = planJumpImpulseToPlatform(dirX);
        if (plannedImpulse < 0f) {
            return false;
        }

        jump(plannedImpulse);
        return true;
    }

    private boolean tryJumpToPlayerHeight(float playerX, float playerY, float dirX) {
        if (!onGround || !jumpsEnabled || jumpCooldownTimer > 0f || jumpInProgress) {
            return false;
        }

        float dx = playerX - x;
        if (dx * dirX <= 0f) {
            return false;
        }

        float dy = playerY - y;
        if (dy < 24f || dy > maxJumpHeight() + 40f) {
            return false;
        }

        float plannedImpulse = planJumpImpulseTowardPlayerHeight(dirX, playerX, playerY);
        if (plannedImpulse < 0f) {
            return false;
        }

        jump(plannedImpulse);
        return true;
    }

    private void syncFacingOffset() {
        innerXOffset = facingRight ? innerXOffsetFacingRight : innerXOffsetFacingLeft;
    }

    private float directionX() {
        return facingRight ? 1f : -1f;
    }

    private void jump(float impulse) {
        if (!onGround || jumpCooldownTimer > 0f) {
            return;
        }

        // For "jump down / forward" (landing on a lower platform) we may intentionally use a smaller
        // impulse than minJumpImpulse, so clamp only to [0, jumpImpulse] here. The planner decides
        // the appropriate impulse depending on the target.
        velocityY = Math.max(0f, Math.min(jumpImpulse, impulse));
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
        return (jumpImpulse * jumpImpulse) / (2f * g);
    }

    private boolean wouldStepOffEdge(float dirX, float delta) {
        float stepX = speed * delta * dirX;
        float nextHitboxX = currentHitboxX() + stepX;
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
        float nextHitboxX = currentHitboxX() + stepX;
        return overlapsSpike(createCenteredProbe(nextHitboxX, y, innerBoundaries.width, 2f));
    }

    private float planJumpImpulseToPlatform(float dirX) {
        if (!jumpsEnabled) return -1f;

        float g = Math.abs(gravity);
        if (g <= 1f) {
            return -1f;
        }

        float hitboxW = innerBoundaries.width;
        float probeWidth = narrowProbeWidth(hitboxW);
        float takeoffFeetX = currentHitboxX() + (hitboxW - probeWidth) / 2f;

        float airSpeed = jumpAirSpeed();
        if (airSpeed <= 1f) {
            return -1f;
        }

        // Pass 1: preserve existing behavior: prefer planned jumps that use at least minJumpImpulse
        // and only target platforms slightly below the current ground.
        float planned = planJumpImpulseToPlatformInRange(
                dirX,
                takeoffFeetX,
                airSpeed,
                g,
                /*minImpulseAllowed=*/minJumpImpulse,
                /*minTop=*/y - 120f,
                /*maxTop=*/y + Math.max(30f, maxJumpHeight() + 5f));
        if (planned >= 0f) {
            return planned;
        }

        // Pass 2: "gap jump down": if there's a platform ahead but below the current ground,
        // allow a smaller impulse so we can land safely on it instead of stopping at the edge.
        float maxTargetDrop = Math.max(240f, maxJumpHeight() + 180f);
        return planJumpImpulseToPlatformInRange(
                dirX,
                takeoffFeetX,
                airSpeed,
                g,
                /*minImpulseAllowed=*/0f,
                /*minTop=*/y - maxTargetDrop,
                /*maxTop=*/y - 2f);
    }

    private float planJumpImpulseTowardPlayerHeight(float dirX, float playerX, float playerY) {
        if (!jumpsEnabled) return -1f;

        float g = Math.abs(gravity);
        if (g <= 1f) {
            return -1f;
        }

        float hitboxW = innerBoundaries.width;
        float probeWidth = narrowProbeWidth(hitboxW);
        float takeoffFeetX = currentHitboxX() + (hitboxW - probeWidth) / 2f;

        float airSpeed = jumpAirSpeed();
        if (airSpeed <= 1f) {
            return -1f;
        }

        float heightTolerance = Math.max(28f, innerBoundaries.height * 0.4f);
        return planJumpImpulseToPlatformInRange(
                dirX,
                takeoffFeetX,
                airSpeed,
                g,
                /*minImpulseAllowed=*/0f,
                /*minTop=*/playerY - heightTolerance,
                /*maxTop=*/playerY + heightTolerance,
                playerX);
    }

    private float planJumpImpulseToPlatformInRange(float dirX, float takeoffFeetX, float airSpeed,
            float g, float minImpulseAllowed, float minTop, float maxTop) {
        return planJumpImpulseToPlatformInRange(dirX, takeoffFeetX, airSpeed, g, minImpulseAllowed, minTop, maxTop,
                Float.NaN);
    }

    private float planJumpImpulseToPlatformInRange(float dirX, float takeoffFeetX, float airSpeed,
            float g, float minImpulseAllowed, float minTop, float maxTop, float preferredPlayerX) {
        float margin = 6f;

        float bestImpulse = -1f;
        float bestScore = Float.POSITIVE_INFINITY;

        for (Rectangle rectangle : collisionRectangles) {
            if (dirX > 0) {
                if (rectangle.x + rectangle.width < takeoffFeetX + 1f) continue;
            } else {
                if (rectangle.x > takeoffFeetX - 1f) continue;
            }

            if (!Float.isNaN(preferredPlayerX)) {
                float playerMargin = Math.max(48f, innerBoundaries.width);
                if (dirX > 0 && rectangle.x > preferredPlayerX + playerMargin) continue;
                if (dirX < 0 && rectangle.x + rectangle.width < preferredPlayerX - playerMargin) continue;
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

            float w = maxX - minX;
            float[] candidates = w >= 24f
                    ? new float[] {
                        minX,
                        minX + w * 0.25f,
                        minX + w * 0.50f,
                        minX + w * 0.75f,
                        maxX
                    }
                    : new float[] { minX, (minX + maxX) * 0.5f, maxX };

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
                float requiredV = (deltaY + 0.5f * g * t * t) / t;
                if (requiredV < 0f) {
                    continue;
                }

                if (requiredV < minImpulseAllowed || requiredV > jumpImpulse) {
                    continue;
                }

                float vyAtT = requiredV - g * t;
                if (vyAtT > 0f) {
                    continue;
                }

                if (wouldLandOnSpike(targetFeetX, top)) {
                    continue;
                }

                float score = dx;
                if (!Float.isNaN(preferredPlayerX)) {
                    score += Math.abs(targetFeetX - preferredPlayerX);
                }

                if (score < bestScore) {
                    bestScore = score;
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

        Rectangle feet = new Rectangle(feetX, topY, narrowProbeWidth(innerBoundaries.width), 2f);
        return overlapsSpike(feet);
    }

    private void stepVertical(float delta) {
        float hitboxW = innerBoundaries.width;
        float hitboxH = innerBoundaries.height;
        float hitboxX = currentHitboxX();

        onGround = false;
        velocityY += gravity * delta;
        float newY = y + velocityY * delta;

        if (velocityY > 0f) {
            float lowestCeiling = Float.POSITIVE_INFINITY;
            float headHeight = 2f;
            Rectangle head = createCenteredProbe(hitboxX, newY + hitboxH - headHeight, hitboxW, headHeight);
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

        if (velocityY <= 0f) {
            float highestFloor = Float.NEGATIVE_INFINITY;
            Rectangle feet = createCenteredProbe(hitboxX, newY, hitboxW, 2f);
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
                jumpInProgress = false;
            }
        }

        y = newY;
        innerBoundaries.setPosition(hitboxX, y);
    }

    private void moveHorizontal(float dirX, float delta) {
        currState = jumpInProgress ? State.jump : State.walk;
        float moveSpeed = jumpInProgress ? jumpAirSpeed() : speed;
        float stepX = moveSpeed * delta * dirX;
        float hitboxX = currentHitboxX();

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

    private float currentHitboxX() {
        return x + innerXOffset;
    }

    private float jumpAirSpeed() {
        return speed * Math.max(1f, jumpAirSpeedMultiplier);
    }

    private float narrowProbeWidth(float hitboxWidth) {
        return Math.min(10f, hitboxWidth);
    }

    private Rectangle createCenteredProbe(float hitboxX, float probeY, float hitboxWidth, float probeHeight) {
        float probeWidth = narrowProbeWidth(hitboxWidth);
        float probeX = hitboxX + (hitboxWidth - probeWidth) / 2f;
        return new Rectangle(probeX, probeY, probeWidth, probeHeight);
    }

    private boolean overlapsSpike(Rectangle probe) {
        for (Rectangle spike : spikeRectangles) {
            if (probe.overlaps(spike)) {
                return true;
            }
        }
        return false;
    }



    // Class Maintenance
    public void dispose() {
        if (idle != null && idle.texture != null) idle.texture.dispose();
        if (walk != null && walk.texture != null) walk.texture.dispose();
        if (attack != null && attack.texture != null) attack.texture.dispose();
        if (death != null && death.texture != null) death.texture.dispose();
        if (hurt != null && hurt.texture != null) hurt.texture.dispose();
        if (jump != null && jump.texture != null) jump.texture.dispose();
        if (deathSound != null) {
            deathSound.dispose();
            deathSound = null;
        }
    }

    public boolean isRemoved() {
        return removed;
    }
}
