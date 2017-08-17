package uk.ac.reading.ft025024.fluct;

import android.content.Context;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

class XMLEditor {
    final static ArrayList<LoadedLevel> levelsList = new ArrayList<>(); //final so can't be rebound, static because we only want one level arraylist over all objects
    private static File levelStore; //needs to be static (only one instance over all objects)
    private static boolean hasDownloaded = false; //static so remains consistent over all objects
    final Context appContext; //context needed for resources

    /**
     * constructor takes a context and sets it for the object of XMLEditor
     */
    XMLEditor(Context c){
        appContext = c;
    }
    /**
     * sets the file to be read from
     */
    private void loadFile(File file){
        levelStore = file;
    }

    /**
     * copies level from raw to the files directory of the app so that
     * levels can be written to the XML file
     */
    void copyLevelsToInternal() throws Exception{
        File file = new File(appContext.getFilesDir(), "levels.xml"); //create a file
        if (!file.exists()) {//if it doesn't exist
            InputStream ins = appContext.getResources().openRawResource(R.raw.levels); //open the raw resource
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int size;
            byte[] buffer = new byte[1024];
            while ((size = ins.read(buffer, 0, 1024)) >= 0) {
                outputStream.write(buffer, 0, size); //read and then write to the buffer
            }
            ins.reset(); //reset and close input stream
            ins.close();
            buffer = outputStream.toByteArray();
            outputStream.close(); //close byte output
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(buffer); //write buffer to file
            fos.flush();
            fos.close();///config  file now in a writable dir for custom levels

            Toast toast = Toast.makeText(appContext, "Created levels", Toast.LENGTH_SHORT);
            toast.show();
            loadFile(file); //set the file object
            readLevels();//read the levels inside
        } else {
            Toast toast = Toast.makeText(appContext, "Loading from internal storage" + file.getAbsolutePath(), Toast.LENGTH_SHORT);
            toast.show();
            loadFile(file);//if file exists, read from it
            readLevels();
        }
    }
    /**
     * takes downloaded document as parameter
     * if files haven't already been downloaded, pass document to be read from
     * (readFromArgument)
     */
    void readDownloadedLevels(Document d){
        if(!hasDownloaded) {
            readFromArgument(d); //pass document to be read
            Toast toast = Toast.makeText(appContext, "Reading downloaded levels", Toast.LENGTH_SHORT);
            toast.show();
            hasDownloaded = true; //only allow downloading once
        }else{
            Toast toast = Toast.makeText(appContext, "Already downloaded", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    /**
     * clear levels array, make an input stream from the raw levels resource
     * create document object from input stream and pass the document as an argument
     */
    void readFromRaw() throws Exception {
        levelsList.clear();
        InputStream input = appContext.getResources().openRawResource(R.raw.levels);//open levels xml file for parsing
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document dom = builder.parse(input); //parse the xml file for reading
        readFromArgument(dom);
    }
    /**
     * clear array and parse the document file and pass it to be read
     */
    void readLevels() throws Exception {
        levelsList.clear();//clear array
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document dom = builder.parse(levelStore); //parse the xml file for reading
        readFromArgument(dom);
    }
    /**
     * delete level at a position
     * parse it, get the n'th level xml element, check if the level is custom
     * if it is, remove the node, remove from array list, delete file and create
     * a new one in its place given the new document
     */
    boolean deleteLevel(int position) throws Exception{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document dom = builder.parse(levelStore); //parse the file
        Element root = dom.getDocumentElement();
        Node toDel = dom.getElementsByTagName("level").item(position);
        NodeList items = root.getElementsByTagName("level");
        Element level = (Element) items.item(position); //get level at the user given position
        Element custLvl = (Element) level.getElementsByTagName("custom").item(0);
        String boolCustom = custLvl.getAttribute("customlevel");
        boolean toBool = Boolean.parseBoolean(boolCustom);
        boolean success = false;

        if (toBool) {
            toDel.getParentNode().removeChild(toDel); //remove the level child node
            dom.normalizeDocument();
            levelsList.remove(position); //remove from array
            String toStore = levelStore.getAbsolutePath(); //store its position
            if(levelStore.delete()) { //delete current file
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(dom);
                StreamResult result = new StreamResult(new File(toStore));
                transformer.transform(source, result); //create the new file
                //levelStore = new File(toStore);
                success = true;
            }else{
                Toast toast = Toast.makeText(appContext, "Cannot delete level", Toast.LENGTH_SHORT);
                toast.show();
            }
            }else{
                Toast toast = Toast.makeText(appContext, "Cannot delete pre-made levels", Toast.LENGTH_SHORT);
                toast.show();
                success = false;
            }
        return success;
    }
    /**
     * read in each level element from the given xml file,
     * at each interval/increment add the level as a new object of type LoadedLevel
     * to the levels array list
     */
    private void readFromArgument(Document dom){
        Element root = dom.getDocumentElement();
        NodeList items = root.getElementsByTagName("level"); //get the level tag name
        for (int j = 0; j < items.getLength(); j++) { // loop through level elements, getting attributes
            Element level = (Element) items.item(j); //cast each item to an element
            String name = level.getAttribute("name"); //get the level name attribute
            Element missileEnab = (Element) level.getElementsByTagName("missile").item(0);
            String enabled = missileEnab.getAttribute("enabled");
            Boolean enabledMissile = Boolean.parseBoolean(enabled);
            Element mapElem = (Element) level.getElementsByTagName("map").item(0);
            String map = mapElem.getAttribute("name");
            Element charElem = (Element) level.getElementsByTagName("char").item(0);
            String charac = charElem.getAttribute("name");
            Element maxOnscreen = (Element) level.getElementsByTagName("max").item(0);
            String maxID = maxOnscreen.getAttribute("maximum");
            int maxNumID = Integer.parseInt(maxID);
            Element liveID = (Element) level.getElementsByTagName("live").item(0);
            String num = liveID.getAttribute("count");
            int livesCount = Integer.parseInt(num);
            Element enemyLives = (Element) level.getElementsByTagName("enemylives").item(0);
            String enemyLiveStr = enemyLives.getAttribute("enemlives");
            int enemyLivesInt = Integer.parseInt(enemyLiveStr);
            Element enemyFire = (Element) level.getElementsByTagName("enemyfiretime").item(0);
            String enemyFireStr = enemyFire.getAttribute("enemfiretime");
            int enemyFireInt = Integer.parseInt(enemyFireStr);
            Element homingTime = (Element) level.getElementsByTagName("homingtime").item(0);
            String homingTimeStr = homingTime.getAttribute("homingmisstime");
            int homingTimeInt = Integer.parseInt(homingTimeStr);
            Element playerFireTime = (Element) level.getElementsByTagName("playerfiretime").item(0);
            String playerFireTimeStr = playerFireTime.getAttribute("firetime");
            int playerFireTimeInt = Integer.parseInt(playerFireTimeStr);
            Element lifelineFireTime = (Element) level.getElementsByTagName("lifeline").item(0);
            String lifelineFireTimeStr = lifelineFireTime.getAttribute("lifefiretime");
            int lifelineFireTimeInt = Integer.parseInt(lifelineFireTimeStr);
            Element hardEnemy = (Element) level.getElementsByTagName("hardenemy").item(0);
            String hardEnemyStr = hardEnemy.getAttribute("hardchance");
            int hardEnemyInt = Integer.parseInt(hardEnemyStr);
            //add the new level to arraylist
            levelsList.add(new LoadedLevel(name, enabledMissile, map, charac, maxNumID, livesCount,
                    enemyLivesInt,enemyFireInt,homingTimeInt, playerFireTimeInt, lifelineFireTimeInt, hardEnemyInt));
        }
    }
    /**
     * given a position in the array, write its attributes to the XML file
     */
    void writeLevel(int position)throws Exception{
        LoadedLevel a = levelsList.get(position);//to write to file
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(levelStore);
        Element newLevel = doc.createElement("level");
        newLevel.setAttribute("name", a.level);
        Element missiles = doc.createElement("missiles");
        Element enabled = doc.createElement("missile");
        enabled.setAttribute("enabled", String.valueOf(a.homingMissiles));
        missiles.appendChild(enabled);
        newLevel.appendChild(missiles);
        Element mapElem = doc.createElement("maps");
        Element mapElemChild = doc.createElement("map");
        mapElemChild.setAttribute("name", a.mapName);
        mapElem.appendChild(mapElemChild);
        newLevel.appendChild(mapElem);
        Element charElem = doc.createElement("character");
        Element charElemChild = doc.createElement("char");
        charElemChild.setAttribute("name", a.characName);
        charElem.appendChild(charElemChild);
        newLevel.appendChild(charElem);
        Element maxOn = doc.createElement("maxonscreen");
        Element maxOnChild = doc.createElement("max");
        maxOnChild.setAttribute("maximum", String.valueOf(a.maxOnScreen));
        maxOn.appendChild(maxOnChild);
        newLevel.appendChild(maxOn);
        Element livesElem = doc.createElement("lives");
        Element livesElemChild = doc.createElement("live");
        livesElemChild.setAttribute("count", String.valueOf(a.lives));
        livesElem.appendChild(livesElemChild);
        newLevel.appendChild(livesElem);
        Element isCustom = doc.createElement("iscustom");
        Element isCustomChild = doc.createElement("custom");
        isCustomChild.setAttribute("customlevel", String.valueOf(true));
        isCustom.appendChild(isCustomChild);
        newLevel.appendChild(isCustom);
        Element enemyElement = doc.createElement("enemy");
        Element enemyChild = doc.createElement("enemylives");
        enemyChild.setAttribute("enemlives", String.valueOf(a.enemyLives));
        Element enemyChild1 = doc.createElement("enemyfiretime");
        enemyChild1.setAttribute("enemfiretime", String.valueOf(a.enemyFireInt));
        Element enemyChild2 = doc.createElement("homingtime");
        enemyChild2.setAttribute("homingmisstime", String.valueOf(a.homingTimeInt));
        enemyElement.appendChild(enemyChild);
        enemyElement.appendChild(enemyChild1);
        enemyElement.appendChild(enemyChild2);
        newLevel.appendChild(enemyElement);
        Element player = doc.createElement("player");
        Element playerChild = doc.createElement("playerfiretime");
        playerChild.setAttribute("firetime", String.valueOf(a.playerFireTimeInt));
        Element playerChild1 = doc.createElement("lifeline");
        playerChild1.setAttribute("lifefiretime", String.valueOf(a.lifelineFireTimeInt));
        player.appendChild(playerChild);
        player.appendChild(playerChild1);
        newLevel.appendChild(player);
        Element spawn = doc.createElement("spawn");
        Element spawnChild = doc.createElement("hardenemy");
        spawnChild.setAttribute("hardchance", String.valueOf(a.hardEnemyInt));
        spawn.appendChild(spawnChild);
        newLevel.appendChild(spawn);
        NodeList levelList = doc.getElementsByTagName("level");
        levelList.item(0).getParentNode().appendChild(newLevel);
        //output the result as the modified doc
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        Result output = new StreamResult(new File(levelStore.getAbsolutePath()));
        Source input = new DOMSource(doc);
        transformer.transform(input, output);
    }
}