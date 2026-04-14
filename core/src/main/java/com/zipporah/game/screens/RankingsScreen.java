package com.zipporah.game.screens;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.zipporah.game.entities.GameRun;

public class RankingsScreen implements Screen {

  private final ScreenManager game;

  ExtendViewport viewport;
  BitmapFont font;
  Texture homeButtonTexture;
  float homeButtonX = 1205f;
  float homeButtonY = 670f;
  float homeButtonWidth = 42f;
  float homeButtonHeight = 39f;

  public RankingsScreen(ScreenManager game) {
    this.game = game;
  }

  @Override
  public void show() {
    viewport = new ExtendViewport(1280, 720);
    font = new BitmapFont();
    homeButtonTexture = new Texture(Gdx.files.internal("Buttons/HomeButton.png"));
    game.playerData.load();
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

    List<GameRun> runs = game.playerData.getRuns();

    game.batch.begin();

    // title
    font.draw(game.batch, "YOUR HIGH SCORES", 570, 690);

    // column headers
    font.draw(game.batch, "RANK", 280, 640);
    font.draw(game.batch, "TIME", 620, 640);
    font.draw(game.batch, "POINTS", 920, 640);

    // row of alt red and white
    Color[] rowColors = {
            Color.WHITE,
            Color.RED,
            Color.WHITE,
            Color.RED,
            Color.WHITE,
            Color.RED,
            Color.WHITE,
            Color.RED,
            Color.WHITE,
            Color.RED
    };

    String[] ranks = {"1ST","2ND","3RD","4TH","5TH","6TH","7TH","8TH","9TH","10TH"};

    if (runs.isEmpty()) {
      font.setColor(Color.WHITE);
      font.draw(game.batch, "No runs saved yet", 400, 580);
    } else {
      float y = 590;
      for (int i = 0; i < runs.size() && i < 10; i++) {
        GameRun run = runs.get(i);
        font.setColor(rowColors[i]);
        font.draw(game.batch, ranks[i], 280, y);
        font.draw(game.batch, String.format("%.1fs", run.elapsedTime), 620, y);
        if(run.points >= 10){
          font.draw(game.batch, String.valueOf(run.points), 937, y);
        } else{
          font.draw(game.batch, String.valueOf(run.points), 940, y);
        }
        y -= 50;
      }
    }

    // reset color to fix homewscreen button bug
    font.setColor(Color.WHITE);

    game.batch.draw(homeButtonTexture, homeButtonX, homeButtonY, homeButtonWidth, homeButtonHeight);
    game.batch.end();
  }

  @Override
  public void resize(int width, int height) {
    if (width <= 0 || height <= 0)
      return;
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
