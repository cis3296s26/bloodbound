package com.zipporah.game.enemies;
import com.badlogic.gdx.math.Rectangle;
import com.zipporah.game.Enemy;

public class Karasu extends Enemy {
    public void create() {
        super.create("Karasu", new int[] {6, 8, 6, 6});

        // Spawn Location
        super.x = 1750;
        super.y = 50;
        super.size = 200;

        // HitBox
        super.innerXOffsetFacingRight = 60f;
        super.innerXOffsetFacingLeft = 70f;
        super.innerXOffset = super.innerXOffsetFacingLeft;

        innerBoundaries = new Rectangle((int) (x + innerXOffset), (int) y, 62, 120);
    }
}
