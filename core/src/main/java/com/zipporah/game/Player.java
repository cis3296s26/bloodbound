package com.zipporah.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;

public class Player extends Sprite {

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

    float time = 0;
    float x = 100f;
    float y = 65f;
    float spriteSpeed = 200.0f;
    float sprintMultiplier = 2.00f;
    float scale = 4f;
    float sprit_size = 200f;


    public static class Projectile {
        Texture projectileSpriteSheet;
        Animation<TextureRegion> projectileAnimation;
        float lifetime = 4f;
        float animationDuration = 0f;
        float speed = 400f;
        float x, y;
        boolean direction = true; // True - right, False - left
        int scaleX;
        boolean facing_right = true;

        // Projectile Animation
        public Projectile(boolean facing_right, float x, float y) {
            projectileSpriteSheet = new Texture("Blood_Charge_1.png");
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

    ArrayList<GameScreen.Projectile> projectiles = new ArrayList<>();

    public void PlayerSprites() {
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
    }

    public void input(float delta) {

    }

}
