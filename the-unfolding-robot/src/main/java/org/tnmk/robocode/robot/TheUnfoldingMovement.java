package org.tnmk.robocode.robot;

import org.tnmk.robocode.common.movement.edm.EnemyDodgeMovement;
import org.tnmk.robocode.common.movement.oscillator.OscillatorMovement;
import org.tnmk.robocode.common.radar.scanall.AllEnemiesObservationContext;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class TheUnfoldingMovement {
    public static final double IDEAL_ENEMY_OSCILLATOR_DISTANCE = 150;
    private final AdvancedRobot robot;
    private final AllEnemiesObservationContext allEnemiesObservationContext;


    private final OscillatorMovement oscillatorMovement;
    private final EnemyDodgeMovement enemyDodgeMovement;

    public TheUnfoldingMovement(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;

        oscillatorMovement = new OscillatorMovement(robot);
        enemyDodgeMovement = new EnemyDodgeMovement(robot, allEnemiesObservationContext);
    }

    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        if (allEnemiesObservationContext.countEnemies() <= 1) {
            moveOscillatorWithIdealDistance(scannedRobotEvent);
        } else {
            enemyDodgeMovement.onScannedRobot(scannedRobotEvent);
        }
    }

    private void moveOscillatorWithIdealDistance(ScannedRobotEvent scannedRobotEvent) {
        int enemyDistance = (int) calculateSuitableEnemyDistance(IDEAL_ENEMY_OSCILLATOR_DISTANCE);
        oscillatorMovement.onScannedRobot(scannedRobotEvent, enemyDistance);
    }

    private double calculateSuitableEnemyDistance(double idealDistance) {
        double battleFieldSize = Math.min(robot.getBattleFieldWidth(), robot.getBattleFieldHeight());
        double haftBattleFieldSize = battleFieldSize * 0.1;
        double thirdFourBattleFieldSize = battleFieldSize * 0.75;
        double idealEnemyDistance = calculateSuitableEnemyDistanceInAppropriateLimit(idealDistance, haftBattleFieldSize, thirdFourBattleFieldSize);
        return idealEnemyDistance;
    }

    private double calculateSuitableEnemyDistanceInAppropriateLimit(double idealDistance, double minDistance, double maxDistance) {
        double ideal = Math.min(maxDistance, Math.max(minDistance, idealDistance));
        return ideal;
    }
}
