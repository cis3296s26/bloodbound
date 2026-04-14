package com.zipporah.game.entities;

import java.util.ArrayList;
import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class StorePlayerData {
  private static final String RUNS_FILE = "runs.json";

  private final Json json = new Json();
  private final ArrayList<GameRun> runs = new ArrayList<>();

  public void saveRun(float elapsedTime, int points) {
    load();
    int runNumber = runs.size() + 1;
    runs.add(new GameRun(elapsedTime, points, runNumber));
    sortRuns();
    Gdx.files.local(RUNS_FILE).writeString(json.prettyPrint(runs), false);
  }

  public void load() {
    runs.clear();
    FileHandle file = Gdx.files.local(RUNS_FILE);

    if (!file.exists()) {
      file.writeString("[]", false);
      return;
    }

    String raw = file.readString();
    if (raw == null || raw.trim().isEmpty()){
      return;
    }

    ArrayList loaded = json.fromJson(ArrayList.class, GameRun.class, raw);
    if (loaded == null){
      return;
    }

    for (Object item : loaded) {
      if (item instanceof GameRun) {
        runs.add((GameRun) item);
      }
    }
    sortRuns();
  }

  private void sortRuns() {
    runs.sort(Comparator.comparingDouble(run -> run.elapsedTime));
  }

  public ArrayList<GameRun> getRuns() {
    return runs;
  }
}
