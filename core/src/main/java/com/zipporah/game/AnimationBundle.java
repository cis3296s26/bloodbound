package com.zipporah.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimationBundle {
    protected Texture texture;
    public Animation<TextureRegion> animation;

    public AnimationBundle (String path, int frames, float frameDuration) {
        this(path, frames, frameDuration, 128, 128);
    }

    public AnimationBundle (String path, int frames, float frameDuration, int tileWidth, int tileHeight) {
        this.texture = new Texture(Gdx.files.internal(path + ".png"));
        TextureRegion[][] split = TextureRegion.split(texture, tileWidth, tileHeight);
        TextureRegion[] splitFrames = new TextureRegion[frames];
        for (int i = 0; i < frames; i++) {
            splitFrames[i] = split[0][i];
        }
        this.animation = new Animation<>(frameDuration, splitFrames);
    }
}
