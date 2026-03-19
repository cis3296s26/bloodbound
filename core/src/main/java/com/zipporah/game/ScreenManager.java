package com.zipporah.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ScreenManager extends Game{

  public SpriteBatch batch;

  @Override
  public void create() {
    batch = new SpriteBatch();
    setScreen(new GameScreen(this));
  }
}
