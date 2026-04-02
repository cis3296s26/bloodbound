package com.zipporah.game;

public class Enemy {

  protected boolean attacking = false;
  protected boolean removed = false;

  public boolean isAttacking() {
    return attacking;
  }

  public boolean isRemoved() {
    return removed;
  }
}
