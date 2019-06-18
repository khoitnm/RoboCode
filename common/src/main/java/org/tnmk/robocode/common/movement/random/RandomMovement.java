package org.tnmk.robocode.common.movement.random;

import java.util.List;
import java.util.Optional;
import org.tnmk.common.math.AngleUtils;
import org.tnmk.robocode.common.constant.RobotPhysics;
import org.tnmk.robocode.common.model.enemy.Enemy;
import org.tnmk.robocode.common.model.enemy.EnemyHistory;
import org.tnmk.robocode.common.model.enemy.EnemyStatisticContext;
import org.tnmk.robocode.common.movement.MoveStrategy;
import org.tnmk.robocode.common.movement.MovementContext;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robot.OnScannedRobotControl;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

public class RandomMovement implements OnScannedRobotControl {
    private final AdvancedRobot robot;
    private final AllEnemiesObservationContext allEnemiesObservationContext;
    private final MovementContext movementContext;

    public RandomMovement(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext, MovementContext movementContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;
        this.movementContext = movementContext;
    }

    private long startTime = 0;

    @Override
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        if (movementContext.hasLowerPriority(MoveStrategy.RANDOM)) {
            movementContext.setMoveStrategy(MoveStrategy.RANDOM);
            startTime = robot.getTime();
            double oldEnemyEnergy = getOldEnemyEnergy(scannedRobotEvent.getName());
            boolean isChangeMovement;
            if (suspectEnemyHasJustFiredBullet(oldEnemyEnergy, scannedRobotEvent.getEnergy())) {
                isChangeMovement = true;
            } else {
                isChangeMovement = Math.random() < .2;
            }
            if (isChangeMovement) {//80% will turn direction randomly.
                double absoluteTurnAngleToEnemy;
                if (robot.getEnergy() / scannedRobotEvent.getEnergy() > 3 || robot.getEnergy() - scannedRobotEvent.getEnergy() > 30d) {
                    absoluteTurnAngleToEnemy = randomAngleMoveTowardEnemy(scannedRobotEvent);
                } else if (scannedRobotEvent.getDistance() < Math.min(robot.getBattleFieldWidth(), robot.getBattleFieldHeight()) * 0.75) {
                    absoluteTurnAngleToEnemy = randomAngleMoveFarAwayFromEnemy(scannedRobotEvent);
                }else{
                    absoluteTurnAngleToEnemy = randomAngleMoveNearlyPerpendicularToEnemy(scannedRobotEvent);
                }
                if (Math.random() < 0.9) {
                    movementContext.reverseDirection();
                    absoluteTurnAngleToEnemy = AngleUtils.normalizeDegree(absoluteTurnAngleToEnemy + 180);
                }
                robot.setTurnRight(absoluteTurnAngleToEnemy);
            } else {
                /** Keep the same movement, doesn't change anything. */
            }
            robot.setAhead(movementContext.getDirection() * 125);
        } else if (movementContext.is(MoveStrategy.RANDOM) && (robot.getTime() - startTime) > 15) {
            movementContext.setNone();
        }
    }

    private double randomAngleMoveNearlyPerpendicularToEnemy(ScannedRobotEvent scannedRobotEvent) {
        int turnDirection = 1;
        if (Math.random() < .5) {
            turnDirection = -1;
        }
        //Move closer to the enemy.
        double turnAngleToEnemy = 60 + Math.random() * 60;
        double relativeTurnAngleToEnemy = turnAngleToEnemy * turnDirection;
        double absoluteTurnAngleToEnemy = scannedRobotEvent.getBearing() + relativeTurnAngleToEnemy;
        return absoluteTurnAngleToEnemy;
    }

    private double randomAngleMoveFarAwayFromEnemy(ScannedRobotEvent scannedRobotEvent) {
        return randomAngleMoveTowardEnemy(scannedRobotEvent) + 180;
    }

    private double randomAngleMoveTowardEnemy(ScannedRobotEvent scannedRobotEvent) {
        int turnDirection = 1;
        if (Math.random() < .5) {
            turnDirection = -1;
        }
        //Move closer to the enemy.
        double turnAngleToEnemy = 30 + Math.random() * 60;//Don't run too directly to the enemy. At least turn 30 degree from the main direction.
        double relativeTurnAngleToEnemy = turnAngleToEnemy * turnDirection;
        double absoluteTurnAngleToEnemy = scannedRobotEvent.getBearing() + relativeTurnAngleToEnemy;
        return absoluteTurnAngleToEnemy;
    }

    private boolean suspectEnemyHasJustFiredBullet(double oldEnemyEnergy, double currentEnemyEnergy) {
        return oldEnemyEnergy - currentEnemyEnergy <= 3 && oldEnemyEnergy - currentEnemyEnergy >= 0.1;
    }

    private double getOldEnemyEnergy(String enemyName) {
        Optional<Enemy> enemyOptional = getOldEnemy(enemyName);
        if (enemyOptional.isPresent()) {
            return enemyOptional.get().getEnergy();
        } else {
            return RobotPhysics.ROBOT_INITIATE_ENERGY;
        }
    }

    private Optional<Enemy> getOldEnemy(String enemyName) {
        EnemyStatisticContext enemyStatisticContext = allEnemiesObservationContext.getEnemyPatternPrediction(enemyName);
        EnemyHistory enemyHistory = enemyStatisticContext.getEnemyHistory();
        if (enemyHistory.countHistoryItems() < 2) {
            return Optional.empty();
        } else {
            List<Enemy> recentHistory = enemyHistory.getLatestHistoryItems(2);
            Enemy oldData = recentHistory.get(1);//the second item is the old data. the first item is the current data.
            return Optional.of(oldData);
        }
    }
}
