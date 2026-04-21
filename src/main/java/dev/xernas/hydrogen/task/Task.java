package dev.xernas.hydrogen.task;

import dev.xernas.hydrogen.AppConstants;

public abstract class Task {

    private int tickCounter = 0;

    public void tick() {
        tickCounter++;
    }

    public int getTickCounter() {
        return tickCounter;
    }

    public int getTickInterval() {
        return (int) (getTimer() / AppConstants.FRAMETIME);
    }

    public void resetCounter() {
        tickCounter = 0;
    }

    public abstract float getTimer();
    public abstract void update(float dt);


}
