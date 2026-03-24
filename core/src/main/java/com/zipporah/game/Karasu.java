package com.zipporah.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class Karasu {
    // add walk later search up bot logic
    Texture walkSpriteSheet;
    Animation<TextureRegion> walk;

    Texture idleSpriteSheet;
    Animation<TextureRegion> idle;

    Texture attackSpriteSheet;
    Animation<TextureRegion> attack;

    float time = 0;
    float x = 1000;
    float y = 70;
    float spriteSpeed = 150.0f;

    // for flip
    boolean facingRight = true;

    // variables for bot states. i need idle, walk and attack
    enum State {idle, walk, attack}

    State currState = State.idle;
    float stateTime = 0;


    public void create(){
        // idle anim
        idleSpriteSheet = new Texture("Skeleton/Idle.png");

        TextureRegion[][] tmp = TextureRegion.split(idleSpriteSheet, 128, 128);
        TextureRegion[] idleFrames = new TextureRegion[7];
        for (int i = 0; i < 7; i++) {
            idleFrames[i] = tmp[0][i];
        }
        idle = new Animation<>(0.1f, idleFrames);

        // walk anim
        walkSpriteSheet = new Texture("Skeleton/Walk.png");

        TextureRegion[][] tmp1 = TextureRegion.split(walkSpriteSheet, 128, 128);
        TextureRegion[] walkFrames = new TextureRegion[7];
        for (int i = 0; i < 7; i++) {
            walkFrames[i] = tmp1[0][i];
        }
        walk = new Animation<>(0.1f, walkFrames);

        // attack
        attackSpriteSheet = new Texture("Skeleton/Attack_1.png");

        TextureRegion[][] tmp2 = TextureRegion.split(attackSpriteSheet, 128, 128);
        TextureRegion[] attackFrames = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            attackFrames[i] = tmp2[0][i];
        }
        attack = new Animation<>(0.1f, attackFrames);
    }

    // follow player sprite
    public void botLogic(float playerX, float playerY, float delta) {
        stateTime += delta;

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

    public void draw(SpriteBatch batch, float time){
        TextureRegion currFrame;
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
        } else {
            drawX = x + 250; // idk if this is right width check
            scaleX = -1;
        }

        batch.draw(currFrame, drawX, y, 0, 0, 250, 250, scaleX, 1, 0);

    }

    public void dispose() {

    }
}