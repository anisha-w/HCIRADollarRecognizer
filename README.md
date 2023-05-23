### Alexander Barquero & Anisha Wadhwani
# Project 2 : Extension of Project 1 - New set of gestures 
### CIS6930: HCIRA

## Introduction
We present our implementation of extension of the Project 1 : $1 Recognizer. 
Our extension includes collecting a new dataset consisting of Uppercase and Lowercase Vowels (A, E, I, O, U, a, e, i, o, u)

## Objectives : 
We run the offline version for user-dependent and user-independent analysis. Additionally we feed our dataset into the GHOST toolkit and compute heatmaps and draw insights about user articulation variability 

## Installation
* Java : You can run the system from any Java enabled IDE, by using the standard running functionalities. As usual, make sure you have Java compiling and runtime capabilities in your computer.If you want to run from a console, you also need to make sure you have Java compiling and runtime capabilities in your computer. Once that is done, please navigate to the folder root where the .java files are located, and execute. 

* Dataset : Download the dataset and unzip the folder in the same folder root where the .java files are located. 

* config.xml : Update the folder path if you are creating new folders for dataset in the tag <templatePath> <>

```sh
<templatePath>/Project2_Resources/xml_logs</templatePath>
<onlineRecognizerTemplatePath>/Project2_Resources/online_template</onlineRecognizerTemplatePath>
```

#### Note: The folder structure is as follows

```sh
|— HCIRADollarRecognizer
	|- DollarRecognizer.java
	|- DollarRecognizerOffline.java
	|- config.java
	|- Project2_Resources
		|- xml_logs
		|- online_template
	
	
```
# Execution Steps 

### Compile files 
```sh
cd <folder_path_for_code>
javac DollarRecognizer.java
javac DollarRecognizerOffline.java
```
Note : Both files need to be compiled always since there are functions being used across the files. 

### Run Online recognizer 
```sh
java DollarRecognizer
```

### Run Offline recognizer 
```sh
java DollarRecognizerOffline
```


# License
MIT

