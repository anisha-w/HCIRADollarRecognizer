#### Alexander Barquero & Anisha Wadhwani
#### Course : CIS6930: Human-Centered Input Recognition Algorithms (HCIRA)

# Implemention of $1 Gesture Recognition Algorithm 
The primary objective of this project revolved around the development of a GUI tailored for the recognition of 16 pre-determined unistroke gestures using the $1 recognition algorithm. Additionally, we expanded the project to enable users to incorporate and evaluate the system with distinct sets of gestures. Here our new dataset consist of Uppercase and Lowercase Vowels (A, E, I, O, U, a, e, i, o, u)

#### $1 Algorithm Reference : 
[Jacob O. Wobbrock, Andrew D. Wilson, and Yang Li. 2007. Gestures without libraries, toolkits or training: a $1 recognizer for user interface prototypes. In Proceedings of the 20th annual ACM symposium on User interface software and technology (UIST '07). ACM, New York, NY, USA, 159-168.](https://dl.acm.org/doi/abs/10.1145/1294211.1294238?casa_token=5FbPgYWhczkAAAAA:4i8ClOkuH7NJi2HkSZyMKIVGQPU7wq1l-hK2YiOxAsKQh1fMx4WqrydSZqOj2aUGWhr7ApxtG-uZ)

## Problem Statement
The goal of Project #1 is to implement the $1 Gesture recognizer algorithm for the 16 gestures given below. Additionally the project goals comprises of the following:
- Developing an Offline recognition module for analysis.
- Developing a module to collect data from users for offline analysis. 
- Measure performance of the algorithm based on the recognition accuracy
 
   	<img width="250" alt="image" src="https://github.com/anisha-w/HCIRADollarRecognizer/assets/36306448/80a046c0-6071-4be8-a43c-ef6f7d5db1a1">


Project #2 (Extension of Project1) Goals:
- Modify the system to add a different set of gestures (Upper case and Lower Case vowels) 
- Create a setup module to simplify the process of changing the dataset
- Measure performance of system for the new dataset

#### Project milestones
The project #1 has been divided into 5 milestones to build the complete system. Project #2 is completed as a single milestone
- Part 1 : Drawing on a Canvas 
- Part 2 : Online/Live Recognition 
- Part 3 : Offline/Test Recognition with $1 
- Part 4 : Collecting Data from People 
- Part 5 : Exploring Data from People 

## Features of the system

https://github.com/anisha-w/HCIRADollarRecognizer/assets/36306448/3759e308-75ce-4d43-b1f8-fb06c982da08

#### Snapshots of GUI

<img width="782" alt="image" src="https://github.com/anisha-w/HCIRADollarRecognizer/assets/36306448/f26a5f42-a60e-4957-ae6c-6abb6cda50ef">

#### Features of the Online/Live Recognizer GUI system: 
- The system identifies specified unistroke gestures.
- Users can draw gestures on the canvas using a mouse, stylus, or finger.
- System displays the Gesture Name and its similarity score as soon as user lifts their pen or releases their mouse.
- The system includes a Clear button that allows users to clean the screen.

#### Features of the User Data Collection GUI system:
- Ask for Participant ID and number of samples to be collected for each gesture
- Display Gesture name, reference image and a counter to indicate number of gestures drawn.
- Gesture types are collected in a random order to accommodate for user fatigue.
- Clear button to allow user to redraw, 
- Submit button stores the data in XML. 
- In case user hits submit without drawing, a popup message is displayed requesting the user to draw.

#### Features of the Offline Reconition Analysis 
- Extract Gesture data from XML file. XML file consists of the (x,y) points and details like Subject ID, Sample Number, Gesture Name etc. 
- Run User-Dependent analysis : Test the choosen candidate gesture against e template samples of each gesture of the same user
- Run User-Independent Analysis : Test randomly choosen candidate gesture against e template samples of each gesture of the r randonly choosen users


## System
Language : Java   
IDE : Microsoft Visual Studio Code   
Version Control : Github repository   

## Installation and Execution

### Installation  
* Java : The computer must have Java compiling and runtime capabilities available.
* Code file: Download all files into "HCIRADollarRecognizer" folder
* Dataset : Download the dataset (XML Folder : https://depts.washington.edu/acelab/proj/dollar/index.html) and unzip the folder.
Move it to "HCIRADollarRecognizer/OfflineResources"
* online Templates : For the 16 default gestures, no setup needed. 

##### Config file setup (config.xml)


```xml
<templatePath>/OfflineResources/xml_logs</templatePath> <!-- folder path for Dataset for offline recognition --> 

<onlineRecognizerTemplatePath>/OnlineResources/online_template</onlineRecognizerTemplatePath> <!--folder path for the online recognition templates-->

<storeTemplatePath>/templateData</storeTemplatePath> <!--folder name where data collected (xml files) is stored -->

<gestureSet>gestureSetP1</gestureSet> <!-- Name of the ACTIVE gesture set; Is user-defined; no spaces -->

<userSize>10</userSize> <!-- Number of users taken for offline recognition -->

<referenceImage>unistrokes.png</referenceImage> <!-- path of the reference image for data collection -->

... <!-- additional tags for Gesture set -->
```

##### Tag structure for Gesture set 
```xml
<{gesture_set_name}Size>{INTEGER}</{gesture_set_name}Size> <!-- number of gestures in dataset gesture_set_name -->
<{gesture_set_name}> <!-- Should be same as the name given under the tag <gestureSet> -->
	<gesture> <!-- Do not change -->
         	<name>{Gesture_Name}</name> <!-- gesture name -->
         	<value>{INTEGER}</value> <!-- numerical id for gesture -->
      	</gesture>
      	..... <!-- N gesture tags -->
</{gesture_set_name}>
```

Example:
```xml
<gestureSetP1Size>16</gestureSetP1Size>
<gestureSetP1>
      <gesture>
         <name>arrow</name>
         <value>0</value>
      </gesture>
      ....
</gestureSetP1>
```

###### Note: Below is the folder structure for the default folder paths

```sh
|— HCIRADollarRecognizer
	|- DollarRecognizer.java
	|- DollarRecognizerOffline.java
	|- config.xml
	|- OfflineResources
		|- xml_logs
	|- OnlineResources
		|- online_template
 ```

### Compile files
```sh
cd <folder_name>
javac  DollarRecognizer.java
javac  DollarRecognizerOffline.java
```
Note : Both files need to be compiled always since there are functions being used across the files. 

#### Run Online recognizer 
```sh
java DollarRecognizer
```

#### Run Offline recognizer 
```sh
java DollarRecognizerOffline
```  

## Implementation
#### System Flow chart
<img width="610" alt="image" src="https://github.com/anisha-w/HCIRADollarRecognizer/assets/36306448/286715be-d274-4cf2-b584-417e0b880100">


## Analysis and Results 

#### Offline Recognition Test - Recognition Accuracy using User Dependent Analysis for the 16 pre-defined gestures

Part 3 : Refers to the performance for the data collected by authors. 
Part 5 : Refers to the performance for the data collected by us

<img width="380" alt="accuracygraph" src="https://github.com/anisha-w/HCIRADollarRecognizer/assets/36306448/60d22f67-0543-4168-af23-0ab0bc673e98">
<img width="300" alt="Heatmap" src="https://github.com/anisha-w/HCIRADollarRecognizer/assets/36306448/44da3000-831e-4c68-aca2-6999fd45ca8b">



Data collected by authors have a uniform drawing speed whereas the users recruited by us did not maintain a uniform speed which is why the samples had lot of variability 

####  Offline Recognition Test - Recognition Accuracy using User Independent Analysis for the 10 gestures used for Project2

<img width="383" alt="image" src="https://github.com/anisha-w/HCIRADollarRecognizer/assets/36306448/4cf90116-b1ee-4658-9c4b-6e10314b1dc4">
<img width="301" alt="image" src="https://github.com/anisha-w/HCIRADollarRecognizer/assets/36306448/c2a79690-e410-4302-bc1c-63f6da34538c">

The recognition accuracy is much lower since the gestures selected are very similar like Upper O and Lower O 


# References:

#### $1 Algorithm:
Jacob O. Wobbrock, Andrew D. Wilson, and Yang Li. 2007. Gestures without libraries, toolkits or training: a $1 recognizer for user interface prototypes. In Proceedings of the 20th annual ACM symposium on User interface software and technology (UIST '07). ACM, New York, NY, USA, 159-168. https://doi.org/10.1145/1294211.1294238

#### Gesture Templates:
https://depts.washington.edu/acelab/proj/dollar/index.html

#### XML Files for Offline Recognition
https://depts.washington.edu/acelab/proj/dollar/index.html

# License
MIT
