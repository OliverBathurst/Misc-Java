package uk.ac.reading.ft025024.fluct;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import static uk.ac.reading.ft025024.fluct.Game.screenHeight;
import static uk.ac.reading.ft025024.fluct.Game.screenWidth;

class Character extends GameEntity {
    int score,playerFireTime,lives =  Intermediary.pubLivesID; //avoiding getters and setters
    boolean isPlaying;
    private final Bitmap scaledBitmap;

    /**
     * pass bitmap in, setup initial position as half screen height and x pos of 100,
     * reset score, scale the bitmap up and store the new width and height in the
     * character's superclass' (GameEntity) for collision detection.
     */
    Character(Bitmap res) {
        x = 100;
        y = screenHeight / 2;
        score = 0;
        scaledBitmap = Bitmap.createScaledBitmap(res, screenWidth/10, screenHeight/10,false);
        height = scaledBitmap.getHeight();
        width = scaledBitmap.getWidth();
    }
    /**
     * simply draw the scaled bitmap
     */
    void draw(Canvas canvas) {
        try {
            canvas.drawBitmap(scaledBitmap, x, y, null);
        }catch(Exception ignored){}
    }
    /**
     * called on restarting game, resets position and score
     */
    void resetScoreAndPos(){score = 0;y = screenHeight/2;x = 100;} //dy = 0;
}