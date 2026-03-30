package com.zipporah.game.screens;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.zipporah.game.entities.GameRun;

public class RankingsScreen implements Screen {

  private final ScreenManager game;

  ExtendViewport viewport;
  BitmapFont font;

  public RankingsScreen(ScreenManager game) {
    this.game = game;
  }

  @Override
  public void show() {
    viewport = new ExtendViewport(1280, 720);
    font = new BitmapFont();
    game.playerData.load();
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

    List<GameRun> pointsRuns = game.playerData.getPointsRuns();
    List<GameRun> speedRuns = game.playerData.getSpeedRuns();

    game.batch.begin();

    font.draw(game.batch, "Press Esc For Back",50, 690);

    font.draw(game.batch, "Player Stats", 560, 690);
    font.draw(game.batch, "Best Run (Overall)", 220, 630);
    font.draw(game.batch, "Fastest Run", 760, 630);

    // Pull Best Runs
    float pointsY = 580;
    if (pointsRuns.isEmpty()) {
      font.draw(game.batch, "No runs saved yet", 120, pointsY);
    } else {
      for (int i = 0; i < pointsRuns.size() && i < 10; i++) {
        GameRun run = pointsRuns.get(i);
        font.draw(game.batch, (i + 1) + ". " + run.points + " pts  " + run.elapsedTime + " s", 120, pointsY);
        pointsY -= 35;
      }
    }

    // Pull Speed Runs
    float speedY = 580;
    if (speedRuns.isEmpty()) {
      font.draw(game.batch, "No runs saved yet", 660, speedY);
    } else {
      for (int i = 0; i < speedRuns.size() && i < 10; i++) {
        GameRun run = speedRuns.get(i);
        font.draw(game.batch, (i + 1) + ". " + run.elapsedTime + " s  " + run.points + " pts", 660, speedY);
        speedY -= 35;
      }
    }

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
    font.dispose();
  }
}