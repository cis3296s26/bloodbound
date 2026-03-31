package com.zipporah.game.entities;

import java.util.ArrayList;
import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class StorePlayerData {
  private static final String POINTS_FILE = "runs_by_points.json";
  private static final String SPEED_FILE = "runs_by_speed.json";

  private final Json json = new Json();
  private final ArrayList<GameRun> pointsRuns = new ArrayList<>();
  private final ArrayList<GameRun> speedRuns = new ArrayList<>();

  public void load() {
    pointsRuns.clear();
    speedRuns.clear();
    readRuns(POINTS_FILE, pointsRuns);
    readRuns(SPEED_FILE, speedRuns);
    sortPointsRuns();
    sortSpeedRuns();
  }

  private void readRuns(String path, ArrayList<GameRun> target) {
    FileHandle file = Gdx.files.local(path);
    if (!file.exists()) {
      file.writeString("[]", false);
    }
    String fileRaw = file.readString();
    if (fileRaw == null || fileRaw.trim().isEmpty()) {
      return;
    }
    ArrayList loaded = json.fromJson(ArrayList.class, GameRun.class, fileRaw);
    if (loaded == null) {
      return;
    }
    for (Object item : loaded) {
      if (item instanceof GameRun) {
        target.add((GameRun) item);
      }
    }
  }

  private void writeRuns(String path, ArrayList<GameRun> runs) {
    Gdx.files.local(path).writeString(json.prettyPrint(runs), false);
  }

  public ArrayList<GameRun> getPointsRuns() {
    return pointsRuns;
  }

  public ArrayList<GameRun> getSpeedRuns() {
    return speedRuns;
  }

  private void sortPointsRuns() {
    pointsRuns.sort(Comparator
        .comparingInt((GameRun run) -> run.points).reversed()
        .thenComparingDouble(run -> run.elapsedTime));
  }

  private void sortSpeedRuns() {
    speedRuns.sort(Comparator.comparingDouble(run -> run.elapsedTime));
  }
}
