package org.tnmk.robocode.common.helper;

import org.tnmk.common.math.Point;
import org.tnmk.robocode.common.model.BaseRobotState;
import org.tnmk.robocode.common.model.FullRobotState;

import org.tnmk.robocode.common.robot.ModernRobot;
import robocode.AdvancedRobot;
import robocode.HitRobotEvent;
import robocode.ScannedRobotEvent;


public class RobotStateConverter {
	public static FullRobotState toTargetState(AdvancedRobot robot, ScannedRobotEvent scannedRobotEvent){
		Point targetPos = MoveHelper.reckonTargetPosition(robot, scannedRobotEvent);
		FullRobotState rs = new FullRobotState();
		rs.setName(scannedRobotEvent.getName());
		rs.setDistanceRemaining(FullRobotState.DISTANCE_REMAINING_UNKNOWN);
		rs.setTurnRemaining(0);
		rs.setHeading(scannedRobotEvent.getHeading());
		rs.setVelocity(scannedRobotEvent.getVelocity());
		rs.setMaxVelocity(rs.getVelocity());
		rs.setMaxTurnRate(0);
		rs.setX(targetPos.x);
		rs.setY(targetPos.y);
		return rs;
	}
	public static BaseRobotState toTargetState(AdvancedRobot robot, HitRobotEvent scannedRobotEvent) {
		Point targetPos = MoveHelper.reckonTargetPosition(robot, scannedRobotEvent);
		FullRobotState rs = new FullRobotState();
		rs.setName(scannedRobotEvent.getName());
		rs.setDistanceRemaining(FullRobotState.DISTANCE_REMAINING_UNKNOWN);
		rs.setTurnRemaining(0);
		rs.setHeading(0);
		rs.setVelocity(0);
		rs.setMaxVelocity(rs.getVelocity());
		rs.setMaxTurnRate(0);
		rs.setX(targetPos.x);
		rs.setY(targetPos.y);
		return rs;
    }
	public static FullRobotState toRobotState(ModernRobot robot){
		FullRobotState rs = new FullRobotState();
		rs.setMoveDirection(robot.getMoveDirection());
		rs.setName(robot.getName());
		rs.setDistanceRemaining(robot.getDistanceRemaining());
		rs.setHeading(robot.getHeading());
		rs.setTurnRemaining(robot.getTurnRemaining());
		rs.setVelocity(robot.getVelocity());
		rs.setX(robot.getX());
		rs.setY(robot.getY());
		return rs;
	}
	
}
