package com.zipporah.game.enemies;

import com.badlogic.gdx.math.Rectangle;
import com.zipporah.game.Enemy;

public class Karasu extends Enemy {
    @Override
    public void create() {
        // 0 - idle, 1 - walk, 2 - attack, 3 - dead, 4 - hurt
        super.create("Karasu", new int[] {6, 8, 6, 6, 3});

        x = 1750;
        y = 50;
        size = 180;

        innerXOffsetFacingRight = 60f;
        innerXOffsetFacingLeft = 70f;
        innerXOffset = innerXOffsetFacingLeft;

        innerBoundaries = new Rectangle((int) (x + innerXOffset), (int) y, 62, 120);
    }
}
