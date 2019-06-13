package org.tnmk.robocode.common.movement.runaway;

import org.tnmk.common.number.DoubleUtils;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.movement.MoveStrategy;
import org.tnmk.robocode.common.movement.MovementContext;
import org.tnmk.robocode.common.robot.LoopableRun;
import org.tnmk.robocode.common.robot.OnHitRobotControl;
import org.tnmk.robocode.common.robot.OnHitWallControl;
import robocode.AdvancedRobot;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;

/**
 * Run away from wall & enemies.
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
        LogHelper.logAdvanceRobot(robot, "Hit enemy: before run away " + robot.getHeading());

        if (!movementContext.is(MoveStrategy.RUN_AWAY_FROM_ENEMIES)) {
            movementContext.setMoveStrategy(MoveStrategy.RUN_AWAY_FROM_ENEMIES);
            RunAwayHelper.changeMovementWhenHit(robot, movementContext.getDirection(), hitRobotEvent.getBearingRadians());
        }
    }


    @Override
    public void runLoop() {
        if (movementContext.is(MoveStrategy.RUN_AWAY_FROM_ENEMIES) || movementContext.is(MoveStrategy.RUN_AWAY_FROM_WALL)) {
            if (DoubleUtils.isConsideredZero(robot.getDistanceRemaining())) {
                movementContext.setNone();
                LogHelper.logAdvanceRobot(robot, "Hit: stop run away ");
            }
        }
    }

    @Override
    public void onHitWall(HitWallEvent hitWallEvent) {
        if (!movementContext.is(MoveStrategy.RUN_AWAY_FROM_WALL)) {
            movementContext.setMoveStrategy(MoveStrategy.RUN_AWAY_FROM_WALL);
            RunAwayHelper.changeMovementWhenHit(robot, movementContext.getDirection(), hitWallEvent.getBearingRadians());
        }
    }
}
