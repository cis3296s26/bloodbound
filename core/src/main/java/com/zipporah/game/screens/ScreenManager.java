package com.zipporah.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.zipporah.game.entities.CurrentRun;

public class ScreenManager extends Game{

  public SpriteBatch batch;
  public CurrentRun timer;

  @Override
  public void create() {
    batch = new SpriteBatch();
    timer = new CurrentRun();
    setScreen(new HomeScreen(this));
  }
}
