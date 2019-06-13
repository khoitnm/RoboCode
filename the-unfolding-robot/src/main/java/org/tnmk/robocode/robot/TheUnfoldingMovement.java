package org.tnmk.robocode.robot;

import java.awt.Color;
import java.awt.geom.Point2D;
import org.tnmk.common.math.GeoMathUtils;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.movement.MovementContext;
import org.tnmk.robocode.common.movement.antigravity.AntiGravityMovement;
import org.tnmk.robocode.common.movement.oscillator.OscillatorMovement;
import org.tnmk.robocode.common.movement.runaway.RunAwayMovement;
import org.tnmk.robocode.common.paint.PaintHelper;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robot.*;
import robocode.*;

public class TheUnfoldingMovement implements InitiableRun, LoopableRun, OnScannedRobotControl, OnHitRobotControl, OnStatusControl {
    public static final double IDEAL_ENEMY_OSCILLATOR_DISTANCE = 150;
    private final AdvancedRobot robot;
    private final AllEnemiesObservationContext allEnemiesObservationContext;
    private final MovementContext movementContext;

    private final RunAwayMovement runAwayMovement;
    private final OscillatorMovement oscillatorMovement;
    private final AntiGravityMovement antiGravityMovement;

    public TheUnfoldingMovement(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;

        movementContext = new MovementContext(robot);
        oscillatorMovement = new OscillatorMovement(robot, movementContext);
        antiGravityMovement = new AntiGravityMovement(robot, allEnemiesObservationContext, movementContext);
        runAwayMovement = new RunAwayMovement(robot, movementContext);
    }

    @Override
    public void runInit() {
        antiGravityMovement.runInit();
    }

    @Override
    public void runLoop() {
        runAwayMovement.runLoop();
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


    @Override
    public void onHitRobot(HitRobotEvent hitRobotEvent) {
        runAwayMovement.onHitRobot(hitRobotEvent);
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

    @Override
    public void onStatus(StatusEvent statusEvent) {
        LogHelper.logAdvanceRobot(robot, "MoveStrategy: " + movementContext.getMoveStrategy());

        RobotStatus status = statusEvent.getStatus();
        Point2D robotPosition = new Point2D.Double(status.getX(), status.getY());
        double normAhead = movementContext.getDirection() * 200;
        double positiveAhead = 300;
        PaintHelper.paintAngleRadian(robot.getGraphics(), robotPosition, status.getHeadingRadians(), positiveAhead, 1, Color.cyan);
        PaintHelper.paintAngleRadian(robot.getGraphics(), robotPosition, status.getHeadingRadians(), normAhead, 2, Color.magenta);

        // If robot is slowing down and then stop, keep the same direction.
        // This ensures that the direction is handled correctly when we want to reverse direction after it hand slowed down and stopped.
        // If we set the direction based on distanceRemaining when it's 0, then direction is always 1 which may not correct.
        if (statusEvent.getStatus().getDistanceRemaining() != 0) {
            int direction = GeoMathUtils.sign(statusEvent.getStatus().getDistanceRemaining());
            if (direction != movementContext.getDirection()) {
                LogHelper.logAdvanceRobot(robot, "Direction: " + direction);
                movementContext.setDirection(direction);
            }
        }
    }

    public void onHitWall(HitWallEvent hitWallEvent) {
        runAwayMovement.onHitWall(hitWallEvent);
    }
}
