package com.zipporah.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class TutorialScreen extends GameScreen {

  // tutorial steps
  private final String[] steps = {
          "Welcome to the tutorial where you will learn the necessary movements for this game!",
          "Collect keys to open doors. Be wary of enemies and spikes as they can kill you!",
          "Also, be on the look out for a potion for extra health on level 2!",
          "Press D to move right.",
          "Press A to move left.",
          "Move while pressing SHIFT to run",
          "Press SPACE or W to jump.",
          "Press Q to send out an attack.",
          "Go up the ladder by holding W and find a chest and press E to collect a key.",
          "Find the door and press E to unlock it."
  };

  private int currentStep = 0;
  private float textWait = 0f;

  // typewriter
  private String fullText    = "";
  private String displayText = "";
  private int    charIndex   = 0;
  private float  charTimer   = 0f;
  private static final float CHAR_DELAY = 0.045f;

  // movement and action tracking
  private boolean movedRight = false;
  private boolean movedLeft  = false;
  private boolean hasJumped  = false;
  private boolean hasAttacked = false;

  // FF7 inspired text dialog
  private BitmapFont ff7Font;
  private Sound typeSound;

  public TutorialScreen(ScreenManager game) {
    super(game);
  }

  @Override
  public void show() {
    super.show(); // everything from the game in

    // FF7 font, doesn't work right now
    ff7Font = new BitmapFont();

    // text sound
    typeSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/textSound.mp3"));

    beginStep(0);
  }

  private void beginStep(int step) {
    currentStep = step;
    fullText    = steps[step];
    displayText = "";
    charIndex   = 0;
    charTimer   = 0f;
    textWait = 5f;
  }

  private void updateTypewriter(float delta) {
    if (charIndex >= fullText.length()){
      return;
    }

    charTimer += delta;
    if (charTimer >= CHAR_DELAY) {
      charTimer -= CHAR_DELAY;
      charIndex++;
      displayText = fullText.substring(0, charIndex);
      typeSound.play(0.4f);
    }
  }

  @Override
  public void render(float delta) {
    input(delta);
    logic(delta);
    draw(delta);
    drawDialog(delta); // box of text on top
  }

  @Override
  protected void input(float delta) {
    // load in all game inputs
    super.input(delta);

    boolean textDone = charIndex >= fullText.length();

    // step progression
    if ((currentStep == 0 || currentStep == 1 || currentStep == 2) && textDone && textWait <= 0f) {
      beginStep(currentStep + 1);
    }

    if (currentStep == 3 && textDone && !movedRight
            && Gdx.input.isKeyPressed(Input.Keys.D)) {
      movedRight = true;
      beginStep(4);
    }

    if (currentStep == 4 && textDone && !movedLeft
            && Gdx.input.isKeyPressed(Input.Keys.A)) {
      movedLeft = true;
      beginStep(5);
    }

    if (currentStep == 5 && textDone && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
      beginStep(6);
    }

    if (currentStep == 6 && textDone && !hasJumped && player.jumping) {
      hasJumped = true;
      beginStep(7);
    }

    if (currentStep == 7 && textDone && !hasAttacked && Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
      hasAttacked = true;
      beginStep(8);
    }

    // chest step — chest1Open is set by super.input(), we just watch for it
    if (currentStep == 8 && textDone && chest1Open) {
      beginStep(9);
    }
    // door step — firstDoorOpen being set means tutorial is done
    if (currentStep == 9 && firstDoorOpen) {
      game.setScreen(new HomeScreen(game));
    }
  }

  @Override
  protected void logic(float delta) {
    super.logic(delta);
    updateTypewriter(delta);
    if (textWait > 0f) {
      textWait -= delta;
    }
  }

  private void drawDialog(float delta) {
    float boxW = 600f;
    float boxH = 80f;
    float boxX = (1280f - boxW) / 2f;
    float boxY = 720f - boxH - 10f;

    viewportHUD.apply();
    shapeRenderer.setProjectionMatrix(viewportHUD.getCamera().combined);

    // blue gradient body, found from https://codepen.io/Kaizzo/pen/aGWwMM
    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    shapeRenderer.setColor(4f/255f, 0f, 157f/255f, 1f);   // top #04009d
    shapeRenderer.rect(boxX, boxY + boxH / 2f, boxW, boxH / 2f);
    shapeRenderer.setColor(6f/255f, 0f, 77f/255f, 1f);    // bottom #06004d
    shapeRenderer.rect(boxX, boxY, boxW, boxH / 2f);
    shapeRenderer.end();

    shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
    shapeRenderer.setColor(231f/255f, 223f/255f, 231f/255f, 1f); // #e7dfe7
    shapeRenderer.rect(boxX - 1, boxY - 1, boxW + 2, boxH + 2);
    shapeRenderer.setColor(156f/255f, 154f/255f, 156f/255f, 1f); // #9c9a9c
    shapeRenderer.rect(boxX - 2, boxY - 2, boxW + 4, boxH + 4);
    shapeRenderer.setColor(66f/255f, 69f/255f, 66f/255f, 1f);    // #424542
    shapeRenderer.rect(boxX - 3, boxY - 3, boxW + 6, boxH + 6);
    shapeRenderer.end();

    // text
    game.batch.setProjectionMatrix(viewportHUD.getCamera().combined);
    game.batch.begin();
    GlyphLayout layout = new GlyphLayout(ff7Font, displayText);
    float textY = boxY + boxH / 2f + layout.height / 2f + 4f;
    ff7Font.draw(game.batch, displayText, boxX + 16f, textY);
    game.batch.end();
  }

  @Override
  public void dispose() {
    super.dispose();
    if (ff7Font   != null) ff7Font.dispose();
    if (typeSound != null) typeSound.dispose();
  }
}