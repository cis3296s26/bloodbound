package com.zipporah.game.enemies;

import com.badlogic.gdx.math.Rectangle;
import com.zipporah.game.Enemy;

public class Karasu extends Enemy {
    public Karasu(int spawnX, int spawnY, int spriteSize, float innerXOffsetFacingRight, float innerXOffsetFacingLeft, int boundaryWidth, int boundaryHeight) {
        // 0 - idle, 1 - walk, 2 - attack, 3 - dead, 4 - hurt, 5 - jump
        super.create("Enemies/Karasu/", new int[] {6, 8, 6, 6, 3, 15});

        super.x = spawnX;
        super.y = spawnY;
        super.size = spriteSize;

        super.innerXOffsetFacingRight = innerXOffsetFacingRight;
        super.innerXOffsetFacingLeft = innerXOffsetFacingLeft;
        super.innerXOffset = innerXOffsetFacingLeft;

        innerBoundaries = new Rectangle((int) (x + innerXOffset), (int) y, boundaryWidth, boundaryHeight);
        super.jumpsEnabled = true;
    }
}
