package uk.ac.reading.ft025024.fluct;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

class GameThread extends Thread
{
    private final SurfaceHolder surfaceHolder;
    private final GameView gameViewObj;
    private volatile boolean isRunning;

    GameThread(SurfaceHolder surfaceHolder, GameView gameView)
    {
        super();
        this.gameViewObj = gameView;
        this.surfaceHolder = surfaceHolder;
    }
    /**
     * set flags for the running of the thread
     */
    void startup()
    {
        isRunning=true;
    }
    void shutdown()
    {
        isRunning=false;
    }
    /**
     * main thread run method handles drawing of canvas and updating of main method
     */
    @Override
    public void run() //main game loop
    {
        while (isRunning) {
            Canvas canvas = null;
             try {
                 if(surfaceHolder.getSurface().isValid()) {
                     long now = System.currentTimeMillis();
                     canvas = surfaceHolder.lockCanvas();//lock canvas for editing
                     synchronized (surfaceHolder) {
                         gameViewObj.draw(canvas); //draw to the canvas
                         gameViewObj.updateGameView();//update the game logic

                         long timeDifference = System.currentTimeMillis() - now;
                         //int FPS = 30;
                         //int FRAME = 1000 / FPS; //33 for 30 fps
                         int timeToSleep = (int) (33 - timeDifference);
                         if (timeToSleep > 0) {
                             try {
                                 GameThread.sleep(timeToSleep); //sleep to provide a consistent frame rate
                             } catch (Exception ignored) {}
                         }
                     }
                 }
             } catch (Exception ignored) {
             } finally { //executes no matter what
                 if (canvas != null) {
                     try {
                         surfaceHolder.unlockCanvasAndPost(canvas);
                     } catch (Exception ignored) {}
                 }
             }
         }
     }
}