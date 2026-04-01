package com.zipporah.game.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;


public class  Karasu {
    TextureRegion currFrame;

    // add walk later search up bot logic
    Texture walkSpriteSheet;
    Animation<TextureRegion> walk;

    Texture idleSpriteSheet;
    Animation<TextureRegion> idle;

    Texture attackSpriteSheet;
    Animation<TextureRegion> attack;

    Texture deathSpriteSheet;
    Animation<TextureRegion> death;

    public float time = 0;
    float x = 1000;
    float y = 70;
    float spriteSpeed = 150.0f;
    public static float health = 100;
    boolean removed = false;

    // Inner Boundaries / HitBox
    public static Rectangle innerBoundaries;
    public float innerXOffset = 110;

    // for flip
    boolean facingRight = true;

    // variables for bot states. i need idle, walk and attack
    enum State {idle, walk, attack, death}

    State currState = State.idle;
    float stateTime = 0;


    public void create(){
        removed = false;
        health = 100;
        // idle anim
        idleSpriteSheet = new Texture("Karasu_tengu/Idle_u.png");

        TextureRegion[][] tmp = TextureRegion.split(idleSpriteSheet, 128, 128);
        TextureRegion[] idleFrames = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            idleFrames[i] = tmp[0][i];
        }
        idle = new Animation<>(0.1f, idleFrames);

        // walk anim
        walkSpriteSheet = new Texture("Karasu_tengu/Walk_u.png");

        TextureRegion[][] tmp1 = TextureRegion.split(walkSpriteSheet, 128, 128);
        TextureRegion[] walkFrames = new TextureRegion[8];
        for (int i = 0; i < 8; i++) {
            walkFrames[i] = tmp1[0][i];
        }
        walk = new Animation<>(0.1f, walkFrames);

        // attack
        attackSpriteSheet = new Texture("Karasu_tengu/Attack_1_u.png");

        TextureRegion[][] tmp2 = TextureRegion.split(attackSpriteSheet, 128, 128);
        TextureRegion[] attackFrames = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            attackFrames[i] = tmp2[0][i];
        }
        attack = new Animation<>(0.1f, attackFrames);

        // death
        deathSpriteSheet = new Texture("Karasu_tengu/Dead_U.png");

        TextureRegion[][] tmp3 = TextureRegion.split(deathSpriteSheet, 128, 128);
        TextureRegion[] deathFrames = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            deathFrames[i] = tmp3[0][i];
        }
        death = new Animation<>(0.2f, deathFrames);

        // Create Karasu's Inner Boundaries
        innerBoundaries = new Rectangle((int) (x + innerXOffset), (int) y, 62, 180);
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

        // find if player is to the right or left
        float dx = playerX - x;
        float dy = playerY - y;
        // distance take pythagorean bc player can be on a platform
        float distance = (float)Math.sqrt(dx * dx + dy * dy);

        facingRight = dx > 0;

        // if far close the distance and once closee attack
        if (distance > 150f) {
            currState = State.walk;

            float dirX = dx / distance;
            float dirY = dy / distance;

            x += dirX * spriteSpeed * delta;
            y += dirY * spriteSpeed * delta;
        }
        else {
            currState = State.attack;
        }

    }

    public void draw(SpriteBatch batch, float time, float delta){
        if (removed) return;
        stateTime += delta;
        float drawX;
        float scaleX;

        // check if the logic makes sense
        switch (currState){
//            case idle:
//                currFrame = idle.getKeyFrame(time, true);
//                break;

            case walk:
                currFrame = walk.getKeyFrame(stateTime, true);
                break;

            case attack:
                currFrame = attack.getKeyFrame(stateTime, true);
                break;

            case death:
                currFrame = death.getKeyFrame(stateTime, false);
                updateEnemyDeath(delta);
                if (removed) return;
                break;

            default:
                currFrame = idle.getKeyFrame(stateTime, true);
        }

        // postion of enemy flipped and facing player to the left yes
        // float drawX = x + 250;
        // float scaleX = -1;

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

    private void updateEnemyDeath(float delta) {
        currFrame = death.getKeyFrame(stateTime, false);
        if (death.isAnimationFinished(stateTime)) {
            removed = true;
            dispose();
        }
    }

    public void dispose() {
        if (idleSpriteSheet != null) idleSpriteSheet.dispose();
        if (walkSpriteSheet != null) walkSpriteSheet.dispose();
        if (attackSpriteSheet != null) attackSpriteSheet.dispose();
        if (deathSpriteSheet != null) deathSpriteSheet.dispose();
    }

    public boolean isRemoved() {
        return removed;
    }
}
