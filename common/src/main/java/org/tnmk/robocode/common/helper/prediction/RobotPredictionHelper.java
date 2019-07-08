package org.tnmk.robocode.common.helper.prediction;

import java.awt.geom.Point2D;
import org.tnmk.common.math.AngleUtils;
import org.tnmk.common.math.GeoMathUtils;
import org.tnmk.common.number.DoubleUtils;
import org.tnmk.robocode.common.constant.RobotPhysics;
import org.tnmk.robocode.common.helper.BattleFieldUtils;
import robocode.AdvancedRobot;
import robocode.Rules;

/**
 * This class helps to predict our own robot position. Not predict enemy position.
 */
public class RobotPredictionHelper {
    public static RobotPrediction predictPosition(AdvancedRobot robot, long expectPredictionTimePeriod) {
        Point2D robotPosition = BattleFieldUtils.constructRobotPosition(robot);
        return predictPosition(expectPredictionTimePeriod, robotPosition, robot.getVelocity(), robot.getDistanceRemaining(), robot.getHeadingRadians(), robot.getTurnRemainingRadians());
    }
    /**
     * @param expectPredictionTimePeriod
     * @param currentPosition
     * @return based on currentPosition and other information, predict the future position after timePeriod.
     */
    public static RobotPrediction predictPosition(long expectPredictionTimePeriod, Point2D currentPosition, double velocity, double distanceRemaining, double headingRadian, double turnRemainingRadian) {
        double normAcceleration = checkAccelerating(velocity, distanceRemaining);

        RobotPrediction robotPrediction = new RobotPrediction();
        if (expectPredictionTimePeriod == 0) {
            robotPrediction.setNormAcceleration(normAcceleration);
            robotPrediction.setTimePeriod(expectPredictionTimePeriod);
            robotPrediction.setPosition(currentPosition);
            robotPrediction.setHeadingRadian(headingRadian);
            robotPrediction.setDistanceRemaining(distanceRemaining);
            robotPrediction.setVelocity(velocity);
            robotPrediction.setTurnRemainingRadian(turnRemainingRadian);
            return robotPrediction;
        }

        double nextHeadingRadian = headingRadian;
        double nextVelocity = velocity;
        double nextDistanceRemaining = distanceRemaining;
        double nextTurnRemainingRadian = turnRemainingRadian;
        Point2D nextPosition = currentPosition;

        for (int i = 0; i < expectPredictionTimePeriod || DoubleUtils.isConsideredZero(nextVelocity); i++) {
            double movingX = nextVelocity * Math.sin(AngleUtils.normalizeRadian(nextHeadingRadian));
            double movingY = nextVelocity * Math.cos(AngleUtils.normalizeRadian(nextHeadingRadian));
            nextPosition = new Point2D.Double(nextPosition.getX() + movingX, nextPosition.getY() + movingY);

            nextDistanceRemaining -= nextVelocity;
            if (nextDistanceRemaining <= RobotPhysics.ROBOT_DISTANCE_TO_STOP_FROM_FULL_SPEED) {
                normAcceleration = -Rules.DECELERATION;
            }

            nextVelocity += normAcceleration;

            double normTurnRateRadian;
            if (nextTurnRemainingRadian > 0) {
                double turnRateRadian = Rules.getTurnRateRadians(velocity);
                normTurnRateRadian = GeoMathUtils.sign(nextTurnRemainingRadian) * turnRateRadian;
            }else{
                normTurnRateRadian = 0;
            }
            nextHeadingRadian += normTurnRateRadian;
            nextTurnRemainingRadian -= normTurnRateRadian;

            robotPrediction.setTimePeriod(i);
            robotPrediction.setPosition(nextPosition);
            robotPrediction.setVelocity(nextVelocity);
            robotPrediction.setHeadingRadian(nextHeadingRadian);
            robotPrediction.setNormAcceleration(normAcceleration);
            robotPrediction.setDistanceRemaining(nextDistanceRemaining);
            robotPrediction.setTurnRemainingRadian(nextTurnRemainingRadian);
        }
        return robotPrediction;
    }

    /**
     * @param currentVelocity
     * @param distanceRemaining
     * @return If accelerating, return {@link Rules#ACCELERATION}.<br/>
     * If deceleration, return -{@link Rules#DECELERATION}
     */
    public static double checkAccelerating(double currentVelocity, double distanceRemaining) {
        double predictDistanceWhenDeceleration = reckonDistanceWhenFinishDeceleration(currentVelocity);
        if (predictDistanceWhenDeceleration >= distanceRemaining) {
            return -Rules.DECELERATION;
        } else {
            return Rules.ACCELERATION;
        }
    }

    /**
     * Hint: Use this formular https://cseweb.ucsd.edu/groups/tatami/kumo/exs/sum/ to calculate the result.
     *
     * @param currentVelocity
     * @return
     */
    public static double reckonDistanceWhenFinishDeceleration(double currentVelocity) {
        long timeWhenFinishDeceleration = (long) Math.ceil(currentVelocity / Rules.DECELERATION);
        double distance = timeWhenFinishDeceleration * (currentVelocity - timeWhenFinishDeceleration + 1);
        return distance;
    }
}
