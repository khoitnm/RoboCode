package org.tnmk.robocode.common.movement.avoidhitenemy;

import org.tnmk.common.math.AngleUtils;
import org.tnmk.common.math.MathUtils;
import org.tnmk.common.number.DoubleUtils;
import org.tnmk.robocode.common.log.LogHelper;
import org.tnmk.robocode.common.movement.MovementContext;
import org.tnmk.robocode.common.movement.SpecialMovementType;
import org.tnmk.robocode.common.robot.LoopableRun;
import org.tnmk.robocode.common.robot.OnHitRobotControl;
import robocode.AdvancedRobot;
import robocode.HitRobotEvent;

import static java.lang.Math.PI;

public class AvoidHitEnemyMovement implements OnHitRobotControl, LoopableRun {
    private final AdvancedRobot robot;
    private final MovementContext movementContext;

    public AvoidHitEnemyMovement(AdvancedRobot robot, MovementContext movementContext) {
        this.robot = robot;
        this.movementContext = movementContext;
    }


    @Override
    public void onHitRobot(HitRobotEvent hitRobotEvent) {
        //It doesn't care what is the current movement strategy, try its way to run away from enemy.
        LogHelper.logAdvanceRobot(robot, "Hit enemy: before run away " + robot.getHeading());

        if (!movementContext.is(SpecialMovementType.RUN_AWAY_FROM_ENEMIES)) {
            movementContext.setSpecialMovementType(SpecialMovementType.RUN_AWAY_FROM_ENEMIES);
            double currentHeadingDirection = MathUtils.sign(Math.cos(robot.getHeadingRadians()));
            double newHeadingDirection = -currentHeadingDirection;

            //Don't need to go back 180 degree, turn 90 degree instead to avoid stuck back & forth forever.
            //If the newHeading is still fail (another HitRobotEvent will be triggered), the next time it will turn 90 degree again.
            //Eventually, it will find a void to get out of crash.
            double newHeadingRadian = robot.getHeadingRadians() + PI / 2;
            robot.setTurnRightRadians(newHeadingRadian);
            robot.setAhead(newHeadingDirection * 200);
            LogHelper.logAdvanceRobot(robot, "Hit enemy: start run away " + robot.getHeading() + ", new heading: " + AngleUtils.toDegree(newHeadingRadian));
        }
    }


    @Override
    public void runLoop() {
        if (movementContext.is(SpecialMovementType.RUN_AWAY_FROM_ENEMIES)) {
            if (DoubleUtils.isConsideredZero(robot.getDistanceRemaining())) {
                movementContext.setNone();
                LogHelper.logAdvanceRobot(robot, "Hit enemy: stop run away ");
            }
        }
    }
}
