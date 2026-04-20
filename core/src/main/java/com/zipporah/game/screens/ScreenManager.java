package com.zipporah.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.zipporah.game.entities.CurrentRun;
import com.zipporah.game.entities.StorePlayerData;

public class ScreenManager extends Game{

  public enum Difficulty {
    EASY,
    NORMAL,
    HARD
  }

  public SpriteBatch batch;
  public CurrentRun timer;
  public StorePlayerData playerData;
  public float musicVolume = 0.30f;
  public float sfxVolume = 0.40f;
  public Difficulty difficulty = Difficulty.NORMAL;

  @Override
  public void create() {
    batch = new SpriteBatch();
    timer = new CurrentRun();
    playerData = new StorePlayerData();
    playerData.load();
    setScreen(new HomeScreen(this));
  }

  public void adjustMusicVolume(float amount) {
    musicVolume = Math.max(0f, Math.min(1f, musicVolume + amount));
  }

  public void adjustSfxVolume(float amount) {
    sfxVolume = Math.max(0f, Math.min(1f, sfxVolume + amount));
    com.zipporah.game.Enemy.sfxVolume = sfxVolume;
  }

  public void cycleDifficulty(int direction) {
    Difficulty[] values = Difficulty.values();
    int nextIndex = difficulty.ordinal() + direction;
    if (nextIndex < 0) {
      nextIndex = values.length - 1;
    } else if (nextIndex >= values.length) {
      nextIndex = 0;
    }
    difficulty = values[nextIndex];
  }

  public float getDamageMultiplier() {
    switch (difficulty) {
      case EASY:
        return 0.75f;
      case HARD:
        return 1.25f;
      default:
        return 1.0f;
    }
  }
}
