package com.zipporah.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Timer {

  private float elapsedTime = 0f;
  private int killCount = 0;
  BitmapFont font;

  public Timer(){
    font = new BitmapFont();
  }

  public void update() {
      elapsedTime += Gdx.graphics.getDeltaTime();
  }

  public void draw(SpriteBatch batch) {
      font.draw(batch, String.format("Time: %.1f", elapsedTime), 1100, 700);
      font.draw(batch, String.format("Kill Count: %1", killCount), 1000, 700);
  }

  public void dispose() {
      font.dispose();
  }
}
