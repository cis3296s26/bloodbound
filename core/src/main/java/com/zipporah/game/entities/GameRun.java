package com.zipporah.game.entities;

public class GameRun {
  public float elapsedTime;
  public int points;

  public GameRun() {
  }

  public GameRun(float elapsedTime, int points) {
    this.elapsedTime = elapsedTime;
    this.points = points;
  }
}
