package org.tnmk.robocode.common.helper;

import org.tnmk.robocode.common.math.LineSegment;
import org.tnmk.robocode.common.math.MathUtils;
import org.tnmk.robocode.common.math.Point;
import org.tnmk.robocode.common.model.BaseRobotState;

import robocode.Rules;

/**
 * Avoid wall, turn direction when necessary
 * 
 * @author Khoi
 *
 */
public class WallSmoothHelper {
	public static final double TURN_LEFT_DIRECTION = 270;
	public static final double TURN_RIGHT_DIRECTION = 90;
	public static final double TURN_TOP_DIRECTION = 0;
	public static final double TURN_BOTTOM_DIRECTION = 180;
	/**
	 * Total size, not radius of robot.
	 */
	private static final double ROBOT_SIZE = 50;

	/**
	 * @param area
	 * @param currentPosition
	 * @param currentMoveAngle
	 * @param speed
	 * @param turnRight
	 * @return The distance to wall after turn (left or right). If result < 0, we will hit wall.
	 */
	public static double distanceToTopWall(Area area, Point currentPosition, double currentMoveAngle, double speed, int turnRight) {
		double maxMoveNextToWall = maxMoveNextToTopWall(currentMoveAngle, speed, turnRight);
		double maxPos = currentPosition.getY() + maxMoveNextToWall;
		return area.getTop() - maxPos;
	}

	public static double distanceToRightWall(Area area, Point currentPosition, double currentMoveAngle, double speed, int turnRight) {
		double maxMoveNextToWall = maxMoveNextToRightWall(currentMoveAngle, speed, turnRight);
		double maxPos = currentPosition.getX() + maxMoveNextToWall;
		return area.getRight() - maxPos;
	}

	public static double distanceToLeftWall(Area area, Point currentPosition, double currentMoveAngle, double speed, int turnRight) {
		double maxMoveNextToWall = maxMoveNextToLeftWall(currentMoveAngle, speed, turnRight);
		double maxPos = currentPosition.getX() - maxMoveNextToWall;
		return maxPos - area.getLeft();
	}

	public static double distanceToBottomWall(Area area, Point currentPosition, double currentMoveAngle, double speed, int turnRight) {
		double maxMoveNextToWall = maxMoveNextToBottomWall(currentMoveAngle, speed, turnRight);
		double maxPos = currentPosition.getY() - maxMoveNextToWall;
		return maxPos - area.getBottom();
	}

	/**
	 * 
	 * @param currentMoveAngle
	 * @param speed
	 * @param turnLeft
	 *            turn right (1) or left (-1). Then we can decide whether turn right or left will be better.
	 * @return always positive number
	 */
	public static double maxMoveNextToTopWall(double currentMoveAngle, double speed, int turnRight) {
		double turnRate = Rules.getTurnRate(speed);
		double moveRadius = MathUtils.reckonMovementRadius(speed, turnRate);
		double moveRadiusAngle = (currentMoveAngle - (turnRight * 90) + 360) % 360; // plus 360 to ensure that this value is positive
		return moveRadius * (1 - Math.cos(moveRadiusAngle));
	}

	public static double maxMoveNextToRightWall(double currentMoveAngle, double speed, int turnRight) {
		return maxMoveNextToTopWall((currentMoveAngle - 90 + 360) % 360, speed, turnRight);
	}

	public static double maxMoveNextToBottomWall(double currentMoveAngle, double speed, int turnRight) {
		return maxMoveNextToTopWall((currentMoveAngle + 180) % 360, speed, turnRight);
	}

	public static double maxMoveNextToLeftWall(double currentMoveAngle, double speed, int turnRight) {
		return maxMoveNextToTopWall((currentMoveAngle + 90) % 360, speed, turnRight);
	}

	public static boolean isTooNearWall(double distanceToWall) {
		return (distanceToWall - (Rules.MAX_VELOCITY + ROBOT_SIZE)) > 0;
	}

	public static Double shouldAvoidWall(Area safeMoveArea, BaseRobotState robotState) {
		Double targetAngle = null;
		double moveAngle = robotState.getMoveAngle();
		double speed = Rules.MAX_VELOCITY;
		Point robotPosition = robotState.getPosition();

		if (moveAngle > 0 && moveAngle < 90) {// TOP & RIGHT wall
			double distanceToTop = distanceToTopWall(safeMoveArea, robotPosition, moveAngle, speed, -1);
			double distanceToRight = distanceToRightWall(safeMoveArea, robotPosition, moveAngle, speed, 1);
			if (distanceToRight < distanceToTop) {
				if (isTooNearWall(distanceToTop)) {
					targetAngle = TURN_LEFT_DIRECTION;
				}
			} else {
				if (isTooNearWall(distanceToRight)) {
					targetAngle = TURN_RIGHT_DIRECTION;
				}
			}
		} else if (moveAngle > 90 && moveAngle < 180) {// RIGHT & BOTTOM wall
			double distanceToBottom = distanceToBottomWall(safeMoveArea, robotPosition, moveAngle, speed, 1);
			double distanceToRight = distanceToRightWall(safeMoveArea, robotPosition, moveAngle, speed, -1);
			if (distanceToRight < distanceToBottom) {
				if (isTooNearWall(distanceToBottom)) {
					targetAngle = TURN_BOTTOM_DIRECTION;
				}
			} else {
				if (isTooNearWall(distanceToRight)) {
					targetAngle = TURN_TOP_DIRECTION;
				}
			}
		} else if (moveAngle > 180 && moveAngle < 270) {// BOTTOM & LEFT wall
			double distanceToBottom = distanceToBottomWall(safeMoveArea, robotPosition, moveAngle, speed, -1);
			double distanceToLeft = distanceToLeftWall(safeMoveArea, robotPosition, moveAngle, speed, 1);
			if (distanceToLeft < distanceToBottom) {
				if (isTooNearWall(distanceToBottom)) {
					targetAngle = TURN_RIGHT_DIRECTION;
				}
			} else {
				if (isTooNearWall(distanceToLeft)) {
					targetAngle = TURN_LEFT_DIRECTION;
				}
			}
		} else if (moveAngle > 270) {// LEFT & TOP wall
			double distanceToTop = distanceToTopWall(safeMoveArea, robotPosition, moveAngle, speed, 1);
			double distanceToLeft = distanceToLeftWall(safeMoveArea, robotPosition, moveAngle, speed, -1);
			if (distanceToLeft < distanceToTop) {
				if (isTooNearWall(distanceToTop)) {
					targetAngle = TURN_TOP_DIRECTION;
				}
			} else {
				if (isTooNearWall(distanceToLeft)) {
					targetAngle = TURN_BOTTOM_DIRECTION;
				}
			}
		}

		// Result
		if (targetAngle == null) {
			return null;
		} else {
			return MathUtils.normalizeDegree(targetAngle - moveAngle);
		}
	}
}
