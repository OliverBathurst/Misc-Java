package uk.ac.reading.ft025024.fluct;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.Random;

public class CustomLevels extends Activity {
    private final String[] mapNames = {"clouds", "fields", "sea"}; //available maps and crafts
    private final String[] craftNames = {"thunder","mig","harrier"}; //available crafts
    private int mapCount = 0, craftCount = 0; //initialise array indexes
    private final Random rng = new Random(); //random for rng
    private SeekBar playerFireTimeSeek,lifeFreqSeekBar,homingTimeoutSeekBar,
            enemyFireFreqSeekBar; //the seek bars
    private TextView hardPercent,enemLives,livesInt,maxInt; //text entry
    private CheckBox checkHoming;
    private Intermediary inter; //intermediary object

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_levels);
        final ImageSwitcher sw = (ImageSwitcher) findViewById(R.id.imageSwitcher);
        final ImageSwitcher craftsw = (ImageSwitcher) findViewById(R.id.craftSwitch);
        final Button nxtMp = (Button) findViewById(R.id.nextMap);
        final Button prvMp = (Button) findViewById(R.id.prevMap);
        final Button randomise = (Button) findViewById(R.id.rand);
        final Button nxtCraft = (Button) findViewById(R.id.nextCraft);
        final Button prvCraft = (Button) findViewById(R.id.prevCraft);
        final Button plyMp = (Button) findViewById(R.id.play);
        final Button savButton = (Button) findViewById(R.id.sav);
        checkHoming = (CheckBox) findViewById(R.id.homing);
        hardPercent = (TextView) findViewById(R.id.hardEnemChance);
        enemLives = (TextView) findViewById(R.id.enemLivesText);
        livesInt = (TextView) findViewById(R.id.livesInt);
        maxInt = (TextView) findViewById(R.id.maxInt);
        playerFireTimeSeek = (SeekBar) findViewById(R.id.playerFireSeekBar);
        lifeFreqSeekBar = (SeekBar) findViewById(R.id.lifeFreqSeekBar);
        homingTimeoutSeekBar = (SeekBar) findViewById(R.id.homingTimeoutSeekBar);
        enemyFireFreqSeekBar = (SeekBar) findViewById(R.id.enemyFireFreqSeekBar);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        inter = new Intermediary(this); //pass context to intermediary object for xml etc

        sw.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_XY); //image switch
                return imageView;
            }
        });
        craftsw.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);//craft image switch
                return imageView;
            }
        });
        plyMp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playGame();
            }
        });
        nxtMp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapCount == (mapNames.length -1)){//if at end of array
                    mapCount = 0; //wraparound to front
                    sw.setImageResource(getResources().getIdentifier(mapNames[mapCount], "drawable", getPackageName()));
                }else{
                    mapCount++; //increment array index
                    sw.setImageResource(getResources().getIdentifier(mapNames[mapCount], "drawable", getPackageName()));
                }
            }
        });
        prvMp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapCount == 0){//if at start
                    mapCount = mapNames.length -1;//wraparound to end
                    sw.setImageResource(getResources().getIdentifier(mapNames[mapCount], "drawable", getPackageName()));
                }else{
                    mapCount--;//decrement array index
                    sw.setImageResource(getResources().getIdentifier(mapNames[mapCount], "drawable", getPackageName()));
                }
            }
        });
        nxtCraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (craftCount == (craftNames.length -1)){
                    craftCount = 0;
                    craftsw.setImageResource(getResources().getIdentifier(craftNames[craftCount], "drawable", getPackageName()));
                }else{
                    craftCount++;
                    craftsw.setImageResource(getResources().getIdentifier(craftNames[craftCount], "drawable", getPackageName()));
                }
            }
        });
        prvCraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (craftCount == 0){
                    craftCount = craftNames.length -1;
                    craftsw.setImageResource(getResources().getIdentifier(craftNames[craftCount], "drawable", getPackageName()));
                }else{
                    craftCount--;
                    craftsw.setImageResource(getResources().getIdentifier(craftNames[craftCount], "drawable", getPackageName()));
                }
            }
        });
        randomise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapCount = rng.nextInt(mapNames.length-1);
                craftCount = rng.nextInt(craftNames.length -1);
                craftsw.setImageResource(getResources().getIdentifier(craftNames[craftCount], "drawable", getPackageName()));
                sw.setImageResource(getResources().getIdentifier(mapNames[mapCount], "drawable", getPackageName()));
                playerFireTimeSeek.setProgress(rng.nextInt(100));
                lifeFreqSeekBar.setProgress(rng.nextInt(100));
                homingTimeoutSeekBar.setProgress(rng.nextInt(100));
                enemyFireFreqSeekBar.setProgress(rng.nextInt(100));
                hardPercent.setText(String.valueOf(rng.nextInt(100)));
                enemLives.setText(String.valueOf(rng.nextInt(10)+1));
                livesInt.setText(String.valueOf(rng.nextInt(10)+1));
                maxInt.setText(String.valueOf(rng.nextInt(7)+3));
                if(Math.random() > 0.5){
                    checkHoming.performClick();
                }
            }
        });
        savButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(CustomLevels.this);
                View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog_box, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(CustomLevels.this);
                alertDialogBuilderUserInput.setView(mView);

                final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
                alertDialogBuilderUserInput.setCancelable(false).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                            if (userInputDialogEditText.getText().toString().trim().length() == 0){
                                Toast toast = Toast.makeText(CustomLevels.this, R.string.levelNotEntered, Toast.LENGTH_SHORT);
                                toast.show();
                            }else if (userInputDialogEditText.getText().toString().trim().length() > 10){
                                Toast toast = Toast.makeText(CustomLevels.this, R.string.levelTooLong, Toast.LENGTH_SHORT);
                                toast.show();
                            }else{
                                if(checkInput()) {
                                    inter.getLevelList().add(new LoadedLevel(userInputDialogEditText.getText().toString().trim(),
                                            checkHoming.isChecked(), mapNames[mapCount], craftNames[craftCount], Integer.parseInt(maxInt.getText().toString().trim()),
                                            Integer.parseInt(livesInt.getText().toString().trim()), Integer.parseInt(enemLives.getText().toString().trim()), 30+enemyFireFreqSeekBar.getProgress(),
                                            150+homingTimeoutSeekBar.getProgress(), 30+playerFireTimeSeek.getProgress(), 300+lifeFreqSeekBar.getProgress(), Integer.parseInt(hardPercent.getText().toString().trim())));
                                    inter.write(inter.getLevelList().size() - 1);
                                    inter.getLevelList().remove(inter.getLevelList().size() - 1);
                                    dialogBox.dismiss();
                                }else{
                                    dialogBox.dismiss();
                                }
                            }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        dialogBox.dismiss();
                    }
                });
                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();
            }
        });
        nxtMp.performClick();
        nxtCraft.performClick();
    }
    /**
     *check if trimmed inputs are empty, are 0, or do not parse correctly
     * returns false if verification fails
     */
    private boolean checkInput(){
        boolean success;
        try {
            if (maxInt.getText().toString().trim().length() == 0 || Integer.parseInt(maxInt.getText().toString().trim()) > 10
                    || Integer.parseInt(livesInt.getText().toString().trim()) <= 0
                    || livesInt.getText().toString().trim().length() == 0 || Integer.parseInt(enemLives.getText().toString().trim()) <= 0
                    || enemLives.getText().toString().trim().length() == 0 || Integer.parseInt(hardPercent.getText().toString().trim())>100) {
                Toast toast = Toast.makeText(CustomLevels.this, "Either max on-screen is >10 or a vital field is 0", Toast.LENGTH_SHORT);
                toast.show();
                success = false;
            } else {
                success = true;
            }
        }catch(Exception e){
            Toast toast = Toast.makeText(CustomLevels.this, e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
            success = false;
        }
        return success;
    }
    /**
     * check the input, if good, add the level to the arraylist, set the count up and play
     * add arbitrary values to the seek bar (in case the user tries to play a game with seek bar values of 0)
     * e.g. if a game was started with the seek bar for player fire time at 0, the player missiles would never fire.
     */
    private void playGame(){
        if(checkInput()) {
            inter.getLevelList().add(new LoadedLevel("Temp", checkHoming.isChecked(), mapNames[mapCount],
                craftNames[craftCount], Integer.parseInt(maxInt.getText().toString().trim()), Integer.parseInt(livesInt.getText().toString().trim()),
                Integer.parseInt(enemLives.getText().toString().trim()), 30+enemyFireFreqSeekBar.getProgress(), 150+homingTimeoutSeekBar.getProgress(),
                30+playerFireTimeSeek.getProgress(), 300+lifeFreqSeekBar.getProgress(), Integer.parseInt(hardPercent.getText().toString().trim())));
            //add the new level to the array
            inter.setCount(inter.getLevelList().size() - 1); //set the index to load from as the new level's index
            inter.setup();//setup (set the variables up)
            inter.getLevelList().remove(inter.getLevelList().size() - 1); //remove level
            startActivity(new Intent(CustomLevels.this, Game.class));//start the game
        }
    }
}
