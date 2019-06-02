package org.tnmk.robocode.robot;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

/**
 * The very simple robot which extends features from {@link AdvancedRobot}
 * http://robowiki.net/wiki/Thread:Talk:Main_Page/Robot_vs_Advanced_robot
 * http://mark.random-article.com/weber/java/robocode/lesson3.html
 */
public class BeginnerAdvancedRobot extends AdvancedRobot {
    public void run() {
        while (true) {
            ahead(100);
            turnGunRight(360);
            //For basic Robot, you needs 2 ticks to execute the above 2 actions
            //For advanced Robot, you only need 1 tick to execute them (by calling execute())
            execute();

            back(100);
            turnGunRight(360);
            execute();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        fire(1);
    }

}
