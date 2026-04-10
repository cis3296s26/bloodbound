package com.zipporah.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class OptionsScreen implements Screen{

  private final ScreenManager game;

  ExtendViewport viewport;
  BitmapFont font;
  Texture homeButtonTexture;
  float homeButtonX = 1205f;
  float homeButtonY = 670f;
  float homeButtonWidth = 42f;
  float homeButtonHeight = 39f;
  
  public OptionsScreen(ScreenManager game){
    this.game = game;
  }

  @Override
  public void show() {
    // settings for 
    // sound effects to be added
    // music to be added
    // other things to change to be added

    viewport = new ExtendViewport(1280, 720);
    font = new BitmapFont();
    homeButtonTexture = new Texture(Gdx.files.internal("Buttons/HomeButton.png"));
  }

  @Override
  public void render(float delta) {
    // Refresh screen
    ScreenUtils.clear(0f, 0f, 0f, 1f);

    viewport.apply();
    game.batch.setProjectionMatrix(viewport.getCamera().combined);

    Vector2 hudMouse = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
    boolean homeButtonHovering =
        hudMouse.x >= homeButtonX &&
        hudMouse.x <= homeButtonX + homeButtonWidth &&
        hudMouse.y >= homeButtonY &&
        hudMouse.y <= homeButtonY + homeButtonHeight;

    if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && homeButtonHovering) {
      game.setScreen(new HomeScreen(game));
      return;
    }

    game.batch.begin();

    game.batch.draw(homeButtonTexture, homeButtonX, homeButtonY, homeButtonWidth, homeButtonHeight);
    
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
    if (font != null) {
      font.dispose();
    }
    if (homeButtonTexture != null) {
      homeButtonTexture.dispose();
    }
  }
  
}
