package org.tnmk.robocode.common.helper;

import org.tnmk.robocode.common.math.Point;
import org.tnmk.robocode.common.predictor.self.RobotState;

import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;


public class RobotStateConverter {
	public static RobotState toRobotState(AdvancedRobot robot, ScannedRobotEvent scannedRobotEvent){
		Point targetPos = MoveHelper.reckonTargetPosition(robot, scannedRobotEvent);
		RobotState rs = new RobotState();
		rs.setDistanceRemaining(RobotState.DISTANCE_REMAINING_UNKNOWN);
		rs.setTurnRemaining(0);
		rs.setHeading(scannedRobotEvent.getHeading());
		rs.setVelocity(scannedRobotEvent.getVelocity());
		rs.setMaxVelocity(rs.getVelocity());
		rs.setMaxTurnRate(0);
		rs.setX(targetPos.x);
		rs.setY(targetPos.y);
		return rs;
	}
	public static RobotState toRobotState(AdvancedRobot robot){
		RobotState rs = new RobotState();
		rs.setDistanceRemaining(robot.getDistanceRemaining());
		rs.setHeading(robot.getHeading());
		rs.setTurnRemaining(robot.getTurnRemaining());
		rs.setVelocity(robot.getVelocity());
		rs.setX(robot.getX());
		rs.setY(robot.getY());
		return rs;
	}
}
