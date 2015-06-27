package org.tnmk.robocode.common.helper;

import org.tnmk.robocode.common.math.LineSegment;
import org.tnmk.robocode.common.math.MathUtils;
import org.tnmk.robocode.common.math.Point;
import org.tnmk.robocode.common.model.BaseRobotState;
import org.tnmk.robocode.common.model.FullRobotState;
import org.tnmk.robocode.main.OutlanderBase;

import robocode.AdvancedRobot;
import robocode.Robot;
import robocode.Rules;
import robocode.ScannedRobotEvent;

public class MoveHelper {
	public static final double ROBOT_SIZE = 50;
	public static final double MOVE_CLOSE_TO_TARGET_MIN_ANGLE = 20;
	public static final double MIN_TURN_RATE = Rules.getTurnRate(Rules.MAX_VELOCITY);
	public static final double MAX_DIFFERENT_OF_NEAR_ANGLES = 2; 
	public static final double MIN_ROBOT_DISTANCE = 200;
	private BattleField battleField;
	
	public enum BattlePosition {
		BOTTOM_LEFT, BOTTOM_RIGHT, TOP_LEFT, TOP_RIGHT;
	}
	
	public static boolean isNearMoveAngle(double currentMoveAngle, double targetMoveAngle){
		return Math.abs(currentMoveAngle - targetMoveAngle) < MAX_DIFFERENT_OF_NEAR_ANGLES;
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
		BattleField battleField = new BattleField(robot.getBattleFieldWidth(), robot.getBattleFieldHeight());
		int sentrySize = 0;
		if (robot instanceof OutlanderBase){
			OutlanderBase outlanderBase = (OutlanderBase)robot;
			if (outlanderBase.getConfig().isMoveOnlyInSafeZone()){
				sentrySize = robot.getSentryBorderSize();
			}
			
		}
		battleField.setSentryBorderSize(sentrySize);
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
		moveTo(targetX, targetY);
	}

	/**
	 * @param targetX
	 * @param targetY
	 * @return angle degree toward the target
	 */
	public double calculateTurnRightAngleToTarget(double targetX, double targetY) {
		FullRobotState robotState = RobotStateConverter.toRobotState(robot);
		return MathUtils.calculateTurnRightDirectionToTarget(robotState.getMoveAngle(), robot.getX(), robot.getY(), targetX, targetY);
	}

	public void moveTo(double targetX, double targetY) {
		double sourceX = robot.getX();
		double sourceY = robot.getY();
		double bearing = calculateTurnRightAngleToTarget(targetX, targetY);
		robot.turnRight(bearing);
		robot.ahead(MathUtils.distance(sourceX, sourceY, targetX, targetY));
	}

	public void moveCloseToTarget(Point targetPoint) {
		FullRobotState thisState = RobotStateConverter.toRobotState(robot);
		double distance = MathUtils.distance(thisState.getPosition(), targetPoint);
		if (distance < MIN_ROBOT_DISTANCE)return;//Don't need to try harder to get more closer to target.
		double turnRightDirection = MathUtils.calculateTurnRightDirectionToTarget(thisState.getMoveAngle(), thisState.getX(), thisState.getY(), targetPoint.getX(), targetPoint.getY());
		if (turnRightDirection > 0) {
			turnRightDirection -= MOVE_CLOSE_TO_TARGET_MIN_ANGLE;
		} else {
			turnRightDirection += MOVE_CLOSE_TO_TARGET_MIN_ANGLE;
		}
		robot.setTurnRight(turnRightDirection);
	}

	public void avoidWallIfNecessary(){
		
	}
	/**
	 * Reckon which wall our robot is heading to.
	 * @param steps
	 * @return
	 */
	public HitAreaResult reckonHitAreaFromInside(BaseRobotState robotState, Area area){
		double moveAngle = robotState.getMoveAngle() % 360;
		
		HitAreaResult result = new HitAreaResult();
		result.setHitArea(area);
		result.setRobotMoveAngle(moveAngle);
		
		if (moveAngle > 0 && moveAngle < 180){//RIGHT wall
			
		}
		if (moveAngle > 90 && moveAngle < 270){//BOTTOM wall
			
		}
		if (moveAngle > 180){//LEFT wall
		}
		if (moveAngle > 270 || moveAngle < 90){//TOP wall
		}

		
		if (moveAngle == 0){
			result.setHitWall(area.getWallTop());
			result.setHitPoint(new Point(robotState.getX(), result.getHitWall().getPointA().getY()));
		}else if (moveAngle == 90){
			result.setHitWall(area.getWallRight());
			result.setHitPoint(new Point(result.getHitWall().getPointA().getX(), robotState.getY()));
		}else if (moveAngle == 180){
			result.setHitWall(area.getWallTop());
			result.setHitPoint(new Point(robotState.getX(), result.getHitWall().getPointA().getY()));
		}else if (moveAngle == 270){
			result.setHitWall(area.getWallLeft());
			result.setHitPoint(new Point(result.getHitWall().getPointA().getX(), robotState.getY()));
		}
		return result;
	}
	public static class HitAreaResult{
		private Area hitArea;
		private LineSegment hitWall;
		private Point hitPoint;
		/**
		 * The angle which robot is running.
		 */
		private double robotMoveAngle;
		
		public Area getHitArea() {
			return hitArea;
		}
		public void setHitArea(Area hitArea) {
			this.hitArea = hitArea;
		}
		public LineSegment getHitWall() {
			return hitWall;
		}
		public void setHitWall(LineSegment hitWall) {
			this.hitWall = hitWall;
		}
		public Point getHitPoint() {
			return hitPoint;
		}
		public void setHitPoint(Point hitPoint) {
			this.hitPoint = hitPoint;
		}
		public double getRobotMoveAngle() {
	        return robotMoveAngle;
        }
		public void setRobotMoveAngle(double robotMoveAngle) {
	        this.robotMoveAngle = robotMoveAngle;
        }
		
	}
}
