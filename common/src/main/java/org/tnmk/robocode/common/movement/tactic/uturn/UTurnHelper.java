package org.tnmk.robocode.common.movement.tactic.uturn;

import robocode.Rules;

public class UTurnHelper {
    private static final double MIN_MAX_VELOCITY = 2.0d;
    private static final double MAX_MAX_VELOCITY = Rules.MAX_VELOCITY;

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
