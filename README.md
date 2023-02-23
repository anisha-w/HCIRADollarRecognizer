### Alexander Barquero & Anisha Wadhwani
# Project #1: Part 3: Offline/Test Recognition with $1 
### CIS6930: HCIRA

# Introduction
We present our implementation of the $1 Recognizer, based on the work done by Wobbrock, Wilson and Li.
In this Part 3, we implement the Offline version of the $1 Recognizer. It reads XML files and stores them as templates after pre-processing, spilts the templates into training templates and testing candidates, executes the recognize function and outputs results.

# System
The application was developed using the Java language, specifically with the Java Standard Edition 18 SDK. 
Implementation was done in Microsoft Visual Studio Code, which is a simple but powerful solution that supports all the necessary IDE features that our team requires for this particular endeavor.
We use a Github repository for source code version control.


# Instalation and Execution

You can run the system from any Java enabled IDE, by using the standard running functionalities. As usual, make sure you have Java compiling and runtime capabilities in your computer.

If you want to run from a console, you also need to make sure you have Java compiling and runtime capabilities in your computer. Once that is done, please navigate to the folder root where the .java files are located, and execute the following commands in order:  

```sh
javac DollarRecognizerOffline.java
java DollarRecognizerOffline
```

# Application Features

- Third version adequately running on a Java environment.
- Templates from XML read correctly 
- Templates gestures split into a training templates and candidates in a balanced count of user and gesture
- Details of each test case, like candidate and templates and N best list printed into csv log file. 
- Average per user accuracy and total accuracy printed in csv log file. 

# Goals and Coding Features

## Part 3

## a) read in a gesture dataset from files to use for templates and candidates 
### File DollarRecognizerOffline; Method extractDataFromXML (line 149)
All xml files read in from xml_log/<user>/medium folder and (x,y) points read into UnistrokeTemplate object. Added additional fields in UnistrokeTemplate class to keep track of user, gesture, gesture-repetiton etc (File DollarRecognizer line 363). All objects added to a list of UnistrokeTemplate objects. 

## b) connect to your existing $1 pre-processing and recognition methods
### File DollarRecognizer; UnistrokeTemplate constructor  (line 378)
Added new constructor to initialize extra fields like gesture-repetiton, gesture-id etc and to perform processing steps (step 1-3 of the $1 algorithm) (line 387)

## c) loop over the gesture dataset to systematically configure your recognizer and test it;   
### File DollarRecognizerOffline; Method randomOfflineRecognizer (line 219)
Extracts templates from list for each user (line 225) and then picks templates for each gesture of this user (line 234). Randomly selects e templates withour repetition from this set (line 236) and one candidate. After selecting e templates for each gesture for 1 user, this set of templates is sent to recognize function along with candidate. The results ( best matched template and n best list) are stored in a object to print later to csv. This loop is repeated for 100 iterations. 

## d) output the result of the recognition tests to a log file. 
### File DollarRecognizerOffline; Method randomOfflineRecognizer (line 372) and generateCSVString (line 55)
Results printed into csv file based on the format given:

User[all-users],GestureType[all-gestures-types],RandomIteration[1to100],#ofTrainingExamples[E],TotalSizeOfTrainingSet[count],TrainingSetContents[specific-gesture-instances],Candidate[specific-instance],RecoResultGestureType[what-was-recognized],CorrectIncorrect[1or0],RecoResultScore,RecoResultBestMatch[specific-instance],RecoResultNBestSorted[instance-and-score]

Per user average accuracy and total calculated and printed (line 321) 

# License
MIT

