package org.tnmk.robocode.common.movement.runaway;

import org.tnmk.common.math.AngleUtils;
import org.tnmk.robocode.common.log.LogHelper;
import robocode.AdvancedRobot;

import static java.lang.Math.PI;

public class RunAwayHelper {
    public static void changeMovementWhenHit(AdvancedRobot robot, int currentMoveDirection, double hitBearing) {
        int newMoveDirection = -currentMoveDirection;

        //Don't need to go back 180 degree, turn 90 degree instead to avoid stuck back & forth forever.
        //If the newHeading is still fail (another HitRobotEvent will be triggered), the next time it will turn 90 degree again.
        //Eventually, it will find a void to get out of crash.
        double newHeadingRadian = robot.getHeadingRadians();
        double normHitBearing = AngleUtils.normalizeDegree(hitBearing);
        if (0 < normHitBearing) {
            newHeadingRadian = robot.getHeadingRadians() + PI / 2;
        } else {
            newHeadingRadian = robot.getHeadingRadians() - PI / 2;
        }
        robot.setTurnRightRadians(newHeadingRadian);
        robot.setAhead(newMoveDirection * 200);
        LogHelper.logAdvanceRobot(robot, "Hit enemy: start run away " + robot.getHeading() + ", new heading: " + AngleUtils.toDegree(newHeadingRadian) + ", direction: " + newMoveDirection);
    }
}
