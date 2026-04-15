package com.zipporah.game.enemies;
import com.badlogic.gdx.math.Rectangle;
import com.zipporah.game.Enemy;

public class Skeleton extends Enemy {
    public Skeleton(int spawnX, int spawnY, int spriteSize, float innerXOffsetFacingRight, float innerXOffsetFacingLeft, int boundaryWidth, int boundaryHeight) {
        // 0 - idle, 1 - walk, 2 - attack, 3 - dead
        super.create("Enemies/Skeleton/", new int[]{7, 7, 4, 5, 3});

        // Spawn Location
        super.x = spawnX;
        super.y = spawnY;
        super.size = spriteSize;

        // HitBox
        super.innerXOffsetFacingRight = innerXOffsetFacingRight;
        super.innerXOffsetFacingLeft = innerXOffsetFacingLeft;
        super.innerXOffset = super.innerXOffsetFacingLeft;

        innerBoundaries = new Rectangle((int) (x + innerXOffset), (int) y, boundaryWidth, boundaryHeight);
        super.jumpsEnabled = false;
    }
}
