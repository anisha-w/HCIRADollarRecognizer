import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


class CanvasPanel extends JPanel implements MouseListener, MouseMotionListener{

    //set all canvas parameters at initialization
    CanvasPanel(){
        setBackground(Color.WHITE);
        setSize(400, 300);
        setVisible(true);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    //define functions of interface MouseListener
    public void mouseClicked(MouseEvent e)
    {
        Graphics graphics = getGraphics();
        graphics.setColor(Color.blue);

        int x = e.getX();
        int y = e.getY();
        // System.out.println("Mouse Clicked at "+x+" "+y);
 
        graphics.fillOval(x, y, 5, 5);
    }
 
    //define functions of interface MouseMotionListener
    public void mouseDragged(MouseEvent e)
    {
        Graphics graphics= getGraphics();
        graphics.setColor(Color.blue);
 
        int x = e.getX();
        int y = e.getY();
        // System.out.println("Mouse dragged "+x+" "+y);
        
        graphics.fillOval(x, y, 5, 5);
    }

    public void mouseMoved(MouseEvent e){}
 
    public void mouseExited(MouseEvent e){}
 
    public void mouseEntered(MouseEvent e){}
 
    public void mouseReleased(MouseEvent e){}
 
    public void mousePressed(MouseEvent e){}

}

class Home{
    JFrame homeFrame;
    CanvasPanel canvasWindow;
    Home(){
        homeFrame=new JFrame("$1 Recognizer");
        setButtons();
        setCanvasPanel();
        homeFrame.setSize(400,500);
        homeFrame.setLayout(null);
        homeFrame.setVisible(true);
    }
    void setCanvasPanel() {
        canvasWindow = new CanvasPanel();
        homeFrame.add(canvasWindow);
    }
    void setButtons(){
        JButton clearBtn=new JButton("Clear");
        clearBtn.setBounds(150,400,100, 40);
        clearBtn.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e){  
                        canvasWindow.repaint();  
                    }  
                });  
        homeFrame.add(clearBtn);
 
    }   
    public static void main(String args[]){
        new Home();
    }
}