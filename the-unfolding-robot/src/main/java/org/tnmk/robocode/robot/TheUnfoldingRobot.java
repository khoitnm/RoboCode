package org.tnmk.robocode.robot;

import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robotdecorator.HiTechDecorator;
import robocode.*;

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


    @Override
    public void run() {
        HiTechDecorator.decorate(this);

        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);

        theUnfoldingRadar.runInit();
        theUnfoldingGun.runInit();
        theUnfoldingMovement.runInit();
        execute();

        while (true) {
            theUnfoldingGun.runLoop();
            theUnfoldingMovement.runLoop();
            execute();
            loopIndex++;
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        /** Radar must be executed before other things so that it can update the latest information. */
        theUnfoldingRadar.onScannedRobot(scannedRobotEvent);
        theUnfoldingMovement.onScannedRobot(scannedRobotEvent);
        theUnfoldingGun.onScannedRobot(scannedRobotEvent);
        //Note don't execute() in robotEvents, otherwise, the actions inside loopRun() will not be triggered.
        //All of event should trigger robot.setXxx() methods only, they will be triggered in loopRun()
//        execute();
    }

    @Override
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
        theUnfoldingMovement.onCustomEvent(customEvent);
        //Note don't execute() in robotEvents, otherwise, the actions inside loopRun() will not be triggered.
        //All of event should trigger robot.setXxx() methods only, they will be triggered in loopRun()
//        execute();
    }

    @Override
    public void onHitRobot(HitRobotEvent hitRobotEvent) {
        theUnfoldingMovement.onHitRobot(hitRobotEvent);
    }

    @Override
    public void onStatus(StatusEvent statusEvent) {
        theUnfoldingMovement.onStatus(statusEvent);
    }

    @Override
    public void onHitWall(HitWallEvent hitWallEvent) {
        theUnfoldingMovement.onHitWall(hitWallEvent);
    }

    @Override
    public void onHitByBullet(HitByBulletEvent hitByBulletEvent) {
        theUnfoldingGun.onHitByBullet(hitByBulletEvent);
    }

    @Override
    public void onWin(WinEvent winEvent) {
        theUnfoldingGun.onWin(winEvent);
    }

    @Override
    public void onBulletHit(BulletHitEvent event) {
        theUnfoldingGun.onBulletHit(event);
        theUnfoldingMovement.onBulletHit(event);
    }
}
