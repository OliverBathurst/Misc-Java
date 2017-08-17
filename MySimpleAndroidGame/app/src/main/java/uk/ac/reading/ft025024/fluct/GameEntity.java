package uk.ac.reading.ft025024.fluct;

import android.graphics.Rect;
/**
 * abstract class is inherited by enemies, projectiles,
 * the player character. Stores the x and y coordinates of
 * the entity and the width and height of their bitmap
 */
abstract class GameEntity {
    int x,y,width,height;
    /**
     *getRectangle returns the rectangle around the entity
     * at position x,y with the added width and height
     */
    Rect getRectangle()
    {
        return new Rect(x, y, x+width, y+height);
    }
}