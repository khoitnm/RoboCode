package org.tnmk.robocode.common.helper;

import java.io.Serializable;

import org.tnmk.common.math.GeoMathUtils;
import org.tnmk.common.math.Point;
import org.tnmk.robocode.common.model.BattleField;
import org.tnmk.robocode.common.model.FullRobotState;

import org.tnmk.robocode.common.robot.tnmkmodernrobot.ModernRobot;
import robocode.HitRobotEvent;
import robocode.Robot;
import robocode.Rules;
import robocode.ScannedRobotEvent;

/**
 * @deprecated  Some methods already replaced by {@link Move2DUtils}
 */
public class MoveHelper implements Serializable{
	public static final double DEFAULT_DISTANCE = 10000;
	public static final double ROBOT_SIZE = 50;
	public static final double MOVE_CLOSE_TO_TARGET_MIN_ANGLE = 20;
	public static final double MIN_TURN_RATE = Rules.getTurnRate(Rules.MAX_VELOCITY);
	public static final double MAX_DIFFERENT_OF_NEAR_ANGLES = 2; 
	public static final double MAX_DISTANCE_TO_TARGET = 100;
	private BattleField battleField;
	
	public enum BattlePosition {
		BOTTOM_LEFT, BOTTOM_RIGHT, TOP_LEFT, TOP_RIGHT;
	}
	
	public static boolean isNearMoveAngle(double currentMoveAngle, double targetMoveAngle){
		return Math.abs(currentMoveAngle - targetMoveAngle) < MAX_DIFFERENT_OF_NEAR_ANGLES;
	}
	public static Point reckonTargetPosition(Robot thisRobot, HitRobotEvent targetRobotEvent) {
		return reckonTargetPosition(thisRobot, targetRobotEvent.getBearing(), ROBOT_SIZE);
	}

	/**
	 * This method is correct, no need to debug.
	 * @param thisRobot
	 * @param targetRobotEvent
	 * @return
	 */
	public static Point reckonTargetPosition(Robot thisRobot, ScannedRobotEvent targetRobotEvent) {
		return reckonTargetPosition(thisRobot, targetRobotEvent.getBearing(), targetRobotEvent.getDistance());
	}
	public static Point reckonTargetPosition(Robot thisRobot, double bearingToEnemy, double distanceToTarget) {
		Point point = new Point();
		double angle = Math.toRadians(thisRobot.getHeading() + bearingToEnemy);
		point.x = (thisRobot.getX() + Math.sin(angle) * distanceToTarget);
		point.y = (thisRobot.getY() + Math.cos(angle) * distanceToTarget);
		return point;
	}
	public static BattleField createBattleField(Robot robot) {
		BattleField battleField = new BattleField(robot.getBattleFieldWidth(), robot.getBattleFieldHeight());
		int sentrySize = 0;
		if (robot instanceof ModernRobot){
			ModernRobot outlanderBase = (ModernRobot)robot;
			if (outlanderBase.getConfig().isMoveOnlyInSafeZone()){
				sentrySize = robot.getSentryBorderSize();
			}
			
		}
		battleField.setSentryBorderSize(sentrySize);
		return battleField;
	}

	private ModernRobot robot;

	public MoveHelper(ModernRobot robot) {
		this.robot = robot;
		battleField = createBattleField(robot);
	}

	public void moveToSafeCorner(BattlePosition position) {
		double targetX;
		double targetY;
		if (position == BattlePosition.BOTTOM_LEFT) {
			targetX = battleField.getSafeArea().getLeft();
			targetY = battleField.getSafeArea().getBottom();
		} else if (position == BattlePosition.BOTTOM_RIGHT) {
			targetX = battleField.getSafeArea().getRight();
			targetY = battleField.getSafeArea().getBottom();
		} else if (position == BattlePosition.TOP_LEFT) {
			targetX = battleField.getSafeArea().getLeft();
			targetY = battleField.getSafeArea().getTop();
		} else {
			targetX = battleField.getSafeArea().getRight();
			targetY = battleField.getSafeArea().getTop();
		}
		setMoveTo(targetX, targetY);
	}

	/**
	 * @param targetX
	 * @param targetY
	 * @return angle degree toward the target
	 */
	public double calculateTurnRightAngleToTarget(double targetX, double targetY) {
		FullRobotState robotState = RobotStateConverter.toRobotState(robot);
		return GeoMathUtils.calculateTurnRightDirectionToTarget(robotState.getMoveAngle(), robot.getX(), robot.getY(), targetX, targetY);
	}
	public void moveTo(double targetX, double targetY) {
		double sourceX = robot.getX();
		double sourceY = robot.getY();
		double bearing = calculateTurnRightAngleToTarget(targetX, targetY);
		robot.turnRight(bearing);
		robot.ahead(GeoMathUtils.distance(sourceX, sourceY, targetX, targetY));
	}
	public void setMoveTo(double targetX, double targetY) {
		double sourceX = robot.getX();
		double sourceY = robot.getY();
		double bearing = calculateTurnRightAngleToTarget(targetX, targetY);
		robot.setTurnRight(bearing);
		robot.setAhead(GeoMathUtils.distance(sourceX, sourceY, targetX, targetY));
	}
	/**
	 * @param targetPoint
	 * @return will move distance
	 */
	public double setTurnCloseToTarget(Point targetPoint) {
		FullRobotState thisState = RobotStateConverter.toRobotState(robot);
		double distance = GeoMathUtils.distance(thisState.getPosition(), targetPoint);
		if (distance < MAX_DISTANCE_TO_TARGET) return distance;//Don't need to try harder to get more closer to target.
		double turnRightDirection = GeoMathUtils.calculateTurnRightDirectionToTarget(thisState.getMoveAngle(), thisState.getX(), thisState.getY(), targetPoint.getX(), targetPoint.getY());
		if (turnRightDirection > 0) {
			turnRightDirection -= MOVE_CLOSE_TO_TARGET_MIN_ANGLE;
		} else {
			turnRightDirection += MOVE_CLOSE_TO_TARGET_MIN_ANGLE;
		}
		robot.setTurnRight(turnRightDirection);
//		return distance*2;
		return DEFAULT_DISTANCE;
	}
	public double setTurnToOtherSideOfBattleField() {
		Point center = this.battleField.getCenter();
		double otherSideX = center.getX() + (center.getX() - robot.getX());
		double otherSideY = center.getY() + (center.getY() - robot.getY());
	    setMoveTo(otherSideX, otherSideY);
	    return battleField.getWidth()+battleField.getHeight();
    }
}
