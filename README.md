### Alexander Barquero & Anisha Wadhwani
# Project #1: Part 1: Drawing on a Canvas
### CIS6930: HCIRA

## Introduction
We present our implementation of the $1 Recognizer, based on the work done by [Wobbrock, Wilson and Li][recognizer].
In this Part 1, we show a blank canvas that can capture strokes, which also includes a button to clear the screen.

## System
The application was developed using the Java language, specifically with the Java Standard Edition 18 SDK. 
Implementation was done in Microsoft Visual Studio Code, which is a simple but powerful solution that supports all the necessary IDE features that our team requires for this particular endeavor.

## User Guide
- The user can click with his mouse anywhere inside the canvas, and the system will capture the mouse press and start drawing.
- The system will continue drawing until the user releases the mouse press.
- The user can click on the Clear button to clear the screen.
- The user can click on the X button in the corner to close the application.

## Instalation and Execution

You can run the system from any Java enabled IDE, by using the standard running functionalities. As usual, make sure you have Java compiling and runtime capabilities in your computer.

If you want to run from a console, you also need to make sure you have Java compiling and runtime capabilities in your computer. Once that is done, please navigate to the folder root where the .java files are located, and execute the following commands in order:  

```sh
javac DollarRecognizer.java
java DollarRecognizer
```

## Features

- First application version adequately running on a Java environment.
- Blank canvas shown when executed.
- User can draw in the canvas with their mouse, and the strokes will be displayed accordingly.
- Includes a Clear 

## License
MIT


  [recognizer]: <(https://depts.washington.edu/acelab/proj/dollar/index.html#:~:text=The%20%241%20Unistroke%20Recognizer%20is,i.e.%2C%20a%20geometric%20template%20matcher)>
