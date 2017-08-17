package uk.ac.reading.ft025024.fluct;

import static uk.ac.reading.ft025024.fluct.Game.screenWidth;
import static uk.ac.reading.ft025024.fluct.Game.screenHeight;
import android.graphics.Bitmap;
import android.graphics.Canvas;

class Enemy extends GameEntity {
    int speed,missileTimer,homingTime,lives= Intermediary.enemyLives;
    final int missileDam;
    private final Bitmap scaleRes;
    /**
     *enemy constructor to create new enemy given their missile
     * damage and their elevation as arguments
     */
    Enemy(Bitmap res,int elevation, int missileDamage) {
        scaleRes = Bitmap.createScaledBitmap(res, screenWidth/10, screenHeight/10,false); //scale bitmap
        missileDam = missileDamage; //setup their missile damage
        speed = 1; //arbitrary value (same for every enemy)
        x = (screenWidth-scaleRes.getWidth()); //position far right-(width of the scaled bitmap)
        y = elevation; //setup opposite
        missileTimer = 0;
        homingTime = 0;//initialise timers
        width = scaleRes.getWidth(); //setup game entity width and height for collision
        height = scaleRes.getHeight();
    }
    /**
     *draw the scaled bitmap
     */
    void draw(Canvas canvas) {
        try {
            canvas.drawBitmap(scaleRes, x, y, null);
        }catch(Exception ignored){}
    }
}
