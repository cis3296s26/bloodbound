package com.zipporah.game.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class ControlsScreen implements Screen{

  private final ScreenManager game;

  ExtendViewport viewport;
  
  public ControlsScreen(ScreenManager game){
    this.game = game;
  }

  @Override
  public void show() {
    // Pending additional functionalities 
    // user controls (attacks, jump, doge, etc) to be added

    viewport = new ExtendViewport(1280, 720);
  }

  @Override
  public void render(float delta) {
    ScreenUtils.clear(0f, 0f, 0f, 1f);
    viewport.apply();
    game.batch.setProjectionMatrix(viewport.getCamera().combined);
  }

  @Override
  public void resize(int width, int height) {
    if (width <= 0 || height <= 0) return;
    viewport.update(width, height, true);
    game.batch.setProjectionMatrix(viewport.getCamera().combined);
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }

  @Override
  public void hide() {
  }

  @Override
  public void dispose() {
  }
  
}