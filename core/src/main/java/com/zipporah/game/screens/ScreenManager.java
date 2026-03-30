package com.zipporah.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.zipporah.game.entities.CurrentRun;
import com.zipporah.game.entities.StorePlayerData;

public class ScreenManager extends Game{

  public SpriteBatch batch;
  public CurrentRun timer;
  public StorePlayerData playerData;

  @Override
  public void create() {
    batch = new SpriteBatch();
    timer = new CurrentRun();
    playerData = new StorePlayerData();
    playerData.load();
    setScreen(new HomeScreen(this));
  }
}
