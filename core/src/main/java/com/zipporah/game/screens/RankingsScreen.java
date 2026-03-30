package com.zipporah.game.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class RankingsScreen implements Screen{

  private final ScreenManager game;

  ExtendViewport viewport;
  
  public RankingsScreen(ScreenManager game){
    this.game = game;
  }

  @Override
  public void show() {
    // Runs
    // Sort by fastest run
    // Sort by points first then speed (fastest overall run)
    // Save top five runs of each
    viewport = new ExtendViewport(1280, 720);
    game.playerData.load();
  }

  @Override
  public void render(float delta) {
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