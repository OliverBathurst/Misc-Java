package uk.ac.reading.ft025024.fluct;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class LevelSelection extends Activity implements AdapterView.OnItemSelectedListener{
    private Intermediary inter;
    private NetworkHandler net;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_selection);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final Button play = (Button) findViewById(R.id.playgame);
        final Button mapDetails = (Button) findViewById(R.id.mapDetails);
        final Button cust = (Button) findViewById(R.id.customLvl);
        final TextView user = (TextView) findViewById(R.id.userName);
        final ImageSwitcher sw = (ImageSwitcher) findViewById(R.id.imgsw); //initialise buttons and image switchers
        final Button refresh = (Button) findViewById(R.id.refresh);
        final Button delete = (Button) findViewById(R.id.delete);
        final Button nextMap = (Button) findViewById(R.id.nextMap);
        final Button prevMap = (Button) findViewById(R.id.prevMap); //the two buttons for cycling levels
        final Spinner spin = (Spinner) findViewById(R.id.spinner2);
        final ArrayAdapter<String> arrAdapt  = new ArrayAdapter<>(LevelSelection.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.names));
        arrAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(arrAdapt); //set adapter for drop down
        spin.setOnItemSelectedListener(this); //make a new listener

        user.setText(MainMenu.username);//display user name
        inter = new Intermediary(this); //new intermediary object for xml tasks
        net = new NetworkHandler();
        copyLevelsToFile();
        refreshLevels();

        sw.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                return imageView;
            }
        });
        nextMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//get next index (if greater than no.levels, it'll return 0)
                inter.getNextIndex();
                sw.setImageResource(Intermediary.pubMapID);
            }
        });
        prevMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inter.getPrevIndex();
                sw.setImageResource(Intermediary.pubMapID);
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//get next index (if greater than no.levels, it'll return 0)
                refreshLevels();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v){
                deleteLevel();
                sw.setImageResource(Intermediary.pubMapID);
            }
        });
        mapDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//get next index (if greater than no.levels, it'll return 0)
                inter.mapSetup();
            }
        });
        cust.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v){
                startActivity(new Intent(LevelSelection.this, CustomLevels.class));
            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v){
                playGame();
            }
        });
      nextMap.performClick();
    }
    /**
     * setup variables for play and start game activity
     */
    private void playGame(){
        inter.setup();
        startActivity(new Intent(LevelSelection.this, Game.class));//start the game
    }
    /**
     * call method to copy the levels xml file to internal storage
     * otherwise, read from the raw directory in worst case scenario
     */
    private void copyLevelsToFile(){
        try {
            inter.copyLevelsToInternal();
        }catch (Exception e){
            Toast toast = Toast.makeText(this, "Cannot copy to storage, reading from assets" + e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
            try{
                inter.readFromRaw();
            }catch (Exception f){
                Toast toast2 = Toast.makeText(this, "Cannot read from assets, reinstall" + f.getMessage(), Toast.LENGTH_SHORT);
                toast2.show();
            }
        }
    }
    /**
     * delete level at the current index position
     */
    private void deleteLevel(){
        try{
            inter.deleteCurrLevel();///delete whole level here
        }catch(Exception e){
            Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    /**
     * re-read levels by emptying level store and reading from file again
     */
    private void refreshLevels(){
        try {
            inter.readLevels();
            inter.setup();
            Toast toast = Toast.makeText(getApplicationContext(), "Refreshing levels", Toast.LENGTH_SHORT);
            toast.show();
        }catch(Exception e){
            Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    /**
     * check if network is available and if so, call method on NetworkHandler object to send results
     */
    private void startUpload(){
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Upload");
                builder.setMessage("Upload scores to leaderboard?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(net.isNetworkAvailable(LevelSelection.super.getApplicationContext())) {
                            try {
                                Toast toast = Toast.makeText(getApplicationContext(), net.execSendLead(), Toast.LENGTH_SHORT);
                                toast.show();
                            } catch (Exception e) {
                                Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }else{
                            Toast toast = Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }catch (Exception e){
                Toast toast = Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
                toast.show();
            }
    }
    /**
     * provide a drop down box with three options, when an item is selected,
     * perform action based on it
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(position){
            case 1:
                listLevels(); //list levels
                break;
            case 2:
                startUpload(); //upload levels
                break;
            case 3:
                downloadAdditionalLevels();//download new levels
                break;
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    /**
     * create a new object of network handler, check if there's internet
     * if there is, execute the download to download the xml document online
     * then pass it to the method that reads documents
     */
    private void downloadAdditionalLevels() {
        if (net.isNetworkAvailable(this)) {
            try {
                inter.readDownloadedLevels(net.execDownload());
            } catch (Exception e) {
                Toast toast = Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
                toast.show();
            }
        }else{
            Toast toast = Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    /**
     * calls list all levels method
     */
    private void listLevels(){
        try {
            inter.listAllLevels();
        }catch (Exception e){
            Toast toast = Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    protected void onDestroy(){
        super.onDestroy();
        inter = null;
        net = null;
    }
}