# Introduction
This project is my robot for the game RoboCode
- https://robocode.sourceforge.io/
- https://github.com/robo-code/robocode

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
### Prerequisite steps to run your robot
- http://robowiki.net/wiki/Robocode/Eclipse/Create_a_Project
- http://robowiki.net/wiki/Robocode/Eclipse/Create_a_Robot
- http://robowiki.net/wiki/Robocode/Add_a_Robot_Project
- http://robowiki.net/wiki/Robocode/Running_from_Eclipse
- http://robowiki.net/wiki/Robocode/Developers_Guide_for_building_Robocode

**The correct setup of your Robot:**
- robot package name: 
    - You can use any name here anything
    - For example: `org.tnmk.robocode.robot`
- robot class name: 
    - This is the robot name
    - You can put any name here
    - For example: `BeginnerBasicRobot` (in package `org.tnmk.robocode.robot`)
- properties:
    - This file describes what should be the main class of Robot.  
    - At the same package and have the same name of robot's class.
    - For example: `org.tnmk.robocode.robot.BeginnerBasicRobot.properties`
- build jar:
    - The final jar file must have the same name of robot's name 
    - For example: `BeginnerBasicRobot.jar`

### Start your robot
http://robowiki.net/wiki/Robocode/Getting_Started

### Implement your robot
Best diagram to show terms in RoboCode:
- https://coggle.it/diagram/51ade2c0e354014b1c00a43c/t/robocode-strategies/a19ae89e8368aa6171bd485adc1017fae44904e554ae9272fec52f6bb85c2294
- Basic information with great images and definitions: https://slideplayer.com/slide/3731495/
- https://www.ibm.com/developerworks/java/library/j-robocode/
- https://www.ibm.com/developerworks/java/library/j-robocode2/j-robocode2-pdf.pdf (some very useful information in the core)

List of sample code: http://old.robowiki.net/robowiki?CodeSnippets

Some tutorial, terms and algorithm for your robot: 
- http://robowiki.net/wiki/Tutorials
- https://www.ibm.com/developerworks/library/j-robotips/index.html

Radar:
  - Basic information: http://robowiki.net/wiki/Melee_Radar
  - https://www.ibm.com/developerworks/library/j-radar/index.html

Aim Target:
  - Waves: http://robowiki.net/wiki/Waves    
  - GuessFactors: 
    - http://robowiki.net/wiki/GuessFactors
    - http://robowiki.net/wiki/GuessFactor_Targeting_(traditional)
  - Displacement Vector: http://robowiki.net/wiki/Displacement_Vector  
  - Play It Forward:
    - http://robowiki.net/wiki/Play_It_Forward        
    
http://mark.random-article.com/robocode/index.html

Movement:
  - Anti-Gravity: 
    - Basic implementation: http://robowiki.net/wiki/Anti-Gravity_Tutorial
    - Detail implementation: https://www.ibm.com/developerworks/java/library/j-antigrav/index.html?ca=drs-
  - Enemy Dodging Movement: http://robowiki.net/wiki/Enemy_Dodging_Movement
  - Wall Smoothing: 
    - http://robowiki.net/wiki/Wall_Smoothing
    - http://robowiki.net/wiki/Wall_Smoothing/Implementations

Utilities functions: https://www.programcreek.com/java-api-examples/index.php?api=robocode.util.Utils

# Some interesting Robots
http://robowiki.net/wiki/DrussGT
http://robowiki.net/wiki/Diamond

# Troubleshoot
In order to RoboCode can recognize your Robot jar, the jar's filename must be the same as the robot's classname (case sensitive?)
And your Robot should need the properties file to include some information.

In application > Preferences > Development Options: 
- You will point to the folder which will contains package of robot classes, for example: `/SourceCode/RoboCode/simple-robot/target/classes`
- Anyway, this folder should not contains different folder point to the same robot's class name. Otherwise, the application will confuse and pickup the first one.  
For example, if you point to `/SourceCode/RoboCode/simple-robot/target/`, that folder will have `./classes/` and `*.jar` files which are both store robot's classes. It will cause problem.

---------------------------------------------------

If a robot fails to complete his turn in the time allotted, the turn will be skipped. 
https://stackoverflow.com/questions/33527613/onscannedrobot-method-never-being-called

# Debug

## Debug by Logging
Use `robot.out.println("XXX");`
Then, when starting game, click to the name of the robot, it will shows logs for that specific robot.

## Debug by Painting
In the code, use `robot.getGraphics().drawXxx()` methods.
When viewing log of a specific robot, you can see it's painting log by click to button "Paint"