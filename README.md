### Alexander Barquero & Anisha Wadhwani
Project #1: Part 4: Collecting Data from People
### CIS6930: HCIRA

# Introduction
We present our implementation of the $1 Recognizer, based on the work done by Wobbrock, Wilson and Li.
In this Part 4, we implement the data collection version of the $1 Recognizer. A new option is added to the existing recognizer system, where the user can choose to either use or collect_data. In data collection mode, the participant id and number of samples per user is taken and then the user is shown the gesture name and a sample drawing for reference. This data is stored in xml files. 

# System
The application was developed using the Java language, specifically with the Java Standard Edition 18 SDK. 
Implementation was done in Microsoft Visual Studio Code, which is a simple but powerful solution that supports all the necessary IDE features that our team requires for this particular endeavor.
We use a Github repository for source code version control.


# Instalation and Execution

You can run the system from any Java enabled IDE, by using the standard running functionalities. As usual, make sure you have Java compiling and runtime capabilities in your computer.

If you want to run from a console, you also need to make sure you have Java compiling and runtime capabilities in your computer. Once that is done, please navigate to the folder root where the .java files are located, and execute the following commands in order:  

```sh
javac DollarRecognizer.java
java DollarRecognizer
```

# Application Features

- Fourth version adequately running on a Java environment.
- Collect data and Use options displayed
- Participant ID and number of samples taken
- Gesture name, reference image and counter for how much gestures have been drawn yet are displayed. 
- Clear and Submit buttons displayed for user. Clear allows user to redraw, and submit stores the data in XML. 
- In case user hits submit without drawing, a popup message is displayed requesting the user to draw. 

# Goals and Coding Features

## Part 4

## a) update your GUI canvas code to write the gesture the user draws to a file;
### File DollarRecognizer; Method extractDataFromXML (line 149)


## b) add prompts to the user to draw 10 samples of each gesture type one at a time (and write them to files)
### File DollarRecognizer; 


## c) recruit 6 people to provide gesture samples for your project  
### File DollarRecognizer; 


## d) submit your full dataset.
### File DollarRecognizer; 


# License
MIT

