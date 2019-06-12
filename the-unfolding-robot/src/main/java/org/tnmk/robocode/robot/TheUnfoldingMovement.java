package org.tnmk.robocode.robot;

import org.tnmk.robocode.common.movement.MovementContext;
import org.tnmk.robocode.common.movement.antigravity.AntiGravityMovement;
import org.tnmk.robocode.common.movement.oscillator.OscillatorMovement;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robot.InitiableRun;
import org.tnmk.robocode.common.robot.Scannable;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class TheUnfoldingMovement implements InitiableRun, Scannable {
    public static final double IDEAL_ENEMY_OSCILLATOR_DISTANCE = 150;
    private final AdvancedRobot robot;
    private final AllEnemiesObservationContext allEnemiesObservationContext;
    private final MovementContext movementContext;


    private final OscillatorMovement oscillatorMovement;
    private final AntiGravityMovement antiGravityMovement;

    public TheUnfoldingMovement(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;

        movementContext = new MovementContext();
        oscillatorMovement = new OscillatorMovement(robot, movementContext);
        antiGravityMovement = new AntiGravityMovement(robot, allEnemiesObservationContext, movementContext);
    }

    @Override
    public void runInit(){
        antiGravityMovement.runInit();
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        int totalExistingEnemies = robot.getOthers();
        if (totalExistingEnemies <= 1) {
            moveOscillatorWithIdealDistance(scannedRobotEvent);
        } else {
            antiGravityMovement.onScannedRobot(scannedRobotEvent);
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
