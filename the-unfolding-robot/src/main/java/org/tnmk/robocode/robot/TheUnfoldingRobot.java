package org.tnmk.robocode.robot;

import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robotdecorator.HiTechDecorator;
import robocode.AdvancedRobot;
import robocode.CustomEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

/**
 * The very simple robot which extends features from {@link AdvancedRobot}
 * http://robowiki.net/wiki/Thread:Talk:Main_Page/Robot_vs_Advanced_robot
 * http://mark.random-article.com/weber/java/robocode/lesson3.html
 */
public class TheUnfoldingRobot extends AdvancedRobot {
    private static int loopIndex = 0;
    private AllEnemiesObservationContext allEnemiesObservationContext = new AllEnemiesObservationContext(this);
    private TheUnfoldingMovement theUnfoldingMovement = new TheUnfoldingMovement(this, allEnemiesObservationContext);
    private TheUnfoldingRadar theUnfoldingRadar = new TheUnfoldingRadar(this, allEnemiesObservationContext);
    private TheUnfoldingGun theUnfoldingGun = new TheUnfoldingGun(this, allEnemiesObservationContext);

    public void run() {
        HiTechDecorator.decorate(this);

        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);

        theUnfoldingGun.runInit();
        theUnfoldingRadar.runInit();
        theUnfoldingMovement.runInit();
        execute();

        while (true) {
            theUnfoldingGun.runLoop();
            execute();
            loopIndex++;
        }
    }

    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        theUnfoldingRadar.onScannedRobot(scannedRobotEvent);
        theUnfoldingMovement.onScannedRobot(scannedRobotEvent);
        theUnfoldingGun.onScannedRobot(scannedRobotEvent);
        //Note don't execute() in robotEvents, otherwise, the actions inside loopRun() will not be triggered.
        //All of event should trigger robot.setXxx() methods only, they will be triggered in loopRun()
//        execute();
    }

    public void onRobotDeath(RobotDeathEvent robotDeathEvent) {
        theUnfoldingRadar.onRobotDeath(robotDeathEvent);
        //Note don't execute() in robotEvents, otherwise, the actions inside loopRun() will not be triggered.
        //All of event should trigger robot.setXxx() methods only, they will be triggered in loopRun()
//        execute();
    }

    @Override
    public void onCustomEvent(CustomEvent customEvent) {
        theUnfoldingRadar.onCustomEvent(customEvent);
        theUnfoldingGun.onCustomEvent(customEvent);
        //Note don't execute() in robotEvents, otherwise, the actions inside loopRun() will not be triggered.
        //All of event should trigger robot.setXxx() methods only, they will be triggered in loopRun()
//        execute();
    }
}
