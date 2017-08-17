package uk.ac.reading.ft025024.fluct;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.animation.*;
import android.widget.TextView;

public class Credits extends Activity {
    private MediaPlayer music;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        TextView paragraph = (TextView) findViewById(R.id.creditPara);
        music = MediaPlayer.create(this, R.raw.credits);
        music.setLooping(true); //loop the music
        android.view.animation.Animation anim = AnimationUtils.loadAnimation(this, R.anim.creditanim);
        paragraph.startAnimation(anim); //start scrolling credits animation
    }
    /**
     * UI methods
     */
    protected void onStart() {
        super.onStart();  // Always call the superclass method first
        try {
            music.prepare(); //prepare and start the music
            music.start();
        }catch(Exception ignored){}
    }
    protected void onPause() {
        super.onPause();  // Always call the superclass method first
        if (music.isPlaying()){ //if the music is playing pause it
            music.pause();
        }
    }
    protected void onResume() {
        super.onResume();  // Always call the superclass method first
        music.start(); //start it if it's paused
    }
    protected void onStop() {
        super.onStop();
        music.stop(); //stop and release the music for freeing memory
    }
    protected void onDestroy() {
        super.onDestroy();
        music.stop(); //stop and release the music for freeing memory
        music.reset();//reset the mediaPlayer, new requirement
        music.release();
    }
}
