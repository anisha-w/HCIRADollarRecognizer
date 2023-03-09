### Alexander Barquero & Anisha Wadhwani
# Project #1: Part 5: Exploring Data from People 
### CIS6930: HCIRA

# Introduction
We present our implementation of the $1 Recognizer, based on the work done by Wobbrock, Wilson and Li.
In this Part 5, we run the Offline version (Part 3) of the $1 Recognizer on the dataset we collect in Part 4 of the project. The log file is generated and accuarcy is calculated. As the second objective of this deliverable, we feed our dataset into the GHOST toolkit and compute heatmaps and draw insights about user articulation variability 

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

- Fifth version adequately running on a Java environment.
- Log File generated correctly 
- Computed Heatmaps using GHOST toolkit. 

# Goals and Coding Features

## Part 5

## a) Run an offline recognition test with $1 (using your code from Part 3) on your new dataset (from Part 4) 
Executed. Few changes made to execute the existing code (part 3) inorder to commodate the differences in naming convention of the two datasets (part 3 and part 4) (line 119,112,115,119)

## b) Output the result of the recognition tests to a log file 
### Fixes for logfile format : Traning set order fixed  
#### File DollarRecognizerOffline; Method generateTrainingSetResultsString() (line 31)
Printing training set into the csv file. [ Reason for fix : Initially training set was printed by printing the template names from the same object as for complete N-best list and hence ordering was different each time since it was ordered by the score]

## c) Run your data through the GHOST heatmap toolkit  
Dataset fed into the toolkit; Settings done as required; computed heatmaps

## d) Extract User Articulation Insights
(1) : Heatmaps suggest that gestures with straight lines and a low angle number (1, 2) tend to have less variability. This is observed specially in the middle section of the straight lines. This is the case for the V, the caret, both square brackets, and the check. Whereas gestures like the triangle, which also has straight lines, does not exhibit such noticeable behaviour, and instead presents lots of variations in the angles.

(2) The heatmaps also suggest that the arrow and pigtail gestures experienced a considerable variance in the way they were started and ended, in terms of the angle and point of origin/end.

# License
MIT

