// CIS6930 Special Topics: Human-Centered Input Recognition Algorithms
// $1 Offline Recognizer implementation by Alexander Barquero and Anisha Wadhwani

import java.util.*;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.awt.*;

/*
 * Keeps track of all details related to a single test
 * Training Template Gesture List
 * Candidate Gesture
 * Number of Training Examples (e)
 * Iteration number (i)
 * Participant list 
 */
class CandidateCompleteResults
{
    public UnistrokeTemplate candidateGesture;
    public ArrayList<SingleMatchResult> trainingSetResults;
    public int randomIteration;
    public int numberOfTrainingExamples;
    public int totalSizeOfTrainingSet;

	//Adding additional variables to keep track of participant list and random100 iterator for user
    public int randomIterationParticipant;
    public int numberOfParticipants;
    public ArrayList<String> participantList;
    
    public ArrayList <UnistrokeTemplate> trainingGesturesSet;

    // Method to order the SingleMatchResults by score
    public void orderSingleMatchResults(){
        Collections.sort(trainingSetResults, new Comparator<SingleMatchResult>() {
            @Override
            public int compare(SingleMatchResult o1, SingleMatchResult o2) {
                return Double.compare(o2.score,o1.score); // for descending order, we switch order of items
            }
        });
    }

    // Method to generate a String with all the training set results, in the format {user1-gestureType1-repetition1,user2-gestureType2-repetition2,...}
    public String generateTrainingSetResultsString(){
        String trainingSetResultsString = "{";
        for(int i=0;i<trainingGesturesSet.size();i++){
            trainingSetResultsString += trainingGesturesSet.get(i).user + "-" + trainingGesturesSet.get(i).gestureType + "-" + trainingGesturesSet.get(i).repetition + ",";
        }
        trainingSetResultsString = trainingSetResultsString.substring(0, trainingSetResultsString.length()-1) + "}";
        return trainingSetResultsString;
    }

    // Method to generate a String with all the training set results and scores, in the format {user1-gestureType1-repetition1,score1,user2-gestureType2-repetition2,score2,...}
    public String generateTrainingSetResultsWithScoresString(){
        String trainingSetResultsString = "{";
        for(int i=0;i<trainingSetResults.size() && i<50 ;i++){
            trainingSetResultsString += trainingSetResults.get(i).templateGesture.user + "-" + trainingSetResults.get(i).templateGesture.gestureType + "-" + trainingSetResults.get(i).templateGesture.repetition + "," + Math.round(trainingSetResults.get(i).score * 1000)/1000.0 + ","; // FIXED
        }
        trainingSetResultsString = trainingSetResultsString.substring(0, trainingSetResultsString.length()-1) + "}";
        return trainingSetResultsString;
    }

    // Method to generate a String with the gesture data, in the format {user-gestureType-repetition}
    public String generateGestureString(UnistrokeTemplate gesture){
        String gestureString = "{" + gesture.user + "-" + gesture.gestureType + "-" + gesture.repetition + "}";
        return gestureString;
    }

    //print participant list
    public String generateParticipantList(){
        String participantString = "{";
        for(int i=0;i<participantList.size();i++)
            participantString+=participantList.get(i)+",";
        participantString=participantString.substring(0,participantString.length()-1)+"}";
        return participantString;

    }

    public String generateCSVString(){
        String csvString = "";
        UnistrokeTemplate bestGesture = trainingSetResults.get(0).templateGesture;
        double bestScore = trainingSetResults.get(0).score;

        int isCorrect = 0;
        if (bestGesture.gestureType.equals(candidateGesture.gestureType)){
            isCorrect = 1;
        }

        csvString += candidateGesture.user + "," + candidateGesture.gestureType + "," 
        + randomIteration + "," + numberOfTrainingExamples + "," + totalSizeOfTrainingSet // random iteration? repetition? number of training examples
        + ",\"" + generateTrainingSetResultsString() + "\",\""
        + generateGestureString(candidateGesture) + "\"," + bestGesture.gestureType + "," + isCorrect + ","
        + bestScore + ",\"" + generateGestureString(bestGesture) + "\",\"" + generateTrainingSetResultsWithScoresString()+"\""; //adding "" with escape characters for creating csv correctly

        return csvString;
    }

    // Method to generate a String with the results of the recognition in CSV format
    public String generateCSVStringIndependentAnalysis(){
        String csvString = "";
        UnistrokeTemplate bestGesture = trainingSetResults.get(0).templateGesture;
        double bestScore = trainingSetResults.get(0).score;

        int isCorrect = 0;
        if (bestGesture.gestureType.equals(candidateGesture.gestureType)){
            isCorrect = 1;
        }

        csvString += numberOfParticipants+",\""+generateParticipantList()+"\","+randomIterationParticipant + "," + candidateGesture.user + "," + candidateGesture.gestureType + "," 
        + randomIteration + "," + numberOfTrainingExamples + "," + totalSizeOfTrainingSet // random iteration? repetition? number of training examples
        + ",\"" + generateTrainingSetResultsString() + "\",\""
        + generateGestureString(candidateGesture) + "\"," + bestGesture.gestureType + "," + isCorrect + ","
        + bestScore + ",\"" + generateGestureString(bestGesture) + "\",\"" + generateTrainingSetResultsWithScoresString()+"\""; //adding "" with escape characters for creating csv correctly

        return csvString;
    }

    // Method to indicate if the candidate gesture was recognized correctly
    public boolean isCorrect(){
        UnistrokeTemplate bestGesture = trainingSetResults.get(0).templateGesture;
        if (bestGesture.gestureType.equals(candidateGesture.gestureType)){
            return true;
        }
        return false;
    }

	//return bestGesture
    public UnistrokeTemplate getBestGesture(){
        return trainingSetResults.get(0).templateGesture;
    }
}

/*
 * Encapsulates the functionality:
 *  1. User dependent Analysis : Test choosen candidate gesture against e template samples of each gesture of the same user
 *  2. User Independent Analysis : Test randomly choosen candidate gesture against e template samples of each gesture of the r randonly choosen users
 * Print results in csv file along with accuracy
 */
public class DollarRecognizerOffline {

    public static Hashtable<String, Integer> gestureTypes;

	//Adding varaibles to read the project setup from config file instead of hardcoded values
    static String gestureSetName; 
    static int gestureSetSize; //how many gestures in the set (10 or 16)
    static Document configDoc;

    static ArrayList<Integer> userList = new ArrayList<>();
    static int userGroupSize;

    // Constructor
    DollarRecognizerOffline() throws Exception{
        
		//Read gestureSet name from config file 
        configDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File("config.xml"));
        configDoc.getDocumentElement().normalize();
        gestureSetName = configDoc.getElementsByTagName("gestureSet").item(0).getTextContent();
        userGroupSize = Integer.valueOf(configDoc.getElementsByTagName("userSize").item(0).getTextContent()); //Added user group size in config file 


        ArrayList <UnistrokeTemplate> gestureList;
        populateGestureTypes();
        gestureList = extractDataFromXML();
        randomOfflineRecognizer(gestureList);
        randomOfflineRecognizerUserIndependent(gestureList);

    }

    // Method to populate the gesture types and their numerical mapping from config file
    public void populateGestureTypes(){

		//Read list of gestures from config file
        gestureSetSize = Integer.parseInt(configDoc.getElementsByTagName(gestureSetName+"Size").item(0).getTextContent());
        gestureTypes = new Hashtable<String, Integer>();

        Element gestureListElement = (Element) configDoc.getDocumentElement().getElementsByTagName(gestureSetName).item(0);
        NodeList gestureList = gestureListElement.getElementsByTagName("gesture");
        for (int i =0; i < gestureList.getLength() ; i++) {
            Element nNode = (Element) gestureList.item(i);
            String name= nNode.getElementsByTagName("name").item(0).getTextContent();
            Integer value = Integer.parseInt(nNode.getElementsByTagName("value").item(0).getTextContent());
            gestureTypes.put(name, value);
        }

    }

    // Method to extract data from an XML, returns a single UnistrokeTemplate object
    public static UnistrokeTemplate createGesture(File templateFile) throws Exception{

        ArrayList<Point> capturedPoints = new ArrayList<>();
        ArrayList<Long> capturedTimestamp = new ArrayList<>();
        
        Document templateXML = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(templateFile);  
        
        templateXML.getDocumentElement().normalize();  
        
        //System.out.println("Root element: " + templateXML.getDocumentElement().getNodeName());  
        NodeList nodeList = templateXML.getElementsByTagName("Point");
        for(int i=0;i<nodeList.getLength();i++){
            //System.out.println(nodeList.item(i).getAttributes().getNamedItem("X").getNodeValue());
            int x = Integer.parseInt(nodeList.item(i).getAttributes().getNamedItem("X").getNodeValue());
            int y = Integer.parseInt(nodeList.item(i).getAttributes().getNamedItem("Y").getNodeValue());
            capturedPoints.add(new Point(x,y));
            capturedTimestamp.add(Long.parseLong(nodeList.item(i).getAttributes().getNamedItem("T").getNodeValue()));
        }

        String fileName = templateFile.getName();
        String gestureId = templateXML.getDocumentElement().getAttribute("Name");
        String user = templateXML.getDocumentElement().getAttribute("Subject");
        String speed = templateXML.getDocumentElement().getAttribute("Speed");
        String gestureType = templateXML.getDocumentElement().getAttribute("Name").replaceAll("[0-9]", ""); // differences in dataset from part 3 and part 4
        int repetition = Integer.parseInt(templateXML.getDocumentElement().getAttribute("Number"));

        return new UnistrokeTemplate(fileName, gestureId, user, speed, gestureType, repetition, capturedPoints,capturedTimestamp);
    }

    // Method to extract data from the XML file list, returns an ArrayList of UnistrokeTemplate objects
    public static ArrayList <UnistrokeTemplate> extractDataFromXML() throws Exception{
        
        ArrayList <UnistrokeTemplate> UnistrokeTemplates = new ArrayList<>();
        String xmlDirPath = System.getProperty("user.dir");
        
        System.out.println("xmlDirPath: " + xmlDirPath);
        
		//read folder name from config file instead of hardcoded file names.
        File xmlDir = new File(xmlDirPath + configDoc.getDocumentElement().getElementsByTagName("templatePath").item(0).getTextContent()); //ANIHSA : MAKE A MORE GENERIC PATH
        
        System.out.println("xmlDir.getAbsolutePath: " + xmlDir.getAbsolutePath());
        
        String dirList[] = xmlDir.list(); 
        for(int i=0;i<dirList.length;i++){
            //System.out.println("dirlist["+i+"]: "+dirList[i]); 
            if(dirList[i].length()<=3 && !dirList[i].contains("pilot")){ 
                //File userDir = new File(xmlDir.getAbsoluteFile()+"/"+dirList[i]+"/medium"); //project 1 part 3 :
                File userDir = new File(xmlDir.getAbsoluteFile()+"/"+dirList[i]);
                String userTemplateList[] = userDir.list(); 
                
                System.out.println("user "+dirList[i]+" number of templates "+userTemplateList.length);
                UnistrokeTemplate newGesture=null;
                for(int j=0;j<userTemplateList.length;j++){
                    //System.out.println("\t"+userTemplateList[j]); 
                    newGesture = createGesture(new File(userDir.getAbsoluteFile()+"/"+userTemplateList[j])); //to store in a array list of arraylists
                    UnistrokeTemplates.add(newGesture);
                }
                if(newGesture!=null)
                    userList.add(newGesture.user); //adding user names(particpant ID) in list
            }
        }
        System.out.println("UnistrokeTemplates.size(): " + UnistrokeTemplates.size());
        return UnistrokeTemplates;
    }

    // Method to filter the gesture list based on the user
    public static ArrayList <UnistrokeTemplate> filterGestureListByUser(ArrayList <UnistrokeTemplate> gestureList, int user){
        ArrayList <UnistrokeTemplate> filteredGestureList = new ArrayList<>();
        for(int i=0;i<gestureList.size();i++){
            if(gestureList.get(i).user==user){
                filteredGestureList.add(gestureList.get(i));
            }
        }
        return filteredGestureList;
    }

    // Method to filter the gesture list based on the sample number
    public static ArrayList <UnistrokeTemplate> filterGestureListByIteration(ArrayList <UnistrokeTemplate> gestureList, int iteration){
        ArrayList <UnistrokeTemplate> filteredGestureList = new ArrayList<>();
        for(int i=0;i<gestureList.size();i++){
            if(gestureList.get(i).repetition==iteration){
                filteredGestureList.add(gestureList.get(i));
            }
        }
        return filteredGestureList;
    }

    // Method to filter the gesture list based on the gesture type
    public static ArrayList <UnistrokeTemplate> filterGestureListByGestureType(ArrayList <UnistrokeTemplate> gestureList, int gestureType){
        ArrayList <UnistrokeTemplate> filteredGestureList = new ArrayList<>();
        for(int i=0;i<gestureList.size();i++){
            if(gestureTypes.get(gestureList.get(i).gestureType).equals(gestureType)){
                filteredGestureList.add(gestureList.get(i));
            }
        }
        return filteredGestureList;
    }

    public static void randomOfflineRecognizer(ArrayList <UnistrokeTemplate> gestureList) throws Exception{

        System.out.println("STARTING USER-DEPENDENT ANALYSIS");
        ArrayList <CandidateCompleteResults> resultLog = new ArrayList<>();
        int totalRuns = 0;
        // For each user
        for(int u=0;u<userGroupSize;u++){
            ArrayList <UnistrokeTemplate> userGestureList = filterGestureListByUser(gestureList, userList.get(u)); // Taking user number from a list instead of having consecutive user numbers
            // For each example
            for(int e=1;e<=9;e++){
                // Repeat X times
                for(int i=1;i<=10;i++){ //RandomIterator
                    ArrayList <UnistrokeTemplate> candidateGestures = new ArrayList<>();
                    ArrayList <UnistrokeTemplate> selectedTemplateGestures = new ArrayList<>();
                    // For each gesture type
                    for(int g=0;g<gestureSetSize;g++){
                        ArrayList <UnistrokeTemplate> typeAndUserGestureList = filterGestureListByGestureType(userGestureList, g);
                        ArrayList <Integer> randomGestureIndices = new ArrayList<>();
                        while(randomGestureIndices.size()<e){
                            int randomGestureIndex = (int)(Math.random()*10);
                            if(!randomGestureIndices.contains(randomGestureIndex)){
                                randomGestureIndices.add(randomGestureIndex);
                            }
                        }
                        for(int j=0;j<randomGestureIndices.size();j++){
                            // Gets the gesture that corresponds to the random index (index=iteration) in the type/user filtered gesture list
                            try{
                                selectedTemplateGestures.add(typeAndUserGestureList.get(randomGestureIndices.get(j)));
                            }catch(Exception ie){
                                System.out.println("u : "+u+" - "+userList.get(u) + 
                                                    " gesturelistsize "+gestureList.size()+
                                                    " userGestureListsize "+userGestureList.size()+
                                                    "\ne : "+e+
                                                    "\ni : "+i+
                                                    "\ng : "+g+
                                                    " userGestureListsize "+userGestureList.size()+
                                                    " typeAndUserGestureListsize "+typeAndUserGestureList.size()+
                                                    "\nj : "+j);
                                System.out.println("j "+j+" "+randomGestureIndices.get(j)+" "+randomGestureIndices.size()+" "+typeAndUserGestureList.size()+" "+g);
                                ie.printStackTrace();
                            }
                        }
                        
                        UnistrokeTemplate candidateGesture = null;
                        while (candidateGesture == null) {
                            int candidateGestureIndex = (int)(Math.random()*10);
                            if(!randomGestureIndices.contains(candidateGestureIndex)){
                                candidateGesture = typeAndUserGestureList.get(candidateGestureIndex);
                            }
                        }
                        // CandidateGestures + 1 candidate gesture, total of 16
                        candidateGestures.add(candidateGesture);
                    }
                    // For each candidate gesture
                    for(int t=0;t<gestureSetSize;t++)
                    {
                        UnistrokeTemplate currentGesture = candidateGestures.get(t);
                        CandidateCompleteResults currentResult = new CandidateCompleteResults();
                        currentResult.candidateGesture = currentGesture;
                        currentResult.randomIteration = i;
                        currentResult.numberOfTrainingExamples = e;
                        currentResult.totalSizeOfTrainingSet = e*gestureSetSize;
                        
                        
                        // Assuming that the result will be an ArrayList of SingleMatchResult objects which have:
                        // 1. The UnistrokeTemplate it scored against
                        // 2. The score it got
                        // The list will be ordered from highest to lowest score   
                        
                        currentResult.trainingSetResults = (ArrayList<SingleMatchResult>)GestureRecognizer.recognize(currentGesture.processedPoints, selectedTemplateGestures).get("N-BEST");
                        currentResult.orderSingleMatchResults();
                        totalRuns ++;
                        if (totalRuns % 1000 == 0)
                        {
                            System.out.println("Total runs: " + totalRuns);
                        }
                        currentResult.trainingGesturesSet = selectedTemplateGestures;
                        resultLog.add(currentResult);
                    }
                }
            }
        }
        
        //Printing to csv file
        System.out.println("Total runs: " + totalRuns);
        String fileName = "1dollarLog_UserDependent_" + System.currentTimeMillis() + ".csv";
        File file = new File(fileName);
        file.createNewFile();
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("Recognition Log: A.Barquero & A.Wadhwani // $1 // Washington $1 Unistroke gesture logs // User-Dependent Random-100 Offline Recognizer");
        bw.newLine();
        bw.newLine();
        bw.write("User[all-users],GestureType[all-gestures-types],RandomIteration[1to100],#ofTrainingExamples[E],TotalSizeOfTrainingSet[count],TrainingSetContents[specific-gesture-instances],Candidate[specific-instance],RecoResultGestureType[what-was-recognized],CorrectIncorrect[1or0],RecoResultScore,RecoResultBestMatch[specific-instance],RecoResultNBestSorted[instance-and-score]");
        bw.newLine();

        int totalCorrect = 0;
        int userCorrect = 0;
        int currentUser = 0;
        int userCounter = 0;
        double userAccuracy = 0;
        Map<Integer, Double> localAverageAccuracy = new HashMap<>();

        for(int i=0;i<resultLog.size();i++){
            userCounter ++;
            if (i==0) 
            {
                currentUser = resultLog.get(i).candidateGesture.user;
            }
            bw.write(resultLog.get(i).generateCSVString());
            if (resultLog.get(i).isCorrect()) {
                totalCorrect++;
                userCorrect++;
            }
            if (resultLog.get(i).candidateGesture.user != currentUser)
            {
                userAccuracy = (userCorrect / (double)(userCounter)) * 100;
                //bw.write("LocalAvgAccuracy," + userAccuracy);
                //bw.newLine();
                localAverageAccuracy.put(currentUser, userAccuracy);
                userCorrect = 0;
                userCounter = 0;
                currentUser = resultLog.get(i).candidateGesture.user;
            }
            bw.newLine();
        }
        userAccuracy = (userCorrect / (double)(userCounter)) * 100;
        localAverageAccuracy.put(currentUser, userAccuracy);

        bw.newLine();
        //Print Accuracy
        for (Map.Entry<Integer, Double> entry : localAverageAccuracy.entrySet()) {
            bw.write("User" + entry.getKey() + "AvgAccuracy," + entry.getValue());
            bw.newLine();
        }
        double totalAverageAccuracy = (totalCorrect / (double)resultLog.size()) * 100;
        bw.write("TotalAvgAccuracy," + totalAverageAccuracy);
        bw.close();
        fw.close();
    }   

    // Method to perform the random offline recognizer
    public static void randomOfflineRecognizerUserIndependent(ArrayList <UnistrokeTemplate> gestureList) throws Exception{

        System.out.println("STARTING USER-INDEPENDENT ANALYSIS");
        ArrayList <CandidateCompleteResults> resultLog = new ArrayList<>();
        int totalRuns = 0;
        // For each user
        for(int u=0;u<userGroupSize-1;u++){
            //ArrayList <UnistrokeTemplate> userGestureList = filterGestureListByUser(gestureList, u); // Proj. 2, user starts with 1
            // Repeat x1 times

            //random100 iterator / random10 iterator 
            for(int r=1;r<=10;r++){
                ArrayList <Integer> randomUserIndices = new ArrayList<>();

                //randomly select u users/participants
                while(randomUserIndices.size()<u){
                    int randomUserIndex = (int)(Math.random()*userGroupSize);
                    if(!randomUserIndices.contains(userList.get(randomUserIndex))){
                        randomUserIndices.add(userList.get(randomUserIndex));
                    }
                }


                //candiate user
                ArrayList <UnistrokeTemplate> candidateUserGestures = null;
                while (candidateUserGestures == null) {
                    int candidateUserIndex = (int)(Math.random()*userGroupSize);
                    if(!randomUserIndices.contains(userList.get(candidateUserIndex))){
                        candidateUserGestures = filterGestureListByUser(gestureList, userList.get(candidateUserIndex));
                    }
                }
                // CandidateGestures + 1 candidate gesture, total of 16
                //candidateUsersGestures.addAll(candidateUserGestures); //ANISHA ??

                // For each example / sample
                for(int e=1;e<=9;e++){
                    // Repeat x2 times
                    for(int i=1;i<=10;i++){ //RandomIterator
                        ArrayList <UnistrokeTemplate> candidateGestures = new ArrayList<>();
                        ArrayList <UnistrokeTemplate> selectedTemplateGestures = new ArrayList<>();
                        // For each gesture type
                        for(int g=0;g<gestureSetSize;g++){

                            ArrayList <Integer> randomGestureIndices = new ArrayList<>();
                            // e samples for each gesture for each user. 
                            while(randomGestureIndices.size()<e){
                                int randomGestureIndex = (int)(Math.random()*10); //10 = number of samples
                                if(!randomGestureIndices.contains(randomGestureIndex)){
                                    randomGestureIndices.add(randomGestureIndex);
                                }
                            }

                            ArrayList <UnistrokeTemplate> userAllGestureList ;
                            //for each of these selected users (randomUserIndices) filter the users gestures from all the xmls 
                            for(int l=0;l<randomUserIndices.size();l++){
                                // Gets the gestures that correspond to the random index (index=user) in the gesture list
                                userAllGestureList = filterGestureListByUser(gestureList, randomUserIndices.get(l)); //
                                //selectedTemplateUserGestures.addAll(userGestureList); // size =  u * number of gestures 
                                ArrayList <UnistrokeTemplate> userEachGestureList = filterGestureListByGestureType(userAllGestureList, g);  // all samples for 1 gesture g
                            
                                for(int j=0;j<randomGestureIndices.size();j++){ // e samples for each user
                                    // Gets the gesture that corresponds to the random index (index=iteration) in the type/user filtered gesture list
                                    try{ //ANISHA : REMOVE
                                        selectedTemplateGestures.add(userEachGestureList.get(randomGestureIndices.get(j))); // selectedTemplateGestures = u users * 10 gestures * e samples 
                                    }catch(Exception exp){
                                        
                                        //System.out.println("ANISHA exception"+u+" "+r+" "+e+" "+g+" "+j+" "+typeAndUserGestureList.size());
                                        
                                        throw exp;
                                    }
                                }
                            }

                            ArrayList <UnistrokeTemplate> candidateGestureList = filterGestureListByGestureType(candidateUserGestures, g);  // all samples for 1 gesture g
                            
                            //get one random sample for testing from the candidate user for the gesture g 
                            UnistrokeTemplate candidateGesture = null;
                            while (candidateGesture == null) {
                                int candidateGestureIndex = (int)(Math.random()*10);
                                if(!randomGestureIndices.contains(candidateGestureIndex)){
                                    candidateGesture = candidateGestureList.get(candidateGestureIndex);
                                }
                            }
                            // CandidateGestures + 1 candidate gesture, total of 16 for project 1
                            candidateGestures.add(candidateGesture); // has 1 sample of each gesture ; total 10 for project 2
                        }

                        //System.out.println("[DEBUG] : selectedTemplateGestures COUNT(u users * 10 gestures * e samples ) "+ selectedTemplateGestures.size() + " ; U = "+u +" ; E= "+e);

                        // For each candidate gesture
                        for(int t=0;t<gestureSetSize;t++){
                            UnistrokeTemplate currentGesture = candidateGestures.get(t);
                            CandidateCompleteResults currentResult = new CandidateCompleteResults();
                            currentResult.candidateGesture = currentGesture;
                            currentResult.randomIteration = i;
                            currentResult.numberOfTrainingExamples = e;
                            currentResult.totalSizeOfTrainingSet = u * e * gestureSetSize; // size of selectedTemplateGestures

                            //Adding user list details
                            currentResult.numberOfParticipants = u;
                            currentResult.randomIterationParticipant = r;
                            currentResult.participantList = new ArrayList<>();
                            for (Integer userIndex : randomUserIndices) {
                                currentResult.participantList.add(userIndex+"");
                            }
                        
                            // Assuming that the result will be an ArrayList of SingleMatchResult objects which have:
                            // 1. The UnistrokeTemplate it scored against
                            // 2. The score it got
                            // The list will be ordered from highest to lowest score   
                        
                            currentResult.trainingSetResults = (ArrayList<SingleMatchResult>)GestureRecognizer.recognize(currentGesture.processedPoints, selectedTemplateGestures).get("N-BEST");
                            currentResult.orderSingleMatchResults();
                            totalRuns ++;
                            if (totalRuns % 1000 == 0)
                            {
                                System.out.println("Total runs: " + totalRuns);
                            }
                            currentResult.trainingGesturesSet = selectedTemplateGestures;
                            resultLog.add(currentResult);
                        }
                    }
                }
            }
        }
        
        System.out.println("Total runs: " + totalRuns);
        String fileName = "1dollarLog_UserIndependent_" + System.currentTimeMillis() + ".csv";
        File file = new File(fileName);
        file.createNewFile();
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("Recognition Log: A.Barquero & A.Wadhwani // $1 // Washington $1 Unistroke gesture logs // User-Independent Random-100 Offline Recognizer");
        bw.newLine();
        bw.newLine();
        bw.write("#Number of Users,User list,RandomIteration [User],Candidate User,GestureType[all-gestures-types],RandomIteration[1to100],#ofTrainingExamples[E],TotalSizeOfTrainingSet[count],TrainingSetContents[specific-gesture-instances],Candidate[specific-instance],RecoResultGestureType[what-was-recognized],CorrectIncorrect[1or0],RecoResultScore,RecoResultBestMatch[specific-instance],RecoResultNBestSorted[instance-and-score]");
        bw.newLine();


        //updating user accuracy calculation to gesture accuracy calculation.
        int totalCorrect = 0;
        // int userCorrect = 0;
        // int currentUser = 0;
        // int userCounter = 0;
        // double userAccuracy = 0;
        int numberOfTestcases = resultLog.size()/10; //since 10 gestures 
        
        //Map<Integer, Double> localAverageAccuracy = new HashMap<>();
        Map<String, Integer> gestureAverageAccuracy = new HashMap<>();

        for(int i=0;i<resultLog.size();i++){
           
            bw.write(resultLog.get(i).generateCSVStringIndependentAnalysis());
            if (resultLog.get(i).isCorrect()) {
                totalCorrect++;
                int gestureCorrectCount = gestureAverageAccuracy.getOrDefault(resultLog.get(i).getBestGesture().gestureType,0);
                gestureCorrectCount++;
                gestureAverageAccuracy.put(resultLog.get(i).getBestGesture().gestureType, gestureCorrectCount);
            }
            bw.newLine();
        }
        
        bw.newLine();

        for (Map.Entry<String, Integer> entry : gestureAverageAccuracy.entrySet()) {
            //bw.write("User" + entry.getKey() + "AvgAccuracy," + entry.getValue());
            bw.write("Gesture " + entry.getKey() + " AvgAccuracy," + entry.getValue()/(1.0*numberOfTestcases));
            //System.out.println("Gesture " + entry.getKey() + " AvgAccuracy," + entry.getValue()/(1.0*numberOfTestcases));
            bw.newLine();
        }
        double totalAverageAccuracy = (totalCorrect / (double)resultLog.size()) * 100;
        bw.write("TotalAvgAccuracy," + totalAverageAccuracy);
        bw.close();
        fw.close();
    }   

    // Main method
    public static void main(String args[]) throws Exception{
        new DollarRecognizerOffline();
    }
}
