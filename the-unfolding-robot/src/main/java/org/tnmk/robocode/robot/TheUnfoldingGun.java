package org.tnmk.robocode.robot;

import org.tnmk.robocode.common.gun.GunStateContext;
import org.tnmk.robocode.common.gun.briareos.BriareosGun;
import org.tnmk.robocode.common.gun.gft.oldalgorithm.GFTAimGun;
import org.tnmk.robocode.common.gun.mobius.MobiusGun;
import org.tnmk.robocode.common.gun.pattern.PatternPredictionGun;
import org.tnmk.robocode.common.model.enemy.EnemyStatisticContext;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robot.InitiableRun;
import org.tnmk.robocode.common.robot.LoopableRun;
import org.tnmk.robocode.common.robot.OnCustomEventControl;
import org.tnmk.robocode.common.robot.OnScannedRobotControl;
import robocode.AdvancedRobot;
import robocode.CustomEvent;
import robocode.ScannedRobotEvent;

public class TheUnfoldingGun implements InitiableRun, LoopableRun, OnScannedRobotControl, OnCustomEventControl {
    private final AdvancedRobot robot;
    private final AllEnemiesObservationContext allEnemiesObservationContext;

    private BriareosGun briareosGun;
    private GFTAimGun gftAimGun;
    private PatternPredictionGun patternPredictionGun;
    private MobiusGun mobiusGun;
    private GunStateContext gunStateContext;


    public TheUnfoldingGun(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;
        this.gunStateContext = new GunStateContext();

        this.briareosGun = new BriareosGun(robot);
        this.mobiusGun = new MobiusGun(robot);
        this.gftAimGun = new GFTAimGun(robot, gunStateContext);
        this.patternPredictionGun = new PatternPredictionGun(robot, allEnemiesObservationContext, gunStateContext);
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        EnemyStatisticContext enemyStatisticContext = allEnemiesObservationContext.getEnemyPatternPrediction(scannedRobotEvent.getName());
//        LogHelper.logSimple(robot, "Enemy: " + scannedRobotEvent.getName()
//                + "\n\t\t Pattern: " + enemyStatisticContext.getPatternIdentification()
//                + "\n\t\t predictionHistory: \t" + enemyStatisticContext.getEnemyPredictionHistory().getAllHistoryItems()
//        );
        if (enemyStatisticContext != null && enemyStatisticContext.hasCertainPattern()) {
            patternPredictionGun.onScannedRobot(scannedRobotEvent);
        } else {
//            mobiusGun.onScannedRobot(scannedRobotEvent);
//            aimGftGunIfCloseEnemyEnough(scannedRobotEvent);
            gftAimGun.onScannedRobot(scannedRobotEvent);
//            briareosGun.onScannedRobot(scannedRobotEvent);
        }
    }

    @Override
    public void onCustomEvent(CustomEvent customEvent) {
        //Nothing at this moment.
    }

    @Override
    public void runInit() {
//        briareosGun.runInit();
        //Nothing at this moment.
    }

    @Override
    public void runLoop() {
//        briareosGun.runLoop();
        patternPredictionGun.runLoop();
    }
}
