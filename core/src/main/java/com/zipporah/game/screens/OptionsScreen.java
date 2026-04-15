package com.zipporah.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.zipporah.game.Enemy;

public class OptionsScreen implements Screen{

  private final ScreenManager game;

  ExtendViewport viewport;
  BitmapFont font;
  Texture homeButtonTexture;
  float homeButtonX = 1205f;
  float homeButtonY = 670f;
  float homeButtonWidth = 42f;
  float homeButtonHeight = 39f;
  Rectangle musicDownBounds = new Rectangle(430f, 510f, 60f, 40f);
  Rectangle musicUpBounds = new Rectangle(790f, 510f, 60f, 40f);
  Rectangle sfxDownBounds = new Rectangle(430f, 390f, 60f, 40f);
  Rectangle sfxUpBounds = new Rectangle(790f, 390f, 60f, 40f);
  Rectangle difficultyLeftBounds = new Rectangle(430f, 270f, 60f, 40f);
  Rectangle difficultyRightBounds = new Rectangle(790f, 270f, 60f, 40f);
  
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

    if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
      if (musicDownBounds.contains(hudMouse)) {
        game.adjustMusicVolume(-0.05f);
      } else if (musicUpBounds.contains(hudMouse)) {
        game.adjustMusicVolume(0.05f);
      } else if (sfxDownBounds.contains(hudMouse)) {
        game.adjustSfxVolume(-0.05f);
        Enemy.sfxVolume = game.sfxVolume;
      } else if (sfxUpBounds.contains(hudMouse)) {
        game.adjustSfxVolume(0.05f);
        Enemy.sfxVolume = game.sfxVolume;
      } else if (difficultyLeftBounds.contains(hudMouse)) {
        game.cycleDifficulty(-1);
      } else if (difficultyRightBounds.contains(hudMouse)) {
        game.cycleDifficulty(1);
      }
    }

    game.batch.begin();

    font.draw(game.batch, "Options", 610, 650);

    font.draw(game.batch, "Music Volume", 520, 540);
    font.draw(game.batch, "[-]", musicDownBounds.x + 10f, musicDownBounds.y + 30f);
    font.draw(game.batch, String.format("%d%%", Math.round(game.musicVolume * 100)), 655, 540);
    font.draw(game.batch, "[+]", musicUpBounds.x + 10f, musicUpBounds.y + 30f);

    font.draw(game.batch, "SFX Volume", 520, 420);
    font.draw(game.batch, "[-]", sfxDownBounds.x + 10f, sfxDownBounds.y + 30f);
    font.draw(game.batch, String.format("%d%%", Math.round(game.sfxVolume * 100)), 655, 420);
    font.draw(game.batch, "[+]", sfxUpBounds.x + 10f, sfxUpBounds.y + 30f);

    font.draw(game.batch, "Difficulty", 520, 300);
    font.draw(game.batch, "[<]", difficultyLeftBounds.x + 8f, difficultyLeftBounds.y + 30f);
    font.draw(game.batch, game.difficulty.name(), 655, 300);
    font.draw(game.batch, "[>]", difficultyRightBounds.x + 8f, difficultyRightBounds.y + 30f);

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
