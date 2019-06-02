# Introduction
This project is my robot for the game RoboCode (https://robocode.sourceforge.io/)

# Terms
## Overview
- Source: our robot
- Target: other robot which is scanned by radar.
- Aimed position: the position when finishing aiming.
- Fired position: the position when bullet hit target.
- Velocity: negative & positive speed.
- Speed: = Abs(velocity) (always positive)
- Direction (Heading): negative & positive angle.
- MoveAngle: = Abs(heading) (always positive)

## Actions 
StandStill -> Start Aim -> Aimed (Finsh aiming) -> Start Fire -> Fired (Finished firing == bullet hit target)

## Robot Guideline
### Create your robot
http://robowiki.net/wiki/Robocode/Eclipse/Create_a_Project
http://robowiki.net/wiki/Robocode/Eclipse/Create_a_Robot
http://robowiki.net/wiki/Robocode/Add_a_Robot_Project
http://robowiki.net/wiki/Robocode/Running_from_Eclipse

**The correct setup of your Robot:**
- robot package name: 
    - You can use any name here anything
    - For example: org.tnmk.robocode.robot
- robot class name: 
    - This is the robot name
    - You can put any name here
    - For example: BeginnerBasicRobot (in package org.tnmk.robocode.robot)
- properties:
    - This file describes what should be the main class of Robot.  
    - At the same package and have the same name of robot's class.
    - For example: org.tnmk.robocode.robot.BeginnerBasicRobot.properties
- build jar:
    - The final jar file must have the same name of robot's name 
    - For example: BeginnerBasicRobot.jar

### Start your robot
http://robowiki.net/wiki/Robocode/Getting_Started

# Troubleshoot
In order to RoboCode can recognize your Robot jar, the jar's filename must be the same as the robot's classname (case sensitive?)
And your Robot should need the properties file to include some information.

In application > Preferences > Development Options: 
- You will point to the folder which will contains package of robot classes, for example: /SourceCode/RoboCode/simple-robot/target/classes
- Anyway, this folder should not contains different folder point to the same robot's class name. Otherwise, the application will confuse and pickup the first one.  
For example, if you point to /SourceCode/RoboCode/simple-robot/target/, that folder will have ./classes/ and *.jar files which are both store robot's classes. It will cause problem.
