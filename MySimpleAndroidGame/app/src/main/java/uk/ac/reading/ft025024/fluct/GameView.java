package uk.ac.reading.ft025024.fluct;

import static uk.ac.reading.ft025024.fluct.Game.screenHeight;
import static uk.ac.reading.ft025024.fluct.Game.screenWidth;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.content.Context;
import android.graphics.BitmapFactory;

class GameView extends SurfaceView implements SurfaceHolder.Callback
{
    private final int noEnemiesOnScreen =  Intermediary.maxOnScreen, enemyFireTime = Intermediary.enemyFireTime,
            lifelineFireTime = Intermediary.lifelineFireTime,playerFireTime = Intermediary.playerFireTime,
            homingMissileTimer = Intermediary.homingMissileTimer,hardChance = Intermediary.hardChance;
    private boolean reset,hasStarted,newGame;
    private final boolean isHoming =  Intermediary.homingMissilesEnabled;
    private final int map = Intermediary.pubMapID, craft = Intermediary.pubCharID;
    private long startReset; //reset timer
    private GameThread gamethread; //game thread obj
    private final Background background = new Background(BitmapFactory.decodeResource(getResources(),  map)); //background object
    private final Character player = new Character(BitmapFactory.decodeResource(getResources(),  craft));
    private static final ArrayList<Projectile> projectiles = new ArrayList<>(); //arraylist that stores projectiles
    private static final ArrayList<Enemy> enemies = new ArrayList<>(); //arraylist that stores enemies
    private int best = 0,lifelineTimer = 0;
    private final Random rngEvent = new Random(); //rng used for spawning enemies and choosing their elevation
    private final static Paint text = new Paint();
    static final HashMap<String, Integer> scoreStore = new HashMap<>(); //hashmap with level name and score achieved
    /**
     *pass context to superclass (SurfaceView)
     * add a callback to the GameView object
     */
    GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
    }
    /**
     * setup background and player using the bitmap resource
     * setup projectile and enemies array lists
     * set the thread to start
     */
    public void surfaceCreated(SurfaceHolder holder){
        text.setTextSize(screenHeight/20); //setup text for OSD
        gamethread = new GameThread(getHolder(), this);
        gamethread.startup(); // start thread
        gamethread.start();//start thread
    }
    /**
     * shutdown thread, if it's not null, try joining
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        boolean retry = true;
        if(gamethread!=null) {
            gamethread.shutdown();
        }
        while (retry) {
            try {
                if(gamethread!=null) {
                    gamethread.join();
                }
                retry = false;
            } catch (Exception e) {
                gamethread = null;
            }
        }
    }
    @Override
    public void surfaceChanged(SurfaceHolder theHolder, int form, int w, int h){}
    /**
     * receive touch events and act accordingly
     * if move event is detected, start playing game
     * if game is playing, move craft to the event location
     * /2 because we don't want the craft to jump to the touch location
     */
    public boolean onTouchEvent(MotionEvent event)
    {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    if (!player.isPlaying && !hasStarted && reset) {
                        player.isPlaying = true; //start the game if not already playing
                    }
                    if (player.isPlaying) {
                        if (!hasStarted) {
                            hasStarted = true;
                        }
                        reset = false;
                        player.y = ((int) event.getY() + player.y) / 2; //moves the player slowly
                        player.x = ((int) event.getX() + player.x) / 2; //doesn't teleport player on press
                    }
            }
        return true;
    }
    /**
     * main method, check the bounds of the player, update score
     * fire enemy and player missiles,check for collision,
     * remove off-screen projectiles
     */
    void updateGameView() {
            if (player.isPlaying) {
                background.updateBg(); //change BG position
                if (player.y <= 0 || player.y >= screenHeight || player.x <= 0|| player.x >= screenWidth){
                    player.isPlaying = false; //end game
                }
                player.score++;
                updateEnemies();
                firePlayerMissiles();
                fireLifeline();

                if(isHoming) {//if homing missiles are enabled
                    fireHomingMissile();
                }
                for (Projectile p : projectiles) { //use enhanced loop for speed
                    p.x -= p.speed;//update projectile

                    if (p.isHoming && (p.homingTime < p.homingTimeout)) {
                        p.y = player.y; //if a projectile is homing, track player y coordinates
                        p.homingTime++; //add to the time it has been homing
                    } else {
                        p.isHoming = false; //timeout the missile
                    }
                    for (Enemy e : enemies) {
                        if (collisionCheck(p, e) && p.isPlayerMissile) { //check if projectile has hit enemy
                            if (e.lives <= 1) {//if the projectile damage would destroy
                                player.score += (e.missileDam*100);
                                enemies.remove(e);
                                projectiles.remove(p);//remove projectile
                                break;
                            } else {
                                e.lives -= 1; //take away missile damage
                                e.speed = -e.speed; //reverse direction upon hit
                                projectiles.remove(p);//remove projectile
                                break;
                            }
                        }
                        if (collisionCheck(player, e)) {//check if player has hit enemy
                            enemies.remove(e); //remove enemy
                            player.isPlaying = false; //disable player
                            break;
                        }
                    }
                    if (collisionCheck(p, player)) {//if there's a collision between player and projectile
                        if (!p.isPlayerMissile) { //if it's not a player missile
                            if (p.isLife) { //if it's a life
                                player.lives++;//add to player lives
                            } else if (player.lives - p.missileDamage <= 0) {
                                player.isPlaying = false;//if the projectile will kill the player, game over
                                break;
                            } else {
                                player.lives -= p.missileDamage;//decrement lives
                            }
                            projectiles.remove(p); //remove the projectile
                            break;
                        }
                    }
                    if (p.x < -100 || p.x > screenWidth) {
                        projectiles.remove(p); //remove if off-screen
                        break;
                    }
                }
            } else { //if player is not playing
                if (!reset) {
                    newGame = false;
                    startReset = System.nanoTime();
                    reset = true;
                }
                long resetTimeElapsed = (System.nanoTime() - startReset) / 1000000;
                if (resetTimeElapsed > 2500 && !newGame) {
                    startNewGame(); //make a new game
                }
            }
        }
    /**
     * scale up the canvas, save its state and draw
     * enemies, projectiles, the background and the player
     */
    @Override
    public void draw(Canvas canvas)
    {
            super.draw(canvas);
            final float scaleX = getWidth() / (screenWidth * 1.f);
            final float scaleY = getHeight() / (screenHeight * 1.f);
            if (canvas != null) {
                canvas.scale(scaleX, scaleY); //scale up canvas
                final int savedState = canvas.save();
                background.draw(canvas); //draw BG
                player.draw(canvas); //draw player
                for (Enemy m : enemies) {
                    m.draw(canvas); //draw all enemies
                }
                for (Projectile m : projectiles) {
                    m.draw(canvas);//draw missiles
                }
                drawStats(canvas);//draw text onscreen
                canvas.restoreToCount(savedState);
            }
    }
    /**
     * provide overlay for the user, draw lives,score,best score and instructions
     */
    private void drawStats(Canvas canvas) { //draw attributes onto screen
        canvas.drawText("Lives: " + player.lives,10,screenHeight/16,text);
        canvas.drawText("Score: " + player.score, 10, screenHeight - 10, text);
        canvas.drawText("Top Score: " + best, screenWidth - (screenWidth/3), screenHeight - 10, text);
        if(!hasStarted){//if game hasn't started, provide instructions
            canvas.drawText("Drag the aircraft to play", screenWidth/2, screenHeight/2, text);
        }
    }
    /**
     * return boolean result (if two game entity's rectangles collide)
     */
    private boolean collisionCheck(GameEntity a, GameEntity b) {
        return Rect.intersects(a.getRectangle(), b.getRectangle()); //collision between two objects of type GameEntity
    }
    /**
     * fire homing missiles if their timer is greater than the spawn timer
     */
    private void fireHomingMissile(){
        for (Enemy m: enemies){
            if (m.homingTime > homingMissileTimer) {//add at player y coordinates
                projectiles.add(new Projectile(super.getContext(), screenWidth,
                        player.y, player.score, 1,1,false,true,false));
                m.homingTime = 0;//reset their homing timer
            }else{m.homingTime++;}//otherwise increment the time
        }
    }
    /**
     * fire a life line if one is due (timer is larger than the fire time)
     * fire at random height and at the screen width (other end of map)
     */
    private void fireLifeline(){
        if (lifelineTimer > lifelineFireTime) {//add at random height
            projectiles.add(new Projectile(super.getContext(),screenWidth,
                    rngEvent.nextInt(screenHeight), player.score, 1,0,true,false,false));
            lifelineTimer = 0;
        }else{lifelineTimer++;} //else increment
    }
    /**
     * fire player missile if the character's fire time is adequate
     */
    private void firePlayerMissiles(){
        if (player.playerFireTime > playerFireTime) {
            projectiles.add(new Projectile(super.getContext(), player.x + player.width,
                    player.y, player.score, -1,1,false,false,true)); //add at player coordinates
            player.playerFireTime = 0;
        }else{player.playerFireTime++;}
    }
    /**
     * if there isn't enough enemies on-screen, spawn another,
     * loop through enemies, if their missile timer is adequate
     * fire a missile. Reverse enemy directions if they hit top/bottom of screen
     */
    private void updateEnemies() {
        if (enemies.size() < noEnemiesOnScreen) {
            switchEnemyType(); //decide what enemy to spawn next
        }
        for (Enemy m : enemies) {
            if (m.missileTimer >= enemyFireTime) {
                projectiles.add(new Projectile(super.getContext(), m.x - m.width,
                        m.y, player.score, 1, m.missileDam, false, false, false)); //add enemy missile at its position
                m.missileTimer = 0; //reset timer
            }else{m.missileTimer++;} //otherwise increment the timer

            if (m.y + m.height >= screenHeight) {
                m.speed = -1; //reverse directions
            } else if (m.y <= 0) {
                m.speed = 1;
            }
            m.y += m.speed; //change their position
        }
    }
    /**
     * if the hard enemy chance is greater than a random between 0-100
     * spawn a hard enemy, otherwise use math.random to generate a random
     * give a 50/50 chance of spawning a (medium or easy enemy)
     */
    private void switchEnemyType(){
        if(hardChance >= rngEvent.nextInt(100)) { //if it's greater than the random
            enemies.add(new Enemy(BitmapFactory.decodeResource(getResources(), R.drawable.enemyhard), rngEvent.nextInt(screenHeight),3));///spawn hard enemy
        }else{
            if (Math.random() >= 0.5){ //otherwise generate 50/50 chance
                enemies.add(new Enemy(BitmapFactory.decodeResource(getResources(), R.drawable.enemyeasy), rngEvent.nextInt(screenHeight),1));//spawn easy enemy
            }else{
                enemies.add(new Enemy(BitmapFactory.decodeResource(getResources(), R.drawable.enemymed), rngEvent.nextInt(screenHeight),2));//spawn medium enemy
            }
        }
    }
    /**
     * start new game, update player score, put map name and score in hashmap
     * clear arrays to projectiles and enemies, reset player position, reinstate lives
     */
    private void startNewGame()
    {
        if(player.score>best)//if score is greater than previous best
        {
            best = player.score; //set best as new score
            scoreStore.put(super.getContext().getResources().getResourceEntryName(map), best); //update value in hashmap
        }
        player.lives =  Intermediary.pubLivesID; //reinstate lives
        projectiles.clear();
        enemies.clear();//clear projectile and enemy arrays
        player.resetScoreAndPos();//player is put in the center of the bg
        newGame = true;
        hasStarted = false;
    }
}