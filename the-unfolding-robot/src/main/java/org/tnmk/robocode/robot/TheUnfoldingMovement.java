package org.tnmk.robocode.robot;

import java.awt.geom.Point2D;
import org.tnmk.common.math.GeoMathUtils;
import org.tnmk.robocode.common.log.DebugHelper;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.movement.MovementContext;
import org.tnmk.robocode.common.movement.strategy.antigravity.AntiGravityMoveController;
import org.tnmk.robocode.common.movement.strategy.oscillator.OscillatorMoveController;
import org.tnmk.robocode.common.movement.strategy.random.RandomMoveController;
import org.tnmk.robocode.common.movement.strategy.runaway.RunAwayMoveController;
import org.tnmk.robocode.common.movement.strategy.wallsmooth.WallSmoothMoveController;
import org.tnmk.robocode.common.paint.PaintHelper;
import org.tnmk.robocode.common.radar.AllEnemiesObservationContext;
import org.tnmk.robocode.common.robot.*;
import org.tnmk.robocode.common.robot.state.AdvanceRobotFightState;
import org.tnmk.robocode.common.robot.state.AdvanceRobotStateMapper;
import org.tnmk.robocode.common.robotdecorator.HiTechDecorator;
import robocode.*;

public class TheUnfoldingMovement implements InitiableRun, LoopableRun, OnScannedRobotControl, OnHitRobotControl, OnStatusControl, OnCustomEventControl, OnBulletHitControl {
    public static final double IDEAL_ENEMY_OSCILLATOR_DISTANCE = 150;

    private final AdvancedRobot robot;
    private final AllEnemiesObservationContext allEnemiesObservationContext;
    private final MovementContext movementContext;

    private final WallSmoothMoveController wallSmoothMoveController;
    private final RunAwayMoveController runAwayMoveController;
    private final OscillatorMoveController oscillatorMoveController;
    private final AntiGravityMoveController antiGravityMoveController;
    private final RandomMoveController randomMoveController;

    public TheUnfoldingMovement(AdvancedRobot robot, AllEnemiesObservationContext allEnemiesObservationContext) {
        this.robot = robot;
        this.allEnemiesObservationContext = allEnemiesObservationContext;

        movementContext = new MovementContext(robot);
        oscillatorMoveController = new OscillatorMoveController(robot, movementContext);
        antiGravityMoveController = new AntiGravityMoveController(robot, allEnemiesObservationContext, movementContext);
        runAwayMoveController = new RunAwayMoveController(robot, movementContext);
        wallSmoothMoveController = new WallSmoothMoveController(robot, movementContext);
        randomMoveController = new RandomMoveController(robot, allEnemiesObservationContext, movementContext);
    }

    @Override
    public void runInit() {
        antiGravityMoveController.runInit();
        wallSmoothMoveController.runInit();
    }

    @Override
    public void runLoop() {
        antiGravityMoveController.runLoop();
        randomMoveController.runLoop();
        runAwayMoveController.runLoop();
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        int totalExistingEnemies = robot.getOthers();
        if (totalExistingEnemies <= 1) {
            randomMoveController.onScannedRobot(scannedRobotEvent);
//            moveOscillatorWithIdealDistance(scannedRobotEvent);
        } else {
            antiGravityMoveController.onScannedRobot(scannedRobotEvent);
        }
    }


    @Override
    public void onHitRobot(HitRobotEvent hitRobotEvent) {
        runAwayMoveController.onHitRobot(hitRobotEvent);
    }

    private void moveOscillatorWithIdealDistance(ScannedRobotEvent scannedRobotEvent) {
        int enemyDistance = (int) calculateSuitableEnemyDistance(IDEAL_ENEMY_OSCILLATOR_DISTANCE);
        oscillatorMoveController.onScannedRobot(scannedRobotEvent, enemyDistance);
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
        DebugHelper.debugStateMoveStrategy(robot, movementContext);
        saveRobotStatus(robot, movementContext);
//        LogHelper.logRobotMovement(robot, "current movement state");
        RobotStatus status = statusEvent.getStatus();
        Point2D robotPosition = new Point2D.Double(status.getX(), status.getY());
        double normAhead = movementContext.getDirection() * 200;
        double positiveAhead = 300;
        PaintHelper.paintAngleRadian(robot.getGraphics(), robotPosition, status.getHeadingRadians(), positiveAhead, 1, HiTechDecorator.AHEAD_DIRECTION_COLOR);
        PaintHelper.paintAngleRadian(robot.getGraphics(), robotPosition, status.getHeadingRadians(), normAhead, 2, HiTechDecorator.ACTUAL_MOVE_DIRECTION_COLOR);
        // If robot is slowing down and then reset, keep the same direction.
        // This ensures that the direction is handled correctly when we want to reverse direction after it hand slowed down and stopped.
        // If we set the direction based on distanceRemaining when it's 0, then direction is always 1 which may not correct.
        if (statusEvent.getStatus().getDistanceRemaining() != 0) {
            int direction = GeoMathUtils.sign(statusEvent.getStatus().getDistanceRemaining());
            if (direction != movementContext.getDirection()) {
                if (DebugHelper.isDebugMoveDirection()) {
                    LogHelper.logRobotMovement(robot, "Update move direction: moveStrategy: " + movementContext.getMoveStrategy() + ", newDirection: " + direction);
                }
                movementContext.setDirection(direction);
            }
        } else {
            //Just keep the old direction, don't change anything.
        }
    }


    private void saveRobotStatus(AdvancedRobot robot, MovementContext movementContext) {
        if (isLatestStateHistorySameAtThisTime(movementContext, robot.getTime())) {
            return;//We already added it, don't need to add anymore.
        }
        AdvanceRobotFightState advanceRobotFightState = AdvanceRobotStateMapper.toFightState(robot);
        movementContext.getRobotHistory().addToHistory(advanceRobotFightState);
    }

    public void onHitWall(HitWallEvent hitWallEvent) {
        runAwayMoveController.onHitWall(hitWallEvent);
    }

    @Override
    public void onCustomEvent(CustomEvent customEvent) {
        wallSmoothMoveController.onCustomEvent(customEvent);
    }

    private static boolean isLatestStateHistorySameAtThisTime(MovementContext movementContext, long time) {
        if (!movementContext.getRobotHistory().isEmpty()) {
            AdvanceRobotFightState latestState = movementContext.getRobotHistory().getLatestHistoryItem();
            return latestState.getTime() == time;
        } else {
            return false;
        }
    }

    @Override
    public void onBulletHit(BulletHitEvent event) {
        AdvanceRobotFightState latestState;
        if (isLatestStateHistorySameAtThisTime(movementContext, robot.getTime())) {
            latestState = movementContext.getRobotHistory().getLatestHistoryItem();
        } else {
            latestState = AdvanceRobotStateMapper.toFightState(robot);
            movementContext.getRobotHistory().addToHistory(latestState);
        }
        latestState.setHitByBullet(true);
    }
}
