package uk.ac.reading.ft025024.fluct;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import org.w3c.dom.Document;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

class NetworkHandler{
    private final String user = MainMenu.username; //the username of player
    private final HashMap score = GameView.scoreStore; //import the static hashmap of level names and scores
    /**
     * returns leaderboard as a formatted string of results
     * encapsulation occurs as it returns result from a private inner class
     */
    String execGetLead() throws Exception{
        return new getTask().execute().get(30000, TimeUnit.MILLISECONDS);//specify a decent timeout
    }
    /**
     * throws an exception if the hashmap is empty (no games have been played)
     * otherwise sends leaderboard results and returns the server response to the user
     */
    String execSendLead() throws Exception{
        if(score.size()==0){
            throw new Exception("Scores are empty");
        }else {
            return new sendTask().execute().get(30000, TimeUnit.MILLISECONDS);
        }
    }
    /**
     * create a new object and execute the download class' method
     * this returns the online xml document to the calling method for reading
     */
    Document execDownload() throws Exception{
        return new downloadMaps().execute().get(30000, TimeUnit.MILLISECONDS);
    }
    /**
     * checks if there is a active internet connection
     * true if present, false otherwise
     */
    boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectManager.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.isConnected();
    }
    /**
     * gets the leaderboard from php and formats it, returns it as a string
     */
    @SuppressWarnings("WeakerAccess")
    class getTask extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... params) {
            String jsonStr;
            String back = "Level | User | Score \n";
            try {
                URL url = new URL("https://mygame12345.000webhostapp.com/getscores.php"); //scores URL
                HttpURLConnection http = (HttpURLConnection)url.openConnection(); //open url connection
                InputStream iStream = http.getInputStream(); //get input stream from connection
                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
                StringBuilder strBuild = new StringBuilder();
                while ((jsonStr = br.readLine()) != null) {
                    strBuild.append(jsonStr).append("\n"); //append each line from server
                }
                final String[] splitEntries = strBuild.toString().split(" "); //split each line
                final StringBuilder formatStr = new StringBuilder();
                for (int i = 0; i < splitEntries.length; i++) {
                    if ((i > 0) && (0 == (i % 3))) {
                        formatStr.append('\n'); //apply new line each triple (name,level,score)
                    }
                    formatStr.append(splitEntries[i]);
                    if (i != (splitEntries.length - 1)) {
                        formatStr.append(' ');
                    }
                }
                back += formatStr.toString();
                br.close(); //close reader
                iStream.close();//close input stream
            } catch (Exception ignored) {}
            return back; //return string
        }
    }
    /**
     * iterates through the hashmap and sends each result in a modified URL
     * sends name, score and level, reads the server response and notifies the user
     */
    @SuppressWarnings("WeakerAccess")
    class sendTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String response ="";
            try {
                Iterator it = score.entrySet().iterator();
                while (it.hasNext()) { //iterate through map
                    Map.Entry pair = (Map.Entry)it.next();
                    String url = "https://mygame12345.000webhostapp.com/savescores.php?name="+user+"&levelname="
                            + pair.getKey() + "&score="+pair.getValue(); //create modified URL
                    URL a = new URL(url); //create new URL
                    HttpURLConnection connection = (HttpURLConnection) a.openConnection();//open connection
                    connection.setDoOutput(true);
                    connection.setRequestMethod("GET"); //make a get request
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("charset", "UTF-8");
                    InputStream os = connection.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder sb = new StringBuilder(); //get input stream and make a string builder
                    String line;

                    while((line = reader.readLine()) != null)
                    {
                         sb.append(line).append("\n");//read server response and append new line
                    }
                    os.close();//close stream
                    response = sb.toString();
                    connection.disconnect(); //disconnect from url
                    it.remove(); // avoids a ConcurrentModificationException
                }
            } catch (Exception ignored) {}
            return response;
        }
    }
    /**
     * downloads xml maps from the url provided, create document from the input stream
     */
    @SuppressWarnings("WeakerAccess")
    class downloadMaps extends AsyncTask<String, Document, Document> {
        @Override
        protected Document doInBackground(String... params) {
            Document response = null;
            try {
                DocumentBuilderFactory dbF = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbF.newDocumentBuilder();//create document builder to parse xml
                InputStream is = new URL("https://mygame12345.000webhostapp.com/additionallevels.xml").openStream();//open stream to xml URL
                response = db.parse(is); //parse the input stream
                is.close();//close
            }catch(Exception ignored){}
            return response;
        }
    }
}