package com.ashcollege.run;

public abstract class MyRunnable implements Runnable {
    private boolean running = true;
    private static final int SPEED =2000;

    public MyRunnable() {
    }

    public void stop() {
        running = false;
    }
    public boolean isRunning(){
        return running;
    }

    public abstract void _run();

    @Override
    public void run() {
        while (running) {
            this._run();
            Utils.sleep(SPEED);
        }
    }
}