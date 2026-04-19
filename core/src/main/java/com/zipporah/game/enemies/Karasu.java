package com.zipporah.game.enemies;

import com.badlogic.gdx.math.Rectangle;
import com.zipporah.game.Enemy;

public class Karasu extends Enemy {
    private static final int KARASU_FRAME_SIZE = 128;
    private static final int KARASU_DRAW_SIZE = 250;

    public Karasu(int spawnX, int spawnY, int spriteSize, float innerXOffsetFacingRight, float innerXOffsetFacingLeft, int boundaryWidth, int boundaryHeight) {
        super.x = spawnX;
        super.y = spawnY;
        super.size = KARASU_FRAME_SIZE;

        // 0 - idle, 1 - walk, 2 - attack, 3 - dead, 4 - hurt, 5 - jump
        super.create("Enemies/Karasu/", new int[] {6, 8, 6, 6, 3, 15});
        super.size = KARASU_DRAW_SIZE;

        super.innerXOffsetFacingRight = innerXOffsetFacingRight;
        super.innerXOffsetFacingLeft = innerXOffsetFacingLeft;
        super.innerXOffset = innerXOffsetFacingLeft;

        innerBoundaries = new Rectangle((int) (x + innerXOffset), (int) y, boundaryWidth, boundaryHeight);
        super.jumpsEnabled = true;
    }
}
