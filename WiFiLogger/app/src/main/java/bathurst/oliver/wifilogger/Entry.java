package bathurst.oliver.wifilogger;

/**
 * Created by Oliver on 01/08/2017.
 * All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Oliver Bathurst <oliverbathurst12345@gmail.com>
 */

class Entry {
    private final String wifiName;
    private final int wifiFrequency, wifiLinkSpeed, wifiStrength;

    Entry(String name, int frequency, int linkSpeed, int strength){
        wifiName = name;
        wifiFrequency = frequency;
        wifiLinkSpeed = linkSpeed;
        wifiStrength = strength;
    }
    String returnName(){
        return wifiName;
    }
    int returnFrequency(){
        return wifiFrequency;
    }
    int returnLinkSpeed(){
        return wifiLinkSpeed;
    }
    int returnStrength(){
        return wifiStrength;
    }
}