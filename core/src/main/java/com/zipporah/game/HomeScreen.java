package com.zipporah.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;

public class HomeScreen implements Screen {

  private final ScreenManager game;

  Texture startButton;
  Texture rankingsButton;
  Texture controlsButton;
  Texture optionsButton;
  Texture quitButton;

  public HomeScreen(ScreenManager game) {
    this.game = game;
  }

  @Override
  public void show() {
    //change pngs
    startButton = new Texture("HomeScreen/StartButton.png");
    rankingsButton = new Texture("HomeScreen/RankingsButton.png");
    controlsButton = new Texture("HomeScreen/ControlsButton.png");
    optionsButton = new Texture("HomeScreen/OptionsButton.png");
    quitButton = new Texture("HomeScreen/QuitButton.png");
  }

  @Override
  public void render(float delta) {
    game.batch.begin();
    game.batch.draw(startButton, 500, 450);
    game.batch.draw(rankingsButton, 500, 350);
    game.batch.draw(controlsButton, 500, 250);
    game.batch.draw(optionsButton, 500, 150);
    game.batch.draw(quitButton, 500, 50);
    game.batch.end();
  }

  @Override
  public void resize(int width, int height) {
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
    startButton.dispose();
    rankingsButton.dispose();
    controlsButton.dispose();
    optionsButton.dispose();
    quitButton.dispose();
  }
  
}
