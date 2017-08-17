package uk.ac.reading.ft025024.fluct;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenu extends Activity {
    static String username; //make public for leaderboard
    private NetworkHandler net;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final TextView userName = (TextView) findViewById(R.id.editText);
        final TextView status = (TextView) findViewById(R.id.status);
        final Button credits = (Button) findViewById(R.id.Credits);
        final Button web = (Button) findViewById(R.id.Website);
        final Button play = (Button) findViewById(R.id.Play);
        final Button leaderboard = (Button) findViewById(R.id.leader);
        net = new NetworkHandler();

        play.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (userName.getText().toString().trim().length() == 0) {
                    status.setText(R.string.noUser);
                } else if (userName.getText().toString().trim().length() > 10) {
                    status.setText(R.string.userTooLong);
                } else {
                    username = userName.getText().toString(); //store the username
                    startActivity(new Intent(MainMenu.this, LevelSelection.class));
                }
            }
        });
        credits.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainMenu.this, Credits.class)); //start credits activity
            }
        });
        web.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://oliverbathurst.wordpress.com/")));
            }
        });
        leaderboard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setupLeader();
            }
        });
    }
    protected void onDestroy() {
        super.onDestroy();
        username = null;
        net = null;
    }
    /**
     * make new network handler object and call the method to get the leaderboard
     * get the server response back as a string and print to the user
     */
    private void setupLeader(){
        if(net.isNetworkAvailable(MainMenu.this)) {
            try {
                String toPrint = net.execGetLead();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Leaderboard");
                builder.setMessage(toPrint);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } catch (Exception e) {
                Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT);
                toast.show();
            }
        }else{
            Toast toast = Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}