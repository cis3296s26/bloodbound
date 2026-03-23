package com.zipporah.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;

public class HomeScreen implements Screen {

  private final ScreenManager game;

  Texture pixelLogo;
  Texture startButton;
  Texture rankingsButton;
  Texture controlsButton;
  Texture optionsButton;
  Texture quitButton;

  float pixelLogoX = 350;
  float pixelLogoY = 600;
  float startX = 552;
  float startY = 460;
  float rankingsX = 552;
  float rankingsY = 360;
  float controlsX = 552;
  float controlsY = 260;
  float optionsX = 552;
  float optionsY = 160;
  float quitX = 552;
  float quitY = 60;

  public HomeScreen(ScreenManager game) {
    this.game = game;
  }

  @Override
  public void show() {
    pixelLogo = new Texture("Logos/PixelLogo.png");
    startButton = new Texture("Buttons/StartButton.png");
    rankingsButton = new Texture("Buttons/RankingsButton.png");
    controlsButton = new Texture("Buttons/ControlsButton.png");
    optionsButton = new Texture("Buttons/OptionsButton.png");
    quitButton = new Texture("Buttons/QuitButton.png");
  }

  @Override
  public void render(float delta) {
    // BYPASS MENU
    game.setScreen(new GameScreen(game));

    float mouseX = Gdx.input.getX();
    float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

    game.batch.begin();
    game.batch.draw(pixelLogo, pixelLogoX, pixelLogoY);
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

        // game.setScreen(new RankingsScreen(game));
      }
      if(controlsHovering){
        // game.setScreen(new ControlsScreen(game));
      }
      if(optionsHovering){
        //  game.setScreen(new OptionsScreen(game));
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
