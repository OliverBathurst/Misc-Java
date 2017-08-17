package uk.ac.reading.ft025024.fluct;

import static uk.ac.reading.ft025024.fluct.Game.screenHeight;
import static uk.ac.reading.ft025024.fluct.Game.screenWidth;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import java.util.Random;

class Projectile extends GameEntity {
    private final Random rand = new Random();
    private final Bitmap res;
    final boolean isLife,isPlayerMissile; //boolean attributes of missile
    final int homingTimeout = 90,missileDamage;
    private final Context c;
    int homingTime = 0,speed;
    boolean isHoming;
    /**
     * constructor for making new projectile objects given parameters
     */
    Projectile(Context context, int x, int y, int score, int rev, int damage, boolean isLifeLine, boolean isHomingMissile,
               boolean isPlayerM) {

        this.c = context; //setup context to resolve bitmaps
        res = Bitmap.createScaledBitmap(getBitmap(damage,isPlayerM), screenWidth/30, screenHeight/30,false);
        super.x = x; //set GameEntity x and y position
        super.y = y;
        isLife = isLifeLine;
        isHoming = isHomingMissile;
        missileDamage = damage;
        isPlayerMissile = isPlayerM;
        width = res.getWidth(); //setup superclass attributes for the object
        height = res.getHeight();
        speed = rev * (8 + (int) (rand.nextDouble()* score /30)); //set missile speed
        if(speed>40)speed = 40; //cap speed
    }
    /**
     * get and return the required bitmap given to arguments
     * the damage of the missile and if it belongs to the player
     */
    private Bitmap getBitmap(int damage, boolean isPlayerMissile){
        Bitmap projectileBitmap;
        if (isPlayerMissile){
            projectileBitmap = BitmapFactory.decodeResource(c.getResources(), R.drawable.missilerev);
        }else {
            switch (damage) {
                case 0:
                    projectileBitmap = BitmapFactory.decodeResource(c.getResources(), R.drawable.lives);
                    break;
                case 1:
                    projectileBitmap = BitmapFactory.decodeResource(c.getResources(), R.drawable.missile);
                    break;
                case 2:
                    projectileBitmap = BitmapFactory.decodeResource(c.getResources(), R.drawable.missilemed);
                    break;
                case 3:
                    projectileBitmap = BitmapFactory.decodeResource(c.getResources(), R.drawable.missilehard);
                    break;
                default:
                    projectileBitmap = BitmapFactory.decodeResource(c.getResources(), R.drawable.missile);
                    break;
            }
        }
        return projectileBitmap;
    }
    /**
     * draw scaled bitmap to canvas
     */
    void draw(Canvas canvas) {
        try{
            canvas.drawBitmap(res,x,y,null);
        }catch(Exception ignored){}
    }
}
