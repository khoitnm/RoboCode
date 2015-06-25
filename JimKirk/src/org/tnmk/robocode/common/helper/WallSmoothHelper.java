package org.tnmk.robocode.common.helper;

import org.tnmk.robocode.common.math.LineSegment;
import org.tnmk.robocode.common.math.MathUtils;
import org.tnmk.robocode.common.math.Point;
import org.tnmk.robocode.common.model.BaseRobotState;

import robocode.Rules;
import robocode.util.Utils;

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
	 * With velocity 8, your turn rate is 4 degree/step. So to turn 90 degree, your robot must move next ahead some more distance.
	 * This constant define that distance. 
	 */
	public static final double DISTANCE_TO_TURN_PERPENDICULAR_ANGLE = 115;
	/**
	 * Total size, not radius of robot.
	 */
	private static final double ROBOT_SIZE = 35;

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
		double cos =  Math.cos(Math.toRadians(moveRadiusAngle));
		return moveRadius * (1 - cos);
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

	public static boolean isAlmostHitWall(double distanceToWall) {
		return (distanceToWall - (Rules.MAX_VELOCITY + ROBOT_SIZE)) < 0;
	}

	public static boolean isAlmostHitWallAfterTurnPerpendicularAngle(double distanceToWall) {
		return isAlmostHitWall(distanceToWall - DISTANCE_TO_TURN_PERPENDICULAR_ANGLE);
	}
	public static double distanceToLeftWall(Area area, Point position) {
		return position.getX() - area.getLeft();
	}

	public static double distanceToRightWall(Area area, Point position) {
		return area.getRight() - position.getX();
	}

	public static double distanceToTopWall(Area area, Point position) {
		return area.getTop() - position.getY();
	}

	public static double distanceToBottomWall(Area area, Point position) {
		return position.getY() - area.getBottom();
	}

	public static Double shouldAvoidWall(Area safeMoveArea, BaseRobotState robotState) {
		Double targetAngle = null;
		double moveAngle = robotState.getMoveAngle();
		double speed = Rules.MAX_VELOCITY;
		Point robotPosition = robotState.getPosition();
		// PERPENDICULAR ANGLE ////////////////////////////////////////////
		// ------------------------------------------------------------------
		if (MoveHelper.isNearMoveAngle(moveAngle, 0)) {
			double distanceToTop = distanceToTopWall(safeMoveArea, robotPosition);
			if (isAlmostHitWallAfterTurnPerpendicularAngle(distanceToTop)) {
				double distanceToLeft = distanceToLeftWall(safeMoveArea, robotPosition);
				double distanceToRight = distanceToRightWall(safeMoveArea, robotPosition);
				if (distanceToLeft > distanceToRight) {
					targetAngle = TURN_LEFT_DIRECTION;
				} else {
					targetAngle = TURN_RIGHT_DIRECTION;
				}
			}
		} else if (MoveHelper.isNearMoveAngle(moveAngle, 180)) {
			double distanceToBottom = distanceToBottomWall(safeMoveArea, robotPosition);
			if (isAlmostHitWallAfterTurnPerpendicularAngle(distanceToBottom)) {
				double distanceToLeft = distanceToLeftWall(safeMoveArea, robotPosition);
				double distanceToRight = distanceToRightWall(safeMoveArea, robotPosition);
				if (distanceToLeft > distanceToRight) {
					targetAngle = TURN_LEFT_DIRECTION;
				} else {
					targetAngle = TURN_RIGHT_DIRECTION;
				}
			}
		} else if (MoveHelper.isNearMoveAngle(moveAngle, 90)) {
			double distanceToRight = distanceToRightWall(safeMoveArea, robotPosition);
			if (isAlmostHitWallAfterTurnPerpendicularAngle(distanceToRight)) {
				double distanceToTop = distanceToTopWall(safeMoveArea, robotPosition);
				double distanceToBottom = distanceToBottomWall(safeMoveArea, robotPosition);
				if (distanceToTop > distanceToBottom) {
					targetAngle = TURN_TOP_DIRECTION;
				} else {
					targetAngle = TURN_BOTTOM_DIRECTION;
				}
			}
		} else if (MoveHelper.isNearMoveAngle(moveAngle, 270)) {
			double distanceToLeft = distanceToLeftWall(safeMoveArea, robotPosition);
			if (isAlmostHitWallAfterTurnPerpendicularAngle(distanceToLeft)) {
				double distanceToTop = distanceToTopWall(safeMoveArea, robotPosition);
				double distanceToBottom = distanceToBottomWall(safeMoveArea, robotPosition);
				if (distanceToTop > distanceToBottom) {
					targetAngle = TURN_TOP_DIRECTION;
				} else {
					targetAngle = TURN_BOTTOM_DIRECTION;
				}
			}
		}
		// OTHER ANGLES ////////////////////////////////////////////
		// ------------------------------------------------------------------
		else if (moveAngle > 0 && moveAngle < 90) {// TOP & RIGHT wall
			double distanceToTop = distanceToTopWall(safeMoveArea, robotPosition, moveAngle, speed, -1);
			double distanceToRight = distanceToRightWall(safeMoveArea, robotPosition, moveAngle, speed, 1);
			double minDistanceToWall = Math.max(distanceToRight, distanceToTop);
			if (isAlmostHitWall(minDistanceToWall)) {
				if (distanceToRight < distanceToTop) {
					targetAngle = TURN_TOP_DIRECTION;
				} else {
					targetAngle = TURN_RIGHT_DIRECTION;
				}
			}

		} else if (moveAngle > 90 && moveAngle < 180) {// RIGHT & BOTTOM wall
			double distanceToBottom = distanceToBottomWall(safeMoveArea, robotPosition, moveAngle, speed, 1);
			double distanceToRight = distanceToRightWall(safeMoveArea, robotPosition, moveAngle, speed, -1);
			double minDistanceToWall = Math.max(distanceToRight, distanceToBottom);
			if (isAlmostHitWall(minDistanceToWall)) {
				if (distanceToRight < distanceToBottom) {
					targetAngle = TURN_BOTTOM_DIRECTION;
				} else {
					targetAngle = TURN_RIGHT_DIRECTION;
				}
			}
		} else if (moveAngle > 180 && moveAngle < 270) {// BOTTOM & LEFT wall
			double distanceToBottom = distanceToBottomWall(safeMoveArea, robotPosition, moveAngle, speed, -1);
			double distanceToLeft = distanceToLeftWall(safeMoveArea, robotPosition, moveAngle, speed, 1);
			double minDistanceToWall = Math.max(distanceToLeft, distanceToBottom);
			if (isAlmostHitWall(minDistanceToWall)) {
				if (distanceToLeft < distanceToBottom) {
					targetAngle = TURN_BOTTOM_DIRECTION;
				} else {
					targetAngle = TURN_LEFT_DIRECTION;
				}
			}

		} else if (moveAngle > 270) {// LEFT & TOP wall
			double distanceToTop = distanceToTopWall(safeMoveArea, robotPosition, moveAngle, speed, 1);
			double distanceToLeft = distanceToLeftWall(safeMoveArea, robotPosition, moveAngle, speed, -1);
			double minDistanceToWall = Math.max(distanceToLeft, distanceToTop);
			if (isAlmostHitWall(minDistanceToWall)) {
				if (distanceToLeft < distanceToTop) {
					targetAngle = TURN_TOP_DIRECTION;
				} else {
					targetAngle = TURN_LEFT_DIRECTION;
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
