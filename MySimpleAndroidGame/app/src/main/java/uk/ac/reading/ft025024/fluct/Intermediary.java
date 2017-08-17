package uk.ac.reading.ft025024.fluct;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;
import java.util.ArrayList;

class Intermediary extends XMLEditor {
    static boolean homingMissilesEnabled = false; //if homing missiles are allowed, initialised to false
    static int pubMapID,pubLivesID,pubCharID,maxOnScreen,
            enemyFireTime,lifelineFireTime,playerFireTime, homingMissileTimer,
            enemyLives, hardChance; //static ints that'll be used to setup in GameView
    private int count = 0; //not static (customlevel class and levelselection class need their own counters)
    /**
     * pass context as an argument to the constructor, (needed to find resources and make toasts)
     */
    Intermediary(Context c) {
        super(c);
    }
    /**
     * sets the static members up with the attributes stored in the loaded level arraylist object at position count
     */
    void setup(){
        pubMapID = appContext.getResources().getIdentifier(levelsList.get(count).mapName, "drawable", appContext.getPackageName());
        pubCharID = appContext.getResources().getIdentifier(levelsList.get(count).characName, "drawable", appContext.getPackageName());
        pubLivesID = levelsList.get(count).lives;
        homingMissilesEnabled = levelsList.get(count).homingMissiles;
        maxOnScreen = levelsList.get(count).maxOnScreen;
        enemyFireTime = levelsList.get(count).enemyFireInt;
        enemyLives = levelsList.get(count).enemyLives;
        homingMissileTimer = levelsList.get(count).homingTimeInt;
        playerFireTime = levelsList.get(count).playerFireTimeInt;
        lifelineFireTime = levelsList.get(count).lifelineFireTimeInt;
        hardChance = levelsList.get(count).hardEnemyInt;
    }
    /**
     * display basic info about the level at the level index count
     */
    void mapSetup(){
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(appContext);
            builder.setTitle("Level Details");
            builder.setMessage("Map Title: " + levelsList.get(count).mapName + "\n"
                    + "Craft Title: " + levelsList.get(count).characName + "\n"
                    + "Level Name: " + levelsList.get(count).level + "\n" + "Lives: "
                    + levelsList.get(count).lives + "\n"
                    + "Homing Missiles Enabled? " + levelsList.get(count).homingMissiles + "\n"
                    + "Max Enemies On-Screen: " + levelsList.get(count).maxOnScreen + "\n"
                    + "Enemy Lives: " + levelsList.get(count).enemyLives);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }catch (Exception e){
            Toast toast = Toast.makeText(appContext, e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    /**
     * make a message box, the contents will be the list levels string
     */
    void listAllLevels(){
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(appContext);
            builder.setTitle("Available Levels");
            builder.setMessage(listLevels());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }catch (Exception e){
            Toast toast = Toast.makeText(appContext, e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    /**
     * return a string of all the levels in the array's level names
     */
    private String listLevels (){
        String a = "";
        for (LoadedLevel level: levelsList){//enhanced for loop
            a += level.level + "\n"; //add the next level and new line
        }
        return a;
    }
    /**
     * get the next index in the array
     */
    void getNextIndex(){
        if(count>=(levelsList.size()-1)){ //if it's at the end of the level array
            count = 0; //set it back to the start
        }else{
            count++; //otherwise increment the counter and return
        }
        setup();
    }
    /**
     * get the previous index in the array
     */
    void getPrevIndex(){
        if(count<=0){ //if it's at the first item
            count = (levelsList.size()-1); //wraparound to the last item's index
        }else{
            count--; //otherwise decrement and return that index
        }
        setup(); //setup the variables so that the game can be started
    }
    /**
     * try deleting a level at the given position
     * if it's successful, reset the counter and setup the variables
     */
    void deleteCurrLevel(){
        try{
            if (deleteLevel(count)){
                count = 0; //if it's successful, reset counter to start
                setup();
                Toast toast = Toast.makeText(appContext, "Deleted level", Toast.LENGTH_SHORT);
                toast.show();
            }
        }catch(Exception e){
            Toast toast = Toast.makeText(appContext, "Cannot delete level, is it saved?", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    /**
     * write a level in the arraylist position to file
     */
    void write(int pos){
        try{
            writeLevel(pos);
        }catch (Exception e){
            Toast toast = Toast.makeText(appContext, e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    /**
     * set the private member count to a new value
     */
    void setCount(int newCount){
        this.count = newCount;
    }
    /**
     * return the levels arraylist for adding to
     */
    @SuppressWarnings("SameReturnValue")
    ArrayList<LoadedLevel> getLevelList(){
        return levelsList;
    }
}
