// CIS6930 Special Topics: Human-Centered Input Recognition Algorithms
// $1 Online Recognizer implementation by Alexander Barquero and Anisha Wadhwani

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.event.*;
import java.util.*;
import java.nio.file.*;
import java.io.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;

//Class that creates the canvas panel which will be used to draw the gesture
class CanvasPanel extends JPanel implements MouseListener, MouseMotionListener
{
    private boolean userIsStroking;
    private int initX, initY, newX, newY;
    public ArrayList<Point> capturedPoints;
    public ArrayList<Point> processedPoints;
    JLabel resultLabel;

    //Recognize mode or Data collection mode
    String mode;
    //Set all canvas parameters at initialization
    CanvasPanel(String mode){
	
		this.mode = mode;
		
        setBackground(Color.BLACK);
        
        setVisible(true);
        addMouseListener(this);
        addMouseMotionListener(this);

		if(mode.equals("recognize")){
			setSize(800, 600);
	        resultLabel = new JLabel("",SwingConstants.CENTER);
	        //resultLabel.setBackground(Color.WHITE);
	        //resultLabel.setOpaque(true);
	        resultLabel.setForeground(Color.PINK);
	        resultLabel.setBounds(0, 0, 400, 50);
	        add(resultLabel);
	        setLayout(null);
	        //validate();
		}else{
			setSize(1024/2, 568);
		}
        

    }

    //Action on mouse pressed
    public void mousePressed(MouseEvent e)
    {
        Graphics2D graphics = (Graphics2D)getGraphics();
        capturedPoints = new ArrayList<>();
        initX = newX = e.getX();
        initY = newY = e.getY();
        capturedPoints.add(new Point(newX, newY));
        graphics.setColor(Color.RED);
        graphics.setStroke(new BasicStroke(10));
        graphics.drawLine(initX, initY, newX, newY);
        userIsStroking = true;
        if(mode.equals("recognize"))
            resultLabel.setText("");
        // System.out.println("Mouse Clicked at "+x+" "+y);
        
    }

    //Action on mouse dragged    
    public void mouseDragged(MouseEvent e)
    {
        if (userIsStroking) {
            newX = e.getX();
            newY = e.getY();
            capturedPoints.add(new Point(newX, newY));
            Graphics2D graphics = (Graphics2D)getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.setStroke(new BasicStroke(4));
            graphics.drawLine(initX, initY, newX, newY);
            initX = newX;
            initY = newY;
        }
    }

    //Action on mouse released    
    public void mouseReleased(MouseEvent e){
        userIsStroking = false;
        if(mode=="recognize")
        {
            GestureRecognizer.setCanvasWindow(this);

            processedPoints = GestureRecognizer.processingGesture(capturedPoints);

            HashMap<String,Object> result = GestureRecognizer.recognize(processedPoints,DollarRecognizer.templates); // no need to pass points to the function. 

            UnistrokeTemplate resultTemplate = (UnistrokeTemplate) result.get("TEMPLATE");
            //System.out.println("Score: " + result.get("SCORE") + " Template : " + resultTemplate.name);

                resultLabel.setText(" Template : " + resultTemplate.gestureType + " | Score: " + result.get("SCORE") );
        }
        // else do nothing

        // else if(mode=="collect_data"){
            
        //     UnistrokeTemplate template = new UnistrokeTemplate("testFile1", "1" , "1" , "medium" , "arrow" , 1, capturedPoints);
        //     template.storeInFile();
        //     //UnistrokeTemplate(String fileName,String gestureId, String user, String speed, String gestureType, int repetition, ArrayList<Point> capturedPoints)
    
        // }
    }
    
    public void mouseMoved(MouseEvent e){}
 
    public void mouseExited(MouseEvent e){}
 
    public void mouseEntered(MouseEvent e){}
 
    public void mouseClicked(MouseEvent e){}

    public void plotPoints(ArrayList<Point> points, Color c){
        Graphics2D graphics = (Graphics2D)getGraphics();
        graphics.setColor(c);
        for (Point point : points) {
            graphics.fillOval(point.x, point.y, 5, 5);
        }
    }

    public void clearLabel(){
        resultLabel.setText("");
    }
}

class GestureRecognizer{

    static int n = 64;
    static int size = 200;
    static double phi = (-1 + Math.sqrt(5))/2;  

    static CanvasPanel canvasWindow;

    static public void setCanvasWindow(CanvasPanel canvas) {
        canvasWindow = canvas;
    }
    static public ArrayList<Point> processingGesture(ArrayList<Point> capturedPoints) {

        ArrayList<Point> resampledPoints = resample(capturedPoints);
        //canvasWindow.plotPoints(resampledPoints,Color.ORANGE);

        ArrayList<Point> rotatedPoints = rotateToZero(resampledPoints);
        // canvasWindow.plotPoints(rotatedPoints,Color.RED);

        ArrayList<Point> scaledPoints = scaleToSquare(rotatedPoints,200);
        // canvasWindow.plotPoints(scaledPoints,Color.GREEN);

        ArrayList<Point> translatedPoints = translateToOrigin(scaledPoints);
        // canvasWindow.plotPoints(translatedPoints,Color.GRAY);

        return translatedPoints;
    }

    //STEP 1 : RESAMPLE
    static public ArrayList<Point> resample(ArrayList<Point> capturedPoints)
    {
        
        double I = pathLength(capturedPoints) / (n - 1);
        // System.out.println("increment "+I);
        double D = 0.0;

        ArrayList<Point> resampledPoints = new ArrayList<>();
        ArrayList<Point2D.Double> pointsDouble = new ArrayList<>(){
            {for(Point pt : capturedPoints)
                add(new Point2D.Double(pt.getX(), pt.getY()));}
        };

        resampledPoints.add(new Point(capturedPoints.get(0).x, capturedPoints.get(0).y)); // adding 0th point to result point list

        for (int j = 1; j < pointsDouble.size(); j++) {
            double d = distance(pointsDouble.get(j - 1), pointsDouble.get(j));
            if ((D + d) >= I) {
                double qx = pointsDouble.get(j - 1).x + ((I - D) / d) * (pointsDouble.get(j).x - pointsDouble.get(j - 1).x);
                double qy = pointsDouble.get(j - 1).y + ((I - D) / d) * (pointsDouble.get(j).y - pointsDouble.get(j - 1).y);
                Point q = new Point((int)Math.round(qx), (int)Math.round(qy));
                resampledPoints.add(q);
                pointsDouble.add(j,new Point2D.Double(qx,qy)); 
                D = 0.0;
            } else {
                D += d;
            }
        }
        if (resampledPoints.size() == n - 1) { // for the last point
            resampledPoints.add(new Point(capturedPoints.get(capturedPoints.size() - 1).x, capturedPoints.get(capturedPoints.size() - 1).y));
        }
        return resampledPoints;
    }

    static public double pathLength(ArrayList<Point> capturedPoints)
    {
        double d = 0.0;
        for (int i = 1; i < capturedPoints.size(); i++) {
            d += distance(capturedPoints.get(i - 1), capturedPoints.get(i));
        }
        return d;
    }

    static public double distance(Point2D.Double a, Point2D.Double b)
    {
        double dx = b.x - a.x;
        double dy = b.y - a.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    //STEP 2 : ROTATE 
    static public ArrayList<Point> rotateToZero(ArrayList<Point> capturedPoints){
        Point centroidPt = centroid(capturedPoints);
        double angleTheta = (double) Math.atan2((capturedPoints.get(0).y - centroidPt.y),(capturedPoints.get(0).x - centroidPt.x));
        return rotateAllPoints(capturedPoints,-angleTheta);
    }
    
    static public ArrayList<Point> rotateAllPoints(ArrayList<Point> capturedPoints, double angleTheta){
        Point centroidPt = centroid(capturedPoints);
        ArrayList<Point> rotatedPoints = new ArrayList<>();
        double xPt,yPt;
        for (Point point : capturedPoints) {
            xPt = ((double)point.x - centroidPt.x) * Math.cos(angleTheta) - ((double)point.y - centroidPt.y)*Math.sin(angleTheta) + centroidPt.x;
            yPt = ((double)point.x - centroidPt.x) * Math.sin(angleTheta) + ((double)point.y - centroidPt.y)*Math.cos(angleTheta) + centroidPt.x;
            rotatedPoints.add(new Point((int)xPt,(int)yPt));
        }
        return rotatedPoints;

    }

    static public Point centroid(ArrayList<Point> capturedPoints){
        int centroidX=0, centroidY=0;
        for (Point point : capturedPoints) {
            centroidX+=point.x;
            centroidY+=point.y;
        }
        return new Point(centroidX/capturedPoints.size(),centroidY/capturedPoints.size());
    }

    //STEP 3 : SCALE AND TRANSLATE 
    static public ArrayList<Point> scaleToSquare(ArrayList<Point> capturedPoints, int size){
        ArrayList<Point> scaledPoints = new ArrayList<>();
        int boundingBox[] = findBoundingBox(capturedPoints);
        int qx,qy;
        for (Point point : capturedPoints) {
            qx = (int)((double)point.x * ((double)size/boundingBox[0]));
            qy = (int)((double)point.y * ((double)size/boundingBox[1]));
            scaledPoints.add(new Point(qx,qy));
        }
        return scaledPoints;
    }

    static public int[] findBoundingBox(ArrayList<Point> points){
        int xMin = points.get(0).x;
        int yMin= points.get(0).y;
        int xMax = points.get(0).x;
        int yMax= points.get(0).y;

        for (Point point : points) {
            if(point.x>xMax)
                xMax = point.x;
            if(point.x<xMin)
                xMin = point.x;
            if(point.y>yMax)
                yMax = point.y;
            if(point.y<yMin)
                yMin = point.y;
        }
        return new int[]{xMax-xMin,yMax-yMin};
    }

    static public ArrayList<Point> translateToOrigin(ArrayList<Point> capturedPoints){
        ArrayList<Point> translatedPoints = new ArrayList<>();
        int qx,qy;

        Point centroidPt = centroid(capturedPoints);
        for (Point point : capturedPoints) {
            qx = point.x - centroidPt.x;
            qy = point.y - centroidPt.y;
            translatedPoints.add(new Point(qx,qy));
        }
        return translatedPoints;
    }

    //STEP 4 : RECOGNIZE
    static public HashMap<String,Object> recognize(ArrayList<Point> processedPoints, UnistrokeTemplate templates[]){
        double b = Double.MAX_VALUE; 
        UnistrokeTemplate templateMatched = new UnistrokeTemplate(null, null);
        double distance;
        for (UnistrokeTemplate unistrokeTemplate : templates) {
            distance = distanceAtBestAngle(processedPoints,unistrokeTemplate,45,-45,2); //angles in degrees
            if(distance < b){
                b = distance;
                templateMatched = unistrokeTemplate;
            }
        }
        double score = (double)1 - (b / (0.5 * Math.sqrt(size*size + size*size)));

        HashMap<String, Object> hm = new HashMap<>();
        hm.put("SCORE", Double.valueOf(score));
        hm.put("TEMPLATE", (UnistrokeTemplate) templateMatched);

        return hm;
    }

    //STEP 4 : RECOGNIZE ARRAYLIST
    static public HashMap<String,Object> recognize(ArrayList<Point> processedPoints, ArrayList<UnistrokeTemplate> templates){
        
        ArrayList<SingleMatchResult> nBestList = new ArrayList<>(); // adding nbest list
        double b = Double.MAX_VALUE; // keep track of the smallest distance for highest matching template. 
        UnistrokeTemplate templateMatched = new UnistrokeTemplate(null, null);
        double distance,score;
        for (UnistrokeTemplate unistrokeTemplate : templates) {
            distance = distanceAtBestAngle(processedPoints,unistrokeTemplate,45,-45,2); //angles in degrees
            score = (double)1 - (distance / (0.5 * Math.sqrt(size*size + size*size)));

            nBestList.add(new SingleMatchResult(unistrokeTemplate, score));
            if(distance < b){
                b = distance;
                templateMatched = unistrokeTemplate;
            }
        }
        score = (double)1 - (b / (0.5 * Math.sqrt(size*size + size*size)));

        HashMap<String, Object> hm = new HashMap<>();
        hm.put("SCORE", Double.valueOf(score));
        hm.put("TEMPLATE", (UnistrokeTemplate) templateMatched);

        hm.put("N-BEST",nBestList);

        return hm;
    }


    // angle in degrees
    static public double distanceAtBestAngle(ArrayList<Point> points, UnistrokeTemplate template, double thetaA, double thetaB, double thetaDiff ){ 
        double x1 = phi * thetaA + (1 - phi) * thetaB;
        double f1 = distanceAtAngle(points,template,x1);
        double x2 = (1 - phi) * thetaA + phi * thetaB;
        double f2 = distanceAtAngle(points,template,x2);
        while(Math.abs(thetaB - thetaA) > thetaDiff){
            if (f1<f2){
                thetaB = x2;
                x2 = x1;
                f2 = f1;
                x1 = phi * thetaA + (1 - phi) * thetaB;
                f1 = distanceAtAngle(points,template,x1);
            }
            else{
                thetaA = x1;
                x1 = x2;
                f1 = f2;
                x2 =  (1 - phi) * thetaA + phi * thetaB;
                f2 = distanceAtAngle(points,template,x2);
            }
        }
        return Math.min(f1,f2);
    }
    
    static public double distanceAtAngle(ArrayList<Point> points, UnistrokeTemplate template, double theta){
        ArrayList<Point> rotatedPoints = rotateAllPoints(points, Math.toRadians(theta));
        return pathDistance(rotatedPoints,template.processedPoints);
    }

    static public double pathDistance(ArrayList<Point> points, ArrayList<Point> templatePoints){
        double d = 0.0;
        for(int i =0; i< points.size();i++){
            d = d + distance(points.get(i), templatePoints.get(i));
        }
        return d / points.size();

    }
    static public double distance(Point a, Point b)
    {
        double dx = b.x - a.x;
        double dy = b.y - a.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
}


class UnistrokeTemplate{
    public String gestureType; // renamed symbol name to gestureType 
    public ArrayList<Point> capturedPoints;
    public ArrayList<Point> processedPoints;

    //adding additional fields to support Offline recognizer
    public String fileName;
    public String gestureId;
    public int user;
    public String speed;
    public int repetition; //sample number

    static String gestureDataPath;

    UnistrokeTemplate(String name, ArrayList<Point> capturedPoints)
    {
        this.gestureType = name;
        this.capturedPoints = capturedPoints;
        processedPoints = new ArrayList<>();
        
    }

    UnistrokeTemplate(String fileName,String gestureId, String user, String speed, String gestureType, int repetition, ArrayList<Point> capturedPoints)
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

    //functions for storing template in xml format
    static void setgestureDataPath(String path){
        gestureDataPath = path;
    }

    void storeInFile(){
        Path path = Paths.get(gestureDataPath+"/"+user); 
        try{
            Files.createDirectories(path);
            System.out.println(path.toString());
            
            Document templateDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element rootElement = templateDocument.createElement("Gesture");
            rootElement.setAttribute("Name", fileName);
            rootElement.setAttribute("Subject", String.valueOf(user));
            rootElement.setAttribute("Speed", speed);
            rootElement.setAttribute("Number", String.valueOf(repetition));

            templateDocument.appendChild(rootElement);
            
            for(int i=0;i<capturedPoints.size();i++){
                Element pointElement = templateDocument.createElement("Point");
                pointElement.setAttribute("X", String.valueOf(capturedPoints.get(i).x));
                pointElement.setAttribute("Y", String.valueOf(capturedPoints.get(i).y));
                //pointElement.setAttribute("T", String.valueOf(time.get(i))); Time?
                rootElement.appendChild(pointElement);

            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            
            DOMSource domSource = new DOMSource(templateDocument);
            StreamResult gestureDataStream = new StreamResult(new File(path.toString()+"/"+fileName+".xml"));
 
            transformer.transform(domSource, gestureDataStream);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            
                
        }

    }
}

//N-best List objects 
class SingleMatchResult
{
    public UnistrokeTemplate templateGesture;
    public double score;

    // Constructor
    SingleMatchResult(UnistrokeTemplate templateGesture, double score){
        this.templateGesture = templateGesture;
        this.score = score;
    }
}

//Main class for our system which invokes the canvas panel and generates the clear button
class DollarRecognizer{
    JFrame homeFrame;
    CanvasPanel canvasWindow;
    static UnistrokeTemplate[] templates = new UnistrokeTemplate[16];
    int participantId;
    int maxSample;
    int gestureCounter;

    static String mode="recognize"; //default
    static String gestureDataPath;

    static String[] gestureNames = {"triangle","x","rectangle","circle","check","caret","zig-zag","arrow","left_square_bracket","right_square_bracket",
    "v","delete","left_curly_brace","right_curly_brace","star","pigtail"};

    static HashMap<String,Integer> currSampleCount = new HashMap<>();
    String currentGesture="";
    int currentSampleNum;
    JLabel gestureCounterLabel, pleaseDrawLabel, gestureNameLabel;
    JButton clearBtn, submitBtn;
        
    DollarRecognizer()
    {
        if(mode.equals("recognize"))
        {
			homeFrame=new JFrame("$1 - Online Recognizer");
			homeFrame.setSize(800,600);
            homeFrame.setLocationRelativeTo(null);
        }
        else if(mode.equals("collect_data"))
        {
            homeFrame=new JFrame("$1 - Data Collector");
			homeFrame.setSize(1024,668);
            homeFrame.setLocationRelativeTo(null);

            // Shows the unistrokes.png image in the right side of the homeFrame
            ImageIcon icon = new ImageIcon("unistrokes.png");
            JLabel imageLabel = new JLabel(icon);
            imageLabel.setBounds(1024/2 + 30, 180, 449, 446);
            homeFrame.add(imageLabel);

            

            pleaseDrawLabel = new JLabel("Please draw the following gesture according to the guide:");
            pleaseDrawLabel.setBounds(1024/2 + 20, 20, 500, 50);
            pleaseDrawLabel.setFont(new Font("Serif", Font.PLAIN, 15));
            homeFrame.add(pleaseDrawLabel);

            gestureNameLabel = new JLabel(currentGesture);
            gestureNameLabel.setBounds(1024/2 + 30, 60, 500, 50);
            gestureNameLabel.setFont(new Font("Serif", Font.PLAIN, 20));
            homeFrame.add(gestureNameLabel);

            gestureCounter = 1;
            gestureCounterLabel = new JLabel("Gesture " + gestureCounter + " of " + maxSample);
            gestureCounterLabel.setBounds(1024-100, 10, 100, 20);
            gestureCounterLabel.setFont(new Font("Serif", Font.PLAIN, 8));
            homeFrame.add(gestureCounterLabel);

        }
        
        setButtons();
        setCanvasPanel();
        
        homeFrame.setLayout(null);
        homeFrame.setVisible(true);
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        System.out.println("Mode "+mode);
        if(mode.equals("recognize")){
            generateTemplates();
            GestureRecognizer.setCanvasWindow(canvasWindow);
        }
        else{
            gestureDataPath = createDirectory();
            UnistrokeTemplate.setgestureDataPath(gestureDataPath);
        }
    }

    // Method that updates the gestureCounterLabel and the gestureNameLabel
    void updateLabels(){

        // If it is the last sample of the last gesture, then show the "Done" button
        if(gestureCounter > maxSample*16){

            // Hide the clear and submit buttons
            clearBtn.setVisible(false);
            submitBtn.setVisible(false);

            gestureNameLabel.setText("Click \"Done\" to finish.");
            gestureCounterLabel.setText("");
            pleaseDrawLabel.setText("Thank you for your participation!");

            // Disable the canvas panel
            canvasWindow.setEnabled(false);
                        
            // Add the done button in the center and bottom of the screen
            JButton doneBtn = new JButton("Done");
            doneBtn.setBounds(206, 668-90, 100, 40);
            doneBtn.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    homeFrame.dispose();
                    // Restart the program
                    String[] args = new String[1];
                    args[0] = "recognize";
                    DollarRecognizer.main(args);


                }
            });
            homeFrame.add(doneBtn);
            
            doneBtn.setVisible(true);
            homeFrame.update(null);
            
        }
        else {
            String gestureName = currentGesture.replace("_", " ");
            gestureNameLabel.setText(gestureName);
            gestureCounterLabel.setText("Gesture " + gestureCounter + " of " + maxSample*16);
 
        }

    }

    // Templates taken from JavaScript 
    void generateTemplates()
    {
        templates[0] = new UnistrokeTemplate("triangle", new ArrayList<Point>(Arrays.asList(new Point(137,139),new Point(135,141),new Point(133,144),new Point(132,146),new Point(130,149),new Point(128,151),new Point(126,155),new Point(123,160),new Point(120,166),new Point(116,171),new Point(112,177),new Point(107,183),new Point(102,188),new Point(100,191),new Point(95,195),new Point(90,199),new Point(86,203),new Point(82,206),new Point(80,209),new Point(75,213),new Point(73,213),new Point(70,216),new Point(67,219),new Point(64,221),new Point(61,223),new Point(60,225),new Point(62,226),new Point(65,225),new Point(67,226),new Point(74,226),new Point(77,227),new Point(85,229),new Point(91,230),new Point(99,231),new Point(108,232),new Point(116,233),new Point(125,233),new Point(134,234),new Point(145,233),new Point(153,232),new Point(160,233),new Point(170,234),new Point(177,235),new Point(179,236),new Point(186,237),new Point(193,238),new Point(198,239),new Point(200,237),new Point(202,239),new Point(204,238),new Point(206,234),new Point(205,230),new Point(202,222),new Point(197,216),new Point(192,207),new Point(186,198),new Point(179,189),new Point(174,183),new Point(170,178),new Point(164,171),new Point(161,168),new Point(154,160),new Point(148,155),new Point(143,150),new Point(138,148),new Point(136,148))));
        templates[1] = new UnistrokeTemplate("x", new ArrayList<Point>(Arrays.asList(new Point(87,142),new Point(89,145),new Point(91,148),new Point(93,151),new Point(96,155),new Point(98,157),new Point(100,160),new Point(102,162),new Point(106,167),new Point(108,169),new Point(110,171),new Point(115,177),new Point(119,183),new Point(123,189),new Point(127,193),new Point(129,196),new Point(133,200),new Point(137,206),new Point(140,209),new Point(143,212),new Point(146,215),new Point(151,220),new Point(153,222),new Point(155,223),new Point(157,225),new Point(158,223),new Point(157,218),new Point(155,211),new Point(154,208),new Point(152,200),new Point(150,189),new Point(148,179),new Point(147,170),new Point(147,158),new Point(147,148),new Point(147,141),new Point(147,136),new Point(144,135),new Point(142,137),new Point(140,139),new Point(135,145),new Point(131,152),new Point(124,163),new Point(116,177),new Point(108,191),new Point(100,206),new Point(94,217),new Point(91,222),new Point(89,225),new Point(87,226),new Point(87,224))));
        templates[2] = new UnistrokeTemplate("rectangle", new ArrayList<Point>(Arrays.asList(new Point(78,149),new Point(78,153),new Point(78,157),new Point(78,160),new Point(79,162),new Point(79,164),new Point(79,167),new Point(79,169),new Point(79,173),new Point(79,178),new Point(79,183),new Point(80,189),new Point(80,193),new Point(80,198),new Point(80,202),new Point(81,208),new Point(81,210),new Point(81,216),new Point(82,222),new Point(82,224),new Point(82,227),new Point(83,229),new Point(83,231),new Point(85,230),new Point(88,232),new Point(90,233),new Point(92,232),new Point(94,233),new Point(99,232),new Point(102,233),new Point(106,233),new Point(109,234),new Point(117,235),new Point(123,236),new Point(126,236),new Point(135,237),new Point(142,238),new Point(145,238),new Point(152,238),new Point(154,239),new Point(165,238),new Point(174,237),new Point(179,236),new Point(186,235),new Point(191,235),new Point(195,233),new Point(197,233),new Point(200,233),new Point(201,235),new Point(201,233),new Point(199,231),new Point(198,226),new Point(198,220),new Point(196,207),new Point(195,195),new Point(195,181),new Point(195,173),new Point(195,163),new Point(194,155),new Point(192,145),new Point(192,143),new Point(192,138),new Point(191,135),new Point(191,133),new Point(191,130),new Point(190,128),new Point(188,129),new Point(186,129),new Point(181,132),new Point(173,131),new Point(162,131),new Point(151,132),new Point(149,132),new Point(138,132),new Point(136,132),new Point(122,131),new Point(120,131),new Point(109,130),new Point(107,130),new Point(90,132),new Point(81,133),new Point(76,133))));
        templates[3] = new UnistrokeTemplate("circle", new ArrayList<Point>(Arrays.asList(new Point(127,141),new Point(124,140),new Point(120,139),new Point(118,139),new Point(116,139),new Point(111,140),new Point(109,141),new Point(104,144),new Point(100,147),new Point(96,152),new Point(93,157),new Point(90,163),new Point(87,169),new Point(85,175),new Point(83,181),new Point(82,190),new Point(82,195),new Point(83,200),new Point(84,205),new Point(88,213),new Point(91,216),new Point(96,219),new Point(103,222),new Point(108,224),new Point(111,224),new Point(120,224),new Point(133,223),new Point(142,222),new Point(152,218),new Point(160,214),new Point(167,210),new Point(173,204),new Point(178,198),new Point(179,196),new Point(182,188),new Point(182,177),new Point(178,167),new Point(170,150),new Point(163,138),new Point(152,130),new Point(143,129),new Point(140,131),new Point(129,136),new Point(126,139))));
        templates[4] = new UnistrokeTemplate("check", new ArrayList<Point>(Arrays.asList(new Point(91,185),new Point(93,185),new Point(95,185),new Point(97,185),new Point(100,188),new Point(102,189),new Point(104,190),new Point(106,193),new Point(108,195),new Point(110,198),new Point(112,201),new Point(114,204),new Point(115,207),new Point(117,210),new Point(118,212),new Point(120,214),new Point(121,217),new Point(122,219),new Point(123,222),new Point(124,224),new Point(126,226),new Point(127,229),new Point(129,231),new Point(130,233),new Point(129,231),new Point(129,228),new Point(129,226),new Point(129,224),new Point(129,221),new Point(129,218),new Point(129,212),new Point(129,208),new Point(130,198),new Point(132,189),new Point(134,182),new Point(137,173),new Point(143,164),new Point(147,157),new Point(151,151),new Point(155,144),new Point(161,137),new Point(165,131),new Point(171,122),new Point(174,118),new Point(176,114),new Point(177,112),new Point(177,114),new Point(175,116),new Point(173,118))));
        templates[5] = new UnistrokeTemplate("caret", new ArrayList<Point>(Arrays.asList(new Point(79,245),new Point(79,242),new Point(79,239),new Point(80,237),new Point(80,234),new Point(81,232),new Point(82,230),new Point(84,224),new Point(86,220),new Point(86,218),new Point(87,216),new Point(88,213),new Point(90,207),new Point(91,202),new Point(92,200),new Point(93,194),new Point(94,192),new Point(96,189),new Point(97,186),new Point(100,179),new Point(102,173),new Point(105,165),new Point(107,160),new Point(109,158),new Point(112,151),new Point(115,144),new Point(117,139),new Point(119,136),new Point(119,134),new Point(120,132),new Point(121,129),new Point(122,127),new Point(124,125),new Point(126,124),new Point(129,125),new Point(131,127),new Point(132,130),new Point(136,139),new Point(141,154),new Point(145,166),new Point(151,182),new Point(156,193),new Point(157,196),new Point(161,209),new Point(162,211),new Point(167,223),new Point(169,229),new Point(170,231),new Point(173,237),new Point(176,242),new Point(177,244),new Point(179,250),new Point(181,255),new Point(182,257))));
        templates[6] = new UnistrokeTemplate("zig-zag", new ArrayList<Point>(Arrays.asList(new Point(307,216),new Point(333,186),new Point(356,215),new Point(375,186),new Point(399,216),new Point(418,186))));
        templates[7] = new UnistrokeTemplate("arrow", new ArrayList<Point>(Arrays.asList(new Point(68,222),new Point(70,220),new Point(73,218),new Point(75,217),new Point(77,215),new Point(80,213),new Point(82,212),new Point(84,210),new Point(87,209),new Point(89,208),new Point(92,206),new Point(95,204),new Point(101,201),new Point(106,198),new Point(112,194),new Point(118,191),new Point(124,187),new Point(127,186),new Point(132,183),new Point(138,181),new Point(141,180),new Point(146,178),new Point(154,173),new Point(159,171),new Point(161,170),new Point(166,167),new Point(168,167),new Point(171,166),new Point(174,164),new Point(177,162),new Point(180,160),new Point(182,158),new Point(183,156),new Point(181,154),new Point(178,153),new Point(171,153),new Point(164,153),new Point(160,153),new Point(150,154),new Point(147,155),new Point(141,157),new Point(137,158),new Point(135,158),new Point(137,158),new Point(140,157),new Point(143,156),new Point(151,154),new Point(160,152),new Point(170,149),new Point(179,147),new Point(185,145),new Point(192,144),new Point(196,144),new Point(198,144),new Point(200,144),new Point(201,147),new Point(199,149),new Point(194,157),new Point(191,160),new Point(186,167),new Point(180,176),new Point(177,179),new Point(171,187),new Point(169,189),new Point(165,194),new Point(164,196))));
        templates[8] = new UnistrokeTemplate("left square bracket", new ArrayList<Point>(Arrays.asList(new Point(140,124),new Point(138,123),new Point(135,122),new Point(133,123),new Point(130,123),new Point(128,124),new Point(125,125),new Point(122,124),new Point(120,124),new Point(118,124),new Point(116,125),new Point(113,125),new Point(111,125),new Point(108,124),new Point(106,125),new Point(104,125),new Point(102,124),new Point(100,123),new Point(98,123),new Point(95,124),new Point(93,123),new Point(90,124),new Point(88,124),new Point(85,125),new Point(83,126),new Point(81,127),new Point(81,129),new Point(82,131),new Point(82,134),new Point(83,138),new Point(84,141),new Point(84,144),new Point(85,148),new Point(85,151),new Point(86,156),new Point(86,160),new Point(86,164),new Point(86,168),new Point(87,171),new Point(87,175),new Point(87,179),new Point(87,182),new Point(87,186),new Point(88,188),new Point(88,195),new Point(88,198),new Point(88,201),new Point(88,207),new Point(89,211),new Point(89,213),new Point(89,217),new Point(89,222),new Point(88,225),new Point(88,229),new Point(88,231),new Point(88,233),new Point(88,235),new Point(89,237),new Point(89,240),new Point(89,242),new Point(91,241),new Point(94,241),new Point(96,240),new Point(98,239),new Point(105,240),new Point(109,240),new Point(113,239),new Point(116,240),new Point(121,239),new Point(130,240),new Point(136,237),new Point(139,237),new Point(144,238),new Point(151,237),new Point(157,236),new Point(159,237))));
        templates[9] = new UnistrokeTemplate("right square bracket", new ArrayList<Point>(Arrays.asList(new Point(112,138),new Point(112,136),new Point(115,136),new Point(118,137),new Point(120,136),new Point(123,136),new Point(125,136),new Point(128,136),new Point(131,136),new Point(134,135),new Point(137,135),new Point(140,134),new Point(143,133),new Point(145,132),new Point(147,132),new Point(149,132),new Point(152,132),new Point(153,134),new Point(154,137),new Point(155,141),new Point(156,144),new Point(157,152),new Point(158,161),new Point(160,170),new Point(162,182),new Point(164,192),new Point(166,200),new Point(167,209),new Point(168,214),new Point(168,216),new Point(169,221),new Point(169,223),new Point(169,228),new Point(169,231),new Point(166,233),new Point(164,234),new Point(161,235),new Point(155,236),new Point(147,235),new Point(140,233),new Point(131,233),new Point(124,233),new Point(117,235),new Point(114,238),new Point(112,238))));
        templates[10] = new UnistrokeTemplate("v", new ArrayList<Point>(Arrays.asList(new Point(89,164),new Point(90,162),new Point(92,162),new Point(94,164),new Point(95,166),new Point(96,169),new Point(97,171),new Point(99,175),new Point(101,178),new Point(103,182),new Point(106,189),new Point(108,194),new Point(111,199),new Point(114,204),new Point(117,209),new Point(119,214),new Point(122,218),new Point(124,222),new Point(126,225),new Point(128,228),new Point(130,229),new Point(133,233),new Point(134,236),new Point(136,239),new Point(138,240),new Point(139,242),new Point(140,244),new Point(142,242),new Point(142,240),new Point(142,237),new Point(143,235),new Point(143,233),new Point(145,229),new Point(146,226),new Point(148,217),new Point(149,208),new Point(149,205),new Point(151,196),new Point(151,193),new Point(153,182),new Point(155,172),new Point(157,165),new Point(159,160),new Point(162,155),new Point(164,150),new Point(165,148),new Point(166,146))));
        templates[11] = new UnistrokeTemplate("delete", new ArrayList<Point>(Arrays.asList(new Point(123,129),new Point(123,131),new Point(124,133),new Point(125,136),new Point(127,140),new Point(129,142),new Point(133,148),new Point(137,154),new Point(143,158),new Point(145,161),new Point(148,164),new Point(153,170),new Point(158,176),new Point(160,178),new Point(164,183),new Point(168,188),new Point(171,191),new Point(175,196),new Point(178,200),new Point(180,202),new Point(181,205),new Point(184,208),new Point(186,210),new Point(187,213),new Point(188,215),new Point(186,212),new Point(183,211),new Point(177,208),new Point(169,206),new Point(162,205),new Point(154,207),new Point(145,209),new Point(137,210),new Point(129,214),new Point(122,217),new Point(118,218),new Point(111,221),new Point(109,222),new Point(110,219),new Point(112,217),new Point(118,209),new Point(120,207),new Point(128,196),new Point(135,187),new Point(138,183),new Point(148,167),new Point(157,153),new Point(163,145),new Point(165,142),new Point(172,133),new Point(177,127),new Point(179,127),new Point(180,125))));
        templates[12] = new UnistrokeTemplate("left curly brace", new ArrayList<Point>(Arrays.asList(new Point(150,116),new Point(147,117),new Point(145,116),new Point(142,116),new Point(139,117),new Point(136,117),new Point(133,118),new Point(129,121),new Point(126,122),new Point(123,123),new Point(120,125),new Point(118,127),new Point(115,128),new Point(113,129),new Point(112,131),new Point(113,134),new Point(115,134),new Point(117,135),new Point(120,135),new Point(123,137),new Point(126,138),new Point(129,140),new Point(135,143),new Point(137,144),new Point(139,147),new Point(141,149),new Point(140,152),new Point(139,155),new Point(134,159),new Point(131,161),new Point(124,166),new Point(121,166),new Point(117,166),new Point(114,167),new Point(112,166),new Point(114,164),new Point(116,163),new Point(118,163),new Point(120,162),new Point(122,163),new Point(125,164),new Point(127,165),new Point(129,166),new Point(130,168),new Point(129,171),new Point(127,175),new Point(125,179),new Point(123,184),new Point(121,190),new Point(120,194),new Point(119,199),new Point(120,202),new Point(123,207),new Point(127,211),new Point(133,215),new Point(142,219),new Point(148,220),new Point(151,221))));
        templates[13] = new UnistrokeTemplate("right curly brace", new ArrayList<Point>(Arrays.asList(new Point(117,132),new Point(115,132),new Point(115,129),new Point(117,129),new Point(119,128),new Point(122,127),new Point(125,127),new Point(127,127),new Point(130,127),new Point(133,129),new Point(136,129),new Point(138,130),new Point(140,131),new Point(143,134),new Point(144,136),new Point(145,139),new Point(145,142),new Point(145,145),new Point(145,147),new Point(145,149),new Point(144,152),new Point(142,157),new Point(141,160),new Point(139,163),new Point(137,166),new Point(135,167),new Point(133,169),new Point(131,172),new Point(128,173),new Point(126,176),new Point(125,178),new Point(125,180),new Point(125,182),new Point(126,184),new Point(128,187),new Point(130,187),new Point(132,188),new Point(135,189),new Point(140,189),new Point(145,189),new Point(150,187),new Point(155,186),new Point(157,185),new Point(159,184),new Point(156,185),new Point(154,185),new Point(149,185),new Point(145,187),new Point(141,188),new Point(136,191),new Point(134,191),new Point(131,192),new Point(129,193),new Point(129,195),new Point(129,197),new Point(131,200),new Point(133,202),new Point(136,206),new Point(139,211),new Point(142,215),new Point(145,220),new Point(147,225),new Point(148,231),new Point(147,239),new Point(144,244),new Point(139,248),new Point(134,250),new Point(126,253),new Point(119,253),new Point(115,253))));
        templates[14] = new UnistrokeTemplate("star", new ArrayList<Point>(Arrays.asList(new Point(75,250),new Point(75,247),new Point(77,244),new Point(78,242),new Point(79,239),new Point(80,237),new Point(82,234),new Point(82,232),new Point(84,229),new Point(85,225),new Point(87,222),new Point(88,219),new Point(89,216),new Point(91,212),new Point(92,208),new Point(94,204),new Point(95,201),new Point(96,196),new Point(97,194),new Point(98,191),new Point(100,185),new Point(102,178),new Point(104,173),new Point(104,171),new Point(105,164),new Point(106,158),new Point(107,156),new Point(107,152),new Point(108,145),new Point(109,141),new Point(110,139),new Point(112,133),new Point(113,131),new Point(116,127),new Point(117,125),new Point(119,122),new Point(121,121),new Point(123,120),new Point(125,122),new Point(125,125),new Point(127,130),new Point(128,133),new Point(131,143),new Point(136,153),new Point(140,163),new Point(144,172),new Point(145,175),new Point(151,189),new Point(156,201),new Point(161,213),new Point(166,225),new Point(169,233),new Point(171,236),new Point(174,243),new Point(177,247),new Point(178,249),new Point(179,251),new Point(180,253),new Point(180,255),new Point(179,257),new Point(177,257),new Point(174,255),new Point(169,250),new Point(164,247),new Point(160,245),new Point(149,238),new Point(138,230),new Point(127,221),new Point(124,220),new Point(112,212),new Point(110,210),new Point(96,201),new Point(84,195),new Point(74,190),new Point(64,182),new Point(55,175),new Point(51,172),new Point(49,170),new Point(51,169),new Point(56,169),new Point(66,169),new Point(78,168),new Point(92,166),new Point(107,164),new Point(123,161),new Point(140,162),new Point(156,162),new Point(171,160),new Point(173,160),new Point(186,160),new Point(195,160),new Point(198,161),new Point(203,163),new Point(208,163),new Point(206,164),new Point(200,167),new Point(187,172),new Point(174,179),new Point(172,181),new Point(153,192),new Point(137,201),new Point(123,211),new Point(112,220),new Point(99,229),new Point(90,237),new Point(80,244),new Point(73,250),new Point(69,254),new Point(69,252))));
        templates[15] = new UnistrokeTemplate("pigtail", new ArrayList<Point>(Arrays.asList(new Point(81,219),new Point(84,218),new Point(86,220),new Point(88,220),new Point(90,220),new Point(92,219),new Point(95,220),new Point(97,219),new Point(99,220),new Point(102,218),new Point(105,217),new Point(107,216),new Point(110,216),new Point(113,214),new Point(116,212),new Point(118,210),new Point(121,208),new Point(124,205),new Point(126,202),new Point(129,199),new Point(132,196),new Point(136,191),new Point(139,187),new Point(142,182),new Point(144,179),new Point(146,174),new Point(148,170),new Point(149,168),new Point(151,162),new Point(152,160),new Point(152,157),new Point(152,155),new Point(152,151),new Point(152,149),new Point(152,146),new Point(149,142),new Point(148,139),new Point(145,137),new Point(141,135),new Point(139,135),new Point(134,136),new Point(130,140),new Point(128,142),new Point(126,145),new Point(122,150),new Point(119,158),new Point(117,163),new Point(115,170),new Point(114,175),new Point(117,184),new Point(120,190),new Point(125,199),new Point(129,203),new Point(133,208),new Point(138,213),new Point(145,215),new Point(155,218),new Point(164,219),new Point(166,219),new Point(177,219),new Point(182,218),new Point(192,216),new Point(196,213),new Point(199,212),new Point(201,211))));
    
        for (UnistrokeTemplate unistrokeTemplate : templates) {
            unistrokeTemplate.processedPoints = GestureRecognizer.processingGesture(unistrokeTemplate.capturedPoints);
        }
    }

    void setCanvasPanel()
    {
        canvasWindow = new CanvasPanel(mode);
        homeFrame.add(canvasWindow);
            
    }

    void setButtons()
    {
        // Create a button that says "Clear"

        clearBtn = new JButton("Clear");
        clearBtn.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e){  
                        canvasWindow.repaint();
                        if(mode.equals("recognize"))
                            canvasWindow.clearLabel();  
                        //System.out.println(((JLabel)canvasWindow.getComponent(0)).getText());
                        //((JLabel)canvasWindow.getComponent(0)).setText("");
                        //homeFrame.setLayout(null);
                    }  
                });  

		if(mode.equals("recognize"))
        	clearBtn.setBounds(350,500,100, 40);         
		else if(mode.equals("collect_data"))
			clearBtn.setBounds(50, 578, 100, 40);      
        homeFrame.add(clearBtn);


		if(mode.equals("collect_data")){
	        // Create a button that says "Submit"
	        submitBtn = new JButton("Submit");
	        submitBtn.addActionListener(new ActionListener(){  
	            public void actionPerformed(ActionEvent e){  
	                gestureCounter++;
                    UnistrokeTemplate template = new UnistrokeTemplate(currentGesture+currentSampleNum, "" , String.valueOf(participantId) , "medium" , currentGesture , currentSampleNum, canvasWindow.capturedPoints);
                    template.storeInFile();
                    currentGesture = getRandomGesture();
                    currentSampleNum = currSampleCount.get(currentGesture);
                    updateLabels();
                    canvasWindow.repaint();
	            }
	        });

	        submitBtn.setBounds(1024/2 - 150, 578, 100, 40);
	        homeFrame.add(submitBtn);
        }

    }
	
    String createDirectory(){ // creating directly inside code folder currently :ANISHA 
        String xmlDirPath = System.getProperty("user.dir");
        try{
            Path path = Paths.get(xmlDirPath+"/templateData"); 
            Files.createDirectories(path);
            System.out.println(path.toAbsolutePath().toString());
            return path.toAbsolutePath().toString();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return "";
        
        
    }

    protected void setMaxSamples(int maxSample) {
        this.maxSample = maxSample;
    }


    protected void setParticipantID(int participantId) {
        this.participantId = participantId;
    }

    String getRandomGesture(){ // all 160 in random : ALEX ANISHA
        int index,count;
        do{
            index = new Random().nextInt(16);
            count = currSampleCount.getOrDefault(gestureNames[index],0);
        } while(count>=maxSample);

        currSampleCount.put(gestureNames[index], count+1);
        return gestureNames[index] ;
    }


    public static void main(String args[]){
        // When the system starts, ask the user to select between two different modes of operation
        // 1. Train the system
        // 2. Use the system
        // If the user selects 1, invoke DollarRecognizer(1), if 2, invoke DollarRecognizer(0)

        // Create a window that asks the user to select between two different modes of operation
        JFrame modeFrame = new JFrame("$1 - Select Mode");
        modeFrame.setSize(400, 200);
        modeFrame.setLocationRelativeTo(null);




        modeFrame.setLayout(null);
        modeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        modeFrame.setVisible(true);

        // Creates a centered bold title label that says "HCIRA - $1 Gesture Recognizer"
        JLabel titleLabel = new JLabel("HCIRA - $1 Gesture Recognizer");
        
        titleLabel.setBounds(50,10,300,40);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 15));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        modeFrame.add(titleLabel);

        // Creates a centered subtitle label that says "by Alexander Barquero and Anisha Wadhwani"
        JLabel subtitleLabel = new JLabel("by Alexander Barquero and Anisha Wadhwani");
        subtitleLabel.setBounds(50,40,300,40);
        subtitleLabel.setHorizontalAlignment(JLabel.CENTER);
        modeFrame.add(subtitleLabel);


        // Creates a button that says "Collect Data"
        JButton trainBtn = new JButton("Collect Data");
        trainBtn.setBounds(50,100,150,40);
        trainBtn.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e){  
                        // Creates a window that asks the user to insert the configuration information
                        JFrame trainFrame = new JFrame("$1 - Collect Data");
                        trainFrame.setSize(400, 200);
                        trainFrame.setLocationRelativeTo(null);
                        trainFrame.setLayout(null);
                        trainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        trainFrame.setVisible(true);

                        JLabel idLabel = new JLabel("Participant ID");
                        idLabel.setBounds(50,10,100,40);
                        trainFrame.add(idLabel);

                        // Creates a text field that asks the user to insert the participant ID
                        JTextField idField = new JTextField();
                        idField.setBounds(50,50,100,40);
                        trainFrame.add(idField);

                        JLabel sampleLabel = new JLabel("Number of Samples");
                        sampleLabel.setBounds(200,10,150,40);
                        trainFrame.add(sampleLabel);

                        // Creates a text field that asks the user to insert the number of samples, with 10 by default
                        JTextField sampleField = new JTextField("10");
                        sampleField.setBounds(200,50,150,40);
                        trainFrame.add(sampleField);

                        // Creates a button that says "Start"
                        JButton startBtn = new JButton("Start");
                        startBtn.setBounds(150,100,100,40);
                        startBtn.addActionListener(new ActionListener(){  
                            public void actionPerformed(ActionEvent e){  
                                        // Create a new DollarRecognizer object
                                        mode = "collect_data";
                                        DollarRecognizer dr = new DollarRecognizer();
                                        // Set the participant ID
                                        dr.setParticipantID(Integer.parseInt(idField.getText()));
                                        // Set the number of samples
                                        dr.setMaxSamples(Integer.parseInt(sampleField.getText()));
                                        
                                        dr.currentGesture = dr.getRandomGesture();
                                        dr.currentSampleNum = currSampleCount.get(dr.currentGesture);

                                        dr.updateLabels();
                                        trainFrame.dispose();
                                        modeFrame.dispose();
                                    }  
                                });
                        trainFrame.add(startBtn);
                    }
                });
        modeFrame.add(trainBtn);

        // Creates a button that says "Use"
        JButton useBtn = new JButton("Use");
        useBtn.setBounds(250,100,100,40);
        useBtn.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e){ 
                        DollarRecognizer.mode = "recognize";
                        new DollarRecognizer();
                        modeFrame.dispose();
                    }  
                });
        modeFrame.add(useBtn);
    }
}