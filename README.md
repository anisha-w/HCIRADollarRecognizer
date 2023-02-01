### Alexander Barquero & Anisha Wadhwani
# Project #1: Part 1: Drawing on a Canvas
### CIS6930: HCIRA

# Introduction
We present our implementation of the $1 Recognizer, based on the work done by Wobbrock, Wilson and Li.
In this Part 1, we show a blank canvas that can capture strokes, which also includes a button to clear the screen.

# System
The application was developed using the Java language, specifically with the Java Standard Edition 18 SDK. 
Implementation was done in Microsoft Visual Studio Code, which is a simple but powerful solution that supports all the necessary IDE features that our team requires for this particular endeavor.
We use a Github repository for source code version control.

# User Guide
- The user can click with his mouse anywhere inside the canvas, and the system will capture the mouse press and start drawing.
- The system will continue drawing until the user releases the mouse press.
- The user can click on the Clear button to clear the screen.
- The user can click on the X button in the corner to close the application.

# Instalation and Execution

You can run the system from any Java enabled IDE, by using the standard running functionalities. As usual, make sure you have Java compiling and runtime capabilities in your computer.

If you want to run from a console, you also need to make sure you have Java compiling and runtime capabilities in your computer. Once that is done, please navigate to the folder root where the .java files are located, and execute the following commands in order:  

```sh
javac DollarRecognizer.java
java DollarRecognizer
```

# Application Features

- First version adequately running on a Java environment.
- Blank canvas shown when executed.
- User can draw in the canvas with their mouse, and the strokes will be displayed accordingly.
- Includes a Clear button that cleans the screen for the user.

# Goals and Coding Features

## a) set up your project development environment - DollarRecognizer.java in Visual Studio Code. 

## b) instantiate a blank ‘canvas’ to the screen using GUI elements
### Class CanvasPanel (line 10)
Creates the canvas that will be used to draw the gesture. Also holds the mouse interaction events.
### Class DollarRecognizer (line 72)
Main class for our system. It invokes the CanvasPanel to create the canvas, centers the canvas on screen, and generates the button that can clear the screen when clicked by the user.

## c) listen for mouse or touch events on the canvas and draw them as the user makes them; and  
### Method mousePressed (line 25)
Activates when the mouse button is initially pressed. Creates a single red point to mark the stroke starting point, and activates a system flag that informs the drag method that the mouse has been pressed so as to continue the line drawing.
### Method mouseDragged (line 40)
Activates when the mouse is dragged. If the mouse pressed flag is activated, it extends the current stroke to include both the new and previous registered X,Y point in the canvas.
### Method mouseReleased (line 56)
Activates when the mouse button is released. It deactivates the flag.

## d) allow the user to clear the canvas. 
### Method setButtons (line 92)
Generates the button that can clear the screen when clicked by the user, and locates it in the lower middle section of the screen.

# License
MIT

