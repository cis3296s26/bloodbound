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

    float time = 0;
    float x = 1000;
    float y = 150;
    float spriteSpeed = 200.0f;

    public void create(){
        idleSpriteSheet = new Texture("Karasu_tengu/Idle.png");

        TextureRegion[][] tmp = TextureRegion.split(idleSpriteSheet, 128, 128);
        TextureRegion[] idleFrames = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            idleFrames[i] = tmp[0][i];
        }
        idle = new Animation<>(0.1f, idleFrames);
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