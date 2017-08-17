package bathurst.oliver.wifilogger;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Main extends Activity {

    private TextView wifiName, wifiFreq, wifiLink, wifiStrength, logBox;
    private String currentName;
    private int currentFreq, currentLink, currentStrength, selectedForDeletion = 0;
    private ListView lv;
    private ArrayList<Entry> entryArray;
    private ArrayList<String> listArray;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Button refresh = (Button) findViewById(R.id.refresh);
        Button delete = (Button) findViewById(R.id.del);
        Button add = (Button) findViewById(R.id.add);
        Button sav = (Button) findViewById(R.id.sav);
        Button upload = (Button) findViewById(R.id.upload);
        Button exit = (Button) findViewById(R.id.exit);

        logBox = (TextView) findViewById(R.id.log);
        wifiName = (TextView) findViewById(R.id.wifiName);
        wifiFreq = (TextView) findViewById(R.id.frequency);
        wifiLink = (TextView) findViewById(R.id.linkSpeed);
        wifiStrength = (TextView) findViewById(R.id.signalStrength);
        lv = (ListView) findViewById(R.id.list);

        entryArray = new ArrayList<>();
        listArray = new ArrayList<>();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected() && !wifiName.getText().toString().equals(getString(R.string.noData))){
                    entryArray.add(new Entry(currentName, currentFreq, currentLink, currentStrength));
                    listArray.add(currentName + " , " + currentFreq + " , " + currentLink + " , " + currentStrength);
                    updateList();
                    updateLog();
                }else{
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listArray.remove(selectedForDeletion);
                entryArray.remove(selectedForDeletion);
                updateList();
                updateLog();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });
        sav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStats();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Selected index: " + position, Toast.LENGTH_SHORT).show();
                selectedForDeletion = position;
            }
        });

        updateStats();

        handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){
                updateStats();
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }
    private void updateStats(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_WIFI_STATE}, 1);
        }else{
            final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiManager.isWifiEnabled() && wifiInfo.getNetworkId() != -1) {
                currentName = wifiInfo.getSSID();
                currentFreq = wifiInfo.getFrequency();
                currentLink = wifiInfo.getLinkSpeed();
                currentStrength = wifiInfo.getRssi();
                wifiName.setText(String.format("%s%s", getString(R.string.ssid), currentName));
                wifiFreq.setText(String.format("%s%s%s", getString(R.string.freq), currentFreq, getString(R.string.mega)));
                wifiLink.setText(String.format("%s%s%s", getString(R.string.link), currentLink, getString(R.string.megaBits)));
                wifiStrength.setText(String.format("%s%s%s", getString(R.string.sigStr), currentStrength, getString(R.string.strength)));
            } else {
                Toast.makeText(getApplicationContext(), "Not connected to WiFi", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void updateList(){
        lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listArray));
    }
    private void updateLog(){
        String toWrite = "";
        for(Entry i : entryArray){
            toWrite += "WiFi name: " + i.returnName() + " Frequency: " + i.returnFrequency()
                    + " Link Speed: " + i.returnLinkSpeed() + " Signal Strength: " + i.returnStrength() + "\n";
        }
        logBox.setText(toWrite);
    }
    private boolean isConnected(){
        final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiManager.isWifiEnabled() && wifiInfo.getNetworkId() != -1;
    }
    private void upload(){
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("*/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/" + "output.xls")));
        startActivity(Intent.createChooser(sharingIntent, "Upload"));
    }
    private void save(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }else{
            Toast.makeText(getApplicationContext(), "Saving", Toast.LENGTH_SHORT).show();
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("results");

            HSSFRow def = sheet.createRow(0);
            def.createCell(0).setCellValue("WiFi Name");
            def.createCell(1).setCellValue("Frequency");
            def.createCell(2).setCellValue("Link Speed");
            def.createCell(3).setCellValue("Strength");

            for (int i = 0; i < entryArray.size(); i++) {
                HSSFRow row = sheet.createRow(i + 1);
                HSSFCell cell = row.createCell(0);
                cell.setCellValue(entryArray.get(i).returnName());
                HSSFCell cell1 = row.createCell(1);
                cell1.setCellValue(entryArray.get(i).returnFrequency());
                HSSFCell cell2 = row.createCell(2);
                cell2.setCellValue(entryArray.get(i).returnLinkSpeed());
                HSSFCell cell3 = row.createCell(3);
                cell3.setCellValue(entryArray.get(i).returnStrength());
            }
            try {
                File f = new File(Environment.getExternalStorageDirectory(), "output.xls");
                FileOutputStream out = new FileOutputStream(f);
                workbook.write(out);
                out.close();
                Toast.makeText(getApplicationContext(), "Saved: " + f.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } catch (IOException ignored) {}
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(null);
        System.exit(0);
    }
    protected void onPause() {
        super.onPause();
        System.exit(0);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(null);
        System.exit(0);
    }
}