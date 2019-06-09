package org.tnmk.robocode.robot;

import org.tnmk.robocode.common.movement.edm.EnemyDodgeMovement;
import org.tnmk.robocode.common.movement.oscillator.OscillatorMovement;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class TheUnfoldingMovement {
    private int enemiesCount = 0;
    private static final double IDEAL_ENEMY_DISTANCE = 600;
    private final AdvancedRobot robot;

    private final OscillatorMovement oscillatorMovement;
    private final EnemyDodgeMovement enemyDodgeMovement;

    public TheUnfoldingMovement(AdvancedRobot robot) {
        this.robot = robot;
        oscillatorMovement = new OscillatorMovement(robot);
        enemyDodgeMovement = new EnemyDodgeMovement(robot);
    }

    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        if (enemiesCount <= 1) {
            moveOscillatorWithIdealDistance(scannedRobotEvent);
        } else {
            enemyDodgeMovement.onScannedRobot(scannedRobotEvent);
        }
    }

    private void moveOscillatorWithIdealDistance(ScannedRobotEvent scannedRobotEvent) {
        double battleFieldSize = Math.min(robot.getBattleFieldWidth(), robot.getBattleFieldHeight());
        double haftBattleFieldSize = battleFieldSize * 0.5;
        double thirdFourBattleFieldSize = battleFieldSize * 0.75;
        int enemyDistance = (int) Math.min(thirdFourBattleFieldSize, Math.max(haftBattleFieldSize, IDEAL_ENEMY_DISTANCE));
        oscillatorMovement.onScannedRobot(scannedRobotEvent, enemyDistance);
    }
}
