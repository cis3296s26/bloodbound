package com.zipporah.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.zipporah.game.screens.GameScreen;

import java.util.ArrayList;

public class Player extends Sprite {
    // character animations
    public TextureRegion currFrame;

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

    float spriteSpeed = 200.0f;
    float sprintMultiplier = 2.00f;
    public float sprit_size = 200f;
    Texture attackSpriteSheet;
    Animation<TextureRegion> attack;
    public boolean attacking = false;
    float attackTime = 0f;
    float time = 0;
    public boolean facing_right = true;
    public float x = 100f;
    public float y = 65f;



    public static class Projectile {
        Texture projectileSpriteSheet;
        public Animation<TextureRegion> projectileAnimation;
        public float lifetime = 4f;
        public float animationDuration = 0f;
        float speed = 400f;
        public float x;
        public float y;
        boolean direction = true; // True - right, False - left
        public int scaleX;

        // Projectile Animation
        public Projectile(boolean facing_right, float x, float y) {
            projectileSpriteSheet = new Texture("Player/Blood_Charge_1.png");
            TextureRegion[][] projectileTemp = TextureRegion.split(projectileSpriteSheet, 64, 48);
            TextureRegion[] projectileFrames = new TextureRegion[3];
            for (int i = 0; i < 3; ++i) projectileFrames[i] = projectileTemp[0][i];
            projectileAnimation = new Animation<>(0.075f, projectileFrames);

            direction = !facing_right;
            if(direction) {
                scaleX = -1;
                this.x = x + 64;
            } else {
                scaleX = 1;
                this.x = x + 132;
            }
            this.y = 60 + y;
        }

        public void update(float delta) {
            animationDuration += delta;
            if(direction) x -= speed * delta;
            else x += speed * delta;
            lifetime -= delta;
        }
    }

    public ArrayList<Player.Projectile> projectiles = new ArrayList<>();





    float velocityY = 0f;
    float gravity = -1500f;
    float jumpAccel = 700;


    public void sprite_init() {
        idleSpriteSheet = new Texture("Player/Idle.png");
        TextureRegion[][] tmp2 = TextureRegion.split(idleSpriteSheet, 128, 128);
        TextureRegion[] idleFrames = new TextureRegion[5];
        for (int i = 0; i < 5; i++) {
            idleFrames[i] = tmp2[0][i];
        }
        idle = new Animation<>(0.1f, idleFrames);

        walkSpriteSheet = new Texture("Player/Walk.png");
        TextureRegion[][] tmp = TextureRegion.split(walkSpriteSheet, 128, 128);
        TextureRegion[] walkFrames = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            walkFrames[i] = tmp[0][i];
        }
        walk = new Animation<>(0.1f, walkFrames);

        jumpSpriteSheet = new Texture("Player/Jump.png");
        TextureRegion[][] tmp3 = TextureRegion.split(jumpSpriteSheet, 128, 128);
        TextureRegion[] jumpFrames = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            jumpFrames[i] = tmp3[0][i];
        }
        jump = new Animation<>(0.075f, jumpFrames);

        sprintSpriteSheet = new Texture("Player/Run.png");
        TextureRegion[][] tmp4 = TextureRegion.split(sprintSpriteSheet, 128, 128);
        TextureRegion[] sprintFrames = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            sprintFrames[i] = tmp4[0][i];
        }
        sprint = new Animation<>(0.125f, sprintFrames);

        attackSpriteSheet = new Texture("Player/Attack_1.png");
        TextureRegion[][] attackTmp = TextureRegion.split(attackSpriteSheet, 128, 128);
        TextureRegion[] attackFrames = new TextureRegion[6];
        for (int i = 0; i < 6; ++i)
            attackFrames[i] = attackTmp[0][i];
        attack = new Animation<>(0.075f, attackFrames);

    }

    public void input(float delta) {
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

        if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.UP) && !jumping) {
            jumping = true;
            // inc y velo
            velocityY = jumpAccel;
        }
        if(jumping) {
            currFrame = jump.getKeyFrame(time, false);
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

    public void updateSpriteAttack(float delta) {
        attackTime += delta;
        currFrame = attack.getKeyFrame(attackTime, false);
        if (attack.isAnimationFinished(attackTime)) {
            attacking = false;

            // Set projectile in motion
            projectiles.add(new Player.Projectile(facing_right, x, y));
        }
    }
}