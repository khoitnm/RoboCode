# Introduction
## Overview
This project is my robot for the game RoboCode
- https://robocode.sourceforge.io/
- https://github.com/robo-code/robocode

## My Robots
My two main robots are:
1. <strong>Briareos</strong> (implemented 2015):
  - Radar: scan 360
  - Gun: Use my own implementation. After that, I realized that the idea is similar to Waves algorithm.
  - Movement: 
    - Just random moving in the same direction. 
    - Wall-smooth: implemented by myself. The code is huge, but it works nicely. May need huge refactor.
    - Hitting wall or Enemies: reverse direction

2. <strong>TheUnfoldingRobot</strong> (implemented 2019):
  - My advanced robot compare to Briareos.
    I named it after reading the book "Reinventing Organizations": the robot is on the journey of unfolding itself.
  - In general, it will behave differently when 1-on-1 and melee (battle with many bots). It combine different strategies for Radar, Gun and Movement differently depend on the situations.
  - Radar: 
    - Melee: Optimal Scan (just scan area with enemies, don't scan redundant areas)
    - One-on-One: Lock radar to the enemy.
  - Gun: Use Waves & GuessFactoring Target + Circular & Linear Pattern Prediction.
  - Movement:
    - Melee:
      - Anti-Gravity movement: 
        - When near the walls: run directly to the destination point.
        - In safe area (far away from the walls): run smoothly without changing direction (but it does turn angle heading) to reach the destination. The path may longer but it can avoid being stuck at some small area.
    - One-on-One: Oscillator: run perpendicular with enemy.   
    - Wall-smooth: In any case, if near the wall, use Wall-smooth: reuse the code of Briareos.
    - Hitting wall or Enemies: In any case, if hit walls or enemies, reverse direction and turn 90 degree (to avoid back-and-forth stuck).
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

**The correct setup of your Robot in your source code project:**
- robot package name: 
    - You can use any name here
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
  - Enemy Dodging Movement: http://robowiki.net/wiki/Enemy_Dodging_Movement (it seems not to be as effective as anti-gravity, but much simplier to implement)
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

# Some notes when calculate angles in RoboCode.
Usually, the bearing and heading values you receive from the game is not the same of the actual values in Geometry maths.
Hence when calculating sin/cos..., the result will be wrong.
That's why before applying sin/cos..., you should convert them to geometry angles.

Use `toGeometryRadian(double inGameRadian)` and `toGeometryDegree(double inGameDegree)` before calculate geometry formulas such as `Math.sin(radian)`, `Math.cos(radian)`

# UnitTest
Some example code:
 - https://hiraidekeone.wordpress.com/2013/02/26/robocode-quality-assurance-and-junit-testing/
 - https://bretkikehara.wordpress.com/2013/02/26/robocode-unit-testing-goodness/
 - https://github.com/robo-code/robocode/blob/master/plugins/testing/robocode.testing.samples/src/main/java/sample/TestWallBehavior.java

Set up to run this test:
 - In IntelliJ menu > Run > Edit Configurations > In VM Options, add: " -Drobocode.home=D:\SourceCode\RoboCode\robocode"
 - Or use the command line: mvn clean install -Drobocode.home="D:\SourceCode\RoboCode\robocode"
