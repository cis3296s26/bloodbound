package com.zipporah.game.entities;

public class GameRun {
  public float elapsedTime;
  public int points;
  public int runNumber;

  public GameRun() {
  }

  public GameRun(float elapsedTime, int points, int runNumber) {
    this.elapsedTime = elapsedTime;
    this.points = points;
    this.runNumber = runNumber;
  }
}
