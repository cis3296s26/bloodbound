package com.zipporah.game.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.zipporah.game.screens.GameScreen;


public class  Karasu {
    // add walk later search up bot logic
    Texture walkSpriteSheet;
    Animation<TextureRegion> walk;

    Texture idleSpriteSheet;
    Animation<TextureRegion> idle;

    Texture attackSpriteSheet;
    Animation<TextureRegion> attack;

    public float time = 0;
    public float x = 1250;
    public float y = 250;
    float spriteSpeed = 150.0f;
    public static float health = 100;

    // Inner Boundaries / HitBox
    public static Rectangle innerBoundaries;
    public float innerXOffset = 110;

    // for flip
    boolean facingRight = true;

    // variables for bot states. i need idle, walk and attack
    enum State {idle, walk, attack}

    State currState = State.idle;
    float stateTime = 0;

    // add gravity
    public float velocityY = 0f;
    public float gravity = -1500f;
    public boolean onGround = false;

    // hitbox idk if these sizes are right check later
    public float width = 80f;
    public float height = 110f; // had to shrink bc he as hitting ceiling

    public Rectangle enemyBox = new Rectangle();

    // check for ground
    public Rectangle ground = new Rectangle();
    float groundAhead = 10f;

    public void create(){
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

        // Create Karasu's Inner Boundaries
        innerBoundaries = new Rectangle((int) (x + innerXOffset), (int) y, 62, 180);
    }

    // follow player sprite
    public void botLogic(float playerX, float playerY, float delta) {
        stateTime += delta;

        float dx = playerX - x;
        float dy = playerY - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        facingRight = dx > 0;
        float dirX = facingRight ? 1 : -1;

        velocityY += gravity * delta;
        y += velocityY * delta;

        enemyBox.set(x + 10, y, width, height);
        onGround = false;


//        // find if player is to the right or left
//        float dx = playerX - x;
//        float dy = playerY - y;
//
//        // distance take pythagorean bc player can be on a platform
//        float distance = (float)Math.sqrt(dx * dx + dy * dy);
//
//        facingRight = dx > 0;
//
//        // walk down stairs (gravity for y coordinate
//
//        onGround = false;
//
//        velocityY += gravity * delta;
//        y += velocityY * delta;
//
//        // hit box
//        onGround = false;
//        enemyBox.set(x + 10, y, width, height);

        for (Rectangle rect : GameScreen.collisionRectangles) {
            if (enemyBox.overlaps(rect)) {
                float enemyFeetY = y;
                float platformTopY = rect.y + rect.height;

                if (velocityY <= 0 && enemyFeetY >= platformTopY - 15) {
                    y = platformTopY;
                    velocityY = 0;
                    onGround = true;
                    enemyBox.set(x + 10, y, width, height);
                    break;
                }
            }
        }

        // if far close the distance and once closee attack
        if (distance > 150f) {
            if (groundAhead(GameScreen.collisionRectangles) || !onGround) {
                currState = State.walk;
                x += dirX * spriteSpeed * delta;
            } else {
                currState = State.idle;
            }
        }
        else if (distance < 150f) {
            currState = State.attack;
        }
        else {
            currState = State.idle;
        }
        innerBoundaries.setPosition(x + innerXOffset, y);
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
            innerXOffset = 80;
        } else {
            drawX = x + 250; // idk if this is right width check
            scaleX = -1;
        }

        batch.draw(currFrame, drawX, y, 0, 0, 250, 250, scaleX, 1, 0);

        // Move Karasu's Inner Boundaries
        innerBoundaries.setPosition(x + innerXOffset, y);
    }

    // ground ahead?
    private boolean groundAhead(Array<Rectangle> floor) {
        float groundX;

        if (facingRight) {
            groundX = x + width + 5;
        } else {
            groundX = x - 25;
        }

        // idk if right check later
        float groundY = y - 100;

        ground.set(groundX, groundY, 20, 40);

        for (Rectangle rect : floor) {
            if (ground.overlaps(rect)) {
                return true;
            }
        }
        return false;
    }


    public void dispose() {

    }
}
