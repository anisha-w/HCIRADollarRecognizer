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

// Class to store the results from each recognition
class SingleMatchResult
{
    public UnistrokeGesture templateGesture;
    public double score;

    // Constructor
    SingleMatchResult(UnistrokeGesture templateGesture, double score){
        this.templateGesture = templateGesture;
        this.score = score;
    }
}

class CandidateCompleteResults
{
    public UnistrokeGesture candidateGesture;
    public ArrayList<SingleMatchResult> trainingSetResults;
    public int randomIteration;
    public int numberOfTrainingExamples;
    public int totalSizeOfTrainingSet;

    // Constructor
    CandidateCompleteResults(UnistrokeGesture candidateGesture, ArrayList<SingleMatchResult> trainingSetResults){
        this.candidateGesture = candidateGesture;
        this.trainingSetResults = trainingSetResults;
    }

    // Method to order the SingleMatchResults by score
    public void orderSingleMatchResults(){
        Collections.sort(trainingSetResults, new Comparator<SingleMatchResult>() {
            @Override
            public int compare(SingleMatchResult o1, SingleMatchResult o2) {
                return Double.compare(o1.score, o2.score);
            }
        });
    }

    // Method to generate a String with all the training set results, in the format {user1-gestureType1-repetition1,user2-gestureType2-repetition2,...}
    public String generateTrainingSetResultsString(){
        String trainingSetResultsString = "{";
        for(int i=0;i<trainingSetResults.size();i++){
            trainingSetResultsString += trainingSetResults.get(i).templateGesture.user + "-" + trainingSetResults.get(i).templateGesture.gestureType + "-" + trainingSetResults.get(i).templateGesture.repetition + ",";
        }
        trainingSetResultsString += "}";
        return trainingSetResultsString;
    }

    // Method to generate a String with all the training set results and scores, in the format {user1-gestureType1-repetition1,score1,user2-gestureType2-repetition2,score2,...}
    public String generateTrainingSetResultsWithScoresString(){
        String trainingSetResultsString = "{";
        for(int i=0;i<trainingSetResults.size();i++){
            trainingSetResultsString += trainingSetResults.get(i).templateGesture.user + "-" + trainingSetResults.get(i).templateGesture.gestureType + "-" + trainingSetResults.get(i).templateGesture.repetition + "," + trainingSetResults.get(i).score + ",";
        }
        trainingSetResultsString += "}";
        return trainingSetResultsString;
    }

    // Method to generate a String with the gesture data, in the format {user-gestureType-repetition}
    public String generateGestureString(UnistrokeGesture gesture){
        String gestureString = "{" + gesture.user + "-" + gesture.gestureType + "-" + gesture.repetition + "}";
        return gestureString;
    }

    // Method to generate a String with the results of the recognition in CSV format
    public String generateCSVString(){
        String csvString = "";
        UnistrokeGesture bestGesture = trainingSetResults.get(0).templateGesture;
        int bestScore = (int)trainingSetResults.get(0).score;

        int isCorrect = 0;
        if (bestGesture.gestureType.equals(candidateGesture.gestureType)){
            isCorrect = 1;
        }

        csvString += candidateGesture.user + "," + candidateGesture.gestureType + "," 
        + candidateGesture.repetition + "," + randomIteration + "," + numberOfTrainingExamples + "," 
        + totalSizeOfTrainingSet + "," + generateTrainingSetResultsString() + ","
        + generateGestureString(candidateGesture) + "," + bestGesture.gestureType + "," + isCorrect + ","
        + bestScore + "," + generateGestureString(bestGesture) + "," + generateTrainingSetResultsWithScoresString();

        return csvString;
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
        ArrayList <SingleMatchResult> results = new ArrayList<>();
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
                        // Assuming that the result will be an ArrayList of Result objects which have:
                        // 1. The UnistrokeGesture it scored against
                        // 2. The score it got
                        // The list will be ordered from highest to lowest score   

                        //resultLog.add(GestureRecognizer.recognize(currentGesture.processedPoints, selectedTemplateGestures));

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
