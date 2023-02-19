// CIS6930 Special Topics: Human-Centered Input Recognition Algorithms
// $1 Offline Recognizer implementation by Alexander Barquero and Anisha Wadhwani

import java.util.*;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.awt.*;

// Class to store the data of a single gesture
class UnistrokeGesture{
    
    public String fileName;
    public String gestureId;
    public int user;
    public String speed;
    public String gestureType; 
    public int repetition;
    public ArrayList<Point> capturedPoints;
    public ArrayList<Point> processedPoints;

    // Constructor
    UnistrokeGesture(String fileName,String gestureId, String user, String speed, String gestureType, int repetition, ArrayList<Point> capturedPoints)
    {
        this.fileName = fileName;
        this.gestureId = gestureId;
        this.user = Integer.parseInt(user);
        this.speed = speed;
        this.gestureType = gestureType;
        this.repetition = repetition;
        this.capturedPoints = capturedPoints;
        this.processedPoints = GestureRecognizer.processingGesture(capturedPoints);       
    }

    // Method to get an UnistrokeTemplate object from an UnistrokeGesture object
    public UnistrokeTemplate getUnistrokeTemplate(){
        return new UnistrokeTemplate(gestureType, processedPoints);
    }

}

public class DollarRecognizerOffline {

    public static Hashtable<String, Integer> gestureTypes;

    // Constructor
    DollarRecognizerOffline() throws Exception{
        ArrayList <UnistrokeGesture> gestureList;
        populateGestureTypes();
        gestureList = extractDataFromXML();
        randomOfflineRecognizer(gestureList);

    }

    // Method to populate the gesture types
    public void populateGestureTypes(){
        gestureTypes = new Hashtable<String, Integer>();
        gestureTypes.put("arrow", 0);
        gestureTypes.put("caret", 1);
        gestureTypes.put("check", 2);
        gestureTypes.put("circle", 3);
        gestureTypes.put("delete_mark", 4);
        gestureTypes.put("left_curly_brace", 5);
        gestureTypes.put("left_sq_bracket", 6);
        gestureTypes.put("pigtail", 7);
        gestureTypes.put("question_mark", 8);
        gestureTypes.put("rectangle", 9);
        gestureTypes.put("right_curly_brace", 10);
        gestureTypes.put("right_sq_bracket", 11);
        gestureTypes.put("star", 12);
        gestureTypes.put("triangle", 13);
        gestureTypes.put("v", 14);
        gestureTypes.put("x", 15);
    }

    // Method to extract data from an XML, returns a single UnistrokeGesture object
    public static UnistrokeGesture createGesture(File templateFile) throws Exception{

        ArrayList<Point> capturedPoints = new ArrayList<>();
        
        Document templateXML = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(templateFile);  
        
        templateXML.getDocumentElement().normalize();  
        
        //System.out.println("Root element: " + templateXML.getDocumentElement().getNodeName());  
        NodeList nodeList = templateXML.getElementsByTagName("Point");
        for(int i=0;i<nodeList.getLength();i++){
            //System.out.println(nodeList.item(i).getAttributes().getNamedItem("X").getNodeValue());
            int x = Integer.parseInt(nodeList.item(i).getAttributes().getNamedItem("X").getNodeValue());
            int y = Integer.parseInt(nodeList.item(i).getAttributes().getNamedItem("Y").getNodeValue());
            capturedPoints.add(new Point(x,y));
        }

        String fileName = templateFile.getName();
        String gestureId = templateXML.getDocumentElement().getAttribute("Name");
        String user = templateXML.getDocumentElement().getAttribute("Subject");
        String speed = templateXML.getDocumentElement().getAttribute("Speed");
        String gestureType = templateXML.getDocumentElement().getAttribute("Name").substring(0,gestureId.length()-2);
        int repetition = Integer.parseInt(templateXML.getDocumentElement().getAttribute("Number"));

        return new UnistrokeGesture(fileName, gestureId, user, speed, gestureType, repetition, capturedPoints);
    }

    // Method to extract data from the XML file list, returns an ArrayList of UnistrokeGesture objects
    public static ArrayList <UnistrokeGesture> extractDataFromXML() throws Exception{
        
        ArrayList <UnistrokeGesture> unistrokeGestures = new ArrayList<>();
        String xmlDirPath = System.getProperty("user.dir");
        
        //System.out.println("xmlDirPath: " + xmlDirPath);
        
        File xmlDir = new File(xmlDirPath + "/Project1Part3_Resources/xml_logs");
        
        //System.out.println("xmlDir.getAbsoluteFile().exists(): " + xmlDir.getAbsoluteFile().exists());
        //System.out.println("user.dir: " + System.getProperty("user.dir"));
        //System.out.println("xmlDir.getAbsolutePath: " + xmlDir.getAbsolutePath());
        
        String dirList[] = xmlDir.list(); 
        for(int i=1;i<dirList.length;i++){
            //System.out.println("dirlist["+i+"]: "+dirList[i]); 
            if(dirList[i].length()==3 && !dirList[i].contains("pilot")){
                File userDir = new File(xmlDir.getAbsoluteFile()+"/"+dirList[i]+"/medium");
                String userTemplateList[] = userDir.list(); 
                //System.out.println(userTemplateList.length);
                for(int j=0;j<userTemplateList.length;j++){
                    //System.out.println("\t"+userTemplateList[j]); 
                    UnistrokeGesture newGesture = createGesture(new File(userDir.getAbsoluteFile()+"/"+userTemplateList[j])); //to store in a array list of arraylists
                    unistrokeGestures.add(newGesture);
                }
            }
        }
        System.out.println("unistrokeGestures.size(): " + unistrokeGestures.size());
        return unistrokeGestures;
    }

    // Method to filter the gesture list based on the user
    public static ArrayList <UnistrokeGesture> filterGestureListByUser(ArrayList <UnistrokeGesture> gestureList, int user){
        ArrayList <UnistrokeGesture> filteredGestureList = new ArrayList<>();
        for(int i=0;i<gestureList.size();i++){
            if(gestureList.get(i).user==user){
                filteredGestureList.add(gestureList.get(i));
            }
        }
        return filteredGestureList;
    }

    // Method to filter the gesture list based on the iteration
    public static ArrayList <UnistrokeGesture> filterGestureListByIteration(ArrayList <UnistrokeGesture> gestureList, int iteration){
        ArrayList <UnistrokeGesture> filteredGestureList = new ArrayList<>();
        for(int i=0;i<gestureList.size();i++){
            if(gestureList.get(i).repetition==iteration){
                filteredGestureList.add(gestureList.get(i));
            }
        }
        return filteredGestureList;
    }

    // Method to filter the gesture list based on the gesture type
    public static ArrayList <UnistrokeGesture> filterGestureListByGestureType(ArrayList <UnistrokeGesture> gestureList, int gestureType){
        ArrayList <UnistrokeGesture> filteredGestureList = new ArrayList<>();
        for(int i=0;i<gestureList.size();i++){
            if(gestureTypes.get(gestureList.get(i).gestureType).equals(gestureType)){
                filteredGestureList.add(gestureList.get(i));
            }
        }
        return filteredGestureList;
    }


    // Method to perform the random offline recognizer
    public static void randomOfflineRecognizer(ArrayList <UnistrokeGesture> gestureList) throws Exception{
        double totalAverageAccuracy = 0;
        double totalAccuracy = 0;
        double totalRepeats = 0;

        ArrayList <String> resultLog = new ArrayList<>();

        // For each user
        for(int u=1;u<10;u++){
            ArrayList <UnistrokeGesture> userGestureList = filterGestureListByUser(gestureList, u);
            // For each example
            for(int e=1;e<9;e++){
                // Repeat 100 times
                for(int i=1;i<100;i++){
                    // For each gesture type
                    ArrayList <UnistrokeGesture> candidateGestures = new ArrayList<>();
                    ArrayList <UnistrokeTemplate> selectedTemplateGestures = new ArrayList<>();
                    //UnistrokeTemplate[] selectedTemplateGestures = new UnistrokeTemplate[e*16];
                    for(int g=0;g<16;g++){
                        ArrayList <UnistrokeGesture> typeAndUserGestureList = filterGestureListByGestureType(userGestureList, g);
                        ArrayList <Integer> randomGestureIndices = new ArrayList<>();
                        while(randomGestureIndices.size()<e){
                            int randomGestureIndex = (int)(Math.random()*10);
                            if(!randomGestureIndices.contains(randomGestureIndex)){
                                randomGestureIndices.add(randomGestureIndex);
                            }
                        }
                        for(int j=0;j<randomGestureIndices.size();j++){
                            // Gets the gesture that corresponds to the random index (index=iteration) in the type/user filtered gesture list
                            selectedTemplateGestures.add(typeAndUserGestureList.get(randomGestureIndices.get(j)).getUnistrokeTemplate());
                        }
                        
                        UnistrokeGesture candidateGesture = null;
                        while (candidateGesture == null) {
                            int candidateGestureIndex = (int)(Math.random()*10);
                            if(!randomGestureIndices.contains(candidateGestureIndex)){
                                candidateGesture = typeAndUserGestureList.get(candidateGestureIndex);
                            }
                        }
                        candidateGestures.add(candidateGesture);
                    }
                    
                    for(int t=0;t<candidateGestures.size();t++)
                    {
                        UnistrokeGesture currentGesture = candidateGestures.get(t);
                        HashMap<String,Object> result = GestureRecognizer.recognize(currentGesture.processedPoints, selectedTemplateGestures);
                        UnistrokeTemplate resultTemplate = (UnistrokeTemplate) result.get("TEMPLATE");
                        totalRepeats++;
                        if (resultTemplate.name == currentGesture.gestureType) {
                            totalAccuracy++;


                        }
                    }
                }
            }
        }
    }   

    // Main method
    public static void main(String args[]) throws Exception{
        new DollarRecognizerOffline();
    }
}
