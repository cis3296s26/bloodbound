package com.zipporah.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CurrentRun {

  private float elapsedTime = 0f;
  private int points = 0;
  BitmapFont font;

  public CurrentRun(){
    font = new BitmapFont();
  }

  public void update() {
      elapsedTime += Gdx.graphics.getDeltaTime();
  }

  public void addPoints(int amount) {
    points += amount;
  }

  public void draw(SpriteBatch batch) {
      font.draw(batch, String.format("Time: %.1f", elapsedTime), 1100, 700);
      font.draw(batch, String.format("Points: %d", points), 1000, 700);
  }

  public void dispose() {
      font.dispose();
  }

  public float getElapsedTime() {
    return elapsedTime;
  }

  public float getPoints() {
    return elapsedTime;
  }

  public void reset() {
    elapsedTime = 0f;
    points = 0;
  }
}
