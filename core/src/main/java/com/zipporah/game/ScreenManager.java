package com.zipporah.game;

import com.badlogic.gdx.Game;

public class ScreenManager extends Game{

  @Override
  public void create() {
    setScreen(new HomeScreen(this));
  }
  
}
