package org.tnmk.robocode.common.movement.tactic.uturn;

import org.tnmk.common.math.GeoMathUtils;
import robocode.AdvancedRobot;
import robocode.Rules;

import java.awt.geom.Point2D;

public class UTurnHelper {
    private static final double MIN_MAX_VELOCITY = 2.0d;
    private static final double MAX_MAX_VELOCITY = Rules.MAX_VELOCITY;

    public static double reckonMoveAngle(AdvancedRobot robot, Point2D destination){
        Point2D currentPosition = new Point2D.Double(robot.getX(), robot.getY());
        double moveAngle = GeoMathUtils.calculateTurnRightDirectionToTarget(robot.getHeading(), currentPosition.getX(), currentPosition.getY(), destination.getX(), destination.getY());
        return moveAngle;
    }

    public static double reckonMaxVelocity(double remainTurnAngleDegree) {
        double maxVelocity;
        if (remainTurnAngleDegree > 30) {
            maxVelocity = MIN_MAX_VELOCITY;
        } else {
            maxVelocity = MAX_MAX_VELOCITY - (remainTurnAngleDegree / 10) * 2;
        }
        return maxVelocity;
    }
}
