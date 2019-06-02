package org.tnmk.robocode.robot;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

/**
 * The very simple robot which extends features from {@link AdvancedRobot}
 * http://robowiki.net/wiki/Thread:Talk:Main_Page/Robot_vs_Advanced_robot
 * http://mark.random-article.com/weber/java/robocode/lesson3.html
 */
public class BeginnerAdvancedRobot extends AdvancedRobot {
    private static int loopIndex = 0;
    public void run() {
        while (true) {
            ahead(80);
            log("Advance Ahead 1");
            turnGunRight(360);
            log("Advance Turn Gun Right 1");

            //For basic Robot, you needs 2 ticks to execute the above 2 actions
            //For advanced Robot, you only need 1 tick to execute them (by calling execute())
            //  when you execute:
            //  - the above action ahead() may need about 10 ticks (max velocity is 8) to finish,
            //  - and turnGunRight() need 360/20=18 ticks to finish.
            //  => So, in total, you need 18 ticks before the execute() is processed?
            execute();
            log("Advance execute 1");

            back(80);
            log("Advance Back");
            turnGunRight(360);
            log("Advance Turn Gun Right 2");
            execute();
            log("Advance execute 2");
            loopIndex++;
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        fire(1);
        log("Advance fire");
    }

    private void log(String message) {
        String finalMessage = String.format("[%s] \t loop[%s] \t %s \t %s", this.getTime(), loopIndex, System.nanoTime(), message);
        out.println(finalMessage);
    }

}
