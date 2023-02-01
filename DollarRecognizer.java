// CIS6930 Special Topics: Human-Centered Input Recognition Algorithms
// $1 Recognizer implementation by Alexander Barquero and Anisha Wadhwani


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

//Class that creates the canvas panel which will be used to draw the gesture
class CanvasPanel extends JPanel implements MouseListener, MouseMotionListener{

    private boolean userIsStroking;
    private int initX, initY, newX, newY;

    //Set all canvas parameters at initialization
    CanvasPanel(){
        setBackground(Color.BLACK);
        setSize(800, 600);
        setVisible(true);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    //Action on mouse pressed
    public void mousePressed(MouseEvent e)
    {
        Graphics2D graphics = (Graphics2D)getGraphics();

        initX = newX = e.getX();
        initY = newY = e.getY();
        graphics.setColor(Color.RED);
        graphics.setStroke(new BasicStroke(10));
        graphics.drawLine(initX, initY, newX, newY);
        userIsStroking = true;
        // System.out.println("Mouse Clicked at "+x+" "+y);
        
    }

    //Action on mouse dragged    
    public void mouseDragged(MouseEvent e)
    {
        if (userIsStroking) {
            newX = e.getX();
            newY = e.getY();
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
    }

    public void mouseMoved(MouseEvent e){}
 
    public void mouseExited(MouseEvent e){}
 
    public void mouseEntered(MouseEvent e){}
 
    public void mouseClicked(MouseEvent e){}

}


//Main class for our system which invokes the canvas panel and generates the clear button
class DollarRecognizer{
    JFrame homeFrame;
    CanvasPanel canvasWindow;
    DollarRecognizer(){
        homeFrame=new JFrame("$1 Recognizer");
        setButtons();
        setCanvasPanel();
        homeFrame.setSize(800,600);
        homeFrame.setLayout(null);
        homeFrame.setVisible(true);
        homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        homeFrame.setLocation(dim.width/2 - 400, dim.height/2 - 300);


    }
    void setCanvasPanel() {
        canvasWindow = new CanvasPanel();
        homeFrame.add(canvasWindow);
    }
    void setButtons(){
        JButton clearBtn = new JButton("Clear");
        clearBtn.setBounds(350,500,100, 40);
        clearBtn.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e){  
                        canvasWindow.repaint();  
                    }  
                });  
        homeFrame.add(clearBtn);
 
    }   
    public static void main(String args[]){
        new DollarRecognizer();
    }
}