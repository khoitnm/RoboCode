package org.tnmk.robocode.common.helper;

import org.tnmk.robocode.common.math.MathUtils;
import org.tnmk.robocode.common.math.Point;
import org.tnmk.robocode.common.predictor.self.RobotState;

import robocode.AdvancedRobot;
import robocode.Robot;
import robocode.ScannedRobotEvent;

public class MoveHelper {
	public static final double MOVE_CLOSE_TO_TARGET_MIN_ANGLE = 20;

	private BattleField battleField;

	public enum BattlePosition {
		BOTTOM_LEFT, BOTTOM_RIGHT, TOP_LEFT, TOP_RIGHT;
	}
	/**
	 * This method is correct, no need to debug.
	 * @param robot
	 * @param scannedRobotEvent
	 * @return
	 */
	public static Point reckonTargetPosition(Robot robot, ScannedRobotEvent scannedRobotEvent) {
		Point point = new Point();
		double angleToEnemy = scannedRobotEvent.getBearing();
		double angle = Math.toRadians(robot.getHeading() + angleToEnemy);
		point.x = (robot.getX() + Math.sin(angle) * scannedRobotEvent.getDistance());
		point.y = (robot.getY() + Math.cos(angle) * scannedRobotEvent.getDistance());
		return point;
	}

	public static BattleField createBattleField(Robot robot) {
		BattleField battleField = new BattleField();
		battleField.setHeight(robot.getBattleFieldHeight());
		battleField.setWidth(robot.getBattleFieldWidth());
		battleField.setSentryBorderSize(robot.getSentryBorderSize());
		return battleField;
	}

	private AdvancedRobot robot;

	public MoveHelper(AdvancedRobot robot) {
		this.robot = robot;
		battleField = createBattleField(robot);
	}

	public void moveToSafeCorner(BattlePosition position) {
		double targetX;
		double targetY;
		if (position == BattlePosition.BOTTOM_LEFT) {
			targetX = battleField.getSafeLeft();
			targetY = battleField.getSafeBottom();
		} else if (position == BattlePosition.BOTTOM_RIGHT) {
			targetX = battleField.getSafeRight();
			targetY = battleField.getSafeBottom();
		} else if (position == BattlePosition.TOP_LEFT) {
			targetX = battleField.getSafeLeft();
			targetY = battleField.getSafeTop();
		} else {
			targetX = battleField.getSafeRight();
			targetY = battleField.getSafeTop();
		}
		moveTo(targetX, targetY);
	}

	/**
	 * @param targetX
	 * @param targetY
	 * @return angle degree toward the target
	 */
	public double calculateTurnRightAngleToTarget(double targetX, double targetY) {
		return MathUtils.calculateTurnRightAngleToTarget(robot.getHeading(), robot.getX(), robot.getY(), targetX, targetY);
	}

	public void moveTo(double targetX, double targetY) {
		double sourceX = robot.getX();
		double sourceY = robot.getY();
		double bearing = calculateTurnRightAngleToTarget(targetX, targetY);
		robot.turnRight(bearing);
		robot.ahead(MathUtils.distance(sourceX, sourceY, targetX, targetY));
	}

	public void moveCloseToTarget(Point targetPoint) {
		RobotState thisState = RobotStateConverter.toRobotState(robot);
		double turnRightAngle = MathUtils.calculateTurnRightAngleToTarget(thisState.getHeading(), thisState.getX(), thisState.getY(), targetPoint.getX(), targetPoint.getY());
		if (turnRightAngle > 0) {
			turnRightAngle -= MOVE_CLOSE_TO_TARGET_MIN_ANGLE;
//			turnRightAngle = Math.max(turnRightAngle - MOVE_CLOSE_TO_TARGET_MIN_ANGLE, MOVE_CLOSE_TO_TARGET_MIN_ANGLE);
		} else {
			turnRightAngle += MOVE_CLOSE_TO_TARGET_MIN_ANGLE;
//			turnRightAngle = Math.min(turnRightAngle + MOVE_CLOSE_TO_TARGET_MIN_ANGLE, -MOVE_CLOSE_TO_TARGET_MIN_ANGLE);
		}
		robot.setTurnRight(turnRightAngle);
	}
}
