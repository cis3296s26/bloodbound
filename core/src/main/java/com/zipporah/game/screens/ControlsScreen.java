package com.zipporah.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class ControlsScreen implements Screen{

  private final ScreenManager game;

  ExtendViewport viewport;
  BitmapFont font;
  
  public ControlsScreen(ScreenManager game){
    this.game = game;
  }

  @Override
  public void show() {
    // Pending additional functionalities 
    // user controls (attacks, jump, doge, etc) to be added

    viewport = new ExtendViewport(1280, 720);
    font = new BitmapFont();
  }

  @Override
  public void render(float delta) {
    // Refresh screen
    ScreenUtils.clear(0f, 0f, 0f, 1f);

    // Back to home
    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
      game.setScreen(new HomeScreen(game));
      return;
    }

    viewport.apply();
    game.batch.setProjectionMatrix(viewport.getCamera().combined);

    game.batch.begin();

    font.draw(game.batch, "Press Esc For Back",50, 690);

    game.batch.end();
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
    font.dispose();
  }
  
}