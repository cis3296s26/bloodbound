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
    float spriteSpeed = 200.0f;

    // for flip
    boolean facingRight = true;

    // variables for bot states. i need idle, walk and attack
    enum botState {idle, walk, attack}


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
            walkFrames[i] = tmp[0][i];
        }
        walk = new Animation<>(0.1f, walkFrames);

        // attack
        attackSpriteSheet = new Texture("Skeleton/Attack_1.png");

        TextureRegion[][] tmp2 = TextureRegion.split(attackSpriteSheet, 128, 128);
        TextureRegion[] attackFrames = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            attackFrames[i] = tmp[0][i];
        }
        attack = new Animation<>(0.1f, attackFrames);
    }

    public void botLogic() {

    }

    public void draw(SpriteBatch batch, float time){
        TextureRegion currFrame = idle.getKeyFrame(time, true);

        // postion of enemy flipped and facing player to the left yes
        float drawX = x + 250;
        float scaleX = -1;

        batch.draw(currFrame, drawX, y, 0, 0, 250, 250, scaleX, 1, 0);

    }

    public void dispose() {

    }
}