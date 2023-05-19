
### Alexander Barquero & Anisha Wadhwani

# Project #1 : Implement $1 Gesture Recognition Algorithm
#### Course : CIS6930: Human-Centered Input Recognition Algorithms (HCIRA)

## Introduction
We present our implementation of the $1 Recognizer, based on the work done by Wobbrock, Wilson and Li.

## Problem Statement
The goal of the project is to implement the $1 Gesture recognizer algorithm for the 16 gestures given below. Additionally the project goals comprises of the following:
- Developing an Offline recognition module for analysis.
- Developing a module to collect data from users for offline analysis. 

#### Features of the Online/Live Recognizer GUI system: 
- The system identifies 16 specified unistroke gestures.
- Users can draw gestures on the canvas using a mouse, stylus, or finger.
- The system considers the gesture drawing complete as soon as the user lifts their pen or releases their mouse.
- Upon completing a gesture, the system promptly displays the recognized gesture name along with a similarity score, indicating the degree of match with known gestures.
- The system includes a Clear button that allows users to easily clean the screen, providing a fresh canvas for drawing new gestures.

#### Features of the User Data Collection GUI system:
- Gesture name, reference image and counter for how much gestures have been drawn yet are displayed.
- Gesture types are collected in a random order to accommodate for user fatigue.
- Clear and Submit buttons displayed for user. Clear allows user to redraw, and submit stores the data in XML. 
- In case user hits submit without drawing, a popup message is displayed requesting the user to draw.

#### Project milestones
The project has been divided into 5 milestones to build the complete system 
Part 1 : Drawing on a Canvas 
Part 2 : Online/Live Recognition 
Part 3 : Offline/Test Recognition with $1 
Part 4 : Collecting Data from People 
Part 5 : Exploring Data from People 

## System
Language : Java 
IDE : Microsoft Visual Studio Code 
Version Control : Github repository

## Installation and Execution

System requirements : The computer must have Java compiling and runtime capabilities available.

#### Compile files
```sh
cd <folder_name>
javac  DollarRecognizer.java
javac  DollarRecognizerOffline.java
```

#### Run Online recognizer 
```sh
java DollarRecognizer
```

#### Run Offline recognizer 
```sh
java DollarRecognizerOffline
```  

## Implementation

#### Unistroke Gestures 
![unistrokes](https://github.com/alebar000/HCIRADollarRecognizer/assets/36306448/b014c199-89ae-43b4-84b8-42e223fb69d4)

#### Online Recognizer GUI
<img width="263" alt="image" src="https://github.com/alebar000/HCIRADollarRecognizer/assets/36306448/5f10e6bb-9f97-4b04-b137-039f40abc57f">

#### Data Collection GUI 
<img width="384" alt="image" src="https://github.com/alebar000/HCIRADollarRecognizer/assets/36306448/54d14442-39d7-49f1-81a7-afed13de8b2c">


## Analysis and Results 

#### Offline Recognition Test Recognition Accuracy
![image](https://github.com/alebar000/HCIRADollarRecognizer/assets/36306448/e8cd4b6e-9df0-435d-824d-d3bbc3c0a9aa)


# References:

#### $1 Algorithm:
Jacob O. Wobbrock, Andrew D. Wilson, and Yang Li. 2007. Gestures without libraries, toolkits or training: a $1 recognizer for user interface prototypes. In Proceedings of the 20th annual ACM symposium on User interface software and technology (UIST '07). ACM, New York, NY, USA, 159-168. https://doi.org/10.1145/1294211.1294238

#### Gesture Templates:
https://depts.washington.edu/acelab/proj/dollar/index.html

#### XML Files for Offline Recognition
https://depts.washington.edu/acelab/proj/dollar/index.html

# License
MIT
