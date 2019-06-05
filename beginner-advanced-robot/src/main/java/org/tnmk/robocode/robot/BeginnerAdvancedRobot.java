package org.tnmk.robocode.robot;

import org.tnmk.common.number.DoubleUtils;
import org.tnmk.robocode.common.log.LogHelper;
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
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);

        while (true) {
            if (DoubleUtils.isConsideredZero(this.getVelocity())) {
                log("Start Advance Ahead 1");
                setAhead(80);
                log("Finish Advance Ahead 1");//16 ticks
            }

            if (DoubleUtils.isConsideredZero(this.getRadarTurnRemaining())) {
                log("Start Advance Turn Radar Right 1");
                setTurnRadarRight(360);
                log("Finish Advance Turn Radar Right 1");
            }

            if (DoubleUtils.isConsideredZero(this.getGunTurnRemaining())) {
                log("Start Advance Turn Gun Right 1");
                setTurnGunRight(360);
                log("Finish Advance Turn Gun Right 1");//20 ticks
            }

            //Differently from Basic Robot, for Advanced Robot, when you execute, 3 above actions will be executed at the same time.
            //  when you execute:
            //  - the above action ahead() may need about 10 ticks (max velocity is 8) to finish,
            //  - and turnGunRight() need 360/20=18 ticks to finish.
            //  => So, in total, you need 18 ticks before the execute() is processed?
            log("Start Advance execute tree actions at the same time 1");
            execute();
            log("Finish Advance execute tree actions at the same time 1");
            log("-----------------------------------");

            if (DoubleUtils.isConsideredZero(this.getVelocity())) {
                log("Start Advance Back");
                back(80);
                log("Finish Advance Back");
            }

            if (DoubleUtils.isConsideredZero(this.getRadarTurnRemaining())) {
                log("Start Advance Turn Radar Right 2");
                turnRadarRight(360);
                log("Finish Advance Turn Radar Right 2");
            }

            if (DoubleUtils.isConsideredZero(this.getGunTurnRemaining())) {
                log("Start Advance Turn Gun Right 2");
                turnGunRight(360);
                log("Finish Advance Turn Gun Right 2");
            }

//            log("Start Advance execute tree actions at the same time 2");
//            execute();
//            log("Finish Advance execute tree actions at the same time 2");

            log("-----------------------------------");
            log("==============================================");
            loopIndex++;
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        fire(1);
        log("Advance fire");
    }

    private void log(String message) {
        LogHelper.logRobot(this, loopIndex, message);
    }

}
