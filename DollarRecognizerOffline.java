import java.util.*;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.awt.*;

class UnistrokeTemplate{
    public String name;
    public ArrayList<Point> capturedPoints;
    public ArrayList<Point> processedPoints;

    public String fileName;

    UnistrokeTemplate(String name, String fileName ,ArrayList<Point> capturedPoints)
    {
        this.name = name;
        this.capturedPoints = capturedPoints;
        this.fileName = fileName;
        processedPoints = new ArrayList<>();
        
    }
}

public class DollarRecognizerOffline {

    DollarRecognizerOffline() throws Exception{
        extractDataFromXML();
    }

    public static UnistrokeTemplate readPoints(File templateFile) throws Exception{

        ArrayList<Point> templatePoints = new ArrayList<>();
        
        Document templateXML = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(templateFile);  
        
        templateXML.getDocumentElement().normalize();  
        
        //System.out.println("Root element: " + templateXML.getDocumentElement().getNodeName());  
        NodeList nodeList = templateXML.getElementsByTagName("Point");
        for(int i=0;i<nodeList.getLength();i++){
            //System.out.println(nodeList.item(i).getAttributes().getNamedItem("X").getNodeValue());
            int x = Integer.parseInt(nodeList.item(i).getAttributes().getNamedItem("X").getNodeValue());
            int y = Integer.parseInt(nodeList.item(i).getAttributes().getNamedItem("Y").getNodeValue());
            templatePoints.add(new Point(x,y));
        }

        return new UnistrokeTemplate(templateXML.getDocumentElement().getAttribute("Name"),templateFile.getName(), templatePoints); //crosscheck

    }

    public static void extractDataFromXML() throws Exception{
        //File xmlDir = new File("/Users/anishawadhwani/Documents/UFL_Resources/Projects/HCIRA/Project1Part3_Resources"); //ANISHA : TO DO TEST
        //File xmlDir = new File("../");
        String xmlDirPath = System.getProperty("user.dir").substring(0, System.getProperty("user.dir").lastIndexOf("/"));
        File xmlDir = new File(xmlDirPath + "/Project1Part3_Resources/xml_logs");
        System.out.println(xmlDir.getAbsoluteFile().exists());
        //System.out.println(System.getProperty("user.dir"));
        System.out.println(xmlDir.getAbsolutePath());
        
        String dirList[] = xmlDir.list(); 
         
        for(int i=1;i<dirList.length;i++){
            //System.out.println(dirList[i]); 
            if(dirList[i].length()==3 && !dirList[i].contains("pilot")){
                File userDir = new File(xmlDir.getAbsoluteFile()+"/"+dirList[i]+"/medium");
                String userTemplateList[] = userDir.list(); 
                System.out.println(userTemplateList.length);
                for(int j=0;j<userTemplateList.length;j++){
                    System.out.println("\t"+userTemplateList[j]); 
                    UnistrokeTemplate unistrokeTemplate= readPoints(new File(userDir.getAbsoluteFile()+"/"+userTemplateList[j])); //to store in a array list of arraylists
                    // = new UnistrokeTemplate(userTemplateList[i], points); 
                    
                    
                }
            }
        }
    }
    public static void main(String args[]) throws Exception{
        new DollarRecognizerOffline();
    }
}
