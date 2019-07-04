package org.tnmk.robocode.robot;

import org.tnmk.robocode.common.error.ErrorLogger;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robot.state.AdvanceRobotFightState;
import org.tnmk.robocode.common.robot.state.AdvanceRobotState;
import org.tnmk.robocode.common.robot.state.AdvanceRobotStateMapper;
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
        ErrorLogger.init(this);

        try {
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
        } catch (RuntimeException e) {
            logAndRethrowException(e);
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        try {
            /** Radar must be executed before other things so that it can update the latest information. */
            theUnfoldingRadar.onScannedRobot(scannedRobotEvent);
            theUnfoldingMovement.onScannedRobot(scannedRobotEvent);
            theUnfoldingGun.onScannedRobot(scannedRobotEvent);
            //Note don't execute() in robotEvents, otherwise, the actions inside loopRun() will not be triggered.
            //All of event should trigger robot.setXxx() methods only, they will be triggered in loopRun()
        } catch (RuntimeException e) {
            logAndRethrowException(e);
        }
    }

    @Override
    public void onRobotDeath(RobotDeathEvent robotDeathEvent) {
        try {
            theUnfoldingRadar.onRobotDeath(robotDeathEvent);
            //Note don't execute() in robotEvents, otherwise, the actions inside loopRun() will not be triggered.
            //All of event should trigger robot.setXxx() methods only, they will be triggered in loopRun()
        } catch (RuntimeException e) {
            logAndRethrowException(e);
        }
    }

    @Override
    public void onCustomEvent(CustomEvent customEvent) {
        try {
            theUnfoldingRadar.onCustomEvent(customEvent);
            theUnfoldingGun.onCustomEvent(customEvent);
            theUnfoldingMovement.onCustomEvent(customEvent);
            //Note don't execute() in robotEvents, otherwise, the actions inside loopRun() will not be triggered.
            //All of event should trigger robot.setXxx() methods only, they will be triggered in loopRun()
        } catch (RuntimeException e) {
            logAndRethrowException(e);
        }
    }

    @Override
    public void onHitRobot(HitRobotEvent hitRobotEvent) {
        try {
            theUnfoldingMovement.onHitRobot(hitRobotEvent);
        } catch (RuntimeException e) {
            logAndRethrowException(e);
        }
    }

    @Override
    public void onStatus(StatusEvent statusEvent) {
        try {
            theUnfoldingMovement.onStatus(statusEvent);
        } catch (RuntimeException e) {
            logAndRethrowException(e);
        }
    }

    @Override
    public void onHitWall(HitWallEvent hitWallEvent) {
        try {
            theUnfoldingMovement.onHitWall(hitWallEvent);
        } catch (RuntimeException e) {
            logAndRethrowException(e);
        }
    }

    @Override
    public void onHitByBullet(HitByBulletEvent hitByBulletEvent) {
        try {
            theUnfoldingGun.onHitByBullet(hitByBulletEvent);
        } catch (RuntimeException e) {
            logAndRethrowException(e);
        }
    }

    @Override
    public void onWin(WinEvent winEvent) {
        try {
            theUnfoldingGun.onWin(winEvent);
        } catch (RuntimeException e) {
            logAndRethrowException(e);
        }
    }

    @Override
    public void onBulletHit(BulletHitEvent event) {
        try {
            theUnfoldingGun.onBulletHit(event);
            theUnfoldingMovement.onBulletHit(event);
        } catch (RuntimeException e) {
            logAndRethrowException(e);
        }
    }

    protected void logAndRethrowException(RuntimeException e) {
        AdvanceRobotState state = AdvanceRobotStateMapper.toState(this);
        String errorMessage = String.format("[%s] \n\tState: %s\n\tenemiesContext: %s", getTime(), state, allEnemiesObservationContext);
        ErrorLogger.getInstance().logException(e, errorMessage);
        throw e;
    }
}
