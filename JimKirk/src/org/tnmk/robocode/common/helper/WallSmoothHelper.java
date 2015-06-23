package org.tnmk.robocode.common.helper;

import org.tnmk.robocode.common.math.LineSegment;
import org.tnmk.robocode.common.math.MathUtils;
import org.tnmk.robocode.common.math.Point;
import org.tnmk.robocode.common.model.BaseRobotState;

import robocode.Rules;
/**
 * Avoid wall, turn direction when necessary
 * @author Khoi
 *
 */
public class WallSmoothHelper {
	public void avoidWallIfNecessary(){
		
	}
	/**
	 * 
	 * @param currentMoveAngle
	 * @param speed
	 * @param turnLeft turn right (1) or left (-1). Then we can decide whether turn right or left will be better.
	 * @return
	 */
	public double distanceToTopWall(double currentMoveAngle, double speed, int turnRight){
		double turnRate = Rules.getTurnRate(speed);
		double moveRadius = MathUtils.reckonMovementRadius(speed, turnRate);
		double moveRadiusAngle = (currentMoveAngle - (turnRight*90) + 360) % 360; //plus 360 to ensure that this value is positive
		return moveRadius * (1 - Math.cos(moveRadiusAngle)); 
	}
	public double distanceToRightWall(double currentMoveAngle, double speed, int turnRight){
		return distanceToTopWall((currentMoveAngle-90 + 360) % 360, speed, turnRight);
	}
	public double distanceToBottomWall(double currentMoveAngle, double speed, int turnRight){
		return distanceToTopWall((currentMoveAngle + 180) % 360, speed, turnRight);
	}
	public double distanceToLeftWall(double currentMoveAngle, double speed, int turnRight){
		return distanceToTopWall((currentMoveAngle + 90) % 360, speed, turnRight);
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
