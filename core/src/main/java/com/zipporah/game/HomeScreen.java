package com.zipporah.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;

public class HomeScreen implements Screen {

  private final ScreenManager game;

  Texture startButton;
  Texture rankingsButton;
  Texture controlsButton;
  Texture optionsButton;
  Texture quitButton;

  float startX = 500;
  float startY = 450;
  float rankingsX = 500;
  float rankingsY = 350;
  float controlsX = 500;
  float controlsY = 250;
  float optionsX = 500;
  float optionsY = 150;
  float quitX = 500;
  float quitY = 50;

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

    float mouseX = Gdx.input.getX();
    float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

    game.batch.begin();
    game.batch.draw(startButton, startX, startY);
    game.batch.draw(rankingsButton, rankingsX, rankingsY);
    game.batch.draw(controlsButton, controlsX, controlsY);
    game.batch.draw(optionsButton, optionsX, optionsY);
    game.batch.draw(quitButton, quitX, quitY);
    game.batch.end();

    boolean startHovering = mouseX >= startX && mouseX <= startX + startButton.getWidth() && mouseY >= startY && mouseY <= startY + startButton.getHeight();
    boolean rankingsHovering = mouseX >= rankingsX && mouseX <= rankingsX + rankingsButton.getWidth() && mouseY >= rankingsY && mouseY <= rankingsY + rankingsButton.getHeight();
    boolean controlsHovering = mouseX >= controlsX && mouseX <= controlsX + controlsButton.getWidth() && mouseY >= controlsY && mouseY <= controlsY + controlsButton.getHeight();
    boolean optionsHovering = mouseX >= optionsX && mouseX <= optionsX + optionsButton.getWidth() && mouseY >= optionsY && mouseY <= optionsY + optionsButton.getHeight();
    boolean quitHovering = mouseX >= quitX && mouseX <= quitX + quitButton.getWidth() && mouseY >= quitY && mouseY <= quitY + quitButton.getHeight();


    if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){
      if(startHovering){
        game.setScreen(new GameScreen(game));
      }
      if(rankingsHovering){
        game.setScreen(new RankingsScreen(game));
      }
      if(controlsHovering){
        game.setScreen(new ControlsScreen(game));
      }
      if(optionsHovering){
        game.setScreen(new OptionsScreen(game));
      }
      if(quitHovering){
        Gdx.app.exit();
      }
    }
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
