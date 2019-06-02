package org.tnmk.robocode.robot;

import robocode.AdvancedRobot;

/**
 * The very simple robot which extends features from {@link AdvancedRobot}
 * http://robowiki.net/wiki/Thread:Talk:Main_Page/Robot_vs_Advanced_robot
 * http://mark.random-article.com/weber/java/robocode/lesson3.html
 */
public class BeginnerAdvancedRobot extends AdvancedRobot {
    public void run() {
        setAdjustRadarForRobotTurn(true);


        while (true) {
            ahead(100);
            turnGunRight(360);
            back(100);
            turnGunRight(360);
        }
    }
}
