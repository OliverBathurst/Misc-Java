package uk.ac.reading.ft025024.fluct;

import static uk.ac.reading.ft025024.fluct.Game.screenWidth;
import android.graphics.Bitmap;
import android.graphics.Canvas;

class Background {
    private final Bitmap image; //background image
    private int x;//BG x position (y not needed)

    /**
     * take in a bitmap as the parameter and set the
     * final background bitmap equal to that of one scaled
     * to the screen's width and height
     */
    Background(Bitmap res)
    {
        image = Bitmap.createScaledBitmap(res, Game.screenWidth, Game.screenHeight,false);//scale it up
    }
    /**
     * move the background to the left, if the background's x position
     * is at the end of the screen, reset the x position
     */
    void updateBg()
    {
        x+= -5;
        if(x<-screenWidth){
            x=0;//reset BG position
        }
    }
    /**
     * draw bitmap, if the background's x position is negative,
     * translate at x + the screen width
     */
    void draw(Canvas canvas)
    {
        canvas.drawBitmap(image, x, 0,null);
        if(x<0) {
            canvas.drawBitmap(image, x + screenWidth, 0, null);//translate the background image
        }
    }
}