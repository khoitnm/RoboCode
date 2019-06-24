package org.tnmk.robocode.common.movement.runaway;

import org.tnmk.common.number.DoubleUtils;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.movement.MoveStrategyType;
import org.tnmk.robocode.common.movement.MovementContext;
import org.tnmk.robocode.common.robot.LoopableRun;
import org.tnmk.robocode.common.robot.OnHitRobotControl;
import org.tnmk.robocode.common.robot.OnHitWallControl;
import robocode.AdvancedRobot;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;

/**
 * Run away from wall & enemies.
 * Note: with {@link org.tnmk.robocode.common.movement.wallsmooth.WallSmoothMovement}, only time we can hit a wall is executing {@link MoveStrategyType#RUN_AWAY_FROM_ENEMIES}.
 * Anyway, we still need both {@link MoveStrategyType#RUN_AWAY_FROM_ENEMIES} and {@link MoveStrategyType#RUN_AWAY_FROM_WALL}
 */
public class RunAwayMovement implements OnHitRobotControl, LoopableRun, OnHitWallControl {
    private final AdvancedRobot robot;
    private final MovementContext movementContext;

    public RunAwayMovement(AdvancedRobot robot, MovementContext movementContext) {
        this.robot = robot;
        this.movementContext = movementContext;
    }


    @Override
    public void onHitRobot(HitRobotEvent hitRobotEvent) {
        //It doesn't care what is the current movement strategy, try its way to run away from enemy.
        LogHelper.logRobotMovement(robot, "Hit enemy: before run away " + robot.getHeading());

        if (movementContext.hasLowerOrEqualPriorityButDifferentStrategy(MoveStrategyType.RUN_AWAY_FROM_ENEMIES)) {
            movementContext.setMoveStrategyType(MoveStrategyType.RUN_AWAY_FROM_ENEMIES);
            RunAwayHelper.changeMovementWhenHit(robot, movementContext.getDirection(), hitRobotEvent.getBearingRadians());
        }
    }


    @Override
    public void runLoop() {
        if (movementContext.is(MoveStrategyType.RUN_AWAY_FROM_ENEMIES) || movementContext.is(MoveStrategyType.RUN_AWAY_FROM_WALL)) {
            /**
             * There could be a chance that the robot is applying another movement strategy, but it need to rest (distanceRemaining is 0) in the middle.
             * So, to avoid reset MovementContext incorrectly, we must check the current movementStrategy is either {@link MoveStrategyType#RUN_AWAY_FROM_WALL} or {@link MoveStrategyType#RUN_AWAY_FROM_ENEMIES},
             */
            if (DoubleUtils.isConsideredZero(robot.getDistanceRemaining())) {
                movementContext.setNone();
//                LogHelper.logRobotMovement(robot, "Hit: stop run away ");
            }
        }
    }

    @Override
    public void onHitWall(HitWallEvent hitWallEvent) {
        if (movementContext.hasLowerOrEqualPriorityButDifferentStrategy(MoveStrategyType.RUN_AWAY_FROM_WALL)) {
            movementContext.setMoveStrategyType(MoveStrategyType.RUN_AWAY_FROM_WALL);
            RunAwayHelper.changeMovementWhenHit(robot, movementContext.getDirection(), hitWallEvent.getBearingRadians());
        }
    }
}
