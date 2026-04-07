package com.zipporah.game.enemies;
import com.badlogic.gdx.math.Rectangle;
import com.zipporah.game.Enemy;

public class Skeleton extends Enemy {
    @Override
    public void create() {
        // 0 - idle, 1 - walk, 2 - attack, 3 - dead
        super.create("Skeleton", new int[] {7, 7, 4, 5, 3});

        // Spawn Location
        super.x = 1800;
        super.y = 50;
        super.size = 200;

        // HitBox
        super.innerXOffsetFacingRight = 60f;
        super.innerXOffsetFacingLeft = 70f;
        super.innerXOffset = super.innerXOffsetFacingLeft;

        innerBoundaries = new Rectangle((int) (x + innerXOffset), (int) y, 62, 120);
    }
}
