package com.zipporah.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.zipporah.game.screens.GameScreen;

import java.util.ArrayList;

public class Player extends Sprite {
    // character animations
    public TextureRegion currFrame;

    String path = "Player/";
    int[] frameCount;

    public AnimationBundle idle;
    public AnimationBundle walk;
    public AnimationBundle jump;
    public AnimationBundle sprint;
    public AnimationBundle attack;
    public AnimationBundle hurt;
    public AnimationBundle dead;

    // healthbar
    public Texture hpForeground1 = new Texture("Player/hp/HealthBar3.png");
    public Texture hpBackground1 = new Texture("Player/hp/HealthBar7.png");
    public float curr_health = 100;
    public float max_health = 100;
    public float health_percentage = curr_health / max_health;
    public float bar_width = hpForeground1.getWidth() * health_percentage;
    public float w_scale = 2.0f;

    // animation control logic
    public boolean jumping = false;
    public float velocityY = 0f;
    public float jumpAccel = 700;

    public boolean attacking = false;
    float attackTime = 0f;

    public boolean isDead = false;
    float timeDead = 0f;

    public boolean isHurt = false;
    float timeHurt = 0f;
    public float hurtCooldown = 0f;

    public float spriteSpeed = 200.0f;
    float sprintMultiplier = 2.00f;

    public float time = 0;
    public boolean facing_right = true;
    public float x = 100f;
    public float y = 65f;

    public float sprit_size = 200f;

    // projectiles
    public static class Projectile {
        public AnimationBundle projectileAnimation;
        public float lifetime = 4f;
        public float animationDuration = 0f;
        float speed = 500f;
        public float x;
        public float y;
        boolean direction = true; // True - right, False - left
        public int scaleX;
        public float damage = 20;

        public Rectangle box;
        float boxXOffset = 5f;
        float boxYOffset = 19f;

        // Projectile Animation
        public Projectile(boolean facing_right, float x, float y) {
            projectileAnimation = new AnimationBundle("Player/Blood_Charge_1", 3, 0.075f, 64, 48);

            direction = !facing_right;
            if (direction) {
                scaleX = -1;
                this.x = x + 64;
                boxXOffset = -32;
            } else {
                scaleX = 1;
                this.x = x + 132;
            }
            this.y = 60 + y;

            box = new Rectangle(this.x + boxXOffset, this.y + boxYOffset, 27f, 10f);
        }

        public void update(float delta) {
            animationDuration += delta;
            if (direction)
                x -= speed * delta;
            else
                x += speed * delta;
            lifetime -= delta;
            box.setPosition(this.x + boxXOffset, this.y + boxYOffset);

            if (overlapsCollisions() || overlapsWall())
                lifetime -= Integer.MAX_VALUE;
        }

        public void dispose() {
            projectileAnimation.texture.dispose();
        }

        private boolean overlapsCollisions() {
            for (Rectangle r : GameScreen.collisionRectangles)
                if (r.overlaps(this.box))
                    return true;
            return false;
        }

        private boolean overlapsWall() {
            for (Rectangle r : GameScreen.wallRectangles)
                if (r.overlaps(this.box))
                    return true;
            return false;
        }
    }

    public ArrayList<Player.Projectile> projectiles = new ArrayList<>();


    public Player() {
        // Initialize Animations
        frameCount = new int[]{5, 6, 6, 6, 6, 2, 5};
        idle = new AnimationBundle(path + "Idle", frameCount[0], 0.1f);
        walk = new AnimationBundle(path + "Walk", frameCount[1], 0.1f);
        jump = new AnimationBundle(path + "Jump", frameCount[2], 0.075f);
        sprint = new AnimationBundle(path + "Run", frameCount[3], 0.125f);
        attack = new AnimationBundle(path + "Attack_1", frameCount[4], 0.075f);
        hurt = new AnimationBundle(path + "Hurt", frameCount[5], 0.15f);
        dead = new AnimationBundle(path + "Dead", frameCount[6], 0.15f);
    }

    public void input(float delta) {
        // default frame idle
        if (hurtCooldown > 0f) {
            hurtCooldown -= delta;
        }
        if (isDead) {
            return;
        }
        if (isHurt) {
            if (updateSpriteHurt(delta)) {
                isHurt = false;
                timeHurt = 0f;
            }
            return;
        }
        currFrame = idle.animation.getKeyFrame(time, true);
        boolean flip = (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT));
        float spriteSpeedSprint;

        // Movement
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
            spriteSpeedSprint = spriteSpeed * sprintMultiplier;
            if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                x += delta * spriteSpeedSprint;
                currFrame = sprint.animation.getKeyFrame(time, true);
                facing_right = true;
            }
            if (flip) {
                x -= delta * spriteSpeedSprint;
                currFrame = sprint.animation.getKeyFrame(time, true);
                facing_right = false;
            }
        } else {
            spriteSpeedSprint = spriteSpeed;
            if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                x += delta * spriteSpeedSprint;
                currFrame = walk.animation.getKeyFrame(time, true);
                facing_right = true;
            }
            if (flip) {
                x -= delta * spriteSpeedSprint;
                currFrame = walk.animation.getKeyFrame(time, true);
                facing_right = false;
            }
        }

        if ((Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
                || Gdx.input.isKeyJustPressed(Input.Keys.UP)) && !jumping) {
            jumping = true;
            // inc y velo
            velocityY = jumpAccel;
        }
        if (jumping) {
            currFrame = jump.animation.getKeyFrame(time, false);
        }

        // Sprite Attack
        if (!attacking && Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            attacking = true;
            attackTime = 0f;
        }
    }

    public void updateSpriteAttack(float delta) {
        attackTime += delta;
        currFrame = attack.animation.getKeyFrame(attackTime, false);
        if (attack.animation.isAnimationFinished(attackTime)) {
            attacking = false;

            // Set projectile in motion
            projectiles.add(new Player.Projectile(facing_right, x, y));
        }
    }

    public boolean updateSpriteDead(float delta) {
        timeDead += delta;
        currFrame = dead.animation.getKeyFrame(timeDead, false);
        // returns when animation is done so i can know when do switch from gamescreen
        // to homescreen
        return dead.animation.isAnimationFinished(timeDead);
    }

    public boolean updateSpriteHurt(float delta) {
        timeHurt += delta;
        currFrame = hurt.animation.getKeyFrame(timeHurt, false);
        return hurt.animation.isAnimationFinished(timeHurt);
    }
}