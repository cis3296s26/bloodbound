package com.zipporah.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.zipporah.game.entities.Timer;

public class ScreenManager extends Game{

  public SpriteBatch batch;
  public Timer timer;

  public Timer timer;

  @Override
  public void create() {
    batch = new SpriteBatch();
    timer = new Timer();
    setScreen(new HomeScreen(this));
  }
}
