package org.tnmk.robocode.common.movement.runaway;

import org.tnmk.common.number.DoubleUtils;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.movement.MoveController;
import org.tnmk.robocode.common.movement.MoveStrategy;
import org.tnmk.robocode.common.movement.MovementContext;
import org.tnmk.robocode.common.movement.wallsmooth.WallSmoothMoveController;
import org.tnmk.robocode.common.robot.LoopableRun;
import org.tnmk.robocode.common.robot.OnHitRobotControl;
import org.tnmk.robocode.common.robot.OnHitWallControl;
import robocode.AdvancedRobot;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;

/**
 * Run away from wall & enemies.
 * Note: with {@link WallSmoothMoveController}, only time we can hit a wall is executing {@link MoveStrategy#RUN_AWAY_FROM_ENEMIES}.
 * Anyway, we still need both {@link MoveStrategy#RUN_AWAY_FROM_ENEMIES} and {@link MoveStrategy#RUN_AWAY_FROM_WALL}
 */
public class RunAwayMoveController implements MoveController, OnHitRobotControl, LoopableRun, OnHitWallControl {
    private final AdvancedRobot robot;
    private final MovementContext movementContext;

    public RunAwayMoveController(AdvancedRobot robot, MovementContext movementContext) {
        this.robot = robot;
        this.movementContext = movementContext;
    }


    @Override
    public void onHitRobot(HitRobotEvent hitRobotEvent) {
        //It doesn't care what is the current movement strategy, try its way to run away from enemy.
        LogHelper.logRobotMovement(robot, "Hit enemy: before run away " + robot.getHeading());

        if (movementContext.hasLowerOrEqualPriorityButDifferentStrategy(MoveStrategy.RUN_AWAY_FROM_ENEMIES)) {
            movementContext.changeMoveStrategy(MoveStrategy.RUN_AWAY_FROM_ENEMIES, this);
            RunAwayHelper.changeMovementWhenHit(robot, movementContext.getDirection(), hitRobotEvent.getBearingRadians());
        }
    }


    @Override
    public void runLoop() {
        if (movementContext.is(MoveStrategy.RUN_AWAY_FROM_ENEMIES) || movementContext.is(MoveStrategy.RUN_AWAY_FROM_WALL)) {
            /**
             * There could be a chance that the robot is applying another movement strategy, but it need to rest (distanceRemaining is 0) in the middle.
             * So, to avoid reset MovementContext incorrectly, we must check the current movementStrategy is either {@link MoveStrategy#RUN_AWAY_FROM_WALL} or {@link MoveStrategy#RUN_AWAY_FROM_ENEMIES},
             */
            if (DoubleUtils.isConsideredZero(robot.getDistanceRemaining())) {
                movementContext.setNone();
//                LogHelper.logRobotMovement(robot, "Hit: stop run away ");
            }
        }
    }

    @Override
    public void onHitWall(HitWallEvent hitWallEvent) {
        if (movementContext.hasLowerOrEqualPriorityButDifferentStrategy(MoveStrategy.RUN_AWAY_FROM_WALL)) {
            movementContext.changeMoveStrategy(MoveStrategy.RUN_AWAY_FROM_WALL, this);
            RunAwayHelper.changeMovementWhenHit(robot, movementContext.getDirection(), hitWallEvent.getBearingRadians());
        }
    }
}
