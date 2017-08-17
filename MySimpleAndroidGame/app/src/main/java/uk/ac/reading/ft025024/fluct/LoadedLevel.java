package uk.ac.reading.ft025024.fluct;

class LoadedLevel {
    final String level,mapName,characName; //all of the attributes that make up a level
    final int lives,maxOnScreen,enemyLives,enemyFireInt,homingTimeInt,
            playerFireTimeInt,lifelineFireTimeInt,hardEnemyInt;
    final boolean homingMissiles;
    /**
     * used when adding levels to the array list, levels are stored in objects of type loaded level in an arraylist
     * constructor takes in attributes and a new level object is created
     */
    LoadedLevel(String levelName, boolean homingEnabled, String mapNameIn, String characterName,int maxOn,
                int livesNum, int enemyLivesInt, int enemyFireInt, int homingTimeInt,
                int playerFireTimeInt,int lifelineFireTimeInt, int hardEnemyInt){
        this.level = levelName;
        this.homingMissiles = homingEnabled;
        this.mapName = mapNameIn;
        this.characName = characterName;
        this.maxOnScreen = maxOn;
        this.lives = livesNum;
        this.enemyLives = enemyLivesInt;
        this.enemyFireInt = enemyFireInt;
        this.homingTimeInt = homingTimeInt;
        this.playerFireTimeInt = playerFireTimeInt;
        this.lifelineFireTimeInt = lifelineFireTimeInt;
        this.hardEnemyInt = hardEnemyInt;
    }
}
